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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Read from the server
    private String read() {
        try {
            String line = dis.readLine();
            return line;
            //return dis.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //
    // Raw DS-Sim Commands
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
        write("GETS ALL\n");
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
            case DATA:
                ResponseDATA rDATA = (ResponseDATA) r;
                OK(false, false);
                if (rDATA.getnRecs() > 0) {
                    switch (currCommand) {
                        case GETS:
                            returnedDATAServers = new ArrayList<>();
                            for (int i = 0; i < rDATA.getnRecs(); i++) {
                                String[] params = read().split(" ");
                                returnedDATAServers.add(
                                        new Server(
                                                params[0],                      //Server Type
                                                Integer.parseInt(params[1]),    //Server ID
                                                Server.parseState(params[2]),   //Server State
                                                Integer.parseInt(params[3]),    //Cur Start Time
                                                Integer.parseInt(params[4]),    //Core
                                                Integer.parseInt(params[5]),    //Memory
                                                Integer.parseInt(params[6])     //Disk
                                        )
                                );
                            }
                            break;
                        case LSTJ:
                            returnedDATAJobs = new ArrayList<>();
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
                }
                break;
            case JOBN:
                ResponseJOBN rJOBN = (ResponseJOBN) r;
                currentJob = new Job(rJOBN);
                break;
            case JOBP:
                ResponseJOBP rJOBP = (ResponseJOBP) r;
                currentJob = new Job(rJOBP);
                break;
            case JCPL:
                ResponseJCPL rJCPL = (ResponseJCPL) r;
                //TODO Possible moving of Jobs of overburdened servers
                break;
            case RESF:
                ResponseRESF rRESF = (ResponseRESF) r;
                //TODO Set server to Unavailable
                break;
            case RESR:
                ResponseRESR rRESR = (ResponseRESR) r;
                //TODO Set server to Inactive
                break;
            case NONE:
                returnedNONE = true;
                break;
            case OK:

                break;
            case ERR:
                ResponseERR rERR = (ResponseERR) r;
                System.out.println("DS-Server encountered an error:\n[" + rERR.getErrorMessage() + "]");
                System.exit(1);
                break;
            case DOT:

                break;
            default:
                System.out.println("Failed to interpret server response. Exiting");
                System.exit(1);
                break;
        }
    }

    //
    // Public DS-Sim Operations for Algorithms
    //

    //HELO, AUTH, REDY
    public void initialise() {
        if (state == ConnectionState.STARTING) {
            HELO();
            AUTH(System.getProperty("user.name"));
            REDY();
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
    public Job getJob() {
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
