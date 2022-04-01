public final class DSSim {

    /*
    Client Commands as raw Strings or functions for params.
    Also uses Job and Server objects as params
    */
    public static final String HELO = "HELO\n";
    public static final String OK = "OK\n";
    public static final String AUTH = AUTH("user");
    public static final String AUTH(String user) {
        return String.format("AUTH %s\n", user);
    }
    public static final String QUIT = "QUIT\n";
    public static final String REDY = "REDY\n";
    public static final String GETSALL = "GETS All\n";
    public static final String GETSTYPE(Server server) {
        return String.format("GETS Type %s\n", server.getServerType());
    }
    public static final String GETSCAPABLE(Job job) {
        return String.format("GETS Capable %d %d %d\n", job.getCore(), job.getMemory(), job.getDisk());
    }
    public static final String GETSAVAIL(Job job) {
        return String.format("GETS Avail %d %d %d\n", job.getCore(), job.getMemory(), job.getDisk());
    }
    public static final String SCHD(Job job, Server server) {
        return String.format("SCHD %d %s %d\n", job.getJobID(), server.getServerType(), server.getServerID());
    }
    public static final String CNTJ(Server server, String jobState) {
        return String.format("CNTJ %s %d %s\n", server.getServerType(), server.getServerID(), jobState);
    }
    public static final String EJWT(Server server) {
        return String.format("EJWT %s %d\n", server.getServerType(), server.getServerID());
    }
    public static final String LSTJ(Server server) {
        return String.format("LSTJ %s %d\n", server.getServerType(), server.getServerID());
    }
    public static final String PSHJ = "PSHJ\n";
    public static final String MIGJ(Job job, Server src, Server dst) {
        return String.format("MIGJ %d %s %s %s %s\n", job.getJobID(), src.getServerType(), src.getServerID(), dst.getServerType(), dst.getServerID());
    }
    public static final String KILJ(Server server, Job job) {
        return String.format("KILJ %s %d %d\n", server.getServerType(), server.getServerID(), job.getJobID());
    }
    public static final String TERM(Server server) {
        return String.format("TERM %s %d\n", server.getServerType(), server.getServerID());
    }

    /*
    Server Responses
    */
    private static final String DATA = "DATA";
    private static final String JOBN = "JOBN";
    private static final String JOBP = "JOBP";
    private static final String JCPL = "JCPL";
    private static final String RESF = "RESF";
    private static final String RESR = "RESR";
    private static final String NONE = "NONE";
    private static final String ERR = "ERR";

    public static enum ResponseType {
        DATA,
        JOBN,
        JOBP,
        JCPL,
        RESF,
        RESR,
        NONE,
        ERR,
        ParsingErr
    }

    //Parse a string sent from the server as a ResponseType
    public static final ResponseType getResponseType(String command) {
        String[] split = command.split(" ");
        switch (split[0]) {
            case DATA: 
                return ResponseType.DATA;
            case JOBN: 
                return ResponseType.JOBN;
            case JOBP: 
                return ResponseType.JOBP;
            case JCPL: 
                return ResponseType.JCPL;
            case RESF: 
                return ResponseType.RESF;
            case RESR: 
                return ResponseType.RESR;
            case NONE: 
                return ResponseType.NONE;
            case ERR: 
                return ResponseType.ERR;
            default:
                return ResponseType.ParsingErr;
        }
    }
}
