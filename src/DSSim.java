import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DSSim {
    public static final String HELO = "HELO\n";
    public static final String OK = "OK\n";
    public static final String AUTH = AUTH("user");
    public static String AUTH(String user) {
        return "AUTH " + user + "\n";
    }
    public static final String QUIT = "QUIT\n";
    public static final String REDY = "REDY\n";
    public static final String GETSALL = "GETS All\n";
    public static String GETSTYPE(String serverType) {
        return "GETS Type " + serverType;
    }
    public static String GETSCAPABLE(String core, String memory, String disk) {
        return String.format("GETS Capable %s %s %s", core, memory, disk);
    }
    public static String GETSCAPABLE(int core, int memory, int disk) {
        return String.format("GETS Capable %d %d %d", core, memory, disk);
    }
    public static String GETSAVAIL(String core, String memory, String disk) {
        return String.format("GETS Avail %s %s %s", core, memory, disk);
    }
    public static String GETSAVAIL(int core, int memory, int disk) {
        return String.format("GETS Avail %d %d %d", core, memory, disk);
    }
    public static String SCHD(Job job, Server server) {
        return String.format("SCHD %d %s %d", job.getJobID(), server.getServerType(), server.getServerID());
    }
    public static String CNTJ(Server server, String jobState) {
        return String.format("CNTJ %s %d %s", server.getServerType(), server.getServerID(), jobState);
    }
    public static String EJWT(Server server) {
        return String.format("EJWT %s %d", server.getServerType(), server.getServerID());
    }
    public static String LSTJ(Server server) {
        return String.format("LSTJ %s %d", server.getServerType(), server.getServerID());
    }
    public static final String PSHJ = "PSHJ\n";
    public static String MIGJ(Job job, Server src, Server dst) {
        return String.format("MIGJ %d %s %s %s %s", job.getJobID(), src.getServerType(), src.getServerID(), dst.getServerType(), dst.getServerID());
    }
    public static String KILJ(Server server, Job job) {
        return String.format("KILJ %s %d %d", server.getServerType(), server.getServerID(), job.getJobID());
    }
    public static String TERM(Server server) {
        return String.format("TERM %s %d", server.getServerType(), server.getServerID());
    }

    /*
    End of Statics
    */

    private DataOutputStream dos;
    private BufferedReader dis;

    public DSSim(DataOutputStream o, BufferedReader i) {
        dos = o;
        dis = i;
    }

    public void write(String s) throws IOException {
        dos.write(s.getBytes());
        dos.flush();
    }

    public String read() throws IOException {
        return dis.readLine();
    }

    public String writeRead(String s) throws IOException {
        write(s);
        return read();
    }

    public void writePrint(String s) throws IOException {
        System.out.println(writeRead(s));
    }

    public void writeDiscard(String s) throws IOException {
        write(s);
        read();
    }

    public String[] readData() throws IOException {
        String dataInfo = read();
        int dataFragmentCount = Integer.parseInt(dataInfo.split(" ")[1]);
        write(DSSim.OK);
        String[] dataFragments = new String[dataFragmentCount];
        for (int i = 0; i < dataFragmentCount; i++) {
            dataFragments[i] = read();
        }
        return dataFragments;
    }

    public List<Server> getServers() throws IOException {
        write(DSSim.GETSALL);
        String[] serverStrings = readData();
        List<Server> servers = new ArrayList<Server>();
        for (int i = 0; i < serverStrings.length; i++) {
            servers.add(new Server(serverStrings[i].split(" ")));
        }
        return servers;
    }
}
