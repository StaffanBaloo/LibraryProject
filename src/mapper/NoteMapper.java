package mapper;

import note.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class NoteMapper {

    public static NoteReminderDTO mapToReminderDTO (Note note){
        return new NoteReminderDTO(note.getNoteId(), note.getMember().getMemberId(), note.getLoan().getId(), note.getType());
    }

    public static ArrayList<NoteReminderDTO> mapToReminderDTOs (ArrayList<Note> notes){
        ArrayList<NoteReminderDTO> noteList;
        noteList = notes.stream()
                .map(NoteMapper::mapToReminderDTO)
                .collect(Collectors.toCollection(ArrayList::new));
        return noteList;
    }
}
