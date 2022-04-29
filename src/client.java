import java.io.IOException;

import DSSimAssist.*;

public class client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 50000;
    private static SocketFacade sim;
    
    public static void main(String[] args) throws IOException {
        sim = new SocketFacade(HOST, PORT);
        //Algorithms.LRR.run(sim);
        Algorithms.FC.run(sim);
    }
}