import java.net.*;
import java.io.*;

public class client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 50000;
    private static final String HELO = "HELO\n";
    private static final String AUTH = "AUTH TEXT\n";
    private static final String REDY = "REDY\n";
    private static final String GETSALL = "GETS All\n";
    private static final String OK = "OK\n";
    public static void main(String[] args) throws IOException {
        try {
            Socket sock = new Socket(HOST, PORT);
            BufferedReader dis = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

            writeflush(dos, HELO);
            System.out.println(dis.readLine());
            writeflush(dos, AUTH);
            System.out.println(dis.readLine());
            writeflush(dos, REDY);
            System.out.println(dis.readLine());
            writeflush(dos, GETSALL);
            String Data = dis.readLine();
            int serverCount = Integer.parseInt(Data.split(" ")[1]);
            System.out.println(Data);
            writeflush(dos, OK);
            Server[] servers = new Server[serverCount];
            for (int i = 0; i < serverCount; i++) {
                String serverData = dis.readLine();
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

    private static void writeflush(DataOutputStream dos, String s) throws IOException {
        dos.write(s.getBytes());
        dos.flush();
    }
}
