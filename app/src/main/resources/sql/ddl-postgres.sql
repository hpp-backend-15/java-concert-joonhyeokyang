-- --
-- -- PostgreSQL database dump
-- --
--
-- -- Dumped from database version 16.4 (Homebrew)
-- -- Dumped by pg_dump version 16.4 (Homebrew)
--
-- SET statement_timeout = 0;
-- SET lock_timeout = 0;
-- SET idle_in_transaction_session_timeout = 0;
-- SET client_encoding = 'UTF8';
-- SET standard_conforming_strings = on;
-- SELECT pg_catalog.set_config('search_path', '', false);
-- SET check_function_bodies = false;
-- SET xmloption = content;
-- SET client_min_messages = warning;
-- SET row_security = off;
--
-- --
-- -- Name: concert-test; Type: SCHEMA; Schema: -; Owner: postgres
-- --
-- DROP SCHEMA IF EXISTS "concert-test" CASCADE;
-- CREATE SCHEMA "concert-test";
--
--
-- SET default_tablespace = '';
--
--
-- --
-- -- Name: concert; Type: TABLE; Schema: concert-test; Owner: postgres
-- --
--
-- CREATE TABLE "concert-test".concert (
--                                         concerts_id bigint NOT NULL,
--                                         performer character varying(255)
-- );
--
--
--
-- --
-- -- Name: concert_seq; Type: SEQUENCE; Schema: concert-test; Owner: postgres
-- --
--
-- CREATE SEQUENCE "concert-test".concert_seq
--     START WITH 1
--     INCREMENT BY 50
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
--
-- ALTER SEQUENCE "concert-test".concert_seq OWNER TO postgres;
--
-- --
-- -- Name: performance_date; Type: TABLE; Schema: concert-test; Owner: postgres
-- --
--
-- CREATE TABLE "concert-test".performance_date (
--                                                  performance_dates_dates date,
--                                                  concerts_id bigint,
--                                                  performance_dates_id bigint NOT NULL
-- );
--
--
-- ALTER TABLE "concert-test".performance_date OWNER TO postgres;
--
-- --
-- -- Name: performance_date_seq; Type: SEQUENCE; Schema: concert-test; Owner: postgres
-- --
--
-- CREATE SEQUENCE "concert-test".performance_date_seq
--     START WITH 1
--     INCREMENT BY 50
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
--
-- ALTER SEQUENCE "concert-test".performance_date_seq OWNER TO postgres;
--
-- --
-- -- Name: queues; Type: TABLE; Schema: concert-test; Owner: postgres
-- --
--
-- CREATE TABLE "concert-test".queues (
--                                        entered_at timestamp(6) without time zone,
--                                        expire_at timestamp(6) without time zone,
--                                        id bigint NOT NULL,
--                                        issue_at timestamp(6) without time zone,
--                                        last_requested_at timestamp(6) without time zone,
--                                        queues_user_id bigint NOT NULL,
--                                        queues_wait_id character varying(255) NOT NULL,
--                                        status character varying(255),
--                                        CONSTRAINT queues_status_check CHECK (((status)::text = ANY ((ARRAY['WAIT'::character varying, 'ACTIVE'::character varying, 'EXPIRED'::character varying])::text[])))
-- );
--
--
-- ALTER TABLE "concert-test".queues OWNER TO postgres;
--
-- --
-- -- Name: queues_seq; Type: SEQUENCE; Schema: concert-test; Owner: postgres
-- --
--
-- CREATE SEQUENCE "concert-test".queues_seq
--     START WITH 1
--     INCREMENT BY 50
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
--
-- ALTER SEQUENCE "concert-test".queues_seq OWNER TO postgres;
--
-- --
-- -- Name: reservation; Type: TABLE; Schema: concert-test; Owner: postgres
-- --
--
-- CREATE TABLE "concert-test".reservation (
--                                             created_at timestamp(6) without time zone,
--                                             modified_at timestamp(6) without time zone,
--                                             reservations_id bigint NOT NULL,
--                                             reservations_seat_id bigint NOT NULL,
--                                             reservations_user_id bigint NOT NULL,
--                                             reservations_status character varying(255) NOT NULL,
--                                             CONSTRAINT reservation_reservations_status_check CHECK (((reservations_status)::text = ANY ((ARRAY['RESERVED'::character varying, 'PAYED'::character varying, 'CANCELLED'::character varying])::text[])))
-- );
--
--
-- ALTER TABLE "concert-test".reservation OWNER TO postgres;
--
-- --
-- -- Name: reservation_seq; Type: SEQUENCE; Schema: concert-test; Owner: postgres
-- --
--
-- CREATE SEQUENCE "concert-test".reservation_seq
--     START WITH 1
--     INCREMENT BY 50
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
--
-- ALTER SEQUENCE "concert-test".reservation_seq OWNER TO postgres;
--
-- --
-- -- Name: seat; Type: TABLE; Schema: concert-test; Owner: postgres
-- --
--
-- CREATE TABLE "concert-test".seat (
--                                      version integer,
--                                      last_reserved_at timestamp(6) without time zone,
--                                      performance_dates_id bigint,
--                                      seats_id bigint NOT NULL,
--                                      seats_price bigint,
--                                      seats_status character varying(255),
--                                      CONSTRAINT seat_seats_status_check CHECK (((seats_status)::text = ANY ((ARRAY['AVAILABLE'::character varying, 'PENDING'::character varying, 'UNAVAILABLE'::character varying])::text[])))
-- );
--
--
-- ALTER TABLE "concert-test".seat OWNER TO postgres;
--
-- --
-- -- Name: seat_seq; Type: SEQUENCE; Schema: concert-test; Owner: postgres
-- --
--
-- CREATE SEQUENCE "concert-test".seat_seq
--     START WITH 1
--     INCREMENT BY 50
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
--
-- ALTER SEQUENCE "concert-test".seat_seq OWNER TO postgres;
--
-- --
-- -- Name: users; Type: TABLE; Schema: concert-test; Owner: postgres
-- --
--
-- CREATE TABLE "concert-test".users (
--                                       version integer NOT NULL,
--                                       users_account_balance bigint,
--                                       users_account_modified_at timestamp(6) without time zone,
--                                       users_id bigint NOT NULL
-- );
--
--
-- ALTER TABLE "concert-test".users OWNER TO postgres;
--
-- --
-- -- Name: users_seq; Type: SEQUENCE; Schema: concert-test; Owner: postgres
-- --
--
-- CREATE SEQUENCE "concert-test".users_seq
--     START WITH 1
--     INCREMENT BY 50
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
--
-- ALTER SEQUENCE "concert-test".users_seq OWNER TO postgres;
--
-- --
-- -- Data for Name: concert; Type: TABLE DATA; Schema: concert-test; Owner: postgres
-- --
--
--
--
-- --
-- -- Data for Name: performance_date; Type: TABLE DATA; Schema: concert-test; Owner: postgres
-- --
--
--
--
-- --
-- -- Data for Name: queues; Type: TABLE DATA; Schema: concert-test; Owner: postgres
-- --
--
--
--
-- --
-- -- Data for Name: reservation; Type: TABLE DATA; Schema: concert-test; Owner: postgres
-- --
--
--
--
-- --
-- -- Data for Name: seat; Type: TABLE DATA; Schema: concert-test; Owner: postgres
-- --
--
--
--
-- --
-- -- Data for Name: users; Type: TABLE DATA; Schema: concert-test; Owner: postgres
-- --
--
--
--
-- --
-- -- Name: concert_seq; Type: SEQUENCE SET; Schema: concert-test; Owner: postgres
-- --
--
-- SELECT pg_catalog.setval('"concert-test".concert_seq', 1, false);
--
--
-- --
-- -- Name: performance_date_seq; Type: SEQUENCE SET; Schema: concert-test; Owner: postgres
-- --
--
-- SELECT pg_catalog.setval('"concert-test".performance_date_seq', 1, false);
--
--
-- --
-- -- Name: queues_seq; Type: SEQUENCE SET; Schema: concert-test; Owner: postgres
-- --
--
-- SELECT pg_catalog.setval('"concert-test".queues_seq', 1, false);
--
--
-- --
-- -- Name: reservation_seq; Type: SEQUENCE SET; Schema: concert-test; Owner: postgres
-- --
--
-- SELECT pg_catalog.setval('"concert-test".reservation_seq', 1, false);
--
--
-- --
-- -- Name: seat_seq; Type: SEQUENCE SET; Schema: concert-test; Owner: postgres
-- --
--
-- SELECT pg_catalog.setval('"concert-test".seat_seq', 1, false);
--
--
-- --
-- -- Name: users_seq; Type: SEQUENCE SET; Schema: concert-test; Owner: postgres
-- --
--
-- SELECT pg_catalog.setval('"concert-test".users_seq', 1, false);
--
--
-- --
-- -- Name: concert concert_pkey; Type: CONSTRAINT; Schema: concert-test; Owner: postgres
-- --
--
-- ALTER TABLE ONLY "concert-test".concert
--     ADD CONSTRAINT concert_pkey PRIMARY KEY (concerts_id);
--
--
-- --
-- -- Name: performance_date performance_date_pkey; Type: CONSTRAINT; Schema: concert-test; Owner: postgres
-- --
--
-- ALTER TABLE ONLY "concert-test".performance_date
--     ADD CONSTRAINT performance_date_pkey PRIMARY KEY (performance_dates_id);
--
--
-- --
-- -- Name: queues queues_pkey; Type: CONSTRAINT; Schema: concert-test; Owner: postgres
-- --
--
-- ALTER TABLE ONLY "concert-test".queues
--     ADD CONSTRAINT queues_pkey PRIMARY KEY (id);
--
--
-- --
-- -- Name: reservation reservation_pkey; Type: CONSTRAINT; Schema: concert-test; Owner: postgres
-- --
--
-- ALTER TABLE ONLY "concert-test".reservation
--     ADD CONSTRAINT reservation_pkey PRIMARY KEY (reservations_id);
--
--
-- --
-- -- Name: seat seat_pkey; Type: CONSTRAINT; Schema: concert-test; Owner: postgres
-- --
--
-- ALTER TABLE ONLY "concert-test".seat
--     ADD CONSTRAINT seat_pkey PRIMARY KEY (seats_id);
--
--
-- --
-- -- Name: users users_pkey; Type: CONSTRAINT; Schema: concert-test; Owner: postgres
-- --
--
-- ALTER TABLE ONLY "concert-test".users
--     ADD CONSTRAINT users_pkey PRIMARY KEY (users_id);
--
--
-- --
-- -- PostgreSQL database dump complete
-- --
--
