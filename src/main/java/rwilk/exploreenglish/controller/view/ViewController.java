package rwilk.exploreenglish.controller.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.LearnItem;
import rwilk.exploreenglish.model.LearnItemChildren;
import rwilk.exploreenglish.model.entity.*;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
  private LearnItemChildren selectedLearnItemChildren;
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

    listViewLearnItems.setCellFactory(lv -> new ListCell<LearnItem>() {
      @Override
      protected void updateItem(LearnItem item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
          this.setText(item.toString());
        }
        if (item instanceof Word) {
          Word word = (Word) item;
          if (StringUtils.isNoneEmpty(StringUtils.trimToEmpty(word.getPartOfSpeech())) && StringUtils.isNoneEmpty(StringUtils.trimToEmpty(word.getGrammarType()))) {
            setStyle("-fx-background-color: #11ff00");
          } else {
            setStyle("");
          }
        } else if (item instanceof Note || item instanceof Exercise) {
          setStyle("-fx-background-color: #FFF200");
        } else {
          setStyle("");
        }
      }
    });
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
        injectService.getWordController().setWordForm((Word) selectedLearnItem);
        injectService.getMainController().tabPane.getSelectionModel().select(2);
        injectService.getSentenceController().setWordComboBox((Word) selectedLearnItem);
      } else if (selectedLearnItem instanceof Exercise) {
        fillInLessonItemChildrenListView(selectedLearnItem);
        injectService.getExerciseController().setExerciseForm((Exercise) selectedLearnItem);
        injectService.getMainController().tabPane.getSelectionModel().select(5);
        injectService.getExerciseItemController().setExerciseComboBox((Exercise) selectedLearnItem);
      } else if (selectedLearnItem instanceof Note) {
        listViewLearnItemChildren.setItems(null);
        injectService.getNoteController().setNoteForm((Note) selectedLearnItem);
        injectService.getMainController().tabPane.getSelectionModel().select(4);
      }
    }
  }

  public void listViewLearnItemChildrenOnMouseClicked(MouseEvent mouseEvent) {
    selectedLearnItemChildren = listViewLearnItemChildren.getSelectionModel().getSelectedItem();
    if (selectedLearnItemChildren != null) {
      if (selectedLearnItemChildren instanceof Sentence) {
        injectService.getSentenceController().setSentenceForm((Sentence) selectedLearnItemChildren);
        injectService.getMainController().tabPane.getSelectionModel().select(3);
      } else if (selectedLearnItemChildren instanceof ExerciseItem) {
        injectService.getExerciseItemController().setExerciseForm((ExerciseItem) selectedLearnItemChildren);
        injectService.getMainController().tabPane.getSelectionModel().select(6);
      }
    }
  }

  private void fillInCoursesListView() {
    List<Course> courses = courseService.getAll().stream()
        .sorted(Comparator.comparing(Course::getPosition))
        .collect(Collectors.toList());
    listViewCourses.setItems(FXCollections.observableArrayList(courses));
    selectedCourse = null;
    selectedLesson = null;
    selectedLearnItem = null;
    selectedLearnItemChildren = null;
  }

  private void fillInLessonsListView(Course course) {
    List<Lesson> lessons = lessonService.getAllByCourse(course).stream()
        .sorted(Comparator.comparing(Lesson::getPosition))
        .collect(Collectors.toList());
    listViewLessons.setItems(FXCollections.observableArrayList(lessons));
    selectedLesson = null;
    selectedLearnItem = null;
    selectedLearnItemChildren = null;
  }

  private void fillInLearnItemsListView(Lesson lesson) {
    List<Note> notes = noteService.getAllByLesson(lesson);
    List<Word> words = wordService.getAllByLesson(lesson);
    List<Exercise> exercises = exerciseService.getAllByLesson(lesson);

    List<LearnItem> learnItems = new ArrayList<>(notes);
    learnItems.addAll(words);
    learnItems.addAll(exercises);
    learnItems = learnItems.stream().sorted(Comparator.comparing(LearnItem::getPosition)).collect(Collectors.toList());

    listViewLearnItems.setItems(FXCollections.observableArrayList(learnItems));
    selectedLearnItem = null;
    selectedLearnItemChildren = null;
  }

  private void fillInLessonItemChildrenListView(LearnItem selectedItem) {
    if (selectedItem instanceof Exercise) {
      Exercise exercise = (Exercise) selectedItem;
      List<LearnItemChildren> learnItemChildren = exerciseItemService.getAllByExercise(exercise).stream()
          .sorted(Comparator.comparing(ExerciseItem::getPosition))
          .collect(Collectors.toList());
      listViewLearnItemChildren.setItems(FXCollections.observableArrayList(learnItemChildren));
    } else if (selectedItem instanceof Word) {
      Word word = (Word) selectedItem;
      List<LearnItemChildren> learnItemChildren = sentenceService.getAllByWord(word).stream()
          .sorted(Comparator.comparing(Sentence::getPosition))
          .collect(Collectors.toList());
      listViewLearnItemChildren.setItems(FXCollections.observableArrayList(learnItemChildren));
    }
    selectedLearnItemChildren = null;
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

  public void buttonCoursePositionUpOnAction(ActionEvent actionEvent) {
    if (selectedCourse != null) {
      courseService.getPreviousCourse(selectedCourse.getPosition())
          .ifPresent(previousCourse -> replaceCourses(selectedCourse, previousCourse));
    }
  }

  public void buttonCoursePositionDownOnAction(ActionEvent actionEvent) {
    if (selectedCourse != null) {
      courseService.getNextCourse(selectedCourse.getPosition())
          .ifPresent(previousCourse -> replaceCourses(selectedCourse, previousCourse));
    }
  }

  public void buttonLessonPositionUpOnAction(ActionEvent actionEvent) {
    if (selectedLesson != null) {
      lessonService.getPreviousLesson(selectedLesson.getCourse().getId(), selectedLesson.getPosition())
          .ifPresent(previousLesson -> replaceLessons(selectedLesson, previousLesson));
    }
  }

  public void buttonLessonPositionDownOnAction(ActionEvent actionEvent) {
    if (selectedLesson != null) {
      lessonService.getNextLesson(selectedLesson.getCourse().getId(), selectedLesson.getPosition())
          .ifPresent(previousLesson -> replaceLessons(selectedLesson, previousLesson));
    }
  }

  public void buttonLearnItemsPositionUpOnAction(ActionEvent actionEvent) {
    if (selectedLearnItem != null) {
      HashMap<Integer, LearnItem> itemHashMap = new HashMap<>();

      wordService.getPreviousWord(selectedLearnItem.getLesson().getId(), selectedLearnItem.getPosition())
          .ifPresent(previousWord -> itemHashMap.put(previousWord.getPosition(), previousWord));
      noteService.getPreviousNote(selectedLearnItem.getLesson().getId(), selectedLearnItem.getPosition())
          .ifPresent(previousNote -> itemHashMap.put(previousNote.getPosition(), previousNote));
      exerciseService.getPreviousExercise(selectedLearnItem.getLesson().getId(), selectedLearnItem.getPosition())
          .ifPresent(previousExercise -> itemHashMap.put(previousExercise.getPosition(), previousExercise));

      if (!itemHashMap.isEmpty()) {
        int currentPosition = selectedLearnItem.getPosition();
        LearnItem previousLearnItem = itemHashMap.values().stream()
            .sorted(Comparator.comparing(LearnItem::getPosition).reversed())
            .collect(Collectors.toList()).get(0);
        selectedLearnItem.setPosition(previousLearnItem.getPosition());
        previousLearnItem.setPosition(currentPosition);
        LearnItem currentLearnItem = saveLearnItem(selectedLearnItem);
        saveLearnItem(previousLearnItem);
        refreshListViewLessonItems();
        selectedLearnItem = currentLearnItem;
      }
    }

  }

  public void buttonLearnItemsPositionDownOnAction(ActionEvent actionEvent) {
    if (selectedLearnItem != null) {
      HashMap<Integer, LearnItem> itemHashMap = new HashMap<>();

      wordService.getNextWord(selectedLearnItem.getLesson().getId(), selectedLearnItem.getPosition())
          .ifPresent(previousWord -> itemHashMap.put(previousWord.getPosition(), previousWord));
      noteService.getNextNote(selectedLearnItem.getLesson().getId(), selectedLearnItem.getPosition())
          .ifPresent(previousNote -> itemHashMap.put(previousNote.getPosition(), previousNote));
      exerciseService.getNextExercise(selectedLearnItem.getLesson().getId(), selectedLearnItem.getPosition())
          .ifPresent(previousExercise -> itemHashMap.put(previousExercise.getPosition(), previousExercise));

      if (!itemHashMap.isEmpty()) {
        int currentPosition = selectedLearnItem.getPosition();
        LearnItem nextLearnItem = itemHashMap.values().stream()
            .sorted(Comparator.comparing(LearnItem::getPosition))
            .collect(Collectors.toList()).get(0);
        selectedLearnItem.setPosition(nextLearnItem.getPosition());
        nextLearnItem.setPosition(currentPosition);
        LearnItem currentLearnItem = saveLearnItem(selectedLearnItem);
        saveLearnItem(nextLearnItem);
        refreshListViewLessonItems();
        selectedLearnItem = currentLearnItem;
      }
    }
  }

  public void buttonLearnItemChildrenPositionUpOnAction(ActionEvent actionEvent) {
    if (selectedLearnItemChildren != null) {
      if (selectedLearnItemChildren instanceof Sentence) {
        Sentence currentSentence = (Sentence) selectedLearnItemChildren;
        sentenceService.getPreviousSentence(currentSentence.getWord().getId(), currentSentence.getPosition())
            .ifPresent(previousSentence -> replaceSentence(currentSentence, previousSentence));
      } else if (selectedLearnItemChildren instanceof ExerciseItem) {
        ExerciseItem currentExerciseItem = (ExerciseItem) selectedLearnItemChildren;
        exerciseItemService.getPreviousExerciseItem(currentExerciseItem.getExercise().getId(), currentExerciseItem.getPosition())
            .ifPresent(previousExerciseItem -> replaceExerciseItem(currentExerciseItem, previousExerciseItem));
      }
    }
  }

  public void buttonLearnItemChildrenPositionDownOnAction(ActionEvent actionEvent) {
    if (selectedLearnItemChildren != null) {
      if (selectedLearnItemChildren instanceof Sentence) {
        Sentence currentSentence = (Sentence) selectedLearnItemChildren;
        sentenceService.getNextSentence(currentSentence.getWord().getId(), currentSentence.getPosition())
            .ifPresent(previousSentence -> replaceSentence(currentSentence, previousSentence));
      } else if (selectedLearnItemChildren instanceof ExerciseItem) {
        ExerciseItem currentExerciseItem = (ExerciseItem) selectedLearnItemChildren;
        exerciseItemService.getNextExerciseItem(currentExerciseItem.getExercise().getId(), currentExerciseItem.getPosition())
            .ifPresent(previousExerciseItem -> replaceExerciseItem(currentExerciseItem, previousExerciseItem));
      }
    }
  }

  private void replaceLessons(Lesson currentLesson, Lesson otherLesson) {
    int currentPosition = currentLesson.getPosition();
    currentLesson.setPosition(otherLesson.getPosition());
    otherLesson.setPosition(currentPosition);
    currentLesson = lessonService.save(currentLesson);
    lessonService.save(otherLesson);
    refreshListViewLessons();
    selectedLesson = currentLesson;
  }

  private void replaceCourses(Course currentCourse, Course otherCourse) {
    int currentPosition = currentCourse.getPosition();
    currentCourse.setPosition(otherCourse.getPosition());
    otherCourse.setPosition(currentPosition);
    currentCourse = courseService.save(currentCourse);
    courseService.save(otherCourse);
    refreshListViewCourses();
    selectedCourse = currentCourse;
  }

  private LearnItem saveLearnItem(LearnItem learnItem) {
    if (learnItem instanceof Word) {
      return wordService.save((Word)learnItem);
    } else if (learnItem instanceof Note) {
      return noteService.save((Note) learnItem);
    } else if (learnItem instanceof Exercise) {
      return exerciseService.save((Exercise) learnItem);
    }
    return null;
  }

  private void replaceSentence(Sentence currentSentence, Sentence otherSentence) {
    int currentPosition = currentSentence.getPosition();
    currentSentence.setPosition(otherSentence.getPosition());
    otherSentence.setPosition(currentPosition);
    currentSentence = sentenceService.save(currentSentence);
    sentenceService.save(otherSentence);
    refreshListViewLessonItemChildren();
    selectedLearnItemChildren = currentSentence;
  }

  private void replaceExerciseItem(ExerciseItem currentExerciseItem, ExerciseItem otherExerciseItem) {
    int currentPosition = currentExerciseItem.getPosition();
    currentExerciseItem.setPosition(otherExerciseItem.getPosition());
    otherExerciseItem.setPosition(currentPosition);
    currentExerciseItem = exerciseItemService.save(currentExerciseItem);
    exerciseItemService.save(otherExerciseItem);
    refreshListViewLessonItemChildren();
    selectedLearnItemChildren = currentExerciseItem;
  }

}
