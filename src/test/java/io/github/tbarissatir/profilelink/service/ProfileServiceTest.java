package io.github.tbarissatir.profilelink;

import io.github.tbarissatir.profilelink.cache.ProfileCache;
import io.github.tbarissatir.profilelink.dto.CreateProfileRequest;
import io.github.tbarissatir.profilelink.exception.ProfileNotFoundException;
import io.github.tbarissatir.profilelink.model.Profile;
import io.github.tbarissatir.profilelink.repository.ProfileRepository;
import io.github.tbarissatir.profilelink.service.ProfileService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProfileServiceTest {

    private FakeProfileRepository repository;
    private FakeProfileCache cache;
    private ProfileService service;

    @BeforeEach
    void setUp() {
        repository = new FakeProfileRepository();
        cache = new FakeProfileCache();
        service = new ProfileService(repository, cache);
    }

    @Test
    void createProfileShouldPersistAndCacheProfile() {
        CreateProfileRequest request = new CreateProfileRequest();
        request.setName("Turgut Barış Satır");
        request.setHeadline("Software Engineer");
        request.setSkills(List.of("Java", "Spring Boot"));

        Profile profile = service.createProfile(request);

        assertNotNull(profile.getId());
        assertEquals(16, profile.getId().length());
        assertEquals("Turgut Barış Satır", profile.getName());

        assertTrue(repository.findById(profile.getId()).isPresent());
        assertTrue(cache.get(profile.getId()).isPresent());
    }

    @Test
    void getProfileShouldReturnProfileFromCache() {
        Profile profile = new Profile(
                "abc12345",
                "Turgut",
                "Software Engineer",
                List.of("Java")
        );

        cache.put(profile);

        Profile result = service.getProfile("abc12345");

        assertEquals(profile, result);
        assertEquals(0, repository.findByIdCalls);
    }

    @Test
    void getProfileShouldFallBackToRepositoryAndPopulateCache() {
        Profile profile = new Profile(
                "abc12345",
                "Turgut",
                "Software Engineer",
                List.of("Java")
        );

        repository.save(profile);

        Profile result = service.getProfile("abc12345");

        assertEquals(profile, result);
        assertTrue(cache.get("abc12345").isPresent());
    }

    @Test
    void getProfileShouldThrowWhenProfileDoesNotExist() {
        assertThrows(
                ProfileNotFoundException.class,
                () -> service.getProfile("missing")
        );
    }

    private static class FakeProfileRepository implements ProfileRepository {

        private final Map<String, Profile> profiles = new HashMap<>();
        private int findByIdCalls = 0;

        @Override
        public boolean existsById(String id) {
            return profiles.containsKey(id);
        }

        @Override
        public Profile save(Profile profile) {
            profiles.put(profile.getId(), profile);
            return profile;
        }

        @Override
        public Optional<Profile> findById(String id) {
            findByIdCalls++;
            return Optional.ofNullable(profiles.get(id));
        }
    }

    private static class FakeProfileCache implements ProfileCache {

        private final Map<String, Profile> profiles = new HashMap<>();

        @Override
        public Optional<Profile> get(String id) {
            return Optional.ofNullable(profiles.get(id));
        }

        @Override
        public void put(Profile profile) {
            profiles.put(profile.getId(), profile);
        }
    }

    @Test
    void createProfileShouldTrimFieldsAndRemoveDuplicateSkills() {
        CreateProfileRequest request = new CreateProfileRequest();
        request.setName("  Turgut Barış Satır  ");
        request.setHeadline("  Software Engineer  ");
        request.setSkills(List.of(" Java ", "Spring Boot", "Java"));

        Profile profile = service.createProfile(request);

        assertEquals("Turgut Barış Satır", profile.getName());
        assertEquals("Software Engineer", profile.getHeadline());
        assertEquals(List.of("Java", "Spring Boot"), profile.getSkills());
    }
}