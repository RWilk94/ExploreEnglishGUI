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
public class Static implements Serializable {
    @Serial
    private static final long serialVersionUID = 9116603829114844065L;
    private Category category;
    private Subcategory subcategory;
}
