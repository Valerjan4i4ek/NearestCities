import java.io.Serializable;

public class Ticket implements Serializable, Comparable<Ticket> {
    private int id;
    private double lat;
    private double lon;
    private int distance;

    public Ticket(int id, double lat, double lon, int distance) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    public int compareTo(Ticket o) {
        return 0;
    }
}
