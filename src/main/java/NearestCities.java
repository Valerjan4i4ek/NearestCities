import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface NearestCities extends Remote{
    List<CityData> sameNameCitiesCount(String cityName) throws RemoteException, FileNotFoundException;
    double getLatByCityName(String cityName) throws RemoteException;
    double getLonByCityName(String cityName) throws RemoteException;
    int addTicket(double lat, double lon, int distance) throws RemoteException;
    Map<CityData, Integer> nearestCities(Ticket ticket) throws RemoteException, FileNotFoundException;
}
