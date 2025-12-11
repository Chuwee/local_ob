package es.onebox.mgmt.insurance.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.insurance.dto.InsurerCreateDTO;
import es.onebox.mgmt.insurance.dto.InsurerDTO;
import es.onebox.mgmt.insurance.dto.InsurerDTORequest;
import es.onebox.mgmt.insurance.dto.InsurersDTO;
import es.onebox.mgmt.insurance.dto.SearchInsurerFilterDTO;
import es.onebox.mgmt.insurance.service.InsurerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = InsurerController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class InsurerController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/insurers";

    private static final String AUDIT_COLLECTION = "INSURERS";

    private final InsurerService insurerService;

    public InsurerController(InsurerService insurerService) {
        this.insurerService = insurerService;
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @GetMapping("/{insurerId}")
    public InsurerDTO getInsurer(@PathVariable @Min(value = 1, message = "insurerId must be above 0") Integer insurerId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return insurerService.getInsurer(insurerId);
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @GetMapping()
    public InsurersDTO searchInsurers(@BindUsingJackson @Valid SearchInsurerFilterDTO filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return insurerService.searchInsurers(filter);
    }

    @Secured({ROLE_SYS_MGR})
    @PostMapping()
    public InsurerDTO createInsurer(@Valid @RequestBody InsurerCreateDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return insurerService.createInsurer(request);
    }

    @Secured({ROLE_SYS_MGR})
    @PutMapping("/{insurerId}")
    public InsurerDTO updateInsurer(@PathVariable @Min(value = 1, message = "insurerId must be above 0") Integer insurerId,
                                    @Valid @RequestBody InsurerDTORequest request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return insurerService.updateInsurer(insurerId, request);
    }
}
