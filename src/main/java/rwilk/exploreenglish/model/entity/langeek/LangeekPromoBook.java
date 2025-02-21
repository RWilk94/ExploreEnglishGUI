package rwilk.exploreenglish.model.entity.langeek;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class LangeekPromoBook implements Serializable {
    @Serial
    private static final long serialVersionUID = -3873861318949804719L;

    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "words_count")
    private Long wordsCount;
    @Column(name = "exercises_count")
    private Long exercisesCount;
    @Column(name = "course_id")
    private Long courseId;
    @Column(name = "course_name")
    private String courseName;
}
