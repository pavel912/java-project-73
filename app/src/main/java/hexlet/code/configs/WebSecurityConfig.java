package hexlet.code.configs;

import hexlet.code.component.JWTHelper;
import hexlet.code.filters.JWTAuthenticationFilter;
import hexlet.code.filters.JWTAuthorizationFilter;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String LOGIN = "/login";
    public static final List<GrantedAuthority> DEFAULT_AUTHORITIES = List.of(new SimpleGrantedAuthority("USER"));
    private static final String USER_CONTROLLER_PATH = "/users";

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsService userDetailsService;

    private final RequestMatcher publicUrls;
    private final RequestMatcher loginRequest;
    private final JWTHelper jwtHelper;

    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(@Value("${base-url}") final String baseUrl,
                          final UserDetailsService userDetailsService,
                             final PasswordEncoder passwordEncoder, final JWTHelper jwtHelper) {
        this.loginRequest = new AntPathRequestMatcher(baseUrl + LOGIN, POST.toString());
        this.publicUrls = new OrRequestMatcher(
                loginRequest,
                new AntPathRequestMatcher(baseUrl + USER_CONTROLLER_PATH, POST.toString()),
                new AntPathRequestMatcher(baseUrl + USER_CONTROLLER_PATH, GET.toString()),
                new NegatedRequestMatcher(new AntPathRequestMatcher(baseUrl + "/**"))
        );
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtHelper = jwtHelper;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final var authenticationFilter = new JWTAuthenticationFilter(
                authenticationManagerBean(),
                loginRequest,
                jwtHelper
        );

        final var authorizationFilter = new JWTAuthorizationFilter(
                publicUrls,
                jwtHelper
        );

        http = http.csrf().disable();

        http = http
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, authException) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    authException.getMessage()
                            );
                        }
                )
                .and();

        http.authorizeRequests()
                .antMatchers("/api/users").permitAll()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/welcome").permitAll()
                .anyRequest().authenticated();

        http
                .addFilter(authenticationFilter)
                .addFilterBefore(
                authorizationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

    }
}
