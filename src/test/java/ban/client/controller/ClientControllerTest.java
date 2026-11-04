package ban.client.controller;

import ban.client.config.SecurityConfig;
import ban.client.dto.request.ClientCreateRequest;
import ban.client.dto.request.ClientStatusRequest;
import ban.client.dto.request.ClientUpdateRequest;
import ban.client.dto.response.ClientResponse;
import ban.client.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientOperator.class)
@Import(SecurityConfig.class)
class ClientOperatorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientService clientService;

    @Test
    void findAll_ShouldReturnClientsPage() throws Exception {
        ClientResponse response = ClientResponse.builder()
                .id(UUID.randomUUID())
                .documentType("CC")
                .documentNumber("123456789")
                .fullName("Juan Pérez")
                .email("juan@test.com")
                .phone("3001234567")
                .address("Bogotá")
                .build();

        Mockito.when(clientService.findAll(any()))
                .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("Juan Pérez"))
                .andExpect(jsonPath("$.content[0].email").value("juan@test.com"));
    }

    @Test
    void findUser_ShouldReturnClient_WhenExists() throws Exception {
        UUID id = UUID.randomUUID();

        ClientResponse response = ClientResponse.builder()
                .id(id)
                .fullName("Juan Pérez")
                .email("juan@test.com")
                .build();

        Mockito.when(clientService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/clients/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    void createClient_ShouldReturnCreated() throws Exception {
        ClientCreateRequest request = new ClientCreateRequest(
                "CC",
                "123456789",
                "Juan Pérez",
                "juan@test.com",
                "3001234567",
                "Bogotá"
        );

        ClientResponse response = ClientResponse.builder()
                .id(UUID.randomUUID())
                .documentType("CC")
                .documentNumber("123456789")
                .fullName("Juan Pérez")
                .email("juan@test.com")
                .phone("3001234567")
                .address("Bogotá")
                .build();

        Mockito.when(clientService.create(any(ClientCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/clients")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan@test.com"))
                .andExpect(jsonPath("$.documentNumber").value("123456789"));
    }

    @Test
    void updateUser_ShouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        ClientUpdateRequest request = new ClientUpdateRequest(
                "Juan Actualizado",
                "juan.update@test.com",
                "3111111111",
                "Medellín"
        );

        mockMvc.perform(put("/clients/{id}", id)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        Mockito.verify(clientService).update(eq(id), any(ClientUpdateRequest.class));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/clients/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(clientService).delete(id);
    }

    @Test
    void updateStatus_ShouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        ClientStatusRequest request = new ClientStatusRequest(0);

        mockMvc.perform(patch("/clients/{id}/status", id)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        Mockito.verify(clientService).changeStatus(eq(id), any(ClientStatusRequest.class));
    }
}