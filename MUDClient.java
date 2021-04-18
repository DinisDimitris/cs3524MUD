package mud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MUDClient {

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static MUDServerInterface server = null;
    private static MUDClientInterface client = null;

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage:\njava MUDClient <host> <port> <callbackport>") ;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        int callbackPort = Integer.parseInt(args[2]) ;


        System.setProperty("java.security.policy", "security.policy") ;
        System.setSecurityManager(new SecurityManager()) ;

        try{
            // create client Instance and export it
            MUDClientImpl clientInstance = new MUDClientImpl();
            client = (MUDClientInterface) UnicastRemoteObject.exportObject(clientInstance, callbackPort);

            // Obtain the server from rmiregistry
            String regURL = "rmi://" + hostname + ":" + port + "/MUDServer";
            System.out.println("Looking up " + regURL + '\n');
            server = (MUDServerInterface) Naming.lookup(regURL);

            System.out.println("Connection Established!\n");
            initialize();

        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }



    private static void initialize()  {
        System.out.println("------------WELCOME TO THE MUD SERVER------------\n");
        try {
            System.out.println(">Input number of MUDs:\n");
            String s = reader.readLine();
            server.createMUDs(3);

        }

        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
