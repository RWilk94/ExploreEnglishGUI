package rwilk.exploreenglish.scrapper.ewa.schema.lesson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

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
public class Word implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String _id;
    private Example example;
    private String audio;
    private List<String> meanings;
    private List<String> examples;
    private String transcription;
    private List<String> localizedMeanings;
    private boolean isRepeatable;
    private String origin;
}
