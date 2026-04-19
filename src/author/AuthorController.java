package author;

import prime.IO;
import book.BookListDTO;
import exceptions.*;
import org.apache.commons.validator.routines.UrlValidator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class AuthorController {
    AuthorService authorService = new AuthorService();
    Scanner scanner = new Scanner(System.in);

    public AuthorController() {

    }

    public void showLibrarianMenu() {
        boolean active = true;

        while (active) {
            System.out.println("""
                    Författarmeny
                    Vad önskas?
                    1. Lägg till en författare.
                    2. Visa alla författare.
                    3. Sök författare med namn.
                    4. Sök författare med nationalitet.
                    5. Visa författarinformation.
                    6. Hitta böcker av en författare.
                    7. Redigera en författare.
                    8. Ta bort en författare.
                    0. Gå tillbaka.""");
            int choice = IO.inputNumber();
            switch (choice) {
                case 1 -> addAuthor();
                case 2 -> showAuthors();
                case 3 -> findByName();
                case 4 -> findByNationality();
                case 5 -> displayAuthor();
                case 6 -> findBooksByAuthor();
                case 7 -> editAuthor();
                case 8 -> deleteAuthor();
                case 0 -> active = false;
                default -> System.out.println("Vänligen gör ett giltigt val.");
            }
        }
    }

    public void addAuthor() {
        String firstName = askForFirstName();
        String lastName = askForLastName();
        String nationality = askForNationality();
        LocalDate birthdate = askForBirthDate();
        String biography = askForBiography();
        String website = askForWebsite();
        Author author = new Author(firstName, lastName, nationality, birthdate, biography, website);
        try {
            authorService.addAuthor(author);
            System.out.println("Författare "+author.getFullName()+" skapad med ID " + author.getId());
        } catch (CantCreateAuthorException e) {
            System.out.println("Kunde inte skapa författare "+author.getFullName()+".");
        }
    }

    public void editAuthor(){
        boolean active = true;
        while(active){
            System.out.println("Vänligen ange författar-ID (0 för att gå tillbaka):");
            int id = IO.inputNumber();
            if(id==0){
                active = false;
            } else if(id<1){
                System.out.println("ID måste vara ett positivt heltal.");
            } else if(!authorService.exists(id)) {
                System.out.println("Det finns ingen författare med det ID:t.");
            } else {
                Author author = authorService.getAuthorById(id);
                boolean active2 = true;
                while(active2) {
                    System.out.println("Vilken information vill du redigera?");
                    System.out.println("1. Förnamn: " + author.getFirstName());
                    System.out.println("2. Efternamn: " + author.getLastName());
                    System.out.println("3. Nationalitet: " + author.getNationality());
                    System.out.println("4. Födelsedatum: " + author.getBirthDate());
                    System.out.println("5. Biografi: " + author.getBiography());
                    System.out.println("6. Webbsida: " + author.getWebsite());
                    System.out.println("9. Spara och gå tillbaka.");
                    System.out.println("0. Gå tillbaka utan att spara.");
                    int choice = IO.inputNumber();
                    switch (choice){
                        case 1 -> author.setFirstName(askForFirstName());
                        case 2 -> author.setLastName(askForLastName());
                        case 3 -> author.setNationality(askForNationality());
                        case 4 -> author.setBirthDate(askForBirthDate());
                        case 5 -> author.setBiography(askForBiography());
                        case 6 -> author.setWebsite(askForWebsite());
                        case 9 -> {
                            try {
                                authorService.save(author);
                                active2 = false;
                                active = false;
                                System.out.println("Sparat författare " + author.getFullName() + ".");
                            } catch (CantSaveAuthorException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        case 0 -> {
                            active2 = false;
                            active = false;
                        }
                    }
                }
            }
        }
    }

    public void displayAuthor(){
        boolean active = true;
        while(active){
            System.out.println("Vänligen ange författar-ID (0 för att gå tillbaka):");
            int id = IO.inputNumber();
            if(id==0){
                active = false;
            } else if(id<1){
                System.out.println("ID måste vara ett positivt heltal.");
            } else if(!authorService.exists(id)) {
                System.out.println("Det finns ingen författare med det ID:t.");
            } else {
                active = false;
                Author author = authorService.getAuthorById(id);
                System.out.println("Förnamn: " + author.getFirstName());
                System.out.println("Efternamn: " + author.getLastName());
                System.out.println("Nationalitet: " + author.getNationality());
                System.out.println("Födelsedatum: " + author.getBirthDate());
                System.out.println("Biografi: " + author.getBiography());
                System.out.println("Webbsida: " + author.getWebsite());
            }
        }
    }

    public void showAuthors() {
        ArrayList<AuthorListDTO> authors = authorService.getAllAuthorListDTOs();
        System.out.println("ID | Namn");
        for (AuthorListDTO author : authors) {
            System.out.println(author.getId() + " | " + author.getFullName());
        }
    }

    public void findByName(){
        boolean active = true;
        while (active) {
            System.out.println("Vänligen ange en del av författarens namn:");
            String name = scanner.nextLine().trim();
            if(name.isEmpty()) {
                System.out.println("Namnet kan inte vara tomt.");
            } else {
                active = false;
                ArrayList<AuthorListDTO> authors = authorService.getAuthorListDTOsByPartialName(name);
                if(!authors.isEmpty()) {
                    System.out.println("ID | Namn");
                    for (AuthorListDTO author : authors) {
                        System.out.println(author.getId() + " | " + author.getFullName());
                    }
                } else {
                    System.out.println("Kunde inte hitta några matchande författare.");
                }
            }
        }
    }

    public void findByNationality(){
        boolean active = true;
        while (active) {
            System.out.println("Vänligen ange en del av författarens nationalitet:");
            String nationality = scanner.nextLine().trim();
            if(nationality.isEmpty()) {
                System.out.println("Nationalitet kan inte vara tomt.");
            } else {
                active = false;
                ArrayList<AuthorListDTO> authors = authorService.getAuthorListDTOsByPartialNationality(nationality);
                if(!authors.isEmpty()) {
                    System.out.println("ID | Namn");
                    for (AuthorListDTO author : authors) {
                        System.out.println(author.getId() + " | " + author.getFullName());
                    }
                } else {
                    System.out.println("Kunde inte hitta några matchande författare.");
                }
            }
        }
    }

    public void findBooksByAuthor(){
        boolean active = true;
        ArrayList<BookListDTO> books;
        while(active){
            System.out.println("Vänligen ange författar-ID (0 för att gå tillbaka):");
            int id = IO.inputNumber();
            if(id==0) {
                active = false;
            }
            else if(authorService.exists(id)) {
                active = false;
                books = authorService.getBooksByAuthorId(id);
                if(!books.isEmpty()) {
                    System.out.println("ID | Titel");
                    for (BookListDTO book : books){
                        System.out.println(book.getBookId() + " | " + book.getTitle());
                    }
                } else {
                    System.out.println("Kunde inte hitta några böcker av den författaren.");
                }
            } else {
                System.out.println("Det finns ingen författare med det ID:t.");
            }
        }
    }

    public void deleteAuthor(){
        boolean active = true;
        while(active){
            System.out.println("Vänligen ange författar-ID (0 för att gå tillbaka):");
            int id = IO.inputNumber();
            if(id==0){
                active = false;
            } else if(id<1){
                System.out.println("ID måste vara ett positivt heltal.");
            } else if(!authorService.exists(id)) {
                System.out.println("Det finns ingen författare med det ID:t.");
            } else {
                active = false;
                Author author = authorService.getAuthorById(id);
                System.out.println("Förnamn: " + author.getFirstName());
                System.out.println("Efternamn: " + author.getLastName());
                System.out.println("Nationalitet: " + author.getNationality());
                System.out.println("Födelsedatum: " + author.getBirthDate());
                System.out.println("Biografi: " + author.getBiography());
                System.out.println("Webbsida: " + author.getWebsite());
                System.out.println("Skriv \"RADERA\" med versaler för att radera författaren.");
                String choice = scanner.nextLine().trim();
                if(choice.equals("RADERA")) {
                    try {
                        authorService.delete(author);
                        System.out.println(author.getFullName() + " raderad.");
                    } catch (CantDeleteAuthorException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("Författaren ej raderad.");
                }
            }
        }
    }

    public String askForFirstName(){
        boolean active = true;
        String firstName = "";
        while (active) {
            System.out.println("Vänligen ange författarens förnamn.");
            firstName = scanner.nextLine().trim();
            if(firstName.length()>0) {
                active=false;
            } else {
                System.out.println("Kan inte ta emot ett tomt namn.");
            }
        }
        return firstName;
    }

    public String askForLastName(){
        boolean active = true;
        String lastName = "";
        while (active) {
            System.out.println("Vänligen ange författarens efternamn.");
            lastName = scanner.nextLine().trim();
            if(lastName.length()>0) {
                active=false;
            } else {
                System.out.println("Kan inte ta emot ett tomt namn.");
            }
        }
        return lastName;
    }

    public String askForNationality(){
        boolean active = true;
        String nationality = "";
        while (active) {
            System.out.println("Vänligen ange författarens nationalitet.");
            nationality = scanner.nextLine().trim();
            if(nationality.length()>0) {
                active=false;
            } else {
                System.out.println("Kan inte ta emot en tom nationalitet.");
            }
        }
        return nationality;
    }

    public String askForBiography(){
        String biography;
        System.out.println("Vänligen skriv in författarens biografi.");
        biography = scanner.nextLine().trim();
        return biography;
    }

    public String askForWebsite(){
        UrlValidator urlValidator = new UrlValidator();
        boolean active = true;
        String website = "";
        while (active) {
            System.out.println("Vänligen ange författarens webbsida.");
            website = scanner.nextLine().trim();
            if (!website.isEmpty()) {
                if (!urlValidator.isValid(website)) {
                    System.out.println("Vänligen ange en giltig webbadress.");
                } else {
                    active = false;
                }
            }
        }
        return website;
    }

    public LocalDate askForBirthDate(){
        boolean active = true;
        LocalDate birthdate = null;
        while(active) {
            System.out.println("Vänligen ange författarens födelsedatum (ÅÅÅÅ-MM-DD):");
            String dateString = scanner.nextLine().trim();
            try {
                birthdate = LocalDate.parse(dateString);
                active = false;
            } catch (DateTimeParseException e) {
                System.out.println("Vänligen ange ett giltigt datum.");
            }
        }
        return birthdate;
    }

}