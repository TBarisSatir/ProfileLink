package io.github.tbarissatir.profilelink.repository;

import io.github.tbarissatir.profilelink.model.Profile;

import java.util.Optional;

public interface ProfileRepository {

    boolean existsById(String id);

    Profile save(Profile profile);

    Optional<Profile> findById(String id);
}