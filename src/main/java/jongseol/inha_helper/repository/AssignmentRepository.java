package jongseol.inha_helper.repository;

import jongseol.inha_helper.domain.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    boolean existsAllAssignmentByWebId(Long webId);

    Assignment findAllAssignmentByWebId(Long webId);
}
