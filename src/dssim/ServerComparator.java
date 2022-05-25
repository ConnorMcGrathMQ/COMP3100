package dssim;

import java.util.Comparator;

public class ServerComparator implements Comparator<Server> {
    Connection sim;
    ComparisonMetric[] metrics;

    public enum ComparisonMetric {
        MOSTCORE,
        LEASTCORE,
        MOSTMEMORY,
        LEASTMEMORY,
        MOSTDISK,
        LEASTDISK,
        MOSTWAITINGJOBS,
        LEASTWAITINGJOBS,
        MOSTRUNNINGJOBS,
        LEASTRUNNINGJOBS,
        MOSTTOTALJOBS,
        LEASTTOTALJOBS,
        MOSTESTJOBTIME,
        LEASTESTJOBTIME
    }
    public ServerComparator(Connection sim, ComparisonMetric... metrics) {
        this.sim = sim;
        this.metrics = metrics;
    }

    @Override
    public int compare(Server serverA, Server serverB) {
        int metricNum = 0;
        int value = 0;
        while (metricNum < metrics.length && value == 0) {
            switch (metrics[metricNum]) {
                case MOSTCORE:
                    value = compareCore(serverA, serverB);
                    break;
                case LEASTCORE:
                    value = -compareCore(serverA, serverB);
                    break;
                case MOSTMEMORY:
                    value = compareMemory(serverA, serverB);
                    break;
                case LEASTMEMORY:
                    value = -compareMemory(serverA, serverB);
                    break;
                case MOSTDISK:
                    value = compareDisk(serverA, serverB);
                    break;
                case LEASTDISK:
                    value = -compareDisk(serverA, serverB);
                    break;
                case MOSTWAITINGJOBS:
                    value = compareWaitingJobs(serverA, serverB);
                    break;
                case LEASTWAITINGJOBS:
                    value = -compareWaitingJobs(serverA, serverB);
                    break;
                case MOSTRUNNINGJOBS:
                    value = compareRunningJobs(serverA, serverB);
                    break;
                case LEASTRUNNINGJOBS:
                    value = -compareRunningJobs(serverA, serverB);
                    break;
                case MOSTTOTALJOBS:
                    value = compareTotalJobs(serverA, serverB);
                    break;
                case LEASTTOTALJOBS:
                    value = -compareTotalJobs(serverA, serverB);
                    break;
                case MOSTESTJOBTIME:
                    value = compareEstJobTime(serverA, serverB);
                    break;
                case LEASTESTJOBTIME:
                    value = -compareEstJobTime(serverA, serverB);
                    break;
            }
            metricNum++;
        }
        return value;
    }

    private int compareCore(Server serverA, Server serverB) {
        return Integer.compare(serverA.getCore(), serverB.getCore());
    }

    private int compareMemory(Server serverA, Server serverB) {
        return Integer.compare(serverA.getMemory(), serverB.getMemory());
    }

    private int compareDisk(Server serverA, Server serverB) {
        return Integer.compare(serverA.getDisk(), serverB.getDisk());
    }

    private int compareWaitingJobs(Server serverA, Server serverB) {
        return Integer.compare(sim.countJobs(serverA, Job.JobState.WAITING), sim.countJobs(serverB, Job.JobState.WAITING));
    }

    private int compareRunningJobs(Server serverA, Server serverB) {
        return Integer.compare(sim.countJobs(serverA, Job.JobState.RUNNING), sim.countJobs(serverB, Job.JobState.RUNNING));
    }

    private int compareTotalJobs(Server serverA, Server serverB) {
        return Integer.compare(sim.countJobs(serverA), sim.countJobs(serverB));
    }

    private int compareEstJobTime(Server serverA, Server serverB) {
        return Integer.compare(sim.estimateWaitingTime(serverA), sim.estimateWaitingTime(serverB));
    }

}
