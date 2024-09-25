package jongseol.inha_helper.repository;

import jongseol.inha_helper.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Subject findSubjectById(Long id);

}
