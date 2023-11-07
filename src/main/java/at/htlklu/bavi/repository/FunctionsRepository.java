package at.htlklu.bavi.repository;

import at.htlklu.bavi.model.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionsRepository extends JpaRepository<Function, Integer>
{
}
