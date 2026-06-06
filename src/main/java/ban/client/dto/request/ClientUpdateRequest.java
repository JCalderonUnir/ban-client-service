package ban.client.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientUpdateRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        String phone,
        String address
) {
}
