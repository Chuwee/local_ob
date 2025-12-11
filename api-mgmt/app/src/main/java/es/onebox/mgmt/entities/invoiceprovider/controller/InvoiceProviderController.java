package es.onebox.mgmt.entities.invoiceprovider.controller;

import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.entities.invoiceprovider.dto.InvoiceProviderInfoDTO;
import es.onebox.mgmt.entities.invoiceprovider.dto.RequestInvoiceProviderDTO;
import es.onebox.mgmt.entities.invoiceprovider.enums.InvoiceProvider;
import es.onebox.mgmt.entities.invoiceprovider.service.InvoiceProviderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(InvoiceProviderController.BASE_URI)
public class InvoiceProviderController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/producers/{producerId}/invoice-providers";

    private final InvoiceProviderService invoiceProviderService;

    @Autowired
    public InvoiceProviderController(InvoiceProviderService invoiceProviderService) {
        this.invoiceProviderService = invoiceProviderService;
    }

    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_EVN_MGR})
    @GetMapping
    public InvoiceProviderInfoDTO getInvoiceProviderInfo(
            @PathVariable @Min(value = 1, message = "producerId must be greater than 0") Long producerId) {
        return invoiceProviderService.getProducerInvoiceProviderInfo(producerId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_EVN_MGR})
    @GetMapping("/options")
    public List<InvoiceProvider> getInvoiceProviderOptions(@PathVariable String producerId) {
        return Arrays.stream(InvoiceProvider.values()).toList();
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_EVN_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceProviderInfoDTO requestInvoiceProvider(
            @PathVariable @Min(value = 1, message = "producerId must be greater than 0") Long producerId,
            @RequestBody @NotNull @Valid RequestInvoiceProviderDTO request) {
        return invoiceProviderService.requestInvoiceProvider(producerId, request);
    }
}
