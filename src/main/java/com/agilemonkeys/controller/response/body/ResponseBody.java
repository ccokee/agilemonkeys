package com.agilemonkeys.controller.response.body;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class ResponseBody {
    Date timestamp;
    String message;
}
