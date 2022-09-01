import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class Client {
    public static final String UNIQUE_BINDING_NAME = "server.NearestCities";
    static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    static Registry registry;
    static NearestCities nearestCities;
//    static Queue<Ticket> queue;
    static Ticket ticket;
//    static boolean check = false;

    static {
        try {
            registry = LocateRegistry.getRegistry("127.0.0.1", 2732);
            nearestCities = (NearestCities) registry.lookup(UNIQUE_BINDING_NAME);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        letsStart();
    }

    public static void letsStart() throws IOException {
        String cityOrCoordinate = "";
        String cityName = "";
        int task;
        double lat;
        double lon;
        int distance;

        System.out.println("City name or coordinates? c/k");
        cityOrCoordinate = reader.readLine();

        if(cityOrCoordinate.equalsIgnoreCase("c")){
            System.out.println("for example: \"Dnipro\" or \"manchester\"");
            cityName = reader.readLine();
            cityName = sameNameCitiesCount(cityName);
            lat = nearestCities.getLatByCityName(cityName);
            lon = nearestCities.getLonByCityName(cityName);
            System.out.println("add distance");
            distance = Integer.parseInt(reader.readLine());
            task = nearestCities.addTicket(lat, lon, distance);
            ticket = new Ticket(task, lat, lon, distance);
            System.out.println("your ticket is №" + task);
//            queue = nearestCities.ticketQueue(ticket);
//            nearestCities(ticket);
        }
        else if(cityOrCoordinate.equalsIgnoreCase("k")){
            System.out.println("for example: \"48.0000\" or \"51.00\"");
            lat = Double.parseDouble(reader.readLine());
            lon = Double.parseDouble(reader.readLine());
            System.out.println("add distance");
            distance = Integer.parseInt(reader.readLine());
            task = nearestCities.addTicket(lat, lon, distance);
            ticket = new Ticket(task, lat, lon, distance);
            System.out.println("your ticket is №" + task);
//            queue = nearestCities.ticketQueue(ticket);
//            nearestCities(ticket);
        }else{
            System.out.println("Incorrect :(");
            letsStart();
        }
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()){
                try {
                    Thread.sleep(3000);
//                    nearestCities(ticket);
                    boolean check = nearestCities.isTicketId(ticket.getId());
                    if (!check){
                        nearestCities(ticket);
                        nearestCities.deleteTicket(ticket.getId());
                    }

//                    for(Ticket t : queue){
//                        Thread.sleep(3000);
//                        boolean check = nearestCities.isTicketId(t.getId());
//                        if (check){
//                            nearestCities(t);
//                            queue.remove(t);
//                            nearestCities.deleteTicket(t.getId());
//                        }
//
//                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()){
                try {
                    letsStart();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static void nearestCities(Ticket ticket) throws FileNotFoundException, RemoteException {
        Map<CityData, Integer> cityDataMap = nearestCities.nearestCities(ticket);

//        TreeMap<CityData, Integer> treeMap = new TreeMap<>(cityDataMap);
        for(Map.Entry<CityData, Integer> entry : cityDataMap.entrySet()){
            System.out.println(entry.getKey().getName() + " " + entry.getKey().getCountry() + " " + entry.getKey().getState() + " " + entry.getValue());
            System.out.println(nearestCities.getReadyForecast(entry.getKey().getName()));
            System.out.println();
        }
    }

    public static String sameNameCitiesCount(String cityName) throws IOException {
        List<CityData> list = nearestCities.sameNameCitiesCount(cityName);
        Map<Integer, String> checkedMap = new LinkedHashMap<>();
        int count = 1;
        int cityNumber = 0;
        String city;

        if(list.size() == 1){
            return cityName;
        }
        else if(list.size() > 1){
            for (CityData cityData : list) {
                System.out.print(count + " ");
                System.out.println(cityData.getId() + " " + cityData.getName() + " " + cityData.getCountry() + " " + cityData.getState());
                checkedMap.put(count, cityData.getName());
                count++;
            }
            System.out.println("choose your city (number)");
            cityNumber = Integer.parseInt(reader.readLine());
            for(Map.Entry<Integer, String> pair: checkedMap.entrySet()){
                if(pair.getKey()==cityNumber){
                    city = pair.getValue();
                    return city;
                }
            }
        }
        return "";
    }
}
