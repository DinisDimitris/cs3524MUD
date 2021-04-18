package mud;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MUDServerInterface extends Remote {
    Boolean intializeGame(String GameName) throws RemoteException;
    Boolean createMUDs(int numberofgames) throws RemoteException;
}
