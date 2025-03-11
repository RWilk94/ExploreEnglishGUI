package rwilk.exploreenglish.scrapper.ewa.schema.lesson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class LessonPhrase implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String _id;
    private String origin;
    private String lessonId;
    private Map<String, String> translation;
    private int number;
    private String localizedTranslation;
}
