package at.htlklu.bavi.repository;

import at.htlklu.bavi.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublishersRepository extends JpaRepository<Publisher, Integer>
{
}
