package at.htlklu.bavi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.RepresentationModel;


import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Member")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Member extends RepresentationModel<Member> implements Serializable {
    //region static Properties
    @Serial
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
    private String phone;
    @NotNull
    @Column(unique = true)
    @Email
    private String email;
    //@JsonIgnore
    private String password;
    @Column(name = "HOUSE_NUMBER")
    private Integer houseNumber;
    @Column(name = "DATE_JOINED")
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate dateJoined;

    private String street;
    @Column(name = "ZIP_CODE")
    private Integer zipCode;
    private String city;
    @NotNull
    @Column(name = "CREATED_BY")
    private String createdBy;


    // https://www.baeldung.com/jpa-many-to-many
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "MemberRole",
            joinColumns = @JoinColumn(name = "MEMBER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    Set<Role> roles;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "MemberInstrument",
            joinColumns = @JoinColumn(name = "MEMBER_ID"),
            inverseJoinColumns = @JoinColumn(name = "INSTRUMENT_ID")
    )
    Set<Instrument> instruments;

    @JsonIgnore
    @OneToMany(mappedBy = "member",
            cascade = CascadeType.MERGE,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<Song> songs = new HashSet<>();


    //endregion


    //region Constructors
    public Member() {
    }

    public Member(String firstname, String surname, LocalDate birthdate, String phone, String email, String password, Integer houseNumber, LocalDate dateJoined, String street, Integer zipCode, String city, String createdBy) {

        super();
        this.firstname = firstname;
        this.surname = surname;
        this.birthdate = birthdate;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.houseNumber = houseNumber;
        this.dateJoined = dateJoined;
        this.street = street;
        this.zipCode = zipCode;
        this.city = city;
        this.createdBy = createdBy;


    }

    //endregion


    //region Getter and Setter


    public Integer getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(Integer houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String eMail) {
        this.email = eMail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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
    public boolean equals(Object obj) {
        boolean equal;
        Member member1 = this;
        if (member1 == obj) {
            equal = true;
        } else if ((!(obj instanceof Member))) {
            equal = false;
        } else {
            Member member2 = (Member) obj;
            equal = member1.memberId != null && Objects.equals(member1.memberId, member2.getMemberId());
        }
        return equal;
    }
    //endregion


}