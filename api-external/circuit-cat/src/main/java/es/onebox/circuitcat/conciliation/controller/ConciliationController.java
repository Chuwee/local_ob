package es.onebox.circuitcat.conciliation.controller;

import es.onebox.circuitcat.conciliation.dto.ConciliationDTO;
import es.onebox.circuitcat.conciliation.dto.ConciliationRequest;
import es.onebox.circuitcat.conciliation.service.ConciliationService;
import es.onebox.common.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.CircuitApiConfig.BASE_URL + "/conciliation")
public class ConciliationController {

    @Autowired
    private ConciliationService conciliationService;

    @PostMapping()
    public ResponseEntity<ConciliationDTO> conciliate(@RequestBody ConciliationRequest request) {

        ConciliationDTO conciliation = conciliationService.getConciliation(request.getSessionIds(), request.getSeats());

        if (conciliation != null) {
            return new ResponseEntity<>(conciliation, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
