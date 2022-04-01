import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SocketFacade {
    private Socket socket;
    private DataOutputStream dos;
    private BufferedReader dis;

    public SocketFacade(String host, int port) throws UnknownHostException, IOException {
        socket = new Socket(host, port);
        dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dos = new DataOutputStream(socket.getOutputStream());
    }

    public void close() throws IOException {
        socket.close();
    }

    public void write(String s) throws IOException {
        dos.write(s.getBytes());
        dos.flush();
    }

    public String read() throws IOException {
        return dis.readLine();
    }

    public String writeRead(String s) throws IOException {
        write(s);
        return read();
    }

    public void writePrint(String s) throws IOException {
        System.out.println(writeRead(s));
    }

    public void writeDiscard(String s) throws IOException {
        write(s);
        read();
    }

    public String[] readData() throws IOException {
        String dataInfo = read();
        int dataFragmentCount = Integer.parseInt(dataInfo.split(" ")[1]);
        write(DSSim.OK);
        String[] dataFragments = new String[dataFragmentCount];
        for (int i = 0; i < dataFragmentCount; i++) {
            dataFragments[i] = read();
        }
        writeDiscard(DSSim.OK);
        return dataFragments;
    }

    public List<Server> getServers() throws IOException {
        write(DSSim.GETSALL);
        String[] serverStrings = readData();
        List<Server> servers = new ArrayList<Server>();
        for (int i = 0; i < serverStrings.length; i++) {
            servers.add(new Server(serverStrings[i].split(" ")));
        }
        return servers;
    }
}
