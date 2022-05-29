package dssim;

import java.util.Comparator;

public class ServerComparator implements Comparator<Server> {
    Connection sim;
    int[] parameters;
    ComparisonMetric[] metrics;

    public enum ComparisonMetric { //Available Metrics for server comparison
        HIGHCORECOUNT,
        LOWCORECOUNT,
        HIGHSPARECORECOUNT,
        LOWSPARECORECOUNT,
        LOWJOBCOUNT,
        LOWJOBTIME,
        BESTCOREUSAGE,
        SERVERSTATUS
    }

    public ServerComparator(Connection sim, int[] parameters, ComparisonMetric... metrics) {
        this.sim = sim;
        this.parameters = parameters;
        this.metrics = metrics;
    }

    @Override
    public int compare(Server serverA, Server serverB) {
        int metricNum = 0;
        int value = 0;
        while (metricNum < metrics.length && value == 0) { //Progressively compare metrics until one returns a non 0 response
            switch (metrics[metricNum]) {
                case HIGHCORECOUNT:
                    value = -compareCore(serverA, serverB, true, -parameters[metricNum]);
                    break;
                case LOWCORECOUNT:
                    value = compareCore(serverA, serverB, true, parameters[metricNum]);
                    break;
                case HIGHSPARECORECOUNT:
                    value = compareCore(serverA, serverB, false, -parameters[metricNum]);
                    break;
                case LOWSPARECORECOUNT:
                    value = compareCore(serverA, serverB, false, parameters[metricNum]);
                    break;
                case LOWJOBCOUNT:
                    value = compareTotalJobs(serverA, serverB, parameters[metricNum]);
                    break;
                case LOWJOBTIME:
                    value = compareEstJobTime(serverA, serverB, parameters[metricNum]);
                    break;
                case BESTCOREUSAGE:
                    value = compareCoreUsage(serverA, serverB, parameters[metricNum]);
                    break;
                case SERVERSTATUS:
                    value = compareStatus(serverA, serverB, parameters[metricNum]);
                    break;
            }
            metricNum++;
        }
        return value;
    }

    private int compareCore(Server serverA, Server serverB, boolean compareBaseValue, int insignificanceThreshold) {
        if (insignificanceThreshold > 0) {
            return Integer.compare(Math.min(serverA.getCore(compareBaseValue), insignificanceThreshold), Math.min(serverB.getCore(compareBaseValue), insignificanceThreshold));
        } else {
            return Integer.compare(Math.max(serverA.getCore(compareBaseValue), -insignificanceThreshold), Math.max(serverB.getCore(compareBaseValue), -insignificanceThreshold));
        }
    }

    private int compareTotalJobs(Server serverA, Server serverB, int insignificanceThreshold) {
        return Integer.compare(Math.min(serverA.getJobs().size(), insignificanceThreshold), Math.min(serverB.getJobs().size(), insignificanceThreshold));
    }

    private int compareEstJobTime(Server serverA, Server serverB, int insignificanceThreshold) {
        return Integer.compare(Math.min(serverA.sumAssignedJobEstTime(), insignificanceThreshold), Math.min(serverB.sumAssignedJobEstTime(), insignificanceThreshold));
    }

    private int compareCoreUsage(Server serverA, Server serverB, int insignificanceThreshold) {
        float spareCoreA = (serverA.getCore() - sim.getJob().getCore());
        float spareCoreB = (serverB.getCore() - sim.getJob().getCore());
        spareCoreA /= (float) serverA.getCore(true);
        spareCoreB /= (float) serverB.getCore(true);
        spareCoreA *= spareCoreA < 0 ? 0 : 100;
        spareCoreB *= spareCoreB < 0 ? 0 : 100;
        return Integer.compare(Math.max((int)spareCoreA, insignificanceThreshold), Math.max((int)spareCoreB, insignificanceThreshold));
    }

    private int compareStatus(Server serverA, Server serverB, int serverStatus) { //server status is a mask of ServerState IDs
        return Integer.compare((1 << serverA.getState().id() & serverStatus) != 0 ? 1 : 0, (1 << serverB.getState().id() & serverStatus) != 0? 1 : 0);
    }


}
