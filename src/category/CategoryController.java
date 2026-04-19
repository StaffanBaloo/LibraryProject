package category;

import exceptions.*;

import java.util.ArrayList;
import java.util.Scanner;
import prime.IO;

public class CategoryController {
    CategoryService categoryService = new CategoryService();
    Scanner scanner = new Scanner(System.in);
    public CategoryController() {
    }

    public void showLibrarianMenu(){
        boolean active = true;
        while (active){
            System.out.println("""
                    Kategorimeny:
                    1. Visa alla kategorier.
                    2. Visa detaljerad kategoriinformation.
                    3. Skapa ny kategori.
                    4. Redigera kategori.
                    5. Radera kategori.
                    0. Go back.""");
            int choice = IO.inputNumber();
            switch (choice) {
                case 1 -> showAllCategories();
                case 2 -> showCategoryInfo();
                case 3 -> createCategory();
                case 4 -> editCategory();
                case 5 -> deleteCategory();
                case 0 -> active = false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
    }

    public void showAllCategories(){
        ArrayList<Category> categories = categoryService.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("Det finns inga kategorier.");
        } else {
            System.out.println("ID | Kategorinamn");
            for (Category category : categories) {
                System.out.println(category.getId() + " | " + category.getName());
            }
        }
    }

    public void showCategoryInfo(){
        boolean active = true;
        while (active) {
            System.out.println("Vänligen ange kategori-ID (eller 0 för att gå tillbaka):");
            int id = IO.inputNumber();
            if(id==0) {
                active = false;
            } else if(id<0){
                System.out.println("ID måste vara ett positivt tal.");
            } else {
                if (categoryService.exists(id)) {
                    Category category = categoryService.getCategoryById(id);
                    System.out.println("Namn: " + category.getName());
                    System.out.println("Beskrivning: " + category.getDescription());
                    active=false;
                } else {
                    System.out.println("Det finns ingen kategori med det ID:t.");
                }
            }
        }
    }

    public void editCategory(){
        boolean active = true;
        while(active){
            System.out.println("Vänligen ange kategori-ID (eller 0 för att gå tillbaka):");
            int id = IO.inputNumber();
            if(id==0){
                active = false;
            } else if(id<1){
                System.out.println("ID måste vara ett positivt tal.");
            } else if(!categoryService.exists(id)) {
                System.out.println("Det finns ingen kategori med det ID:t.");
            } else {
                Category category = categoryService.getCategoryById(id);
                boolean active2 = true;
                while(active2) {
                    System.out.println("Vilken information vill du redigera?");
                    System.out.println("1. Namn: " + category.getName());
                    System.out.println("2. Beskrivning:");
                    System.out.println(category.getDescription());
                    System.out.println("9. Avsluta och spara.");
                    System.out.println("0. Avsluta utan att spara.");
                    int choice = IO.inputNumber();
                    switch (choice){
                        case 1 -> category.setName(askForName());
                        case 2 -> category.setDescription(askForDescription());
                        case 9 -> {
                            try {
                                categoryService.save(category);
                                active2 = false;
                                active = false;
                                System.out.println("Category " + category.getName() + " saved.");
                            } catch (CantSaveCategoryException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        case 0 -> {
                            active2 = false;
                            active = false;
                        }
                        default -> System.out.println("Vänligen ange ett giltigt val.");
                    }
                }
            }
        }
    }

    public void createCategory(){
        String name = askForName();
        String description = askForDescription();
        Category category = new Category(name, description);
        try {
            categoryService.addCategory(category);
            System.out.println("Kategorin " + category.getName() + " skapades med ID: " + category.getId());
        } catch (CantCreateCategoryException e) {
            System.out.println("Kunde inte skapa kategorin "+category.getName()+".");
        }
    }

    public void deleteCategory(){
        boolean active = true;
        while(active){
            System.out.println("Vänligen ange kategori-ID (eller 0 för att gå tillbaka):");
            int id = IO.inputNumber();
            if(id==0){
                active = false;
            } else if(id<1){
                System.out.println("ID måste vara ett positivt tal.");
            } else if(!categoryService.exists(id)) {
                System.out.println("Det finns ingen kategori med det ID:t.");
            } else {
                active = false;
                Category category = categoryService.getCategoryById(id);
                System.out.println("Namn: " + category.getName());
                System.out.println("Beskrivning:");
                System.out.println(category.getDescription());
                System.out.println("Skriv \"RADERA\" med versaler för att radera kategorin.");
                String choice = scanner.nextLine().trim();
                if(choice.equals("RADERA")) {
                    try {
                        categoryService.delete(category);
                        System.out.println(category.getName() + " raderad.");
                    } catch (CantDeleteCategoryException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("Kategorin raderades inte.");
                }
            }
        }
    }

    public String askForName(){
        boolean active = true;
        String name = "";
        while (active) {
            System.out.println("Vänligen ange kategorinamn:");
            name = scanner.nextLine().trim();
            if(name.isEmpty()) {
                System.out.println("Namnet får inte vara tomt.");
            } else {
                active=false;
            }
        }
        return name;
    }

    public String askForDescription(){
        boolean active = true;
        String description = "";
        while (active) {
            System.out.println("Vänligen ange kategoribeskrivning:");
            description = scanner.nextLine().trim();
            if(description.isEmpty()) {
                System.out.println("Beskrivningen får inte vara tom.");
            } else {
                active=false;
            }
        }
        return description;
    }
}
