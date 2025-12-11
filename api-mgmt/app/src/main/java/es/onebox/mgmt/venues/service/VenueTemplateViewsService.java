package es.onebox.mgmt.venues.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.session.Sessions;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateVipView;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateScope;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateView;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateViews;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtVenueErrorCode;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandler;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandlerStrategyProvider;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import es.onebox.mgmt.sessions.enums.SessionField;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.converter.VenueTemplateViewConverter;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateViewDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateViewDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateViewsDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateVipViewsDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateViewDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateViewsDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateViewsFilterDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class VenueTemplateViewsService {

    public static final String PREFIX_SVG = "rest_";

    private final VenuesRepository venuesRepository;
    private final EventsRepository eventsRepository;
    private final EntitiesRepository entitiesRepository;
    private final ValidationService validationService;
    private final String repositoryUrl;
    private final String repositoryBasePath;
    private final AccessControlSystemsRepository accessControlSystemsRepository;
    private final ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider;

    @Autowired
    public VenueTemplateViewsService(VenuesRepository venuesRepository, final EventsRepository eventsRepository,
                                     final EntitiesRepository entitiesRepository, final ValidationService validationService,
                                     @Value("${onebox.repository.S3SecureUrl}") String repositoryUrl,
                                     @Value("${onebox.repository.fileBasePath}") String repositoryBasePath, AccessControlSystemsRepository accessControlSystemsRepository, ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider) {
        this.venuesRepository = venuesRepository;
        this.eventsRepository = eventsRepository;
        this.entitiesRepository = entitiesRepository;
        this.validationService = validationService;
        this.repositoryUrl = repositoryUrl;
        this.repositoryBasePath = repositoryBasePath;
        this.accessControlSystemsRepository = accessControlSystemsRepository;
        this.externalAccessControlHandlerStrategyProvider = externalAccessControlHandlerStrategyProvider;
    }

    public IdDTO createVenueTemplateView(Long venueTemplateId, CreateVenueTemplateViewDTO body) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);

        IdDTO venueTemplateIdDTO = new IdDTO(venuesRepository.createVenueTemplateView(venueTemplateId, VenueTemplateViewConverter.toMs(body)));

        if (venueTemplate.getEventId() != null && venueTemplate.getEntityId() != null
                && venueTemplate.getVenue() != null || venueTemplate.getVenue().getId() != null) {
            checkAndProcessExternalVenueTemplates(venueTemplate.getEntityId(), venueTemplate.getVenue().getId(), venueTemplateId);
        }

        return venueTemplateIdDTO;
    }

    public VenueTemplateViewsDTO getVenueTemplateViews(Long venueTemplateId, VenueTemplateViewsFilterDTO filter) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        if (filter.getSessionId() != null) {
            if (VenueTemplateScope.EVENT.equals(venueTemplate.getScope())) {
                validationService.getAndCheckSession(venueTemplate.getEventId(), filter.getSessionId());
            } else {
                throw ExceptionBuilder.build(ApiMgmtVenueErrorCode.VENUE_TEMPLATE_SESSION_VIEW_UNSUPPORTED_OPERATION);
            }
        }
        VenueTemplateViews containers = venuesRepository.getVenueTemplateViews(venueTemplateId, VenueTemplateViewConverter.toMs(filter));
        return VenueTemplateViewConverter.fromMs(containers, getSVGBaseUrl(), PREFIX_SVG);
    }

    public VenueTemplateViewDTO getVenueTemplateRootView(Long venueTemplateId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        VenueTemplateView container = venuesRepository.getVenueTemplateView(venueTemplateId, "root");

        return VenueTemplateViewConverter.fromMs(container, getSVGBaseUrl(), PREFIX_SVG);
    }

    public VenueTemplateViewDTO getVenueTemplateView(Long venueTemplateId, Long containerId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        VenueTemplateView container = venuesRepository.getVenueTemplateView(venueTemplateId, containerId.toString());

        return VenueTemplateViewConverter.fromMs(container, getSVGBaseUrl(), PREFIX_SVG);
    }

    public void updateVenueTemplateViewsVip(Long venueTemplateId, Long sessionId, UpdateVenueTemplateVipViewsDTO body) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        if (sessionId != null) {
            validateVipViewsUpdate(venueTemplateId, venueTemplate, sessionId);
        }
        List<UpdateVenueTemplateVipView> out = VenueTemplateViewConverter.toMs(body, sessionId);
        venuesRepository.updateVenueTemplateVipViews(venueTemplateId, out);

        if (venueTemplate.getEventId() != null && venueTemplate.getEntityId() != null &&
                venueTemplate.getVenue() != null || venueTemplate.getVenue().getId() != null) {
            checkAndProcessExternalVenueTemplates(venueTemplate.getEntityId(), venueTemplate.getVenue().getId(), venueTemplateId);
        }

    }

    public void updateVenueTemplateViews(Long venueTemplateId, UpdateVenueTemplateViewsDTO body) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        venuesRepository.updateVenueTemplateViews(venueTemplateId, VenueTemplateViewConverter.toMs(body));

        if (venueTemplate.getEventId() != null && venueTemplate.getEntityId() != null
                && venueTemplate.getVenue() != null || venueTemplate.getVenue().getId() != null) {
            checkAndProcessExternalVenueTemplates(venueTemplate.getEntityId(), venueTemplate.getVenue().getId(), venueTemplateId);
        }
    }

    public void updateVenueTemplateView(Long venueTemplateId, Long viewId, UpdateVenueTemplateViewDTO body) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        venuesRepository.updateVenueTemplateView(venueTemplateId, viewId, VenueTemplateViewConverter.toMs(body));

        if (venueTemplate.getEventId() != null && venueTemplate.getEntityId() != null
                && venueTemplate.getVenue() != null || venueTemplate.getVenue().getId() != null) {
            checkAndProcessExternalVenueTemplates(venueTemplate.getEntityId(), venueTemplate.getVenue().getId(), venueTemplateId);
        }
    }

    public void deleteVenueTemplateView(Long venueTemplateId, Long viewId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        venuesRepository.deleteVenueTemplateView(venueTemplateId, viewId);
    }

    public void updateVenueTemplateViewTemplate(Long venueTemplateId, Long viewId, String template) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        venuesRepository.updateVenueTemplateViewTemplate(venueTemplateId, viewId, template);
    }

    private void validateVipViewsUpdate(Long venueTemplateId, VenueTemplate venueTemplate, Long sessionId) {
        if (VenueTemplateScope.EVENT.equals(venueTemplate.getScope())) {
            Entity entity = this.entitiesRepository.getEntity(venueTemplate.getEntityId());
            if (BooleanUtils.isFalse(entity.getAllowVipViews())) {
                throw ExceptionBuilder.build(ApiMgmtVenueErrorCode.VENUE_TEMPLATE_SESSION_VIEW_UNSUPPORTED_ENTITY_OPERATION);
            }
            SessionSearchFilter filter = toSessionFilter(sessionId, venueTemplateId);
            Sessions sessions = this.eventsRepository.getSessions(SecurityUtils.getUserOperatorId(), venueTemplate.getEventId(), filter);
            if (sessions.getMetadata().getTotal() == 0) {
                throw ExceptionBuilder.build(ApiMgmtVenueErrorCode.VENUE_TEMPLATE_SESSION_VIEW_INVALID_PARAM);
            }
        } else {
            throw ExceptionBuilder.build(ApiMgmtVenueErrorCode.VENUE_TEMPLATE_SESSION_VIEW_UNSUPPORTED_OPERATION);
        }
    }

    private String getSVGBaseUrl() {
        return repositoryUrl + repositoryBasePath;
    }

    private static SessionSearchFilter toSessionFilter(Long sessionId, Long venueTemplateId) {
        SessionSearchFilter filter = new SessionSearchFilter();
        filter.setLimit(0L);
        filter.setOffset(0L);
        filter.setVenueTemplateId(venueTemplateId);
        filter.setId(List.of(sessionId));
        filter.setFields(Collections.singletonList(SessionField.ID.name()));
        return filter;
    }


    private void checkAndProcessExternalVenueTemplates(Long entityId ,Long venueId, Long venueTemplateId) {
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
