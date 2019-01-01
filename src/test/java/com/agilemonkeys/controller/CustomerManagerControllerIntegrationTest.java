package com.agilemonkeys.controller;

import com.agilemonkeys.domain.Customer;
import com.google.gson.Gson;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerManagerControllerIntegrationTest {

    private static String username = "test-user";
    private static String username2 = "test-user-2";
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
        insertNewUser(username);
        insertNewAdmin(username2);

        Customer customer = getCustomer();
        MockMultipartFile costumer = getMultipartCustomer(customer);
        MockMultipartFile photo = getMultipartPhoto();

        // Add new Customer.
        MvcResult mvcResult = mockMvc.perform(multipart("/customer")
                .file(costumer)
                .file(photo).with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andReturn();

        // Add another new Customer.
        MvcResult mvcResult2 = mockMvc.perform(multipart("/customer")
                .file(costumer)
                .file(photo).with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andReturn();

        String id = mvcResult.getResponse().getContentAsString();
        String id2 = mvcResult2.getResponse().getContentAsString();

        // Retrieve first added Customer by username. Assert values.
        mockMvc.perform(get("/customer/" + id)
                .contentType(APPLICATION_JSON)
                .with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.surname", is("Mayer")))
                .andExpect(jsonPath("$.photoUrl", not(isEmptyString())))
                .andExpect(jsonPath("$.createdBy", is(username)))
                .andExpect(jsonPath("$.lastModifiedBy", is(username)))
                .andExpect(jsonPath("$.email", is("test@gmail.com")));

        // Find all customers.
        mockMvc.perform(get("/customers")
                .with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));


        // Update first added Customer.
        MockMultipartFile updatedCustomer = getMultipartUpdatedCustomer(id);
        mockMvc.perform(getUpdateBuilder()
                .file(updatedCustomer).with(httpBasic(username2, password)))
                .andExpect(status().isOk());

        // Get first user by its id and check everything was updated. Also We are login with another username o check
        // createdBy didn't change but lastModifiedBy did.
        mockMvc.perform(get("/customer/" + id)
                .contentType(APPLICATION_JSON)
                .with(httpBasic(username2, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.name", is("Tony")))
                .andExpect(jsonPath("$.surname", is("Smith")))
                .andExpect(jsonPath("$.photoUrl", is(nullValue())))
                .andExpect(jsonPath("$.createdBy", is(username)))
                .andExpect(jsonPath("$.lastModifiedBy", is(username2)))
                .andExpect(jsonPath("$.email", is("test-2@gmail.com")));

        // Delete both added users.
        mockMvc.perform(delete("/customer/" + id)
                .contentType(APPLICATION_JSON)
                .with(httpBasic(username2, password)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/customer/" + id2)
                .contentType(APPLICATION_JSON)
                .with(httpBasic(username2, password)))
                .andExpect(status().isOk());

        deleteUser(username);
        deleteUser(username2);
    }

    /**
     * Add new Customer with provided id.
     * HTTP 400 Bad request expected. Custom error message.
     */
    @Test
    public void testAddWithProvidedId() throws Exception {
        insertNewUser(username);

        Customer customer = getCustomer();
        customer.setId("fake-id");

        mockMvc.perform(multipart("/customer")
                .file(getMultipartCustomer(customer)).with(httpBasic(username, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("id is auto-generated.")));

        deleteUser(username);
    }

    /**
     * Add new Customer with provided createdBy.
     * HTTP 400 Bad request expected. Custom error message.
     */
    @Test
    public void testAddWithProvidedCreatedBy() throws Exception {
        insertNewUser(username);

        Customer customer = getCustomer();
        customer.setCreatedBy("fake-id");

        mockMvc.perform(multipart("/customer")
                .file(getMultipartCustomer(customer)).with(httpBasic(username, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("createdBy is generated by the system.")));

        deleteUser(username);
    }

    /**
     * Add new Customer with provided lastModifiedBy.
     * HTTP 400 Bad request expected. Custom error message.
     */
    @Test
    public void testAddWithProvidedLastModifiedBy() throws Exception {
        insertNewUser(username);

        Customer customer = getCustomer();
        customer.setLastModifiedBy("fake-id");

        mockMvc.perform(multipart("/customer")
                .file(getMultipartCustomer(customer)).with(httpBasic(username, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("lastModifiedBy is generated by the system.")));

        deleteUser(username);
    }

    /**
     * Add new Customer with provided photoUrl.
     * HTTP 400 Bad request expected. Custom error message.
     */
    @Test
    public void testAddWithProvidedPhotoUrl() throws Exception {
        insertNewUser(username);

        Customer customer = getCustomer();
        customer.setPhotoUrl("www.google.com/test.png");

        mockMvc.perform(multipart("/customer")
                .file(getMultipartCustomer(customer)).with(httpBasic(username, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("photoUrl is generated by the system.")));

        deleteUser(username);
    }

    /**
     * Add new Customer with provided photoUrl.
     * HTTP 400 Bad request expected. Custom error message.
     */
    @Test
    public void testAddWithWrongEmailFormat() throws Exception {
        insertNewUser(username);

        Customer customer = getCustomer();
        customer.setEmail("sdaw");

        mockMvc.perform(multipart("/customer")
                .file(getMultipartCustomer(customer)).with(httpBasic(username, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Email format not complaint.")));

        deleteUser(username);
    }

    /**
     * Add new Customer when providing a different type than a photo file.
     * HTTP 400 Bad request expected. Custom error message.
     */
    @Test
    public void testAddWithAnotherTypeOfFile() throws Exception {
        insertNewUser(username);

        mockMvc.perform(multipart("/customer")
                .file(getMultipartCustomer(getCustomer()))
                .file(getMultipartAnotherFile()).with(httpBasic(username, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Provided file must be either a PNG, GIF or JPEG and must be less than 5 MB.")));

        deleteUser(username);
    }

    /**
     * Find Customer by id when not found
     * HTTP 404 Not found expected. Custom error message.
     */
    @Test
    public void testFindByIdNotFound() throws Exception {
        insertNewUser(username);

        mockMvc.perform(get("/customer/fake-id")
                .contentType(APPLICATION_JSON)
                .with(httpBasic(username,password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Customer ID fake-id not found.")));

        deleteUser(username);
    }

    /**
     * Update Customer with New Customer
     * HTTP 404 not found expected. Custom error message.
     */
    @Test
    public void testUpdateWithNewCustomer() throws Exception {
        insertNewUser(username);

        MockMultipartFile updatedCustomer = getMultipartUpdatedCustomer("fake-id");
        mockMvc.perform(getUpdateBuilder()
                .file(updatedCustomer).with(httpBasic(username, password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Provided Customer ID fake-id doesn't exist in Repository.")));

        deleteUser(username);
    }

    /**
     * Delete Customer when Customer not found
     * HTTP 404 not found expected. Custom error message.
     */
    @Test
    public void testDeleteNotFound() throws Exception {
        insertNewUser(username);

        mockMvc.perform(delete("/customer/fake-id")
            .contentType(APPLICATION_JSON)
            .with(httpBasic(username, password)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Customer ID fake-id wasn't found.")));

        deleteUser(username);
    }

    private MockMultipartHttpServletRequestBuilder getUpdateBuilder() {
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/update-customer");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });
        return builder;
    }

    private Customer getCustomer() {
        return Customer.builder()
                .name("John")
                .surname("Mayer")
                .email("test@gmail.com")
                .build();
    }

    private MockMultipartFile getMultipartCustomer(Customer customer) throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(customer);
        return new MockMultipartFile("customer", "", "application/json", json.getBytes());
    }

    private MockMultipartFile getMultipartUpdatedCustomer(String id) throws Exception{
        Customer customer = Customer.builder()
                .id(id)
                .name("Tony")
                .surname("Smith")
                .email("test-2@gmail.com")
                .build();
        Gson gson = new Gson();
        String json = gson.toJson(customer);
        return new MockMultipartFile("customer", "", "application/json", json.getBytes());
    }

    private MockMultipartFile getMultipartPhoto() throws Exception{
        File photo = new File(getClass().getClassLoader().getResource("image.png").getFile());
        byte[] photoByteArray = Files.readAllBytes(photo.toPath());
        return new MockMultipartFile("photo", "image.png", "image/png", photoByteArray);
    }

    private MockMultipartFile getMultipartAnotherFile() throws Exception{
        File photo = new File(getClass().getClassLoader().getResource("application.properties").getFile());
        byte[] photoByteArray = Files.readAllBytes(photo.toPath());
        return new MockMultipartFile("photo", "application.properties", "text/plain", photoByteArray);
    }

    private void insertNewUser(String username) {
        jdbcTemplate.execute("INSERT INTO users(username, password, role) VALUES('" + username + "','$2a$10$AjHGc4x3Nez/p4ZpvFDWeO6FGxee/cVqj5KHHnHfuLnIOzC5ag4fm','USER');");
    }

    private void insertNewAdmin(String username) {
        jdbcTemplate.execute("INSERT INTO users(username, password, role) VALUES('" + username + "','$2a$10$AjHGc4x3Nez/p4ZpvFDWeO6FGxee/cVqj5KHHnHfuLnIOzC5ag4fm','ADMIN');");
    }

    private void deleteUser(String username) {
        jdbcTemplate.execute("DELETE FROM users where username='" + username + "';");
    }
}
