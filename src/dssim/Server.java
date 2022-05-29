package dssim;

import java.util.ArrayList;
import java.util.List;

public class Server implements Comparable<Server>{
    private final String serverType;
    private final int serverID;
    private ServerState state;
    private int curStartTime;
    private final int baseCurStartTime;
    private int core;
    private final int baseCore;
    private int memory;
    private final int baseMemory;
    private int disk;
    private final int baseDisk;

    private List<Job> assignedJobs = new ArrayList<>();

    public enum ServerState {
        INACTIVE(0, "inactive"),
        BOOTING(1, "booting"),
        IDLE(2, "idle"),
        ACTIVE(3, "active"),
        UNAVAILABLE(4, "unavailable");

        private int id;
        private String value;
        ServerState(int id, String value) {
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
        baseCurStartTime = _curStartTime;
        baseCore = _core;
        baseMemory = _memory;
        baseDisk = _disk;
        core = _core;
        memory = _memory;
        disk = _disk;
    }

    public void update(ServerState _state, int _curStartTime, int _core, int _memory, int _disk) {
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
    public ServerState getState() {
        return state;
    }
    public int getCurStartTime() {
        return curStartTime;
    }
    public int getCore() {
        return getCore(false);
    }
    public int getCore(boolean getBaseValue) {
        return getBaseValue ? baseCore : core;
    }
    public int getMemory() {
        return getMemory(false);
    }
    public int getMemory(boolean getBaseValue) {
        return getBaseValue ? baseMemory : memory;
    }
    public int getDisk() {
        return getDisk(false);
    }
    public int getDisk(boolean getBaseValue) {
        return getBaseValue ? baseDisk : disk;
    }

    public void assignJob(Job j) {
        assignedJobs.add(j);
    }

    public boolean removeJob(Job j) {
        return assignedJobs.remove(j);
    }

    public int sumAssignedJobEstTime() {
        int total = 0;
        for (Job j : assignedJobs) {
            total += j.getEstRunTime();
        }
        return total;
    }

    public void jobComplete(int jobID) {
        for (Job j : assignedJobs) {
            if (j.getJobID() == jobID) {
                removeJob(j);
                break;
            }
        }
    }

    public List<Job> getJobs() {
        return assignedJobs;
    }

    @Override
    public int compareTo(Server o) {
        return Integer.compare(this.getCore(), o.getCore());
    }
}

