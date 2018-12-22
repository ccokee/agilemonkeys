package com.agilemonkeys.controller.exception;

import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
public class ExceptionResponse {
    Date timestamp;
    String message;
    String details;
}
