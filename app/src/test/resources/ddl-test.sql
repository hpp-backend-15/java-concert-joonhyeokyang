create table if not exists test.concerts
(
    concerts_id bigint auto_increment
        primary key,
    performer   varchar(255) null
);

create table if not exists test.performance_dates
(
    performance_dates_dates date   null,
    concerts_id             bigint null,
    performance_dates_id    bigint auto_increment
        primary key
);

create table if not exists test.queues
(
    entered_at        datetime(6)                        null,
    expire_at         datetime(6)                        null,
    id                bigint auto_increment
        primary key,
    issue_at          datetime(6)                        null,
    last_requested_at datetime(6)                        null,
    queues_user_id    bigint                             not null,
    queues_wait_id    varchar(255)                       not null,
    status            enum ('ACTIVE', 'EXPIRED', 'WAIT') null
);

create table if not exists test.reservations
(
    created_at           datetime(6)                             null,
    modified_at          datetime(6)                             null,
    reservations_id      bigint auto_increment
        primary key,
    reservations_seat_id bigint                                  not null,
    reservations_user_id bigint                                  not null,
    reservations_status  enum ('CANCELLED', 'PAYED', 'RESERVED') not null
);

create table if not exists test.seats
(
    version              int                                          null,
    last_reserved_at     datetime(6)                                  null,
    performance_dates_id bigint                                       null,
    seats_id             bigint auto_increment
        primary key,
    seats_price          bigint                                       null,
    seats_status         enum ('AVAILABLE', 'PENDING', 'UNAVAILABLE') null
);

create table if not exists test.users
(
    version                   int         not null,
    users_account_balance     bigint      null,
    users_account_modified_at datetime(6) null,
    users_id                  bigint auto_increment
        primary key
);

create table if not exists test.outbox_events
(
    outbox_events_relational_id bigint       null,
    outbox_events_type          varchar(255) null,
    outbox_events_status        varchar(255) null,
    outbox_events_created_at    datetime(6)  null,
    outbox_events_id            bigint auto_increment
        primary key
);

