package server.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Book;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class BookRepository {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Path dataPath;

    public BookRepository() {
        this(System.getProperty("data.path", "data/books.json"));
    }
    public BookRepository(String path) {
        this.dataPath = Paths.get(path);
    }

    /** Crea el directorio si no existe y si el json no existe lo inicializa con lista vacía. */
    public void ensureInit() throws IOException {
        if (dataPath.getParent() != null) {
            Files.createDirectories(dataPath.getParent());
        }
        if (!Files.exists(dataPath)) {
            saveAll(Collections.emptyList());
        }
    }

    public synchronized List<Book> loadAll() throws IOException {
        if (!Files.exists(dataPath)) return Collections.emptyList();
        byte[] json = Files.readAllBytes(dataPath);
        if (json.length == 0) return Collections.emptyList();
        return mapper.readValue(json, new TypeReference<List<Book>>() {});
    }

    /** Escribe de forma atómica (archivo temporal + move). */
    public synchronized void saveAll(Collection<Book> books) throws IOException {
        byte[] json = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(books);
        Path tmp = dataPath.resolveSibling(dataPath.getFileName() + ".tmp");
        Files.write(tmp, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.move(tmp, dataPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    public Path getDataPath() { return dataPath; }
}
