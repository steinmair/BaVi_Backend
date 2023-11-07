package at.htlklu.bavi.model;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Composer")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Composer extends RepresentationModel<Composer> implements Serializable
{
    //region static Properties
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

    @JsonIgnore
    @OneToMany(mappedBy = "composer",
            cascade = CascadeType.MERGE,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<Song> songs = new HashSet<Song>();

    //endregion


    //region Constructors

    public Composer() {

    }

    public Composer(Integer composerId, String firstname, String surname) {
        this.composerId = composerId;
        this.firstname = firstname;
        this.surname = surname;
    }
    //endregion


    //region Getter and Setter

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