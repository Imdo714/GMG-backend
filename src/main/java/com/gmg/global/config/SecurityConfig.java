package com.gmg.global.config;

import com.gmg.api.member.service.MemberService;
import com.gmg.global.exception.authenticationEntryPoint.CustomAuthenticationEntryPoint;
import com.gmg.global.oauth.customHandler.CustomOAuth2UserService;
import com.gmg.global.oauth.customHandler.handler.CustomOAuth2FailureHandler;
import com.gmg.global.oauth.customHandler.handler.CustomSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CorsConfig corsConfig;
    private final MemberService memberService;
    public SecurityConfig(CorsConfig corsConfig, MemberService memberService) {
        this.corsConfig = corsConfig;
        this.memberService = memberService;
    }

    @Bean
    public CustomOAuth2UserService customOAuth2UserService() {
        return new CustomOAuth2UserService(memberService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        // Security 에서 걸린 애들 즉, authenticated()에 로그인을 안한 애들
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )

                // oauth2/authorization/google 로 접근시
                // SpringSecurity 가 이 URL 을 intercept 해서 Google OAuth2 로그인 화면으로 이동시킵니다.
                .oauth2Login(oauth -> oauth
                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(customOAuth2UserService())  // userInfoEndpoint 가 소셜로그인 정보를 가져와 userService 에서 커스텀 서비스 만들어서 사용자 정보 처리
                    )
                    .successHandler(new CustomSuccessHandler()) // 로그인 성공 시
                    .failureHandler(new CustomOAuth2FailureHandler()) // 로그인 실패 시
                )

                .addFilterBefore(corsConfig.corsFilter(), ChannelProcessingFilter.class); // ChannelProcessingFilter 실행 전에 CORS 부터 검증

        return http.build();
    }

}
