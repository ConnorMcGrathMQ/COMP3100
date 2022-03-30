public class Job {
    int submitTime;
    int jobID;
    int estRunTime;
    int core;
    int memory;
    int disk;

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
}
