package mud;
import java.rmi.RemoteException;
import java.util.*;

public class MUDServerImpl implements MUDServerInterface{
    private static Hashtable<String, MUD> mudGames = new Hashtable<>();

    private int maxGames = 5;

    private static Map<String, MUDClientInterface> clientCallbacks = new HashMap<>();
    // keep track of games
    private static int countGames = 0;


    public void addClient(String clientName, MUDClientInterface client) throws RemoteException{
        clientCallbacks.put(clientName,client);
    }
    @Override
    public Boolean intializeGame(String GameName)   {
        try {
            // create and store mud game
            mudGames.put(GameName,new MUD("mymud.edg","mymud.msg","mymud.thg"));
            System.out.println(GameName + " Has been created!\n");
            return true;

        }
        catch (Exception e) {
            System.out.println("Game could not be created.\n");
            e.printStackTrace();
        }

        return false;
    }


    public Boolean createMUDs(int numberofgames) throws RemoteException {
        if (maxGames < countGames + numberofgames) {
            System.out.println("Maximum number of games reached");
            return false;
        }

        for(int i =0 ; i <  numberofgames; i++){
            intializeGame("MUD" + countGames);
            countGames++;

        }
        return true;
    }


    public String showUsers(String MUDNamme) throws  RemoteException{
        MUD currentGame = mudGames.get(MUDNamme);
        String usersToString = "Players in this MUD:";
        ArrayList<User> users = currentGame.getUsers();
        for ( int i = 0 ; i < users.size(); i++){
            usersToString += "\n->" + users.get(i).getName();
        }
        return usersToString;
    }
    public String getStartLocation(String MUDName) throws  RemoteException{
        MUD currentGame = mudGames.get(MUDName);
        return currentGame.startLocation();
    }

    public String getSummary(String MUDName) throws RemoteException{
        MUD currentGame = mudGames.get(MUDName);
        return currentGame.toString();
    }

    public String showLocation(String MUDName, String location) throws RemoteException{
        MUD currentGame = mudGames.get(MUDName);
        return currentGame.locationInfo(location);
    }

    public String pickItem(String MUDName, String location, String username,String item) throws RemoteException{
        MUD currentGame = mudGames.get(MUDName);
        if (currentGame.pickItem(location,username,item) ){

                // send callback message to active clients in current MUD
                for (User activeUser: currentGame.getUsers()){
                    for (Map.Entry<String, MUDClientInterface> pair : clientCallbacks.entrySet()){
                        if (activeUser.getName().equals(pair.getKey())){
                            pair.getValue().receiveMessage("\n" + username + " has picked up " + item);

                        }
                }
            }
            return "\nYou have picked up: " + item + "\n";
        }

        else return "\nThere is no " + item + " in your location\n";
    }

    public String showUserItems(String MUDName, String user) throws RemoteException{
        MUD currentGame = mudGames.get(MUDName);
        return currentGame.showItems(user);
    }
    public String moveThing(String MUDName,String location, String dir,String thing) throws RemoteException{
        MUD currentGame = mudGames.get(MUDName);
        for (User activeUser : currentGame.getUsers()) {
            for (Map.Entry<String, MUDClientInterface> pair : clientCallbacks.entrySet()) {
                if (activeUser.getName().equals(pair.getKey())) {
                    pair.getValue().receiveMessage("\n" + thing + " has moved " + dir + " from " + location + "\n");

                }

            }

        }
        return currentGame.moveThing(location,dir,thing);
    }

    public String sendMessageTo(String sender, String recipient,String message) throws RemoteException{
        if ( sender.equals(recipient)) return "Cannot send message to yourself";
        for (Map.Entry<String ,MUDClientInterface> pair : clientCallbacks.entrySet()){
            if ( pair.getKey().equals(recipient)){
                pair.getValue().receiveMessage("\nmessage from " + sender + " : " + message + "\n");
                return "\nMessage sent\n";
            }
        }
        return "\nRecipient not found\n";
    }


    public String showServers() throws RemoteException{
        String s = "";
        for (String game: mudGames.keySet() ){
            s += game + " ";
        }
        if (s.length() > 1){
            return "Games available: " + s;
        }
        // TODO ask user if he wants to create a server
        else return "No games available";
    }

    public int addPlayer(String name, String mudInstance) throws RemoteException{
                    MUD currentGame = mudGames.get(mudInstance);
                    if (currentGame == null) return -2;
                    int added = currentGame.AddUser(name);

                    // user added , send callback
                    if (added == 2) {
                        for (User activeUser : currentGame.getUsers()) {
                            for (Map.Entry<String, MUDClientInterface> pair : clientCallbacks.entrySet()) {
                                if (activeUser.getName().equals(pair.getKey())) {
                                    pair.getValue().receiveMessage("\n" + name + " has entered the server");
                                }
                            }
                        }
                        return 1;
                    }
                    // user already exists
                    else if (added == 1)return 0;

                    // max capacity exceeded
                    else if ( added == 0) return -1;

                    return -2;
                }


    public String removeUser(String MUDName, String user, String location) throws RemoteException{
        MUD mudGame = mudGames.get(MUDName);
        if (mudGame.removeUser(user,location)) {

            // user exits , send callback
            for (User activeUser : mudGame.getUsers()) {
                for (Map.Entry<String, MUDClientInterface> pair : clientCallbacks.entrySet()) {
                    if (activeUser.getName().equals(pair.getKey())) {
                        pair.getValue().receiveMessage("\n" + user + " has left the server");

                    }

                }

            }
            return "User " + user + " has left " + MUDName;
        }

        else return "User not found";

    }

    // find player in the servers in case of CTRL C shutdown
    public MUD findPlayer(String name) throws  RemoteException{
        for (Map.Entry<String,MUD> mudGame : mudGames.entrySet()){
            for (User users: mudGame.getValue().getUsers()){
                if (users.getName().equals(name)) return mudGame.getValue();
            }
        }
        return null;
    }
}
