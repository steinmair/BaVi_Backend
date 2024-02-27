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
@Table(name = "Instrument")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Instrument extends RepresentationModel<Instrument> implements Serializable {
    //region static Properties
    @Serial
    private static final long serialVersionUID = -6574326723164905323L;

    //endregion


    //region Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INSTRUMENT_ID")
    private Integer instrumentId;

    @NotBlank
    private String name;

    @NotNull
    @Column(name = "CREATED_BY")
    private String createdBy;

    @JsonIgnore
    @ManyToMany(mappedBy = "instruments")
    Set<Member> members;

    //endregion


    //region Constructors

    public Instrument() {

    }

    public Instrument(Integer instrumentId, String name, String createdBy) {
        this.instrumentId = instrumentId;
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

    public Integer getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Integer instrumentId) {
        this.instrumentId = instrumentId;
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
