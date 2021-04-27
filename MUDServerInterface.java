package mud;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface MUDServerInterface extends Remote {
    Boolean intializeGame(String GameName) throws RemoteException;
    Boolean createMUDs(int numberofgames) throws RemoteException;
    String getStartLocation(String MUDName) throws  RemoteException;
    String showServers() throws RemoteException;
    int addPlayer(String name, String mudInstance) throws RemoteException;
    String getSummary(String MUDName) throws RemoteException;
    String moveThing(String MUDName,String location, String dir,String thing) throws RemoteException;
    String showUsers(String MUDNamme) throws  RemoteException;
    String showLocation(String MUDName, String location) throws RemoteException;
    String pickItem(String MUDName, String location, String username,String item) throws RemoteException;
    String showUserItems(String MUDName, String user) throws RemoteException;
    String removeUser(String MUDName, String user, String location) throws RemoteException;
    void addClient(String clientName, MUDClientInterface client) throws RemoteException;
    String sendMessageTo(String sender, String recipient,String message) throws RemoteException;
    MUD findPlayer(String name) throws  RemoteException;
}
