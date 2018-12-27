package com.agilemonkeys.domain;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.Optional;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    String id;

    @Column(name = "name")
    String name;

    @Column(name = "surname")
    String surname;

    @Column(name = "created_by")
    String createdBy;

    @Column(name = "last_modified_by")
    String lastModifiedBy;

    @Column(name = "photo_url")
    String photoUrl;

    @Column(name = "email")
    String email;

    // This byte array won't be persisted in the Relational Repo.
    // It's included in the domain model so it can be persisted in the File Storage Repository.
    @Transient
    Optional<Photo> photo;
}
