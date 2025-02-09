package rwilk.exploreenglish.scrapper.langeek.schema;

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
public class NLPAnalyzedData implements Serializable {
    @Serial
    private static final long serialVersionUID = -5917896787477154758L;
    private String comparativeForm;
    private String superlativeForm;
    private Boolean isComparable;
    private String partOfSpeech;
    private String word;
}
