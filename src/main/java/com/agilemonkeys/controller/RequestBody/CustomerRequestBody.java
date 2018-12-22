package com.agilemonkeys.controller.RequestBody;

import lombok.Builder;
import lombok.Data;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Data
@Entity
public class CustomerRequestBody {
    @NotBlank(message = "name must contain at least one non-whitespace character")
    String name;

    @NotBlank(message = "surname must contain at least one non-whitespace character")
    String surname;

    @Email(message = "must be a valid email address")
    String email;
}
