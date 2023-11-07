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
@Table(name = "Genre")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Genre extends RepresentationModel<Genre> implements Serializable
{
    //region static Properties
    private static final long serialVersionUID = -6574326723164905323L;

    //endregion


    //region Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GENRE_ID")
    private Integer genreId;

    @NotBlank
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "genre",
            cascade = CascadeType.MERGE,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<Song> songs = new HashSet<Song>();

    //endregion


    //region Constructors

    public Genre() {

    }
    public Genre(Integer genreId, String name) {
        this.genreId = genreId;
        this.name = name;
    }


    //endregion


    //region Getter and Setter

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
    }

    //endregion



}