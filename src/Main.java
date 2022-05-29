import dssim.*;
import dssim.algorithm.*;

import java.io.IOException;


public class Main {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 50000;
    private static final String USER = System.getProperty("user.name");
    private static Connection sim;
    private static String algorithmArg = "";

    public static void main(String[] args) throws IOException {
        sim = new Connection(HOST, PORT);
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-a") && i+1 < args.length) {
                algorithmArg = args[i+1];
            }
        }
        switch (algorithmArg) {
            case "FC":
                FC.run(sim);
                break;
            case "NC":
                NC.run(sim);
                break;
            case "":
                System.err.println(String.format("No Algorithm Selected", algorithmArg));
                System.exit(1);
            default:
                System.err.println(String.format("Algorithm \"%s\" not found", algorithmArg));
                System.exit(1);
        }
    }
}