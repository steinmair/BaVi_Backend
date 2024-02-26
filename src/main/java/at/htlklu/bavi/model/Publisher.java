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
@Table(name = "Publisher")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Publisher extends RepresentationModel<Publisher> implements Serializable {
    //region static Properties
    @Serial
    private static final long serialVersionUID = -6574326723164905323L;

    //endregion


    //region Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PUBLISHER_ID")
    private Integer publisherId;

    @NotBlank
    private String name;

    @NotNull
    @Column(name = "CREATED_BY")
    private String createdBy;

    @JsonIgnore
    @OneToMany(mappedBy = "publisher",
            cascade = CascadeType.MERGE,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<Song> songs = new HashSet<>();

    //endregion


    //region Constructors

    public Publisher() {

    }

    public Publisher(Integer publisherId, String name, String createdBy) {
        this.publisherId = publisherId;
        this.name = name;
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

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
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