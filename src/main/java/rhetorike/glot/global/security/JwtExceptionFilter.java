package rhetorike.glot.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rhetorike.glot.global.error.ErrorCode;
import rhetorike.glot.global.error.exception.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtBlockedException | JwtExpiredException | JwtWrongFormatException |
                 UserNotFoundException e) {
            setErrorResponse(response, e);
        }
    }

    public void setErrorResponse(HttpServletResponse response, GlotUncheckedException e) throws IOException {
        ErrorCode errorCode = e.getErrorCode();
        setResponseBody(response, errorCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorCode.getHttpStatus().value());
    }

    private void setResponseBody(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, Object> body = new HashMap<>();
        fillBody(response, errorCode, body);
        mapper.writeValue(response.getOutputStream(), body);
    }

    private void fillBody(HttpServletResponse response, ErrorCode errorCode, Map<String, Object> body) {
        body.put("status", errorCode.getHttpStatus().value());
        body.put("code", errorCode.getCode());
        body.put("name", errorCode.name());
        body.put("message", errorCode.getMessage());
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
