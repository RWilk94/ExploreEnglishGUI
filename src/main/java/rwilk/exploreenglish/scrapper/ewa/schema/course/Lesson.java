package rwilk.exploreenglish.scrapper.ewa.schema.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lesson implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int passScore;
    private String _id;
    private boolean isFree;
    private boolean isAdult;
    private boolean hasAdultContent;
    private String kind;
    private int number;
    private int totalExercises;
    private String origin;
    private String promoAction;
    private String courseId;
    private String title;
    private String imageId;
    private Map<String, Boolean> isReady;
    private Image image;
    private String avatar;
    private List<String> media;
    private String id;
    private int totalPhrases;
    private double progress;
    private int starsEarned;
    private boolean isComplete;
    private boolean isLocked;
}
