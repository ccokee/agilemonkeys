package com.agilemonkeys.repository.impl;

import com.agilemonkeys.domain.Customer;
import com.agilemonkeys.domain.Photo;
import com.agilemonkeys.exception.CustomerRepositoryException;
import com.agilemonkeys.exception.FileStorageException;
import com.agilemonkeys.repository.CustomerRelationalRepository;
import com.agilemonkeys.repository.CustomerRepository;
import com.agilemonkeys.repository.FileStorageRepository;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Qualifier("CustomerRepositoryImpl")
public class CustomerRepositoryImpl implements CustomerRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomerRepositoryImpl.class);

    @Autowired
    private CustomerRelationalRepository customerRelationalRepository;

    @Autowired
    private FileStorageRepository fileStorageRepository;

    public CustomerRepositoryImpl(
            CustomerRelationalRepository customerRelationalRepository,
            FileStorageRepository fileStorageRepository) {
        this.customerRelationalRepository = customerRelationalRepository;
        this.fileStorageRepository = fileStorageRepository;
    }

    @Override
    public Customer add(@NonNull Customer customer) throws FileStorageException {
        // We add generated id to Customer domain model.
        String id = UUID.randomUUID().toString();
        customer.setId(id);

        // We add generated photoUrl from fileStorageRepository to Customer domain model.
        String photoUrl = uploadPhoto(customer);
        customer.setPhotoUrl(photoUrl);

        try {
            customerRelationalRepository.save(customer);
            log.info("Customer {} relational's data has been persisted.", id);
        } catch (Exception e) {
            throw new CustomerRepositoryException(e);
        }
        return customer;
    }

    @Override
    public Optional<Customer> findById(@NonNull String id) {
        log.info("Finding Customer {} ...", id);
        try{
            return customerRelationalRepository.findById(id);
        } catch (Exception e) {
            throw new CustomerRepositoryException(e);
        }
    }

    @Override
    public Iterable<Customer> findAll() {
        log.info("Retrieving all customers...");
        try{
            return customerRelationalRepository.findAll();
        } catch (Exception e) {
            throw new CustomerRepositoryException(e);
        }
    }

    @Override
    public int update(@NonNull Customer customer) {
        log.info("Updating Customer {}...", customer.getId());
        Optional<Customer> existingCustomer = customerRelationalRepository.findById(customer.getId());
        Customer updatedCustomer = null;

        // We only update existing customers, otherwise we throw an exception.
        if(existingCustomer.isPresent()) {
            // If photo exists, we need to delete it first: Uploading a new file with the same name will replace the old
            // file but Customer might upload a new photo in a different format.
            deleteExistingPhoto(existingCustomer.get());

            String photoUrl = uploadPhoto(customer);
            customer.setPhotoUrl(photoUrl);

            try{
                return customerRelationalRepository.update(
                        customer.getName(),
                        customer.getSurname(),
                        customer.getLastModifiedBy(),
                        customer.getPhotoUrl(),
                        customer.getEmail(),
                        customer.getId());
            } catch (Exception e) {
                throw new CustomerRepositoryException(e);
            }
        } else {
            throw new CustomerRepositoryException("Provided Customer " + customer.getId() + " doesn't exist in Repository.");
        }
    }

    @Override
    public void delete(@NonNull String id){
        log.info("Deleting Customer {}...", id);
        Optional<Customer> existingCustomer = customerRelationalRepository.findById(id);

        if(existingCustomer.isPresent()) {
            deleteExistingPhoto(existingCustomer.get());

            try{
                customerRelationalRepository.deleteById(id);
            } catch (Exception e) {
                throw new CustomerRepositoryException(e);
            }
        } else {
            throw new CustomerRepositoryException("Customer " + id + " wasn't found.");
        }
    }

    /**
     * Deletes existing photo for persisted {@link Customer}.
     * @param existingCustomer Existing {@link Customer} instance.
     * @throws FileStorageException upon Failure in deletion.
     */
    private void deleteExistingPhoto(@NonNull Customer existingCustomer) {
        String existingPhotoUrl = existingCustomer.getPhotoUrl();

        if(existingPhotoUrl != null) {
            // We retrieve the photo full name from the photoUrl stored in the DB.
            // We could have persisted the file format in a different column in the DB to avoid this but it's done this way
            // to avoid redundancy of data.
            String existingPhotoFileName = existingPhotoUrl
                    .split("https://www.googleapis.com/download/storage/v1/b/crm-photos/o/")[1]
                    .split("\\?")[0];
            try {
                fileStorageRepository.deleteFile(existingPhotoFileName);
                log.info("Deleted old photo for Customer {}", existingCustomer.getId());
            } catch (FileStorageException e){
                throw new CustomerRepositoryException(e);
            }
        }
    }

    /**
     * Upload new photo for {@link Customer} in {@link FileStorageRepository}.
     * @param customer {@link Customer} instance.
     * @return photoUrl generated after uploading photo to {@link FileStorageRepository}.
     * @throws FileStorageException upon Failure in uploading operation.
     */
    private String uploadPhoto(@NonNull Customer customer) {
        String photoUrl = null;
        Optional<Photo> photo = customer.getPhoto();

        // We persist Photo in fileStorageRepository only if it was provided.
        if(photo.isPresent()) {
            log.info("Photo for Customer {} was provided and will be persisted.", customer.getId());
            try {
                photoUrl = fileStorageRepository.uploadFile(
                        photo.get().getByteArray(),
                        customer.getId() + "." + photo.get().getFileFormat());
                log.info("Photo for Customer {} has been persisted.", customer.getId());
            } catch (FileStorageException e) {
                throw new CustomerRepositoryException(e);
            }
        }
        return photoUrl;
    }
}
