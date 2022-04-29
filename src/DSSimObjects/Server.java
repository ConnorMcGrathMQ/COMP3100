package DSSimObjects;
public class Server implements Comparable<Server>{
    private String serverType;
    private int serverID;
    private String state;
    private int curStartTime;
    private int core;
    private int memory;
    private int disk;

    /*
    Constructors
    */
    public Server(String[] args) {
        this(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
    }
    public Server(String _serverType, String _serverID, String _state, String _curStartTime, String _core, String _memory, String _disk) {
        this(_serverType, Integer.parseInt(_serverID), _state, Integer.parseInt(_curStartTime), Integer.parseInt(_core), Integer.parseInt(_memory), Integer.parseInt(_disk));
    }
    public Server(String _serverType, int _serverID, String _state, int _curStartTime, int _core, int _memory, int _disk) {
        serverType = _serverType;
        serverID = _serverID;
        state = _state;
        curStartTime = _curStartTime;
        core = _core;
        memory = _memory;
        disk = _disk;
    }

    public String getServerType() {
        return serverType;
    }
    public int getServerID() {
        return serverID;
    }
    public String getState() {
        return state;
    }
    public int getCurStartTime() {
        return curStartTime;
    }
    public int getCore() {
        return core;
    }
    public int getMemory() {
        return memory;
    }
    public int getDisk() {
        return disk;
    }

    @Override
    public int compareTo(Server o) {
        return Integer.compare(this.getCore(), o.getCore());
    }
}

