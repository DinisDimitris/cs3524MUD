package mud;

import java.io.BufferedReader;
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

        System.out.println(gameJoined);
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

        while (true) {
            String direction = handleGameInput();
            location = server.moveThing(gameName, location, direction, userName);

            System.out.println("\nYour current location is " + location);
        }

    }

    private static String handleGameInput(){
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


            //TODO can change this so that we check for user duplicates when entering username
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
