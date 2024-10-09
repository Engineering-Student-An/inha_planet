package jongseol.inha_helper.repository;

import jongseol.inha_helper.domain.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    boolean existsAllByWebId(Long webId);

    List<Assignment> findAssignmentsBySubject_Id(Long subjectId);
}
