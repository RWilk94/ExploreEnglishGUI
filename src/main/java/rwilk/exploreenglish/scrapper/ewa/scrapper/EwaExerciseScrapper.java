package rwilk.exploreenglish.scrapper.ewa.scrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.ewa.EwaExercise;
import rwilk.exploreenglish.model.entity.ewa.EwaLesson;
import rwilk.exploreenglish.repository.ewa.EwaExerciseRepository;
import rwilk.exploreenglish.repository.ewa.EwaLessonRepository;
import rwilk.exploreenglish.scrapper.ewa.schema.course.Child;
import rwilk.exploreenglish.scrapper.ewa.schema.course.Lesson;
import rwilk.exploreenglish.scrapper.ewa.schema.course.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EwaExerciseScrapper implements CommandLineRunner {

    private final EwaExerciseRepository ewaExerciseRepository;
    private final EwaLessonRepository ewaLessonRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {

    }

    @Transactional
    public void webScrap(final EwaLesson ewaLesson) {
        final Result result = getEwaResult(ewaLesson);
        final List<Lesson> lessons = result.getChilds().stream()
                .map(Child::getLessons)
                .flatMap(Collection::stream)
                .toList();

        final List<EwaExercise> ewaExercises = new ArrayList<>();
        for (final Lesson lesson : lessons) {
            final EwaExercise ewaExercise = EwaExercise.builder()
                    .id(null)
                    .ewaId(lesson.getId())
                    .kind(lesson.getKind())
                    .title(lesson.getTitle())
                    .imageId(lesson.getImageId() != null ? lesson.getImage().get_id() : null)
                    .imageS(lesson.getImage() != null ? lesson.getImage().getS() : null)
                    .imageM(lesson.getImage() != null ? lesson.getImage().getM() : null)
                    .imageL(lesson.getImage() != null ? lesson.getImage().getL() : null)
                    .imageXl(lesson.getImage() != null ? lesson.getImage().getXl() : null)
                    .isAdult(lesson.isAdult())
                    .isReady(false)
                    .jsonData(null)
                    .ewaLesson(ewaLesson)
                    .build();
            ewaExercises.add(ewaExercise);
        }

        ewaExerciseRepository.saveAll(ewaExercises);
        ewaLesson.setIsReady(true);
        ewaLessonRepository.save(ewaLesson);
    }

    private Result getEwaResult(final EwaLesson ewaLesson) {
        try {
            return objectMapper.readValue(ewaLesson.getJsonData(), Result.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
