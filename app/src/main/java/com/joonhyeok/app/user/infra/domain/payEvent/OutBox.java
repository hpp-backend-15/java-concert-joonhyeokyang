package com.joonhyeok.app.user.infra.domain.payEvent;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.joonhyeok.app.user.infra.domain.payEvent.OutBoxStatus.*;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "outbox_events")
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class OutBox {
    @Id
    private Long id;

    private String type;

    @Enumerated(STRING)
    private OutBoxStatus status;

    private LocalDateTime createdAt;

    private Long relationalId;

    public static OutBox issueOutBox(String type, Long relationalId) {
        return new OutBox(null, type, INIT, LocalDateTime.now(), relationalId);
    }

    public void changeStatusToSuccess() {
        this.status = SEND_SUCCESS;
    }

    public void changeStatusToFail() {
        this.status = SEND_FAIL;
    }
}
