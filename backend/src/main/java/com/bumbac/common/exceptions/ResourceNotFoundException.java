package com.bumbac.common.exceptions;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String resourceName) {
        super(resourceName + " not found.");
    }
}
