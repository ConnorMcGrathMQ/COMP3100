package dssim;

public class Server implements Comparable<Server>{
    private String serverType;
    private int serverID;
    private ServerState state;
    private int curStartTime;
    private int core;
    private int memory;
    private int disk;

    public enum ServerState {
        INACTIVE(0, "inactive"),
        BOOTING(1, "booting"),
        IDLE(2, "idle"),
        ACTIVE(3, "active"),
        UNAVAILABLE(4, "unavailable");

        private int id;
        private String value;
        private ServerState(int id, String value) {
            this.id = id;
            this.value = value;
        }

        public int id() {
            return id;
        }

        public String value() {
            return value;
        }
    }

    public Server(String _serverType, int _serverID, ServerState _state, int _curStartTime, int _core, int _memory, int _disk) {
        serverType = _serverType;
        serverID = _serverID;
        state = _state;
        curStartTime = _curStartTime;
        core = _core;
        memory = _memory;
        disk = _disk;
    }

    public static ServerState parseState(String input) {
        switch (input) {
            case "inactive":
                return ServerState.INACTIVE;
            case "booting":
                return ServerState.BOOTING;
            case "idle":
                return ServerState.IDLE;
            case "active":
                return ServerState.ACTIVE;
            case "unavailable":
                return ServerState.UNAVAILABLE;
            default:
                return null;
        }
    }

    public String getServerType() {
        return serverType;
    }
    public int getServerID() {
        return serverID;
    }
    public String getState() {
        return state.value();
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

