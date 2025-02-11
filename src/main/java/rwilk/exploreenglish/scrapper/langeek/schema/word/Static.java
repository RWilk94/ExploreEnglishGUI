package rwilk.exploreenglish.scrapper.langeek.schema.word;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Static implements Serializable {
    @Serial
    private static final long serialVersionUID = 5991193048502494118L;
    private WordEntry wordEntry;
    private Map<String, List<Example>> simpleExamples;
}
