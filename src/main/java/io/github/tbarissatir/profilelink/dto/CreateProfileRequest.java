package io.github.tbarissatir.profilelink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateProfileRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Headline is required")
    @Size(max = 160, message = "Headline must be at most 160 characters")
    private String headline;

    @NotEmpty(message = "At least one skill is required")
    @Size(max = 20, message = "A maximum of 20 skills is allowed")
    private List<
            @NotBlank(message = "Skills cannot be blank")
            @Size(max = 50, message = "Each skill must be at most 50 characters")
                    String
            > skills;

    public CreateProfileRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}