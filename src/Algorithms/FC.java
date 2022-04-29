package Algorithms;

import java.io.IOException;

import DSSimAssist.*;
import DSSimObjects.*;

public class FC implements DSSimAlgorithm {
    public static void run(SocketFacade sim) throws IOException {
        try {
            sim.writeDiscard(DSSim.HELO);
            sim.writeDiscard(DSSim.AUTH(System.getProperty("user.name")));

            //Collect jobs until NONE response and assign to first capable server
            String output;
            while(DSSim.getResponseType(output = sim.writeRead(DSSim.REDY)) != DSSim.ResponseType.NONE) {
                switch (DSSim.getResponseType(output)) {
                    case JOBN:
                        Job toSchedule = new Job(output.split(" "));
                        sim.write(DSSim.GETSCAPABLE(toSchedule));
                        Server capable = new Server(sim.readData()[0].split(" "));
                        sim.writeDiscard(DSSim.SCHD(toSchedule, capable));
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
