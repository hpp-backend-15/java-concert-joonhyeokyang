```mermaid
erDiagram
    CONCERT {
        int id PK
        string performer
    }

    PERFORMANCE_DATE {
        int id PK
        date performance_date
        int concert_id
        list seats_id
    }

    SEAT {
        int id PK
        date last_reserved_at
        string status
    }

    USER {
        int id PK
    }
    
    ACCOUNT{
        int balance
        date modifiedAt
    }
    
    QUEUE{
        int id PK
        string sessionId
        string status
        date expireAt
        date issuedAt
        date lastRequestedAt
    }
    
    SESSION{
        string sessionId
     }
    
    PAYMENT{
        int id pk
        int amount
        string reservationId
        string userId
    }
    
    CHARGE_HISTORY{
        string id pk
        string accountId
        string userId
        string amount
    }

    RESERVATION {
        int id PK
        string status
        int user_id
        int seats_id
        int performance_date_id
        int performance_date
        date createdAt
        date modifiedAt
    }

    CONCERT ||--o{ PERFORMANCE_DATE: "has"
    PERFORMANCE_DATE ||--o{ SEAT: "allocates"
    USER ||--o{ RESERVATION: "makes"
    USER ||--o| QUEUE: "joins"
    USER ||--o| ACCOUNT: "has"
    USER ||--o| CHARGE_HISTORY: "has"
    USER ||--o{ PAYMENT: "makes"
    QUEUE ||--|| SESSION: "look up"
    PAYMENT ||--o{ RESERVATION: "for"
    RESERVATION ||--|| SEAT: "holds"
    PERFORMANCE_DATE ||--o{ RESERVATION: "reserves"
    
```