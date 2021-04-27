package mud;

import java.io.BufferedReader;
import java.util.List;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MUDClient {

    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static MUDServerInterface server = null;
    private static MUDClientInterface client = null;
    private static String playerPos = null;
    private static String playerName = null;

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage:\njava MUDClient <host> <port> <callbackport>") ;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        int callbackPort = Integer.parseInt(args[2]) ;


        System.setProperty("java.security.policy", "security.policy") ;
        System.setSecurityManager(new SecurityManager()) ;

        try {
            // create client Instance and export it
            MUDClientImpl clientInstance = new MUDClientImpl();
            client = (MUDClientInterface) UnicastRemoteObject.exportObject(clientInstance, callbackPort);

            // Obtain the server from rmiregistry
            String regURL = "rmi://" + hostname + ":" + port + "/MUDServer";
            System.out.println("Looking up " + regURL + '\n');
            server = (MUDServerInterface) Naming.lookup(regURL);

            System.out.println("Connection Established!");
            initialize();

        }

        catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }

    }

    private static void initialize()  {
        System.out.println("Enter username:");
        playerName = handleInput();
        String gameJoined = "";
        System.out.println("Welcome to the MUD Server " + playerName + "!\n");
        try {
            while (true) {
                helpMenu();
                String s = handleInput();
                assert s != null;
                if (s.equals("create")){
                    System.out.println("Enter number of servers\n");
                    s = handleInput();
                    boolean isCreated = server.createMUDs(Integer.parseInt(s));
                    if(isCreated) System.out.println(s + " mud games have been created!\n");
                    else System.out.println("Servers exceed game limit\n");
                }
                else if (s.equals("join")) {
                    gameJoined = joinGame();
                    break;
                }
            }

        System.out.println("Current users: " +  server.showUsers(gameJoined));
        playGame(gameJoined,playerName);


        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void reinitialize(String playerName){
        try {
            String gameJoined = "";
            while (true) {
                helpMenu();
                String s = handleInput();
                assert s != null;
                if (s.equals("create")){
                    System.out.println("Enter number of servers\n");
                    s = handleInput();
                    boolean isCreated = server.createMUDs(Integer.parseInt(s));
                    if(isCreated) System.out.println(s + " mud games have been created!\n");
                    else System.out.println("Servers exceed game limit\n");
                }
                else if (s.equals("join")) {
                    gameJoined = joinGame();
                    break;
                }
            }

            System.out.println("Current users: " +  server.showUsers(gameJoined));
            playGame(gameJoined,playerName);


        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void playGame(String gameName,String userName) throws RemoteException {
        String location = server.getStartLocation(gameName);
        System.out.println("\nWelcome to " + gameName + " " + userName + "!\n");
        String summary = server.getSummary(gameName);
        System.out.println(summary);

        System.out.println("\nYour current location is " + location);

        try {
            while (true) {
                int decision = handleGameInput();
                if (decision == 1) {
                    String direction = handleDirection();
                    location = server.moveThing(gameName, location, direction, userName);

                    System.out.println("\nYour current location is " + location);
                }
                else if (decision == 2){
                    System.out.println("What item do you want to pick?");
                    String item = handleInput();
                    System.out.println(server.pickItem(gameName,location,userName,item));
                }
                else if (decision == 3){
                    System.out.println(server.showLocation(gameName,location));
                }
                else if (decision == 4){
                    System.out.println(server.showUserItems(gameName,userName));
                }

                else if (decision == 0 ){
                    System.out.println(server.removeUser(gameName,userName,location));
                    System.out.println("Go back to menu? -> 1 Quit? -> 0");
                    String s = handleInput();

                    //TODO delete user from the whole server, add logged boolean
                    assert s != null;
                    if ( s.equals("0")) System.exit(1);

                    // start from menu again, save user name
                    else if (s.equals("1")) reinitialize(playerName);
                }
            }

        }

        catch (IOException e){
            e.printStackTrace();
        }
    }

    private static int handleGameInput(){
        try{
            System.out.println("\n:::Commands:::\n");
            System.out.println("Enter to see location info\nmove -> move\npick -> pick up item \nexit -> exit server" +
                    "\ninventory-> show inventory\nplayers-> show current players in the server");
            // check for special characters
            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            String in = reader.readLine().trim();
            Matcher m = p.matcher(in);
            boolean notvalid = m.find();

            if (in.equals("move")){
                return 1;
            }

            else if(in.equals("pick")){
                return 2;
            }

            else if(in.equals("")){
                return 3;
            }

            else if (in.equals("inventory")){
                return 4;
            }

            else if (in.equals("players")){
                return 5;
            }

            else if(in.equals("exit")){
                return 0;
            }

            else if(notvalid){
                System.out.println("Special characters are not allowed.\n");
                return handleGameInput();
            }
            else{
                System.out.println("Invalid input\n");
                return handleGameInput();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }
    private static String handleDirection(){
        try{
            System.out.println("\nEnter direction: north,east,south,west:\n");
            // check for special characters
            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            String in = reader.readLine().trim();
            Matcher m = p.matcher(in);
            boolean notvalid = m.find();

            if (in.equals("north") | in.equals("south") | in.equals("west") | in.equals("east")){
                return in;
            }

            else if(notvalid){
                System.out.println("Special characters are not allowed.\n");
                return handleInput();
            }
            else{
                System.out.println("Invalid input\n");
                return handleInput();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String handleInput(){
        try {
            // check for special characters
            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            String in = reader.readLine().trim();
            Matcher m = p.matcher(in);
            boolean notvalid = m.find();

            if(notvalid){
                System.out.println("Special characters not allowed.\n");
                return handleInput();
            }

            if (in.equals("")){
                System.out.println("Didn't catch that one. Try again?\n");
                return handleInput();
            }

            return in;
        }

        catch(IOException e ){
            e.printStackTrace();
        }
        return null;
    }

    private static void helpMenu(){
        System.out.println("List of commands:");
        System.out.println(" 1)join -> join a game");
        System.out.println(" 2)create -> create a game\n");
    }


    private static String joinGame() {
        try{
            System.out.println(server.showServers());
            System.out.print("\nEnter name of MUD you would like to join\n");
            String game = reader.readLine();
            int userAdded = server.addPlayer(playerName, game);

            if (userAdded == 1) {
                System.out.println("You have joined " + game);
                return game;
            }
            // user exists, try joining another server
            else if (userAdded == 0 ) {
                System.out.println("User " + playerName + " already exists in the server\n");
                return joinGame();
            }
            // game not found , try joining again
            else {
                System.out.println(game + " not found\n");
                return joinGame();
            }

        }
        catch (IOException e ){
            System.out.println("IO error occured\n");
            e.printStackTrace();
        }
        return null;
    }

}
