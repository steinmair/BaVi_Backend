package at.htlklu.bavi.Assembler;

import at.htlklu.bavi.controller.GenreController;
import at.htlklu.bavi.model.Genre;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public
class GenreModelAssembler implements RepresentationModelAssembler<Genre, EntityModel<Genre>> {

    @Override
    public EntityModel<Genre> toModel(Genre genre) {

        return EntityModel.of(genre, //
                linkTo(methodOn(GenreController.class).one(genre.getGenreId())).withSelfRel(),
                linkTo(methodOn(GenreController.class).all()).withRel("genres"));
    }
}
