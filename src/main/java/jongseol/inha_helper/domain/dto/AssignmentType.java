package jongseol.inha_helper.domain.dto;

import lombok.Getter;

@Getter
public enum AssignmentType {
    ASSIGNMENT("ASSIGNMENT"), VIDEO("VIDEO"), QUIZ("QUIZ");

    private final String displayName;

    AssignmentType(String displayName) {
        this.displayName = displayName;
    }
}
