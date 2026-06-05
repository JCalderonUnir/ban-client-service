package ban.client.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record ClientCreateRequest(
        @NotBlank String documentType,
        @NotBlank String documentNumber,
        @NotBlank String fullName,
        @Email @NotBlank String email,
        String phone,
        String address
) {
}
