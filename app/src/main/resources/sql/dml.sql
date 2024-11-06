--
-- Data for Name: concert; Type: TABLE DATA; Schema: concert-test; Owner: postgres
--

INSERT INTO concert VALUES (1, 'joonhyeok');


--
-- Data for Name: performance_date; Type: TABLE DATA; Schema: concert-test; Owner: postgres
--

INSERT INTO performance_date VALUES ('2024-10-31', 1, 1);
INSERT INTO performance_date VALUES ('2024-11-01', 1, 2);
INSERT INTO performance_date VALUES ('2024-11-02', 1, 3);


--
-- Data for Name: queues; Type: TABLE DATA; Schema: concert-test; Owner: postgres
--

INSERT INTO queues VALUES (NULL, NULL, 1, '2024-10-31 21:26:19.371355', NULL, 1, '1', 'ACTIVE');


--
-- Data for Name: reservation; Type: TABLE DATA; Schema: concert-test; Owner: postgres
--



--
-- Data for Name: seat; Type: TABLE DATA; Schema: concert-test; Owner: postgres
--
-- performanceId 1: 1부터 33,333개
DELIMITER $$

CREATE PROCEDURE loopInsert()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE performance_dates_id INT DEFAULT 1;

    WHILE i <= 3000 DO
        -- performance_dates_id 값 설정
        IF i <= 1000 THEN
            SET performance_dates_id = 1;
        ELSEIF i <= 2000 THEN
            SET performance_dates_id = 2;
ELSE
            SET performance_dates_id = 3;
END IF;

        -- 데이터 삽입
INSERT INTO concert.seat (version, last_reserved_at, performance_dates_id, seats_price, seats_status)
VALUES (0, NULL, performance_dates_id, 0, 'AVAILABLE');

-- 카운터 증가
SET i = i + 1;
END WHILE;
END $$

DELIMITER ;
--
-- Data for Name: users; Type: TABLE DATA; Schema: concert-test; Owner: postgres
--

INSERT INTO users VALUES (0, 0, '2024-10-31 21:26:19.371344', 1);

CALL loopInsert();