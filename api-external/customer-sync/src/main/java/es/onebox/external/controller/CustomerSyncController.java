package es.onebox.external.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.external.dto.ExternalSyncRequest;
import es.onebox.external.dto.ExternalSyncResponse;
import es.onebox.external.service.CustomerSyncService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
@RequestMapping(ApiConfig.CustomerSyncApiConfig.BASE_URL)
public class CustomerSyncController {

    private final CustomerSyncService customerSyncService;

    public CustomerSyncController(CustomerSyncService customerSyncService) {
        this.customerSyncService = customerSyncService;
    }

    @PostMapping("/customers/external-sync")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Serializable> syncCustomers(@RequestBody @Valid ExternalSyncRequest request) {
        ExternalSyncResponse externalSyncResponse = customerSyncService.syncCustomers(request);
        return new ResponseEntity<>(externalSyncResponse, HttpStatus.OK);
    }
}
