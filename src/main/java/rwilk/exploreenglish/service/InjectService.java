package rwilk.exploreenglish.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import rwilk.exploreenglish.controller.course.CourseController;
import rwilk.exploreenglish.controller.exercise.ExerciseController;
import rwilk.exploreenglish.controller.exerciseitem.ExerciseItemController;
import rwilk.exploreenglish.controller.lesson.LessonController;
import rwilk.exploreenglish.controller.main.MainController;
import rwilk.exploreenglish.controller.note.NoteController;
import rwilk.exploreenglish.controller.sentence.SentenceController;
import rwilk.exploreenglish.controller.view.ViewController;
import rwilk.exploreenglish.controller.word.WordController;

@Getter
@Setter
@Service
public class InjectService {

  private MainController mainController;
  private CourseController courseController;
  private LessonController lessonController;
  private WordController wordController;
  private SentenceController sentenceController;
  private NoteController noteController;
  private ExerciseController exerciseController;
  private ExerciseItemController exerciseItemController;
  private ViewController viewController;

}
