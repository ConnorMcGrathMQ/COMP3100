import java.util.Comparator;

public class ServerComparator implements Comparator<Server>{

    @Override
    public int compare(Server serverA, Server serverB) {
        //Inverted to sort descending
        return Integer.compare(serverB.getCore(), serverA.getCore());
    }
    
}
