package mud;

import java.rmi.RemoteException;

public class MUDClientImpl implements MUDClientInterface {

    @Override
    public void receiveMessage(String message) throws RemoteException {
            System.out.println(message);
    }
}
