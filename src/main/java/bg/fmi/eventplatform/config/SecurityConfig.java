package bg.fmi.eventplatform.config;

import bg.fmi.eventplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.http.HttpClient;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_ORGANIZER = "ORGANIZER";
    private static final String ROLE_SPEAKER = "SPEAKER";

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers.addHeaderWriter(
                        new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/v3/api-docs", "/v3/api-docs/**", "/h2-console/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/events", "/events/*",
                                "/events/*/summary", "/events/*/agenda",
                                "/events/*/tickets", "/events/*/tickets/*",
                                "/events/*/feedback/summary", "/events/*/feedback/ai-summary").permitAll()
                        .requestMatchers(HttpMethod.GET, "/events/*/feedback").hasAnyRole(ROLE_ORGANIZER, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/speakers", "/speakers/*",
                                "/speakers/*/materials").permitAll()
                        .requestMatchers(HttpMethod.POST, "/events").hasAnyRole(ROLE_ORGANIZER, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/events/*").hasAnyRole(ROLE_ORGANIZER, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/events/*/status").hasAnyRole(ROLE_ORGANIZER, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/events/*").hasAnyRole(ROLE_ORGANIZER, ROLE_ADMIN)
                        .requestMatchers("/events/*/tickets/**").hasAnyRole(ROLE_ORGANIZER, ROLE_ADMIN)
                        .requestMatchers("/events/*/agenda/**").hasAnyRole(ROLE_ORGANIZER, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/speakers").hasAnyRole(ROLE_ORGANIZER, ROLE_SPEAKER, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/speakers/*").hasAnyRole(ROLE_ORGANIZER, ROLE_SPEAKER, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/speakers/*/materials").hasAnyRole(ROLE_ORGANIZER, ROLE_SPEAKER, ROLE_ADMIN)
                        .requestMatchers("/uploads/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users/me", "/users/me/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/users/*").hasRole(ROLE_ADMIN)
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigin));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
