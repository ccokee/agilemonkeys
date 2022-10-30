package com.agilemonkeys.repository;

import com.agilemonkeys.domain.Role;
import com.agilemonkeys.domain.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findByUsername(String username);
    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.username=:username")
    int deleteByUsername(@Param("username") String username);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.name=:name, u.surname=:surname, u.role=:role WHERE u.username=:username")
    int update(@Param("name") String name,
                @Param("surname") String surname,
                @Param("role") Role role,
                @Param("username") String username);
}
