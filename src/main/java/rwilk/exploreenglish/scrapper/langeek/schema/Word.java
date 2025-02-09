package rwilk.exploreenglish.scrapper.langeek.schema;

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
    private static final long serialVersionUID = 8904623302207411784L;
    private int id;
    private String word;
    private List<OtherForm> otherForms;
    private String lang;
    private String fullForm;
    private List<Translation> translations;
    private String wordVoice;
    private String wordTranscription;
}
