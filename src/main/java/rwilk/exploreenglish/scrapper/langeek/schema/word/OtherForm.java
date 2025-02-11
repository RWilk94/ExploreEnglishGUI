package rwilk.exploreenglish.scrapper.langeek.schema.word;

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
public class OtherForm implements Serializable {
    @Serial
    private static final long serialVersionUID = 6741442433468441689L;
    private String wordForm;
    private String type;
    private String pronunciation;
    private String localSource;
}
