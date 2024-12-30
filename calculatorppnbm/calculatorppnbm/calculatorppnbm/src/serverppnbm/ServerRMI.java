package serverppnbm;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRMI {
    public static Operation services;
    
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(4000);
            services = new OperationImpl();
            registry.rebind("services", services);
            System.out.println("Server PPnBM is running on port 4000...");
        } catch (RemoteException e) {
            System.out.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
