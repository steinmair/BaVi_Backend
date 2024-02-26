package at.htlklu.bavi.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;


import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "Role")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Role extends RepresentationModel<Role> implements Serializable {
    //region static Properties

    // Admin alles
    // Schriftführer -- Members bearbeiten
    // Archivar -- Songs bearbeiten, upload files
    // Normal Users -- view songs, download files
    @Serial
    private static final long serialVersionUID = -6574326723164905323L;

    //endregion


    //region Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID")
    private Integer roleId;

    @Enumerated(EnumType.STRING)
    private ERole name;

    @NotNull
    @Column(name = "CREATED_BY")
    private String createdBy;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    Set<Member> members;

    //endregion


    //region Constructors

    public Role() {

    }

    public Role(Integer roleId, ERole name, String createdBy) {
        this.roleId = roleId;
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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }

    public Set<Member> getMembers() {
        return members;
    }

    public void setMembers(Set<Member> members) {
        this.members = members;
    }

//endregion


}
