package dssim.algorithm;

import dssim.Connection;
import dssim.Server;

import java.util.List;


public class FC implements DSSimAlgorithm<FC> {
    public static void run(Connection sim) { //An implementation of First Capable for testing purposes
        List<Server> servers;
        while (sim.getJob() != null) {
            servers = sim.getServersCapable(sim.getJob());
            sim.schedule(servers.get(0));
        }
        sim.quit();
    }
}
