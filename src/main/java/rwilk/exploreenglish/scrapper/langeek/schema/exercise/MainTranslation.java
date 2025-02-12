package rwilk.exploreenglish.scrapper.langeek.schema.exercise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import rwilk.exploreenglish.scrapper.langeek.schema.word.LocalizedProperties;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MainTranslation implements Serializable {
    @Serial
    private static final long serialVersionUID = 920314245857121218L;
    private PartOfSpeech partOfSpeech;
    private String title;
    private WordPhoto wordPhoto;
    private String translation;
    private int id;
    private Word word;
    private LocalizedProperties localizedProperties;
}
