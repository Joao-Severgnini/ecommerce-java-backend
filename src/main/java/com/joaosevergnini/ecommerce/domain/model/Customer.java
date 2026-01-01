package com.joaosevergnini.ecommerce.domain.model;

import java.util.Objects;

public class Customer {
    private final Long id;
    private final String name;
    private final String email;

    // Constructor without id for new customers
    public Customer(String name, String email) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        Objects.requireNonNull(email, "email");
        if (email.isBlank()) throw new IllegalArgumentException("email must not be blank");

        this.id = null;
        this.name = name;
        this.email = email;
    }

    // Constructor with id for existing customers in DB
    public Customer(long id, String name, String email) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        Objects.requireNonNull(email, "email");
        if (email.isBlank()) throw new IllegalArgumentException("email must not be blank");

        this.id = id;
        this.name = name;
        this.email = email;
    }


    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
