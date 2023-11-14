package at.htlklu.bavi.Assembler;

import at.htlklu.bavi.controller.ComposerController;
import at.htlklu.bavi.model.Composer;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public
class ComposerModelAssembler implements RepresentationModelAssembler<Composer, EntityModel<Composer>> {

    @Override
    public EntityModel<Composer> toModel(Composer composer) {

        return EntityModel.of(composer, //
                linkTo(methodOn(ComposerController.class).one(composer.getComposerId())).withSelfRel(),
                linkTo(methodOn(ComposerController.class).all()).withRel("composers"));
    }
}
