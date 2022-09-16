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
    MyThread myThread = new MyThread(ticketCache);

    public RemoteNearestCitiesServer(){
        myThread.start();
    }


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
        return ticketCache.addTicket(lat, lon, distance);
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
        }
        return map;
    }

//    @Override
//    public String deleteTicket(int id) throws RemoteException {
//        ticketCache.deleteTicket(id);
//        return "";
//    }

    @Override
    public boolean isTicketId(int id) throws RemoteException {
        return ticketCache.isTicketId(id);
    }

    @Override
    public Map<Integer, Ticket> ticketMapQueue(List<Integer> ticketList) throws RemoteException {
        return ticketCache.ticketMapQueue(ticketList);
    }
}

