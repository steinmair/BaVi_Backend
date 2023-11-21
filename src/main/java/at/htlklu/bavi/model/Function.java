package at.htlklu.bavi.model;




import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Function")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Function extends RepresentationModel<Function> implements Serializable
{
    //region static Properties
    private static final long serialVersionUID = -6574326723164905323L;

    //endregion


    //region Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FUNCTION_ID")
    private Integer functionId;

    @NotBlank
    private String name;

    @NotNull
    @Column(name = "CREATED_BY")
    private String createdBy;

    @JsonIgnore
    @ManyToMany(mappedBy = "functions")
    Set<Member> members;

    //endregion


    //region Constructors

    public Function() {

    }

    public Function(Integer functionId, String name,String createdBy) {
        this.functionId = functionId;
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

    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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
