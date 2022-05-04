package Algorithms;

import java.io.IOException;
import java.util.List;

import DSSimAssist.*;
import DSSimObjects.*;

public class FF implements DSSimAlgorithm{
    public static void run(SocketFacade sim) throws IOException {
        try {
            sim.writeDiscard(DSSim.HELO);
            sim.writeDiscard(DSSim.AUTH(System.getProperty("user.name")));

            //Collect jobs until NONE response and assign to best available server, if none, best capable
            String output;
            while(DSSim.getResponseType(output = sim.writeRead(DSSim.REDY)) != DSSim.ResponseType.NONE) {
                switch (DSSim.getResponseType(output)) {
                    case JOBN:
                        Job toSchedule = new Job(output.split(" "));
                        List<Server> servers = sim.getAvailableServers(toSchedule);
                        servers.removeIf(s -> {
                            try {
                                return sim.writeReadData(DSSim.LSTJ(s)).length > 0;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return false;
                        });
                        if (servers.size() == 0) {
                            servers = sim.getCapableServers(toSchedule);
                        }
                        sim.writeDiscard(DSSim.SCHD(toSchedule, servers.get(0)));
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
