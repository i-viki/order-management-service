package io.github.vikij.ordermanagement.auth.controller;

import io.github.vikij.ordermanagement.auth.dto.LoginRequest;
import io.github.vikij.ordermanagement.auth.dto.LoginResponse;
import io.github.vikij.ordermanagement.auth.dto.SignupRequest;
import io.github.vikij.ordermanagement.auth.jwt.JwtUtil;
import io.github.vikij.ordermanagement.common.exception.DuplicateResourceException;
import io.github.vikij.ordermanagement.user.entity.AppUser;
import io.github.vikij.ordermanagement.user.entity.Role;
import io.github.vikij.ordermanagement.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        log.info("Login attempt for username={}", request.getUsername());
        AppUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );
        log.info("Login successful for username={}", request.getUsername());
        return new LoginResponse(token);
    }

    @PostMapping("/signup")
    public void signup(@RequestBody SignupRequest request) {
        log.info("Signup attempt for username={}", request.getUsername());
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already exists");
        }

        AppUser user = new AppUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER
        );

        userRepository.save(user);
        log.info("User signed up successfully: {}", request.getUsername());
    }

}
