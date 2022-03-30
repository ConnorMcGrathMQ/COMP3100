import java.io.*;

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
        return String.format("SCHD %d %s %d", job.jobID, server.serverType, server.serverID);
    }
    public static String CNTJ(Server server, String jobState) {
        return String.format("CNTJ %s %d %s", server.serverType, server.serverID, jobState);
    }
    public static String EJWT(Server server) {
        return String.format("EJWT %s %d", server.serverType, server.serverID);
    }
    public static String LSTJ(Server server) {
        return String.format("LSTJ %s %d", server.serverType, server.serverID);
    }
    public static final String PSHJ = "PSHJ\n";
    public static String MIGJ(Job job, Server src, Server dst) {
        return String.format("MIGJ %d %s %s %s %s", job.jobID, src.serverType, src.serverID, dst.serverType, dst.serverID);
    }
    public static String KILJ(Server server, Job job) {
        return String.format("KILJ %s %d %d", server.serverType, server.serverID, job.jobID);
    }
    public static String TERM(Server server) {
        return String.format("TERM %s %d", server.serverType, server.serverID);
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
}
