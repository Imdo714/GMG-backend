package com.gmg.global.oauth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmg.api.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isExcludedUrl(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = jwtTokenProvider.resolveToken(request);

            if (token != null) {
                if (jwtTokenProvider.validateToken(token)) {
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    throw new JwtException("유효하지 않은 JWT 토큰입니다.");
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.");
        } catch (JwtException e) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            setErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류 발생");
        }
    }

    private boolean isExcludedUrl(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        Set<String> urls = EXCLUDED_URLS.getOrDefault(method, Set.of());
        return urls.contains(uri);
    }

    private static final Map<String, Set<String>> EXCLUDED_URLS = Map.of(
            "POST", Set.of("/login", "/join")
    );

    // 에러는 넘겨주는 곳
    private void setErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Object> apiResponse = ApiResponse.of(status, message);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(apiResponse);

        response.getWriter().write(json);
    }
}
