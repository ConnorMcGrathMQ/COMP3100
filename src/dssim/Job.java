package dssim;

import dssim.response.ResponseJOBN;
import dssim.response.ResponseJOBP;

public class Job {
    private int jobID, submitTime, startTime, estRunTime, core, memory, disk;
    private JobState jobState;

    public enum JobState {
        SUBMITTED(0),
        WAITING(1),
        RUNNING(2);

        private int value;

        private JobState(int value) {
            this.value = value;
        }
        public int value() {
            return value;
        }

    }
    public Job(ResponseJOBN r) {
        this(r.getSubmitTime(), r.getJobID(), r.getEstRuntime(), r.getCore(), r.getMemory(), r.getDisk());
    }

    public Job(ResponseJOBP r) {
        this(r.getSubmitTime(), r.getJobID(), r.getEstRuntime(), r.getCore(), r.getMemory(), r.getDisk());
    }

    public Job(int _submitTime, int _jobID, int _estRunTime, int _core, int _memory, int _disk) {
        submitTime = _submitTime;
        jobID = _jobID;
        estRunTime = _estRunTime;
        core = _core;
        memory = _memory;
        disk = _disk;
    }

    public Job(int _jobID, JobState _jobState, int _submitTime, int _startTime, int _estRunTime, int _core, int _memory, int _disk) {
        jobID = _jobID;
        jobState = _jobState;
        submitTime = _submitTime;
        startTime = _startTime;
        estRunTime = _estRunTime;
        core = _core;
        memory = _memory;
        disk = _disk;
    }

    public static Job.JobState parseState(String input) {
        switch (input) {
            case "0":
                return JobState.SUBMITTED;
            case "1":
                return JobState.WAITING;
            case "2":
                return JobState.RUNNING;
            default:
                return null;
        }
    }

    public int getSubmitTime() {
        return submitTime;
    }

    public int getJobID() {
        return jobID;
    }

    public int getEstRunTime() {
        return estRunTime;
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
