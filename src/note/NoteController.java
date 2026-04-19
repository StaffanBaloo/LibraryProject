package note;

import member.Member;
import prime.IO;
import prime.Main;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class NoteController {
    NoteService noteService = new NoteService();
    Scanner scanner = new Scanner(System.in);

    public NoteController() {

    }

    public void showMenu() {
        boolean active = true;
        int unreadNotes;
        while (active){
            System.out.println("Meddelandemeny");
            unreadNotes = noteService.getNumberUnreadNotesByMember(Main.loggedInUser);
            if(unreadNotes>0){
                System.out.println("Du har "+unreadNotes+ " olästa meddelanden.");
            }
            System.out.println("""
                    1. Läs det äldsta olästa meddelandet.
                    2. Läs ett specifikt meddelande.
                    3. Visa olästa meddelanden.
                    4. Visa alla meddelanden.
                    0. Gå tillbaka.""");
            int choice=Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> readOldestUnread(Main.loggedInUser);
                case 2 -> read(Main.loggedInUser);
                case 3 -> listUnread(Main.loggedInUser);
                case 4 -> listAll(Main.loggedInUser);
                case 0 -> active=false;
                default -> System.out.println("Vänligen ange ett giltigt val.");
            }
        }
    }

    void readOldestUnread(Member member){
        if(noteService.getNumberUnreadNotesByMember(member)==0){
            System.out.println("Du har inga olästa meddelanden.");
        } else {
            Note note = noteService.getOldestUnreadByMember(member);
            System.out.println("Meddelandetyp: " + note.getType() + ".");
            System.out.println("Skickat: " + note.getSentDate() + ".");
            if (!Objects.isNull(note.getLoan())) {
                System.out.println("Gäller:");
                System.out.println(note.getLoan().toString());
            }
            System.out.println("Meddelande:");
            System.out.println(note.getMessage());
            noteService.markRead(note);
        }
    }

    void read(Member member){
        boolean active = true;
        if(noteService.getNumberNotesByMember(member)>0) {
            while (active) {
                System.out.println("Vänligen ange meddelande-ID (eller 0 för att gå tillbaka)");
                int noteId = IO.inputNumber();
                if (noteId > 0) {
                    Note note = noteService.getNote(noteId);
                    if (note.getNoteId() == 0) {
                        System.out.println("Kunde inte hitta meddelande " + noteId + ".");
                    } else {
                        if (note.getMember().getMemberId() == member.getMemberId()) {
                            System.out.println("Meddelandetyp: " + note.getType() + ".");
                            System.out.println("Skickat: " + note.getSentDate() + ".");
                            if (!Objects.isNull(note.getLoan())) {
                                System.out.println("Gäller:");
                                System.out.println(note.getLoan().toString());
                            }
                            System.out.println("Meddelande:");
                            System.out.println(note.getMessage());
                            noteService.markRead(note);
                            active = false;
                        } else {
                            System.out.println("Det meddelandet är inte till dig.");
                        }
                    }
                } else active = false;
            }
        } else System.out.println("Du har inga meddelanden.");
    }

    void listUnread(Member member){
        if(noteService.getNumberUnreadNotesByMember(member)>0){
            System.out.println("Du har följande olästa meddelanden:");
            ArrayList<Note> notes = noteService.getUnreadNotesByMember(member);
            for (Note note: notes) {
                System.out.println(note.toString());
            }
        }else {
            System.out.println("Du har inga olästa meddelanden.");
        }
    }

    void listAll(Member member){
        if(noteService.getNumberNotesByMember(member)>0){
            System.out.println("Du har följande meddelanden:");
            ArrayList<Note> notes = noteService.getNotesByMember(member);
            for (Note note: notes) {
                System.out.println(note.toString());
            }
        }else {
            System.out.println("Du har inga meddelanden.");
        }
    }
}
