package ban.client.mapper;

import ban.client.dto.response.ClientResponse;
import ban.client.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    public ClientResponse toResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .documentType(client.getDocumentType())
                .documentNumber(client.getDocumentNumber())
                .fullName(client.getFullName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .address(client.getAddress())
                .status(client.getStatus())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }
}
