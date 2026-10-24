package io.github.tbarissatir.profilelink.repository;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import io.github.tbarissatir.profilelink.model.Profile;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class JsonProfileRepository implements ProfileRepository {

    private final ObjectMapper objectMapper;
    private final File file;
    private final Map<String, Profile> profiles;

    public JsonProfileRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.file = new File("profiles.json");
        this.profiles = loadProfiles();
    }

    @Override
    public boolean existsById(String id) {
        return profiles.containsKey(id);
    }

    @Override
    public Profile save(Profile profile) {
        profiles.put(profile.getId(), profile);
        persistProfiles();
        return profile;
    }

    @Override
    public Optional<Profile> findById(String id) {
        return Optional.ofNullable(profiles.get(id));
    }

    private Map<String, Profile> loadProfiles() {
        if (!file.exists()) {
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(
                    file,
                    new TypeReference<>() {}
            );
        } catch (JacksonException e) {
            throw new IllegalStateException("Could not load profile data", e);
        }
    }

    private void persistProfiles() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, profiles);
        } catch (JacksonException e) {
            throw new IllegalStateException("Could not save profile data", e);
        }
    }
}