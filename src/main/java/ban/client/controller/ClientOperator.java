package ban.client.controller;

import ban.client.dto.request.ClientCreateRequest;
import ban.client.dto.request.ClientStatusRequest;
import ban.client.dto.request.ClientUpdateRequest;
import ban.client.dto.response.ClientResponse;
import ban.client.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/clients")
public class ClientOperator {
    private final ClientService clientService;

    public ClientOperator(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<Page<ClientResponse>> findAll(
            @PageableDefault(size = 10) Pageable pageable) {
            return ResponseEntity.ok(clientService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> findUser(
            @PathVariable UUID id
    ){
        ClientResponse response = clientService.findById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping
    public ResponseEntity<ClientResponse> createClient(
            @Valid @RequestBody ClientCreateRequest request
    ) {

        ClientResponse response = clientService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody ClientUpdateRequest request
    ){
        ClientResponse response = clientService.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ClientStatusRequest request
    ){
        clientService.changeStatus(id, request);
        return ResponseEntity.noContent().build();
    }
}
