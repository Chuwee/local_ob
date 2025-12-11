package es.onebox.mgmt.b2b.publishing.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingDTO;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingFilterSessionsRequest;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingFiltersRequest;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingFiltersResponseDTO;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingsExportRequest;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingsResponseDTO;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingsSearchRequest;
import es.onebox.mgmt.b2b.publishing.service.B2BPublishingService;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.export.ExportService;
import es.onebox.mgmt.export.dto.ExportResponse;
import es.onebox.mgmt.export.dto.ExportStatusResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ADMIN;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_ACTION_GET;
import static es.onebox.mgmt.config.AuditTag.AUDIT_ACTION_SEARCH;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(B2BPublishingController.BASE_URI)
public class B2BPublishingController {
    protected static final String BASE_URI = ApiConfig.BASE_URL + "/b2b/publishing";

    private final B2BPublishingService b2bPublishingService;
    private final ExportService exportService;

    private static final String AUDIT_COLLECTION = "B2B_PUBLISHING";

    @Autowired
    public B2BPublishingController(B2BPublishingService b2bPublishingService, ExportService exportService) {
        this.b2bPublishingService = b2bPublishingService;
        this.exportService = exportService;
    }

    @GetMapping(value = "/seats")
    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ADMIN})
    public SeatPublishingsResponseDTO searchSeatPublishings(@Valid @BindUsingJackson SeatPublishingsSearchRequest searchRequest) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_ACTION_SEARCH, AUDIT_COLLECTION);
        return b2bPublishingService.searchSeatPublishings(searchRequest);
    }

    @GetMapping(value = "/seats/{id}")
    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ADMIN})
    public SeatPublishingDTO getSeatPublishingById(@PathVariable Long id) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_ACTION_GET, AUDIT_COLLECTION);
        return b2bPublishingService.getById(id);
    }

    @GetMapping(value = "/seats/filters/{filter}")
    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ADMIN})
    public SeatPublishingFiltersResponseDTO getSeatsFilterOptions(@PathVariable String filter,
                                                                  @Valid @BindUsingJackson SeatPublishingFiltersRequest filterRequest) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_ACTION_GET, AUDIT_COLLECTION);
        return b2bPublishingService.getSeatsFilterOptions(filter, filterRequest);
    }

    @GetMapping(value = "/seats/filters/sessions")
    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ADMIN})
    public SeatPublishingFiltersResponseDTO getSeatsFilterSessions(@Valid @BindUsingJackson SeatPublishingFilterSessionsRequest filterSessionsRequest) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_ACTION_GET, AUDIT_COLLECTION);
        return b2bPublishingService.getSeatsFilterSessions(filterSessionsRequest);
    }

    @PostMapping(value = "/seats/exports")
    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ADMIN})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportResponse exportsearchSeatPublishings(@BindUsingJackson @Valid SeatPublishingsSearchRequest filter,
                                                      @RequestBody @Valid SeatPublishingsExportRequest body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_EXPORT);
        return exportService.exportSeatPublishings(filter, body);
    }

    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ADMIN})
    @RequestMapping(method = RequestMethod.GET, value = "/{exportId}")
    public ExportStatusResponse getExportInfo(@PathVariable String exportId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_EXPORT);

        return exportService.checkSeatPublishingsStatus(exportId);
    }
}
