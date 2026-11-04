package ban.client.dto.response;

import lombok.Builder;

import java.sql.ClientInfoStatus;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ClientResponse(
        UUID id,
        String documentType,
        String documentNumber,
        String fullName,
        String email,
        String phone,
        String address,
        Integer status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
