import dssim.*;
import dssim.algorithm.*;

import java.io.IOException;


public class Main {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 50000;
    private static final String USER = System.getProperty("user.name");
    private static Connection sim;

    public static void main(String[] args) throws IOException {
        sim = new Connection(HOST, PORT);
        FC.run(sim);
    }
}