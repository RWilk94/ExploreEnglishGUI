package rwilk.exploreenglish.scrapper.langeek.schema.exercise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
public class PageProps implements Serializable {
    @Serial
    @JsonSerialize
    private static final long serialVersionUID = 1196214161354209554L;
    private InitialState initialState;
}
