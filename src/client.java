import java.io.IOException;

import Algorithms.*;
import DSSimAssist.*;

public class client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 50000;
    private static SocketFacade sim;
    
    public static void main(String[] args) throws IOException {
        sim = new SocketFacade(HOST, PORT);
        if (args.length > 0) {
            switch (args[0]) {
                case "LRR":
                    LRR.run(sim);
                    break;
                case "FC":
                    FC.run(sim);
                    break;
                case "FF":
                    FF.run(sim);
                    break;
                case "-BF":
                    BF.run(sim);
                    break;
                case "WF":
                    WF.run(sim);
                    break;
            }
        }
    }
}