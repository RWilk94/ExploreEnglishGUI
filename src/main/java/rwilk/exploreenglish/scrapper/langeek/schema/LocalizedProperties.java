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
public class LocalizedProperties implements Serializable {
    @Serial
    private static final long serialVersionUID = -1445096267715833489L;
    private String translation;
    private String otherTranslations;
}
