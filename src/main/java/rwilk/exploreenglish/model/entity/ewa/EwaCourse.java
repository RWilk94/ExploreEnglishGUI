package rwilk.exploreenglish.model.entity.ewa;

import lombok.*;
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
@Table(name = "ewa_courses")
public class EwaCourse implements Serializable {
    @Serial
    private static final long serialVersionUID = 5107043430339822308L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    @Column(name = "ewa_id")
    private String ewaId;
    @Column(name = "name", length = 2000)
    private String name;
    @Column(name = "description", length = 2000)
    private String description;
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

    @Lob
    @Column(name = "json_data", columnDefinition = "LONGTEXT")
    private String jsonData;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;
    @Column(name = "is_ready", columnDefinition = "int default 0")
    Boolean isReady;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EwaCourse ewaCourse = (EwaCourse) o;
        return Objects.equals(id, ewaCourse.id) && Objects.equals(ewaId, ewaCourse.ewaId) && Objects.equals(name, ewaCourse.name) && Objects.equals(description, ewaCourse.description) && Objects.equals(imageId, ewaCourse.imageId) && Objects.equals(imageS, ewaCourse.imageS) && Objects.equals(imageM, ewaCourse.imageM) && Objects.equals(imageL, ewaCourse.imageL) && Objects.equals(imageXl, ewaCourse.imageXl) && Objects.equals(jsonData, ewaCourse.jsonData) && Objects.equals(modifyDate, ewaCourse.modifyDate) && Objects.equals(isReady, ewaCourse.isReady);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ewaId, name, description, imageId, imageS, imageM, imageL, imageXl, jsonData, modifyDate, isReady);
    }
}
