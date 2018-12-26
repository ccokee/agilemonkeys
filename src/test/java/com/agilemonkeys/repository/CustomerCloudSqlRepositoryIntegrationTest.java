package com.agilemonkeys.repository;

import com.agilemonkeys.domain.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerCloudSqlRepositoryIntegrationTest {

    @Autowired
    CustomerCloudSqlRepository repository;

    @Test
    public void testCRUD() {
        // Save one Customer and find it by Id
        Customer customer = Customer.builder()
                .name("John")
                .surname("Smith")
                .createdBy("test-user")
                .lastModifiedBy("test-user")
                .photoUrl("www.google.com/test.png")
                .email("test@gmail.com")
                .build();
        Customer savedCustomer = repository.save(customer);
        assertNotNull(savedCustomer.getId());

        Optional<Customer> foundCustomer = repository.findById(savedCustomer.getId());
        assertEquals(savedCustomer.getId(), foundCustomer.get().getId());

        // Save a second Customer
        Customer customer2 = Customer.builder()
                .name("Peter")
                .surname("Smith")
                .createdBy("test-user")
                .lastModifiedBy("test-user")
                .photoUrl("www.google.com/test.png")
                .email("test@gmail.com")
                .build();
        Customer savedCustomer2 = repository.save(customer2);

        // Find All costumers (2 in total)
        List<Customer> customers = new ArrayList<>();
        repository.findAll().forEach(customers::add);

        assertEquals(2, customers.size());
        customers.clear();

        // Delete the first Customer
        repository.deleteById(foundCustomer.get().getId());

        // Delete all remaining costumers
        repository.deleteAll();
        repository.findAll().forEach(customers::add);
        assertEquals(0, customers.size());
    }
}
