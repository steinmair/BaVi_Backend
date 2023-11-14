package at.htlklu.bavi.Assembler;

import at.htlklu.bavi.controller.InstrumentController;
import at.htlklu.bavi.model.Instrument;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public
class InstrumentModelAssembler implements RepresentationModelAssembler<Instrument, EntityModel<Instrument>> {

    @Override
    public EntityModel<Instrument> toModel(Instrument instrument) {

        return EntityModel.of(instrument, //
                linkTo(methodOn(InstrumentController.class).one(instrument.getInstrumentId())).withSelfRel(),
                linkTo(methodOn(InstrumentController.class).all()).withRel("instruments"));
    }
}
