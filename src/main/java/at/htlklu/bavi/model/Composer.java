package at.htlklu.bavi.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;


import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Composer")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Composer extends RepresentationModel<Composer> implements Serializable {
    //region static Properties
    @Serial
    private static final long serialVersionUID = -6574326723164905323L;

    //endregion


    //region Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMPOSER_ID")
    private Integer composerId;

    @NotBlank
    private String firstname;

    @NotBlank
    private String surname;
    @NotNull
    @Column(name = "CREATED_BY")
    private String createdBy;

    @JsonIgnore
    @OneToMany(mappedBy = "composer",
            cascade = CascadeType.MERGE,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<Song> songs = new HashSet<>();

    //endregion


    //region Constructors

    public Composer() {

    }

    public Composer(Integer composerId, String firstname, String surname, String createdBy) {
        this.composerId = composerId;
        this.firstname = firstname;
        this.surname = surname;
        this.createdBy = createdBy;
    }
    //endregion


    //region Getter and Setter


    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getComposerId() {
        return composerId;
    }

    public void setComposerId(Integer composerId) {
        this.composerId = composerId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
    }

    //endregion


}