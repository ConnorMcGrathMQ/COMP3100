package dssim.response;

import dssim.Response;

public class ResponseRESF extends Response {
    private int serverID, timeOfFailure;
    private String serverType;

    public ResponseRESF(String serverType, int serverID, int timeOfFailure) {
        this.serverType = serverType;
        this.serverID = serverID;
        this.timeOfFailure = timeOfFailure;
    }

    public String getServerType() {
        return serverType;
    }

    public int getServerID() {
        return serverID;
    }

    public int getTimeOfFailure() {
        return timeOfFailure;
    }
}
