package rwilk.exploreenglish.scrapper.langeek.schema.exercise;

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
public class Subcategory implements Serializable {
    @Serial
    private static final long serialVersionUID = -6856238742924299018L;
    private int id;
    private String title;
    private String description;
    private int wordsCount;
    private List<Card> cards;
}
