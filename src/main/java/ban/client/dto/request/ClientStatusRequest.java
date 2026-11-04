package ban.client.dto.request;

import ban.client.enums.ClientStatus;
import jakarta.validation.constraints.NotNull;

public record ClientStatusRequest(
        @NotNull(message = "El estado es obligatorio")
        Integer status
) {
}
