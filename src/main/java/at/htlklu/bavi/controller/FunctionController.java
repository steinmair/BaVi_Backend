package at.htlklu.bavi.controller;

import at.htlklu.bavi.Assembler.FunctionModelAssembler;
import at.htlklu.bavi.Assembler.MemberModelAssembler;
import at.htlklu.bavi.model.Function;
import at.htlklu.bavi.model.Member;
import at.htlklu.bavi.repository.FunctionsRepository;
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
public class FunctionController {

    //https://spring.io/guides/tutorials/rest/
    private final FunctionsRepository functionsRepository;
    private final FunctionModelAssembler functionModelAssembler;

    public FunctionController(FunctionsRepository functionsRepository, FunctionModelAssembler functionModelAssembler) {
        this.functionsRepository = functionsRepository;
        this.functionModelAssembler = functionModelAssembler;
    }

    @GetMapping("/functions")
    public CollectionModel<EntityModel<Function>> all() {

        List<EntityModel<Function>> functions = functionsRepository.findAll().stream() //
                .map(functionModelAssembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(functions, linkTo(methodOn(FunctionController.class).all()).withSelfRel());
    }

    @PostMapping("/functions")
    ResponseEntity<?> newFunction(@RequestBody Function newFunction) {

        EntityModel<Function> entityModel = functionModelAssembler.toModel(functionsRepository.save(newFunction));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    //Single Song
    @GetMapping("/functions/{id}")
    public EntityModel<Function> one(@PathVariable Integer id){

        Function function = functionsRepository.findById(id).orElseThrow(() -> new NotFoundException("Function ("+id + ")not found"));

        return functionModelAssembler.toModel(function);
    }

    @PutMapping("/functions/{id}")
    ResponseEntity<?> replaceFunction(@RequestBody Function newFunction, @PathVariable Integer id){


        Function updatedFunction = functionsRepository.findById(id) //
                .map(function -> {
                    function.setName(function.getName());
                    function.setCreatedBy(function.getCreatedBy());
                    return functionsRepository.save(function);
                }) //
                .orElseGet(() -> {
                    newFunction.setFunctionId(id);
                    return functionsRepository.save(newFunction);
                });


        EntityModel<Function> entityModel = functionModelAssembler.toModel(updatedFunction);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);

    }

    @DeleteMapping("/functions/{id}")
    ResponseEntity<?> deleteFunction(@PathVariable Integer id) {

        functionsRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}