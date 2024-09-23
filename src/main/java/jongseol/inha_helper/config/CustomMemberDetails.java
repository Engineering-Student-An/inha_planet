package jongseol.inha_helper.config;

import jongseol.inha_helper.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomMemberDetails implements UserDetails {
    private final Member member;


    public CustomMemberDetails(Member member) {
        this.member = member;
    }

    // 현재 user의 role을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "MEMBER";
            }
        });

        return collection;
    }

    // password 반환
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    // loginId 반환
    @Override
    public String getUsername() {
        return member.getLoginId();
    }


    public Member getMember() {
        return this.member;
    }
}
