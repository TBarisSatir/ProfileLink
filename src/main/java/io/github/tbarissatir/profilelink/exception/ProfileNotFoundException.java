package io.github.tbarissatir.profilelink.exception;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String id) {
        super("Profile not found: " + id);
    }
}