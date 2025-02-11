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
public class WordPhoto implements Serializable {
    @Serial
    private static final long serialVersionUID = 8176505253703073324L;
    private String photo;
}
