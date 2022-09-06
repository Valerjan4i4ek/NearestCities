import java.io.FileNotFoundException;
import java.rmi.RemoteException;

public class MyThread extends Thread{
    private final RemoteNearestCitiesServer remoteNearestCitiesServer = new RemoteNearestCitiesServer();
    public void run(){
        try {
            remoteNearestCitiesServer.ololo();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
