package serverppnbm;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class OperationImpl extends UnicastRemoteObject implements Operation {
    
    public OperationImpl() throws RemoteException {
        super();
    }
    
    @Override
    public double getTarifPPnBM(String jenisBarang) throws RemoteException {
        switch(jenisBarang) {
            case "Barang Mewah Kategori 1":
                return 10.0;
            case "Barang Mewah Kategori 2":
                return 20.0;
            case "Barang Mewah Kategori 3":
                return 30.0;
            default:
                return 0.0;
        }
    }
    
    @Override
    public double hitungPPnBM(double hargaBarang, String jenisBarang, double tarifPPnBM) throws RemoteException {
        double ppnbm = hargaBarang * (tarifPPnBM/100);
        return hargaBarang + ppnbm;
    }
}
