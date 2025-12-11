package es.onebox.mgmt.venues;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles.Codes;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.IdNameListWithMetadata;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.CompetitionDTO;
import es.onebox.mgmt.events.dto.LoadedCapacityExternalDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.export.ExportService;
import es.onebox.mgmt.export.dto.ExportResponse;
import es.onebox.mgmt.export.dto.ExportStatusResponse;
import es.onebox.mgmt.venues.dto.BlockingReasonDTO;
import es.onebox.mgmt.venues.dto.BlockingReasonRequestDTO;
import es.onebox.mgmt.venues.dto.CloneTemplateRequestDTO;
import es.onebox.mgmt.venues.dto.CreateDynamicTagGroupRequestDTO;
import es.onebox.mgmt.venues.dto.CreateTemplateRequestDTO;
import es.onebox.mgmt.venues.dto.CreateVenueTagConfigRequestDTO;
import es.onebox.mgmt.venues.dto.CreateVenueTemplatePriceTypeRestrictionDTO;
import es.onebox.mgmt.venues.dto.DynamicTagDTO;
import es.onebox.mgmt.venues.dto.DynamicTagGroupDTO;
import es.onebox.mgmt.venues.dto.GateDTO;
import es.onebox.mgmt.venues.dto.GateRequestDTO;
import es.onebox.mgmt.venues.dto.InteractiveVenueDTO;
import es.onebox.mgmt.venues.dto.InteractiveVenueRequestDTO;
import es.onebox.mgmt.venues.dto.PriceTypeChannelContentFilterDTO;
import es.onebox.mgmt.venues.dto.PriceTypeChannelContentsListDTO;
import es.onebox.mgmt.venues.dto.PriceTypeDTO;
import es.onebox.mgmt.venues.dto.PriceTypeRequestDTO;
import es.onebox.mgmt.venues.dto.QuotaDTO;
import es.onebox.mgmt.venues.dto.SearchVenueTemplatesResponse;
import es.onebox.mgmt.venues.dto.UpdateDynamicTagGroupRequestDTO;
import es.onebox.mgmt.venues.dto.UpdateTemplateRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTagConfigRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateDetailsDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateFilterDTO;
import es.onebox.mgmt.venues.dto.VenueTemplatePriceTypeRestrictionDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateRestrictionsDTO;
import es.onebox.mgmt.venues.dto.VenueTemplatesFilterOptionsRequest;
import es.onebox.mgmt.venues.dto.VenueTemplatesSeatExportRequest;
import es.onebox.mgmt.venues.dto.VenueTemplatesSectorExportRequest;
import es.onebox.mgmt.venues.dto.VenueTemplatesViewExportRequest;
import es.onebox.mgmt.venues.dto.capacity.QuotaCapacityDTO;
import es.onebox.mgmt.venues.dto.capacity.QuotaCapacityListDTO;
import es.onebox.mgmt.venues.service.VenueTemplateRestrictionsService;
import es.onebox.mgmt.venues.service.VenueTemplatesService;
import es.onebox.venue.venuetemplates.VenueMapProto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_EDI;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(VenueTemplatesController.BASE_URI)
public class VenueTemplatesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/venue-templates";

    private static final String AUDIT_COLLECTION = "VENUE_TEMPLATES";
    private static final String AUDIT_SUBCOLLECTION_TAGS = "VENUE_TEMPLATES_TAG";
    private static final String AUDIT_SUBCOLLECTION_AVET_CAPACITY = "VENUE_TEMPLATES_AVET_COMPETITIONS";
    private static final String AUDIT_SUBCOLLECTION_PRICE_TYPES = "VENUE_TEMPLATES_PRICE_TYPES";
    private static final String AUDIT_SUBCOLLECTION_INTERACTIVE_VENUE = "VENUE_TEMPLATES_INTERACTI_VEVENUE";
    private static final String AUDIT_EXPORTS_VENUE = "VENUE_TEMPLATES_EXPORTS";
    private static final String AUDIT_EXPORTS_STATUS_VENUE = "VENUE_TEMPLATES_EXPORTS_STATUS";

    private static final String TEMPLATE_ID_MUST_BE_ABOVE_0 = "Template Id must be above 0";
    private static final String PRICE_TYPE_ID_MUST_BE_ABOVE_0 = "Price type Id must be above 0";

    private static final String SECTOR_TYPE = "SECTORS";
    private static final String SEAT_TYPE = "SEATS";
    private static final String VIEW_TYPE = "VIEWS";

    private final ExportService exportService;
    private final VenueTemplatesService venueTemplatesService;
    private final VenueTemplateRestrictionsService venueTemplateRestrictionsService;

    @Autowired
    public VenueTemplatesController(ExportService exportService, VenueTemplatesService venueTemplatesService,
                                    VenueTemplateRestrictionsService venueTemplateRestrictionsService) {
        this.exportService = exportService;
        this.venueTemplatesService = venueTemplatesService;
        this.venueTemplateRestrictionsService = venueTemplateRestrictionsService;
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{venueTemplateId}")
    public VenueTemplateDetailsDTO getVenueTemplate(@PathVariable(value = "venueTemplateId") Long venueTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return venueTemplatesService.getVenueTemplate(venueTemplateId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CNL_MGR, ROLE_ENT_MGR})
    @GetMapping
    public SearchVenueTemplatesResponse getVenueTemplates(@BindUsingJackson @Valid VenueTemplateFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return venueTemplatesService.findVenueTemplates(filter);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createVenueTemplate(@Validated @RequestBody CreateTemplateRequestDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        if (StringUtils.isEmpty(body.getName())) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "name is mandatory", null);
        }
        if (body.getFromTemplateId() == null) {
            if (body.getVenueId() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "venue_id is mandatory", null);
            }
            if (body.getSpaceId() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "space_id is mandatory", null);
            }
        }
        if (body.getEventId() == null && body.getScope() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "event_id or scope is mandatory", null);
        }

        Long templateId = venueTemplatesService.createVenueTemplate(body);

        return new IdDTO(templateId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{venueTemplateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVenueTemplate(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                    @RequestBody UpdateTemplateRequestDTO templateData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        venueTemplatesService.updateVenueTemplate(venueTemplateId, templateData);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{venueTemplateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenueTemplate(@PathVariable(value = "venueTemplateId") Long venueTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        venueTemplatesService.deleteVenueTemplate(venueTemplateId);
    }


    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_REC_EDI})
    @GetMapping( value = "/{venueTemplateId}/map", produces = {"application/x-protobuf"})
    public byte[] getVenueTemplateMapProtobuf(@PathVariable(value = "venueTemplateId") Long venueTemplateId) throws IOException {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        InputStream is = venueTemplatesService.getVenueTemplateMap(venueTemplateId);
        return IOUtils.toByteArray(is);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_REC_EDI})
    @GetMapping(value = "/{venueTemplateId}/map", produces = {MediaType.APPLICATION_JSON_VALUE})
    public VenueMapProto.VenueMap getVenueTemplateMapJson(@PathVariable(value = "venueTemplateId") Long venueTemplateId) throws IOException {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        InputStream is = venueTemplatesService.getVenueTemplateMap(venueTemplateId);
        return VenueMapProto.VenueMap.parseFrom(is);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{venueTemplateId}/blocking-reasons")
    public List<BlockingReasonDTO> getVenueTemplateBlockingReasons(@PathVariable(value = "venueTemplateId") Long venueTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);

        return venueTemplatesService.getBlockingReasons(venueTemplateId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/{venueTemplateId}/blocking-reasons")
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createBlockingReason(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                      @RequestBody BlockingReasonRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_CREATE);

        if (requestDTO.getName() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "name is mandatory", null);
        }

        return new IdDTO(venueTemplatesService.createBlockingReason(venueTemplateId, requestDTO));
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{venueTemplateId}/blocking-reasons/{blockingReasonId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBlockingReason(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                     @PathVariable(value = "blockingReasonId") Long blockingReasonId,
                                     @RequestBody BlockingReasonRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_UPDATE);

        venueTemplatesService.updateBlockingReason(venueTemplateId, blockingReasonId, requestDTO);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{venueTemplateId}/blocking-reasons/{blockingReasonId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlockingReason(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                     @PathVariable(value = "blockingReasonId") Long blockingReasonId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_DELETE);

        venueTemplatesService.deleteBlockingReason(venueTemplateId, blockingReasonId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{venueTemplateId}/price-types")
    public List<PriceTypeDTO> getVenueTemplatePriceTypes(@PathVariable(value = "venueTemplateId") Long venueTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);

        return venueTemplatesService.getPriceTypes(venueTemplateId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/{venueTemplateId}/price-types")
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createPriceType(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                 @RequestBody PriceTypeRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_CREATE);

        return new IdDTO(venueTemplatesService.createPriceType(venueTemplateId, requestDTO));
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{venueTemplateId}/price-types/{priceTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePriceType(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                @PathVariable(value = "priceTypeId") Long priceTypeId,
                                @RequestBody PriceTypeRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_UPDATE);

        venueTemplatesService.updatePriceType(venueTemplateId, priceTypeId, requestDTO);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{venueTemplateId}/price-types/{priceTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePriceType(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                @PathVariable(value = "priceTypeId") Long priceTypeId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_DELETE);

        venueTemplatesService.deletePriceType(venueTemplateId, priceTypeId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{venueTemplateId}/quotas")
    public List<QuotaDTO> getVenueTemplateQuotas(@PathVariable(value = "venueTemplateId") Long venueTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);

        return venueTemplatesService.getQuotas(venueTemplateId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/{venueTemplateId}/quotas")
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createQuota(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                             @RequestBody CreateVenueTagConfigRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_CREATE);

        return new IdDTO(venueTemplatesService.createQuota(venueTemplateId, requestDTO));
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{venueTemplateId}/quotas/{quotaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateQuota(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                            @PathVariable(value = "quotaId") Long quotaId,
                            @RequestBody VenueTagConfigRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_UPDATE);

        venueTemplatesService.updateQuota(venueTemplateId, quotaId, requestDTO);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{venueTemplateId}/quotas/{quotaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuota(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                            @PathVariable(value = "quotaId") Long quotaId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_DELETE);

        venueTemplatesService.deleteQuota(venueTemplateId, quotaId);

    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{venueTemplateId}/gates")
    public List<GateDTO> getVenueTemplateGates(@PathVariable(value = "venueTemplateId") Long venueTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);

        return venueTemplatesService.getGates(venueTemplateId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/{venueTemplateId}/gates")
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createGate(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                            @RequestBody GateRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_CREATE);

        return new IdDTO(venueTemplatesService.createGate(venueTemplateId, requestDTO));
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{venueTemplateId}/gates/{gateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGate(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                           @PathVariable(value = "gateId") Long gateId,
                           @RequestBody GateRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_UPDATE);

        venueTemplatesService.updateGate(venueTemplateId, gateId, requestDTO);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{venueTemplateId}/gates/{gateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGate(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                           @PathVariable(value = "gateId") Long gateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_DELETE);

        venueTemplatesService.deleteGate(venueTemplateId, gateId);
    }


    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, Codes.ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{venueTemplateId}/avet-competitions")
    public List<CompetitionDTO> getAvetCompetitions(
            @PathVariable Long venueTemplateId,
            @RequestParam(value = "event_entity_id") Long eventEntityId,
            @RequestParam(value = "skip_used", required = false, defaultValue = "false") boolean skipUsed) {

        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_SUBCOLLECTION_AVET_CAPACITY, AuditTag.AUDIT_ACTION_SEARCH);
        return venueTemplatesService.getAvetCompetitions(venueTemplateId, eventEntityId, skipUsed);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/{venueTemplateId}/clone")
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO cloneVenueTemplate(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                    @Valid @RequestBody CloneTemplateRequestDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CLONE);
        return new IdDTO(venueTemplatesService.cloneVenueTemplate(venueTemplateId, request));
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/filters/{filter}")
    public IdNameListWithMetadata getVenueTemplatesFilterOptions(@PathVariable String filter,
                                                                 @BindUsingJackson @Valid VenueTemplatesFilterOptionsRequest request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return venueTemplatesService.getVenueTemplatesFilterOptions(filter, request);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{venueTemplateId}/price-types/{priceTypeId}/channel-contents")
    public PriceTypeChannelContentsListDTO getPriceTypeCommElements(@PathVariable Long venueTemplateId,
                                                                    @PathVariable Long priceTypeId,
                                                                    @BindUsingJackson @Valid PriceTypeChannelContentFilterDTO filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_SUBCOLLECTION_PRICE_TYPES, AuditTag.AUDIT_ACTION_GET);
        return venueTemplatesService.getPriceTypeCommElements(venueTemplateId, priceTypeId, filter);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/{venueTemplateId}/price-types/{priceTypeId}/channel-contents")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void upsertPriceTypeCommElements(@PathVariable Long venueTemplateId,
                                            @PathVariable Long priceTypeId,
                                            @RequestBody PriceTypeChannelContentsListDTO commElements) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_SUBCOLLECTION_PRICE_TYPES, AuditTag.AUDIT_ACTION_UPDATE);
        if (commElements == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                    "communication elements are mandatory", null);
        }

        venueTemplatesService.upsertPriceTypeCommElements(venueTemplateId, priceTypeId, commElements);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{venueTemplateId}/capacity/quotas")
    public List<QuotaCapacityDTO> getVenueTemplateQuotasCapacity(@PathVariable(value = "venueTemplateId") Long venueTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);

        return venueTemplatesService.getQuotasCapacity(venueTemplateId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{venueTemplateId}/capacity/quotas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVenueTemplateQuotasCapacities(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                                    @RequestBody QuotaCapacityListDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_UPDATE);

        venueTemplatesService.updateQuotasCapacity(venueTemplateId, requestDTO);

    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{venueTemplateId}/interactive-venue")
    public InteractiveVenueDTO getInteractiveVenue(@PathVariable(value = "venueTemplateId") Long venueTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_INTERACTIVE_VENUE, AuditTag.AUDIT_ACTION_GET);

        return venueTemplatesService.getInteractiveVenue(venueTemplateId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{venueTemplateId}/interactive-venue")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateInteractiveVenue(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                       @Valid @RequestBody InteractiveVenueRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_INTERACTIVE_VENUE, AuditTag.AUDIT_ACTION_UPDATE);

        venueTemplatesService.updateInteractiveVenue(venueTemplateId, requestDTO);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @PostMapping("/{venueTemplateId}/exports/sectors")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportResponse exportSectors(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                        @Valid @RequestBody VenueTemplatesSectorExportRequest body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_EXPORTS_VENUE, AuditTag.AUDIT_ACTION_EXPORT);
        return exportService.exportVenueTemplatesSectors(venueTemplateId, body);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @PostMapping("/{venueTemplateId}/exports/seats")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportResponse exportSeats(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                      @Valid @RequestBody VenueTemplatesSeatExportRequest body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_EXPORTS_VENUE, AuditTag.AUDIT_ACTION_EXPORT);
        return exportService.exportVenueTemplatesSeats(venueTemplateId, body);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @PostMapping("/{venueTemplateId}/exports/views")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportResponse exportViews(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                      @RequestBody VenueTemplatesViewExportRequest body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_EXPORTS_VENUE, AuditTag.AUDIT_ACTION_EXPORT);
        return exportService.exportVenueTemplatesViews(venueTemplateId, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{venueTemplateId}/exports/{type}/{exportId}")
    public ExportStatusResponse getExportInfo(@PathVariable Long venueTemplateId, @PathVariable String type,
                                              @PathVariable String exportId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_EXPORTS_STATUS_VENUE, AuditTag.AUDIT_ACTION_GET);
        return switch (type) {
            case SECTOR_TYPE, SEAT_TYPE, VIEW_TYPE ->
                    exportService.checkVenueTemplatesStatus(venueTemplateId, exportId, type);
            default -> throw ExceptionBuilder.build(ApiMgmtErrorCode.VENUE_TEMPLATE_WRONG_EXPORT_TYPE);
        };
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping("/{venueTemplateId}/price-types/{priceTypeId}/restrictions")
    public VenueTemplatePriceTypeRestrictionDTO getVenueTemplatePriceTypeRestrictions(
            @PathVariable @Min(value = 1, message = TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
            @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_SUBCOLLECTION_PRICE_TYPES, AuditTag.AUDIT_ACTION_GET);
        return venueTemplateRestrictionsService.getVenueTemplatePriceTypeRestrictions(venueTemplateId, priceTypeId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/{venueTemplateId}/price-types/{priceTypeId}/restrictions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void upsertVenueTemplatePriceTypeRestrictions(
            @PathVariable @Min(value = 1, message = TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
            @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
            @RequestBody @Valid CreateVenueTemplatePriceTypeRestrictionDTO restrictions) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_SUBCOLLECTION_PRICE_TYPES, AuditTag.AUDIT_ACTION_UPDATE);
        if (restrictions == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                    "restrictions are mandatory", null);
        }

        venueTemplateRestrictionsService.upsertVenueTemplatePriceTypeRestrictions(venueTemplateId, priceTypeId, restrictions);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{venueTemplateId}/price-types/{priceTypeId}/restrictions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenueTemplatePriceTypeRestriction(
            @PathVariable @Min(value = 1, message = TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
            @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        venueTemplateRestrictionsService.deleteVenueTemplatePriceTypeRestriction(venueTemplateId, priceTypeId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping("/{venueTemplateId}/restricted-price-types")
    public VenueTemplateRestrictionsDTO getAllVenueTemplateRestrictions(
            @PathVariable @Min(value = 1, message = TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_SUBCOLLECTION_PRICE_TYPES, AuditTag.AUDIT_ACTION_GET);
        return venueTemplateRestrictionsService.getAllVenueTemplateRestrictions(venueTemplateId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{venueTemplateId}/external-capacity")
    public LoadedCapacityExternalDTO getExternalCapacity(@PathVariable @Min(value = 1, message = "venueTemplateId must be above 0") Integer venueTemplateId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_SUBCOLLECTION_AVET_CAPACITY, AuditTag.AUDIT_ACTION_GET);
        return venueTemplatesService.getAvetCapacity(venueTemplateId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{venueTemplateId}/dynamic-tag-groups")
    public List<DynamicTagGroupDTO> getDynamicTagGroups(
            @PathVariable @Min(value = 1, message = TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);
        return venueTemplatesService.getDynamicTagGroups(venueTemplateId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/{venueTemplateId}/dynamic-tag-groups")
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createDynamicTagGroup(
            @PathVariable @Min(value = 1, message = TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
            @Valid @RequestBody CreateDynamicTagGroupRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_CREATE);
        return new IdDTO(venueTemplatesService.createDynamicTagGroup(venueTemplateId, requestDTO));
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{venueTemplateId}/dynamic-tag-groups/{tagGroupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDynamicTagGroup(
            @PathVariable @Min(value = 1, message = TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
            @PathVariable Long tagGroupId,
            @Valid @RequestBody UpdateDynamicTagGroupRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_UPDATE);
        venueTemplatesService.updateDynamicTagGroup(venueTemplateId, tagGroupId, requestDTO);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{venueTemplateId}/dynamic-tag-groups/{tagGroupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDynamicTagGroup(
            @PathVariable @Min(value = 1, message = TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
            @PathVariable Long tagGroupId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_DELETE);
        venueTemplatesService.deleteDynamicTagGroup(venueTemplateId, tagGroupId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{venueTemplateId}/dynamic-tag-groups/{tagGroupId}/tags")
    public List<DynamicTagDTO> getDynamicTagGroupTags(
            @PathVariable @Min(value = 1, message = TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
            @PathVariable Long tagGroupId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);
        return venueTemplatesService.getDynamicTagGroupTags(venueTemplateId, tagGroupId);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/{venueTemplateId}/dynamic-tag-groups/{tagGroupId}/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createDynamicTagGroupTag(@PathVariable Long venueTemplateId,
                                          @PathVariable Long tagGroupId,
                                          @RequestBody CreateVenueTagConfigRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_CREATE);

        return new IdDTO(venueTemplatesService.createDynamicTagGroupTag(venueTemplateId, tagGroupId, requestDTO));
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{venueTemplateId}/dynamic-tag-groups/{tagGroupId}/tags/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDynamicTagGroupTag(@PathVariable Long venueTemplateId,
                                         @PathVariable Long tagGroupId,
                                         @PathVariable Long tagId,
                                         @RequestBody VenueTagConfigRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_UPDATE);
        venueTemplatesService.updateDynamicTagGroupTag(venueTemplateId, tagGroupId, tagId, requestDTO);
    }

    @Secured({ROLE_REC_MGR, ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{venueTemplateId}/dynamic-tag-groups/{tagGroupId}/tags/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDynamicTagGroupTag(@PathVariable Long venueTemplateId,
                                         @PathVariable Long tagGroupId,
                                         @PathVariable Long tagId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_DELETE);
        venueTemplatesService.deleteDynamicTagGroupTag(venueTemplateId, tagGroupId, tagId);
    }
}
