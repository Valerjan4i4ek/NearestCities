import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OpenWeatherMapJsonParser implements WeatherParser{
    private final static String API_CALL_TEMPLATE = "https://api.openweathermap.org/data/2.5/forecast?q=";
    private final static String API_KEY_TEMPLATE = "&units=metric&APPID=de834929791b3dbe8e75a9cba9eaaf2a";
    private final static String USER_AGENT = "Chrome/104.0.0.0";
    private final static DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static DateTimeFormatter OUTPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM dd", Locale.US);

    @Override
    public String getReadyForecast(String city) {
        String result;
        try {
            String jsonRawData = downloadJsonRawData(city);
            List<String> linesOfForecast = convertRawDataToList(jsonRawData);
            result = String.format("%s:%s%s", city, System.lineSeparator(), parseForecastDataFromList(linesOfForecast));
        } catch (IllegalArgumentException e) {
            return String.format("Can't find \"%s\" city. Try another one, for example: \"Dnipro\" or \"Manchester\"", city);
        } catch (Exception e) {
            e.printStackTrace();
            return "The service is not available, please try later";
        }
        return result;
    }

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

    private static List<String> convertRawDataToList(String data) throws Exception {
        List<String> weatherList = new ArrayList<>();

        JsonNode arrNode = new ObjectMapper().readTree(data).get("list");
        if (arrNode != null && arrNode.isArray()) {
            for (final JsonNode objNode : arrNode) {
                weatherList.add(objNode.toString());
                break;
            }
        }
        return weatherList;
    }

    private static String parseForecastDataFromList(List<String> weatherList) throws Exception {
        final StringBuffer sb = new StringBuffer();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String line : weatherList) {
            {
                String dateTime;
                JsonNode mainNode;
                JsonNode weatherArrNode;
                JsonNode cloudsNode;

                try {
                    mainNode = objectMapper.readTree(line).get("main");
                    weatherArrNode = objectMapper.readTree(line).get("weather");

                    for (final JsonNode objNode : weatherArrNode) {
                        dateTime = objectMapper.readTree(line).get("dt_txt").toString();
                        cloudsNode = objectMapper.readTree(line).get("clouds");
                        sb.append(formatForecastData(dateTime, objNode.get("main").toString(), mainNode.get("temp").asDouble(), cloudsNode.toString()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private static String formatForecastData(String date, String description, double temperature, String clouds) throws Exception {
        LocalDateTime forecastDateTime = LocalDateTime.parse(date.replaceAll("\"", ""), INPUT_DATE_TIME_FORMAT);
        String formattedDateTime = forecastDateTime.format(OUTPUT_DATE_TIME_FORMAT);

        String formattedTemperature;
        long roundedTemperature = Math.round(temperature);
        if (roundedTemperature > 0) {
            formattedTemperature = "+" + String.valueOf(Math.round(temperature));
        } else {
            formattedTemperature = String.valueOf(Math.round(temperature));
        }

        String formattedDescription = description.replaceAll("\"", "");
        String formattedClouds = clouds.replaceAll("[^0-9]", "");

//        String weatherIconCode = WeatherUtils.weatherIconsCodes.get(formattedDescription);

        return String.format("%s  temperature: %s  precipitation: %s  %s clouds: %s percent \n", formattedDateTime, formattedTemperature, formattedDescription, System.lineSeparator(), formattedClouds);
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
