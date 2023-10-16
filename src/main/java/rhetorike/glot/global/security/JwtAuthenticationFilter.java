package rhetorike.glot.global.security;

import io.jsonwebtoken.Claims;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rhetorike.glot.domain._1auth.repository.blockedtoken.BlockedTokenRepository;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.global.error.exception.JwtBlockedException;
import rhetorike.glot.global.security.jwt.AccessToken;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final BlockedTokenRepository blockedTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(Header.AUTH);
        if (StringUtils.isNotEmpty(jwt)) {
            Claims claims = getClaims(jwt);
            Long userId = Long.parseLong(claims.getSubject());
            Authentication unauthenticatedToken = JwtAuthenticationToken.unauthenticated(userId);
            Authentication authenticatedToken = jwtAuthenticationProvider.authenticate(unauthenticatedToken);
            SecurityContextHolder.getContext().setAuthentication(authenticatedToken);
        }
        filterChain.doFilter(request, response);
    }

    private Claims getClaims(String jwt) {
        if (blockedTokenRepository.findByContent(jwt).isPresent()) {
            throw new JwtBlockedException();
        }
        return AccessToken.from(jwt).extractClaims();
    }
}
