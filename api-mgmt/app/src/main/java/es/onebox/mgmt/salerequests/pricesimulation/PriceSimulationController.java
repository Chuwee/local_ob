package es.onebox.mgmt.salerequests.pricesimulation;


import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.export.ExportService;
import es.onebox.mgmt.export.dto.ExportResponse;
import es.onebox.mgmt.export.dto.ExportStatusResponse;
import es.onebox.mgmt.salerequests.pricesimulation.dto.PriceSimulationExportRequest;
import es.onebox.mgmt.salerequests.pricesimulation.dto.VenueConfigPricesSimulationDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;


@RestController
@RequestMapping(
        value = ApiConfig.BASE_URL,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class PriceSimulationController {

    public static final String BASE_URI = "/catalog-sale-requests/{saleRequestId}/price-simulation";

    private static final String AUDIT_COLLECTION = "SALE_REQUESTS_PRICE_SIMULATION";

    private final PriceSimulationService priceSimulationService;
    private final ExportService exportService;

    @Autowired
    public PriceSimulationController(PriceSimulationService priceSimulationService,
        ExportService exportService) {
        this.priceSimulationService = priceSimulationService;
        this.exportService = exportService;
    }

    @GetMapping(value = BASE_URI)
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public List<VenueConfigPricesSimulationDTO> priceSimulation(@PathVariable Long saleRequestId ) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return priceSimulationService.getPriceSimulation(saleRequestId);
    }

    @PostMapping(value = BASE_URI + "/exports")
    @Secured({ROLE_ENT_MGR, ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportResponse export(@PathVariable Long saleRequestId,
        @Valid @RequestBody PriceSimulationExportRequest requestBody) {
        Audit.addTags(AuditTag.AUDIT_ACTION_EXPORT, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return exportService.exportPriceSimulations(saleRequestId, requestBody);
    }

    @GetMapping(value = "/catalog-sale-requests/price-simulation/exports/{exportId}")
    @Secured({ROLE_ENT_MGR, ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public ExportStatusResponse getExportInfo(@PathVariable String exportId) {
        Audit.addTags(AuditTag.AUDIT_ACTION_EXPORT, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return exportService.getPriceSimulationExportStatus(exportId);
    }

}
