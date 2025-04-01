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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import gtf.integradora.entity.Usuario;
import gtf.integradora.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;
    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(JwtTokenUtil jwtTokenUtil, UsuarioRepository usuarioRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll() // Permite todo temporalmente
                )
                .formLogin(form -> form.disable()) // Desactiva login por formulario
                .logout(logout -> logout.disable()); // Desactiva logout también

        return http.build();
    }

    /*
     * Esto se debe de descomentar despues para poner la seguridad
     * 
     * @Bean
     * public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     * http.csrf().disable()
     * .authorizeHttpRequests(auth -> auth
     * .requestMatchers("/api/estadisticas/**").permitAll()
     * .requestMatchers("/api/partidos/**").hasRole("ARBITRO")
     * .anyRequest().authenticated())
     * .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil,
     * usuarioRepository),
     * UsernamePasswordAuthenticationFilter.class)
     * .formLogin(form -> form.permitAll()) // Aquí cierra formLogin correctamente
     * .logout(logout -> logout.permitAll()); // Aquí empieza logout por separado
     * 
     * return http.build();
     * }
     */

    // Configuración de AuthenticationManagerBuilder con userDetailsService
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(username -> {
            Usuario usuario = usuarioRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            return new User(usuario.getEmail(), usuario.getPassword(), usuario.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
        });
        return authenticationManagerBuilder.build();
    }
}