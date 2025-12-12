package es.onebox.fifaqatar.conciliation.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.fifaqatar.conciliation.service.CustomersConciliationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = ApiConfig.QatarApiConfig.BASE_URL)
public class CustomerConciliationController {
    private static final String CUSTOMER_CONCILIATION = "/customers-conciliation";
    private static final String CUSTOMER_CREATION_FROM_CSV = "/customers-creation-csv";

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerConciliationController.class);

    private final CustomersConciliationService conciliationService;

    public CustomerConciliationController(CustomersConciliationService conciliationService) {
        this.conciliationService = conciliationService;
    }


    @PostMapping(CUSTOMER_CONCILIATION)
    public Map<String, Object> updateCustomers(@RequestParam(required = false) List<Long> channelIds) throws IOException {
        return conciliationService.updateCustomers(channelIds);
    }

    @PostMapping(
            value = CUSTOMER_CREATION_FROM_CSV, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> createCustomersFromCsv(@RequestParam(required = false) List<Long> channelIds, @RequestPart("file") MultipartFile file) throws IOException {
        return conciliationService.createCustomersFromCsv(file, channelIds);

    }
}

