package at.htlklu.bavi.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "Instrument")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Instrument extends RepresentationModel<Instrument> implements Serializable
{
    //region static Properties
    private static final long serialVersionUID = -6574326723164905323L;

    //endregion


    //region Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INSTRUMENT_ID")
    private Integer functionId;

    @NotBlank
    private String name;

    //endregion


    //region Constructors

    public Instrument() {

    }

    public Instrument(Integer functionId, String name) {
        this.functionId = functionId;
        this.name = name;
    }

    //endregion


    //region Getter and Setter

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


    //endregion



}
