package mud;

import java.rmi.RemoteException;

public class MUDClientImpl implements MUDClientInterface {

    @Override
    public void receiveMessage(String message) throws RemoteException {
            System.out.print(String.format("\033[%dA", 2));
            System.out.println();
            System.out.print("\033[2K");
            System.out.println(message);
            System.out.print("Enter command: ");
    }
}
