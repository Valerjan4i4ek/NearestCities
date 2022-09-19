import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class MyThread extends Thread{
    private final static String JSON_FILE_NAME = "Server/citylist.json";
    private final TicketCache ticketCache;
    Calculator calculation = new Calculator();
    OpenWeatherMapJsonParser openWeatherMapJsonParser = new OpenWeatherMapJsonParser();
//    TicketCache ticketCache = new TicketCache();

    public MyThread(TicketCache ticketCache){
        this.ticketCache = ticketCache;
    }


    private static List<CityData> jsonToCityData(String fileName) throws FileNotFoundException {
        return Arrays.asList(new Gson().fromJson(new FileReader(fileName), CityData[].class));
    }

    @Override
    public void run(){
        while (true){
            try {
                Thread.sleep(1000);

                Map<Integer, Ticket> ticketMap = ticketCache.ticketMapQueueWithoutArgs();
                Map<CityData, Integer> map = new LinkedHashMap<>();
                ResultsAnswer resultsAnswer = null;
                if(ticketMap != null && !ticketMap.isEmpty()){

                    for(Map.Entry<Integer, Ticket> entry : ticketMap.entrySet()){
                        try {
                            map = nearestCitiesCalculate(entry.getValue());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        resultsAnswer = resultsAnswerInThread(map);
                        ticketCache.addResultsAnswer(entry.getKey(), resultsAnswer);
//                        ticketCache.addMapResults(entry.getKey(), map);

                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ResultsAnswer resultsAnswerInThread(Map<CityData, Integer> map){
        String s = "";
        ResultsAnswer resultsAnswer = null;
        if(map != null && !map.isEmpty()){
            for(Map.Entry<CityData, Integer> pair : map.entrySet()){
                s = openWeatherMapJsonParser.getReadyForecast(pair.getKey().getName());
                resultsAnswer = new ResultsAnswer(pair.getKey(), pair.getValue(), s);
            }
        }

        return resultsAnswer;
    }

    public Map<CityData, Integer> nearestCitiesCalculate(Ticket ticket) throws RemoteException, FileNotFoundException {

        List<CityData> cityDataList = jsonToCityData(JSON_FILE_NAME);
        Map<CityData, Integer> map = new HashMap<>();
        int distance = 0;
        double deltaLat = calculation.computeDelta(ticket.getLat());
        double deltaLon = calculation.computeDelta(ticket.getLon());
        double aroundLat = calculation.around(ticket.getDistance(), deltaLat);
        double aroundLon = calculation.around(ticket.getDistance(), deltaLon);
        if(cityDataList != null && !cityDataList.isEmpty()){
            for (CityData cityData : cityDataList){
                if(cityData.getCoord().getLat() >= ticket.getLat() - aroundLat && cityData.getCoord().getLat() <= ticket.getLat() + aroundLat){
                    if(cityData.getCoord().getLon() >= ticket.getLon() - aroundLon && cityData.getCoord().getLon() <= ticket.getLon() + aroundLon){
                        if(calculation.distanceBetweenCoordinate(ticket.getLat(), ticket.getLon(), cityData.getCoord().getLat(), cityData.getCoord().getLon())*1000 <= ticket.getDistance()){
                            distance = /*ticket.getDistance() - */(int) calculation.distanceBetweenCoordinate(ticket.getLat(), ticket.getLon(), cityData.getCoord().getLat(), cityData.getCoord().getLon());
                            map.put(cityData, distance);
                        }
                    }
                }
            }
        }
        TreeMap<CityData, Integer> treeMap = new TreeMap<>(map);
        map = treeMap;
        Map<CityData, Integer> returnMap = map.entrySet().stream()
                .sorted(Map.Entry.<CityData, Integer>comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        return returnMap;
    }
}
