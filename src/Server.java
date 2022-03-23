public class Server {
    String serverType;
    int serverID;
    String state;
    int curStartTime, core, memory, disk;

    String serverName;

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

        serverName = String.format("%s %d", serverType, serverID);
    }
}

