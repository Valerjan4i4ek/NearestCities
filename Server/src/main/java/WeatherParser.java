public interface WeatherParser {
    double getLatByCityName(String cityName);
    double getLonByCityName(String cityName);
    String getReadyForecast(String city);
}
