package es.onebox.event.priceengine.simulation.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.priceengine.exception.PriceEngineErrorCode;
import es.onebox.event.priceengine.packs.PackTaxes;
import es.onebox.event.priceengine.packs.PackVenueConfigPricesBase;
import es.onebox.event.priceengine.packs.PackVenueConfigPricesSimulation;
import es.onebox.event.priceengine.simulation.dao.AssignmentPriceZoneDao;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.priceengine.simulation.dao.EventChannelDao;
import es.onebox.event.priceengine.simulation.dao.EventPromotionTemplateDao;
import es.onebox.event.priceengine.simulation.domain.VenueConfigMap;
import es.onebox.event.priceengine.simulation.domain.VenueConfigPricesSimulation;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import es.onebox.event.priceengine.simulation.record.PriceZoneRateVenueConfigCustomRecord;
import es.onebox.event.priceengine.simulation.util.PackSimulationUtils;
import es.onebox.event.priceengine.simulation.util.SimulationUtils;
import es.onebox.event.priceengine.surcharges.CatalogSurchargeService;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.priceengine.taxes.domain.SessionTaxes;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class PriceEngineSimulationService {

    private final EventChannelDao eventChannelDao;
    private final ChannelEventDao channelEventDao;
    private final AssignmentPriceZoneDao assignmentPriceZoneDao;
    private final EventPromotionTemplateDao eventPromotionTemplateDao;
    private final CatalogSurchargeService surchargeService;

    @Autowired
    public PriceEngineSimulationService(EventChannelDao eventChannelDao,
                                        ChannelEventDao channelEventDao,
                                        AssignmentPriceZoneDao assignmentPriceZoneDao,
                                        EventPromotionTemplateDao eventPromotionTemplateDao,
                                        CatalogSurchargeService surchargeService) {
        this.eventChannelDao = eventChannelDao;
        this.channelEventDao = channelEventDao;
        this.assignmentPriceZoneDao = assignmentPriceZoneDao;
        this.eventPromotionTemplateDao = eventPromotionTemplateDao;
        this.surchargeService = surchargeService;
    }

    public List<VenueConfigPricesSimulation> getPricesSimulationBySaleRequestId(Long saleRequestId) {
        CpanelEventoCanalRecord eventChannel = eventChannelDao.findById(saleRequestId.intValue());
        if (Objects.nonNull(eventChannel)) {
            Optional<CpanelCanalEventoRecord> channelEvent
                    = channelEventDao.getChannelEvent(eventChannel.getIdcanal(), eventChannel.getIdevento());
            if (channelEvent.isPresent()) {
                return getPricesSimulationBySaleRequestId(eventChannel, channelEvent.get());
            }
            throw new OneboxRestException(PriceEngineErrorCode.CHANNEL_EVENT_NOT_FOUND);
        }
        throw new OneboxRestException(PriceEngineErrorCode.EVENT_CHANNEL_NOT_FOUND);
    }

    public List<VenueConfigPricesSimulation> getPriceSimulationSaleRequestId(Long saleRequestId) {
        CpanelEventoCanalRecord eventChannel = eventChannelDao.findById(saleRequestId.intValue());
        if (eventChannel != null) {
            Optional<CpanelCanalEventoRecord> channelEvent
                = channelEventDao.getChannelEvent(eventChannel.getIdcanal(), eventChannel.getIdevento());
            if (channelEvent.isPresent()) {
                return getPricesSimulationBySaleRequestId(eventChannel, channelEvent.get());
            }
        }
        return List.of();
    }

    public List<VenueConfigPricesSimulation> getPricesSimulationByEventIdAndChannelId(CpanelEventoRecord event, Long channelId) {
        Optional<CpanelCanalEventoRecord> channelEvent = channelEventDao.getChannelEvent(channelId.intValue(), event.getIdevento());
        if (channelEvent.isEmpty()) {
            throw new OneboxRestException(PriceEngineErrorCode.CHANNEL_EVENT_NOT_FOUND);
        }
        Optional<CpanelEventoCanalRecord> eventChannel = eventChannelDao.getEventChannel(event.getIdevento(), channelId.intValue());
        if (eventChannel.isEmpty()) {
            throw new OneboxRestException(PriceEngineErrorCode.EVENT_CHANNEL_NOT_FOUND);
        }
        return getPricesSimulationBySaleRequestId(eventChannel.get(), channelEvent.get());
    }

    public List<VenueConfigPricesSimulation> getPricesSimulationBySaleRequestId(CpanelEventoCanalRecord eventChannel,
                                                                                CpanelCanalEventoRecord channelEvent) {
        Integer eventId = eventChannel.getIdevento();
        Integer channelId = eventChannel.getIdcanal();
        List<PriceZoneRateVenueConfigCustomRecord> result;
        if (Objects.nonNull(channelEvent.getTodosgruposventa())
                && BooleanUtils.isFalse(BooleanUtils.toBoolean(channelEvent.getTodosgruposventa()))) {
            result = assignmentPriceZoneDao.getPriceZonesRatesGroupSalesVenueConfigByEventId(channelEvent.getIdcanaleevento());
        } else {
            result = assignmentPriceZoneDao.getPriceZonesRatesVenueConfigByEventId(eventId);
        }
        ChannelEventSurcharges surcharges = surchargeService.getSurchargeRangesByChannelEventRelationShips(channelEvent, eventChannel);
        List<EventPromotionRecord> promotionsRecord = eventPromotionTemplateDao.getPromotionsByEventId(eventId);
        boolean eventChannelSpecificChannelSurcharges = false;
        if (Objects.nonNull(eventChannel.getAplicarrecargoscanalespecificos())) {
            eventChannelSpecificChannelSurcharges = eventChannel.getAplicarrecargoscanalespecificos().equals((byte) 1);
        }
        return SimulationUtils.simulatePrices(result, surcharges, promotionsRecord, channelId, eventChannelSpecificChannelSurcharges);
    }

    public VenueConfigPricesSimulation getPriceSimulationForCatalog(Long channelId,
                                                                    Map<Integer, VenueConfigMap> venueConfigMap,
                                                                    ChannelEventSurcharges surcharges,
                                                                    Boolean eventChannelSpecificChannelSurcharges,
                                                                    List<EventPromotionRecord> promotionsRecord,
                                                                    SessionTaxes taxes) {
        return SimulationUtils.simulatePrices(venueConfigMap, surcharges, promotionsRecord, channelId.intValue(), eventChannelSpecificChannelSurcharges, taxes).get(0);
    }

    public PackVenueConfigPricesSimulation getPackPricesSimulationForCatalog(Long channelId,
                                                                             PackVenueConfigPricesBase venueConfigMap,
                                                                             ChannelEventSurcharges surcharges,
                                                                             List<EventPromotionRecord> promotionsRecord,
                                                                             PackTaxes taxes) {
        return PackSimulationUtils.simulatePackPrice(channelId, venueConfigMap, surcharges, promotionsRecord, taxes);

    }

}
