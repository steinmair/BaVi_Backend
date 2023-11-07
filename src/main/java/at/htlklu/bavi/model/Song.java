package at.htlklu.bavi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Song")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Song extends RepresentationModel<Song> implements Serializable
{
    //region static Properties
    private static final long serialVersionUID = -6574326723164905323L;

    //endregion


    //region Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SONG_ID")
    private Integer songId;

    @NotBlank
    private String name;
    @NotBlank
    private String url;
    @NotNull
    private Double price;
    @Column(name = "DATE_CREATED")
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate dateCreated;
    private String createdBy;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PUBLISHER_ID")
    private Publisher publisher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GENRE_ID")
    private Genre genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPOSER_ID")
    private Composer composer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;


    //endregion


    //region Constructors
    public Song()
    {
    }

    public Song(Integer songId, String name, String url, Double price, LocalDate dateCreated, String createdBy) {
        this.songId = songId;
        this.name = name;
        this.url = url;
        this.price = price;
        this.dateCreated = dateCreated;
        this.createdBy = createdBy;



    }

//endregion


    //region Getter and Setter

    public Integer getSongId() {
        return songId;
    }

    public void setSongId(Integer songId) {
        this.songId = songId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Composer getComposer() {
        return composer;
    }

    public void setComposer(Composer composer) {
        this.composer = composer;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    //endregion

    //region HashCode and Equals
	@Override
	public int hashCode() {
	    return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean equal;
		Song song1 = this;
		if (song1 == obj)
		{
			equal = true;
		}
		else if ((obj == null) || (!(obj instanceof Song)))
		{
			equal = false;
		}
		else
		{
			Song song2 = (Song) obj;
			equal = song1.songId != null && Objects.equals(song1.songId, song2.getSongId());
		}
		return equal;
	}
	//endregion



}