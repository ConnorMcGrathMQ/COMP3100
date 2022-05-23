package dssim.response;

import dssim.Response;

public class ResponseJOBN extends Response {
    private int submitTime, jobID, estRuntime, core, memory, disk;

    public ResponseJOBN(int submitTime, int jobID, int estRuntime, int core, int memory, int disk) {
        this.submitTime = submitTime;
        this.jobID = jobID;
        this.estRuntime = estRuntime;
        this.core = core;
        this.memory = memory;
        this.disk = disk;
    }

    public int getSubmitTime() {
        return submitTime;
    }

    public int getJobID() {
        return jobID;
    }

    public int getEstRuntime() {
        return estRuntime;
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
}
