package com.bumbac.core.exception;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String resourceName) {
        super(resourceName + " not found.");
    }
}
