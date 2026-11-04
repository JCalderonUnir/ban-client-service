package ban.client.validation;

import ban.client.dto.request.ClientCreateRequest;
import ban.client.dto.request.ClientStatusRequest;
import ban.client.dto.request.ClientUpdateRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClientValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void createRequest_ShouldBeValid() {
        ClientCreateRequest request = new ClientCreateRequest(
                "CC",
                "123456789",
                "Juan Pérez",
                "juan@test.com",
                "3001234567",
                "Bogotá"
        );

        Set<ConstraintViolation<ClientCreateRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void createRequest_ShouldFail_WhenEmailIsInvalid() {
        ClientCreateRequest request = new ClientCreateRequest(
                "CC",
                "123456789",
                "Juan Pérez",
                "correo-mal",
                "3001234567",
                "Bogotá"
        );

        Set<ConstraintViolation<ClientCreateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void createRequest_ShouldFail_WhenFullNameIsBlank() {
        ClientCreateRequest request = new ClientCreateRequest(
                "CC",
                "123456789",
                "",
                "juan@test.com",
                "3001234567",
                "Bogotá"
        );

        Set<ConstraintViolation<ClientCreateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void updateRequest_ShouldFail_WhenEmailIsInvalid() {
        ClientUpdateRequest request = new ClientUpdateRequest(
                "Juan Pérez",
                "correo-mal",
                "3001234567",
                "Bogotá"
        );

        Set<ConstraintViolation<ClientUpdateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void statusRequest_ShouldFail_WhenStatusIsNull() {
        ClientStatusRequest request = new ClientStatusRequest(null);

        Set<ConstraintViolation<ClientStatusRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }
}