import java.net.*;
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
            String data = sim.writeRead(DSSim.GETSALL);
            int serverCount = Integer.parseInt(data.split(" ")[1]);
            System.out.println(data);
            sim.writePrint(DSSim.OK);
            Server[] servers = new Server[serverCount];
            for (int i = 0; i < serverCount; i++) {
                String serverData = sim.read();
                System.out.println(serverData);
                servers[i] = new Server(serverData.split(" "));
            }

            for (Server server : servers) {
                System.out.println(server.serverName);
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