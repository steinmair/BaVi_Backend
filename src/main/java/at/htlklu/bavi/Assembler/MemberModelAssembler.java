package at.htlklu.bavi.Assembler;

import at.htlklu.bavi.controller.MemberController;
import at.htlklu.bavi.model.Member;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public
class MemberModelAssembler implements RepresentationModelAssembler<Member, EntityModel<Member>> {

    @Override
    public EntityModel<Member> toModel(Member member) {

        return EntityModel.of(member, //
                linkTo(methodOn(MemberController.class).one(member.getMemberId())).withSelfRel(),
                linkTo(methodOn(MemberController.class).all()).withRel("members"));
    }
}
