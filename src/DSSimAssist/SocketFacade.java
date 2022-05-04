package DSSimAssist;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import DSSimObjects.*;

//Handles the socket connection and the reading/writing of client server messages
public class SocketFacade {
    public final String host;
    public final int port;
    private Socket socket;
    private DataOutputStream dos;
    private BufferedReader dis;

    public SocketFacade(String host, int port) throws UnknownHostException, IOException {
        socket = open(host, port);
        this.host = host;
        this.port = port;
        dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dos = new DataOutputStream(socket.getOutputStream());
    }

    //Open a new Socket
    private Socket open(String host, int port) throws UnknownHostException, IOException {
        if (socket != null) {
            socket.close();
        }
        return new Socket(host, port);
    }

    //Close the current socket
    public void close() throws IOException {
        socket.close();
        socket = null;
    }

    //Write to the server and flush
    public void write(String s) throws IOException {
        dos.write(s.getBytes());
        dos.flush();
    }

    //Read from the server
    private String read() throws IOException {
        return dis.readLine();
    }

    //Write to the server and return its response
    public String writeRead(String s) throws IOException {
        write(s);
        return read();
    }

    //Write to the server and print its response
    public void writePrint(String s) throws IOException {
        System.out.println(writeRead(s));
    }

    //Write to the server and ignore its response
    public void writeDiscard(String s) throws IOException {
        write(s);
        read();
    }

    //Read an entire DATA block and return it as a String[]
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

    public String[] writeReadData(String s) throws IOException {
        write(s);
        return readData();
    }

    //Requests all the servers and returns them as a list
    public List<Server> getServers() throws IOException {
        write(DSSim.GETSALL);
        String[] serverStrings = readData();
        List<Server> servers = new ArrayList<Server>();
        for (int i = 0; i < serverStrings.length; i++) {
            servers.add(new Server(serverStrings[i].split(" ")));
        }
        return servers;
    }

    //Requests all capable servers and returns them as a list
    public List<Server> getCapableServers(Job j) throws IOException {
        write(DSSim.GETSCAPABLE(j));
        String[] serverStrings = readData();
        List<Server> servers = new ArrayList<Server>();
        for (int i = 0; i < serverStrings.length; i++) {
            servers.add(new Server(serverStrings[i].split(" ")));
        }
        return servers;
    }

    //Requests all available servers and returns them as a list
    public List<Server> getAvailableServers(Job j) throws IOException {
        write(DSSim.GETSAVAIL(j));
        String[] serverStrings = readData();
        List<Server> servers = new ArrayList<Server>();
        for (int i = 0; i < serverStrings.length; i++) {
            servers.add(new Server(serverStrings[i].split(" ")));
        }
        return servers;
    }
}
