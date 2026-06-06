package ban.client.service.impl;

import ban.client.dto.request.ClientCreateRequest;
import ban.client.dto.request.ClientStatusRequest;
import ban.client.dto.request.ClientUpdateRequest;
import ban.client.dto.response.ClientResponse;
import ban.client.entity.Client;
import ban.client.mapper.ClientMapper;
import ban.client.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void create_ShouldSaveClientAndReturnResponse() {
        ClientCreateRequest request = new ClientCreateRequest(
                "CC",
                "123456789",
                "Juan Pérez",
                "3001234567",
                "juan@test.com",
                "Bogotá"
        );

        Client savedClient = Client.builder()
                .id(UUID.randomUUID())
                .documentType("CC")
                .documentNumber("123456789")
                .fullName("Juan Pérez")
                .phone("3001234567")
                .email("juan@test.com")
                .address("Bogotá")
                .status(1)
                .build();

        ClientResponse response = ClientResponse.builder()
                .id(savedClient.getId())
                .documentType("CC")
                .documentNumber("123456789")
                .fullName("Juan Pérez")
                .phone("3001234567")
                .email("juan@test.com")
                .address("Bogotá")
                .status(1)
                .build();

        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);
        when(clientMapper.toResponse(savedClient)).thenReturn(response);

        ClientResponse result = clientService.create(request);

        assertNotNull(result);
        assertEquals("Juan Pérez", result.fullName());
        assertEquals("juan@test.com", result.email());

        verify(clientRepository).save(any(Client.class));
        verify(clientMapper).toResponse(savedClient);
    }

    @Test
    void findById_ShouldReturnClient_WhenClientExists() {
        UUID id = UUID.randomUUID();

        Client client = Client.builder()
                .id(id)
                .fullName("Juan Pérez")
                .email("juan@test.com")
                .build();

        ClientResponse response = ClientResponse.builder()
                .id(id)
                .fullName("Juan Pérez")
                .email("juan@test.com")
                .build();

        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        when(clientMapper.toResponse(client)).thenReturn(response);

        ClientResponse result = clientService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Juan Pérez", result.fullName());

        verify(clientRepository).findById(id);
    }

    @Test
    void findById_ShouldThrowException_WhenClientDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(clientRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> clientService.findById(id)
        );

        assertEquals("Cliente no encontrado", exception.getMessage());

        verify(clientRepository).findById(id);
        verify(clientMapper, never()).toResponse(any());
    }

    @Test
    void update_ShouldUpdateClientAndReturnResponse() {
        UUID id = UUID.randomUUID();

        ClientUpdateRequest request = new ClientUpdateRequest(
                "Juan Actualizado",
                "juan.new@test.com",
                "3111111111",
                "Medellín"
        );

        Client client = Client.builder()
                .id(id)
                .fullName("Juan Pérez")
                .email("juan@test.com")
                .phone("3001234567")
                .address("Bogotá")
                .build();

        Client updatedClient = Client.builder()
                .id(id)
                .fullName("Juan Actualizado")
                .email("juan.new@test.com")
                .phone("3111111111")
                .address("Medellín")
                .build();

        ClientResponse response = ClientResponse.builder()
                .id(id)
                .fullName("Juan Actualizado")
                .email("juan.new@test.com")
                .phone("3111111111")
                .address("Medellín")
                .build();

        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(updatedClient);
        when(clientMapper.toResponse(updatedClient)).thenReturn(response);

        ClientResponse result = clientService.update(id, request);

        assertNotNull(result);
        assertEquals("Juan Actualizado", result.fullName());
        assertEquals("juan.new@test.com", result.email());

        verify(clientRepository).findById(id);
        verify(clientRepository).save(client);
    }

    @Test
    void changeStatus_ShouldUpdateClientStatus() {
        UUID id = UUID.randomUUID();

        ClientStatusRequest request = new ClientStatusRequest(0);

        Client client = Client.builder()
                .id(id)
                .status(1)
                .build();

        when(clientRepository.findById(id)).thenReturn(Optional.of(client));

        clientService.changeStatus(id, request);

        assertEquals(0, client.getStatus());

        verify(clientRepository).findById(id);
        verify(clientRepository).save(client);
    }

    @Test
    void delete_ShouldDeleteClient_WhenClientExists() {
        UUID id = UUID.randomUUID();

        Client client = Client.builder()
                .id(id)
                .fullName("Juan Pérez")
                .build();

        when(clientRepository.findById(id)).thenReturn(Optional.of(client));

        clientService.delete(id);

        verify(clientRepository).findById(id);
        verify(clientRepository).delete(client);
    }
}