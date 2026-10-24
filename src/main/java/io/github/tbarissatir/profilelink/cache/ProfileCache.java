package io.github.tbarissatir.profilelink.cache;

import io.github.tbarissatir.profilelink.model.Profile;

import java.util.Optional;

public interface ProfileCache {

    Optional<Profile> get(String id);

    void put(Profile profile);
}