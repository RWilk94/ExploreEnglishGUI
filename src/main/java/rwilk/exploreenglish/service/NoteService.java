package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.Note;
import rwilk.exploreenglish.repository.NoteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

  private final NoteRepository noteRepository;

  public NoteService(NoteRepository noteRepository) {
    this.noteRepository = noteRepository;
  }

  public List<Note> getAll() {
    return noteRepository.findAll();
  }

  public List<Note> getAllByLesson(Lesson lesson) {
    return noteRepository.findAllByLesson(lesson);
  }

  public Optional<Note> getById(Long id) {
    return noteRepository.findById(id);
  }

  public Note save(Note note) {
    return noteRepository.save(note);
  }

  @Transactional
  public void delete(Note note) {
    noteRepository.delete(note);
  }

  @Transactional
  public void deleteByLesson(Lesson lesson) {
    noteRepository.deleteAllByLesson(lesson);
  }

  @Transactional
  public void deleteById(Long id) {
    noteRepository.deleteById(id);
  }
}
