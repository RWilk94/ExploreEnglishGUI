package rwilk.exploreenglish.scrapper.ewa.schema.course2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import rwilk.exploreenglish.scrapper.ewa.schema.lesson.Lesson;

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
public class Result implements Serializable {
    @Serial
    private static final long serialVersionUID = -8885671764863825285L;
    private List<Course> rows;
}
