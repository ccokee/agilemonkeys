package com.agilemonkeys.controller.validation;

import java.util.Arrays;
import java.util.List;

public class ValidationPhotoFile {

    public static final List<String> contentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif");
    public static final long maxSize = 5000000;
}
