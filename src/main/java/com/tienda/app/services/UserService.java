package com.tienda.app.services;

import com.tienda.app.dtos.auth.CheckTokenRequest;
import com.tienda.app.dtos.auth.LoginRequest;
import com.tienda.app.dtos.auth.LoginResponse;
import com.tienda.app.dtos.auth.RegisterRequest;
import com.tienda.app.models.Role;
import com.tienda.app.models.User;
import com.tienda.app.models.UserInfo;
import com.tienda.app.repositories.RoleRepository;
import com.tienda.app.repositories.UserInfoRepository;
import com.tienda.app.repositories.UserRepository;
import com.tienda.app.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       UserInfoRepository userInfoRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRole().getRoleName().name())
                        .build()
                ).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return this.userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    public void deleteUserById(Long id) {
        this.userRepository.deleteById(id);
    }

    @Transactional
    public User createUser(RegisterRequest userFromFront) {
        if (this.userRepository.existsByUsername(userFromFront.getUsername())) {
            throw new IllegalArgumentException("User already exists");
        }

        Role role = this.roleRepository.findByRoleName(userFromFront.getRoleName())
                .orElseThrow(() -> new IllegalArgumentException("Role no permitido"));

        User user = new User();
        user.setUsername(userFromFront.getUsername());
        user.setPassword(passwordEncoder.encode(userFromFront.getPassword()));
        user.setRole(role);
        user = this.userRepository.save(user);

        UserInfo userInfo = new UserInfo();
        userInfo.setUser(user);
        userInfo.setFirstName(userFromFront.getFirstName());
        userInfo.setLastName(userFromFront.getLastName());
        userInfo.setAddress(userFromFront.getAddress());

        this.userInfoRepository.save(userInfo);
        return user;
    }

    @Transactional
    public LoginResponse login(LoginRequest credentials) {
        User user = this.userRepository.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!this.passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        LoginResponse loginData = new LoginResponse();
        loginData.setUsername(credentials.getUsername());
        loginData.setRole(user.getRole().getRoleName());
        loginData.setToken(this.jwtUtil.generateToken(user.getUsername()));

        return loginData;
    }

    public boolean checkToken(CheckTokenRequest checkTokenRequest) {
        return this.jwtUtil.validateToken(
                checkTokenRequest.getToken(),
                checkTokenRequest.getUsername()
        );
    }

    public Optional<User> getUserProfile(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
}
