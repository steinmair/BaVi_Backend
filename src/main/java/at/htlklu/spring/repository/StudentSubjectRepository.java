package at.htlklu.spring.repository;

import at.htlklu.spring.model.StudentSubject;
import at.htlklu.spring.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Integer>
{
}
