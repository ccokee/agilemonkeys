package com.agilemonkeys.controller.response.error;

import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
public class ExceptionResponse {
    Date timestamp;
    String message;
}
