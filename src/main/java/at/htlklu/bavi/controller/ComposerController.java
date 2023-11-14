package at.htlklu.bavi.controller;

import at.htlklu.bavi.Assembler.ComposerModelAssembler;
import at.htlklu.bavi.Assembler.MemberModelAssembler;
import at.htlklu.bavi.model.Composer;
import at.htlklu.bavi.model.Function;
import at.htlklu.bavi.model.Member;
import at.htlklu.bavi.repository.ComposersRepository;
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
public class ComposerController {

    //https://spring.io/guides/tutorials/rest/
    private final ComposersRepository composersRepository;
    private final ComposerModelAssembler composerModelAssembler;

    public ComposerController(ComposersRepository composersRepository, ComposerModelAssembler composerModelAssembler) {
        this.composersRepository = composersRepository;
        this.composerModelAssembler = composerModelAssembler;
    }

    @GetMapping("/composers")
    public CollectionModel<EntityModel<Composer>> all() {

        List<EntityModel<Composer>> composers = composersRepository.findAll().stream() //
                .map(composerModelAssembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(composers, linkTo(methodOn(ComposerController.class).all()).withSelfRel());
    }

    @PostMapping("/composers")
    ResponseEntity<?> newComposer(@RequestBody Composer newComposer) {

        EntityModel<Composer> entityModel = composerModelAssembler.toModel(composersRepository.save(newComposer));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    //Single Song
    @GetMapping("/composers/{id}")
    public EntityModel<Composer> one(@PathVariable Integer id){

        Composer composer = composersRepository.findById(id).orElseThrow(() -> new NotFoundException("Composer (" + id + ") not found"));

        return composerModelAssembler.toModel(composer);
    }

    @PutMapping("/composers/{id}")
    ResponseEntity<?> replaceComposer(@RequestBody Composer newComposer, @PathVariable Integer id){


        Composer updatedComposer = composersRepository.findById(id) //
                .map(composer -> {
                    composer.setFirstname(composer.getFirstname());
                    composer.setSurname(composer.getSurname());
                    return composersRepository.save(composer);
                }) //
                .orElseGet(() -> {
                    newComposer.setComposerId(id);
                    return composersRepository.save(newComposer);
                });



        EntityModel<Composer> entityModel = composerModelAssembler.toModel(updatedComposer);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);

    }

    @DeleteMapping("/composers/{id}")
    ResponseEntity<?> deleteComposer(@PathVariable Integer id) {

        composersRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}