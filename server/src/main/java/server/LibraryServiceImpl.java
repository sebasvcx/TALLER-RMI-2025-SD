package server;

import api.LibraryService;
import api.dto.*;
import model.Book;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LibraryServiceImpl extends UnicastRemoteObject implements LibraryService {
    private final Map<String, Book> books = new ConcurrentHashMap<>();
    private final Map<String, String> titleToIsbn = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public LibraryServiceImpl() throws RemoteException {
        super();
        // TODO: cargar db (por ahora hardcode para que arranque)
        var b1 = new Book("978-0132350884", "Clean Code", 3, 0);
        books.put(b1.getIsbn(), b1); titleToIsbn.put(b1.getTitle(), b1.getIsbn());
    }

    private ReentrantLock lockFor(String isbn){ return locks.computeIfAbsent(isbn, k -> new ReentrantLock()); }

    @Override public LoanResult loanByIsbn(String isbn) throws RemoteException {
        var lock = lockFor(isbn); lock.lock();
        try {
            var b = books.get(isbn);
            if (b == null) return new LoanResult(false, null, "No existe");
            if (b.getAvailable() <= 0) return new LoanResult(false, null, "Sin stock");
            b.lendOne();
            return new LoanResult(true, LocalDate.now().plusDays(7), "Préstamo OK");
        } finally { lock.unlock(); }
    }

    @Override public LoanResult loanByTitle(String title) throws RemoteException {
        var isbn = titleToIsbn.get(title);
        if (isbn == null) return new LoanResult(false, null, "Título no existe");
        return loanByIsbn(isbn);
    }

    @Override public QueryResult queryByIsbn(String isbn) throws RemoteException {
        var b = books.get(isbn);
        return (b == null) ? new QueryResult(false, 0, "No existe") :
                new QueryResult(true, b.getAvailable(), "OK");
    }

    @Override public ReturnResult returnByIsbn(String isbn) throws RemoteException {
        var lock = lockFor(isbn); lock.lock();
        try {
            var b = books.get(isbn);
            if (b == null) return new ReturnResult(false, "No existe");
            if (b.getLentCopies() <= 0) return new ReturnResult(false, "Nada por devolver");
            b.returnOne();
            return new ReturnResult(true, "Devolución OK");
        } finally { lock.unlock(); }
    }
}
