package at.htlklu.bavi.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import at.htlklu.bavi.controller.SongController;
import at.htlklu.bavi.model.Song;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public
class SongModelAssembler implements RepresentationModelAssembler<Song, EntityModel<Song>> {

    @Override
    public EntityModel<Song> toModel(Song song) {

        return EntityModel.of(song, //
                linkTo(methodOn(SongController.class).one(song.getSongId())).withSelfRel(),
                linkTo(methodOn(SongController.class).all()).withRel("employees"));
    }
}
