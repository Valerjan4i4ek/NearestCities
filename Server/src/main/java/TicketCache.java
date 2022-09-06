import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class TicketCache {
    MySQLClass sql = new MySQLClass();
    List<Ticket> ticketList = sql.getTicketCache();
    Map<Integer, Ticket> ticketInnerMap = getInnerMapTicketCache(ticketList);
    List<Integer> listTicketId;
    int countTicketId;

    public Map<Integer, Ticket> getInnerMapTicketCache(List<Ticket> list){
        Map<Integer, Ticket> map = new ConcurrentHashMap<>();
        for(Ticket ticket : list){
            map.put(ticket.getId(), ticket);
        }
        return map;
    }

    public int addTicket(double lat, double lon, int distance) {
        incrementTicketId();
        ticketInnerMap.put(countTicketId, new Ticket(countTicketId, lat, lon, distance));
        sql.addTicket(new Ticket(countTicketId, lat, lon, distance));
        return countTicketId;
    }

    public boolean isTicketId(int id){
        if(ticketInnerMap.containsKey(id)){
            return false;
        }
        else{
            return true;
        }
//        int i = sql.isTicketId(id);
//        if(i != 0){
//            return false;
//        }
//        else {
//            return true;
//        }
    }

    public Map<Integer, Ticket> ticketMapQueue(){
        return ticketInnerMap;
    }

    public Queue<Ticket> ticketQueue() {
        Queue<Ticket> queue = new PriorityQueue<Ticket>();
        for(Map.Entry<Integer, Ticket> entry : ticketInnerMap.entrySet()){
            queue.add(entry.getValue());
        }

        return queue;
    }


    public String deleteTicket(int id) {
        ticketInnerMap.remove(id);
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
