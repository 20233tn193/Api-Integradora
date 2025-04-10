package gtf.integradora.security;

import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import gtf.integradora.entity.Usuario;
import gtf.integradora.repository.UsuarioRepository;

@Configuration
public class SecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;
    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(JwtTokenUtil jwtTokenUtil, UsuarioRepository usuarioRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(username -> {
            Usuario usuario = usuarioRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            return new User(usuario.getEmail(), usuario.getPassword(),
                    usuario.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        }).passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // 🔓 Configuración para desarrollo: todo permitido
    @Bean
    @Profile("dev")
    public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());
        return http.build();
    }

    // 🔒 Configuración para producción: roles aplicados
    @Bean
    @Profile("prod")
    public SecurityFilterChain prodFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        // Swagger público
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()

                        // Rutas públicas
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/estadisticas/**").permitAll()
                        .requestMatchers("/api/partidos/torneo/**", "/api/partidos/calendario/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/torneos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/torneos/{id}").permitAll()
                        .requestMatchers("/api/estadisticas/**").permitAll()


                        // ✅ Acceso público a los pagos detallados
                        .requestMatchers(HttpMethod.GET, "/api/pagos/detalles").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pagos/detalles/torneo/**").permitAll()

                        // ✅ Acceso restringido a lo demás de pagos
                        .requestMatchers("/api/pagos/**").hasAuthority("ADMIN")

                        // Rutas privadas por rol
                        .requestMatchers("/api/usuarios/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/arbitros/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/campos/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/torneos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/partidos/generar-jornada/**").hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/torneos/**").hasAnyAuthority("ADMIN", "DUENO")
                        
                        .requestMatchers("/api/partidos/**").hasAuthority("ARBITRO")
                        .requestMatchers("/api/duenos/**").hasAnyAuthority("ADMIN", "DUENO")
                        .requestMatchers("/api/partidos/registrar-resultado/**").hasAuthority("ARBITRO")
                        .requestMatchers("/api/duenos/**").hasAuthority("DUENO")

                        


                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil, usuarioRepository),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());
        return http.build();
    }
}