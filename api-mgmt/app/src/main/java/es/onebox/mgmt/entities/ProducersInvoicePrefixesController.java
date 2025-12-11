package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerInoivcePrefixFilter;
import es.onebox.mgmt.entities.dto.CreateProducerInvoicePrefixRequestDTO;
import es.onebox.mgmt.entities.dto.ProducerInvoicePrefixesDTO;
import es.onebox.mgmt.entities.dto.UpdateProducerInvoicePrefixRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@Validated
@RestController
@RequestMapping(
        value = ProducersInvoicePrefixesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ProducersInvoicePrefixesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/producers/{producerId}/invoice-prefixes";

    private static final String PRODUCER_ID_MUST_BE_ABOVE_0 = "Producer Id must be above 0";
    private static final String INVOICE_PREFIX_ID_MUST_BE_ABOVE_0 = "Invoice prefix Id must be above 0";

    private static final String AUDIT_COLLECTION = "PRODUCER_INVOICE_PREFIXES";

    private final ProducersInvoicePrefixesService producersInvoicePrefixesService;

    @Autowired
    public ProducersInvoicePrefixesController(final ProducersInvoicePrefixesService producersInvoicePrefixesService) {
        this.producersInvoicePrefixesService = producersInvoicePrefixesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ProducerInvoicePrefixesDTO getProducerInvoicePrefixes(
            @PathVariable @Min(value = 1, message = PRODUCER_ID_MUST_BE_ABOVE_0) Long producerId,
            @BindUsingJackson @Valid ProducerInoivcePrefixFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return producersInvoicePrefixesService.getProducersInvoicePrefixes(producerId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createProducerInvoicePrefix(
            @PathVariable @Min(value = 1, message = PRODUCER_ID_MUST_BE_ABOVE_0) Long producerId,
            @RequestBody @Valid CreateProducerInvoicePrefixRequestDTO createProducerInvoicePrefixRequestDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return producersInvoicePrefixesService.createInvoicePrefix(producerId, createProducerInvoicePrefixRequestDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{invoicePrefixId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateProducerInvoicePrefix(
            @PathVariable @Min(value = 1, message = PRODUCER_ID_MUST_BE_ABOVE_0) Long producerId,
            @PathVariable @Min(value = 1, message = INVOICE_PREFIX_ID_MUST_BE_ABOVE_0) Long invoicePrefixId,
            @RequestBody @Valid UpdateProducerInvoicePrefixRequestDTO updateProducerInvoicePrefixRequestDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        producersInvoicePrefixesService.updateInvoicePrefix(producerId, invoicePrefixId, updateProducerInvoicePrefixRequestDTO);
    }

}
