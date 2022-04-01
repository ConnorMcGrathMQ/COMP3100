import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 50000;
    private static final String USER = System.getProperty("user.name");
    private static SocketFacade sim;
    
    public static void main(String[] args) throws IOException {
        try {
            //Create new handler for the connection
            sim = new SocketFacade(HOST, PORT);

            //Send default start sequence
            sim.writeDiscard(DSSim.HELO);
            sim.writeDiscard(DSSim.AUTH(USER));
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
        } catch (UnknownHostException e) {
            System.err.println("Unknown Host Exception: " + HOST);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IO Exception to: " + HOST);
            System.exit(1);
        } finally {
            sim.close();
        }
    }
}