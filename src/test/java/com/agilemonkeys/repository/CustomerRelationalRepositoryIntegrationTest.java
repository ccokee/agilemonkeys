package com.agilemonkeys.repository;

import com.agilemonkeys.domain.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerRelationalRepositoryIntegrationTest {

    @Autowired
    CustomerRelationalRepository repository;

    @Test
    public void testCRUD() {
        // Save one Customer and find it by Id
        String id = UUID.randomUUID().toString();
        Customer customer = Customer.builder()
                .id(id)
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
        String id2 = UUID.randomUUID().toString();
        Customer customer2 = Customer.builder()
                .id(id2)
                .name("Peter")
                .surname("Smith")
                .createdBy("test-user")
                .lastModifiedBy("test-user")
                .photoUrl("www.google.com/test.png")
                .email("test@gmail.com")
                .build();
        repository.save(customer2);

        // Find All costumers (2 in total)
        List<Customer> customers = new ArrayList<>();
        repository.findAll().forEach(customers::add);

        assertEquals(2, customers.size());
        customers.clear();

        // Update name's on first Customer
        customer.setName("Juan");
        repository.save(customer);

        Optional<Customer> foundCustomerAfterChange = repository.findById(customer.getId());
        assertEquals("Juan", foundCustomerAfterChange.get().getName());

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

    @Test(expected = EmptyResultDataAccessException.class)
    public void testDeleteByIdNotFound (){
        repository.deleteById("fake-id");
    }

    @Test
    public void testFindByIdNotFound (){
        Optional<Customer> customer = repository.findById("fake-id");
        assertEquals(Optional.empty(), customer);
    }
}
