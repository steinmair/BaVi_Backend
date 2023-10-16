package at.htlklu.spring.repository;

import at.htlklu.spring.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolClassesRepository extends JpaRepository<SchoolClass, Integer>
{
}
