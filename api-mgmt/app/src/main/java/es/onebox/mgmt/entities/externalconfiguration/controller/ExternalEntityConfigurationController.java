package es.onebox.mgmt.entities.externalconfiguration.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.externalconfiguration.dto.ExternalConfigRequestDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.ExternalConfigResponseDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.InventoryProviderConfigResponseDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.LinkClubConfigDTO;
import es.onebox.mgmt.entities.externalconfiguration.service.ExternalEntityConfigurationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(value = ExternalEntityConfigurationController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ExternalEntityConfigurationController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/entities";

    private static final String URI_SUFFIX = "/{entityId}/external-configurations";

    private static final String URI_SUFFIX_INVENTORY = "/{entityId}/inventory-providers";
    private static final String AUDIT_COLLECTION = "ENTITIES_EXTERNAL_CONFIGURATIONS";

    private final ExternalEntityConfigurationService externalEntityConfigurationService;

    @Autowired
    public ExternalEntityConfigurationController(final ExternalEntityConfigurationService externalEntityConfigurationService) {
        this.externalEntityConfigurationService = externalEntityConfigurationService;
    }

    @Secured({ROLE_OPR_MGR})
    @GetMapping(URI_SUFFIX)
    public ExternalConfigResponseDTO getExternalConfigByEntityId(@PathVariable("entityId") Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        ConverterUtils.checkField(entityId, "entityId");
        return externalEntityConfigurationService.getExternalEntityConfiguration(entityId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping(URI_SUFFIX_INVENTORY)
    public InventoryProviderConfigResponseDTO getInventoryProvidersConfigByEntityId(@PathVariable("entityId") Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        ConverterUtils.checkField(entityId, "entityId");
        return externalEntityConfigurationService.getInventoryProvidersConfigByEntityId(entityId);
    }

    @Secured({ROLE_OPR_MGR})
    @PutMapping(URI_SUFFIX)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateExternalConfiguration(@PathVariable("entityId") Long entityId,
                                            @Valid @RequestBody ExternalConfigRequestDTO externalConfigRequestDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        ConverterUtils.checkField(entityId, "entityId");
        externalEntityConfigurationService.updateExternalEntityConfiguration(entityId, externalConfigRequestDTO);
    }

    @Secured({ROLE_OPR_MGR})
    @PostMapping(URI_SUFFIX + "/link")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkClub(@PathVariable("entityId") Long entityId, @Valid @RequestBody LinkClubConfigDTO linkClubConfigDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_LINK);

        ConverterUtils.checkField(entityId, "entityId");
        externalEntityConfigurationService.linkClub(entityId, linkClubConfigDTO);
    }

    @Secured({ROLE_OPR_MGR})
    @PostMapping(URI_SUFFIX + "/unlink")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlinkClub(@PathVariable("entityId") Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UNLINK);

        ConverterUtils.checkField(entityId, "entityId");
        externalEntityConfigurationService.unlinkClub(entityId);
    }

    @Secured({ROLE_OPR_MGR})
    @GetMapping("/external-configurations/codes/available")
    public List<String> getAvailableCodes() {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return externalEntityConfigurationService.getAvailableClubCodes();
    }
}
