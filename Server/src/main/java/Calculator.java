public class Calculator {
    private static final int EARTH_RADIUS = 6371210;
    private static final double l = 111.1;

    public double computeDelta(double degrees){
        return (Math.PI/180) * EARTH_RADIUS * Math.cos(degreesToRadians(degrees));
    }

    public double degreesToRadians (double degrees){
        return degrees * (Math.PI/180);
    }

    public double around(int distance, double delta){
        return distance/delta;
    }

    public double distanceBetweenCoordinate(double latStart, double lonStart, double latFinal, double lonFinal){
        double delta = Math.acos(Math.sin(latStart) * Math.sin(latFinal) + Math.cos(latStart) * Math.cos(latFinal) * Math.cos(lonFinal - lonStart));
        return delta * l;
    }
}
