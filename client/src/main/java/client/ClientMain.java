package client;

import api.LibraryService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        String host = (args.length > 0) ? args[0] : "127.0.0.1";
        Registry reg = LocateRegistry.getRegistry(host, 1099);
        var lib = (LibraryService) reg.lookup("LibraryService");

        System.out.println(lib.queryByIsbn("978-0132350884"));
        System.out.println(lib.loanByIsbn("978-0132350884"));
        System.out.println(lib.queryByIsbn("978-0132350884"));
        System.out.println(lib.returnByIsbn("978-0132350884"));
        System.out.println(lib.queryByIsbn("978-0132350884"));
    }
}
