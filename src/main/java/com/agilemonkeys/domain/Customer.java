package com.agilemonkeys.domain;

import lombok.*;
import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Integer id;

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
}
