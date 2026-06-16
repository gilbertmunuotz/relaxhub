package com.relaxhub.backend.service;

import com.relaxhub.backend.dto.AuthResponse;
import com.relaxhub.backend.dto.LoginRequest;
import com.relaxhub.backend.dto.RegisterRequest;
import com.relaxhub.backend.dto.UserResponse;
import com.relaxhub.backend.entity.User;
import com.relaxhub.backend.exception.EmailAlreadyExistsException;
import com.relaxhub.backend.exception.InvalidCredentialsException;
import com.relaxhub.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());

        User savedUser = userRepository.save(user);
        return buildAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone()
        );
        return new AuthResponse(token, jwtService.getExpirationMs(), userResponse);
    }
}
