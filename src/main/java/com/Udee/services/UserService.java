package com.Udee.services;

import com.Udee.exceptions.notFound.UserNotFoundException;
import com.Udee.models.User;
import com.Udee.models.projections.UserProjection;
import com.Udee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser(User user) {
        user.setPass(passwordEncoder.encode(user.getPass()));
        return userRepository.save(user);
    }

    public Page<User> findAll(Specification<User> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }


    public UserProjection findProjectedById(Integer userId) {
        return userRepository.findProjectedById(userId).orElseThrow(UserNotFoundException::new);
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User login(String email, String pass) {
        User u = userRepository.findByEmail(email);
        return passwordEncoder.matches(pass, u.getPass()) ? u : null;
    }
}
