package at.htlklu.bavi.Assembler;

import at.htlklu.bavi.controller.FunctionController;
import at.htlklu.bavi.model.Function;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public
class FunctionModelAssembler implements RepresentationModelAssembler<Function, EntityModel<Function>> {

    @Override
    public EntityModel<Function> toModel(Function function) {

        return EntityModel.of(function, //
                linkTo(methodOn(FunctionController.class).one(function.getFunctionId())).withSelfRel(),
                linkTo(methodOn(FunctionController.class).all()).withRel("functions"));
    }
}
