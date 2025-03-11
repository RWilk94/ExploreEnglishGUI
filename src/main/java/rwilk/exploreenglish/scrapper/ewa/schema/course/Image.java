package rwilk.exploreenglish.scrapper.ewa.schema.course;

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
public class Image implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String _id;
    private String xl;
    private String s;
    private String m;
    private String l;
}
