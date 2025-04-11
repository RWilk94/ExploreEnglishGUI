package rwilk.exploreenglish.scrapper.ewa.schema.lesson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class LessonWord implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String _id;
    private String origin;
//    private Meanings meanings;
    private Map<String, Meaning> meanings;
    private List<String> examples;
    private String transcription;
    private String audio;
    private List<String> localizedMeanings;
}
