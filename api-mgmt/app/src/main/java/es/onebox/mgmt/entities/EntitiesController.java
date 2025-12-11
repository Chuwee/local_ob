package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.auth.dto.AuthConfigDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.InventoryDTO;
import es.onebox.mgmt.entities.dto.AvailableCampaignsDTO;
import es.onebox.mgmt.entities.dto.CreateEntityRequestDTO;
import es.onebox.mgmt.entities.dto.EntityDTO;
import es.onebox.mgmt.entities.dto.EntityDonationProvidersDTO;
import es.onebox.mgmt.entities.dto.EntitySearchFilterDTO;
import es.onebox.mgmt.entities.dto.EntityTaxApiDTO;
import es.onebox.mgmt.entities.dto.EntityVisibilitiesDTO;
import es.onebox.mgmt.entities.dto.SearchEntitiesResponse;
import es.onebox.mgmt.entities.dto.SearchManagedEntitiesResponse;
import es.onebox.mgmt.entities.dto.UpdateEntityRequestDTO;
import es.onebox.mgmt.entities.dto.UserLimitsDTO;
import es.onebox.mgmt.entities.enums.EntityType;
import es.onebox.mgmt.events.dto.CapacityExternalDTO;
import es.onebox.mgmt.events.dto.LoadedCapacityExternalDTO;
import es.onebox.mgmt.events.dto.UpdateCapacityExternalDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static es.onebox.core.security.Roles.Codes.ROLE_CRM_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = EntitiesController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class EntitiesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/entities";

    private static final String AUDIT_COLLECTION = "ENTITIES";
    private static final String AUDIT_SUBCOLLECTION_TYPES = "TYPES";
    private static final String AUDIT_SUBCOLLECTION_ROLES = "ROLES";
    private static final String AUDIT_SUBCOLLECTION_PERIODICITIES = "PERIODICITIES";
    private static final String AUDIT_SUBCOLLECTION_TERMS = "TYPES";
    private static final String AUDIT_SUBCOLLECTION_TAXES = "TAXES";
    private static final String AUDIT_SUBCOLLECTION_DONATION_PROVIDERS = "DONATION_PROVIDERS";
    private static final String AUDIT_SUBCOLLECTION_CUSTOMERS_AUTH = "CUSTOMERS_AUTH_CONFIG";
    private static final String AUDIT_SUBCOLLECTION_CAPACITY_MAPPING = "CAPACITY_MAPPING";

    private final EntitiesService entityService;
    private final EntityVisibilityService entityVisibilityService;

    @Autowired
    public EntitiesController(EntitiesService entityService, EntityVisibilityService entityVisibilityService) {
        this.entityService = entityService;
        this.entityVisibilityService = entityVisibilityService;
    }


    @Secured({Roles.Codes.ROLE_CNL_MGR, Roles.Codes.ROLE_EVN_MGR, Roles.Codes.ROLE_ENT_MGR, Roles.Codes.ROLE_ENT_ANS,
            Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_SYS_MGR, ROLE_SYS_ANS, Roles.Codes.ROLE_CNL_SAC,
            Roles.Codes.ROLE_CRM_MGR})
    @GetMapping("/{entityId}")
    public EntityDTO getEntity(@PathVariable Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        ConverterUtils.checkField(entityId, "entityId");
        return entityService.getEntity(entityId);
    }

    @Secured({Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_SYS_MGR,
            Roles.Codes.ROLE_SYS_ANS, Roles.Codes.ROLE_ENT_ADMIN})
    @GetMapping
    public SearchEntitiesResponse getEntities(@BindUsingJackson @Valid EntitySearchFilterDTO filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return entityService.getEntities(filter);
    }

    @Secured({Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_SYS_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createEntity(@RequestBody @Valid CreateEntityRequestDTO entity) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        ConverterUtils.checkField(entity.getName(), "name");
        ConverterUtils.checkField(entity.getShortName(), "short_name");
        ConverterUtils.checkField(entity.getSocialReason(), "social_reason");
        ConverterUtils.checkField(entity.getNif(), "nif");
        ConverterUtils.checkField(entity.getEmail(), "email");
        ConverterUtils.checkField(entity.getDefaultLanguage(), "default_language");
        ConverterUtils.checkField(entity.getContact(), "contact");
        ConverterUtils.checkField(entity.getContact().getCity(), "city");
        ConverterUtils.checkField(entity.getContact().getCountry(), "country");
        ConverterUtils.checkField(entity.getContact().getCountry().getCode(), "code");

        if (CommonUtils.isEmpty(entity.getTypes())) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "at least 1 type must be defined", null);
        }

        return new IdDTO(entityService.create(entity));
    }

    @Secured({Roles.Codes.ROLE_ENT_MGR, Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_SYS_MGR})
    @PutMapping("/{entityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEntity(@PathVariable Long entityId, @RequestBody @Valid UpdateEntityRequestDTO entity) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        if (entity.getId() != null && !entity.getId().equals(entityId)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "entityId is different between pathVariable and requestBody", null);
        }

        entityService.update(entityId, entity);
    }

    @Secured({Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_SYS_MGR})
    @DeleteMapping("/{entityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntity(@PathVariable Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        entityService.delete(entityId);
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, Roles.Codes.ROLE_ENT_MGR, Roles.Codes.ROLE_ENT_ANS, Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_SYS_MGR})
    @GetMapping("/{entityId}/types")
    public List<EntityType> getEntityTypes(@PathVariable Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TYPES, AuditTag.AUDIT_ACTION_GET);

        return entityService.getEntityTypes(entityId);
    }

    @Secured({Roles.Codes.ROLE_ENT_MGR, Roles.Codes.ROLE_ENT_ANS, Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_SYS_MGR})
    @GetMapping("/{entityId}/types/available")
    public List<EntityType> getAvailableEntityTypes(@PathVariable Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TYPES, AuditTag.AUDIT_ACTION_GET_AVAILABLE);

        return entityService.getAvailableEntityTypes(entityId);
    }

    @Secured({Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_SYS_MGR})
    @PutMapping("/{entityId}/types/{entityType}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setEntityType(@PathVariable Long entityId, @PathVariable EntityType entityType) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TYPES, AuditTag.AUDIT_ACTION_ADD);

        entityService.setEntityType(entityId, entityType);
    }

    @Secured({Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_SYS_MGR})
    @GetMapping("/{entityId}/managed-entities")
    public SearchManagedEntitiesResponse searchManagedEntities(@PathVariable Long entityId,
                                                               @BindUsingJackson @Valid BaseRequestFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return entityService.searchManagedEntities(entityId, filter);
    }

    @Secured({Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_SYS_MGR})
    @DeleteMapping("/{entityId}/types/{entityType}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsetEntityType(@PathVariable Long entityId, @PathVariable EntityType entityType) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TYPES, AuditTag.AUDIT_ACTION_DELETE);

        entityService.unsetEntityType(entityId, entityType);
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, Roles.Codes.ROLE_ENT_MGR, Roles.Codes.ROLE_ENT_ANS, Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_SYS_MGR})
    @GetMapping("/{entityId}/taxes")
    public List<EntityTaxApiDTO> getEntityTaxes(@PathVariable Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAXES, AuditTag.AUDIT_ACTION_GET);

        return entityService.findTaxes(entityId);
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, Roles.Codes.ROLE_ENT_ANS, Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS})
    @GetMapping("/{entityId}/loaded-external-capacities")
    public List<LoadedCapacityExternalDTO> getLoadedExternalCapacities(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return entityService.getLoadedExternalCapacities(entityId);
    }

    @Secured({ROLE_SYS_ANS, ROLE_SYS_MGR})
    @GetMapping("/user-limits")
    public UserLimitsDTO getUserLimits() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return entityService.getUserLimits();
    }

    @Secured(Roles.Codes.ROLE_OPR_MGR)
    @GetMapping("/{entityId}/provider/{providerId}/external-inventories")
    public List<InventoryDTO> getExternalInventory(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @PathVariable String providerId,
            @RequestParam(value = "skip_used", required = false) Boolean skipUsed
    ) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return entityService.getExternalInventory(entityId, providerId, skipUsed);
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, Roles.Codes.ROLE_ENT_MGR, Roles.Codes.ROLE_ENT_ANS, Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_SYS_MGR})
    @GetMapping("/{entityId}/external-capacities")
    public Set<CapacityExternalDTO> getExternalCapacities(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return entityService.getExternalCapacities(entityId);
    }

    @Secured(Roles.Codes.ROLE_OPR_MGR)
    @PostMapping("/{entityId}/external-capacities/{capacityId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void loadCapacityExternal(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @PathVariable Integer capacityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_IMPORT);
        entityService.loadCapacityExternal(entityId, capacityId);
    }

    @Secured(Roles.Codes.ROLE_OPR_MGR)
    @PutMapping("/{entityId}/external-capacities/{capacityId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateCapacityExternal(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @PathVariable Integer capacityId,
            @RequestBody @Valid UpdateCapacityExternalDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        entityService.updateCapacityExternal(entityId, capacityId, body);
    }

    @Secured(Roles.Codes.ROLE_OPR_MGR)
    @PostMapping("/{entityId}/external-capacities/refresh")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateExternalEntityEvents(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_REFRESH);
        entityService.updateExternalEntityEvents(entityId);
    }

    @Secured(Roles.Codes.ROLE_OPR_MGR)
    @PostMapping("/{entityId}/external-capacities/{capacityId}/refresh")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateExternalEntityEvents(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @PathVariable(value= "capacityId", required = false) Integer capacityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_REFRESH);
        entityService.updateExternalEntityEvents(entityId, capacityId);
    }

    @Secured(Roles.Codes.ROLE_OPR_MGR)
    @DeleteMapping("/{entityId}/external-capacities/{capacityId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteCapacityExternal(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @PathVariable Integer capacityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        entityService.deleteCapacityExternal(entityId, capacityId);
    }

    @Secured(Roles.Codes.ROLE_OPR_MGR)
    @PostMapping("/{entityId}/external-capacities/{capacityId}/mapping")
    public void createCapacityMappings(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @PathVariable Integer capacityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CAPACITY_MAPPING, AuditTag.AUDIT_ACTION_CREATE);
        ConverterUtils.checkField(capacityId, "capacityId");
        ConverterUtils.checkField(entityId, "entityId");
        entityService.createCapacityMappings(entityId, capacityId);
    }


    @Secured({ROLE_SYS_ANS, ROLE_SYS_MGR})
    @GetMapping(value = "/{entityId}/visibility")
    public EntityVisibilitiesDTO getEntityVisibilities(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return entityVisibilityService.getEntityVisibilities(entityId);
    }

    @Secured({ROLE_SYS_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/{entityId}/visibility")
    public void updateEntityVisibilities(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                                         @RequestBody @Valid EntityVisibilitiesDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        entityVisibilityService.setEntityVisibilities(entityId, body);
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, Roles.Codes.ROLE_ENT_MGR, Roles.Codes.ROLE_ENT_ANS, Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_SYS_MGR})
    @GetMapping("/{entityId}/external-periodicities")
    public List<IdNameDTO> getExternalPeriodicities(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PERIODICITIES, AuditTag.AUDIT_ACTION_GET);
        return entityService.getExternalPeriodicities(entityId);
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, Roles.Codes.ROLE_ENT_MGR, Roles.Codes.ROLE_ENT_ANS, Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_SYS_MGR})
    @GetMapping("/{entityId}/external-terms")
    public List<IdNameDTO> getExternalTerms(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TERMS, AuditTag.AUDIT_ACTION_GET);
        return entityService.getExternalTerms(entityId);
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, Roles.Codes.ROLE_ENT_MGR, Roles.Codes.ROLE_ENT_ANS, Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_SYS_MGR})
    @GetMapping("/{entityId}/external-roles")
    public List<IdNameDTO> getExternalRoles(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_ROLES, AuditTag.AUDIT_ACTION_GET);
        return entityService.getExternalRoles(entityId);
    }

    @Secured({Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_CNL_MGR})
    @GetMapping("/{entityId}/donation-providers")
    public EntityDonationProvidersDTO getDonationProviders(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId
    ) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_DONATION_PROVIDERS, AuditTag.AUDIT_ACTION_GET);
        return entityService.getActiveDonationProviders(entityId);
    }

    @Secured({Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_ENT_ADMIN, Roles.Codes.ROLE_CNL_MGR, Roles.Codes.ROLE_COL_MGR})
    @GetMapping("/{entityId}/auth-config")
    public AuthConfigDTO getAuthConfig(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId
    ) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CUSTOMERS_AUTH, AuditTag.AUDIT_ACTION_GET);
        return entityService.getAuthConfig(entityId);
    }

    @Secured({Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_ENT_ADMIN, Roles.Codes.ROLE_CNL_MGR, Roles.Codes.ROLE_COL_MGR})
    @PutMapping("/{entityId}/auth-config")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAuthConfig(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId, @RequestBody AuthConfigDTO authConfigDTO
    ) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CUSTOMERS_AUTH, AuditTag.AUDIT_ACTION_UPDATE);
        entityService.updateAuthConfig(entityId, authConfigDTO);
    }

    @Secured({Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_CNL_MGR})
    @GetMapping("/{entityId}/donation-providers/{providerId}/campaigns")
    public AvailableCampaignsDTO getAvailableCampaigns(
            @PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
            @PathVariable @Min(value = 1, message = "Incorrect donation provider ID") Long providerId
    ) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_DONATION_PROVIDERS, AuditTag.AUDIT_ACTION_GET);
        return entityService.getAvailableCampaigns(entityId, providerId);
    }

}
