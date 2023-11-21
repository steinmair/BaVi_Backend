package at.htlklu.bavi.controller;

import at.htlklu.bavi.Assembler.InstrumentModelAssembler;
import at.htlklu.bavi.Assembler.MemberModelAssembler;
import at.htlklu.bavi.model.Instrument;
import at.htlklu.bavi.model.Member;
import at.htlklu.bavi.repository.InstrumentsRepository;
import at.htlklu.bavi.repository.MembersRepository;
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
@RequestMapping("/instruments")
public class InstrumentController {

    //https://spring.io/guides/tutorials/rest/
    private final InstrumentsRepository instrumentsRepository;
    private final InstrumentModelAssembler instrumentModelAssembler;

    private static final Logger logger = LogManager.getLogger(InstrumentController.class);

    public InstrumentController(InstrumentsRepository instrumentsRepository, InstrumentModelAssembler instrumentModelAssembler) {
        this.instrumentsRepository = instrumentsRepository;
        this.instrumentModelAssembler = instrumentModelAssembler;
    }

    @GetMapping("")
    public CollectionModel<EntityModel<Instrument>> all() {

        logger.info("/instruments all Method called");

        List<EntityModel<Instrument>> instruments = instrumentsRepository.findAll().stream() //
                .map(instrumentModelAssembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(instruments, linkTo(methodOn(InstrumentController.class).all()).withSelfRel());
    }

    @PostMapping("")
    ResponseEntity<?> newInstrument(@RequestBody Instrument newInstrument) {

        logger.info("/instruments newInstrument Method called");

        EntityModel<Instrument> entityModel = instrumentModelAssembler.toModel(instrumentsRepository.save(newInstrument));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    //Single Song
    @GetMapping("/{id}")
    public EntityModel<Instrument> one(@PathVariable Integer id){

        logger.info("/instruments/{id} one Method called");

        Instrument instrument = instrumentsRepository.findById(id).orElseThrow(() -> new NotFoundException("Instrument ("+ id + ")not found"));

        return instrumentModelAssembler.toModel(instrument);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replaceInstrument(@RequestBody Instrument newInstrument, @PathVariable Integer id){

        logger.info("/instruments/{id} replaceInstrument Method called");


        Instrument updatedInstrument = instrumentsRepository.findById(id) //
                .map(instrument -> {
                    instrument.setName(instrument.getName());
                    instrument.setCreatedBy(instrument.getCreatedBy());

                    return instrumentsRepository.save(instrument);
                }) //
                .orElseGet(() -> {
                    newInstrument.setInstrumentId(id);
                    return instrumentsRepository.save(newInstrument);
                });


        EntityModel<Instrument> entityModel = instrumentModelAssembler.toModel(updatedInstrument);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);

    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteInstrument(@PathVariable Integer id) {

        logger.info("/instruments/{id} deleteInstrument Method called");

        instrumentsRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}