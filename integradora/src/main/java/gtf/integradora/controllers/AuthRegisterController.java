package gtf.integradora.controllers;

import gtf.integradora.dto.RegistroDuenoRequest;
import gtf.integradora.entity.Usuario;
import gtf.integradora.entity.Dueno;
import gtf.integradora.repository.UsuarioRepository;
import gtf.integradora.repository.DuenoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import gtf.integradora.repository.ArbitroRepository;
import gtf.integradora.entity.Arbitro;
import gtf.integradora.dto.RegistroArbitroRequest;
import gtf.integradora.dto.RegistroAdministradorRequest;
import gtf.integradora.entity.Administrador;
import gtf.integradora.repository.AdministradorRepository;
import gtf.integradora.util.EncryptUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthRegisterController {

    private final UsuarioRepository usuarioRepository;
    private final DuenoRepository duenoRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ArbitroRepository arbitroRepository;
    private final AdministradorRepository administradorRepository;

    public AuthRegisterController(
            UsuarioRepository usuarioRepository,
            DuenoRepository duenoRepository,
            ArbitroRepository arbitroRepository,
            AdministradorRepository administradorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.duenoRepository = duenoRepository;
        this.arbitroRepository = arbitroRepository;
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/register/dueno")
    public ResponseEntity<?> registrarDueno(@RequestBody RegistroDuenoRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un usuario con ese correo");
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRoles(request.getRoles());
        usuario.setEliminado(false);
        usuario = usuarioRepository.save(usuario);

        // Crear dueño vinculado al usuario
        Dueno dueno = new Dueno();
        dueno.setNombre(request.getNombre());
        dueno.setApellido(request.getApellido());
        dueno.setIdUsuario(usuario.getId());
        dueno.setEliminado(false);

        Dueno guardado = duenoRepository.save(dueno);

        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PostMapping("/register/arbitro")
public ResponseEntity<?> registrarArbitro(@RequestBody RegistroArbitroRequest request) {
    if (usuarioRepository.findByEmail(request.getCorreo()).isPresent()) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un usuario con ese correo");
    }

    // Crear usuario
    Usuario usuario = new Usuario();
    usuario.setEmail(request.getCorreo());
    usuario.setPassword(passwordEncoder.encode(request.getPassword()));
    usuario.setRoles(request.getRoles());
    usuario.setEliminado(false);
    usuario = usuarioRepository.save(usuario);

    // Crear árbitro vinculado al usuario
    Arbitro arbitro = new Arbitro();
    arbitro.setNombre(request.getNombre());
    arbitro.setApellido(request.getApellido());
    arbitro.setIdUsuario(usuario.getId());
    arbitro.setEliminado(false);

    try {
        // 🔐 Encriptar la imagen (si viene incluida)
        if (request.getFotoUrl() != null && !request.getFotoUrl().isEmpty()) {
            String encryptedImage = EncryptUtil.encrypt(request.getFotoUrl());
            arbitro.setFotoUrl(encryptedImage);
        } else {
            arbitro.setFotoUrl(null);
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al encriptar la imagen del árbitro.");
    }

    Arbitro guardado = arbitroRepository.save(arbitro);

    return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
}

    @PostMapping("/register/admin")
    public ResponseEntity<?> registrarAdministrador(@RequestBody RegistroAdministradorRequest request) {
        if (usuarioRepository.findByEmail(request.getCorreo()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un usuario con ese correo");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getCorreo());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRoles(request.getRoles());
        usuario.setEliminado(false);
        usuario = usuarioRepository.save(usuario);

        Administrador admin = new Administrador();
        admin.setNombre(request.getNombre());
        admin.setIdUsuario(usuario.getId());
        admin.setEliminado(false);

        Administrador guardado = administradorRepository.save(admin);

        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }
}