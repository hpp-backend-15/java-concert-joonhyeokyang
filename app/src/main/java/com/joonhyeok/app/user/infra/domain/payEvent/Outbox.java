package com.joonhyeok.app.user.infra.domain.payEvent;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.joonhyeok.app.user.infra.domain.payEvent.OutboxStatus.*;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "outbox_events")
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Outbox {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "outbox_events_id")
    private Long id;

    @Column(name = "outbox_events_type")
    private String type;

    @Getter
    @Enumerated(STRING)
    @Column(name = "outbox_events_status")
    private OutboxStatus status;

    @Column(name = "outbox_events_created_at")
    private LocalDateTime createdAt;

    @Column(name = "outbox_events_relational_id")
    private Long relationalId;

    public static Outbox issueOutbox(String type, Long relationalId) {
        return new Outbox(null, type, INIT, LocalDateTime.now(), relationalId);
    }

    public void changeStatusToSuccess() {
        this.status = SEND_SUCCESS;
    }

    public void changeStatusToFail() {
        this.status = SEND_FAIL;
    }

}
