package rwilk.exploreenglish.scrapper.ewa.schema.lesson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import rwilk.exploreenglish.scrapper.ewa.schema.course.Image;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lesson implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String _id;
    private String kind;
    private String origin;
    private String courseId;
    private String title;
    private Image image;
    private String avatar;
    private List<String> media;
    private String id;
    private int totalPhrases;
    private List<Exercise> exercises;
    private List<String> lessonWords;
    private List<LessonPhrase> lessonPhrases;
}
