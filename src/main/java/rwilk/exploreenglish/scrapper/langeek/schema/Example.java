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
public class Example implements Serializable {
    @Serial
    private static final long serialVersionUID = -3314005894957721971L;
    private List<String> words;
    private List<Integer> mainWordIndexes;
}
