package com.gmg.api.meeting.service;

import com.gmg.global.oauth.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MeetingServiceImplTest {

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("")
    @Test
    void test() {
        int res = 3 + 2;
        assertThat(res).isEqualTo(5);
    }

}