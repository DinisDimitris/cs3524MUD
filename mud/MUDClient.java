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
    private static String playerName = null;

    private static String gameJoined = null;
    private static String location;

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (playerName == null) {
                    System.out.println("exiting server...");
                }
                try {
                    // user is in a mud , delete user and drop all items
                    if (gameJoined != null) {
                        server.removeUser(gameJoined, playerName, location);
                    }
                    // player not in a game but still in a server
                    else {
                        playerName = null;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        if (args.length < 3) {
            System.err.println("Usage:\njava MUDClient <host> <port> <callbackport>");
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        int callbackPort = Integer.parseInt(args[2]);

        System.setProperty("java.security.policy", "security.policy");
        System.setSecurityManager(new SecurityManager());

        try {
            // create client Instance and export it
            MUDClientImpl clientInstance = new MUDClientImpl();
            client = (MUDClientInterface) UnicastRemoteObject.exportObject(clientInstance, callbackPort);

            // Obtain the server from rmiregistry
            String regURL = "rmi://" + hostname + ":" + port + "/MUDServer";
            System.out.println("Looking up " + regURL);
            server = (MUDServerInterface) Naming.lookup(regURL);
            clear();
            System.out.println("\nConnection to " + regURL + " established!");

            initialize();

        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private static void initialize() throws RemoteException {
        System.out.print("Enter a username to join the server: ");
        playerName = handleInput();
        // add client for callback
        server.addClient(playerName,client);
        mainMenu(playerName);
    }
    
    private static void helpMenu() {
        try {
            clear();
            System.out.println("\nWelcome to the MUD Server, " + playerName + "!");
            System.out.println(server.showServers());
        }
        catch (IOException e ) {
            System.out.println("Couldn't get list of games.\n");
            e.printStackTrace();
        }
        System.out.println("\nCommands:");
        System.out.println("join [game name] -> join a game");
        System.out.println("create [# of games] -> create new games");
        System.out.println("exit -> exit the MUD server\n");
    }

    private static void mainMenu(String playerName){
        String response = "";
        while (true) {
            helpMenu();
            System.out.println(response);
            System.out.print("Enter command: ");
            String s = handleInput();
            if (s == null) {
                response = "Invalid input - special characters are not allowed.";
            }
            else if (s.contains(" ")) {
                String[] w = s.split(" ");
                if (w[0].equals("join")) {
                    response = joinGame(w[1]);
                    if (response.startsWith("MUD"))
                        break;
                }
                else if (w[0].equals("create")) {
                    boolean isCreated = false;
                    response = "Can't create, maximum number of games reached";
                    try {
                        isCreated = server.createMUDs(Integer.parseInt(w[1]));
                    } 
                    catch (IOException | NumberFormatException e) {
                        response = "Couldn't parse '" + w[1] + "', please enter a number.";
                    }
                    if (isCreated)
                        response = w[1] + " mud games have been created!";
                }
            }
            else if (s.equals("exit")) {
                // todo: delete user from the whole server, add logged boolean
                System.exit(1);
            }
            else {
                response = "Invalid command.";
            }
        }
        try {
            playGame(response,playerName);
        } 
        catch (IOException e) {
            System.out.println("Couldn't join game, error: " + e);
        }
    }

    private static String joinGame(String game) {
        try{
            int userAdded = server.addPlayer(playerName, game);
            if (userAdded == 1) {
                return game;
            }
            // user exists, try joining another server
            else if (userAdded == 0 ) {
                return "User " + playerName + " already exists in the server";
            }
            // server full
            else if (userAdded == -1){
                return "Server is full";
            }
            else{
                return "Game '" + game + "' not found";
            }
        }
        catch (IOException e ){
            System.out.println("IO error occured");
            e.printStackTrace();
        }
        return null;
    }

    private static void playGame(String gameName,String userName) throws RemoteException {
        location = server.getStartLocation(gameName);
        try {
            String response = "";
            while (true) {
                clear();
                System.out.println("\nWelcome to " + gameName + ", " + userName + "!");
                System.out.println(server.showUsers(gameName));
                System.out.println(server.showLocation(gameName,location));
                System.out.println(response);


                int decision = handleGameInput();
                if (decision == 1) {
                    String direction = handleDirection();
                    location = server.moveThing(gameName, location, direction, userName);
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
                else if (decision ==  5){
                    System.out.println(server.showUsers(gameName));
                }
                else if (decision == 6){
                    System.out.print("Enter message: ");
                    String message = handleInput();
                    System.out.println(server.showUsers(gameName));
                    System.out.print("\nEnter recipient: ");
                    String recipient = handleInput();
                    System.out.println(server.sendMessageTo(userName, recipient, message));
                }
                else if (decision == 0 ){
                    System.out.println(server.removeUser(gameName,userName,location));
                    mainMenu(playerName);
                }


                
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private static int handleGameInput(){
        System.out.println("\nCommands:");
        System.out.println("move -> move\npick -> pick up item \ninventory -> show inventory\nplayers -> show players currently in the server" +
                "\nmessage -> send message to a player\nexit -> exit the game\n[Enter Key] -> view current location\n");
        System.out.print("Enter command: ");
        String in = handleInput();

        if (in == null) {
            System.out.println("Special characters are not allowed.\n");
            return handleGameInput();
        }

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

        else if (in.equals("message")){
            return 6;
        }

        else if(in.equals("exit")){
            return 0;
        }
        
        else{
            System.out.println("Invalid input\n");
            return handleGameInput();
        }
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
            
            if (notvalid) {
                return null;
            }

            return in;
        }
        catch(IOException e ){
            e.printStackTrace();
        }
        return null;
    }

    private static void clear() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
    }
}
