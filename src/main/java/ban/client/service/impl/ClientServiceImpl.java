package ban.client.service.impl;

import ban.client.dto.request.ClientCreateRequest;
import ban.client.dto.request.ClientStatusRequest;
import ban.client.dto.request.ClientUpdateRequest;
import ban.client.dto.response.ClientResponse;
import ban.client.entity.Client;
import ban.client.mapper.ClientMapper;
import ban.client.repository.ClientRepository;
import ban.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponse create(ClientCreateRequest request) {
        Client clientSave = Client.builder()
                .documentType(request.documentType())
                .documentNumber(request.documentNumber())
                .fullName(request.fullName())
                .phone(request.phone())
                .email(request.email())
                .address(request.address())
                .status(1)
                .build();

        Client clientSaved = clientRepository.save(clientSave);

        return clientMapper.toResponse(clientSaved);
    }

    @Override
    public ClientResponse findById(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return clientMapper.toResponse(client);
    }

    @Override
    public Page<ClientResponse> findAll(Pageable pageable) {
        return clientRepository.findAll(pageable)
                .map(clientMapper::toResponse);
    }

    @Override
    public ClientResponse update(UUID id, ClientUpdateRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        client.setFullName(request.fullName());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setAddress(request.address());

        Client updated = clientRepository.save(client);

        return clientMapper.toResponse(updated);
    }

    @Override
    public void changeStatus(UUID id, ClientStatusRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        client.setStatus(request.status());
        clientRepository.save(client);
    }

    @Override
    public void delete(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        clientRepository.delete(client);
    }
}
