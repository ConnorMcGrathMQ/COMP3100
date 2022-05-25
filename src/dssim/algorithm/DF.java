package dssim.algorithm;

import dssim.Connection;
import dssim.Server;
import dssim.ServerComparator;
import static dssim.ServerComparator.ComparisonMetric;

import java.util.Collections;
import java.util.List;


public class DF implements DSSimAlgorithm<DF> { //Distributed Fit
    public static void run(Connection sim) {
        List<Server> servers;
        while (sim.getJob() != null) {
            servers = sim.getServersAvailable(sim.getJob());
            if (servers.size() == 0) {
                servers = sim.getServersCapable(sim.getJob());
            }
            Collections.sort(servers, new ServerComparator(sim, ComparisonMetric.LEASTESTJOBTIME, ComparisonMetric.MOSTCORE));
            sim.schedule(servers.get(0));
        }
        sim.quit();
    }
}
