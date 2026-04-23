package note;

import java.util.ArrayList;
import mapper.NoteMapper;

public class NoteReminderDTOService {
    NoteService noteService = new NoteService();

    public ArrayList<NoteReminderDTO> getAllNoteReminderDTOs(){
        return NoteMapper.mapToReminderDTOs(noteService.getAllNotes());
    }
}
