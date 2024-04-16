package com.example.apilabthird.security.service;

import com.example.apilabthird.DTO.auth.AuthenticationRequest;
import com.example.apilabthird.DTO.auth.AuthenticationResponse;
import com.example.apilabthird.DTO.auth.RegisterRequest;
import com.example.apilabthird.model.User;
import com.example.apilabthird.model.UserRole;
import com.example.apilabthird.repository.UserRepository;
import com.example.apilabthird.security.service.exception.UserAlreadyExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        log.debug("Register request decrypted: " + request);
        if (optionalUser.isPresent())
            throw new UserAlreadyExistException("User with username " + optionalUser.get().getUsername() + " already exists");

        UserRole role = UserRole.USER;
        if (request.getRole() != null && request.getRole().equals("admin"))
            role = UserRole.ADMIN;
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .userRole(role)
                .build();

        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        if (user.isEmpty())
            throw new UsernameNotFoundException("User with username " + request.getUsername() + " not found");
        String jwtToken = jwtService.generateToken(user.get());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
