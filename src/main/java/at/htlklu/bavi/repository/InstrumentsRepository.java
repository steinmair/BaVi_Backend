package at.htlklu.bavi.repository;

import at.htlklu.bavi.model.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstrumentsRepository extends JpaRepository<Instrument, Integer>
{
}
