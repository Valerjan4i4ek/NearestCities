public class ConsoleData {
    private String cityName;
    private double lat;
    private double lon;
    private int distance;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "ConsoleData{" +
                "cityName='" + cityName + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", distance=" + distance +
                '}';
    }
}
