import java.util.Map;

public class ResultsAnswer {
    private CityData cityData;
    private int distance;
    private String openWeatherMapJsonParser;

    public ResultsAnswer(CityData cityData, int distance, String openWeatherMapJsonParser) {
        this.cityData = cityData;
        this.distance = distance;
        this.openWeatherMapJsonParser = openWeatherMapJsonParser;
    }

    public CityData getCityData() {
        return cityData;
    }

    public void setCityData(CityData cityData) {
        this.cityData = cityData;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getOpenWeatherMapJsonParser() {
        return openWeatherMapJsonParser;
    }

    public void setOpenWeatherMapJsonParser(String openWeatherMapJsonParser) {
        this.openWeatherMapJsonParser = openWeatherMapJsonParser;
    }
}
