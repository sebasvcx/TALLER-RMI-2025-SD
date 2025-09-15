package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Book implements Serializable {
    private String isbn;
    private String title;
    private int totalCopies;
    private int lentCopies;

    public Book() {}
    public Book(String isbn, String title, int totalCopies, int lentCopies) {
        this.isbn = isbn; this.title = title;
        this.totalCopies = totalCopies; this.lentCopies = lentCopies;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public int getTotalCopies() { return totalCopies; }
    public int getLentCopies() { return lentCopies; }
    public int getAvailable() { return totalCopies - lentCopies; }
    public void lendOne()   { lentCopies++; }
    public void returnOne() { lentCopies = Math.max(0, lentCopies - 1); }
}
