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
@Table(name = "Member")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Member extends RepresentationModel<Member> implements Serializable
{
    //region static Properties
    private static final long serialVersionUID = -6574326723164905323L;

    //endregion


    //region Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Integer memberId;

    @NotBlank
    private String firstname;
    @NotBlank
    private String surname;

    @Column(name = "BIRTHDATE")
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate birthdate;
    private Integer phone;
    private String eMail;

    @Column(name = "DATE_JOINED")
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate dateJoined;



    private String street;
    private Integer zipCode;
    private String city;


    // https://www.baeldung.com/jpa-many-to-many
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "MemberFunction",
            joinColumns = @JoinColumn(name = "MEMBER_ID"),
            inverseJoinColumns = @JoinColumn(name = "FUNCTION_ID")
    )
    Set<Function> functions;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "MemberFunction",
            joinColumns = @JoinColumn(name = "MEMBER_ID"),
            inverseJoinColumns = @JoinColumn(name = "INSTRUMENT_ID")
    )
    Set<Instrument> instruments;

    @JsonIgnore
    @OneToMany(mappedBy = "member",
            cascade = CascadeType.MERGE,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<Song> songs = new HashSet<Song>();


    //endregion


    //region Constructors
    public Member()
    {
    }

    public Member( String firstname, String surname, LocalDate birthdate, Integer phone, String eMail, LocalDate dateJoined, String street, Integer zipCode, String city) {

        super();
        this.firstname = firstname;
        this.surname = surname;
        this.birthdate = birthdate;
        this.phone = phone;
        this.eMail = eMail;
        this.dateJoined = dateJoined;
        this.street = street;
        this.zipCode = zipCode;
        this.city = city;



    }

    //endregion


    //region Getter and Setter

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
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

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public LocalDate getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(LocalDate dateJoined) {
        this.dateJoined = dateJoined;
    }


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Set<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(Set<Function> functions) {
        this.functions = functions;
    }

    public Set<Instrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(Set<Instrument> instruments) {
        this.instruments = instruments;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
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
		Member member1 = this;
		if (member1 == obj)
		{
			equal = true;
		}
		else if ((obj == null) || (!(obj instanceof Member)))
		{
			equal = false;
		}
		else
		{
			Member member2 = (Member) obj;
			equal = member1.memberId != null && Objects.equals(member1.memberId, member2.getMemberId());
		}
		return equal;
	}
	//endregion



}