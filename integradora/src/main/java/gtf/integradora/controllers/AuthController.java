package gtf.integradora.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import gtf.integradora.entity.LoginRequest;
import gtf.integradora.repository.UsuarioRepository;
import gtf.integradora.security.JwtTokenUtil;
import org.springframework.security.core.userdetails.User;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenUtil jwtTokenUtil;
    @SuppressWarnings("unused")
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;

    public AuthController(JwtTokenUtil jwtTokenUtil, UsuarioRepository usuarioRepository,
            AuthenticationManager authenticationManager) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();

                // 🔍 Buscamos el usuario en la base de datos por su email
                var usuarioOpt = usuarioRepository.findByEmail(user.getUsername());
                if (usuarioOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("message", "Usuario no encontrado"));
                }

                var usuario = usuarioOpt.get(); // ✅ Aquí ya tienes acceso al ID del usuario

                // 🔐 Generar el token con los roles
                String token = jwtTokenUtil.generarToken(
                        authentication.getName(),
                        user.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()));

                // 🧾 Armar respuesta con token, rol y usuarioId
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("rol", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")));
                response.put("usuarioId", usuario.getId()); // ✅ Ahora sí incluido

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Usuario inválido"));
            }

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciales inválidas"));
        }
    }
}