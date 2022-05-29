package dssim.algorithm;

import dssim.Connection;
import dssim.Server;
import dssim.ServerComparator;
import static dssim.ServerComparator.ComparisonMetric.*;

import java.util.Collections;
import java.util.List;


public class NC implements DSSimAlgorithm<NC> {
    public static void run(Connection sim) { //Implementation of Nested Comparisons with set values and metrics
        List<Server> servers;
        //While jobs still available
        while (sim.getJob() != null) {
            //Try collecting servers available for the job
            servers = sim.getServersAvailable(sim.getJob());
            //If no such servers exist
            if (servers.isEmpty()) {
                //Get the servers that are capable of the job
                servers = sim.getServersCapable(sim.getJob());
                //If had to fall back to capable servers, organise them by if they are:
                    //Either Idle or Active
                    //Provide the most efficient usage of cores
                    //Have the lowest estimated time from summing all jobs on the server
                //With each subsequent test only occurring if the previous one was inconclusive (same value, like a tiebreaker)
                servers.sort(new ServerComparator(sim, new int[]{1<<2 | 1<<3, 0, 0}, SERVERSTATUS, BESTCOREUSAGE, LOWJOBTIME));
            } else {
                //Otherwise, just sort by the servers currently available cores
                servers.sort(new ServerComparator(sim, new int[]{0}, LOWCORECOUNT));
            }
            //Then schedule the job on the server with the highest priority after sorting, index 0
            sim.schedule(servers.get(0));
        }
        //After all jobs complete, get the DS-Server and the Connection to end
        sim.quit();
    }
}
