package com.agilemonkeys.controller;

import com.agilemonkeys.domain.Role;
import com.agilemonkeys.domain.User;
import com.google.gson.Gson;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserManagerControllerIntegrationTest {

    private static String username = "user1";
    private static String username2 = "user2";
    private static String password = "welcome1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * CRUD successful operations.
     */
    @Test
    public void testCrud() throws Exception {
        insertNewAdminUser(username);

        Gson gson = new Gson();

        User user = getUser();
        String json = gson.toJson(user);

        User user2 = getUser2();
        String json2 = gson.toJson(user2);

        // Add new User.
        mockMvc.perform(post("/user")
                .content(json).with(httpBasic(username, password))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is("User airode successfully added.")));

        // Add another new user.
        mockMvc.perform(post("/user")
                .content(json2).with(httpBasic(username, password))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is("User airode2 successfully added.")));


        // Retrieve first added user by username. Assert values.
        mockMvc.perform(get("/user/airode")
                .contentType(APPLICATION_JSON)
                .with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.surname", is(user.getSurname())))
                .andExpect(jsonPath("$.role", is(user.getRole().name())));

        // Find all users.
        mockMvc.perform(get("/users")
                .with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(2))));

        // Update first user by updating its name.
        user.setName("Frank");
        user.setSurname("Adt");
        user.setRole(Role.USER);
        String updatedJson = gson.toJson(user);
        mockMvc.perform(put("/update-user")
                .content(updatedJson).with(httpBasic(username, password))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User airode successfully updated.")));

        // Get first user by its username to check name was changed.
        mockMvc.perform(get("/user/airode")
                .contentType(APPLICATION_JSON)
                .with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user.getName())));

        // Delete both added users.
        mockMvc.perform(delete("/user/airode")
                .contentType(APPLICATION_JSON)
                .with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User airode successfully deleted.")));

        mockMvc.perform(delete("/user/airode2")
                .contentType(APPLICATION_JSON)
                .with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User airode2 successfully deleted.")));

        deleteUser(username);
    }

    /**
     * Add new User with Null Username.
     * HTTP 400 Bad Request expected. Custom error message.
     */
    @Test
    public void testAddWithNullUsername() throws Exception {
        insertNewAdminUser(username);

        User user = getUser();
        user.setUsername(null);
        Gson gson = new Gson();
        String json = gson.toJson(user);

        mockMvc.perform(post("/user")
                .content(json).with(httpBasic(username, password))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Username must contain at least one non-whitespace character.")));

        deleteUser(username);
    }

    /**
     * Add new User with short Username.
     * HTTP 400 Bad request expected. Custom error message.
     */
    @Test
    public void testAddWitShortUsername() throws Exception {
        insertNewAdminUser(username);

        User user = getUser();
        user.setUsername("sd");
        Gson gson = new Gson();
        String json = gson.toJson(user);

        mockMvc.perform(post("/user")
                .content(json).with(httpBasic(username, password))
                .contentType(APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Username must have between 5 and 32 characters.")));

        deleteUser(username);
    }

    /**
     * Add new User with provided password.
     * HTTP 400 Bad request expected. Custom error message.
     */
    @Test
    public void testAddWithPasswordProvided() throws Exception {
        insertNewAdminUser(username);

        User user = getUser();
        user.setPassword("password");
        Gson gson = new Gson();
        String json = gson.toJson(user);

        mockMvc.perform(post("/user")
                .content(json).with(httpBasic(username, password))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Password is auto-generated.")));

        deleteUser(username);
    }

    /**
     * Add new User with Null Role.
     * HTTP 400 Bad request expected. Custom error message.
     */
    @Test
    public void testAddWithNullRole() throws Exception {
        insertNewAdminUser(username);

        User user = getUser();
        user.setRole(null);
        Gson gson = new Gson();
        String json = gson.toJson(user);

        mockMvc.perform(post("/user")
                .content(json).with(httpBasic(username, password))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Role must be provided.")));

        deleteUser(username);
    }

    /**
     * Add new User when User already exists.
     * HTTP 409 Conflict expected. Custom error message.
     */
    @Test
    public void testAddWithExistingUser() throws Exception {
        insertNewAdminUser(username);

        Gson gson = new Gson();
        User user = getUser();
        String json = gson.toJson(user);

        mockMvc.perform(post("/user")
                .content(json).with(httpBasic(username, password))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is("User airode successfully added.")));


        mockMvc.perform(post("/user")
                .content(json).with(httpBasic(username, password))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("User airode already exists.")));

        mockMvc.perform(delete("/user/airode")
                .contentType(APPLICATION_JSON)
                .with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User airode successfully deleted.")));

        deleteUser(username);
    }

    /**
     * Add new User Role different from defined one (ROLE, ADMIN).
     * HTTP 400 Bad request expected. Custom error message.
     */
    @Test(expected = NestedServletException.class)
    public void testAddWithNonExistingRole() throws Exception {
        insertNewAdminUser(username);

        String json = "{\"username\":\"airode\",\"name\":\"Rodrigo\", \"surname\":\"Doria\", \"role\":\"FAKE\"}";

        try{
            mockMvc.perform(post("/user")
                    .content(json).with(httpBasic(username, password))
                    .contentType(APPLICATION_JSON));
        } finally {
            deleteUser(username);

        }
    }

    /**
     * Find User by username when not found
     * HTTP 404 Not found expected. Custom error message.
     */
    @Test
    public void testFindByUsernameNotFound() throws Exception {
        insertNewAdminUser(username);

        mockMvc.perform(get("/user/fake-username")
                .contentType(APPLICATION_JSON)
                .with(httpBasic(username,password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("User with username fake-username not found.")));

        deleteUser(username);
    }

    /**
     * Update User when User is new
     * HTTP 404 Not found expected. Custom error message.
     */
    @Test
    public void testUpdateWithNewUser() throws Exception {
        insertNewAdminUser(username);

        Gson gson = new Gson();
        User user = getUser();
        String json = gson.toJson(user);

        mockMvc.perform(put("/update-user")
                .content(json).with(httpBasic(username,password))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Username airode doesn't exist in DB.")));

        deleteUser(username);
    }

    /**
     * Delete User when User not found.
     * HTTP 404 Not found expected. Custom error message.
     */
    @Test
    public void testDeleteByUsername() throws Exception {
        insertNewAdminUser(username);

        mockMvc.perform(delete("/user/fake-username")
                .contentType(APPLICATION_JSON)
                .with(httpBasic(username,password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Username fake-username doesn't exist in DB.")));

        deleteUser(username);
    }

    /**
     * Try any action when user has USER role and is not authorized.
     * HTTP 404 Not found expected. Custom error message.
     */
    @Test
    public void testAddWhenUserIsNotAdmin() throws Exception {
        insertNewUser(username2);

        Gson gson = new Gson();
        User user = getUser();
        String json = gson.toJson(user);

        // Add new User.
        mockMvc.perform(post("/user")
                .content(json).with(httpBasic(username2, password))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());

        deleteUser(username2);
    }

    private User getUser() {
        return User.builder()
                .username("airode")
                .name("John")
                .surname("Smith")
                .role(Role.ADMIN)
                .build();
    }

    private User getUser2() {
        return User.builder()
                .username("airode2")
                .name("Neatan")
                .surname("Olae")
                .role(Role.USER)
                .build();
    }

    private void insertNewAdminUser(String username) {
        jdbcTemplate.execute("INSERT INTO users(username, password, role) VALUES('" + username + "','$2a$10$AjHGc4x3Nez/p4ZpvFDWeO6FGxee/cVqj5KHHnHfuLnIOzC5ag4fm','ADMIN');");
    }

    private void insertNewUser(String username) {
        jdbcTemplate.execute("INSERT INTO users(username, password, role) VALUES('" + username + "','$2a$10$AjHGc4x3Nez/p4ZpvFDWeO6FGxee/cVqj5KHHnHfuLnIOzC5ag4fm','USER');");
    }

    private void deleteUser(String username) {
        jdbcTemplate.execute("DELETE FROM users where username='" + username + "';");
    }
}
