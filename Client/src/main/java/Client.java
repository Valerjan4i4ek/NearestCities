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
    static Map<Integer, Ticket> ticketMapQueue;
    static Ticket ticket;
    static List<Integer> personalClientTickets = new LinkedList<>();


    public static void reconnectTicketMapQueueInThread() {
        boolean connect = false;
        while (!connect) {
            try {
                registry = LocateRegistry.getRegistry("127.0.0.1", 2732);
                nearestCities = (NearestCities) registry.lookup(UNIQUE_BINDING_NAME);
                ticketMapQueue = nearestCities.ticketMapQueue(personalClientTickets);
                ticketMapQueueInThread();

                connect = true;
            }catch (RemoteException e) {
//                e.printStackTrace();
            } catch (NotBoundException e) {
//                e.printStackTrace();
            } catch (FileNotFoundException e) {
//                e.printStackTrace();
            }
            if (!connect) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    static {
        try {
            registry = LocateRegistry.getRegistry("127.0.0.1", 2732);
            nearestCities = (NearestCities) registry.lookup(UNIQUE_BINDING_NAME);
            ticketMapQueue = nearestCities.ticketMapQueue(personalClientTickets);
        } catch (RemoteException e) {
//            e.printStackTrace();
        } catch (NotBoundException e) {
//            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()){
                reconnectTicketMapQueueInThread();
            }
        }).start();




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
            personalClientTickets.add(task);
            System.out.println("your ticket is №" + task);
            letsStart();
        }
        else if(cityOrCoordinate.equalsIgnoreCase("k")){
            System.out.println("for example: \"48.0000\" or \"51.00\"");
            lat = Double.parseDouble(reader.readLine());
            lon = Double.parseDouble(reader.readLine());
            System.out.println("add distance");
            distance = Integer.parseInt(reader.readLine());
            task = nearestCities.addTicket(lat, lon, distance);
            ticket = new Ticket(task, lat, lon, distance);
            personalClientTickets.add(task);
            System.out.println("your ticket is №" + task);
            letsStart();
        }else{
            System.out.println("Incorrect :(");
            letsStart();
        }
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()){
                try {
                    ticketMapQueueInThread();
                } catch (RemoteException e) {
//                        e.printStackTrace();
                } catch (FileNotFoundException e) {
//                        e.printStackTrace();
                }
            }
        }).start();

    }


    public static void ticketMapQueueInThread() throws RemoteException, FileNotFoundException {
        if(ticketMapQueue != null && !ticketMapQueue.isEmpty()){
            for(Map.Entry<Integer, Ticket> entry : ticketMapQueue.entrySet()){

                if(personalClientTickets.contains(entry.getKey())){
                    boolean check = nearestCities.isTicketId(entry.getKey());
                    if(check){
                        System.out.println("Ticket № " + entry.getKey() + " is not ready yet");
                    }
                    else{
                        nearestCities(entry.getKey());
//                    deleteTicket(entry.getKey());
//                    ticketMapQueue.remove(entry.getKey());
                    }

                }

            }
        }

    }

    public static void nearestCities(int ticketId) throws FileNotFoundException, RemoteException {
        Map<CityData, Integer> cityDataMap = nearestCities.nearestCities(ticketId);
        if (cityDataMap != null && !cityDataMap.isEmpty()){
            for(Map.Entry<CityData, Integer> entry : cityDataMap.entrySet()){
                System.out.println(entry.getKey().getName() + " " + entry.getKey().getCountry() + " " + entry.getKey().getState() + " " + entry.getValue());
                System.out.println(nearestCities.getReadyForecast(entry.getKey().getName()));
                System.out.println();
            }
        }
    }



//    public static void deleteTicket(int id) throws RemoteException {
//        nearestCities.deleteTicket(id);
//    }

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
