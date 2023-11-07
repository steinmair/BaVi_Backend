package at.htlklu.bavi.repository;

import at.htlklu.bavi.model.Composer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComposersRepository extends JpaRepository<Composer, Integer>
{
}
