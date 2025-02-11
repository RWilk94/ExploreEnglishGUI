package rwilk.exploreenglish.scrapper.langeek;

import rwilk.exploreenglish.model.entity.langeek.LangeekCourse;
import rwilk.exploreenglish.model.entity.langeek.LangeekExercise;
import rwilk.exploreenglish.model.entity.langeek.LangeekLesson;

public interface LangeekScrapper {
    void webScrapCourse(final String courseUrl);
    void webScrapLessons(final LangeekCourse langeekCourse);
    void webScrapExercises(final LangeekLesson langeekLesson);
    void webScrapWords(final LangeekExercise langeekExercise);
}
