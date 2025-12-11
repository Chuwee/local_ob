package es.onebox.mgmt.templateszones.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityBlockType;
import es.onebox.mgmt.entities.contents.dto.EntityTextBlocksDTO;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlocksDTO;
import es.onebox.mgmt.entities.contents.enums.EntityBlockCategory;
import es.onebox.mgmt.templateszones.dto.TemplateZonesDTO;
import es.onebox.mgmt.templateszones.dto.TemplatesZonesRequestDTO;
import es.onebox.mgmt.templateszones.dto.TemplatesZonesRequestFilterDTO;
import es.onebox.mgmt.templateszones.dto.TemplatesZonesResponseDTO;
import es.onebox.mgmt.templateszones.dto.TemplatesZonesUpdateRequestDTO;
import es.onebox.mgmt.templateszones.enums.TemplatesZonesTagType;
import es.onebox.mgmt.templateszones.service.TemplatesZonesService;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ADMIN;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(value = ApiConfig.BASE_URL + "/entities/{entityId}/templates-zones")
@Validated
public class TemplatesZonesController {

    private static final String AUDIT_COLLECTION = "TEMPLATES_ZONES";

    private final TemplatesZonesService templatesZonesService;

    @Autowired
    public TemplatesZonesController(TemplatesZonesService templatesZonesService) {
        this.templatesZonesService = templatesZonesService;
    }

    @GetMapping
    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_SYS_MGR, ROLE_SYS_ANS, ROLE_ENT_ADMIN, ROLE_EVN_MGR})
    public TemplatesZonesResponseDTO getTemplatesZones(@PathVariable @Min(value = 1, message = "entityId must be above 0") Integer entityId,
                                                       @BindUsingJackson TemplatesZonesRequestFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return templatesZonesService.getTemplatesZones(entityId, filter);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({ROLE_OPR_MGR, ROLE_ENT_MGR})
    public IdDTO createTemplateZones(@PathVariable @Min(value = 1, message = "entityId must be above 0") Integer entityId,
                                     @RequestBody @Valid TemplatesZonesRequestDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return templatesZonesService.createTemplatesZone(entityId, request);
    }

    @GetMapping("/{templateZonesId}")
    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_SYS_MGR, ROLE_SYS_ANS, ROLE_ENT_ADMIN, ROLE_EVN_MGR})
    public TemplateZonesDTO<TemplatesZonesTagType> getTemplatesZoneById(@PathVariable @Min(value = 1, message = "entityId must be above 0") Integer entityId,
                                                                        @PathVariable @Min(value = 1, message = "templatesZonesId must be above 0") Integer templateZonesId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return templatesZonesService.getTemplateZones(entityId, templateZonesId);
    }

    @PutMapping("/{templateZonesId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_OPR_MGR, ROLE_ENT_MGR})
    public void updateTemplateZones(@PathVariable @Min(value = 1, message = "entityId must be above 0") Integer entityId,
                                    @PathVariable @Min(value = 1, message = "templatesZonesId must be above 0") Integer templateZonesId,
                                    @RequestBody @Valid TemplatesZonesUpdateRequestDTO updateDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        templatesZonesService.updateTemplatesZone(entityId, templateZonesId, updateDTO);
    }

    @DeleteMapping("/{templateZonesId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_OPR_MGR, ROLE_ENT_MGR})
    public void deleteTemplateZones(@PathVariable @Min(value = 1, message = "entityId must be above 0") Integer entityId,
                                    @PathVariable @Min(value = 1, message = "templatesZonesId must be above 0") Integer templateZonesId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        templatesZonesService.deleteTemplatesZone(entityId, templateZonesId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_EVN_MGR})
    @GetMapping("/{templateZonesId}/contents/{category}")
    public EntityTextBlocksDTO getCommunicationElement(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @PathVariable @Min(value = 1, message = "templateZoneId must be above 0") Long templateZonesId,
            @PathVariable EntityBlockCategory category,
            @RequestParam(value = "language", required = false) @LanguageIETF String language,
            @RequestParam(value = "type", required = false) List<EntityBlockType> type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.templatesZonesService.getTextBlocks(entityId, templateZonesId, language, category, type);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{templateZonesId}/contents/{category}")
    public void updateCommunicationElement(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @PathVariable @Min(value = 1, message = "templateZoneId must be above 0") Long templateZonesId,
            @PathVariable EntityBlockCategory category,
            @NotEmpty @RequestBody @Valid UpdateEntityTextBlocksDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.templatesZonesService.updateTextBlocks(entityId, templateZonesId, category, body);
    }
}