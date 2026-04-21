package book;

import author.Author;
import category.Category;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Book {

    private int bookId;
    private String title;
    private String isbn;
    private int yearPublished;
    private int totalCopies;
    private int availableCopies;
    private ArrayList<Author> authors;
    private ArrayList<Category> categories;
    private String summary;
    private int pageCount;
    private String language;

    public Book(int bookId, String title, String isbn, int yearPublished, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.yearPublished = yearPublished;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public Book(String title, String isbn, int yearPublished, int totalCopies, int availableCopies, String summary, int pageCount, String language) {
        this.title = title;
        this.isbn = isbn;
        this.yearPublished = yearPublished;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.summary = summary;
        this.pageCount = pageCount;
        this.language = language;
    }

    public Book(int bookId, String title, String isbn, int yearPublished, int totalCopies, int availableCopies, ArrayList<Author> authors, ArrayList<Category> categories, String summary, int pageCount, String language) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.yearPublished = yearPublished;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.authors = authors;
        this.categories = categories;
        this.summary = summary;
        this.pageCount = pageCount;
        this.language = language;
    }

    public Book(int bookId, String title, String isbn, int yearPublished, int totalCopies, int availableCopies, String summary, int pageCount, String language) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.yearPublished = yearPublished;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.summary = summary;
        this.pageCount = pageCount;
        this.language = language;
        this.authors = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    public Book() {
        this.bookId = 0;
        this.title = "";
        this.isbn = "";
        this.yearPublished = 0;
        this.totalCopies = 0;
        this.availableCopies = 0;
        this.authors = new ArrayList<Author>();
        this.categories = new ArrayList<Category>();
        this.summary = "";
        this.pageCount = 0;
        this.language = "";
    }

    @Override
    public String toString() {
        String fullBook = "";
        fullBook += "title = '" + title + "'";
        fullBook += " | yearPublished = " + yearPublished;
        fullBook += " | availableCopies = " + availableCopies;
        fullBook += " | author(s) = " + listAuthors();
        fullBook += " | summary = '" + summary + "'";
        fullBook += " | pageCount=" + pageCount;
        fullBook += " | language='" + language;
        return fullBook;
    }

    public String listAuthors(){
        String value;
        if(authors.isEmpty()) {
            value = "(No authors)";
        } else {
            value = authors
                    .stream()
                    .map(Author::getFullName)
                    .collect(Collectors.joining(", "));
        }
        return value;
    }

    public String listCategories(){
        String value;
        if(categories.isEmpty()) {
            value = "(No categories)";
        } else {
            value = categories
                    .stream()
                    .map(Category::getName)
                    .collect(Collectors.joining(", "));
        }
        return value;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(int yearPublished) {
        this.yearPublished = yearPublished;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public void addAuthor(Author author) {
        this.authors.add(author);
    }

    public void clearAuthors(){
        authors.clear();
    }

    public void setSingleAuthor (Author author) {
        authors.clear();
        authors.add(author);
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isAvailable(){
        return (availableCopies>0);
    }

}
