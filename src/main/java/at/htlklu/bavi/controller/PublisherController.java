package at.htlklu.bavi.controller;

import at.htlklu.bavi.Assembler.PublisherModelAssembler;
import at.htlklu.bavi.model.Publisher;
import at.htlklu.bavi.model.Song;
import at.htlklu.bavi.repository.PublishersRepository;
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
@RequestMapping("/publishers")
public class PublisherController {

    //https://spring.io/guides/tutorials/rest/
    private final PublishersRepository publishersRepository;
    private final PublisherModelAssembler publisherModelAssembler;

    public PublisherController(PublishersRepository publishersRepository, PublisherModelAssembler publisherModelAssembler) {
        this.publishersRepository = publishersRepository;
        this.publisherModelAssembler = publisherModelAssembler;
    }

    @GetMapping("")
    public CollectionModel<EntityModel<Publisher>> all() {

        List<EntityModel<Publisher>> publishers = publishersRepository.findAll().stream() //
                .map(publisherModelAssembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(publishers, linkTo(methodOn(PublisherController.class).all()).withSelfRel());
    }

    @PostMapping("")
    ResponseEntity<?> newPublisher(@RequestBody Publisher newPublisher) {

        EntityModel<Publisher> entityModel = publisherModelAssembler.toModel(publishersRepository.save(newPublisher));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    //Single Song
    @GetMapping("/{id}")
    public EntityModel<Publisher> one(@PathVariable Integer id){

        Publisher publisher = publishersRepository.findById(id).orElseThrow(() -> new NotFoundException("Publisher (" + id + ")not found"));

        return publisherModelAssembler.toModel(publisher);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replacePublisher(@RequestBody Publisher newPublisher, @PathVariable Integer id){


        Publisher updatedPublisher = publishersRepository.findById(id) //
                .map(publisher -> {
                    publisher.setName(newPublisher.getName());
                    publisher.setCreatedBy(newPublisher.getCreatedBy());
                    return publishersRepository.save(publisher);
                }) //
                .orElseGet(() -> {
                    newPublisher.setPublisherId(id);
                    return publishersRepository.save(newPublisher);
                });

        EntityModel<Publisher> entityModel = publisherModelAssembler.toModel(updatedPublisher);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);

    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deletePublisher(@PathVariable Integer id) {

        publishersRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}