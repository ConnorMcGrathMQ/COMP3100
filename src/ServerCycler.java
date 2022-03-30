import java.util.Iterator;
import java.util.List;

public class ServerCycler implements Iterator<Server>{
    List<Server> servers;
    int current;

    public ServerCycler(List<Server> _servers) {
        servers = _servers;
        current = 0;
    }

    @Override
    public boolean hasNext() {
        //Cycles through servers, so should always have something next
        return true;
    }

    @Override
    public Server next() {
        current++;
        if (current > servers.size()) {
            current = 0;
        }
        return servers.get(current);
    }

}
