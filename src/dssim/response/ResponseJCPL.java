package dssim.response;

import dssim.Response;

public class ResponseJCPL extends Response {
    private int endTime, jobID, serverID;
    private String serverType;

    public ResponseJCPL(int endTime, int jobID, String serverType, int serverID) {
        this.endTime = endTime;
        this.jobID = jobID;
        this.serverType = serverType;
        this.serverID = serverID;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getJobID() {
        return jobID;
    }

    public String getServerType() {
        return serverType;
    }

    public int getServerID() {
        return serverID;
    }
}
