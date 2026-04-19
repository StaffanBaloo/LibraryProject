package fine;

import prime.IO;
import prime.Main;
import member.Member;

import java.util.ArrayList;

public class FineController {
    FineService fineService = new FineService();
    public FineController() {
    }

    public void showMenu(){
        boolean active = true;
        while(active){
            System.out.println("""
                    Bötesmeny:
                    1. Se alla mina obetalda böter.
                    2. Betala en bot.
                    0. Gå tillbaka.""");
            int choice = IO.inputNumber();
            switch (choice) {
                case 1 -> showUnpaidFines(Main.loggedInUser);
                case 2 -> payFine(Main.loggedInUser);
                case 0 -> active = false;
                default -> System.out.println("Vänligen gör ett giltigt val.");
            }
        }
    }

    public void showUnpaidFines(Member member){
        ArrayList<Fine> fines = fineService.getAllUnpaidFinesForMember(member);
        if(fines.isEmpty()) {
            System.out.println("Du har inga obetalda böter.");
        } else {
            System.out.println("ID | Belopp | Titel");
            for (Fine fine : fines) {
                System.out.println(fine.getId() + " | " + fine.getAmount() + " kr | " + fine.getLoan().getBook().getTitle());
            }
        }
    }

    public void payFine(Member member) {
        boolean active = true;
        while(active) {
            System.out.println("Vänligen ange bot-ID (eller 0 för att gå tillbaka):");
            int id = IO.inputNumber();
            if(id==0) {
                active = false;
            } else if (fineService.exists(id)){
                Fine fine = fineService.getFineById(id);
                if(member.getMemberId()==fine.getLoan().getMember().getMemberId()) {
                    fineService.payFine(fine);
                    active = false;
                } else {
                    System.out.println("Den boten hör inte till ett av dina lån.");
                }
            } else {
                System.out.println("Det finns ingen bot med ID "+id+".");
            }
        }
    }
}
