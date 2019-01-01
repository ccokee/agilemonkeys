package com.agilemonkeys.repository;

import com.agilemonkeys.domain.Customer;
import com.agilemonkeys.domain.Photo;
import com.agilemonkeys.exception.CustomerRepositoryException;
import com.agilemonkeys.repository.impl.CustomerRepositoryImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerRepositoryIntegrationTest {

    @Autowired
    private CustomerRepositoryImpl customerRepository;

    @Test
    public void testAddAndFindById() throws Exception{

        // Add new Customer.
        Customer customer = getCustomerWithPhoto();
        Customer savedCustomer = customerRepository.add(customer);
        assertNotNull(savedCustomer);

        // Checking the save operation generated id and photoUrl.
        assertNotNull(savedCustomer.getId());
        assertNotNull(savedCustomer.getPhotoUrl());

        // Check both Customer instances are the same if adding id and photoUrl to the Customer's request.
        customer.setId(savedCustomer.getId());
        customer.setPhotoUrl(savedCustomer.getPhotoUrl());
        assertEquals(customer, savedCustomer);

        // Test findById since we have the id available.
        Optional<Customer> foundCustomer = customerRepository.findById(savedCustomer.getId());
        assertEquals(savedCustomer.getId(), foundCustomer.get().getId());
        assertEquals(savedCustomer.getPhotoUrl(), foundCustomer.get().getPhotoUrl());

        // Delete customer.
        customerRepository.delete(savedCustomer.getId());
    }

    @Test
    public void testAddWithoutPhoto() {

        // Add new Customer without a Photo.
        Customer customer = getCustomerWithoutPhoto();
        Customer savedCustomer = customerRepository.add(customer);
        assertNotNull(savedCustomer);

        // Checking the save operation generated id and photoUrl is still null.
        assertNotNull(savedCustomer.getId());
        assertNull(savedCustomer.getPhotoUrl());

        // Check both Customer instances are the same if adding id and photoUrl to the Customer's request.
        customer.setId(savedCustomer.getId());
        assertEquals(customer, savedCustomer);

        // Delete customer
        customerRepository.delete(savedCustomer.getId());
    }

    @Test(expected = NullPointerException.class)
    public void testAddWithNullCustomer() {
        customerRepository.add(null);
    }

    @Test
    public void testFindByIdNotFound() {
        Optional<Customer> customer = customerRepository.findById("fake-id");
        assertEquals(Optional.empty(), customer);
    }

    @Test
    public void testFindAll() {
        // Add new customer
        Customer customer = getCustomerWithoutPhoto();
        Customer savedCustomer = customerRepository.add(customer);

        // Add another new customer
        Customer customer2 = getCustomerWithoutPhoto();
        Customer savedCustomer2 = customerRepository.add(customer2);

        // Check findAll will retrieve at least 2 customers (the 2 recently added, there could be more)
        List<Customer> customers = new ArrayList<>();
        customerRepository.findAll().forEach(customers::add);
        assertTrue(customers.size() >= 2);

        // Delete both customers
        customerRepository.delete(savedCustomer.getId());
        customerRepository.delete(savedCustomer2.getId());
    }

    @Test
    public void testUpdateOldCustomerWithPhotoAndUpdatedPhoto() throws Exception{
        // Add new customer with Photo
        Customer customer = getCustomerWithPhoto();
        Customer savedCustomer = customerRepository.add(customer);

        // Create new Photo
        Photo photo = createPhoto("image2.jpeg");
        savedCustomer.setName("Juan");
        savedCustomer.setPhoto(Optional.of(photo));

        // Update customer and verify it worked
        int updatedCustomer = customerRepository.update(savedCustomer);
        assertEquals(1, updatedCustomer);

        // Check updated customer has a new name and new photo
        Customer foundCustomer = customerRepository.findById(savedCustomer.getId()).get();
        assertEquals(savedCustomer.getName(), foundCustomer.getName());
        assertTrue(foundCustomer.getPhotoUrl().contains("jpeg"));

        // Delete Customer
        customerRepository.delete(savedCustomer.getId());
    }

    @Test
    public void testUpdateOldCustomerWithPhotoAndNewWithoutPhoto() throws Exception{
        // Add new Customer with Photo
        Customer customer = getCustomerWithPhoto();
        Customer savedCustomer = customerRepository.add(customer);

        // Update customer without a photo
        savedCustomer.setName("Juan");
        savedCustomer.setPhoto(Optional.empty());

        // Update customer and verify it worked
        int updatedCustomer = customerRepository.update(savedCustomer);
        assertEquals(1, updatedCustomer);

        // Check updated customer has a new name and no reference to any photo (since it was erased by the update).
        Customer foundCustomer = customerRepository.findById(savedCustomer.getId()).get();
        assertEquals(savedCustomer.getName(), foundCustomer.getName());
        assertNull(savedCustomer.getPhotoUrl());

        // Delete Customers
        customerRepository.delete(savedCustomer.getId());
    }

    @Test
    public void testUpdateOldCustomerWithoutPhotoAndNewPhoto() throws Exception{
        //Add new Customer without a photo
        Customer customer = getCustomerWithoutPhoto();
        Customer savedCustomer = customerRepository.add(customer);

        // Update customer with Photo.
        Photo photo = createPhoto("image2.jpeg");
        savedCustomer.setName("Juan");
        savedCustomer.setPhoto(Optional.of(photo));

        // Update customer and verify it worked
        int updatedCustomer = customerRepository.update(savedCustomer);
        assertEquals(1, updatedCustomer);

        // Check updated customer has a new name and now has a reference to a photoUrl.
        Customer foundCustomer = customerRepository.findById(savedCustomer.getId()).get();
        assertEquals(savedCustomer.getName(), foundCustomer.getName());
        assertTrue(foundCustomer.getPhotoUrl().contains("jpeg"));

        // Delete Customer
        customerRepository.delete(savedCustomer.getId());
    }

    @Test
    public void testUpdateOldCustomerWithoutPhotoAndNewWithoutPhoto() {
        // Add new Customer without photo
        Customer customer = getCustomerWithoutPhoto();
        Customer savedCustomer = customerRepository.add(customer);

        // Update Customer's name, still no photo added
        savedCustomer.setName("Juan");

        // Update customer and verify it worked
        int updatedCustomer = customerRepository.update(savedCustomer);
        assertEquals(1, updatedCustomer);

        // Check updated customer has a new name and still doesn't have a reference to photoUrl.
        Customer foundCustomer = customerRepository.findById(savedCustomer.getId()).get();
        assertEquals(savedCustomer.getName(), foundCustomer.getName());
        assertNull(foundCustomer.getPhotoUrl());

        // Delete Customer
        customerRepository.delete(savedCustomer.getId());
    }

    @Test(expected = CustomerRepositoryException.class)
    public void testUpdateWithNonExistentCustomer(){
        customerRepository.update(Customer.builder().id("fake-id").build());
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateNullCustomer(){
        customerRepository.update(null);
    }

    @Test(expected = CustomerRepositoryException.class)
    public void testDeleteNonExistentCustomer () {
        customerRepository.delete("fake-id");
    }
    private Customer getCustomerWithPhoto() throws Exception{
        Photo photo = createPhoto("image.png");
        return Customer.builder()
                .name("John")
                .surname("Smith")
                .createdBy("test-user")
                .lastModifiedBy("test-user")
                .email("test@gmail.com")
                .photo(Optional.of(photo))
                .build();
    }

    private Customer getCustomerWithoutPhoto() {
        return Customer.builder()
                .name("John")
                .surname("Smith")
                .createdBy("test-user")
                .lastModifiedBy("test-user")
                .email("test@gmail.com")
                .photo(Optional.empty())
                .build();
    }

    private Photo createPhoto(String fileName) throws Exception{
        String fileFormat = fileName.split("\\.")[1];

        File file = new File(getClass().getClassLoader().getResource(fileName).getFile());
        byte[] imageByteArray = Files.readAllBytes(file.toPath());

        return Photo.builder()
                .byteArray(imageByteArray)
                .fileFormat(fileFormat)
                .build();
    }
}
