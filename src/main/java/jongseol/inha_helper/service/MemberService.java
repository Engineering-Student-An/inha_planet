package jongseol.inha_helper.service;

import jakarta.transaction.Transactional;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.domain.dto.IclassForm;
import jongseol.inha_helper.domain.dto.JoinRequest;
import jongseol.inha_helper.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberService{

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void join(JoinRequest joinRequest, String email, IclassForm iclassForm) {
        joinRequest.setPassword(bCryptPasswordEncoder.encode(joinRequest.getPassword()));

        memberRepository.save(joinRequest.toEntity(email, iclassForm));
    }

    @Transactional
    public void resetIclassInfo(Member member, IclassForm iclassForm) {
        member.setIclassInfo(iclassForm.getStuId(), iclassForm.getPassword());
    }

    @Transactional
    public void resetEmail(Member member, String email) {
        member.setEmail(email);
    }

    public boolean checkLoginIdDuplicate(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    public Member findMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    public boolean passwordCheck(Member member, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return encoder.matches(password, member.getPassword());
    }

    @Transactional
    public void resetPassword(Member member, String password) {

        Member updatedMember = Member.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .password(bCryptPasswordEncoder.encode(password))
                .stuId(member.getStuId())
                .iPassword(member.getIPassword())
                .email(member.getEmail()).build();

        memberRepository.save(updatedMember);
    }

    // 수강중인 과목 설정
    @Transactional
    public void setSubject(Member member, List<Long> subjectIds) {
        member.setSubjectList(subjectIds);
    }

    public String maskIPassword(Member member) {

        return "*".repeat(member.getIPassword().length());
    }
}
