package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        var svc = new LibraryServiceImpl();
        LocateRegistry.createRegistry(1099);             // Levanta rmiregistry embebido
        Registry reg = LocateRegistry.getRegistry();
        reg.rebind("LibraryService", svc);
        System.out.println("Servidor RMI listo en 1099 (LibraryService). Ctrl+C para salir.");
        Thread.currentThread().join();                   // mantener vivo
    }
}
