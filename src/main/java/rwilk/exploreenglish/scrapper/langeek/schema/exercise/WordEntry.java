package rwilk.exploreenglish.scrapper.langeek.schema.exercise;

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
public class WordEntry implements Serializable {
    @Serial
    private static final long serialVersionUID = -4930935105229976148L;
    private int id;
    private String entry;
}
