package at.htlklu.bavi.controller;

import at.htlklu.bavi.Assembler.MemberModelAssembler;
import at.htlklu.bavi.model.Member;
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
@RequestMapping("/members")
public class MemberController {

    //https://spring.io/guides/tutorials/rest/
    private final MembersRepository membersRepository;
    private final MemberModelAssembler memberModelAssembler;

    public MemberController(MembersRepository membersRepository, MemberModelAssembler memberModelAssembler) {
        this.membersRepository = membersRepository;
        this.memberModelAssembler = memberModelAssembler;
    }

    @GetMapping("")
    public CollectionModel<EntityModel<Member>> all() {

        List<EntityModel<Member>> members = membersRepository.findAll().stream() //
                .map(memberModelAssembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(members, linkTo(methodOn(MemberController.class).all()).withSelfRel());
    }

    @PostMapping("")
    ResponseEntity<?> newMember(@RequestBody Member newMember) {

        EntityModel<Member> entityModel = memberModelAssembler.toModel(membersRepository.save(newMember));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    //Single Song
    @GetMapping("/{id}")
    public EntityModel<Member> one(@PathVariable Integer id){

        Member member = membersRepository.findById(id).orElseThrow(() -> new NotFoundException("Member ("+ id + ") not found"));

        return memberModelAssembler.toModel(member);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replaceMember(@RequestBody Member newMember, @PathVariable Integer id){


        Member updatedMember = membersRepository.findById(id) //
                .map(member -> {
                    member.setFirstname(member.getFirstname());
                    member.setSurname(member.getSurname());
                    member.setBirthdate(member.getBirthdate());
                    member.setPhone(member.getPhone());
                    member.seteMail(member.geteMail());
                    member.setHouseNumber(member.getHouseNumber());
                    member.setDateJoined(member.getDateJoined());
                    member.setStreet(member.getStreet());
                    member.setZipCode(member.getZipCode());
                    member.setCity(member.getCity());
                    member.setCreatedBy(member.getCreatedBy());
                    return membersRepository.save(member);
                }) //
                .orElseGet(() -> {
                    newMember.setMemberId(id);
                    return membersRepository.save(newMember);
                });


        EntityModel<Member> entityModel = memberModelAssembler.toModel(updatedMember);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);

    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteMember(@PathVariable Integer id) {

        membersRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}