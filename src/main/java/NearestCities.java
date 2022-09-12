import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public interface NearestCities extends Remote{
    List<CityData> sameNameCitiesCount(String cityName) throws RemoteException, FileNotFoundException;
    double getLatByCityName(String cityName) throws RemoteException;
    double getLonByCityName(String cityName) throws RemoteException;
    int addTicket(double lat, double lon, int distance) throws RemoteException;
    Map<CityData, Integer> nearestCitiesCalculate(Ticket ticket) throws RemoteException, FileNotFoundException;
    String getReadyForecast(String city) throws RemoteException;
    Map<CityData, Integer> nearestCities(int ticketId) throws RemoteException, FileNotFoundException;
    String deleteTicket(int id) throws RemoteException;
    boolean isTicketId(int id) throws RemoteException;
//    Queue<Ticket> ticketQueue() throws RemoteException;
    Map<Integer, Ticket> ticketMapQueue(List<Integer> ticketList) throws RemoteException;
    String calculateInThread(List<Integer> ticketList) throws RemoteException, FileNotFoundException;
    String addListResults(List<Integer> listTicketId) throws RemoteException;
}
