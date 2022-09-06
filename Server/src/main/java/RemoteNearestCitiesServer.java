import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class RemoteNearestCitiesServer implements NearestCities{
    private final static String JSON_FILE_NAME = "Server/citylist.json";
    OpenWeatherMapJsonParser openWeatherMapJsonParser = new OpenWeatherMapJsonParser();
    TicketCache ticketCache = new TicketCache();
    Calculator calculation = new Calculator();

    private static List<CityData> jsonToCityData(String fileName) throws FileNotFoundException {
        return Arrays.asList(new Gson().fromJson(new FileReader(fileName), CityData[].class));
    }

    @Override
    public List<CityData> sameNameCitiesCount(String cityName)throws RemoteException, FileNotFoundException{
        List<CityData> cityDataList = jsonToCityData(JSON_FILE_NAME);
        List<CityData> list = new ArrayList<>();
        if(!cityDataList.isEmpty()){
            for (CityData cityData : cityDataList) {
                if (cityData.getName().equalsIgnoreCase(cityName)) {
                    list.add(cityData);
                }
            }
        }
        System.out.println();
        for (CityData cityData : list) {
            System.out.println(cityData.getId() + " " + cityData.getName() + " " + cityData.getCountry());
        }
        return list;
    }

    @Override
    public double getLatByCityName(String cityName) throws RemoteException {
        return openWeatherMapJsonParser.getLatByCityName(cityName);
    }

    @Override
    public double getLonByCityName(String cityName) throws RemoteException {
        return openWeatherMapJsonParser.getLonByCityName(cityName);
    }

    @Override
    public int addTicket(double lat, double lon, int distance) throws RemoteException {
//        incrementTicketId();
//        sql.addTicket(new Ticket(countTicketId, lat, lon, distance));
        return ticketCache.addTicket(lat, lon, distance);
    }

    @Override
    public Map<CityData, Integer> nearestCitiesCalculate(Ticket ticket) throws RemoteException, FileNotFoundException {
        List<CityData> cityDataList = jsonToCityData(JSON_FILE_NAME);
        Map<CityData, Integer> map = new HashMap<>();
        int distance = 0;
        double deltaLat = calculation.computeDelta(ticket.getLat());
        double deltaLon = calculation.computeDelta(ticket.getLon());
        double aroundLat = calculation.around(ticket.getDistance(), deltaLat);
        double aroundLon = calculation.around(ticket.getDistance(), deltaLon);
        if(!cityDataList.isEmpty()){
            for (CityData cityData : cityDataList){
                if(cityData.getCoord().getLat() >= ticket.getLat() - aroundLat && cityData.getCoord().getLat() <= ticket.getLat() + aroundLat){
                    if(cityData.getCoord().getLon() >= ticket.getLon() - aroundLon && cityData.getCoord().getLon() <= ticket.getLon() + aroundLon){
                        if(calculation.distanceBetweenCoordinate(ticket.getLat(), ticket.getLon(), cityData.getCoord().getLat(), cityData.getCoord().getLon()) <= ticket.getDistance()){
                            distance = ticket.getDistance() - (int) calculation.distanceBetweenCoordinate(ticket.getLat(), ticket.getLon(), cityData.getCoord().getLat(), cityData.getCoord().getLon());
                            map.put(cityData, distance);
                        }
                    }
                }
            }
        }
        TreeMap<CityData, Integer> treeMap = new TreeMap<>(map);
        map = treeMap;
        Map<CityData, Integer> returnMap = map.entrySet().stream()
                .sorted(Map.Entry.<CityData, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        return returnMap;
    }

    @Override
    public String getReadyForecast(String city) throws RemoteException {
        return openWeatherMapJsonParser.getReadyForecast(city);
    }

    @Override
    public Map<CityData, Integer> nearestCities(int ticketId) throws RemoteException, FileNotFoundException {
        Map<Integer, Map<CityData, Integer>> chekMap = ticketCache.getResults();
        Map<CityData, Integer> map = new HashMap<>();
        if(chekMap != null && !chekMap.isEmpty()){
            if(chekMap.containsKey(ticketId)){
                map = chekMap.get(ticketId);
            }
            else{
                System.out.println("ololo taska ne gotova");
            }
        }
        return map;
    }

    @Override
    public String calculateInThread() throws FileNotFoundException, RemoteException {
        Map<Integer, Ticket> ticketMap = ticketCache.ticketMapQueue();
        Map<CityData, Integer> map = new LinkedHashMap<>();
//        Map<Integer, Map<CityData, Integer>> chekMap = new LinkedHashMap<>();
        if(ticketMap != null && !ticketMap.isEmpty()){
            for(Map.Entry<Integer, Ticket> entry : ticketMap.entrySet()){
                map = nearestCitiesCalculate(entry.getValue());
                ticketCache.addResult(entry.getKey(), map);
//                chekMap.put(entry.getKey(), map);
            }
        }
        return "";
    }

    @Override
    public String deleteTicket(int id) throws RemoteException {
        ticketCache.deleteTicket(id);
        return "";
    }

    @Override
    public boolean isTicketId(int id) throws RemoteException {
        return ticketCache.isTicketId(id);
    }

    @Override
    public Queue<Ticket> ticketQueue() throws RemoteException {
        return ticketCache.ticketQueue();
    }

    @Override
    public Map<Integer, Ticket> ticketMapQueue() throws RemoteException {
        return ticketCache.ticketMapQueue();
    }
}

//    public Map<CityData, Integer> nearestCities2(Ticket clientTicket) throws RemoteException, FileNotFoundException{
//        Map<Integer, Ticket> ticketMap = ticketCache.ticketMapQueue();
//        Map<CityData, Integer> map = new LinkedHashMap<>();
//        Map<Ticket, Map<CityData, Integer>> chekMap = new LinkedHashMap<>();
//        if(ticketMap != null && !ticketMap.isEmpty()){
//            for(Map.Entry<Integer, Ticket> entry : ticketMap.entrySet()){
//                map = nearestCitiesCalculate(entry.getValue());
//                chekMap.put(entry.getValue(), map);
//            }
//            if(ticketMap.containsKey(clientTicket.getId())){
//                return chekMap.get(clientTicket);
//            }
//            ticketCache.deleteTicket(clientTicket.getId());
//        }
//        return map;
//    }

//        Queue<Ticket> queue = ticketCache.ticketQueue();
//        Map<CityData, Integer> map = new LinkedHashMap<>();
//        Map<Ticket, Map<CityData, Integer>> chekMap = new LinkedHashMap<>();
//        if(queue != null && !queue.isEmpty()){
//            for(Ticket ticket : queue){
//                map = nearestCitiesCalculate(ticket);
//                chekMap.put(ticket, map);
////                ticketCache.deleteTicket(ticket.getId());
//            }
//            if(queue.contains(clientTicket)){
//                return chekMap.get(clientTicket);
//            }
//            ticketCache.deleteTicket(clientTicket.getId());
//        }
//
//        return map;
