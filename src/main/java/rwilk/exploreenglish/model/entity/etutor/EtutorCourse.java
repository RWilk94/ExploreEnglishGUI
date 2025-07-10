package rwilk.exploreenglish.model.entity.etutor;

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "etutor_courses")
public class EtutorCourse implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    @Column(name = "name", length = 2000)
    private String name;
    @Column(name = "description", length = 2000)
    private String description;
    @Column(name = "href", length = 2000)
    private String href;
    @Column(name = "image", length = 256)
    private String image;
    @Column(name = "language")
    private String language;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;
    @Column(name = "is_ready")
    Boolean isReady;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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
        EtutorCourse that = (EtutorCourse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(href, that.href) && Objects.equals(image, that.image) && Objects.equals(language, that.language) && Objects.equals(modifyDate, that.modifyDate) && Objects.equals(isReady, that.isReady);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, href, image, language, modifyDate, isReady);
    }
}
