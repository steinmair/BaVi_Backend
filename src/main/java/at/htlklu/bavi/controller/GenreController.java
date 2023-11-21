package at.htlklu.bavi.controller;

import at.htlklu.bavi.Assembler.GenreModelAssembler;
import at.htlklu.bavi.Assembler.MemberModelAssembler;
import at.htlklu.bavi.model.Genre;
import at.htlklu.bavi.model.Member;
import at.htlklu.bavi.repository.GenresRepository;
import at.htlklu.bavi.repository.MembersRepository;
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
@RequestMapping("/genres")
public class GenreController {

    //https://spring.io/guides/tutorials/rest/

    //http://localhost:8082/genres
    private final GenresRepository genresRepository;
    private final GenreModelAssembler genreModelAssembler;

    public GenreController(GenresRepository genresRepository, GenreModelAssembler genreModelAssembler) {
        this.genresRepository = genresRepository;
        this.genreModelAssembler = genreModelAssembler;
    }

    @GetMapping("")
    public CollectionModel<EntityModel<Genre>> all() {

        List<EntityModel<Genre>> genres = genresRepository.findAll().stream() //
                .map(genreModelAssembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(genres, linkTo(methodOn(GenreController.class).all()).withSelfRel());
    }

    @PostMapping("")
    ResponseEntity<?> newGenre(@RequestBody Genre newGenre) {

        EntityModel<Genre> entityModel = genreModelAssembler.toModel(genresRepository.save(newGenre));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    //Single Song
    @GetMapping("/{id}")
    public EntityModel<Genre> one(@PathVariable Integer id){

        Genre genre = genresRepository.findById(id).orElseThrow(() -> new NotFoundException("Genre (" + id + ") not found"));

        return genreModelAssembler.toModel(genre);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replaceGenre(@RequestBody Genre newGenre, @PathVariable Integer id){


        Genre updatedGenre = genresRepository.findById(id) //
                .map(genre -> {
                    genre.setName(genre.getName());
                    genre.setCreatedBy(genre.getCreatedBy());
                    return genresRepository.save(genre);
                }) //
                .orElseGet(() -> {
                    newGenre.setGenreId(id);
                    return genresRepository.save(newGenre);
                });

        EntityModel<Genre> entityModel = genreModelAssembler.toModel(updatedGenre);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);

    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteGenre(@PathVariable Integer id) {

        genresRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}