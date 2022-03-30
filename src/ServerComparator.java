import java.util.Comparator;

public class ServerComparator implements Comparator<Server>{

    @Override
    public int compare(Server serverA, Server serverB) {
        return Integer.compare(serverA.core, serverB.core);
    }
    
}
