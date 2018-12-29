package com.agilemonkeys.service;

import com.agilemonkeys.domain.Role;
import com.agilemonkeys.domain.User;
import com.agilemonkeys.exception.UserNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserManagerServiceIntegrationTest {

    @Autowired
    private UserManagerService userManagerService;

    @Test
    public void testAddAndFindById() {
        User user = getUser1();

        User savedUser = userManagerService.add(user);
        assertNotNull(savedUser);

        // Check the add operation generated a random password
        assertNotNull(savedUser.getPassword());

        // Check both User instances are the same if adding password to the request.
        user.setPassword(savedUser.getPassword());
        assertEquals(user.getUsername(), savedUser.getUsername());
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getSurname(), savedUser.getSurname());
        assertEquals(user.getRole(), savedUser.getRole());
        assertEquals(user.getPassword(), savedUser.getPassword());

        // Test findByUsername since we have the username available.
        User foundUser = userManagerService.findByUsername(user.getUsername());
        assertEquals(savedUser.getUsername(), foundUser.getUsername().trim());
        assertEquals(savedUser.getName(), foundUser.getName());
        assertEquals(savedUser.getSurname(), foundUser.getSurname());
        assertEquals(savedUser.getRole(), foundUser.getRole());
        assertEquals(savedUser.getPassword(), foundUser.getPassword());

        userManagerService.deleteByUsername(user.getUsername());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testAddTwiceWithSameUsername() {
        User user = getUser1();
        User user2 = getUser1();

        userManagerService.add(user);
        try{
            userManagerService.add(user2);
        } finally {
            userManagerService.deleteByUsername(user.getUsername());
        }
    }

    @Test(expected = UserNotFoundException.class)
    public void testFindByUsernameNotFound() {
        userManagerService.findByUsername("fake-id");
    }

    @Test
    public void testFindAll() {
        User user = getUser2();
        User user2 = getUser3();

        userManagerService.add(user);
        userManagerService.add(user2);

        List<User> users = new ArrayList<>();
        userManagerService.findAll().forEach(users::add);

        assertTrue(users.size() >= 2);

        userManagerService.deleteByUsername(user.getUsername());
        userManagerService.deleteByUsername(user2.getUsername());
    }

    @Test
    public void testUpdate() {
        User user = getUser2();
        userManagerService.add(user);

        user.setName("Tain");
        user.setRole(Role.USER);

        User updatedUser = userManagerService.update(user);
        assertEquals(user.getName(), updatedUser.getName());
        assertEquals(user.getRole(), updatedUser.getRole());

        userManagerService.deleteByUsername(user.getUsername());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testUpdateWithNewUser() {
        User user = getUser2();
        userManagerService.update(user);
    }

    @Test(expected = UserNotFoundException.class)
    public void testDeleteByUsernameNotFound() {
        userManagerService.deleteByUsername("fake-id");
    }


    private User getUser1() {
        return User.builder()
                .username("airode")
                .name("John")
                .surname("Smith")
                .role(Role.ADMIN)
                .build();
    }

    private User getUser2() {
        return User.builder()
                .username("user1")
                .name("Ailton")
                .surname("Smith")
                .role(Role.USER)
                .build();
    }

    private User getUser3() {
        return User.builder()
                .username("user2")
                .name("Ailton")
                .surname("Smith")
                .role(Role.USER)
                .build();
    }
}
