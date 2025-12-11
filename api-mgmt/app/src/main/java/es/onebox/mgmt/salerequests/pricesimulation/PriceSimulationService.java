package es.onebox.mgmt.salerequests.pricesimulation;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.pricesimulation.dto.VenueConfigPricesSimulationDTO;
import es.onebox.mgmt.salerequests.validation.SaleRequestsValidations;
import es.onebox.mgmt.seasontickets.service.SeasonTicketValidationService;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PriceSimulationService {

    private final SecurityManager securityManager;
    private final EventsRepository eventsRepository;
    private final SaleRequestsRepository saleRequestsRepository;
    private final EventChannelsRepository eventChannelsRepository;
    private final PriceSimulationRepository priceSimulationRepository;
    private final SeasonTicketValidationService stValidationService;
    private final MasterdataService masterdataService;

    @Autowired
    public PriceSimulationService(PriceSimulationRepository priceSimulationRepository,
                                  SaleRequestsRepository saleRequestsRepository,
                                  SecurityManager securityManager,
                                  EventsRepository eventsRepository,
                                  EventChannelsRepository eventChannelsRepository,
                                  SeasonTicketValidationService stValidationService, MasterdataService masterdataService) {
        this.priceSimulationRepository = priceSimulationRepository;
        this.saleRequestsRepository = saleRequestsRepository;
        this.securityManager = securityManager;
        this.eventsRepository = eventsRepository;
        this.eventChannelsRepository = eventChannelsRepository;
        this.stValidationService = stValidationService;
        this.masterdataService = masterdataService;
    }

    public List<VenueConfigPricesSimulationDTO> getPriceSimulation(Long saleRequestId) {
        SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                                                                    saleRequestsRepository::getSaleRequestDetail,
                                                                    securityManager::checkEntityAccessible);
        return PriceSimulationConverter.convertToListDto(priceSimulationRepository.getPriceSimulation(saleRequestId), masterdataService.getCurrencies());
    }

    public List<VenueConfigPricesSimulationDTO> getPriceSimulation(Long eventId, Long channelId) {
        Event event = eventsRepository.getEvent(eventId);
        if (Objects.isNull(event)) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(event.getEntityId());
        try {
            eventChannelsRepository.getEventChannel(eventId, channelId);
        } catch (OneboxRestException e) {
            if (e.getErrorCode().equals(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND.getErrorCode())) {
                throw new OneboxRestException(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND);
            }
            throw e;
        }
        return PriceSimulationConverter.convertToListDto(priceSimulationRepository.getPriceSimulation(eventId, channelId), masterdataService.getCurrencies());
    }

    public List<VenueConfigPricesSimulationDTO> getSeasonTicketPriceSimulation(Long seasonTicketId, Long channelId) {
        stValidationService.getAndCheckSeasonTicketChannel(seasonTicketId, channelId);
        return PriceSimulationConverter.convertToListDto(priceSimulationRepository.getPriceSimulation(seasonTicketId, channelId), masterdataService.getCurrencies());
    }
}
