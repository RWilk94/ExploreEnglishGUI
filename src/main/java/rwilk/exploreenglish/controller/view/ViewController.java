package rwilk.exploreenglish.controller.view;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.entity.Course;
import rwilk.exploreenglish.model.entity.Exercise;
import rwilk.exploreenglish.model.entity.ExerciseItem;
import rwilk.exploreenglish.model.entity.LearnItemChildren;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.LearnItem;
import rwilk.exploreenglish.model.entity.Note;
import rwilk.exploreenglish.model.entity.Sentence;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.service.CourseService;
import rwilk.exploreenglish.service.ExerciseItemService;
import rwilk.exploreenglish.service.ExerciseService;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.LessonService;
import rwilk.exploreenglish.service.NoteService;
import rwilk.exploreenglish.service.SentenceService;
import rwilk.exploreenglish.service.WordService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class ViewController implements Initializable {

  private final InjectService injectService;
  private final CourseService courseService;
  private final LessonService lessonService;
  private final WordService wordService;
  private final NoteService noteService;
  private final ExerciseService exerciseService;
  private final ExerciseItemService exerciseItemService;
  private final SentenceService sentenceService;

  private Course selectedCourse;
  private Lesson selectedLesson;
  private LearnItem selectedLearnItem;
  public ListView<Course> listViewCourses;
  public ListView<Lesson> listViewLessons;
  public ListView<LearnItem> listViewLearnItems;
  public ListView<LearnItemChildren> listViewLearnItemChildren;

  public ViewController(InjectService injectService, CourseService courseService, LessonService lessonService, WordService wordService, NoteService noteService, ExerciseService exerciseService, ExerciseItemService exerciseItemService, SentenceService sentenceService) {
    this.injectService = injectService;
    this.courseService = courseService;
    this.lessonService = lessonService;
    this.wordService = wordService;
    this.noteService = noteService;
    this.exerciseService = exerciseService;
    this.exerciseItemService = exerciseItemService;
    this.sentenceService = sentenceService;
    this.injectService.setViewController(this);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    fillInCoursesListView();
  }

  public void listViewCoursesOnMouseClicked(MouseEvent mouseEvent) {
    selectedCourse = listViewCourses.getSelectionModel().getSelectedItem();
    if (selectedCourse != null) {
      fillInLessonsListView(selectedCourse);
      listViewLearnItems.setItems(null);
      listViewLearnItemChildren.setItems(null);
      injectService.getCourseController().setCourseForm(selectedCourse);
      injectService.getMainController().tabPane.getSelectionModel().select(0);

      injectService.getLessonController().setCourseComboBox(selectedCourse);
    }
  }

  public void listViewLessonsOnMouseClicked(MouseEvent mouseEvent) {
    selectedLesson = listViewLessons.getSelectionModel().getSelectedItem();
    if (selectedLesson != null) {
      fillInLearnItemsListView(selectedLesson);
      listViewLearnItemChildren.setItems(null);
      injectService.getLessonController().setLessonForm(selectedLesson);
      injectService.getMainController().tabPane.getSelectionModel().select(1);

      injectService.getWordController().setLessonComboBox(selectedLesson);
      injectService.getNoteController().setLessonComboBox(selectedLesson);
      injectService.getExerciseController().setLessonComboBox(selectedLesson);
    }
  }

  public void listViewLearnItemsOnMouseClicked(MouseEvent mouseEvent) {
    selectedLearnItem = listViewLearnItems.getSelectionModel().getSelectedItem();
    if (selectedLearnItem != null) {
      if (selectedLearnItem instanceof Word) {
        fillInLessonItemChildrenListView(selectedLearnItem);
        injectService.getWordController().setWordForm((Word)selectedLearnItem);
        injectService.getMainController().tabPane.getSelectionModel().select(2);
        injectService.getSentenceController().setWordComboBox((Word)selectedLearnItem);
      } else if (selectedLearnItem instanceof Exercise) {
        fillInLessonItemChildrenListView(selectedLearnItem);
        injectService.getExerciseController().setExerciseForm((Exercise)selectedLearnItem);
        injectService.getMainController().tabPane.getSelectionModel().select(5);
        injectService.getExerciseItemController().setExerciseComboBox((Exercise)selectedLearnItem);
      } else if (selectedLearnItem instanceof Note) {
        injectService.getNoteController().setNoteForm((Note)selectedLearnItem);
        injectService.getMainController().tabPane.getSelectionModel().select(4);
      }
    }
  }

  public void listViewLearnItemChildrenOnMouseClicked(MouseEvent mouseEvent) {
    LearnItemChildren selectedItem = listViewLearnItemChildren.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
      if (selectedItem instanceof Sentence) {
        injectService.getSentenceController().setSentenceForm((Sentence) selectedItem);
        injectService.getMainController().tabPane.getSelectionModel().select(3);
      } else if (selectedItem instanceof ExerciseItem) {
        injectService.getExerciseItemController().setExerciseForm((ExerciseItem) selectedItem);
        injectService.getMainController().tabPane.getSelectionModel().select(6);
      }
    }
  }

  private void fillInCoursesListView() {
    List<Course> courses = courseService.getAll();
    listViewCourses.setItems(FXCollections.observableArrayList(courses));
    selectedCourse = null;
    selectedLesson = null;
    selectedLearnItem = null;
  }

  private void fillInLessonsListView(Course course) {
    List<Lesson> lessons = lessonService.getAllByCourse(course);
    listViewLessons.setItems(FXCollections.observableArrayList(lessons));
    selectedLesson = null;
    selectedLearnItem = null;
  }

  private void fillInLearnItemsListView(Lesson lesson) {
    List<Note> notes = noteService.getAllByLesson(lesson);
    List<Word> words = wordService.getAllByLesson(lesson);
    List<Exercise> exercises = exerciseService.getAllByLesson(lesson);

    List<LearnItem> learnItems = new ArrayList<>(notes);
    learnItems.addAll(words);
    learnItems.addAll(exercises);

    listViewLearnItems.setItems(FXCollections.observableArrayList(learnItems));
    selectedLearnItem = null;
  }

  private void fillInLessonItemChildrenListView(LearnItem selectedItem) {
    if (selectedItem instanceof Exercise) {
      Exercise exercise = (Exercise) selectedItem;
      List<ExerciseItem> exerciseItems = exerciseItemService.getAllByExercise(exercise);
      List<LearnItemChildren> learnItemChildren = new ArrayList<>(exerciseItems);
      listViewLearnItemChildren.setItems(FXCollections.observableArrayList(learnItemChildren));
    } else if (selectedItem instanceof Word) {
      Word word = (Word) selectedItem;
      List<Sentence> sentences = sentenceService.getAllByWord(word);
      List<LearnItemChildren> learnItemChildren = new ArrayList<>(sentences);
      listViewLearnItemChildren.setItems(FXCollections.observableArrayList(learnItemChildren));
    }
  }

  public void refreshListViewCourses() {
    fillInCoursesListView();
    listViewLessons.setItems(null);
    listViewLearnItems.setItems(null);
    listViewLearnItemChildren.setItems(null);
  }

  public void refreshListViewLessons() {
    if (selectedCourse != null) {
      fillInLessonsListView(selectedCourse);
    } else {
      listViewLessons.setItems(null);
    }
    listViewLearnItems.setItems(null);
    listViewLearnItemChildren.setItems(null);
  }

  public void refreshListViewLessonItems() {
    if (selectedLesson != null) {
      fillInLearnItemsListView(selectedLesson);
    } else {
      listViewLearnItems.setItems(null);
    }
    listViewLearnItemChildren.setItems(null);
  }

  public void refreshListViewLessonItemChildren() {
    if (selectedLearnItem != null) {
      fillInLessonItemChildrenListView(selectedLearnItem);
    } else {
      listViewLearnItemChildren.setItems(null);
    }
  }

}
