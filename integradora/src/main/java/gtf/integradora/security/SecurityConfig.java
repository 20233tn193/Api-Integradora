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
import org.springframework.security.core.userdetails.UserDetailsService;
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
    public UserDetailsService userDetailsService() {
        return username -> {
            Usuario usuario = usuarioRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            return new User(usuario.getEmail(), usuario.getPassword(),
                    usuario.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
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

    @Bean
    @Profile("dev")
    public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());
        return http.build();
    }

    @Bean
    @Profile("prod")
    public SecurityFilterChain prodFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-ui.html",
                                "/webjars/**")
                        .permitAll()

                        // Rutas públicas
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/estadisticas/**").permitAll()
                        .requestMatchers("/api/partidos/torneo/**", "/api/partidos/calendario/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/partidos/completo/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/partidos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/torneos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/torneos/{id}").permitAll()

                        // Pagos públicos
                        .requestMatchers(HttpMethod.GET, "/api/pagos/detalles").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pagos/detalles/torneo/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pagos/detallados").hasAuthority("ADMIN")

                        // Pagos accesibles por DUEÑO
                        .requestMatchers("/api/pagos/dueno/**").hasAuthority("DUENO")

                        // 📄 Permitir DUENO descargar credenciales PDF
                        .requestMatchers(HttpMethod.GET, "/api/equipos/{equipoId}/credenciales")
                        .hasAnyAuthority("DUENO", "ADMIN")

                        // ✅ Rutas específicas permitidas para DUEÑO
                        .requestMatchers(HttpMethod.GET, "/api/equipos/dueño/**").hasAnyAuthority("DUENO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/equipos/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/equipos/torneo-con-dueno/**")
                        .hasAnyAuthority("DUENO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/jugadores/equipo/**")
                        .hasAnyAuthority("DUENO", "ARBITRO", "ADMIN")
                        .requestMatchers("/api/duenos/**").hasAnyAuthority("ADMIN", "DUENO")

                        // Pagos restringidos solo para ADMIN
                        .requestMatchers("/api/pagos/**").hasAuthority("ADMIN")

                        // Otras rutas solo para ADMIN
                        .requestMatchers("/api/usuarios/**").hasAuthority("ADMIN")

                // ✅ Permitir árbitro ver su propia info (DEBE IR ANTES)
                .requestMatchers(HttpMethod.GET, "/api/arbitros/usuario/**").hasAnyAuthority("ARBITRO", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/arbitros/{id}/partidos").hasAuthority("ADMIN")
                .requestMatchers("/api/arbitros/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET,"/api/campos/{id}").permitAll()
                .requestMatchers("/api/campos/**").hasAuthority("ADMIN")
                .requestMatchers("/api/torneos/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/partidos/generar-jornada/**").hasAuthority("ADMIN")

                        // ✅ Solo PUT /api/partidos/registrar-resultado/** para árbitros
                        .requestMatchers("/api/partidos/registrar-resultado/**").hasAuthority("ARBITRO")

                        // ❌ El resto de métodos en /api/partidos/** sí requiere ser árbitro
                        // ✅ Solo los métodos sensibles requieren autorización
                        .requestMatchers(HttpMethod.POST, "/api/partidos/**").hasAnyAuthority("ADMIN","ARBITRO")
                        .requestMatchers(HttpMethod.PUT, "/api/partidos/**").hasAuthority("ARBITRO")
                        .requestMatchers(HttpMethod.DELETE, "/api/partidos/**").hasAuthority("ADMIN")

                        // ⛔ El resto de /api/equipos/** solo para ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/equipos").hasAnyAuthority("DUENO", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/equipos/**").hasAnyAuthority("DUENO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/equipos/inscribir-existente").hasAuthority("DUENO")
                        .requestMatchers("/api/equipos/**").hasAuthority("ADMIN")

// Pagos restringidos solo para ADMIN
.requestMatchers("/api/pagos/**").hasAuthority("ADMIN")
                        // Otros
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil, usuarioRepository),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }

}
