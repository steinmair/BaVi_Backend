package at.htlklu.bavi.controller;

import at.htlklu.bavi.Assembler.SongModelAssembler;
import at.htlklu.bavi.model.Song;
import at.htlklu.bavi.repository.SongsRepository;
import at.htlklu.bavi.restController.TemplateRestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/songs")
public class SongController {

    //https://spring.io/guides/tutorials/rest/
    private final SongsRepository songsRepository;
    private final SongModelAssembler songModelAssembler;
	//region Properties
	private static final Logger logger = LogManager.getLogger(SongController.class);

    public SongController(SongsRepository songsRepository, SongModelAssembler songModelAssembler) {
        this.songsRepository = songsRepository;
        this.songModelAssembler = songModelAssembler;
    }

    @GetMapping("")
    public CollectionModel<EntityModel<Song>> all() {

        logger.info("/songs all Method called");

        List<EntityModel<Song>> employees = songsRepository.findAll().stream() //
                .map(songModelAssembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(employees, linkTo(methodOn(SongController.class).all()).withSelfRel());
    }

    @PostMapping("")
    ResponseEntity<?> newSong(@RequestBody Song newSong) {

        logger.info("/songs newSong Method called");

        EntityModel<Song> entityModel = songModelAssembler.toModel(songsRepository.save(newSong));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    //Single Song
    @GetMapping("/{id}")

    public EntityModel<Song> one(@PathVariable Integer id){

        logger.info("/songs/{id} one Method called");

        Song song = songsRepository.findById(id).orElseThrow(() -> new NotFoundException("Song ("+id + ")not found"));

        return songModelAssembler.toModel(song);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replaceSong(@RequestBody Song newSong, @PathVariable Integer id){

        logger.info("/songs/{id} replaceSong Method called");

        Song updatedSong = songsRepository.findById(id) //
                .map(song -> {
                    song.setName(newSong.getName());
                    song.setUrl(newSong.getUrl());
                    song.setPrice(newSong.getPrice());
                    song.setDateCreated(newSong.getDateCreated());
                    song.setCreatedBy(newSong.getCreatedBy());
                    return songsRepository.save(song);
                }) //
                .orElseGet(() -> {
                    newSong.setSongId(id);
                    return songsRepository.save(newSong);
                });

        EntityModel<Song> entityModel = songModelAssembler.toModel(updatedSong);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);

    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteSong(@PathVariable Integer id) {

        logger.info("/songs/{id} deleteSong Method called");

        songsRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}