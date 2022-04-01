import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 50000;
    private static final String USER = "username";
    private SocketFacade sim;
    public void main(String[] args) throws IOException {
        try {
            //Create new handler for the connection
            sim = new SocketFacade(HOST, PORT);

            //Send default start sequence
            sim.writePrint(DSSim.HELO);
            sim.writePrint(DSSim.AUTH(USER));
            sim.writePrint(DSSim.REDY);

            //Get all servers and sort by cores
            List<Server> servers = sim.getServers();
            Collections.sort(servers, new ServerComparator());

            //Filter out all servers with less the the most cores
            Server best = servers.get(0);
            servers.removeIf(s -> (s.compareTo(best) < 0));

            //Create cyclic iterator of filtered server list
            Iterator<Server> serverCycler = new ServerCycler(servers);

            //Collect jobs until NONE response and assign to servers cyclicly
            String output;
            while(DSSim.getResponseType(output = sim.writeRead(DSSim.REDY)) != DSSim.ResponseType.NONE) {
                if (DSSim.getResponseType(output) == DSSim.ResponseType.JOBN) {
                    Job toSchedule = new Job(output.split(" "));
                    sim.writeDiscard(DSSim.SCHD(toSchedule, serverCycler.next()));
                }
            }
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