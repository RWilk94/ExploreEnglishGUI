package rwilk.exploreenglish.controller.word;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.Toggle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.exploreenglish.model.PartOfSpeechEnum;
import rwilk.exploreenglish.model.entity.Lesson;
import rwilk.exploreenglish.model.entity.Term;
import rwilk.exploreenglish.model.entity.Word;
import rwilk.exploreenglish.repository.CourseRepository;
import rwilk.exploreenglish.repository.ExerciseItemRepository;
import rwilk.exploreenglish.repository.ExerciseRepository;
import rwilk.exploreenglish.repository.LessonRepository;
import rwilk.exploreenglish.repository.LessonWordRepository;
import rwilk.exploreenglish.repository.NoteRepository;
import rwilk.exploreenglish.repository.SentenceRepository;
import rwilk.exploreenglish.repository.WordRepository;
import rwilk.exploreenglish.repository.WordSoundRepository;
import rwilk.exploreenglish.repository.release.ReleaseCourseRepository;
import rwilk.exploreenglish.repository.release.ReleaseExerciseRepository;
import rwilk.exploreenglish.repository.release.ReleaseExerciseRowRepository;
import rwilk.exploreenglish.repository.release.ReleaseLessonRepository;
import rwilk.exploreenglish.repository.release.ReleaseNoteRepository;
import rwilk.exploreenglish.repository.release.ReleaseSentenceRepository;
import rwilk.exploreenglish.repository.release.ReleaseWordRepository;
import rwilk.exploreenglish.service.InjectService;
import rwilk.exploreenglish.service.LessonService;
import rwilk.exploreenglish.service.LessonWordService;
import rwilk.exploreenglish.service.ReleaseWordService;
import rwilk.exploreenglish.service.WordService;
import rwilk.exploreenglish.service.WordSoundService;
import rwilk.exploreenglish.utils.WordUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import lombok.Getter;

@Controller
public class WordController implements Initializable {

  private final InjectService injectService;
  private final LessonService lessonService;
  private final WordService wordService;
  private final LessonWordService lessonWordService;
  private final WordSoundService wordSoundService;
  private final ReleaseWordService releaseWordService;

  @Getter private final ReleaseCourseRepository releaseCourseRepository;
  @Getter private final CourseRepository courseRepository;
  @Getter private final ReleaseExerciseRepository releaseExerciseRepository;
  @Getter private final ExerciseRepository exerciseRepository;
  @Getter private final ReleaseExerciseRowRepository releaseExerciseRowRepository;
  @Getter private final ExerciseItemRepository exerciseItemRepository;
  @Getter private final ReleaseLessonRepository releaseLessonRepository;
  @Getter private final LessonRepository lessonRepository;
  @Getter private final ReleaseNoteRepository releaseNoteRepository;
  @Getter private final NoteRepository noteRepository;
  @Getter private final ReleaseSentenceRepository releaseSentenceRepository;
  @Getter private final SentenceRepository sentenceRepository;
  @Getter private final ReleaseWordRepository releaseWordRepository;
  @Getter private final WordRepository wordRepository;
  @Getter private final LessonWordRepository lessonWordRepository;
  @Getter private final WordSoundRepository wordSoundRepository;
  @FXML private TabPane tabPane;
  private WordFormController wordFormController;
  private WordTableController wordTableController;

  @FXML private AnchorPane anchorPaneForm;
  @FXML private AnchorPane anchorPaneTable;

  public WordController(final InjectService injectService, final LessonService lessonService,
                        final WordService wordService, final LessonWordService lessonWordService,
                        final WordSoundService wordSoundService, final ReleaseWordService releaseWordService, final ReleaseCourseRepository releaseCourseRepository, final CourseRepository courseRepository, final ReleaseExerciseRepository releaseExerciseRepository, final ExerciseRepository exerciseRepository, final ReleaseExerciseRowRepository releaseExerciseRowRepository, final ExerciseItemRepository exerciseItemRepository, final ReleaseLessonRepository releaseLessonRepository, final LessonRepository lessonRepository, final ReleaseNoteRepository releaseNoteRepository, final NoteRepository noteRepository, final ReleaseSentenceRepository releaseSentenceRepository, final SentenceRepository sentenceRepository, final ReleaseWordRepository releaseWordRepository, final WordRepository wordRepository, final LessonWordRepository lessonWordRepository, final WordSoundRepository wordSoundRepository) {
    this.injectService = injectService;
    this.lessonService = lessonService;
    this.wordService = wordService;
    this.lessonWordService = lessonWordService;
    this.wordSoundService = wordSoundService;
    this.releaseWordService = releaseWordService;
    this.releaseCourseRepository = releaseCourseRepository;
    this.courseRepository = courseRepository;
    this.releaseExerciseRepository = releaseExerciseRepository;
    this.exerciseRepository = exerciseRepository;
    this.releaseExerciseRowRepository = releaseExerciseRowRepository;
    this.exerciseItemRepository = exerciseItemRepository;
    this.releaseLessonRepository = releaseLessonRepository;
    this.lessonRepository = lessonRepository;
    this.releaseNoteRepository = releaseNoteRepository;
    this.noteRepository = noteRepository;
    this.releaseSentenceRepository = releaseSentenceRepository;
    this.sentenceRepository = sentenceRepository;
    this.releaseWordRepository = releaseWordRepository;
    this.wordRepository = wordRepository;
    this.lessonWordRepository = lessonWordRepository;
    this.wordSoundRepository = wordSoundRepository;
    injectService.setWordController(this);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    init();
  }

  public void init() {
    try {
      FXMLLoader fxmlLoaderForm = new FXMLLoader();
      fxmlLoaderForm.setLocation(getClass().getResource("/scene/word/word_form.fxml"));
      ScrollPane form = fxmlLoaderForm.load();
      wordFormController = fxmlLoaderForm.getController();

      FXMLLoader fxmlLoaderTable = new FXMLLoader();
      fxmlLoaderTable.setLocation(getClass().getResource("/scene/word/word_table.fxml"));
      VBox table = fxmlLoaderTable.load();
      wordTableController = fxmlLoaderTable.getController();

      wordFormController.init(this);
      wordTableController.init(this);

      anchorPaneForm.getChildren().add(form);
      anchorPaneTable.getChildren().add(table);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setWordForm(Word word) {
    wordFormController.setWordForm(word);
    wordFormController.translate(false);
  }

  public void setWordForm(Term term) {
    wordFormController.setWordForm(term);
    wordFormController.translate(false);
  }

  public void refreshTableView() {
    wordTableController.fillInTableView();
  }

  public void refreshChildTableView() {
    injectService.getSentenceController().refreshTableView();
    injectService.getSentenceController().refreshWordComboBox();
    injectService.getViewController().refreshListViewLessonItems();
  }

  public void refreshChildComboBoxes() {
    injectService.getSentenceController().refreshWordComboBox();
    injectService.getViewController().refreshListViewLessonItems();
  }

  public void refreshLessonComboBox() {
    wordFormController.initializeLessonComboBox();
  }

  public void setLessonComboBox(Lesson lesson) {
    wordFormController.getComboBoxLesson().getSelectionModel().select(lesson);
  }

  public void setMeaningAndProperties(String text, String partOfSpeech) {
    if (StringUtils.isNoneEmpty(text)) {
//      wordFormController.textFieldSynonym.setText(WordUtils.extractSynonym(text));
//      wordFormController.textFieldOpposite.setText(WordUtils.extractOpposite(text));

      String extractedPartOfSpeech = WordUtils.extractPartOfSpeech(partOfSpeech);
      Toggle toggleButtonPOS = wordFormController.getToggleGroupPartOfSpeech().getToggles().stream()
          .filter(toggle -> toggle.getUserData().toString().equalsIgnoreCase(extractedPartOfSpeech))
          .findFirst()
          .orElse(null);
      wordFormController.getToggleGroupPartOfSpeech().selectToggle(toggleButtonPOS);

      String grammarTag = WordUtils.extractGrammarTag(text);
      Toggle toggleButtonGT = wordFormController.getToggleGroupGrammar().getToggles().stream()
          .filter(toggle -> toggle.getUserData().toString().equalsIgnoreCase(grammarTag))
          .findFirst()
          .orElse(null);
      if (extractedPartOfSpeech.equals(PartOfSpeechEnum.RZECZOWNIK.getValue())
          && toggleButtonGT != null && !toggleButtonGT.getUserData().toString().isEmpty()) {
        wordFormController.getToggleGroupGrammar().selectToggle(toggleButtonGT);
        if (toggleButtonGT.getUserData().toString().equals("countable and uncountable")) {
          wordFormController.getToggleGroupArticle().selectToggle(
              wordFormController.getToggleGroupArticle().getToggles().stream()
                  .filter(toggle -> toggle.getUserData().toString().equalsIgnoreCase(""))
                  .findFirst()
                  .orElse(null));
        }
      } else {
        wordFormController.getToggleGroupGrammar().selectToggle(wordFormController.getToggleGroupGrammar().getToggles()
            .stream()
            .filter(toggle -> toggle.getUserData().toString().equalsIgnoreCase(""))
            .findFirst()
            .orElse(null));
      }
      if (text.contains("[")) {
        wordFormController.getTextFieldPolishName().setText(text.substring(0, text.indexOf("[")).trim());
      } else {
        wordFormController.getTextFieldPolishName().setText(text);
      }
    }
  }

  public LessonService getLessonService() {
    return lessonService;
  }

  public WordService getWordService() {
    return wordService;
  }

  public LessonWordService getLessonWordService() {
    return lessonWordService;
  }

  public WordSoundService getWordSoundService() {
    return wordSoundService;
  }

  public WordTableController getWordTableController() {
    return wordTableController;
  }

  public WordFormController getWordFormController() {
    return wordFormController;
  }

  public InjectService getInjectService() {
    return injectService;
  }

  public ReleaseWordService getReleaseWordService() {
    return releaseWordService;
  }

  public TabPane getTabPane() {
    return tabPane;
  }
}
