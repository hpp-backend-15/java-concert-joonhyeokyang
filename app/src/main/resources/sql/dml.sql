--
-- Data for Name: concert; Type: TABLE DATA; Schema: concert; Owner: postgres
--

INSERT INTO "concert".concert VALUES (1, 'joonhyeok');


--
-- Data for Name: performance_date; Type: TABLE DATA; Schema: concert; Owner: postgres
--

INSERT INTO "concert".performance_date VALUES ('2024-10-31', 1, 1);
INSERT INTO "concert".performance_date VALUES ('2024-11-01', 1, 2);
INSERT INTO "concert".performance_date VALUES ('2024-11-02', 1, 3);


--
-- Data for Name: queues; Type: TABLE DATA; Schema: concert; Owner: postgres
--

INSERT INTO "concert".queues VALUES (NULL, NULL, 1, '2024-10-31 21:26:19.371355', NULL, 1, '1', 'ACTIVE');


--
-- Data for Name: reservation; Type: TABLE DATA; Schema: concert; Owner: postgres
--



--
-- Data for Name: seat; Type: TABLE DATA; Schema: concert; Owner: postgres
--
-- performanceId 1: 1부터 33,333개
INSERT INTO "concert".seat (version, last_reserved_at, performance_dates_id, seats_id, seats_price, seats_status)
SELECT 0 AS version, CAST(NULL AS timestamp) AS last_reserved_at, 1 AS performanceId,
       generate_series(1, 33333) AS seats_id,
       0 AS price, 'AVAILABLE' AS status
UNION ALL

-- performanceId 2: 33,334부터 66,667개
SELECT 0 AS version, CAST(NULL AS timestamp) AS last_reserved_at, 2 AS performanceId,
       generate_series(33334, 66667) AS seats_id,
       0 AS price, 'AVAILABLE' AS status
UNION ALL

-- performanceId 3: 66,668부터 100,000개
SELECT 0 AS version, CAST(NULL AS timestamp) AS last_reserved_at, 3 AS performanceId,
       generate_series(66668, 100000) AS seats_id,
       0 AS price, 'AVAILABLE' AS status;




--
-- Data for Name: users; Type: TABLE DATA; Schema: concert; Owner: postgres
--

INSERT INTO "concert".users VALUES (0, 0, '2024-10-31 21:26:19.371344', 1);
