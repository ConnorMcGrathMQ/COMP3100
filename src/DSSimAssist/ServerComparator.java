package DSSimAssist;

import java.util.Comparator;

import DSSimObjects.Server;

public class ServerComparator implements Comparator<Server>{

    @Override
    public int compare(Server serverA, Server serverB) {
        //Inverted to sort descending
        return Integer.compare(serverB.getCore(), serverA.getCore());
    }
    
}
