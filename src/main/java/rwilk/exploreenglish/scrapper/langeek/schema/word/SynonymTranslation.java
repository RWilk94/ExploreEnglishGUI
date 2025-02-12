package rwilk.exploreenglish.scrapper.langeek.schema.word;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SynonymTranslation implements Serializable {
    @Serial
    private static final long serialVersionUID = 7019520736229818960L;
    private String wordEntry;
    private int wordEntryId;
    private String word;
    private int wordId;
    private int translationId;
    private String partOfSpeech;
}
