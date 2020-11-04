package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import rwilk.exploreenglish.repository.NoteRepository;

@Service
public class NoteService {

  private final NoteRepository noteRepository;

  public NoteService(NoteRepository noteRepository) {
    this.noteRepository = noteRepository;
  }

}
