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
public class Child implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String _id;
    private int channel;
    private boolean isAdult;
    private int number;
    private String parentId;
    private String origin;
    private String title;
    private String imageId;
    private String courseRole;
    private Image image;
    private String backgroundImage;
    private String id;
    private List<Lesson> lessons;
    private boolean isComplete;
    private int starsTotal;
    private int starsEarned;
    private String description;
    private String goalDescription;
    private List<Child> childs;
}
