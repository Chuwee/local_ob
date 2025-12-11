package es.onebox.mgmt.venues.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.common.IdNameListWithMetadata;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.common.dto.CreateVenueTemplateRequest;
import es.onebox.mgmt.datasources.common.dto.QuotaCapacity;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.CompetitionDTO;
import es.onebox.mgmt.datasources.integration.avetconfig.repository.AvetConfigRepository;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSearchFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventType;
import es.onebox.mgmt.datasources.ms.event.dto.event.Events;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.Sessions;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.PriceTypeCommunicationElement;
import es.onebox.mgmt.datasources.ms.venue.dto.Venue;
import es.onebox.mgmt.datasources.ms.venue.dto.template.BlockingReason;
import es.onebox.mgmt.datasources.ms.venue.dto.template.DynamicTag;
import es.onebox.mgmt.datasources.ms.venue.dto.template.DynamicTagGroup;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Gate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.InteractiveVenue;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Quota;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateStatus;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplates;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplatesFilter;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplatesFiltersRequest;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.entities.EntitiesService;
import es.onebox.mgmt.entities.converter.CapacityConverter;
import es.onebox.mgmt.entities.factory.InventoryProviderService;
import es.onebox.mgmt.entities.factory.InventoryProviderServiceFactory;
import es.onebox.mgmt.events.dto.LoadedCapacityExternalDTO;
import es.onebox.mgmt.events.enums.EventStatus;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtVenueErrorCode;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandler;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandlerStrategyProvider;
import es.onebox.mgmt.seasontickets.service.ReleasedSeatsQuotaHelper;
import es.onebox.mgmt.seasontickets.service.SeasonTicketChangeSeatsService;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.SessionUtils;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.converter.VenueTemplateBlockingReasonConverter;
import es.onebox.mgmt.venues.converter.VenueTemplateConverter;
import es.onebox.mgmt.venues.converter.VenueTemplateDynamicTagsConverter;
import es.onebox.mgmt.venues.converter.VenueTemplateGateConverter;
import es.onebox.mgmt.venues.converter.VenueTemplateInteractiveVenueConverter;
import es.onebox.mgmt.venues.converter.VenueTemplatePriceTypesConverter;
import es.onebox.mgmt.venues.converter.VenueTemplateQuotaConverter;
import es.onebox.mgmt.venues.converter.VenueTemplateScopeConverter;
import es.onebox.mgmt.venues.converter.VenueTemplateTypeConverter;
import es.onebox.mgmt.venues.converter.VenueTemplatesConverter;
import es.onebox.mgmt.venues.converter.VenueTemplatesFilterConverter;
import es.onebox.mgmt.venues.dto.AdditionalConfigDTO;
import es.onebox.mgmt.venues.dto.BlockingReasonDTO;
import es.onebox.mgmt.venues.dto.BlockingReasonRequestDTO;
import es.onebox.mgmt.venues.dto.CloneTemplateRequestDTO;
import es.onebox.mgmt.venues.dto.CreateDynamicTagGroupRequestDTO;
import es.onebox.mgmt.venues.dto.CreateTemplateRequestDTO;
import es.onebox.mgmt.venues.dto.CreateVenueTagConfigRequestDTO;
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
import es.onebox.mgmt.venues.dto.VenueTemplateFilterScoped;
import es.onebox.mgmt.venues.dto.VenueTemplatesFilterOptionsRequest;
import es.onebox.mgmt.venues.dto.capacity.QuotaCapacityDTO;
import es.onebox.mgmt.venues.dto.capacity.QuotaCapacityListDTO;
import es.onebox.mgmt.venues.enums.VenueTemplateScopeDTO;
import es.onebox.mgmt.venues.enums.VenueTemplateTypeDTO;
import es.onebox.mgmt.venues.enums.VenueTemplatesFilterOption;
import es.onebox.mgmt.venues.utils.VenueTemplateUtils;
import es.onebox.mgmt.venues.utils.VenueValidationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.onebox.core.security.Roles.ROLE_ENT_ADMIN;
import static es.onebox.core.utils.common.CommonUtils.ifNull;

@Service
public class VenueTemplatesService {

    public static final int VALID_IMAGE_WIDTH = 550;
    public static final int VALID_IMAGE_HEIGHT = 340;

    private final VenuesRepository venuesRepository;
    private final EventsRepository eventsRepository;
    private final EntitiesRepository entitiesRepository;
    private final TicketsRepository ticketsRepository;
    private final SecurityManager securityManager;
    private final AvetConfigRepository avetConfigRepository;
    private final MasterdataService masterdataService;
    private final VenueValidationUtils venueValidationUtils;
    private final ValidationService validationService;
    private final EntitiesService entitiesService;
    private final SeasonTicketChangeSeatsService seasonTicketChangeSeatsService;
    private final InventoryProviderServiceFactory inventoryProviderServiceFactory;
    private final ReleasedSeatsQuotaHelper releasedSeatsQuotaHelper;
    private final AccessControlSystemsRepository accessControlSystemsRepository;
    private final ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider;

    @Autowired
    public VenueTemplatesService(VenuesRepository venuesRepository,
                                 final EventsRepository eventsRepository,
                                 final EntitiesRepository entitiesRepository,
                                 final TicketsRepository ticketsRepository,
                                 final SecurityManager securityManager,
                                 final AvetConfigRepository avetConfigRepository,
                                 final MasterdataService masterdataService,
                                 final VenueValidationUtils venueValidationUtils,
                                 final ValidationService validationService,
                                 final EntitiesService entitiesService,
                                 final SeasonTicketChangeSeatsService seasonTicketChangeSeatsService,
                                 InventoryProviderServiceFactory inventoryProviderServiceFactory,
                                 final ReleasedSeatsQuotaHelper releasedSeatsQuotaHelper, AccessControlSystemsRepository accessControlSystemsRepository, ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider) {
        this.venuesRepository = venuesRepository;
        this.eventsRepository = eventsRepository;
        this.entitiesRepository = entitiesRepository;
        this.ticketsRepository = ticketsRepository;
        this.securityManager = securityManager;
        this.avetConfigRepository = avetConfigRepository;
        this.masterdataService = masterdataService;
        this.venueValidationUtils = venueValidationUtils;
        this.validationService = validationService;
        this.entitiesService = entitiesService;
        this.seasonTicketChangeSeatsService = seasonTicketChangeSeatsService;
        this.inventoryProviderServiceFactory = inventoryProviderServiceFactory;
        this.releasedSeatsQuotaHelper = releasedSeatsQuotaHelper;
        this.accessControlSystemsRepository = accessControlSystemsRepository;
        this.externalAccessControlHandlerStrategyProvider = externalAccessControlHandlerStrategyProvider;
    }

    public VenueTemplateDetailsDTO getVenueTemplate(Long venueTemplateId) {
        if (venueTemplateId == null || venueTemplateId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "venueTemplateId must be a positive integer", null);
        }

        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId, true);

        return VenueTemplateConverter.convertToDetails(venueTemplate);
    }

    public SearchVenueTemplatesResponse findVenueTemplates(VenueTemplateFilterDTO filter) {
        securityManager.checkEntityAccessible(filter);

        prepareFilterByEventOrScope(filter);

        List<Long> visibleEntities = null;
        if (CommonUtils.isTrue(filter.getIncludeThirdPartyTemplates())) {
            visibleEntities = securityManager.getVisibleEntities(SecurityUtils.getUserEntityId());
        }

        VenueTemplatesFilter templatesFilter = VenueTemplatesFilterConverter.convert(filter, visibleEntities);
        VenueTemplates venueTemplates = venuesRepository.getVenueTemplates(SecurityUtils.getUserOperatorId(),
                templatesFilter, filter.getSort(), filter.getFields());

        return VenueTemplatesConverter.fromMsVenue(venueTemplates);
    }

    public Long createVenueTemplate(CreateTemplateRequestDTO request) {

        validateTemplateCreation(request);

        if (request.getImage() != null && request.getImage().isPresent()) {
            FileUtils.checkImage(request.getImage().get(), VALID_IMAGE_WIDTH, VALID_IMAGE_HEIGHT, "VENUE_TEMPLATE");
        }
        String providerId = request.getAdditionalConfig() == null || request.getAdditionalConfig().getInventoryProvider() == null ?
                null : request.getAdditionalConfig().getInventoryProvider().getCode();

        InventoryProviderService inventoryProviderService =
                inventoryProviderServiceFactory.getIntegrationService(request.getEntityId(), providerId);

        CreateVenueTemplateRequest venueTemplateRequest = VenueTemplateConverter.convertToDatasource(request);

        Long venueTemplateId = inventoryProviderService.createVenueTemplate(venueTemplateRequest);

        if (venueTemplateRequest.getEventId() != null && venueTemplateRequest.getVenueId() != null
                && venueTemplateId != null && venueTemplateRequest.getEntityId() != null) {
            checkAndProcessExternalVenueTemplates(venueTemplateRequest.getEntityId(), venueTemplateRequest.getVenueId(), venueTemplateId);
        }

        return venueTemplateId;
    }

    public void updateVenueTemplate(Long venueTemplateId, UpdateTemplateRequestDTO templateData) {
        VenueTemplate venueTemplate = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        validateUpdateVenueTemplate(templateData, venueTemplate);

        venuesRepository.updateVenueTemplate(venueTemplateId, VenueTemplateConverter.toMsVenue(templateData));

        if (venueTemplate.getEventId() != null && venueTemplate.getVenue() != null
                && venueTemplate.getVenue().getId() != null && venueTemplate.getEntityId() != null) {
            checkAndProcessExternalVenueTemplates(venueTemplate.getEntityId(), venueTemplate.getVenue().getId(), venueTemplateId);
        }
    }

    private void validateUpdateVenueTemplate(UpdateTemplateRequestDTO templateData, VenueTemplate venueTemplate) {
        if (templateData.getImage() != null && templateData.getImage().isPresent()) {
            FileUtils.checkImage(templateData.getImage().get(), VALID_IMAGE_WIDTH, VALID_IMAGE_HEIGHT, "VENUE_TEMPLATE");
        }

        if (templateData.getVenueId() != null) {
            if (VenueTemplateType.ACTIVITY.equals(venueTemplate.getTemplateType()) || VenueTemplateType.THEME_PARK.equals(venueTemplate.getTemplateType())) {
                Venue venue = venueValidationUtils.validateVenueId(templateData.getVenueId());
                securityManager.checkEntityAccessible(venue.getEntity().getId());
            } else {
                throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN, "Update of venue_id only available for this templateType", null);
            }
        }
    }

    public void deleteVenueTemplate(Long venueTemplateId) {
        VenueTemplate venueTemplate = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        UpdateVenueTemplate updateVenueTemplate = new UpdateVenueTemplate();
        updateVenueTemplate.setStatus(VenueTemplateStatus.DELETED);

        InventoryProviderService inventoryProviderService =
                inventoryProviderServiceFactory.getIntegrationService(venueTemplate.getEntityId(), venueTemplate.getInventoryProvider());

        inventoryProviderService.deleteVenueTemplate(venueTemplate.getEntityId(), venueTemplateId, updateVenueTemplate);
    }

    public InputStream getVenueTemplateMap(Long venueTemplateId) {
        if (venueTemplateId == null || venueTemplateId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "venueTemplateId must be a positive integer", null);
        }

        validationService.getAndCheckVenueTemplate(venueTemplateId);

        return venuesRepository.getVenueTemplateMap(venueTemplateId);
    }

    public List<BlockingReasonDTO> getBlockingReasons(Long venueTemplateId) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);

        List<BlockingReason> blockingReasons = venuesRepository.getBlockingReasons(venueTemplateId);
        blockingReasons = VenueTemplateUtils.filterSocialDistancingBlockingReasons(venueTemplate, blockingReasons);
        return blockingReasons.stream().map(VenueTemplateBlockingReasonConverter::fromMsEvent).collect(Collectors.toList());
    }

    public Long createBlockingReason(Long venueTemplateId, BlockingReasonRequestDTO requestDTO) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        return venuesRepository.createBlockingReason(venueTemplateId, VenueTemplateBlockingReasonConverter.toMsEvent(requestDTO));
    }

    public void updateBlockingReason(Long venueTemplateId, Long blockingReasonId, BlockingReasonRequestDTO requestDTO) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        venuesRepository.updateBlockingReason(venueTemplateId, blockingReasonId, VenueTemplateBlockingReasonConverter.toMsEvent(requestDTO));
    }

    public void deleteBlockingReason(Long venueTemplateId, Long blockingReasonId) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        List<BlockingReasonDTO> blockingReasons = getBlockingReasons(venueTemplateId);
        BlockingReasonDTO blockingReason = blockingReasons.stream().filter(b -> b.getId().equals(blockingReasonId))
                .findFirst().orElseThrow(() -> OneboxRestException.builder(ApiMgmtVenueErrorCode.BLOCKING_REASON_NOT_FOUND).build());
        venuesRepository.deleteBlockingReason(venueTemplateId, blockingReasonId);
    }

    public List<PriceTypeDTO> getPriceTypes(Long venueTemplateId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        List<PriceType> priceTypes = venuesRepository.getPriceTypes(venueTemplateId);
        return priceTypes.stream().map(VenueTemplatePriceTypesConverter::fromMsEvent).collect(Collectors.toList());
    }

    public Long createPriceType(Long venueTemplateId, PriceTypeRequestDTO requestDTO) {
        VenueTemplate venueTemplate = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        if (venueTemplate.getTemplateType().equals(VenueTemplateType.AVET)) {
            throw new OneboxRestException(ApiMgmtErrorCode.VENUE_TEMPLATE_PRICE_TYPE_CREATE_AVET_NOT_ALLOWED);
        }

        Long priceType = venuesRepository.createPriceType(venueTemplateId, requestDTO);

        if (venueTemplate.getEventId() != null) {
            seasonTicketChangeSeatsService.handleNewPriceZone(venueTemplate.getEventId());
        }

        return priceType;
    }

    public void updatePriceType(Long venueTemplateId, Long priceTypeId, PriceTypeRequestDTO requestDTO) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        venuesRepository.updatePriceType(venueTemplateId, priceTypeId, requestDTO);
    }

    public void deletePriceType(Long venueTemplateId, Long priceTypeId) {
        VenueTemplate venueTemplate = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        if (venueTemplate.getTemplateType().equals(VenueTemplateType.AVET)) {
            throw new OneboxRestException(ApiMgmtErrorCode.VENUE_TEMPLATE_PRICE_TYPE_DELETE_AVET_NOT_ALLOWED);
        }

        venuesRepository.deletePriceType(venueTemplateId, priceTypeId);
    }

    public List<QuotaDTO> getQuotas(Long venueTemplateId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        List<Quota> quotas = venuesRepository.getQuotas(venueTemplateId);
        return quotas.stream().map(VenueTemplateQuotaConverter::fromMsEvent).collect(Collectors.toList());
    }

    public Long createQuota(Long venueTemplateId, CreateVenueTagConfigRequestDTO requestDTO) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        if (hasReleasedSeatsQuotaCode(requestDTO.getCode())) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_RELEASED_SEAT_QUOTA);
        }

        return venuesRepository.createQuota(venueTemplateId, requestDTO);
    }

    private static boolean hasReleasedSeatsQuotaCode(String code) {
        return code != null && code.equals(ReleasedSeatsQuotaHelper.RELEASED_SEAT_QUOTA_CODE);
    }




    public void updateQuota(Long venueTemplateId, Long quotaId, VenueTagConfigRequestDTO requestDTO) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        validateReleasedSeatQuotaUpdate(venueTemplateId, quotaId, requestDTO);

        venuesRepository.updateQuota(venueTemplateId, quotaId, requestDTO);
    }

    private void validateReleasedSeatQuotaUpdate(Long venueTemplateId, Long quotaId, VenueTagConfigRequestDTO requestDTO) {
        boolean isReleasedSeatQuota = releasedSeatsQuotaHelper.isReleasedSeatQuota(quotaId, venueTemplateId);
        boolean hasReleasedSeatQuotaCode = hasReleasedSeatsQuotaCode(requestDTO.getCode());
        if (isReleasedSeatQuota && !hasReleasedSeatQuotaCode ||
                !isReleasedSeatQuota && hasReleasedSeatQuotaCode) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_RELEASED_SEAT_QUOTA);
        }
    }

    public void deleteQuota(Long venueTemplateId, Long quotaId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        venuesRepository.deleteQuota(venueTemplateId, quotaId);
    }

    public List<GateDTO> getGates(Long venueTemplateId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        List<Gate> gates = venuesRepository.getGates(venueTemplateId);
        return gates.stream().map(VenueTemplateGateConverter::fromMsEvent).collect(Collectors.toList());
    }

    public Long createGate(Long venueTemplateId, GateRequestDTO requestDTO) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        return venuesRepository.createGate(venueTemplateId, requestDTO);
    }

    public void updateGate(Long venueTemplateId, Long gateId, GateRequestDTO requestDTO) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        venuesRepository.updateGate(venueTemplateId, gateId, requestDTO);
    }

    public void deleteGate(Long venueTemplateId, Long gateId) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        venuesRepository.deleteGate(venueTemplateId, gateId);
    }

    private Long validateEventTemplateCreation(Long eventId, Boolean graphic, Long fromTemplateId) {
        Long templateEntityId;
        Event event = validationService.getAndCheckEventExternalWithoutSecurity(eventId);
        if (fromTemplateId == null && event.getType().equals(EventType.NORMAL) && graphic == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "graphic is mandatory on NORMAL type events", null);
        }
        if (event.getType().equals(EventType.AVET)) {
            throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_RESOURCE, "AVET events cannot contain more than one template", null);
        }
        templateEntityId = event.getEntityId();
        return templateEntityId;
    }

    public List<CompetitionDTO> getAvetCompetitions(Long venueTemlateId, Long eventEntityId, boolean skipUsed) {
        if (eventEntityId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_ENTITY_ID_MANDATORY);
        }
        VenueTemplate venueTemplate = this.venuesRepository.getVenueTemplate(venueTemlateId);

        if (venueTemplate == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_FOUND);
        }

        securityManager.checkEntityAccessible(venueTemplate.getEntityId());

        Long avetCapacityId = venueTemplate.getAvetCapacityId();
        if (avetCapacityId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.VENUE_TEMPLATE_IS_NOT_AVET);
        }


        List<CompetitionDTO> avetCompetitions = this.avetConfigRepository.getCompetitions(avetCapacityId);
        List<Long> avetCompetitionIds = avetCompetitions
                .stream()
                .map(competitionDTO -> competitionDTO.getId().longValue())
                .collect(Collectors.toList());

        final EventSearchFilter filter = new EventSearchFilter();
        filter.setOperatorId(SecurityUtils.getUserOperatorId());
        filter.setStatus(EventStatus.actives().stream().map(Enum::name).toList());
        filter.setVenueId(venueTemplate.getVenue().getId());
        filter.setAvetCompetitions(avetCompetitionIds);

        Events events = eventsRepository.getEvents(filter);
        if (!venueTemplate.getEntityId().equals(eventEntityId) || skipUsed) {
            return avetCompetitions.stream()
                    .filter(competitionDTO -> events.getData().stream().noneMatch(event -> event.getExternalId().intValue() == competitionDTO.getId()))
                    .collect(Collectors.toList());
        }
        return avetCompetitions;
    }

    public Long cloneVenueTemplate(Long fromTemplateId, CloneTemplateRequestDTO cloneRequest) {
        VenueTemplate fromVenueTemplate = venuesRepository.getVenueTemplate(fromTemplateId);
        if (fromVenueTemplate == null || fromVenueTemplate.getTemplateType() == VenueTemplateType.AVET) {
            throw OneboxRestException
                    .builder(ApiMgmtErrorCode.VENUE_TEMPLATE_CLONE_AVET_NOT_ALLOWED).build();
        }

        CreateTemplateRequestDTO request = buildCreateTemplateFromClone(cloneRequest, fromVenueTemplate);
        validateTemplateCreation(request);

        CreateVenueTemplateRequest venueTemplateRequest = VenueTemplateConverter.convertToDatasource(request);

        return venuesRepository.createVenueTemplate(venueTemplateRequest);
    }

    private static CreateTemplateRequestDTO buildCreateTemplateFromClone(CloneTemplateRequestDTO cloneRequest, VenueTemplate fromVenueTemplate) {
        CreateTemplateRequestDTO request = new CreateTemplateRequestDTO();
        request.setName(cloneRequest.getName());
        request.setEntityId(ifNull(cloneRequest.getEntityId(), fromVenueTemplate.getEntityId()));
        request.setVenueId(cloneRequest.getVenueId());
        request.setSpaceId(cloneRequest.getSpaceId());
        request.setEventId(fromVenueTemplate.getEventId());
        request.setGraphic(fromVenueTemplate.getGraphic());
        request.setScope(VenueTemplateScopeConverter.toVenueTemplateScopeDTO(fromVenueTemplate.getScope()));
        request.setType(VenueTemplateTypeConverter.toVenueTemplateTypeDTO(fromVenueTemplate.getTemplateType()));
        request.setFromTemplateId(fromVenueTemplate.getId());
        if(fromVenueTemplate.getAvetCapacityId() != null){
            AdditionalConfigDTO additionalConfig = new AdditionalConfigDTO();
            additionalConfig.setCapacityId(fromVenueTemplate.getAvetCapacityId().intValue());
            request.setAdditionalConfig(additionalConfig);
        }
        return request;
    }

    private void validateTemplateCreation(CreateTemplateRequestDTO request) {

        Long templateEntityId = SecurityUtils.getUserEntityId();
        if(request.getEntityId() != null){
            templateEntityId = request.getEntityId();
        }

        if (request.getEventId() != null) {
            templateEntityId = validateEventTemplateCreation(request.getEventId(), request.getGraphic(), request.getFromTemplateId());
        } else if (VenueTemplateScopeDTO.STANDARD.equals(request.getScope())) {
            templateEntityId = VenueValidationUtils.validateStandardTemplate(request.getEntityId(), request.getType(), request.getGraphic(), request.getFromTemplateId());
        } else if (VenueTemplateScopeDTO.ARCHETYPE.equals(request.getScope())) {
            templateEntityId = venueValidationUtils.validateArchetypeTemplate(request.getVenueId(), templateEntityId, request.getGraphic(), request.getFromTemplateId());
        }

        securityManager.checkEntityAccessible(templateEntityId);

        if (request.getFromTemplateId() != null && venuesRepository.getVenueTemplate(request.getFromTemplateId()) == null) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.NOT_FOUND).
                    setMessage("There's no template with the given id").build();
        }

        if (request.getType() != null && request.getType().equals(VenueTemplateTypeDTO.AVET)) {
            if (request.getAdditionalConfig() == null || request.getAdditionalConfig().getCapacityId() == null) {
                throw OneboxRestException.builder(ApiMgmtVenueErrorCode.VENUE_TEMPLATE_AVET_CAPACITY_ID_MANDATORY).build();
            }

            // check that the given capacity ID belongs to the same club code than the entity ID
            if (!entitiesService.isCapacityIdFromEntity(request.getAdditionalConfig().getCapacityId(), request.getEntityId())) {
                throw OneboxRestException.builder(ApiMgmtVenueErrorCode.VENUE_TEMPLATE_AVET_CAPACITY_ID_ENTITY_MISMATCH).build();
            }
        }
    }

    public IdNameListWithMetadata getVenueTemplatesFilterOptions(String filterName, VenueTemplatesFilterOptionsRequest request) {
        VenueTemplatesFilterOption.validateFilter(filterName);
        prepareFilterByEventOrScope(request);
        Long entityId = null;
        Long entityAdminId = null;
        if(SecurityUtils.hasAnyRole(ROLE_ENT_ADMIN)){
            entityAdminId = SecurityUtils.getUserEntityId();
        }
        else if (SecurityUtils.getUserEntityId() != SecurityUtils.getUserOperatorId()) {
            entityId = SecurityUtils.getUserEntityId();
        }
        VenueTemplatesFiltersRequest filtersRequest = VenueTemplatesFilterConverter
                .convert(request, SecurityUtils.getUserOperatorId(), entityId, entityAdminId);

        return venuesRepository.getVenueTemplatesFilterOptions(filterName, filtersRequest);
    }

    private void prepareFilterByEventOrScope(VenueTemplateFilterScoped filter) {
        if (filter.getEventId() != null) {
            Event event = eventsRepository.getEvent(filter.getEventId());
            if (event == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.NOT_FOUND, "no event found with id: " + filter.getEventId(), null);
            }
            securityManager.checkEntityAccessible(event.getEntityId());
            filter.setScope(null);
        } else if (CommonUtils.isEmpty(filter.getScope())) {
            filter.setScope(Arrays.asList(VenueTemplateScopeDTO.ARCHETYPE, VenueTemplateScopeDTO.STANDARD));
        }
    }

    public PriceTypeChannelContentsListDTO getPriceTypeCommElements(Long venueTemplateId, Long priceTypeId,
                                                                    PriceTypeChannelContentFilterDTO filter) {
        if (venueTemplateId == null || venueTemplateId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_ID);
        }
        if (priceTypeId == null || priceTypeId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_ID);
        }
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        List<PriceTypeCommunicationElement> commElements = venuesRepository.getPriceTypeCommElements(venueTemplateId, priceTypeId, filter);
        return VenueTemplatePriceTypesConverter.fromMsVenue(commElements);
    }

    public void upsertPriceTypeCommElements(Long venueTemplateId, Long priceTypeId,
                                            PriceTypeChannelContentsListDTO commElements) {
        if (venueTemplateId == null || venueTemplateId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_ID);
        }
        if (priceTypeId == null || priceTypeId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_ID);
        }
        if (commElements == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                    "communication elements are mandatory", null);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        commElements.stream().peek(ce -> ConverterUtils.checkLanguage(ce.getLanguage(), languages)).forEach(el -> {
            if (el.getValue().length() > el.getType().getLength()) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.INVALID_COMM_ELEM_VALUE_LENGTH, el.getType().name(),
                        el.getType().getLength());
            }
        });

        validationService.getAndCheckVenueTemplate(venueTemplateId);
        venuesRepository.upsertPriceTypeCommElements(venueTemplateId, priceTypeId,
                VenueTemplatePriceTypesConverter.toMsVenue(commElements));
    }

    public List<QuotaCapacityDTO> getQuotasCapacity(Long venueTemplateId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        List<QuotaCapacity> capacities = venuesRepository.getQuotasCapacity(venueTemplateId);

        return VenueTemplateQuotaConverter.fromMsDTO(capacities);
    }

    public void updateQuotasCapacity(Long venueTemplateId, QuotaCapacityListDTO requestDTO) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        venuesRepository.updateQuotasCapacity(venueTemplateId,VenueTemplateQuotaConverter.toMsDTO(requestDTO));
        updateSessionsCapacity(venueTemplate);
    }

    public InteractiveVenueDTO getInteractiveVenue(Long venueTemplateId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        InteractiveVenue interactiveVenue = venuesRepository.getInteractiveVenue(venueTemplateId);

        return VenueTemplateInteractiveVenueConverter.fromMsDTO(interactiveVenue);
    }

    public void updateInteractiveVenue(Long venueTemplateId, InteractiveVenueRequestDTO requestDTO) {
        VenueTemplate venueTemplate = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);
        validateUpdateInteractiveVenue(venueTemplate, requestDTO);
        venuesRepository.updateInteractiveVenue(venueTemplateId,
                VenueTemplateInteractiveVenueConverter.toMsDTO(requestDTO));
    }

    private void validateUpdateInteractiveVenue(VenueTemplate venueTemplate, InteractiveVenueRequestDTO interactiveVenueToUpdate) {
        Entity entity = entitiesRepository.getEntity(venueTemplate.getEntityId());

        if (entity.getInteractiveVenue() == null || CommonUtils.isFalse(entity.getInteractiveVenue().getEnabled())) {
            throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_VENUE_TEMPLATE_INTERACTIVE_VENUE_UPDATE);
        }
        if (Boolean.TRUE.equals(interactiveVenueToUpdate.getEnabled())
                && CommonUtils.isBlank(interactiveVenueToUpdate.getMultimediaContentCode())) {
            throw new OneboxRestException(ApiMgmtErrorCode.VENUE_TEMPLATE_INTERACTIVE_VENUE_CODE_MANDATORY);
        }
    }

    public LoadedCapacityExternalDTO getAvetCapacity(Integer venueTemplateId) {
        validationService.getAndCheckVenueTemplate(Long.valueOf(venueTemplateId));
        return CapacityConverter.toDTO(avetConfigRepository.getCapacity(venueTemplateId));
    }

    public List<DynamicTagGroupDTO> getDynamicTagGroups(Long venueTemplateId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        List<DynamicTagGroup> tagGroups = venuesRepository.getVenueTemplateDynamicTagGroups(venueTemplateId);
        return VenueTemplateDynamicTagsConverter.fromMsVenueGroups(tagGroups);
    }

    public Long createDynamicTagGroup(Long venueTemplateId, CreateDynamicTagGroupRequestDTO request) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);
        DynamicTagGroup createRequest = new DynamicTagGroup();
        createRequest.setCode(request.getCode());
        createRequest.setName(request.getName());
        return venuesRepository.createVenueTemplateDynamicTagGroup(venueTemplateId, createRequest);
    }

    public void updateDynamicTagGroup(Long venueTemplateId, Long tagGroupId, UpdateDynamicTagGroupRequestDTO request) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);
        DynamicTagGroup updateRequest = new DynamicTagGroup();
        updateRequest.setCode(request.getCode());
        updateRequest.setName(request.getName());
        venuesRepository.updateVenueTemplateDynamicTagGroup(venueTemplateId, tagGroupId, updateRequest);
    }

    public void deleteDynamicTagGroup(Long venueTemplateId, Long tagGroupId) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);
        venuesRepository.deleteVenueTemplateDynamicTagGroup(venueTemplateId, tagGroupId);
    }

    public List<DynamicTagDTO> getDynamicTagGroupTags(Long venueTemplateId, Long tagGroupId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        List<DynamicTag> tags = venuesRepository.getDynamicTagGroupTags(venueTemplateId, tagGroupId);
        return VenueTemplateDynamicTagsConverter.fromMsVenueTags(tags);
    }

    public Long createDynamicTagGroupTag(Long venueTemplateId, Long tagGroupId, CreateVenueTagConfigRequestDTO request) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);
        return venuesRepository.createDynamicTagGroupTag(venueTemplateId, tagGroupId, request);
    }

    public void updateDynamicTagGroupTag(Long venueTemplateId, Long tagGroupId, Long tagId, VenueTagConfigRequestDTO requestDTO) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);
        venuesRepository.updateDynamicTagGroupTag(venueTemplateId, tagGroupId, tagId, requestDTO);
    }

    public void deleteDynamicTagGroupTag(Long venueTemplateId, Long tagGroupId, Long tagId) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);
        venuesRepository.deleteDynamicTagGroupTag(venueTemplateId, tagGroupId, tagId);
    }

    private void updateSessionsCapacity(VenueTemplate venueTemplate) {
        if (venueTemplate.getEventId() != null) {
            List<Session> sessions;
            SessionSearchFilter filter = new SessionSearchFilter();
            filter.setOffset(0L);
            filter.setLimit(1000L);
            filter.setVenueTemplateId(venueTemplate.getId());
            filter.setStatus(SessionUtils.notFinalized());

            Sessions sessionsToUpdate = eventsRepository.getSessions(SecurityUtils.getUserOperatorId(),
                    venueTemplate.getEventId(), filter);
            Long total = sessionsToUpdate.getMetadata().getTotal();
            sessions = new ArrayList<>(sessionsToUpdate.getData());

            while (total > filter.getLimit() + filter.getOffset()) {
                Long offset = filter.getLimit() + filter.getOffset();
                filter.setOffset(offset);
                sessions.addAll(eventsRepository.getSessions(SecurityUtils.getUserOperatorId(),
                        venueTemplate.getEventId(), filter).getData());
            }

            List<QuotaCapacity> venueCapacity = venuesRepository.getQuotasCapacity(venueTemplate.getId());
            sessions.forEach(session -> {
                if (BooleanUtils.isTrue(session.getUseVenueConfigCapacity())) {
                    ticketsRepository.updateQuotasCapacitySkipRefreshSession(venueTemplate.getEventId(), session.getId(), venueCapacity);
                }
            });
            Event emptyEventUpdateRequest = new Event(); //Update event to queue an event refresh.
            emptyEventUpdateRequest.setId(venueTemplate.getEventId());
            eventsRepository.updateEvent(emptyEventUpdateRequest);
        }
    }

    private void checkAndProcessExternalVenueTemplates(Long entityId, Long venueId, Long venueTemplateId) {
        if (venueTemplateId == null) {
            return;
        }

        List<AccessControlSystem> venueAccessControlSystems = accessControlSystemsRepository.findByVenueIdCached(venueId);

        if (CollectionUtils.isNotEmpty(venueAccessControlSystems)) {
            venueAccessControlSystems.stream().distinct().forEach(accessControlSystem -> {
                ExternalAccessControlHandler externalAccessControlHandler;
                externalAccessControlHandler = externalAccessControlHandlerStrategyProvider.provide(accessControlSystem.name());

                if (externalAccessControlHandler == null) {
                    return;
                }

                externalAccessControlHandler.addOrUpdateVenueElements(entityId, venueTemplateId);
            });
        }
    }
}
