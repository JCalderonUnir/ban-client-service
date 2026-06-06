package ban.client.service;

import ban.client.dto.request.ClientCreateRequest;
import ban.client.dto.request.ClientStatusRequest;
import ban.client.dto.request.ClientUpdateRequest;
import ban.client.dto.response.ClientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ClientService{

    ClientResponse create(ClientCreateRequest request);

    ClientResponse findById(UUID id);

    Page<ClientResponse> findAll(Pageable pageable);

    ClientResponse update(UUID id, ClientUpdateRequest request);

    void changeStatus(UUID id, ClientStatusRequest request);

    void delete(UUID id);
}