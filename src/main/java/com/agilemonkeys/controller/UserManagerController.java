package com.agilemonkeys.controller;

import com.agilemonkeys.controller.ResponseBody.UserResponseBody;
import com.agilemonkeys.domain.User;
import com.agilemonkeys.mapper.UserMapper;
import com.agilemonkeys.service.UserManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class UserManagerController {

    private static final Logger log = LoggerFactory.getLogger(UserManagerController.class);

    private UserManagerService userManagerService;

    public UserManagerController(UserManagerService userManagerService) {
        this.userManagerService = userManagerService;
    }

    /**
     * Add new {@link User}.
     * @param user instance of {@link User} to be added.
     * @return {@link ResponseEntity} instance: Http response entity with empty body and Http Status code 200.
     */
    @Secured( {"ROLE_ADMIN"} )
    @PostMapping(
            value = "/user",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity add(@Validated({UserValidationGroup.Add.class}) @RequestBody User user) {
        log.info("Adding new User {}", user.getUsername());

        userManagerService.add(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Find {@link User} by Username.
     * @param username user name.
     * @return {@link ResponseEntity} instance: Http response entity with found {@link UserResponseBody} body and Http Status code 200.
     */
    @Secured( {"ROLE_ADMIN"} )
    @GetMapping(path = "/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseBody> findByUsername(@PathVariable("username") String username) {
        log.info("Retrieving User {}.", username);
        User user = userManagerService.findByUsername(username);

        UserResponseBody userResponseBody = UserMapper.userToUserReponseBody(user);
        return ResponseEntity.ok(userResponseBody);
    }

    /**
     * Find all {@link User} persisted.
     * @return {@link ResponseEntity} instance: Http response entity with found List of {@link UserResponseBody} body and Http Status code 200.
     */
    @Secured( {"ROLE_ADMIN"} )
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserResponseBody>> findAll() {
        log.info("Retrieving all users...");

        Iterable<User> users = userManagerService.findAll();

        List<UserResponseBody> userResponseBodies = StreamSupport.stream(users.spliterator(), false)
                .map(UserMapper::userToUserReponseBody)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userResponseBodies);
    }

    /**
     * Update {@link User}.
     * @param user instance of {@link User} to be updated.
     * @return {@link ResponseEntity} instance: Http response entity with empty body and Http Status code 200.
     */
    @Secured( {"ROLE_ADMIN"} )
    @PutMapping(
            value = "/update-user",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity update(@Validated({UserValidationGroup.Update.class}) @RequestBody User user) {
        log.info("Updating User {}", user.getUsername());

        userManagerService.update(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Delete {@link User}.
     * @param username user name.
     * @return {@link ResponseEntity} instance: Http response entity with empty body and Http Status code 200.
     */
    @Secured( {"ROLE_ADMIN"} )
    @DeleteMapping(path = "/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteByUsername(@PathVariable("username") String username){
        log.info("Deleting User {}", username);

        userManagerService.deleteByUsername(username);
        return new ResponseEntity(HttpStatus.OK);
    }
}
