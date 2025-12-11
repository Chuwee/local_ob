package es.onebox.mgmt.salerequests.pricesimulation;

import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.pricesimulation.VenueConfigPricesSimulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PriceSimulationRepository {


    private final MsEventDatasource msEventDatasource;

    @Autowired
    public PriceSimulationRepository(MsEventDatasource msEventDatasource){
        this.msEventDatasource = msEventDatasource;
    }

    public List<VenueConfigPricesSimulation> getPriceSimulation(Long saleRequestId) {
        return msEventDatasource.getPriceSimulation(saleRequestId);
    }

    public List<VenueConfigPricesSimulation> getPriceSimulation(Long eventId, Long channelId) {
        return msEventDatasource.getPriceSimulation(eventId, channelId);
    }

}
