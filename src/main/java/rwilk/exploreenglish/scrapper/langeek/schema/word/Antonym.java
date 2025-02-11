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
public class Antonym implements Serializable {
    @Serial
    private static final long serialVersionUID = -4355118092114616563L;
    private String word;
}
