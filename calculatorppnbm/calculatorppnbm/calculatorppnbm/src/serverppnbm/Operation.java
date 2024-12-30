package serverppnbm;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Operation extends Remote {
    public double hitungPPnBM(double hargaBarang, String jenisBarang, double tarifPPnBM) throws RemoteException;
    public double getTarifPPnBM(String jenisBarang) throws RemoteException;
}
