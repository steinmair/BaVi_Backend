package at.htlklu.bavi.Assembler;

import at.htlklu.bavi.controller.PublisherController;
import at.htlklu.bavi.controller.SongController;
import at.htlklu.bavi.model.Publisher;
import at.htlklu.bavi.model.Song;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public
class PublisherModelAssembler implements RepresentationModelAssembler<Publisher, EntityModel<Publisher>> {

    @Override
    public EntityModel<Publisher> toModel(Publisher publisher) {

        return EntityModel.of(publisher, //
                linkTo(methodOn(PublisherController.class).one(publisher.getPublisherId())).withSelfRel(),
                linkTo(methodOn(PublisherController.class).all()).withRel("publishers"));
    }
}
