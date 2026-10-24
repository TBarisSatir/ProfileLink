package io.github.tbarissatir.profilelink.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import io.github.tbarissatir.profilelink.cache.ProfileCache;
import io.github.tbarissatir.profilelink.dto.CreateProfileRequest;
import io.github.tbarissatir.profilelink.model.Profile;
import io.github.tbarissatir.profilelink.repository.ProfileRepository;
import io.github.tbarissatir.profilelink.exception.ProfileNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;



@Service
public class ProfileService {

    private static final Logger logger =
            LoggerFactory.getLogger(ProfileService.class);

    private final ProfileRepository profileRepository;
    private final ProfileCache profileCache;

    public ProfileService(
            ProfileRepository profileRepository,
            ProfileCache profileCache
    ) {
        this.profileRepository = profileRepository;
        this.profileCache = profileCache;
    }

    public Profile createProfile(CreateProfileRequest request) {

        String id = generateUniqueId();

        List<String> normalizedSkills = request.getSkills()
                .stream()
                .map(String::trim)
                .filter(skill -> !skill.isBlank())
                .distinct()
                .toList();

        Profile profile = new Profile(
                id,
                request.getName().trim(),
                request.getHeadline().trim(),
                normalizedSkills
        );

        Profile savedProfile = profileRepository.save(profile);

        try {
            profileCache.put(savedProfile);
            logger.debug("Profile {} stored in cache after creation", savedProfile.getId());
        } catch (RuntimeException e) {
            logger.warn(
                    "Profile {} was persisted but could not be cached",
                    savedProfile.getId(),
                    e
            );
        }

        return savedProfile;
    }

    private String generateUniqueId() {
        String id;

        do {
            id = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 16);
        } while (profileRepository.existsById(id));

        return id;
    }

    public Profile getProfile(String id) {
        try {
            Optional<Profile> cachedProfile = profileCache.get(id);

            if (cachedProfile.isPresent()) {
                logger.debug("Cache hit for profile {}", id);
                return cachedProfile.get();
            }

            logger.debug("Cache miss for profile {}", id);

        } catch (RuntimeException e) {
            logger.warn(
                    "Cache lookup failed for profile {}, falling back to repository",
                    id,
                    e
            );
        }

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id));

        logger.debug("Profile {} loaded from repository", id);

        try {
            profileCache.put(profile);
            logger.debug("Profile {} stored in cache", id);
        } catch (RuntimeException e) {
            logger.warn(
                    "Profile {} was retrieved but could not be cached",
                    id,
                    e
            );
        }

        return profile;
    }

}