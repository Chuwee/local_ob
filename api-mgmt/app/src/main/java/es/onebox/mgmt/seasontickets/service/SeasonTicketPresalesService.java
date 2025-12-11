package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.CustomPromotionalCodeValidation;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsFilter;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypes;
import es.onebox.mgmt.datasources.ms.entity.repository.CustomerTypesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Provider;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.PreSaleConfigDTO;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.repository.EventPromotionsRepository;
import es.onebox.mgmt.entities.EntitiesService;
import es.onebox.mgmt.entities.dto.EntityDTO;
import es.onebox.mgmt.entities.factory.InventoryProviderService;
import es.onebox.mgmt.entities.factory.InventoryProviderServiceFactory;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtSessionErrorCode;
import es.onebox.mgmt.seasontickets.SeasonTicketUtils;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketPresalesConverter;
import es.onebox.mgmt.seasontickets.dto.CreateSeasonTicketPresaleDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketPresaleDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketPresaleDTO;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.converters.SessionSaleConstraintsConverter;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class SeasonTicketPresalesService {

    private static final String PRESALE_PROMOTIONAL_CODE_VALIDATION_SERVICE = "presalePromotionalCodeValidationService";

    private final EventsRepository eventsRepository;
    private final EventPromotionsRepository eventPromotionsRepository;
    private final SaleRequestsRepository saleRequestsRepository;
    private final CustomerTypesRepository customerTypesRepository;
    private final ChannelsRepository channelsRepository;
    private final EntitiesService entitiesService;
    private final ValidationService validationService;
    private final InventoryProviderServiceFactory inventoryProviderServiceFactory;

    protected SeasonTicketPresalesService(EventsRepository eventsRepository, EventPromotionsRepository eventPromotionsRepository,
                                          SaleRequestsRepository saleRequestsRepository, CustomerTypesRepository customerTypesRepository,
                                          ChannelsRepository channelsRepository, EntitiesService entitiesService,
                                          ValidationService validationService,
                                          InventoryProviderServiceFactory inventoryProviderServiceFactory) {
        this.inventoryProviderServiceFactory = inventoryProviderServiceFactory;
        this.eventsRepository = eventsRepository;
        this.eventPromotionsRepository = eventPromotionsRepository;
        this.saleRequestsRepository = saleRequestsRepository;
        this.customerTypesRepository = customerTypesRepository;
        this.channelsRepository = channelsRepository;
        this.entitiesService = entitiesService;
        this.validationService = validationService;
    }

    public List<SeasonTicketPresaleDTO> getSeasonTicketPresale(Long seasonTicketId) {
        Long sessionId = checkSeasonTicketAndGetSession(seasonTicketId);
        Session session = validationService.getAndCheckOnlySession(seasonTicketId, sessionId);

        List<PreSaleConfigDTO> preSaleConfigDTOS = eventsRepository.getSessionPreSale(seasonTicketId, sessionId);

        List<SeasonTicketPresaleDTO> preSalesDTO = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(preSaleConfigDTOS)) {
            preSaleConfigDTOS.forEach(presale -> {
                Set<IdNameDTO> presaleChannels;
                if (presale.getPresalePromotionId() != null) {
                    EventPromotionChannels promoChannels = eventPromotionsRepository.getEventPromotionsChannels(seasonTicketId, presale.getPresalePromotionId().longValue());
                    presaleChannels = promoChannels.getChannels();
                } else {
                    MsSaleRequestsFilter filter = SessionSaleConstraintsConverter.toSalePresaleFilter(seasonTicketId);
                    MsSaleRequestsResponseDTO saleRequests = saleRequestsRepository.searchSaleRequests(filter);

                    presaleChannels = saleRequests.getData().stream()
                            .filter(d -> (ChannelSubtype.PORTAL_WEB.equals(d.getChannel().getSubtype())
                                    || ChannelSubtype.BOX_OFFICE_WEB.equals(d.getChannel().getSubtype()))
                                    && d.getChannel().getEntity().getId().equals(session.getEntityId()))
                            .map(sr -> new IdNameDTO(sr.getChannel().getId(), sr.getChannel().getName()))
                            .collect(Collectors.toSet());

                }

                Set<IdNameDTO> customerTypes = customerTypesRepository.getCustomerTypes(session.getEntityId()).getData()
                        .stream()
                        .map(customerType -> new IdNameDTO(customerType.getId(), customerType.getName()))
                        .collect(Collectors.toSet());

                EntityDTO entity = entitiesService.getEntity(session.getEntityId());

                preSalesDTO.add(SeasonTicketPresalesConverter.toDTO(presale, presaleChannels, customerTypes, entity));
            });
        }
        return preSalesDTO;
    }

    public SeasonTicketPresaleDTO createSeasonTicketPresale(Long seasonTicketId, CreateSeasonTicketPresaleDTO request) {
        Long sessionId = checkSeasonTicketAndGetSession(seasonTicketId);
        validationService.getAndCheckOnlySession(seasonTicketId, sessionId);

        String providerId = request.getAdditionalConfig() == null || request.getAdditionalConfig().getInventoryProvider() == null ?
                null : request.getAdditionalConfig().getInventoryProvider().getCode();

        if (request.getAdditionalConfig() != null && request.getAdditionalConfig().getEntityId() == null) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_PRESALE_ENTITY_ID_MANDATORY);
        }

        Long entityId = request.getAdditionalConfig() == null ? null : request.getAdditionalConfig().getEntityId();
        InventoryProviderService inventoryProviderService = inventoryProviderServiceFactory.getIntegrationService(entityId, providerId);

        PreSaleConfigDTO preSale = SeasonTicketPresalesConverter.toMsEvent(request);
        PreSaleConfigDTO preSaleConfigDTO = inventoryProviderService.createSessionPresale(seasonTicketId, sessionId, preSale, true);

        Set<IdNameDTO> customerTypes = null;
        if (CollectionUtils.isNotEmpty(preSaleConfigDTO.getActiveCustomerTypes())) {
            customerTypes = customerTypesRepository.getCustomerTypes(request.getAdditionalConfig().getEntityId()).getData()
                    .stream()
                    .map(customerType -> new IdNameDTO(customerType.getId(), customerType.getName()))
                    .collect(Collectors.toSet());
        }

        return SeasonTicketPresalesConverter.toDTO(preSaleConfigDTO, null, customerTypes, null);
    }

    public void updateSeasonTicketPresale(Long seasonTicketId, Long presalesId, UpdateSeasonTicketPresaleDTO request) {
        Long sessionId = checkSeasonTicketAndGetSession(seasonTicketId);

        Session session = validationService.getAndCheckOnlySession(seasonTicketId, sessionId);
        EntityDTO entity = entitiesService.getEntity(session.getEntityId());

        SeasonTicketUtils.validateSeasonTicketPresaleConfig(session, entity, request);

        MsSaleRequestsResponseDTO saleRequests = null;
        if (CollectionUtils.isNotEmpty(request.getChannels())) {
            MsSaleRequestsFilter filter = SessionSaleConstraintsConverter.toSalePresaleFilter(seasonTicketId);
            saleRequests = saleRequestsRepository.searchSaleRequests(filter);
        }

        CustomerTypes customerTypes = null;
        if (CollectionUtils.isNotEmpty(request.getCustomerTypes())) {
            customerTypes = customerTypesRepository.getCustomerTypes(session.getEntityId());
        }

        PreSaleConfigDTO preSale = SeasonTicketPresalesConverter.toMsEvent(request, saleRequests, customerTypes);

        if (CollectionUtils.isNotEmpty(preSale.getActiveChannels())) {
            for (Long channelId : preSale.getActiveChannels()) {
                ChannelConfig channelConfig = new ChannelConfig();
                CustomPromotionalCodeValidation customPromoValidation = new CustomPromotionalCodeValidation();
                customPromoValidation.setServiceImpl(PRESALE_PROMOTIONAL_CODE_VALIDATION_SERVICE);
                channelConfig.setCustomPromotionalCodeValidation(customPromoValidation);
                channelsRepository.updateChannelConfig(channelId, channelConfig);
            }
        }

        eventsRepository.updateSessionPreSale(seasonTicketId, sessionId, presalesId, preSale);
    }

    public void deleteSeasonTicketPresale(Long seasonTicketId, Long presalesId) {
        Long sessionId = checkSeasonTicketAndGetSession(seasonTicketId);
        SeasonTicket seasonTicket = validationService.getAndCheckSeasonTicket(seasonTicketId);

        Provider provider = seasonTicket.getInventoryProvider();
        String providerCode = provider == null ? null : provider.getCode();
        Long entityId = seasonTicket.getEntityId();

        InventoryProviderService inventoryProviderService = inventoryProviderServiceFactory.getIntegrationService(entityId, providerCode);

        inventoryProviderService.deleteSessionPresale(entityId, seasonTicketId, sessionId, presalesId, true);
    }

    private Long checkSeasonTicketAndGetSession(Long seasonTicketId) {
        validationService.getAndCheckSeasonTicket(seasonTicketId);
        SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();

        return eventsRepository.getSessions(SecurityUtils.getUserOperatorId(), seasonTicketId, sessionSearchFilter).getData()
                .stream().findFirst().orElseThrow( () -> new OneboxRestException(ApiMgmtErrorCode.SESSION_NOT_FOUND)).getId();
    }
}
