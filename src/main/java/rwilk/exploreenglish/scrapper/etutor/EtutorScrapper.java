package rwilk.exploreenglish.scrapper.etutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rwilk.exploreenglish.model.LanguageEnum;
import rwilk.exploreenglish.model.entity.etutor.EtutorCourse;
import rwilk.exploreenglish.repository.etutor.EtutorCourseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorExerciseRepository;
import rwilk.exploreenglish.repository.etutor.EtutorLessonRepository;
import rwilk.exploreenglish.scrapper.etutor.content_v2.ContentScrapper;
import rwilk.exploreenglish.scrapper.etutor.course.CourseScrapper;
import rwilk.exploreenglish.scrapper.etutor.distinct.DistinctService;
import rwilk.exploreenglish.scrapper.etutor.exercise.ExerciseScrapper;
import rwilk.exploreenglish.scrapper.etutor.lesson.LessonScrapper;
import rwilk.exploreenglish.scrapper.etutor.type.ExerciseType;

import java.util.List;

@Component
public class EtutorScrapper implements CommandLineRunner {

    private final CourseScrapper courseScrapper;
    private final LessonScrapper lessonScrapper;
    private final ExerciseScrapper exerciseScrapper;
    private final ContentScrapper contentScrapper;
    private final DistinctService distinctService;
    private final EtutorCourseRepository etutorCourseRepository;
    private final EtutorLessonRepository etutorLessonRepository;
    private final EtutorExerciseRepository etutorExerciseRepository;
    private final Long courseId;

    public EtutorScrapper(CourseScrapper courseScrapper, LessonScrapper lessonScrapper,
                          ExerciseScrapper exerciseScrapper, ContentScrapper contentScrapper,
                          DistinctService distinctService, EtutorCourseRepository etutorCourseRepository,
                          EtutorLessonRepository etutorLessonRepository,
                          EtutorExerciseRepository etutorExerciseRepository,
                          @Value("${explore-english.course-id}") Long courseId) {
        this.courseScrapper = courseScrapper;
        this.lessonScrapper = lessonScrapper;
        this.exerciseScrapper = exerciseScrapper;
        this.contentScrapper = contentScrapper;
        this.distinctService = distinctService;
        this.etutorCourseRepository = etutorCourseRepository;
        this.etutorLessonRepository = etutorLessonRepository;
        this.etutorExerciseRepository = etutorExerciseRepository;
        this.courseId = courseId;
    }

    @Override
    public void run(String... args) throws Exception {
            // webScrap();
    }

    public void webScrap() {
//        List<EtutorCourse> courses = etutorCourseRepository.findAll();
//
//        if (courses.size() != courseScrapper.countCourses()) {
//            courseScrapper.webScrapAndSaveCourses();
//        }
//        courses = etutorCourseRepository.findAllByIsReady(false);
//
//        courses.forEach(course -> {
//            etutorLessonRepository.deleteAllByCourse_Id(course.getId());
//
//            lessonScrapper.webScrapAndSaveLessons(course);
//
//            course.setIsReady(true);
//            etutorCourseRepository.save(course);
//        });
//
//        etutorLessonRepository.findAllByIsReady(false)
//                .stream()
//                .filter(it -> it.getCourse().getId().equals(courseId))
//                .forEach(lesson -> {
//                    etutorExerciseRepository.deleteAllByLesson_Id(lesson.getId());
//
//                    exerciseScrapper.webScrapContent(lesson);
//
//                    lesson.setIsReady(true);
//                    etutorLessonRepository.save(lesson);
//                });

//        etutorExerciseRepository.findAllByIsReady(false)
//                .stream()
//                .filter(it -> it.getLesson().getCourse().getId() < 15L)
//                .filter(it -> ExerciseType.fromString(it.getType()) != ExerciseType.MULTIREPRESENTATION)
//                .forEach(contentScrapper::webScrap);

         // distinctService.fixEmptyPolishNameInWords();
//          distinctService.fixEmptyPolishNameInWordsByHtml();
//          distinctService.generateEtutorLessonWords();
    }
}
