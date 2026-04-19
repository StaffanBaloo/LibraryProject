package note;

import loan.Loan;
import member.Member;
import java.util.ArrayList;


public class NoteService {
    NoteRepository noteRepository = new NoteRepository();

    public Note getById(int noteId){
        return noteRepository.getNoteById(noteId);
    }

    public void sendNote(Member member, Loan loan, String type){
        Note note = new Note(member, loan, type);
        noteRepository.createNote(note);
    }

    public int getNumberUnreadNotesByMember(Member member){
        return noteRepository.getNumberUnreadNotesByMember(member);

    }

    public int getNumberNotesByMember(Member member){
        return noteRepository.getNumberNotesByMember(member);
    }

    public void markRead(Note note){
        noteRepository.markRead(note);
    }

    public void markUnread(Note note){
        noteRepository.markUnread(note);
    }

    public Note getNote(int noteId){
        return noteRepository.getNoteById(noteId);
    }

    public Note getOldestUnreadByMember(Member member){
        return noteRepository.getOldestUnreadByMember(member);
    }

    public ArrayList<Note> getNotesByMember(Member member) {
        return noteRepository.getNotesByMember(member);
    }

    public ArrayList<Note> getUnreadNotesByMember(Member member) {
        ArrayList<Note> notes = noteRepository.getNotesByMember(member);
        ArrayList<Note> newnotes = new ArrayList<>();
        for(Note note: notes){
            if(note.isUnread()){
                newnotes.add(note);
            }
        }
        return newnotes;
    }

}
