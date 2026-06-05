package ban.client.repository;

import ban.client.entity.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void save_ShouldPersistClient() {
        Client client = Client.builder()
                .documentType("CC")
                .documentNumber("123456789")
                .fullName("Juan Pérez")
                .email("juan@test.com")
                .phone("3001234567")
                .address("Bogotá")
                .status(1)
                .build();

        Client saved = clientRepository.save(client);

        assertNotNull(saved.getId());
        assertEquals("Juan Pérez", saved.getFullName());
        assertEquals("juan@test.com", saved.getEmail());
    }

    @Test
    void findById_ShouldReturnClient_WhenExists() {
        Client client = Client.builder()
                .documentType("CC")
                .documentNumber("123456789")
                .fullName("Juan Pérez")
                .email("juan@test.com")
                .phone("3001234567")
                .address("Bogotá")
                .status(1)
                .build();

        Client saved = clientRepository.save(client);

        Optional<Client> result = clientRepository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("Juan Pérez", result.get().getFullName());
    }

    @Test
    void delete_ShouldRemoveClient() {
        Client client = Client.builder()
                .documentType("CC")
                .documentNumber("123456789")
                .fullName("Juan Pérez")
                .email("juan@test.com")
                .status(1)
                .build();

        Client saved = clientRepository.save(client);

        clientRepository.delete(saved);

        Optional<Client> result = clientRepository.findById(saved.getId());

        assertTrue(result.isEmpty());
    }
}