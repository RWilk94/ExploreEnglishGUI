package rwilk.exploreenglish.model.entity.ewa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "ewa_exercises")
public class EwaExercise implements Serializable {
    @Serial
    private static final long serialVersionUID = -4215815849274499334L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    @Column(name = "ewa_id")
    private String ewaId;
    @Column(name = "kind")
    private String kind;
    @Column(name = "title")
    private String title;
    @Column(name = "origin")
    private String origin;
    @Column(name = "image_id")
    private String imageId;
    @Column(name = "image_s")
    private String imageS;
    @Column(name = "image_m")
    private String imageM;
    @Column(name = "image_l")
    private String imageL;
    @Column(name = "image_xl")
    private String imageXl;
    @Column(name = "is_adult")
    private Boolean isAdult;
    @Column(name = "number")
    private Integer number;

    @Lob
    @Column(name = "json_data", columnDefinition = "LONGTEXT")
    private String jsonData;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;
    @Column(name = "is_ready")
    Boolean isReady;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
    private EwaLesson ewaLesson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEwaId() {
        return ewaId;
    }

    public void setEwaId(String ewaId) {
        this.ewaId = ewaId;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageS() {
        return imageS;
    }

    public void setImageS(String imageS) {
        this.imageS = imageS;
    }

    public String getImageM() {
        return imageM;
    }

    public void setImageM(String imageM) {
        this.imageM = imageM;
    }

    public String getImageL() {
        return imageL;
    }

    public void setImageL(String imageL) {
        this.imageL = imageL;
    }

    public String getImageXl() {
        return imageXl;
    }

    public void setImageXl(String imageXl) {
        this.imageXl = imageXl;
    }

    public Boolean getAdult() {
        return isAdult;
    }

    public void setAdult(Boolean adult) {
        isAdult = adult;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Boolean getReady() {
        return isReady;
    }

    public void setIsReady(Boolean ready) {
        isReady = ready;
    }

    public EwaLesson getEwaLesson() {
        return ewaLesson;
    }

    public void setEwaLesson(EwaLesson ewaLesson) {
        this.ewaLesson = ewaLesson;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EwaExercise that = (EwaExercise) o;
        return Objects.equals(id, that.id) && Objects.equals(ewaId, that.ewaId) && Objects.equals(kind, that.kind) && Objects.equals(title, that.title) && Objects.equals(origin, that.origin) && Objects.equals(imageId, that.imageId) && Objects.equals(imageS, that.imageS) && Objects.equals(imageM, that.imageM) && Objects.equals(imageL, that.imageL) && Objects.equals(imageXl, that.imageXl) && Objects.equals(isAdult, that.isAdult) && Objects.equals(number, that.number) && Objects.equals(jsonData, that.jsonData) && Objects.equals(modifyDate, that.modifyDate) && Objects.equals(isReady, that.isReady) && Objects.equals(ewaLesson, that.ewaLesson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ewaId, kind, title, origin, imageId, imageS, imageM, imageL, imageXl, isAdult, number, jsonData, modifyDate, isReady, ewaLesson);
    }
}
