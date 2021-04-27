package mud;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MUDClientInterface extends Remote {
    void receiveMessage(String message) throws RemoteException;
}
