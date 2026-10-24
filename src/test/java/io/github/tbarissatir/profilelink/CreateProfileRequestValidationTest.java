package io.github.tbarissatir.profilelink;

import io.github.tbarissatir.profilelink.dto.CreateProfileRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateProfileRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    @Test
    void blankNameShouldFailValidation() {
        CreateProfileRequest request = new CreateProfileRequest();
        request.setName("   ");
        request.setHeadline("Software Engineer");
        request.setSkills(List.of("Java"));

        Set<ConstraintViolation<CreateProfileRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("name"))
        );
    }

    @Test
    void blankSkillShouldFailValidation() {
        CreateProfileRequest request = new CreateProfileRequest();
        request.setName("Turgut");
        request.setHeadline("Software Engineer");
        request.setSkills(List.of("Java", "   "));

        Set<ConstraintViolation<CreateProfileRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream()
                        .anyMatch(v ->
                                v.getPropertyPath()
                                        .toString()
                                        .startsWith("skills"))
        );
    }

    @Test
    void moreThanTwentySkillsShouldFailValidation() {
        CreateProfileRequest request = new CreateProfileRequest();
        request.setName("Turgut");
        request.setHeadline("Software Engineer");
        request.setSkills(
                Collections.nCopies(21, "Java")
        );

        Set<ConstraintViolation<CreateProfileRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("skills"))
        );
    }

    @Test
    void validRequestShouldPassValidation() {
        CreateProfileRequest request = new CreateProfileRequest();
        request.setName("Turgut");
        request.setHeadline("Software Engineer");
        request.setSkills(List.of("Java", "Spring Boot"));

        Set<ConstraintViolation<CreateProfileRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}