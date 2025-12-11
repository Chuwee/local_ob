package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPrice;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceStatusRequest;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceZone;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicRatesPrice;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DynamicPriceRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public DynamicPriceRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public DynamicPriceConfig getDynamicPriceConfig(Long eventId, Long sessionId, Boolean initialize) {
        return msEventDatasource.getSessionDynamicPriceConfig(eventId, sessionId, initialize);
    }

    public DynamicPriceZone getDynamicPriceZone(Long eventId, Long sessionId, Long idPriceZone) {
        return msEventDatasource.getDynamicPriceZone(eventId, sessionId, idPriceZone);
    }


    public List<DynamicRatesPrice> getDynamicRatePrice(Long eventId, Long sessionId, Long idPriceZone) {
        return msEventDatasource.getDynamicRatePrice(eventId, sessionId, idPriceZone);
    }

    public void createOrUpdateDynamicPrice(Long eventId, Long sessionId, Long idPriceZone, List<DynamicPrice> requests) {
        msEventDatasource.createOrUpdateDynamicPrice(eventId, sessionId, idPriceZone, requests);
    }

    public void deleteDynamicPriceConfig(Long eventId, Long sessionId, Long idPriceZone, Integer orderId) {
        msEventDatasource.deleteSessionDynamicPriceConfig(eventId, sessionId, idPriceZone, orderId);
    }

    public void activateDynamicPriceConfig(Long eventId, Long sessionId, DynamicPriceStatusRequest request) {
        msEventDatasource.activateDynamicPriceConfig(eventId, sessionId, request);
    }
}
