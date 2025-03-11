package rwilk.exploreenglish.scrapper.ewa.schema.course;

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
public class EwaCourseResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -7307983153887898227L;
    private List<Result> result;
}
