package gtf.integradora.security;

import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.web.SecurityFilterChain;
import gtf.integradora.entity.Usuario;
import gtf.integradora.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @SuppressWarnings("unused")
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

        // Este se tiene que COMENTAR despues de hacer las pruebas:

        // @SuppressWarnings("removal")

        // @Bean
        // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http.csrf().disable()
        // .authorizeHttpRequests(auth -> auth
        // .requestMatchers("/**").permitAll() // Permite todo temporalmente
        // )
        // .formLogin(form -> form.disable()) // Desactiva login por formulario
        // .logout(logout -> logout.disable()); // Desactiva logout también

        // return http.build();
        // }

        // Este se debe DESCOMENTAR despues de hacer las pruebas:

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.csrf().disable()
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/**").permitAll()
                                                .requestMatchers("/api/estadisticas/**").permitAll()
                                                .requestMatchers("/api/partidos/**").hasRole("ARBITRO")
                                                .requestMatchers("/api/duenos/**").hasRole("DUENO")
                                                .requestMatchers("/api/torneos/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil, usuarioRepository),
                                                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                                .formLogin(form -> form.disable())
                                .logout(logout -> logout.disable());

                return http.build();
        }

        // Configuración de AuthenticationManagerBuilder con userDetailsService
        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
                AuthenticationManagerBuilder authenticationManagerBuilder = http
                                .getSharedObject(AuthenticationManagerBuilder.class);
                authenticationManagerBuilder
                                .userDetailsService(username -> {
                                        Usuario usuario = usuarioRepository.findByEmail(username)
                                                        .orElseThrow(() -> new UsernameNotFoundException(
                                                                        "Usuario no encontrado"));
                                        return new User(usuario.getEmail(), usuario.getPassword(),
                                                        usuario.getRoles().stream()
                                                                        .map(SimpleGrantedAuthority::new)
                                                                        .collect(Collectors.toList()));
                                })
                                .passwordEncoder(passwordEncoder()); // 👉 Aquí se conecta el encoder
                return authenticationManagerBuilder.build();
        }
}