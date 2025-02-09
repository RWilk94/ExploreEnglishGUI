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
public class Metadata implements Serializable {
    @Serial
    private static final long serialVersionUID = -4296996568498880867L;
    private NLPAnalyzedData nlpAnalyzedData;
}
