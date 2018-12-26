package com.agilemonkeys.controller;

import com.agilemonkeys.controller.ResponseBody.CustomerResponseBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CustomerManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerManagerController customerManagerController;

    @Test
    public void getCostumers() throws Exception {
        CustomerResponseBody customerResponseBody = CustomerResponseBody.builder()
            .id(13)
            .name("John")
            .surname("Langley")
            .photoUrl("www.test/image.png")
            .createdBy("user1")
            .lastModifiedBy("user2")
            .email("test@gmail.com")
            .build();

        List<CustomerResponseBody> customerResponseBodies = singletonList(customerResponseBody);
        given(customerManagerController.getCostumers()).willReturn(ResponseEntity.ok(customerResponseBodies));

        mockMvc.perform(get("/customers")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(customerResponseBody.getId())))
                .andExpect(jsonPath("$[0].name", is(customerResponseBody.getName())))
                .andExpect(jsonPath("$[0].surname", is(customerResponseBody.getSurname())))
                .andExpect(jsonPath("$[0].photoUrl", is(customerResponseBody.getPhotoUrl())))
                .andExpect(jsonPath("$[0].createdBy", is(customerResponseBody.getCreatedBy())))
                .andExpect(jsonPath("$[0].lastModifiedBy", is(customerResponseBody.getLastModifiedBy())))
                .andExpect(jsonPath("$[0].email", is(customerResponseBody.getEmail())));
    }
}
