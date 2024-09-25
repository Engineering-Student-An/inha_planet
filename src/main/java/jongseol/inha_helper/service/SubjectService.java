package jongseol.inha_helper.service;

import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.domain.Subject;
import jongseol.inha_helper.domain.dto.SubjectResponseDto;
import jongseol.inha_helper.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static jongseol.inha_helper.domain.dto.KoreanDay.getKoreanDayOfWeek;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Transactional
    public void save(Long id, String code, String name, String day, String hour) {

        String[] splitCode = code.split("_");
        String[] splitDay = day.split("<BR>");
        String[] splitHour = hour.split("<BR>");

        List<String> time = new ArrayList<>();
        for (int i = 0; i < splitDay.length; i++) {
            // 웹강 과목이 아닌 경우
            if(!splitDay[i].equals("WEB")) {
                String[] splitHour2 = splitHour[i].split("~");
                time.add(splitDay[i] + transformTime(Integer.parseInt(splitHour2[0])) + "~" + transformTime(Integer.parseInt(splitHour2[1])+1));
            }

            // 웹강 과목인 경우
            else {
                time.add("WEB");
                break;
            }
        }

        Subject subject = Subject.builder()
                .id(id)
                .name(name)
                .code(splitCode[2] + "-" + splitCode[3])    // 학수번호
                .time(time).build();

        subjectRepository.save(subject);
    }

    public boolean existsById(Long id) {
        return subjectRepository.existsById(id);
    }



    public LocalTime transformTime(int time) {

        // 9시 시작
        LocalTime baseTime = LocalTime.of(9, 0);

        return baseTime.plusMinutes((time - 1) * 30L);
    }

    public Subject findById(Long courseId) {
        return subjectRepository.findSubjectById(courseId);
    }


    public List<SubjectResponseDto> getTodaySubjects(Member member) {

        List<SubjectResponseDto> todaySubjects = new ArrayList<>();  // 오늘 강의 목록의 이름 리스트

        for (Long subjectId : member.getSubjectList()) {

            Subject subject = findById(subjectId);
            for (String time : subject.getTime()) {
                if(time.contains(getKoreanDayOfWeek(LocalDate.now().getDayOfWeek()))) {

                    // 요일을 제외하고 시간 부분만 추출하기 위해 정규 표현식 사용
                    String timePart = time.replaceAll("^[가-힣]+", "");

                    todaySubjects.add(new SubjectResponseDto(subject.getName(), timePart));
                }
            }

        }

        // 오늘 강의 시간 순 오름차순 정렬
        todaySubjects.sort(Comparator.comparing(SubjectResponseDto::getTime));

        return todaySubjects;
    }
}
