package team03.monew.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class MDCLoggingFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String requestId = UUID.randomUUID().toString();  // 요청 ID
    String ip = request.getRemoteAddr();  // IP 주소

    MDC.put("requestId", requestId);
    MDC.put("ip", ip);

    // 응답 헤더에 추가
    response.addHeader("X-Request-ID", requestId);
    response.addHeader("X-Client-IP", ip);

    try {
      filterChain.doFilter(request, response);
    } finally { // 자원 정리를 보장하기 위해 try/finally문 사용
      // MDC 값 제거
      MDC.clear();
    }
  }
}
