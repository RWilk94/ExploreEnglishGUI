package rwilk.exploreenglish.controller.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.LearnItem;
import rwilk.exploreenglish.model.LearnItemChild;
import rwilk.exploreenglish.model.PartOfSpeechEnum;
import rwilk.exploreenglish.model.entity.*;
import rwilk.exploreenglish.service.CourseService;
import rwilk.exploreenglish.service.ExerciseItemService;
import rwilk.exploreenglish.service.ExerciseService;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.LessonService;
import rwilk.exploreenglish.service.LessonWordService;
import rwilk.exploreenglish.service.NoteService;
import rwilk.exploreenglish.service.WordSentenceService;

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
  private final NoteService noteService;
  private final ExerciseService exerciseService;
  private final ExerciseItemService exerciseItemService;
  private final LessonWordService lessonWordService;
  private final WordSentenceService wordSentenceService;

  private Course selectedCourse;
  private List<Lesson> lessons;
  private List<LearnItem> learnItems;
  private Lesson selectedLesson;
  private LearnItem selectedLearnItem;
  private LearnItemChild selectedLearnItemChild;
  public ListView<Course> listViewCourses;
  public TextField textFieldFilterLessons;
  public ListView<Lesson> listViewLessons;
  public TextField textFieldFilterLearnItems;
  public ListView<LearnItem> listViewLearnItems;
  public ListView<LearnItemChild> listViewLearnItemChildren;

  public ViewController(InjectService injectService, CourseService courseService, LessonService lessonService,
                        NoteService noteService, ExerciseService exerciseService,
                        ExerciseItemService exerciseItemService,
                        LessonWordService lessonWordService,
                        WordSentenceService wordSentenceService) {
    this.injectService = injectService;
    this.courseService = courseService;
    this.lessonService = lessonService;
    this.noteService = noteService;
    this.exerciseService = exerciseService;
    this.exerciseItemService = exerciseItemService;
    this.lessonWordService = lessonWordService;
    this.wordSentenceService = wordSentenceService;
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
        } else {
          this.setText("");
        }
        if (item instanceof LessonWord) {
          Word word = ((LessonWord) item).getWord();
          if ((/*nonEmpty(word.getSound()) && */nonEmpty(word.getPartOfSpeech()) && word.getPartOfSpeech().equals(PartOfSpeechEnum.RZECZOWNIK.getValue())
              && nonEmpty(word.getGrammarType()) &&
              ((word.getGrammarType().contains("countable") && (word.getArticle().equals("a") || word.getArticle().equals("an") || word.getArticle().equals("the"))) || word.getGrammarType().equals("uncountable") && StringUtils.trimToEmpty(word.getArticle()).isEmpty()))
              || (/*nonEmpty(word.getSound()) && */nonEmpty(word.getPartOfSpeech()) && !word.getPartOfSpeech().equals(PartOfSpeechEnum.RZECZOWNIK.getValue())
                  && StringUtils.trimToEmpty(word.getGrammarType()).isEmpty() && StringUtils.trimToEmpty(word.getArticle()).isEmpty())
              || (/*nonEmpty(word.getSound()) && */nonEmpty(word.getPartOfSpeech()) && word.getPartOfSpeech().equals(PartOfSpeechEnum.RZECZOWNIK.getValue())
              && nonEmpty(word.getGrammarType()) &&
              word.getGrammarType().equals("plural") && (word.getArticle().equals("")))) {
            setStyle("-fx-background-color: #11ff00");
          } else if ((nonEmpty(word.getPartOfSpeech()) && word.getPartOfSpeech().equals(PartOfSpeechEnum.RZECZOWNIK.getValue())
              && nonEmpty(word.getGrammarType()) &&
              ((word.getGrammarType().contains("countable") && (word.getArticle().equals("a") || word.getArticle().equals("an") || word.getArticle().equals("the"))) || word.getGrammarType().equals("uncountable") && StringUtils.trimToEmpty(word.getArticle()).isEmpty()))
              || (!word.getPartOfSpeech().equals(PartOfSpeechEnum.RZECZOWNIK.getValue())
              && StringUtils.trimToEmpty(word.getGrammarType()).isEmpty() && StringUtils.trimToEmpty(word.getArticle()).isEmpty())
              || (nonEmpty(word.getPartOfSpeech()) && word.getPartOfSpeech().equals(PartOfSpeechEnum.RZECZOWNIK.getValue())
              && nonEmpty(word.getGrammarType()) &&
              word.getGrammarType().equals("plural") && (word.getArticle().equals("")))) {
            setStyle("-fx-background-color: #ff7700");
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

    textFieldFilterLessons.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!CollectionUtils.isEmpty(lessons)) {
        List<Lesson> filtered = lessons.stream()
                                              .filter(l -> l.getEnglishName().toLowerCase().contains(newValue.toLowerCase())
                                                      || l.getPolishName().toLowerCase().contains(newValue.toLowerCase()))
                                              .collect(Collectors.toList());
        listViewLessons.setItems(FXCollections.observableArrayList(filtered));
      }
    });

    textFieldFilterLearnItems.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!CollectionUtils.isEmpty(learnItems)) {
        List<LearnItem> filtered = learnItems.stream()
                                            .filter(item -> item.getName().toLowerCase().contains(newValue))
                                            .collect(Collectors.toList());
        listViewLearnItems.setItems(FXCollections.observableArrayList(filtered));
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
      if (selectedLearnItem instanceof LessonWord lessonWord) {
        final Word word = lessonWord.getWord();
        fillInLessonItemChildrenListView(selectedLearnItem);
        injectService.getWordController().setWordForm(word);
        injectService.getWordController().getWordTableController().textFieldFilterByPlName.setText(word.getPolishName());
        injectService.getMainController().tabPane.getSelectionModel().select(2);
        injectService.getSentenceController().setWordComboBox(word);
      } else if (selectedLearnItem instanceof Exercise exercise) {
        fillInLessonItemChildrenListView(selectedLearnItem);
        injectService.getExerciseController().setExerciseForm(exercise);
        injectService.getMainController().tabPane.getSelectionModel().select(5);
        injectService.getExerciseItemController().setExerciseComboBox(exercise);
      } else if (selectedLearnItem instanceof Note note) {
        listViewLearnItemChildren.setItems(null);
        injectService.getNoteController().setNoteForm(note);
        injectService.getMainController().tabPane.getSelectionModel().select(4);
      }
    }
  }

  public void listViewLearnItemChildrenOnMouseClicked(MouseEvent mouseEvent) {
    selectedLearnItemChild = listViewLearnItemChildren.getSelectionModel().getSelectedItem();
    if (selectedLearnItemChild != null) {
      if (selectedLearnItemChild instanceof WordSentence) {
        injectService.getSentenceController().setSentenceForm(((WordSentence) selectedLearnItemChild).getSentence());
        injectService.getMainController().tabPane.getSelectionModel().select(3);
      } else if (selectedLearnItemChild instanceof ExerciseItem) {
        injectService.getExerciseItemController().setExerciseForm((ExerciseItem) selectedLearnItemChild);
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
    selectedLearnItemChild = null;
  }

  private void fillInLessonsListView(Course course) {
    lessons = lessonService.getAllByCourse(course).stream()
        .sorted(Comparator.comparing(Lesson::getPosition))
        .collect(Collectors.toList());
    listViewLessons.setItems(FXCollections.observableArrayList(lessons));
    selectedLesson = null;
    selectedLearnItem = null;
    selectedLearnItemChild = null;
  }

  private void fillInLearnItemsListView(Lesson lesson) {
    List<Note> notes = noteService.getAllByLesson(lesson);
    List<LessonWord> lessonWords = lessonWordService.getAllByLesson(lesson);
    List<Exercise> exercises = exerciseService.getAllByLesson(lesson);

    learnItems = new ArrayList<>(notes);
    learnItems.addAll(lessonWords);
    learnItems.addAll(exercises);
    learnItems = learnItems.stream().sorted(Comparator.comparing(LearnItem::getPosition)).collect(Collectors.toList());

    listViewLearnItems.setItems(FXCollections.observableArrayList(learnItems));
    selectedLearnItem = null;
    selectedLearnItemChild = null;
  }

  private void fillInLessonItemChildrenListView(LearnItem selectedItem) {
    if (selectedItem instanceof Exercise exercise) {
      final List<LearnItemChild> learnItemChildren = exerciseItemService.getAllByExercise(exercise).stream()
                                                                  .sorted(Comparator.comparing(ExerciseItem::getPosition))
                                                                  .collect(Collectors.toList());
      listViewLearnItemChildren.setItems(FXCollections.observableArrayList(learnItemChildren));
    } else if (selectedItem instanceof LessonWord lessonWord) {
      final Word word = lessonWord.getWord();
      final List<LearnItemChild> learnItemChildren = wordSentenceService.getAllByWord(word).stream()
                                                                  .sorted(Comparator.comparing(WordSentence::getPosition))
                                                                  .collect(Collectors.toList());
      listViewLearnItemChildren.setItems(FXCollections.observableArrayList(learnItemChildren));
    }
    selectedLearnItemChild = null;
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
      Lesson lesson = selectedLearnItem.getLesson();

      lessonWordService.getPreviousWord(lesson.getId(), selectedLearnItem.getPosition())
          .ifPresent(previousWord -> itemHashMap.put(previousWord.getPosition(), previousWord));
      noteService.getPreviousNote(lesson.getId(), selectedLearnItem.getPosition())
          .ifPresent(previousNote -> itemHashMap.put(previousNote.getPosition(), previousNote));
      exerciseService.getPreviousExercise(lesson.getId(), selectedLearnItem.getPosition())
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
      Lesson lesson = selectedLearnItem.getLesson();

      lessonWordService.getNextWord(lesson.getId(), selectedLearnItem.getPosition())
          .ifPresent(previousWord -> itemHashMap.put(previousWord.getPosition(), previousWord));
      noteService.getNextNote(lesson.getId(), selectedLearnItem.getPosition())
          .ifPresent(previousNote -> itemHashMap.put(previousNote.getPosition(), previousNote));
      exerciseService.getNextExercise(lesson.getId(), selectedLearnItem.getPosition())
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
    if (selectedLearnItemChild != null) {
      if (selectedLearnItemChild instanceof WordSentence) {
        WordSentence currentSentence = (WordSentence) selectedLearnItemChild;
        wordSentenceService.getPreviousSentence(currentSentence.getWord().getId(), currentSentence.getPosition())
            .ifPresent(previousSentence -> replaceSentence(currentSentence, previousSentence));
      } else if (selectedLearnItemChild instanceof ExerciseItem) {
        ExerciseItem currentExerciseItem = (ExerciseItem) selectedLearnItemChild;
        exerciseItemService.getPreviousExerciseItem(currentExerciseItem.getExercise().getId(), currentExerciseItem.getPosition())
            .ifPresent(previousExerciseItem -> replaceExerciseItem(currentExerciseItem, previousExerciseItem));
      }
    }
  }

  public void buttonLearnItemChildrenPositionDownOnAction(ActionEvent actionEvent) {
    if (selectedLearnItemChild != null) {
      if (selectedLearnItemChild instanceof WordSentence) {
        WordSentence currentSentence = (WordSentence) selectedLearnItemChild;
        wordSentenceService.getNextSentence(currentSentence.getWord().getId(), currentSentence.getPosition())
            .ifPresent(previousSentence -> replaceSentence(currentSentence, previousSentence));
      } else if (selectedLearnItemChild instanceof ExerciseItem) {
        ExerciseItem currentExerciseItem = (ExerciseItem) selectedLearnItemChild;
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
    if (learnItem instanceof LessonWord) {
      return lessonWordService.save((LessonWord) learnItem);
    } else if (learnItem instanceof Note) {
      return noteService.save((Note) learnItem);
    } else if (learnItem instanceof Exercise) {
      return exerciseService.save((Exercise) learnItem);
    }
    return null;
  }

  private void replaceSentence(WordSentence currentSentence, WordSentence otherSentence) {
    int currentPosition = currentSentence.getPosition();
    currentSentence.setPosition(otherSentence.getPosition());
    otherSentence.setPosition(currentPosition);
    currentSentence = wordSentenceService.save(currentSentence);
    wordSentenceService.save(otherSentence);
    refreshListViewLessonItemChildren();
    selectedLearnItemChild = currentSentence;
  }

  private void replaceExerciseItem(ExerciseItem currentExerciseItem, ExerciseItem otherExerciseItem) {
    int currentPosition = currentExerciseItem.getPosition();
    currentExerciseItem.setPosition(otherExerciseItem.getPosition());
    otherExerciseItem.setPosition(currentPosition);
    currentExerciseItem = exerciseItemService.save(currentExerciseItem);
    exerciseItemService.save(otherExerciseItem);
    refreshListViewLessonItemChildren();
    selectedLearnItemChild = currentExerciseItem;
  }

  private boolean nonEmpty(String text) {
    return StringUtils.isNoneEmpty(StringUtils.trimToEmpty(text));
  }

}
