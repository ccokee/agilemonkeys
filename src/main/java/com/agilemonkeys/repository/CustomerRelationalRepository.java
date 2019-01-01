package com.agilemonkeys.repository;

import com.agilemonkeys.domain.Customer;
import com.agilemonkeys.domain.Role;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface CustomerRelationalRepository extends CrudRepository<Customer, String> {
    @Modifying
    @Transactional
    @Query("UPDATE Customer c SET c.name=:name, c.surname=:surname, c.lastModifiedBy=:lastModifiedBy, c.photoUrl=:photoUrl, c.email=:email WHERE c.id=:id")
    int update(@Param("name") String name,
               @Param("surname") String surname,
               @Param("lastModifiedBy") String lastModifiedBy,
               @Param("photoUrl") String photoUrl,
               @Param("email") String email,
               @Param("id") String id);
}
