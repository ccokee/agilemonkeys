package com.agilemonkeys.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class Customer {
    String id;
    String name;
    String surname;
    MultipartFile photo;
    String photoUrl;
    String createdBy;
    String lastModifiedBy;
    String email;
}
