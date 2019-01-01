package com.agilemonkeys.controller.responseBody;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponseBody {
    String id;
    String name;
    String surname;
    String photoUrl;
    String createdBy;
    String lastModifiedBy;
    String email;
}
