import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenWeatherMapJsonParser implements WeatherParser{
    private final static String API_CALL_TEMPLATE = "https://api.openweathermap.org/data/2.5/forecast?q=";
    private final static String API_KEY_TEMPLATE = "&units=metric&APPID=de834929791b3dbe8e75a9cba9eaaf2a";
    private final static String USER_AGENT = "Chrome/104.0.0.0";

    @Override
    public double getLatByCityName(String cityName) {
        double lat = 0.0;
        try {
            String jsonRawData = downloadJsonRawData(cityName);
            lat = getLat(jsonRawData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lat;
    }

    @Override
    public double getLonByCityName(String cityName) {
        double lon = 0.0;
        try {
            String jsonRawData = downloadJsonRawData(cityName);
            lon = getLon(jsonRawData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lon;
    }

    private static String downloadJsonRawData(String city) throws Exception {
        String urlString = API_CALL_TEMPLATE + city + API_KEY_TEMPLATE;
        URL urlObject = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = connection.getResponseCode();
        if (responseCode == 404) {
            throw new IllegalArgumentException();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public double getLat(String data) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        double lat = 0.0;
        JsonNode cityNode;
        JsonNode coordNode;


        try{
            cityNode = objectMapper.readTree(data).get("city");
            coordNode = cityNode.get("coord");
            lat = coordNode.get("lat").asDouble();
            System.out.println(lat);
        }catch (IOException e){
            e.printStackTrace();
        }
        return lat;
    }
    public double getLon(String data) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        double lon = 0.0;
        JsonNode cityNode;
        JsonNode coordNode;

        try{
            cityNode = objectMapper.readTree(data).get("city");
            coordNode = cityNode.get("coord");
            lon = coordNode.get("lon").asDouble();
            System.out.println(lon);
        }catch (IOException e){
            e.printStackTrace();
        }
        return lon;
    }
}
