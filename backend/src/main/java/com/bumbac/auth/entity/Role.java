package com.bumbac.auth.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.ManyToMany;

import java.util.Set;

public enum Role {
    USER,
    ADMIN,
    CONTENT_MANAGER;

    @JsonBackReference
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    }
