package ban.client.mapper;

import ban.client.dto.response.ClientResponse;
import ban.client.entity.Client;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClientMapperTest {

    private final ClientMapper clientMapper = new ClientMapper();

    @Test
    void toResponse_ShouldMapClientToClientResponse() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Client client = Client.builder()
                .id(id)
                .documentType("CC")
                .documentNumber("123456789")
                .fullName("Juan Pérez")
                .email("juan@test.com")
                .phone("3001234567")
                .address("Bogotá")
                .status(1)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        ClientResponse response = clientMapper.toResponse(client);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("CC", response.documentType());
        assertEquals("123456789", response.documentNumber());
        assertEquals("Juan Pérez", response.fullName());
        assertEquals("juan@test.com", response.email());
        assertEquals("3001234567", response.phone());
        assertEquals("Bogotá", response.address());
        assertEquals(1, response.status());
        assertEquals(createdAt, response.createdAt());
        assertEquals(updatedAt, response.updatedAt());
    }
}