package dssim.algorithm;

import dssim.Connection;


public class FC implements DSSimAlgorithm<FC> {
    public static void run(Connection sim) {
        while (sim.getJob() != null) {
            sim.schedule(sim.getServersCapable(sim.getJob()).get(0));
        }
        sim.quit();
    }
}
