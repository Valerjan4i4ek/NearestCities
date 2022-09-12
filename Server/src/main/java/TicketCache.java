import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TicketCache {
    MySQLClass sql = new MySQLClass();
    List<Ticket> ticketList = sql.getTicketCache();
    Map<Integer, Ticket> ticketInnerMap = getInnerMapTicketCache(ticketList);
    Map<Integer, Map<CityData, Integer>> resultsMap = new LinkedHashMap<>();
    List<Integer> resultsList = new LinkedList<>();
    List<Integer> listTicketId;
    int countTicketId;

    public Map<Integer, Ticket> getInnerMapTicketCache(List<Ticket> list){
        Map<Integer, Ticket> map = new ConcurrentHashMap<>();
        for(Ticket ticket : list){
            map.put(ticket.getId(), ticket);
        }
        return map;
    }

    public Map<Integer, Map<CityData, Integer>> getResults(){
        Map<Integer, Map<CityData, Integer>> map = new LinkedHashMap<>();
        for(Integer i : resultsList){
            if(ticketInnerMap.containsKey(i)){
                map.put(i, resultsMap.get(i));
            }
//            else{
//                System.out.println("another client task or no task");
//            }
        }
        return map;
    }

    public void addListResults(List<Integer> listTicketId){
        resultsList.addAll(listTicketId);
    }

    public void addMapResults(int ticketId, Map<CityData, Integer> map){
        resultsMap.put(ticketId, map);
    }

    public Map<Integer, Ticket> ticketMapQueue(List<Integer> ticketList){
        Map<Integer, Ticket> map = new LinkedHashMap<>();
        addListResults(ticketList);
        if(ticketList != null && !ticketList.isEmpty()){
            for(int i : ticketList){
                if(ticketInnerMap.containsKey(i)){
                    map.put(i, ticketInnerMap.get(i));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
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
    }
//    public Queue<Ticket> ticketQueue() {
//        Queue<Ticket> queue = new PriorityQueue<Ticket>();
//        for(Map.Entry<Integer, Ticket> entry : ticketInnerMap.entrySet()){
//            queue.add(entry.getValue());
//        }
//
//        return queue;
//    }


    public void deleteTicket(int id) {
        if(ticketInnerMap.containsKey(id) && resultsMap.containsKey(id)){
            ticketInnerMap.remove(id);
            resultsMap.remove(id);
//            resultsList.remove(id);
            sql.deleteTicket(id);
        }
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
