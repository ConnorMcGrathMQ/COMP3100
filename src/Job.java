public class Job {
    private int submitTime;
    private int jobID;
    private int estRunTime;
    private int core;
    private int memory;
    private int disk;

    public Job(String[] args) {
        this(args[0], args[1], args[2], args[3], args[4], args[5]);
    }

    public Job(String _submitTime, String _jobID, String _estRunTime, String _core, String _memory, String _disk) {
        this(Integer.parseInt(_submitTime), Integer.parseInt(_jobID), Integer.parseInt(_estRunTime), Integer.parseInt(_core), Integer.parseInt(_memory), Integer.parseInt(_disk));
    }

    public Job(int _submitTime, int _jobID, int _estRunTime, int _core, int _memory, int _disk) {
        submitTime = _submitTime;
        jobID = _jobID;
        estRunTime = _estRunTime;
        core = _core;
        memory = _memory;
        disk = _disk;    
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
