package at.htlklu.bavi.repository;

import at.htlklu.bavi.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembersRepository extends JpaRepository<Member, Integer> {

    Optional<Member> findByEmail(String email);
}
