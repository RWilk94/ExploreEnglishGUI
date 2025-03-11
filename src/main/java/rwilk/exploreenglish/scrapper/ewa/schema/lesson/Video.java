package rwilk.exploreenglish.scrapper.ewa.schema.lesson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import rwilk.exploreenglish.scrapper.ewa.schema.course.Image;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Video implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Image thumbnail;
}
