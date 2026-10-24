package io.github.tbarissatir.profilelink.controller;

import io.github.tbarissatir.profilelink.dto.CreateProfileRequest;
import io.github.tbarissatir.profilelink.model.Profile;
import io.github.tbarissatir.profilelink.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Profile createProfile(
            @Valid @RequestBody CreateProfileRequest request
    ) {
        return profileService.createProfile(request);
    }

    @GetMapping("/{id}")
    public Profile getProfile(@PathVariable String id) {
        return profileService.getProfile(id);
    }
}