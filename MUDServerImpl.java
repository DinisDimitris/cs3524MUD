package mud;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class MUDServerImpl implements MUDServerInterface{
    private static Hashtable<String, MUD> mudGames = new Hashtable<>();

    private int maxGames = 5;


    // keep track of games
    private static int countGames = 0;


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
            System.out.println("Number of games exceed limit");
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
        return currentGame.getUsers().toString();
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
        if (currentGame.pickItem(location,username,item) ) return "\nYou have picked up: " + item;

        else return "\nThere is no " + item + " in your location";
    }

    public String showUserItems(String MUDName, String user) throws RemoteException{
        MUD currentGame = mudGames.get(MUDName);
        return currentGame.showItems(user);
    }
    public String moveThing(String MUDName,String location, String dir,String thing) throws RemoteException{
        MUD currentGame = mudGames.get(MUDName);
        return currentGame.moveThing(location,dir,thing);
    }


    public String showServers() throws RemoteException{
        String s = "";
        for (String game: mudGames.keySet() ){
            s += game + " ";
        }
        if (s.length() > 1){
            return "Servers available: " + s;
        }
        // TODO ask user if he wants to create a server
        else return "No servers available";
    }

    public int addPlayer(String name, String mudInstance) throws RemoteException{
            for (String key : mudGames.keySet()) {
                if (mudInstance.equals(key)) {
                    MUD mudGame = mudGames.get(key);
                    boolean added = mudGame.AddUser(name);

                    // user added
                    if (added) return 1;
                    // user already exists
                    else return 0;

                }
            }
            // mud game not found
            return -1;
    }
    public String removeUser(String MUDName, String user, String location) throws RemoteException{
        MUD mudGame = mudGames.get(MUDName);
        if (mudGame.removeUser(user,location)) return "User " + user + " has left " + MUDName ;

        else return "User not found";

    }
}
