package com.gmg.global.config;

import com.gmg.global.exception.authenticationEntryPoint.CustomAuthenticationEntryPoint;
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

    public SecurityConfig(CorsConfig corsConfig) {
        this.corsConfig = corsConfig;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/test").permitAll()
                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        // Security 에서 걸린 애들 즉, authenticated()에 로그인을 안한 애들
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )

                .addFilterBefore(corsConfig.corsFilter(), ChannelProcessingFilter.class); // ChannelProcessingFilter 실행 전에 CORS 부터 검증

        return http.build();
    }

}
