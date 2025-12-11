package es.onebox.event.pricesengine;

import es.onebox.event.events.dao.EventDao;
import es.onebox.event.priceengine.simulation.domain.VenueConfigPricesSimulation;
import es.onebox.event.priceengine.simulation.service.PriceEngineSimulationService;
import es.onebox.event.pricesengine.converter.PriceSimulationConverter;
import es.onebox.event.pricesengine.dto.VenueConfigPricesSimulationDTO;
import es.onebox.event.seasontickets.dto.pricesimulation.PriceSimulationResponse;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceSimulationService {

    private final PriceEngineSimulationService priceEngineSimulationService;
    private final EventDao eventDao;

    @Autowired
    public PriceSimulationService(PriceEngineSimulationService priceEngineSimulationService, EventDao eventDao) {
        this.priceEngineSimulationService = priceEngineSimulationService;
        this.eventDao = eventDao;
    }

    public List<VenueConfigPricesSimulationDTO> getPriceSimulationBySaleRequestId(Long saleRequestId) {
        List<VenueConfigPricesSimulation> result = priceEngineSimulationService.getPricesSimulationBySaleRequestId(saleRequestId);
        return PriceSimulationConverter.convertToDto(result, eventDao.getCurrencyIdByChannel(saleRequestId));
    }

    public List<VenueConfigPricesSimulationDTO> getPriceSimulationIdEventAndChannelId(Long eventId, Long channelId) {
        CpanelEventoRecord event = eventDao.getById(eventId.intValue());
        List<VenueConfigPricesSimulation> result =
                priceEngineSimulationService.getPricesSimulationByEventIdAndChannelId(event, channelId);
        return PriceSimulationConverter.convertToDto(result, event.getIdcurrency().longValue());
    }

    public PriceSimulationResponse getPriceSimulation(Long saleRequestId) {
        List<VenueConfigPricesSimulation> venueConfigPricesSimulations = priceEngineSimulationService.getPriceSimulationSaleRequestId(
            saleRequestId);
        if (venueConfigPricesSimulations.isEmpty()) {
            return createEmptyResponse();
        }
        PriceSimulationResponse response = new PriceSimulationResponse();
        response.setData(venueConfigPricesSimulations);
        return response;
    }

    private PriceSimulationResponse createEmptyResponse() {
        PriceSimulationResponse response = new PriceSimulationResponse();
        response.setData(List.of());
        return response;
    }
}
