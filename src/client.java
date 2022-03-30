import java.net.*;
import java.util.*;
import java.io.*;

public class client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 50000;
    public static void main(String[] args) throws IOException {
        try {
            Socket sock = new Socket(HOST, PORT);

            DSSim sim = new DSSim(new DataOutputStream(sock.getOutputStream()), new BufferedReader(new InputStreamReader(sock.getInputStream())));

            sim.writePrint(DSSim.HELO);
            sim.writePrint(DSSim.AUTH);
            sim.writePrint(DSSim.REDY);
            List<Server> servers = sim.getServers();
            Collections.sort(servers, new ServerComparator());
            Server best = servers.get(0);
            servers.removeIf(s -> (s.compareTo(best) < 0));
            Iterator<Server> serverCycler = new ServerCycler(servers);
            while(serverCycler.hasNext()) {
                System.out.println(serverCycler.next().serverName);
            }
            
            
            sock.close();
        } catch (UnknownHostException e) {
            System.err.println("Unknown Host Exception: " + HOST);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IO Exception to: " + HOST);
            System.exit(1);
        }
    }
}