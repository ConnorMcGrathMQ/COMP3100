package dssim.response;

import dssim.Response;

public class ResponseRESR extends Response {
    private int serverID, timeOfRecovery;
    private String serverType;

    public ResponseRESR(String serverType, int serverID, int timeOfRecovery) {
        this.serverType = serverType;
        this.serverID = serverID;
        this.timeOfRecovery = timeOfRecovery;
    }

    public String getServerType() {
        return serverType;
    }

    public int getServerID() {
        return serverID;
    }

    public int getTimeOfRecovery() {
        return timeOfRecovery;
    }
}
