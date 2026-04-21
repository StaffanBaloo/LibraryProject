package book;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import prime.IO;
import prime.Main;
import author.*;
import category.*;
import loan.*;
import exceptions.*;

public class BookController {
    BookService bookService = new BookService();
    BookListDTOService bookListDTOService = new BookListDTOService();
    LoanService loanService = new LoanService();
    AuthorService authorService = new AuthorService();
    CategoryService categoryService = new CategoryService();
    Scanner scanner = new Scanner(System.in);

    public BookController() {

    }

    public void showMenu(){
        boolean active = true;

        while (active) {
            System.out.println("""
                    Bokmeny:
                    1. Visa alla böcker.
                    2. Visa alla tillgängliga böcker.
                    3. Sök böcker på författare.
                    4. Sök böcker på titel.
                    5. Sök böcker på kategori.
                    6. Sök böcker på nyckelord.
                    7. Visa top 10 senaste året.
                    8. Visa detaljerad bokinformation på ID.
                    9. Låna bok.
                    10. Visa alla kategorier.
                    0. Gå tillbaka.""");
            int choice=IO.inputNumber();
            switch (choice) {
                case 1 -> showAllBooks();
                case 2 -> showAvailableBooks();
                case 3 -> findByAuthor();
                case 4 -> findByTitle();
                case 5 -> findByCategory();
                case 6 -> findByKeyword();
                case 7 -> {
                    LoanController loanController = new LoanController();
                    loanController.showTopList(10, LocalDate.now().minusYears(1), LocalDate.now());
                }
                case 8 -> showDetailedInfo();
                case 9 -> borrowBook();
                case 10 -> {
                    var categoryController = new CategoryController();
                    categoryController.showAllCategories();
                }
                case 0 -> active = false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
    }

    public void showLibrarianMenu() {
        boolean active = true;
        int choice;
        while (active) {
            System.out.println("""
                    Välkommen, biblotekarie!
                    1. Visa alla böcker.
                    2. Visa alla tillgängliga böcker.
                    3. Sök böcker på författare.
                    4. Sök böcker på titel.
                    5. Sök böcker på kategori.
                    6. Sök böcker på nyckelord.
                    7. Visa detaljerad bokinformation på ID.
                    8. Lägg till bok.
                    9. Redigera bok.
                    10. Ta bort bok.
                    11. Visa alla kategorier.
                    0. Gå tillbaka.""");
            choice = IO.inputNumber();
            switch (choice) {
                case 1 -> showAllBooks();
                case 2 -> showAvailableBooks();
                case 3 -> findByAuthor();
                case 4 -> findByTitle();
                case 5 -> findByCategory();
                case 6 -> findByKeyword();
                case 7 -> showDetailedInfo();
                case 8 -> addBook();
                case 9 -> editBook();
                case 10 -> deleteBook();
                case 11 -> {
                    var categoryController = new CategoryController();
                    categoryController.showAllCategories();
                }
                case 0 -> active = false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
    }

    public void showAllBooks(){
        ArrayList<BookListDTO> bookDTOs = bookListDTOService.getAllBooksList();
        System.out.println("ID | Titel | Författare | Tillgängliga exemplar");
        for (BookListDTO bookDTO : bookDTOs) {
            System.out.println(bookDTO.toString());
        }
    }

    public void showAvailableBooks(){
        ArrayList<BookListDTO> bookDTOs = bookListDTOService.getAvailableBooksList();
        System.out.println("ID | Titel | Författare | Tillgängliga exemplar");
        for (BookListDTO bookDTO : bookDTOs) {
            System.out.println(bookDTO.toString());
        }
    }

    public void findByAuthor(){
        ArrayList<BookListDTO> bookDTOs;
        System.out.println("Vänligen ange författare:");
        String searchTerm = scanner.nextLine().trim();
        if (IO.isNumeric(searchTerm)){
            bookDTOs = bookListDTOService.getBookListByAuthorId(Integer.parseInt(searchTerm));
        } else {
            bookDTOs = bookListDTOService.getBookListByAuthorName(searchTerm);
        }
        if(bookDTOs.isEmpty()){
            System.out.println("Hittade inga böcker.");
        } else {
            System.out.println("ID | Titel | Författare | Tillgängliga exemplar");
            for (BookListDTO bookDTO : bookDTOs) {
                System.out.println(bookDTO.toString());
            }
        }
    }

    public void findByTitle(){
        System.out.println("Vänligen ange titel:");
        String searchTerm = scanner.nextLine();
        ArrayList<BookListDTO> bookDTOs = bookListDTOService.getBookListByTitle(searchTerm);
        if(bookDTOs.isEmpty()){
            System.out.println("Hittade inga böcker.");
        } else {
            System.out.println("ID | Titel | Författare | Tillgängliga exemplar");
            for (BookListDTO bookDTO : bookDTOs) {
                System.out.println(bookDTO.toString());
            }
        }
    }

    public void findByCategory(){
        ArrayList<BookListDTO> bookDTOs;
        System.out.println("Vänligen ange kategori:");
        String searchTerm = scanner.nextLine();
        if (IO.isNumeric(searchTerm)){
            bookDTOs = bookListDTOService.getBookListByCategoryId(Integer.parseInt(searchTerm));
        } else {
            bookDTOs = bookListDTOService.getBookListByCategory(searchTerm);
        }
        if(bookDTOs.isEmpty()){
            System.out.println("Hittade inga böcker.");
        } else {
            System.out.println("ID | Titel | Författare | Tillgängliga exemplar");
            for (BookListDTO bookDTO : bookDTOs) {
                System.out.println(bookDTO.toString());
            }
        }
    }

    public void findByKeyword(){
        System.out.println("Vänligen ange nyckelord:");
        String searchTerm = scanner.nextLine();
        ArrayList<BookListDTO> bookDTOs = bookListDTOService.getBookListByKeyword(searchTerm);
        if(bookDTOs.isEmpty()){
            System.out.println("Hittade inga böcker.");
        } else {
            System.out.println("ID | Titel | Författare | Tillgängliga exemplar");
            for (BookListDTO bookDTO : bookDTOs) {
                System.out.println(bookDTO.toString());
            }
        }
    }

    public void showDetailedInfo(){
        System.out.println("Vänligen ange bok-ID:");
        int searchId = IO.inputNumber();
        if(bookService.exists(searchId)) {
            Book book = bookService.getBookById(searchId);
            System.out.println("Bok-ID: "+book.getBookId());
            System.out.println("Titel: "+book.getTitle());
            System.out.println("Författare: "+book.listAuthors());
            System.out.println("Kategorier: " + book.listCategories());
            System.out.println("Sammanfattning: " + book.getSummary());
            System.out.println("ISBN: "+book.getIsbn());
            System.out.println("Publikationsår: " + book.getYearPublished());
            System.out.println("Språk: " + book.getLanguage());
            System.out.println("Sidantal: " + book.getPageCount());
            System.out.println("Exemplar: " + book.getAvailableCopies() + "/" +book.getTotalCopies());
        }
        else {
            System.out.println("Hittade inte bok med ID " + searchId + ".");
        }
    }

    public void borrowBook(){
        System.out.println("Vänligen ange bok-ID:");
        int bookId = Integer.parseInt(scanner.nextLine().trim());
        try {
            Book book = bookService.getBookById(bookId);
            loanService.createLoan(book, Main.loggedInUser);
        }
        catch (CantCreateLoanException e) {
            System.out.println("Du kan inte låna bok "+ bookId + ".");
            System.out.println(e.getMessage());
        }
    }

    public void deleteBook(){
        System.out.println("Vänligen ange bok-ID:");
        int searchId = IO.inputNumber();
        if(bookService.exists(searchId)) {
            Book book = bookService.getBookById(searchId);
            System.out.println("Bok-ID: "+book.getBookId());
            System.out.println("Titel: "+book.getTitle());
            System.out.println("Författare: "+book.listAuthors());
            System.out.println("Kategorier: " + book.listCategories());
            System.out.println("Sammanfattning: " + book.getSummary());
            System.out.println("ISBN: "+book.getIsbn());
            System.out.println("Publikationsår: " + book.getYearPublished());
            System.out.println("Språk: " + book.getLanguage());
            System.out.println("Sidantal: " + book.getPageCount());
            System.out.println("Exemplar: " + book.getAvailableCopies() + "/" +book.getTotalCopies());
            System.out.println("Skriv \"RADERA\" med versaler för att radera författaren.");
            String choice = scanner.nextLine().trim();
            if(choice.equals("RADERA")) {
                try {
                    bookService.remove(book);
                    System.out.println(book.getTitle() + " raderad.");
                } catch (CantRemoveBookException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Författaren ej raderad.");
            }
        }
        else {
            System.out.println("Hittade inte författare med ID " + searchId + ".");
        }
    }

    public void addBook() {
        String title, isbn, summary, language;
        int yearPublished, copies, pageCount;
        ArrayList<Integer> authorIdList;
        ArrayList<Integer> categoryIdList;
        title = askForTitle();
        isbn = askForISBN();
        yearPublished = askForYearPublished();
        copies = askForCopies();
        summary = askForSummary();
        pageCount = askForPageCount();
        language = askForLanguage();

        authorIdList=createAuthorIdList();
        categoryIdList = createCategoryIdList();
        Book book = new Book(title, isbn, yearPublished, copies, copies, summary, pageCount, language);
        try {
            bookService.addBook(book, authorIdList, categoryIdList);
            System.out.println("Boken " +  book.getTitle() + " skapad med ID "+book.getBookId()+".");
        } catch (CantCreateBookException e) {
            System.out.println(e.getMessage());
        }

    }

    public void editBook(){
        boolean active = true;
        String title, isbn, summary, language;
        int yearPublished, copies, pageCount;
        ArrayList<Author> authors;
        ArrayList<Category> categories;
        Book book = null;
        while (active) {
            System.out.println("Vänligen ange bok-ID:");
            int id = IO.inputNumber();
            if (bookService.exists(id)) {
                book = bookService.getBookById(id);
                active = false;
            } else {
                System.out.println("Det finns ingen bok med det ID:t.");
            }
        }
        active = true;
        while (active) {
            System.out.println("What do you wish to edit?");
            System.out.println("1. Titel: " + book.getTitle());
            System.out.println("2. ISBN: " + book.getIsbn());
            System.out.println("3. Publikationsår: " + book.getYearPublished());
            System.out.println("4. Antal exemplar: " + book.getTotalCopies());
            System.out.println("5. Sammanfattning: " + book.getSummary());
            System.out.println("6. Sidantal: " + book.getPageCount());
            System.out.println("7. Språk: " + book.getLanguage());
            System.out.println("8. Författare: " +book.listAuthors() + " (this will save the changes immediately)");
            System.out.println("9. Kategorier: "+book.listCategories() + " (this will save the changes immediately)");
            System.out.println("10. Spara och gå tillbaka.");
            System.out.println("0. Gå tillbaka utan att spara.");
            int choice = IO.inputNumber();
            switch (choice){
                case 1 ->{
                    title = askForTitle();
                    book.setTitle(title);
                }
                case 2 ->{
                    isbn = askForISBN();
                    book.setIsbn(isbn);
                }
                case 3 ->{
                    yearPublished = askForYearPublished();
                    book.setYearPublished(yearPublished);
                }
                case 4 ->{
                    copies = askForCopies();
                    int copiesChange = copies-book.getTotalCopies();
                    book.setTotalCopies(copies);
                    //Change available copies by the same amount as total copies, but with a minimum of 0.
                    book.setAvailableCopies(Math.max(0, book.getAvailableCopies()+copiesChange));
                    System.out.println("Det kommer nu att finnas "+book.getAvailableCopies()+ " exemplar tillgängliga av "+book.getTotalCopies()+".");
                }
                case 5 ->{
                    summary = askForSummary();
                    book.setSummary(summary);
                }
                case 6 ->{
                    pageCount = askForPageCount();
                    book.setPageCount(pageCount);
                }
                case 7 ->{
                    language = askForLanguage();
                    book.setLanguage(language);
                }
                case 8 -> {
                    authors = askForAuthors(book.getAuthors());
                    book.setAuthors(authors);
                }
                case 9 -> {
                    categories = askForCategories(book.getCategories());
                    book.setCategories(categories);
                }
                case 10 ->{
                    try {
                        bookService.save(book);
                        bookService.saveAuthors(book);
                        bookService.saveCategories(book);
                    } catch (CantCreateBookException e) {
                        System.out.println(e.getMessage());
                    }
                    active = false;
                }
                case 0 -> active = false;
            }
        }
    }

    public ArrayList<Author> askForAuthors(ArrayList<Author> authors){
        boolean active = true;
        ArrayList<Author> newAuthors = (ArrayList<Author>) authors.clone();
        while(active){
            if(newAuthors.isEmpty()) {
                System.out.println("Boken har för närvarande inga författare.");
            } else {
                System.out.println("Nuvarande författare:");
                for (Author author : newAuthors) {
                    System.out.println(author.getId() + " | " + author.getFullName());
                }
            }
            System.out.println("""
                1. Lägg till författare med författar-ID.
                2. Ta bort författare med författar-ID.
                3. Visa alla författare.
                4. Sök författare på partiellt namn.
                0. Avsluta redigering av författare.""");
            int choice = IO.inputNumber();
            switch (choice){
                case 1 -> {
                    System.out.println("Vänligen ange författar-ID:");
                    int id = IO.inputNumber();
                    if (authorService.exists(id)) {
                        if(!existsInAuthorList(newAuthors,id)){
                            newAuthors.add(authorService.getAuthorById(id));
                        } else {
                            System.out.println("Den författaren finns redan i listan.");
                        }
                    } else {
                        System.out.println("Det finns ingen författare med det ID:t.");
                    }
                }

                case 2 -> {
                    System.out.println("Vänligen ange författar-ID:");
                    int id = IO.inputNumber();
                    if (authorService.exists(id)) {
                        removeFromAuthorList(newAuthors, id);
                    } else {
                        System.out.println("Det finns ingen författare med det ID:t.");
                    }
                }

                case 3 -> {
                    ArrayList<AuthorListDTO> authorlist = authorService.getAllAuthorListDTOs();
                    System.out.println("ID | Namn");
                    for (AuthorListDTO author : authorlist) {
                        System.out.println(author.toString());
                    }
                }
                case 4 -> {
                    System.out.println("Vänligen ange del av namnet:");
                    String name = scanner.nextLine().trim();
                    ArrayList<AuthorListDTO> authorlist = authorService.getAuthorListDTOsByPartialName(name);
                    if(authorlist.isEmpty()){
                        System.out.println("Det finns ingen författare med det ID:t.");
                    } else {
                        System.out.println("ID | Namn");
                        for (AuthorListDTO author : authorlist) {
                            System.out.println(author.toString());
                        }
                    }
                }
                case 0 -> active=false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
        return newAuthors;
    }

    public ArrayList<Category> askForCategories(ArrayList<Category> categories){
        boolean active = true;
        ArrayList<Category> newCategories = (ArrayList<Category>) categories.clone();
        while(active){
            if(newCategories.isEmpty()) {
                System.out.println("Boken har för närvarande inga kategorier.");
            } else {
                System.out.println("Nuvarande kategorier:");
                for (Category category : newCategories) {
                    System.out.println(category.getId() + " | " + category.getName());
                }
            }
            System.out.println("""
                1. Lägg till kategori med kategori-ID.
                2. Ta bort kategori med kategori-ID.
                3. Visa alla kategorier.
                4. Sök kategorier på partiellt namn.
                0. Avsluta redigering av kategorier.""");
            int choice = IO.inputNumber();
            switch (choice){
                case 1 -> {
                    System.out.println("Vänligen ange kategori-ID:");
                    int id = IO.inputNumber();
                    if (categoryService.exists(id)) {
                        if(!existsInCategoryList(newCategories,id)){
                            newCategories.add(categoryService.getCategoryById(id));
                        } else {
                            System.out.println("Den kategorin finns redan i listan.");
                        }
                    } else {
                        System.out.println("Det finns ingen kategori med det ID:t.");
                    }
                }

                case 2 -> {
                    System.out.println("Vänligen ange kategori-ID:");
                    int id = IO.inputNumber();
                    if (categoryService.exists(id)) {
                        removeFromCategoryList(newCategories, id);
                    } else {
                        System.out.println("Det finns ingen kategori med det ID:t.");
                    }
                }

                case 3 -> {
                    ArrayList<Category> categorylist = categoryService.getAllCategories();
                    System.out.println("ID | Namn");
                    for (Category category : categorylist) {
                        System.out.println(category.getId() + " | " + category.getName());
                    }
                }
                case 4 -> {
                    System.out.println("Vänligen ange en del av namnet:");
                    String name = scanner.nextLine().trim();
                    ArrayList<Category> categorylist = categoryService.getCategoriesByPartialName(name);
                    if(categories.isEmpty()){
                        System.out.println("Det finns ingen kategori med det ID:t.");
                    } else {
                        System.out.println("ID | Namn");
                        for (Category category : categorylist) {
                            System.out.println(category.getId() + " | " + category.getName());
                        }
                    }
                }
                case 0 -> active=false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
        return newCategories;
    }

    public ArrayList<Integer> createAuthorIdList() {
        boolean active=true;
        ArrayList<Integer> authorIdList = new ArrayList<>();
        while (active){
            System.out.println("""
                    Författarlistemeny
                    1. Lägg till författare med ID.
                    2. Visa författarlista.
                    3. Sök författare på del av namnet.
                    0. Avsluta redigering av författare.""");
            int choice = IO.inputNumber();
            switch (choice){
                case 1 -> {
                    System.out.println("Vänligen ange författar-ID:");
                    int id = IO.inputNumber();
                    if (authorService.exists(id)) {
                        authorIdList.add(id);
                    } else {
                        System.out.println("Det finns ingen författare med det ID:t.");
                    }
                }
                case 2 -> {
                    ArrayList<AuthorListDTO> authors = authorService.getAllAuthorListDTOs();
                    System.out.println("ID | Namn");
                    for (AuthorListDTO author : authors) {
                        System.out.println(author.toString());
                    }
                }
                case 3 -> {
                    System.out.println("Vänligen ange en del av namnet:");
                    String name = scanner.nextLine().trim();
                    ArrayList<AuthorListDTO> authors = authorService.getAuthorListDTOsByPartialName(name);
                    if(authors.isEmpty()){
                        System.out.println("Det finns ingen författare med det ID:t.");
                    } else {
                        System.out.println("ID | Namn");
                        for (AuthorListDTO author : authors) {
                            System.out.println(author.toString());
                        }
                    }
                }
                case 0 -> active=false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
        return authorIdList;
    }

    public ArrayList<Integer> createCategoryIdList() {
        boolean active=true;
        ArrayList<Integer> categoryIdList = new ArrayList<>();
        while (active){
            System.out.println("""
                    Kategorilistemeny
                    1. Lägg till kategori med ID.
                    2. Visa kategorilista.
                    3. Sök kategori på del av namnet.
                    0. Avsluta redigering av kategorilista.""");
            int choice = IO.inputNumber();
            switch (choice){
                case 1 -> {
                    System.out.println("Vänligen ange kategori-ID.");
                    int id = IO.inputNumber();
                    if (categoryService.exists(id)) {
                        categoryIdList.add(id);
                    } else {
                        System.out.println("Det finns ingen kategori med det ID:t.");
                    }
                }
                case 2 -> {
                    ArrayList<Category> categories = categoryService.getAllCategories();
                    System.out.println("ID | Kategori | Beskrivning");
                    for (Category category : categories) {
                        System.out.println(category.toString());
                    }
                }
                case 3 -> {
                    System.out.println("Vänligen ange en del av namnet:");
                    String name = scanner.nextLine().trim();
                    ArrayList<Category> categories = categoryService.getCategoriesByPartialName(name);
                    if(categories.isEmpty()){
                        System.out.println("Det finns ingen kategori med det namnet.");
                    } else {
                        System.out.println("ID | Kategori | Beskrivning");
                        for (Category category : categories) {
                            System.out.println(category.toString());
                        }
                    }
                }
                case 0 -> active=false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
        return categoryIdList;
    }

    public String askForTitle(){
        System.out.println("Vänligen ange bokens titel:");
        String title = scanner.nextLine().trim();
        return title;
    }

    public String askForISBN(){
        System.out.println("Vänligen ange ISBN:");
        String isbn = scanner.nextLine().trim();
        // I choose not to validate ISBN because doing that properly would add a lot of work to testing that's beyond the scope of the project.
        return isbn;
    }

    public int askForYearPublished(){
        boolean active = true;
        int yearPublished = 0;
        while(active){
            System.out.println("Vänligen ange publikationsår:");
            yearPublished = IO.inputNumber();
            if(yearPublished> LocalDate.now().getYear()) {
                System.out.println("Publikationsåret måste vara i år eller tidigare.");
            } else {
                active = false;
            }
        }
        return yearPublished;
    }

    public int askForCopies(){
        boolean active = true;
        int copies =0;
        while (active){
            System.out.println("Vänligen ange hur många exemplar biblioteket har:");
            copies = IO.inputNumber();
            if(copies<0) {
                System.out.println("Antalet exemplar kan inte vara negativt.");
            } else {
                active = false;
            }
        }
        return copies;
    }

    public String askForSummary(){
        System.out.println("Vänligen ange en sammanfattning av boken:");
        String summary = scanner.nextLine().trim();
        return summary;
    }

    public int askForPageCount(){
        boolean active = true;
        int pageCount =0;
        while (active){
            System.out.println("Vänligen ange hur många sidor boken har:");
            pageCount = IO.inputNumber();
            if(pageCount<0) {
                System.out.println("Boken kan inte ha ett negativt antal sidor.");
            } else {
                active = false;
            }
        }
        return pageCount;
    }

    public String askForLanguage(){
        boolean active = true;
        String language ="";
        while(active) {
            System.out.println("Vänligen ange bokens språk:");
            language = scanner.nextLine().trim();
            if(language.length()<2){
                System.out.println("Det är för kort.");
            } else {
                language = language.substring(0,1).toUpperCase()+language.substring(1).toLowerCase();
                active=false;
            }
        }
        return language;
    }

    public boolean existsInAuthorList(ArrayList<Author> authors, int authorId){
        for(Author author : authors){
            if(author.getId()==authorId) return true;
        }
        return false;
    }

    public boolean existsInCategoryList(ArrayList<Category> categories, int categoryId){
        for(Category category : categories){
            if(category.getId() == categoryId) return true;
        }
        return false;
    }

    public void removeFromAuthorList(ArrayList<Author> authors, int authorId){
        for(Author author : authors){
            if(author.getId()==authorId) {
                authors.remove(author);
                return;
            }
        }
        System.out.println("Författaren med ID " + authorId + " finns inte i listan.");
    }

    public void removeFromCategoryList(ArrayList<Category> categories, int categoryId) {
        for(Category category : categories){
            if(category.getId()==categoryId){
                categories.remove(category);
                return;
            }
        }
        System.out.println("Kategorin med ID " + categoryId + " finns inte i listan.");
    }

}
