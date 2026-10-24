package io.github.tbarissatir.profilelink.cache;

import io.github.tbarissatir.profilelink.model.Profile;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JsonProfileCache implements ProfileCache {

    private static final Logger logger = LoggerFactory.getLogger(JsonProfileCache.class);

    private final ObjectMapper objectMapper;
    private final File file;
    private final Map<String, Profile> cache;

    public JsonProfileCache(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.file = new File("cache.json");
        this.cache = loadCache();
    }

    @Override
    public Optional<Profile> get(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public void put(Profile profile) {
        cache.put(profile.getId(), profile);
        persistCache();
    }

    private Map<String, Profile> loadCache() {
        if (!file.exists()) {
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(
                    file,
                    new TypeReference<>() {}
            );
        } catch (JacksonException e) {
            logger.warn(
                    "Could not load profile cache. Starting with an empty cache.",
                    e
            );

            return new HashMap<>();
        }
    }

    private void persistCache() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, cache);
        } catch (JacksonException e) {
            throw new IllegalStateException("Could not save profile cache", e);
        }
    }
}