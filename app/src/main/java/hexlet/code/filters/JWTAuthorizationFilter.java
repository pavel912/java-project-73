package hexlet.code.filters;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hexlet.code.component.JWTHelper;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import static hexlet.code.configs.WebSecurityConfig.DEFAULT_AUTHORITIES;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

@SecurityScheme(type = SecuritySchemeType.APIKEY, name="BearerAuth")
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer";

    private final RequestMatcher publicUrls;
    private final JWTHelper jwtHelper;

    public JWTAuthorizationFilter(final RequestMatcher publicUrls,
                                  final JWTHelper jwtHelper) {
        this.publicUrls = publicUrls;
        this.jwtHelper = jwtHelper;
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return publicUrls.matches(request);
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {

        final UsernamePasswordAuthenticationToken authToken;

        try {
            authToken = Optional.ofNullable(request.getHeader(AUTHORIZATION))
                    .map(header -> header.replaceFirst("^" + BEARER, ""))
                    .map(String::trim)
                    .map(jwtHelper::verify)
                    .map(claims -> claims.get(SPRING_SECURITY_FORM_USERNAME_KEY))
                    .map(Object::toString)
                    .map(this::buildAuthToken)
                    .orElseThrow();

            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken buildAuthToken(final String username) {
        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                DEFAULT_AUTHORITIES
        );
    }
}
