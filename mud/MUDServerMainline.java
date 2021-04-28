package mud;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MUDServerMainline {


    public static void main(String args[]) {
        if (args.length < 2) {
            System.err.println("Usage: \njava MUDServerMainline <registryPort> <serverPort>");
        }

        try {
            // register serv on rmiregistry
            String hostname = (InetAddress.getLocalHost()).getCanonicalHostName();
            int registryPort = Integer.parseInt(args[0]);
            int serverPort = Integer.parseInt(args[1]);

            System.setProperty("java.security.policy", "security.policy");
            System.setSecurityManager(new SecurityManager());

            String regURL = "rmi://" + hostname + ":" + registryPort + "/MUDServer";
            System.out.println("Registering " + regURL + '\n');

            MUDServerImpl mudServer = new MUDServerImpl();
            MUDServerInterface mudStub = (MUDServerInterface) UnicastRemoteObject.exportObject(mudServer, serverPort);

            Naming.rebind(regURL, mudStub);
            // initialize one mud
            mudServer.createMUDs(1);

        }

        catch (UnknownHostException | RemoteException | MalformedURLException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
