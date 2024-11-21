package com.joonhyeok.app.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joonhyeok.app.queue.domain.QueueRepository;
import com.joonhyeok.app.queue.domain.QueueStatus;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/ddl-test.sql")
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class QueueControllerIntegrateTest {

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    private static final String BASE_URL = "/queue";

    @Test
    void 대기열_등록에_성공한다() throws Exception {
        //given
        userRepository.save(new User(1L, new Account(0L, null), 0));

        //when
        ResultActions resultActions = mvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterQueueRequest().userId(1L)))
        );

        //then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(header().exists("Wait-Token"));

    }

    @Test
    void 존재하지_않는_유저는_대기열등록이_불가능하다() throws Exception {
        //given


        //when
        ResultActions resultActions = mvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterQueueRequest().userId(-1L)))
        );

        //then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    void 이미_대기하는_유저는_대기열등록이_불가능하다() throws Exception {
        //given
        userRepository.save(new User(1L, new Account(0L, null), 0));

        mvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterQueueRequest().userId(1L)))
        );

        //when
        ResultActions resultActions = mvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterQueueRequest().userId(1L)))
        );

        //then
        resultActions
                .andExpect(status().isConflict());
    }

    @Test
    void 대기열에_등록된_유저를_조회할수있다() throws Exception {
        //given
        userRepository.save(new User(1L, new Account(0L, null), 0));
        ResultActions registerResult = mvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterQueueRequest().userId(1L)))
        );

        String waitId = registerResult.andReturn().getResponse().getHeader("Wait-Token");


        //when
        ResultActions resultActions = mvc.perform(
                get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Wait-Token", waitId)
        );


        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.positionInQueue").value(1))
                .andExpect(jsonPath("$.waitId").value(waitId))
                .andExpect(jsonPath("$.status").value(QueueStatus.WAIT.name()));
    }

    @Test
    void 대기열에_ACTIVE된_유저를_조회하는경우_status는_ACTIVE() throws Exception {
        //given
        userRepository.save(new User(1L, new Account(0L, null), 0));
        ResultActions registerResult = mvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterQueueRequest().userId(1L)))
        );

        String waitId = registerResult.andReturn().getResponse().getHeader("Wait-Token");
        Thread.sleep(1200);


        //when
        ResultActions resultActions = mvc.perform(
                get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Wait-Token", waitId)
        );


        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.positionInQueue").value(0))
                .andExpect(jsonPath("$.waitId").value(waitId))
                .andExpect(jsonPath("$.status").value(QueueStatus.ACTIVE.name()));

    }


    @Test
    void 대기열에_존재하지않는_waitId를_조회할수없다() throws Exception {
        //given
        //when
        ResultActions resultActions = mvc.perform(
                get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Wait-Token", "invalidRandomWaitToken")
        );
        
        //then
        resultActions.andExpect(status().isBadRequest());
    }

}
