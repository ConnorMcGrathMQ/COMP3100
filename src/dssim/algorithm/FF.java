package dssim.algorithm;

import dssim.Connection;
import dssim.Server;
import dssim.ServerComparator;

import java.util.Collections;
import java.util.List;


public class FF implements DSSimAlgorithm<FF> {
    public static void run(Connection sim) {
        List<Server> servers;
        while (sim.getJob() != null) {
            servers = sim.getServersAvailable(sim.getJob());
            if (servers.size() == 0) {
                servers = sim.getServersCapable(sim.getJob());
            }
            sim.schedule(servers.get(0));
        }
        sim.quit();
    }
}
