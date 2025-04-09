package rwilk.exploreenglish.scrapper.ewa.scrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rwilk.exploreenglish.model.entity.ewa.EwaCourse;
import rwilk.exploreenglish.model.entity.ewa.EwaLesson;
import rwilk.exploreenglish.repository.ewa.EwaCourseRepository;
import rwilk.exploreenglish.repository.ewa.EwaLessonRepository;
import rwilk.exploreenglish.scrapper.ewa.schema.course.EwaCourseResponse;
import rwilk.exploreenglish.scrapper.ewa.schema.course.Result;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EwaLessonScrapper implements CommandLineRunner {

    private final EwaCourseRepository ewaCourseRepository;
    private final EwaLessonRepository ewaLessonRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        // scrapLesson("db6bea10-3178-46ae-885a-d06e986f8521");
    }

    @Transactional
    public void webScrap(final EwaCourse ewaCourse) {
        final EwaCourseResponse ewaCourseResponse = getEwaCourseResponse(ewaCourse);
        System.out.println(ewaCourseResponse);

        final List<EwaLesson> ewaLessons = new ArrayList<>();
        for (final Result result : ewaCourseResponse.getResult()) {
            final EwaLesson ewaLesson = EwaLesson.builder()
                    .id(null)
                    .ewaId(result.getId())
                    .channel(result.getChannel())
                    .isAdult(result.isAdult())
                    .number(result.getNumber())
                    .title(result.getTitle())
                    .imageId(result.getImageId())
                    .imageS(result.getImage() != null ? result.getImage().getS() : null)
                    .imageM(result.getImage() != null ? result.getImage().getM() : null)
                    .imageL(result.getImage() != null ? result.getImage().getL() : null)
                    .imageXl(result.getImage() != null ? result.getImage().getXl() : null)
                    .courseRole(result.getCourseRole())
                    .backgroundImage(result.getBackgroundImage())
                    .description(result.getDescription())
                    .goalDescription(result.getGoalDescription())
                    .jsonData(getEwaLessonJson(result))
                    .isReady(false)
                    .course(ewaCourse)
                    .build();

            ewaLessons.add(ewaLesson);
        }

        ewaLessonRepository.saveAll(ewaLessons);
        ewaCourse.setIsReady(true);
        ewaCourseRepository.save(ewaCourse);
    }

    private EwaCourseResponse getEwaCourseResponse(final EwaCourse ewaCourse) {
        try {
            return objectMapper.readValue(ewaCourse.getJsonData(), EwaCourseResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getEwaLessonJson(final Result result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
