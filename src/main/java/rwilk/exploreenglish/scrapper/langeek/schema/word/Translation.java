package rwilk.exploreenglish.scrapper.langeek.schema.word;

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
public class Translation implements Serializable {
    @Serial
    private static final long serialVersionUID = 164828378100148910L;
    private int id;
    private PartOfSpeech partOfSpeech;
    private WordPhoto wordPhoto;
    private String translation;
    private String alternativeTranslation;
    private String otherTranslations;
    private List<Translation> subTranslations;
    private List<Synonym> synonyms;
    private List<Antonym> antonyms;
    private String level;
    private boolean isDialectSpecific;
    private boolean hideNLPExamples;
    private String visibility;
    private String wiki;
    private String updatedAt;
    private Metadata metadata;
    private LocalizedProperties localizedProperties;
    private String automatedTranslatorDefinition;
    private String title;
    private String titleVoice;
    private String titleTranscription;
}
