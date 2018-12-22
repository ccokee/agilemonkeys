package com.agilemonkeys.controller.ResponseBody;

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
