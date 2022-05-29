package dssim.algorithm;

import dssim.Connection;
import dssim.Server;
import dssim.ServerComparator;
import static dssim.ServerComparator.ComparisonMetric.*;

import java.util.Collections;
import java.util.List;


public class DF implements DSSimAlgorithm<DF> {
    public static void run(Connection sim) {
        List<Server> servers;
        while (sim.getJob() != null) {
            servers = sim.getServersAvailable(sim.getJob());
            if (servers.isEmpty()) {
                servers = sim.getServersCapable(sim.getJob());
                servers.sort(new ServerComparator(sim, new int[]{1<<2 | 1<<3, 0, 0}, SERVERSTATUS, BESTCOREUSAGE, LOWJOBTIME));
            } else {
                servers.sort(new ServerComparator(sim, new int[]{0}, LOWCORECOUNT));
            }
            sim.schedule(servers.get(0));
        }
        sim.quit();
    }
}
