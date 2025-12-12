package es.onebox.internal.automaticsales.processsales.controller;

import es.onebox.common.security.Role;
import es.onebox.internal.automaticsales.processsales.dto.AutomaticSaleRequest;
import es.onebox.internal.automaticsales.processsales.dto.ProcessSalesRequest;
import es.onebox.internal.automaticsales.processsales.dto.UpdateProcessSalesRequest;
import es.onebox.internal.automaticsales.processsales.service.ProcessSalesService;
import es.onebox.internal.config.InternalApiConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(InternalApiConfig.AutomaticSales.BASE_URL)
public class ProcessSalesController {

    @Autowired
    private ProcessSalesService processSalesService;

    @Secured(Role.OPERATOR_MANAGER)
    @PostMapping("/sessions/{sessionId}/sales")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void processSales(@PathVariable(value = "sessionId") Long sessionId,
                             @RequestBody @Valid @NotNull ProcessSalesRequest request) {
        processSalesService.processSales(sessionId, request);
    }

    @Secured(Role.OPERATOR_MANAGER)
    @PostMapping("/sales")
    public String processIndividualSales(@RequestBody @Valid @NotNull AutomaticSaleRequest request) {
        return processSalesService.processIndividualSale(request);
    }

    @Secured(Role.OPERATOR_MANAGER)
    @PutMapping("/sessions/{sessionId}/sales")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modifyProcessSales(@PathVariable(value = "sessionId") Long sessionId,
                                   @RequestBody @Valid @NotNull UpdateProcessSalesRequest request) {
        processSalesService.modifyProcessSales(sessionId, request);
    }

}
