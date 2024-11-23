package com.joonhyeok.app.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joonhyeok.app.concert.ConcertTestHelper;
import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.ConcertRepository;
import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import com.joonhyeok.app.reservation.domain.ReservationStatus;
import com.joonhyeok.app.user.domain.user.Account;
import com.joonhyeok.app.user.domain.user.User;
import com.joonhyeok.app.user.domain.user.UserRepository;
import com.joonhyeok.openapi.models.RegisterQueueRequest;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/ddl-test.sql")
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class UserPayControllerIntegrateTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;


    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    private static final String BASE_URL = "/users/{userId}/reservations/{reservationId}/pay";


    @Test
    void 결제에_성공한다() throws Exception {
        //given
        Concert concertWithAvailableSeats = ConcertTestHelper.createConcertWithAvailableSeats();
        concertWithAvailableSeats.getAvailablePerformanceDates()
                .stream()
                .flatMap(dates -> dates.getSeatList().stream())
                .forEach(Seat::reserveSeat);

        concertRepository.save(concertWithAvailableSeats);


        userRepository.save(new User(1L, new Account(0L, null), 0));
        reservationRepository.save(new Reservation(ReservationStatus.RESERVED, 1L, 1L));


        //when
        ResultActions resultActions = mvc.perform(
                post(BASE_URL, 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterQueueRequest().userId(1L)))
        );

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value(1));

    }


}
