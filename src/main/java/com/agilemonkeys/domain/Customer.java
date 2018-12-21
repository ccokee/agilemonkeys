package com.agilemonkeys.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Customer {
    String id;
    String name;
    String surname;
}
