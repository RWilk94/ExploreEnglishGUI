package rwilk.exploreenglish.scrapper.ewa.schema.lesson2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import rwilk.exploreenglish.scrapper.ewa.schema.course.Lesson;

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
public class Course implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private List<Lesson> lessons;
}
