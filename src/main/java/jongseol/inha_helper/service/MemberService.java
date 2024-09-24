package jongseol.inha_helper.service;

import jakarta.transaction.Transactional;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.domain.dto.IclassForm;
import jongseol.inha_helper.domain.dto.JoinRequest;
import jongseol.inha_helper.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public Member findMemberByLoginId(String loginId) {
        return memberRepository.findMemberByLoginId(loginId);
    }

    public Member findMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }
}
