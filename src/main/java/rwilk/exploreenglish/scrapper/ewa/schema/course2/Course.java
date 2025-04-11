package rwilk.exploreenglish.scrapper.ewa.schema.course2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import rwilk.exploreenglish.scrapper.ewa.schema.course.Child;
import rwilk.exploreenglish.scrapper.ewa.schema.course.Image;

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
public class Course implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String _id;
    private int channel;
    private boolean isAdult;
    private int number;
    private String origin;
    private String title;
    private String imageId;
    private String courseRole;
    private Image image;
    private Image backgroundImage;
    private String id;
    private int starsTotal;
    private int starsEarned;
    private boolean isComplete;
    private String description;
    private String goalDescription;
    private List<Child> childs;
}
