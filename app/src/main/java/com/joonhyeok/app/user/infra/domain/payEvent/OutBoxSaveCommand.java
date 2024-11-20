package com.joonhyeok.app.user.infra.domain.payEvent;

public record OutBoxSaveCommand(
        String type,
        Long relationalId
) {
    public OutBox createOutBox() {
        return OutBox.issueOutBox(type, relationalId);
    }
}
