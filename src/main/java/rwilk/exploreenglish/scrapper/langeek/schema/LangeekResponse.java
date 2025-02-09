package rwilk.exploreenglish.scrapper.langeek.schema;

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
public class LangeekResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -1144217525649068886L;
    private PageProps pageProps;
}
