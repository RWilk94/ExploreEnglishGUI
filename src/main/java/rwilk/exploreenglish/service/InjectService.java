package rwilk.exploreenglish.service;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;

import rwilk.exploreenglish.controller.course.CourseController;
import rwilk.exploreenglish.controller.exercise.ExerciseController;
import rwilk.exploreenglish.controller.exerciseitem.ExerciseItemController;
import rwilk.exploreenglish.controller.lesson.LessonController;
import rwilk.exploreenglish.controller.main.MainController;
import rwilk.exploreenglish.controller.note.NoteController;
import rwilk.exploreenglish.controller.sentence.SentenceController;
import rwilk.exploreenglish.controller.term.TermDuplicatedTableController;
import rwilk.exploreenglish.controller.term.TermTableController;
import rwilk.exploreenglish.controller.view.ViewController;
import rwilk.exploreenglish.controller.word.WordController;
import rwilk.exploreenglish.controller.word.scrapper.ScrapperController;
import rwilk.exploreenglish.controller.word.scrapper.ScrapperTabController;

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
  private TermTableController termTableController;
  private TermDuplicatedTableController termDuplicatedTableController;
  private ScrapperController scrapperController;
  private ScrapperTabController scrapperTabController;

}
