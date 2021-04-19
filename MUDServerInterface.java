package mud;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MUDServerInterface extends Remote {
    Boolean intializeGame(String GameName) throws RemoteException;
    Boolean createMUDs(int numberofgames) throws RemoteException;
    String getStartLocation(String MUDName) throws  RemoteException;
    String showServers() throws RemoteException;
    int addPlayer(String name, String mudInstance) throws RemoteException;
}
