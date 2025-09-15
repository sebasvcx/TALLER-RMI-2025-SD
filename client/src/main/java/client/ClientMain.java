package client;

import api.LibraryService;
import api.dto.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        String host = (args.length > 0) ? args[0] : "127.0.0.1";
        int port = 1099;

        Registry reg = LocateRegistry.getRegistry(host, port);
        LibraryService lib = (LibraryService) reg.lookup("LibraryService");

        // --- Datos que asumimos están en data/books.json del servidor ---
        String isbnCleanCode = "978-0132350884";  // Clean Code (3 copias en el ejemplo)
        String isbnCLRS      = "978-0262033848";  // CLRS (2 copias, 1 prestada en el ejemplo)
        String tituloPrincipito = "El principito";
        String tituloCien = "Cien años de soledad";

        System.out.println("=== PRUEBAS OK ===");
        // 1) Consultar disponibilidad por ISBN
        System.out.println("Query Clean Code:       " + lib.queryByIsbn(isbnCleanCode));
        // 2) Prestar por ISBN
        System.out.println("Loan Clean Code:        " + lib.loanByIsbn(isbnCleanCode));
        // 3) Volver a consultar para ver el decremento
        System.out.println("Query Clean Code (2):   " + lib.queryByIsbn(isbnCleanCode));
        // 4) Prestar por Título
        System.out.println("Loan por título (El principito): " + lib.loanByTitle(tituloPrincipito));
        // 5) Devolver por ISBN
        System.out.println("Return Clean Code:      " + lib.returnByIsbn(isbnCleanCode));
        // 6) Otra consulta a CLRS
        System.out.println("Query CLRS:             " + lib.queryByIsbn(isbnCLRS));

        System.out.println("\n=== PRUEBAS DE ERRORES A PROPÓSITO ===");
        // 7) ISBN inexistente (consulta)
        String isbnFake = "000-0000000000";
        System.out.println("Query ISBN inexistente: " + lib.queryByIsbn(isbnFake));

        // 8) Préstamo por título inexistente
        System.out.println("Loan por título inexistente: " + lib.loanByTitle("Título que no existe"));

        // 9) Forzar “sin stock”: intenta prestar varias veces el mismo ISBN hasta vaciar
        System.out.println("\nForzando SIN STOCK de Clean Code:");
        for (int i = 1; i <= 5; i++) {
            LoanResult r = lib.loanByIsbn(isbnCleanCode);
            System.out.println("  Intento " + i + " -> " + r);
            if (!r.ok()) break; // cuando se quede sin stock, saldrá false
        }
        System.out.println("Query Clean Code (post-sin-stock): " + lib.queryByIsbn(isbnCleanCode));

        // 10) Devolución de un ISBN que no tiene préstamos: llama return hasta que falle
        System.out.println("\nDevoluciones Clean Code hasta vaciar lentCopies:");
        for (int i = 1; i <= 5; i++) {
            ReturnResult r = lib.returnByIsbn(isbnCleanCode);
            System.out.println("  Devolución " + i + " -> " + r);
            if (!r.ok()) break; // cuando ya no haya préstamos, debería fallar con mensaje amigable
        }

        // 11) Argumentos vacíos (según tus validaciones deberían devolver mensaje de error)
        System.out.println("\nArgumentos vacíos:");
        try { System.out.println("LoanByIsbn(\"\"): " + lib.loanByIsbn("")); }
        catch (Exception e) { System.out.println("  Excepción esperada: " + e.getMessage()); }

        try { System.out.println("LoanByTitle(\"\"): " + lib.loanByTitle("")); }
        catch (Exception e) { System.out.println("  Excepción esperada: " + e.getMessage()); }

        // 12) Última prueba OK: prestar por título de "Cien años de soledad"
        System.out.println("\nLoan por título (Cien años de soledad): " + lib.loanByTitle(tituloCien));
        System.out.println("FIN DE PRUEBAS.");
    }
}
