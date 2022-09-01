import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class TicketCache {
    MySQLClass sql = new MySQLClass();
    List<Ticket> ticketList = sql.getTicketCache();
    Map<Integer, Ticket> ticketInnerMap = getInnerMapAuthorization(ticketList);
    List<Integer> listTicketId;
    int countTicketId;

    public Map<Integer, Ticket> getInnerMapAuthorization(List<Ticket> list){
        Map<Integer, Ticket> map = new ConcurrentHashMap<>();
        for(Ticket ticket : list){
            map.put(ticket.getId(), ticket);
        }
        return map;
    }

    public int addTicket(double lat, double lon, int distance) {
        incrementTicketId();
        sql.addTicket(new Ticket(countTicketId, lat, lon, distance));
        return countTicketId;
    }

    public Queue<Ticket> ticketQueue(Ticket ticket) {
        Queue<Ticket> queue = new PriorityQueue<Ticket>();
        queue.add(ticket);
        return queue;
    }


    public String deleteTicket(int id) {
        sql.deleteTicket(id);
        return "";
    }

    public void incrementTicketId(){
        listTicketId = sql.getTicketId();
        if(listTicketId != null && !listTicketId.isEmpty()){
            countTicketId = listTicketId.get(listTicketId.size()-1);
            countTicketId++;
        }
        else{
            countTicketId++;
        }
    }
}
