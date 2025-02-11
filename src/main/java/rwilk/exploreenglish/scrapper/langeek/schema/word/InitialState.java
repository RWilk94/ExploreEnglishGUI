package rwilk.exploreenglish.scrapper.langeek.schema.word;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class InitialState implements Serializable {
    @Serial
    private static final long serialVersionUID = -2633167135704086649L;
    @JsonProperty("static")
    private Static staticData;
}
