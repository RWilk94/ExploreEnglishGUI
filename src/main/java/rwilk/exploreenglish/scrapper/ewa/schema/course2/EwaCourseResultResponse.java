package rwilk.exploreenglish.scrapper.ewa.schema.course2;

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
public class EwaCourseResultResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -1450024505013499680L;
    private Result result;
}
