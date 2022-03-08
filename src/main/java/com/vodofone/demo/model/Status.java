package com.vodofone.demo.model;

import lombok.Getter;

public enum Status {
    ON("ON"),
    OFF("OFF"),
    NA("N/A");

    @Getter
    private final String name;

    Status(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
