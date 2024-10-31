--
-- Data for Name: concert; Type: TABLE DATA; Schema: concert-test; Owner: postgres
--

INSERT INTO "concert-test".concert VALUES (1, 'joonhyeok');


--
-- Data for Name: performance_date; Type: TABLE DATA; Schema: concert-test; Owner: postgres
--

INSERT INTO "concert-test".performance_date VALUES ('2024-10-31', 1, 1);
INSERT INTO "concert-test".performance_date VALUES ('2024-11-01', 1, 2);
INSERT INTO "concert-test".performance_date VALUES ('2024-11-02', 1, 3);


--
-- Data for Name: queues; Type: TABLE DATA; Schema: concert-test; Owner: postgres
--

INSERT INTO "concert-test".queues VALUES (NULL, NULL, 1, '2024-10-31 21:26:19.371355', NULL, 1, '1', 'ACTIVE');


--
-- Data for Name: reservation; Type: TABLE DATA; Schema: concert-test; Owner: postgres
--



--
-- Data for Name: seat; Type: TABLE DATA; Schema: concert-test; Owner: postgres
--
-- performanceId 1: 1부터 60,666개
INSERT INTO "concert-test".seat (version, last_reserved_at, performance_dates_id, seats_id, seats_price, seats_status)
SELECT 0 AS version, NULL AS modified, 1 AS performanceId,
       generate_series(1, 60666) AS seats_id,
       0 AS price, 'AVAILABLE' AS status
UNION ALL

-- performanceId 2: 60,667부터 80,000개
SELECT 0 AS version, NULL AS modified, 2 AS performanceId,
       generate_series(60667, 80000) AS seats_id,
       0 AS price, 'AVAILABLE' AS status
UNION ALL

-- performanceId 3: 80,001부터 100,000개
SELECT 0 AS version, NULL AS modified, 3 AS performanceId,
       generate_series(80001, 100000) AS seats_id,
       0 AS price, 'AVAILABLE' AS status;



--
-- Data for Name: users; Type: TABLE DATA; Schema: concert-test; Owner: postgres
--

INSERT INTO "concert-test".users VALUES (0, 0, '2024-10-31 21:26:19.371344', 1);
