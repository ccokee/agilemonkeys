package com.agilemonkeys.service;

import com.agilemonkeys.domain.User;
import com.agilemonkeys.exception.UserNotFoundException;
import com.agilemonkeys.repository.UserRepository;
import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserManagerService {

    private static final Logger log = LoggerFactory.getLogger(UserManagerService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserManagerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User add(@NonNull User user) {
        log.info("Service received new User {} to be added.", user.toString());
        user.setPassword(passwordEncoder.encode(genereateRandomPassword()));
        user.setNew(true);
        return userRepository.save(user);
    }

    public User findByUsername(@NonNull String username) {
        log.info("Service retrieving User with username {}.", username);
        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent())
            throw new UserNotFoundException("User with username " + username + " not found.");

        return user.get();
    }

    public Iterable<User> findAll() {
        log.info("Service retrieving all users.");
        return userRepository.findAll();
    }

    public User update(@NonNull User user) {
        if(user.getUsername() == null)
            throw new UserNotFoundException("username must be provided in Request.");

        log.info("Service updating User {}", user.getUsername());
        user.setNew(false);
        return userRepository.save(user);
    }

    public void deleteByUsername(@NonNull String username) {
        log.info("Service deleting User with username {}.", username);
        int numberOfRowDeleted = userRepository.deleteByUsername(username);

        System.out.println(numberOfRowDeleted);

        if(numberOfRowDeleted == 0)
            throw new UserNotFoundException("Provided username doesn't exist in DB.");
    }

    /**
     * Generates Random password for User authentication. Since Admins create users, random generation is needed.
     * @return Generated Random password
     */
    private String genereateRandomPassword() {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(2);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(specialChar)
                .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        String password = pwdChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return password;
    }
}
