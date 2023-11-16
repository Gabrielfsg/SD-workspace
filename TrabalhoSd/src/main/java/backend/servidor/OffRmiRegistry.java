package backend.servidor;

import java.rmi.server.UnicastRemoteObject;

class OffRmiRegistry extends Thread {
    @Override
    public void run() {
        try {
            UnicastRemoteObject.unexportObject(java.rmi.registry.LocateRegistry.getRegistry(1099), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
