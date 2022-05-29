package dssim;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import dssim.Job.JobState;
import dssim.algorithm.DSSimAlgorithm;
import dssim.response.*;

//Handles the socket connection and the reading/writing of client server messages
public class Connection {
    private Socket socket;
    private DataOutputStream dos;
    private BufferedReader dis;

    private ConnectionState state = ConnectionState.STARTING;

    enum ConnectionState {
        STARTING,
        READY,
        CLOSED
    }
    private boolean returnedNONE = false;
    private List<Server> returnedDATAServers;

    private List<Job> returnedDATAJobs;
    private Job currentJob;
    private CommandType currCommand;
    enum CommandType {
        HELO,
        OK,
        AUTH,
        QUIT,
        REDY,
        GETS,
        SCHD,
        CNTJ,
        EJWT,
        LSTJ,
        PSHJ,
        MIGJ,
        KILJ,
        TERM
    }

    private List<Server> servers = new ArrayList<>();

    public Connection(String host, int port) {
        try {
            socket = new Socket(host, port);
            dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dos = new DataOutputStream(socket.getOutputStream());
            initialise();
        } catch (IOException e) {
            dis = null;
            dos = null;
            System.err.println("IO Exception to: " + host);
            System.exit(1);
        }
    }

    //Write to the server and flush
    private void write(String s) {
        try {
            dos.write(s.getBytes());
            dos.flush();
//            System.out.print("-" + s + "-");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Read from the server
    private String read() {
        try {
            String line = dis.readLine();
//            System.out.print("|" + line + "|");
            return line;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //
    // Raw DS-Sim Commands
    // Those that return a command in response are delegated to interpretResponse()
    // Others, such as CNTJ, EJWT or TERM will parse the integer themselves
    //

    private void HELO() {
        write("HELO\n");
        currCommand = CommandType.HELO;
        interpretResponse();
    }

    private void OK() {
        OK(true, true);
    }

    private void OK(boolean setCurrCommand, boolean interpretResponse) {
        write("OK\n");
        if (setCurrCommand) {
            currCommand = CommandType.OK;
        }
        if (interpretResponse) {
            interpretResponse();
        }
    }

    private void AUTH(String username) {
        write(String.format("AUTH %s\n", username));
        currCommand = CommandType.AUTH;
        interpretResponse();
    }

    private void QUIT() {
        write("QUIT\n");
        currCommand = CommandType.QUIT;
    }

    private void REDY() {
        write("REDY\n");
        currCommand = CommandType.REDY;
        interpretResponse();
    }

    private List<Server> GETSALL() {
        write("GETS All\n");
        currCommand = CommandType.GETS;
        interpretResponse();
        return returnedDATAServers;
    }

    private List<Server> GETSTYPE(Server type) {
        write(String.format("GETS Type %s\n", type.getServerType()));
        currCommand = CommandType.GETS;
        interpretResponse();
        return returnedDATAServers;
    }

    private List<Server> GETSCAPABLE(Job job) {
        write(String.format("GETS Capable %d %d %d\n", job.getCore(), job.getMemory(), job.getDisk()));
        currCommand = CommandType.GETS;
        interpretResponse();
        return returnedDATAServers;
    }

    private List<Server> GETSAVAIL(Job job) {
        write(String.format("GETS Avail %d %d %d\n", job.getCore(), job.getMemory(), job.getDisk()));
        currCommand = CommandType.GETS;
        interpretResponse();
        return returnedDATAServers;
    }

    //Schedule
    private void SCHD(Job job, Server server) {
        write(String.format("SCHD %d %s %d\n", job.getJobID(), server.getServerType(), server.getServerID()));
        currCommand = CommandType.SCHD;
        interpretResponse();
        currentJob = null;
    }

    //Count Jobs
    private int CNTJ(Server server, JobState jobState) {
        write(String.format("CNTJ %s %d %s\n", server.getServerType(), server.getServerID(), jobState.value()));
        currCommand = CommandType.CNTJ;
        return Integer.parseInt(read());
    }

    //Estimate Job Waiting Time
    private int EJWT(Server server) {
        write(String.format("EJWT %s %d\n", server.getServerType(), server.getServerID()));
        currCommand = CommandType.EJWT;
        return Integer.parseInt(read());
    }

    //List Jobs
    private List<Job> LSTJ(Server server) {
        write(String.format("LSTJ %s %d\n", server.getServerType(), server.getServerID()));
        currCommand = CommandType.LSTJ;
        interpretResponse();
        return returnedDATAJobs;
    }

    private void PSHJ() {
        write("PSHJ\n");
        currCommand = CommandType.PSHJ;
        interpretResponse();
        currentJob = null;
    }

    private void MIGJ(Job job, Server src, Server dst) {
        write(String.format("MIGJ %d %s %s %s %s\n", job.getJobID(), src.getServerType(), src.getServerID(), dst.getServerType(), dst.getServerID()));
        currCommand = CommandType.MIGJ;
        interpretResponse();
    }

    private void KILJ(Server server, Job job) {
        write(String.format("KILJ %s %d %d\n", server.getServerType(), server.getServerID(), job.getJobID()));
        currCommand = CommandType.KILJ;
        interpretResponse();
    }

    private int TERM(Server server) {
        write(String.format("TERM %s %d\n", server.getServerType(), server.getServerID()));
        currCommand = CommandType.TERM;
        return Integer.parseInt(read().split(" ")[0]);
    }

    //Analyse Reply from Server
    private void interpretResponse() {
        String s = read();
        Response r = Response.createResponse(s);
        switch (Response.type(r)) {
            case DATA: //Start reading a data block
                ResponseDATA rDATA = (ResponseDATA) r; //Cast type
                switch (currCommand) { //Switch based on expected content type
                    case GETS:
                        returnedDATAServers = new ArrayList<>();
                        if (rDATA.getnRecs() == 0) { //If empty data set, skip straight to the final OK
                            break;
                        }
                        OK(false, false); //Special OK that doesnt trigger the interpret again
                        for (int i = 0; i < rDATA.getnRecs(); i++) {
                            String[] params = read().split(" "); //Read one DATA rec

                            //Search for an exisiting server by that identifier
                            Server serverExists = null;
                            for (Server server : servers) {
                                if (server.getServerType().equals(params[0]) && server.getServerID() == Integer.parseInt(params[1])) {
                                    serverExists = server;
                                    break;
                                }
                            }
                            //If it exists, return it instead and update its information
                            if (serverExists != null) {
                                returnedDATAServers.add(serverExists);
                                serverExists.update(
                                        Server.parseState(params[2]),
                                        Integer.parseInt(params[3]),
                                        Integer.parseInt(params[4]),
                                        Integer.parseInt(params[5]),
                                        Integer.parseInt(params[6])
                                );
                            //If it does not exist, create it
                            } else {
                                    Server newServer = new Server(
                                            params[0],                      //Server Type
                                            Integer.parseInt(params[1]),    //Server ID
                                            Server.parseState(params[2]),   //Server State
                                            Integer.parseInt(params[3]),    //Cur Start Time
                                            Integer.parseInt(params[4]),    //Core
                                            Integer.parseInt(params[5]),    //Memory
                                            Integer.parseInt(params[6])     //Disk
                                    );
                                    servers.add(newServer);
                                    returnedDATAServers.add(newServer);
                            }
                        }
                        break;
                    case LSTJ:
                        returnedDATAJobs = new ArrayList<>();
                        if (rDATA.getnRecs() == 0) {
                            break;
                        }
                        for (int i = 0; i < rDATA.getnRecs(); i++) {
                            String[] params = read().split(" ");
                            returnedDATAJobs.add(
                                    new Job(
                                            Integer.parseInt(params[0]),    //Job ID
                                            Job.parseState(params[1]),      //Job State
                                            Integer.parseInt(params[2]),    //Submit Time
                                            Integer.parseInt(params[3]),    //Start Time
                                            Integer.parseInt(params[4]),    //Est Run Time
                                            Integer.parseInt(params[5]),    //Core
                                            Integer.parseInt(params[6]),    //Memory
                                            Integer.parseInt(params[7])     //Disk
                                    )
                            );
                        }
                        break;
                    default:
                        System.out.println("Unexpectedly received DATA in response to " + currCommand.toString() + ". Exiting");
                        System.exit(1);
                }
                OK();
                break;
            case JOBN: //New Job
                ResponseJOBN rJOBN = (ResponseJOBN) r;
                currentJob = new Job(rJOBN);
                break;
            case JOBP: //Job to allocate
                ResponseJOBP rJOBP = (ResponseJOBP) r;
                currentJob = new Job(rJOBP);
                break;
            case JCPL: //Job completion
                ResponseJCPL rJCPL = (ResponseJCPL) r;
                annouceCompletion(rJCPL.getJobID()); //Notify servers
                break;
            case RESF: //Server Failure
                ResponseRESF rRESF = (ResponseRESF) r;
                //TODO Modifications that require server failure information
                break;
            case RESR: //Server recovery
                ResponseRESR rRESR = (ResponseRESR) r;
                //TODO Modifications that require server failure information
                break;
            case NONE: //All jobs have finished
                returnedNONE = true;
                break;
            case OK: //Do nothing
                break;
            case ERR: //DS-Server Error
                ResponseERR rERR = (ResponseERR) r;
                System.out.println("DS-Server encountered an error:\n" + rERR.getErrorMessage() + "\nPrevious Command was: " + currCommand.toString());
                System.exit(1);
                break;
            case DOT: //Do nothing ('.' returned from after DATA)
                break;
            default: //Parsing error
                System.out.println("Failed to interpret server response. Exiting");
                System.exit(1);
                break;
        }
    }

    public void annouceCompletion(int jobID) { //notify servers of job completion
        for (Server s : servers) {
            s.jobComplete(jobID);
        }
    }

    //
    // Public DS-Sim Operations for Algorithms
    // Most methods are self explanatory and include a comment of the DS Sim command they implement
    //

    //HELO, AUTH, REDY, GETSALL
    public void initialise() { //Send start up commands and collect all servers
        if (state == ConnectionState.STARTING) {
            HELO();
            AUTH(System.getProperty("user.name"));
            REDY();
            GETSALL();
            state = ConnectionState.READY;
        }
    }

    //QUIT
    public void quit() {
        try {
            QUIT();
            socket.close();
            socket = null;
            state = ConnectionState.CLOSED;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //REDY
    public Job getJob() { //Attempt to optimise the job collection by storing the most recent job and allowing other objects to repeatedly call getJob() without asking the server constantly
        while (currentJob == null && !returnedNONE) {
            REDY();
        }
        return currentJob;
    }

    //GETS
    public List<Server> getServers() {
        return GETSALL();
    }

    //GETS
    public List<Server> getServersType(Server type) {
        return GETSTYPE(type);
    }

    //GETS
    public List<Server> getServersCapable(Job job) {
        return GETSCAPABLE(job);
    }

    //GETS
    public List<Server> getServersAvailable(Job job) {
        return GETSAVAIL(job);
    }

    //SCHD
    public void schedule(Server server) {
        server.assignJob(getJob());
        SCHD(getJob(), server);
    }

    //CNTJ
    public int countJobs(Server server) {
        int sum = 0;
        for (JobState j : JobState.values()) {
            sum += countJobs(server, j);
        }
        return sum;
    }

    //CNTJ
    public int countJobs(Server server, JobState state) {
        return CNTJ(server, state);
    }

    //EJWT
    public int estimateWaitingTime(Server server) {
        return EJWT(server);
    }

    //LSTJ
    public List<Job> getJobs(Server server) {
        return LSTJ(server);
    }

    //PSHJ
    public void pushJob() {
        PSHJ();
    }

    //MIGJ
    public void migrateJob(Job job, Server source, Server dest) {
        MIGJ(job, source, dest);
    }

    //KILJ
    public void killJob(Server server, Job job) {
        KILJ(server, job);
    }

    //TERM
    public int terminateServer(Server server) {
        return TERM(server);
    }

    public ConnectionState getState() {
        return state;
    }
}
