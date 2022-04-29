package Algorithms;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import DSSimAssist.*;
import DSSimObjects.*;

public class LRR implements DSSimAlgorithm {
    public static void run(SocketFacade sim) throws IOException {
        try {
            //Send default start sequence
            sim.writeDiscard(DSSim.HELO);
            sim.writeDiscard(DSSim.AUTH(System.getProperty("user.name")));
            Job firstJob = new Job(sim.writeRead(DSSim.REDY).split(" "));

            //Get all servers and sort by cores
            List<Server> servers = sim.getServers();
            Collections.sort(servers, new ServerComparator());

            //Filter out all servers with less the the most cores
            Server best = servers.get(0);
            servers.removeIf(s -> !(s.getServerType().equals(best.getServerType())));

            //Create cyclic iterator of filtered server list
            Iterator<Server> serverCycler = new ServerCycler(servers);

            //Schedule First Job
            sim.writeDiscard(DSSim.SCHD(firstJob, serverCycler.next()));

            //Collect jobs until NONE response and assign to servers cyclicly
            String output;
            while(DSSim.getResponseType(output = sim.writeRead(DSSim.REDY)) != DSSim.ResponseType.NONE) {
                switch (DSSim.getResponseType(output)) {
                    case JOBN:
                        Job toSchedule = new Job(output.split(" "));
                        sim.writeDiscard(DSSim.SCHD(toSchedule, serverCycler.next()));
                        break;
                    default:
                        break;
                }
            }
            sim.writeDiscard(DSSim.QUIT);
        } catch (IOException e) {
            System.err.println("IO Exception to: " + sim.host);
            System.exit(1);
        } finally {
            sim.close();
        }
    }
}
