package com.vodofone.demo.exception;

import lombok.Getter;

public enum ErrorCode {
    FILE_NOT_FOUND("FILE-NOT-FOUND"),
    FILE_PATH_MISSING("FILE-PATH-MISSING"),
    NO_DEVICE_FOUND("NO-DEVICE-FOUND"),
    UNABLE_TO_LOCATE_DEVICE("UNABLE-TO-LOCATE-DEVICE");

    @Getter
    private final String name;

    ErrorCode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
