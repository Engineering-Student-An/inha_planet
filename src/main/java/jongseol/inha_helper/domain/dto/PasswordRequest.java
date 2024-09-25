package jongseol.inha_helper.domain.dto;

import jakarta.validation.constraints.Size;
import jongseol.inha_helper.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordRequest {

    private String currentPassword;

    @Size(min = 8, message = "8자 이상 비밀번호를 입력해주세요!")
    private String password;
    private String passwordCheck;

}
