package com.agilemonkeys.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Photo {

    private byte[] byteArray;
    private String fileFormat;
}
