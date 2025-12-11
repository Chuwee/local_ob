package es.onebox.mgmt.sessions.presales;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.collectives.CollectivesService;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.CustomPromotionalCodeValidation;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsFilter;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveDetailDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypes;
import es.onebox.mgmt.datasources.ms.entity.repository.CustomerTypesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.Provider;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.PreSaleConfigDTO;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.repository.EventPromotionsRepository;
import es.onebox.mgmt.entities.EntitiesService;
import es.onebox.mgmt.entities.dto.EntityDTO;
import es.onebox.mgmt.entities.factory.InventoryProviderService;
import es.onebox.mgmt.entities.factory.InventoryProviderServiceFactory;
import es.onebox.mgmt.exception.ApiMgmtSessionErrorCode;
import es.onebox.mgmt.sessions.SessionUtils;
import es.onebox.mgmt.sessions.converters.SessionPreSaleConverter;
import es.onebox.mgmt.sessions.converters.SessionSaleConstraintsConverter;
import es.onebox.mgmt.sessions.dto.CreateSessionPreSaleDTO;
import es.onebox.mgmt.sessions.dto.SessionPreSaleDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionPreSaleDTO;
import es.onebox.mgmt.sessions.enums.PresaleValidatorType;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SessionPresalesService {

    private static final String PRESALE_PROMOTIONAL_CODE_VALIDATION_SERVICE = "presalePromotionalCodeValidationService";

    private final EventsRepository eventsRepository;
    private final SaleRequestsRepository saleRequestsRepository;
    private final ChannelsRepository channelsRepository;
    private final EventPromotionsRepository eventPromotionsRepository;
    private final CollectivesService collectivesService;
    private final ValidationService validationService;
    private final CustomerTypesRepository customerTypesRepository;
    private final EntitiesService entitiesService;
    private final InventoryProviderServiceFactory inventoryProviderServiceFactory;

    protected SessionPresalesService(EventsRepository eventsRepository, SaleRequestsRepository saleRequestsRepository, ChannelsRepository channelsRepository,
                                     CollectivesService collectivesService, EventPromotionsRepository eventPromotionsRepository,
                                     ValidationService validationService, CustomerTypesRepository customerTypesRepository,
                                     EntitiesService entitiesService,
                                     InventoryProviderServiceFactory inventoryProviderServiceFactory) {
        this.eventsRepository = eventsRepository;
        this.saleRequestsRepository = saleRequestsRepository;
        this.channelsRepository = channelsRepository;
        this.eventPromotionsRepository = eventPromotionsRepository;
        this.collectivesService = collectivesService;
        this.validationService = validationService;
        this.customerTypesRepository = customerTypesRepository;
        this.entitiesService = entitiesService;
        this.inventoryProviderServiceFactory = inventoryProviderServiceFactory;
    }

    public List<SessionPreSaleDTO> getSessionPreSale(Long eventId, Long sessionId) {
        Session session = validationService.getAndCheckSession(eventId, sessionId);

        // TODO rellenar ActiveCustomTypes en ms-event
        List<PreSaleConfigDTO> preSaleConfigDTOS = eventsRepository.getSessionPreSale(eventId, sessionId);
        List<SessionPreSaleDTO> preSalesDTO = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(preSaleConfigDTOS)) {
            preSaleConfigDTOS.forEach(presale -> {
                Set<IdNameDTO> presaleChannels;
                if (presale.getPresalePromotionId() != null) {
                    EventPromotionChannels promoChannels = eventPromotionsRepository.getEventPromotionsChannels(eventId, presale.getPresalePromotionId().longValue());
                    presaleChannels = promoChannels.getChannels();
                } else {
                    MsSaleRequestsFilter filter = SessionSaleConstraintsConverter.toSalePresaleFilter(eventId);
                    MsSaleRequestsResponseDTO saleRequests = saleRequestsRepository.searchSaleRequests(filter);

                    presaleChannels = saleRequests.getData().stream()
                        .filter(d -> (ChannelSubtype.PORTAL_WEB.equals(d.getChannel().getSubtype()) || ChannelSubtype.BOX_OFFICE_WEB.equals(d.getChannel().getSubtype()))
                            && (!PresaleValidatorType.CUSTOMERS.equals(presale.getValidatorType()) || d.getChannel().getEntity().getId().equals(session.getEntityId())))
                        .map(sr -> new IdNameDTO(sr.getChannel().getId(), sr.getChannel().getName()))
                        .collect(Collectors.toSet());

                }

                MsCollectiveDetailDTO collective = null;
                if (PresaleValidatorType.COLLECTIVE.equals(presale.getValidatorType())) {
                    collective = getAndCheckCollective(presale.getValidatorId());
                }

                Set<IdNameDTO> customerTypes = customerTypesRepository.getCustomerTypes(session.getEntityId()).getData()
                        .stream()
                        .map(customerType -> new IdNameDTO(customerType.getId(), customerType.getName()))
                        .collect(Collectors.toSet());

                EntityDTO entity = entitiesService.getEntity(session.getEntityId());

                preSalesDTO.add(SessionPreSaleConverter.toDTO(presale, presaleChannels, collective, customerTypes, entity, session.getIsSmartBooking()));
            });
        }
        return preSalesDTO;
    }

    public SessionPreSaleDTO createSessionPreSale(Long eventId, Long sessionId, CreateSessionPreSaleDTO request) {
        validationService.getAndCheckSession(eventId, sessionId);
        MsCollectiveDetailDTO collective = null;
        if (PresaleValidatorType.COLLECTIVE.equals(request.getValidatorType())) {
            collective = getAndCheckCollective(request.getValidatorId());
        }


        String providerId = request.getAdditionalConfig() == null || request.getAdditionalConfig().getInventoryProvider() == null ?
                null : request.getAdditionalConfig().getInventoryProvider().getCode();

        if (request.getAdditionalConfig() != null && request.getAdditionalConfig().getEntityId() == null) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_PRESALE_ENTITY_ID_MANDATORY);
        }

        Long entityId = request.getAdditionalConfig() == null ? null : request.getAdditionalConfig().getEntityId();

        InventoryProviderService inventoryProviderService = inventoryProviderServiceFactory.getIntegrationService(entityId, providerId);
        PreSaleConfigDTO preSale = SessionPreSaleConverter.toMsEvent(request);

        PreSaleConfigDTO preSaleConfigDTO = inventoryProviderService.createSessionPresale(eventId, sessionId, preSale, false);

        Set<IdNameDTO> customerTypes = null;
        if (CollectionUtils.isNotEmpty(preSaleConfigDTO.getActiveCustomerTypes())) {
            customerTypes = customerTypesRepository.getCustomerTypes(request.getAdditionalConfig().getEntityId()).getData()
                    .stream()
                    .map(customerType -> new IdNameDTO(customerType.getId(), customerType.getName()))
                    .collect(Collectors.toSet());
        }

        return SessionPreSaleConverter.toDTO(preSaleConfigDTO, null, collective, customerTypes, null, null);
    }

    public void updateSessionPreSale(Long eventId, Long sessionId, Long presalesId, UpdateSessionPreSaleDTO request) {
        Session session = validationService.getAndCheckSession(eventId, sessionId);
        EntityDTO entity = entitiesService.getEntity(session.getEntityId());

        SessionUtils.validateSessionPreSaleConfig(session, entity, request);

        MsSaleRequestsResponseDTO saleRequests = null;
        if (CollectionUtils.isNotEmpty(request.getChannels())) {
            MsSaleRequestsFilter filter = SessionSaleConstraintsConverter.toSalePresaleFilter(eventId);
            saleRequests = saleRequestsRepository.searchSaleRequests(filter);
        }

        CustomerTypes customerTypes = null;
        if (CollectionUtils.isNotEmpty(request.getCustomerTypes())) {
            customerTypes = customerTypesRepository.getCustomerTypes(session.getEntityId());
        }

        PreSaleConfigDTO preSale = SessionPreSaleConverter.toMsEvent(request, saleRequests, customerTypes);

        if (CollectionUtils.isNotEmpty(preSale.getActiveChannels())) {
            for (Long channelId : preSale.getActiveChannels()) {
                ChannelConfig channelConfig = new ChannelConfig();
                CustomPromotionalCodeValidation customPromoValidation = new CustomPromotionalCodeValidation();
                customPromoValidation.setServiceImpl(PRESALE_PROMOTIONAL_CODE_VALIDATION_SERVICE);
                channelConfig.setCustomPromotionalCodeValidation(customPromoValidation);
                channelsRepository.updateChannelConfig(channelId, channelConfig);
            }
        }

        eventsRepository.updateSessionPreSale(eventId, sessionId, presalesId, preSale);
    }

    public void deleteSessionPreSale(Long eventId, Long sessionId, Long presalesId) {
        validationService.getAndCheckSession(eventId, sessionId);
        Event event = validationService.getAndCheckEvent(eventId);

        Provider provider = event.getInventoryProvider();
        String providerCode = provider == null ? null : provider.getCode();
        Long entityId = event.getEntityId();

        InventoryProviderService inventoryProviderService = inventoryProviderServiceFactory.getIntegrationService(entityId, providerCode);

        inventoryProviderService.deleteSessionPresale(entityId, eventId, sessionId, presalesId, false);
    }

    private MsCollectiveDetailDTO getAndCheckCollective(Long validatorId) {
        if (validatorId == null) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_PRESALE_COLLECTIVE_NOT_FOUND);
        }
        MsCollectiveDetailDTO collective = collectivesService.getAndCheckCollective(validatorId);
        if (collective == null) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_PRESALE_COLLECTIVE_NOT_FOUND);
        }
        return collective;
    }

}
