package server;

import api.LibraryService;
import api.dto.*;
import model.Book;
import server.persistence.BookRepository;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LibraryServiceImpl extends UnicastRemoteObject implements LibraryService {
    private final BookRepository repo = new BookRepository();

    private final Map<String, Book> books = new ConcurrentHashMap<>();
    private final Map<String, String> titleToIsbn = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public LibraryServiceImpl() throws RemoteException {
        super();
        try {
            repo.ensureInit();
            loadFromDisk();                 // <-- carga inicial real
            if (books.isEmpty()) {          // Si está vacío, inicializa con un  algunos libros de demo
                var b1 = new Book("978-0132350884", "Clean Code", 3, 0);
                var b2 = new Book("978-0262033848", "CLRS", 2, 1);
                books.put(b1.getIsbn(), b1); titleToIsbn.put(b1.getTitle(), b1.getIsbn());
                books.put(b2.getIsbn(), b2); titleToIsbn.put(b2.getTitle(), b2.getIsbn());
                saveToDisk();
            }
            System.out.println("BD en: " + repo.getDataPath().toAbsolutePath());
        } catch (IOException e) {
            throw new RemoteException("Error inicializando repositorio", e);
        }
    }

    private void rebuildIndexes(Collection<Book> all) {
        books.clear(); titleToIsbn.clear();
        for (Book b : all) {
            books.put(b.getIsbn(), b);
            titleToIsbn.put(b.getTitle(), b.getIsbn());
        }
    }

    private void loadFromDisk() throws IOException {
        var list = repo.loadAll();
        rebuildIndexes(list);
    }

    private void saveToDisk() throws IOException {
        repo.saveAll(books.values());
    }

    private ReentrantLock lockFor(String isbn){ return locks.computeIfAbsent(isbn, k -> new ReentrantLock()); }

    @Override public LoanResult loanByIsbn(String isbn) throws RemoteException {
        var lock = lockFor(isbn); lock.lock();
        try {
            var b = books.get(isbn);
            if (b == null) return new LoanResult(false, null, "No existe");
            if (b.getAvailable() <= 0) return new LoanResult(false, null, "Sin stock");
            b.lendOne();
            saveToDisk();                                     // <--
            return new LoanResult(true, LocalDate.now().plusDays(7), "Préstamo OK");
        } catch (IOException io) {
            throw new RemoteException("No pude guardar la BD", io);
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
            saveToDisk();                                     // <--
            return new ReturnResult(true, "Devolución OK");
        } catch (IOException io) {
            throw new RemoteException("No pude guardar la BD", io);
        } finally { lock.unlock(); }
    }
}
