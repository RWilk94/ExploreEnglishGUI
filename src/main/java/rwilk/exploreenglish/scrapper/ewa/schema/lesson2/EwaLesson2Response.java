package rwilk.exploreenglish.scrapper.ewa.schema.lesson2;

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
public class EwaLesson2Response implements Serializable {
    @Serial
    private static final long serialVersionUID = -7307983153887898227L;
    private Result result;
}
