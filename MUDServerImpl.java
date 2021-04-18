package mud;
import java.rmi.RemoteException;
import java.util.Hashtable;

public class MUDServerImpl implements MUDServerInterface{
    private static Hashtable<String, MUD> mudGames = new Hashtable<>();
    private int maxGames = 3;

    public Boolean createMUDs(int numberofgames) throws RemoteException {
        if (maxGames < numberofgames) {
            System.out.println("Number of games exceed limit");
            return false;
        }

        for(int i = 0; i < numberofgames + 1; i++){
            intializeGame("MUD" + i);

        }
        return true;
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
}
