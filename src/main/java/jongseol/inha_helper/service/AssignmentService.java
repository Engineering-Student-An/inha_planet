package jongseol.inha_helper.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jongseol.inha_helper.domain.Assignment;
import jongseol.inha_helper.domain.Subject;
import jongseol.inha_helper.domain.dto.AssignmentRequestDto;
import jongseol.inha_helper.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AssignmentService {

    @PersistenceContext
    private final EntityManager em;

    private final AssignmentRepository assignmentRepository;
    private final SubjectService subjectService;

    public boolean notExistsByWebId(Long webId) {
        return !assignmentRepository.existsAllByWebId(webId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(AssignmentRequestDto requestDto) {
        try {
            Assignment assignment = Assignment.builder()
                    .webId(requestDto.getWebId())
                    .name(requestDto.getName())
                    .assignmentType(requestDto.getAssignmentType())
                    .deadline(requestDto.getDeadline())
                    .build();

            Subject subject = subjectService.findById(requestDto.getCourseId());
            assignment.setSubject(subject);

            assignmentRepository.save(assignment);
            em.clear();
        } catch (Exception e) {
            throw new RuntimeException("Assignment 저장 중 오류 발생", e);
        }
    }

    public List<Assignment> findByCourseId(Long courseId) {
        return assignmentRepository.findAssignmentsBySubject_Id(courseId);
    }

}

