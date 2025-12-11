package es.onebox.event.events.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.onebox.event.catalog.dao.ChannelSessionPriceCouchDao;
import es.onebox.event.catalog.dao.couch.ChannelSessionPricesDocument;
import es.onebox.event.datasources.ms.venue.dto.SectorDTO;
import es.onebox.event.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.event.events.converter.EventRateConverter;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.ExternalSessionRateCouchDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.RateExternalTypeDao;
import es.onebox.event.events.domain.ExternalRateType;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.priceengine.simulation.dao.EventChannelDao;
import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelExternalRateTypeRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;

@Service
public class EventRateHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventRateHelper.class);

	private final EventDao eventDao;
	private final SessionDao sessionDao;
	private final RateDao rateDao;
	private final EventConfigCouchDao eventConfigCouchDao;
	private final RateExternalTypeDao rateExternalTypeDao;
	private final ExternalSessionRateCouchDao externalSessionRateCouchDao;
	private final ChannelSessionPriceCouchDao channelSessionPriceCouchDao;
	private final EventChannelDao eventChannelDao;
	private final VenuesRepository venuesRepository;

	public EventRateHelper(EventDao eventDao, SessionDao sessionDao, RateDao rateDao,
			EventConfigCouchDao eventConfigCouchDao, RateExternalTypeDao rateExternalTypeDao,
			ExternalSessionRateCouchDao externalSessionRateCouchDao,
			ChannelSessionPriceCouchDao channelSessionPriceCouchDao, EventChannelDao eventChannelDao,
			VenuesRepository venuesRepository) {
		this.eventDao = eventDao;
		this.sessionDao = sessionDao;
		this.rateDao = rateDao;
		this.eventConfigCouchDao = eventConfigCouchDao;
		this.rateExternalTypeDao = rateExternalTypeDao;
		this.externalSessionRateCouchDao = externalSessionRateCouchDao;
		this.channelSessionPriceCouchDao = channelSessionPriceCouchDao;
		this.eventChannelDao = eventChannelDao;
		this.venuesRepository = venuesRepository;
	}


    @MySQLWrite
	public void refreshExternalRates(Integer eventId, Integer sessionId, Integer priceZoneId) {
        LOGGER.info("[EXTERNAL RATES] Refreshing external rates for event: {}", eventId);
		CpanelEventoRecord eventRecord = eventDao.getById(eventId);
		if (!EventStatus.IN_PROGRAMMING.getId().equals(eventRecord.getEstado())
				&& !EventStatus.IN_PROGRESS.getId().equals(eventRecord.getEstado())
				&& !EventStatus.PLANNED.getId().equals(eventRecord.getEstado())
				&& !EventStatus.READY.getId().equals(eventRecord.getEstado())) {
			return;
		}

		List<SessionRecord> activeSessions = sessionDao.findActiveSessionsWithTemplateByEventId(eventId);
		if (activeSessions == null || activeSessions.isEmpty()) {
			return;
		}

		// Channel promotions are blocked for ITH
		List<EventChannelForCatalogRecord> eventChannels = eventChannelDao.getEventChannels(eventId.longValue());
		if (eventChannels == null || eventChannels.isEmpty()) {
			return;
		}

		EventConfig eventConfig = eventConfigCouchDao.get(eventId.toString());
		List<CpanelExternalRateTypeRecord> externalRateTypeRecords = rateExternalTypeDao
				.getEventRateExternalTypes(eventConfig.getInventoryProvider().name());

		Long channelId = eventChannels.get(0).getIdcanal().longValue();
		Set<ExternalRateType> externalRateTypes = new HashSet<>();

		if (sessionId != null) {
			Optional<SessionRecord> optSession = activeSessions.stream()
					.filter(se -> se.getSessionId().equals(sessionId)).findFirst();
			if (optSession.isEmpty()) {
				return;
			}

			ChannelSessionPricesDocument doc = channelSessionPriceCouchDao.get(channelId, sessionId.longValue());
			List<CpanelTarifaRecord> rates = rateDao.getRatesBySession(sessionId);
			List<SectorDTO> sectors = venuesRepository.getSectorsByTemplateId(optSession.get().getVenueTemplateId());

			ExternalRateType externalRateType = EventRateConverter.fromChannelSessionPrices(
					doc, rates, externalRateTypeRecords, sectors);
			externalRateTypes.add(externalRateType);
			externalSessionRateCouchDao.remove(sessionId.toString());

		} else {
			List<Integer> sessionIds = activeSessions.stream()
					.map(SessionRecord::getIdsesion)
					.toList();

			Map<Long, ChannelSessionPricesDocument> priceDocs = channelSessionPriceCouchDao.getBySessionIds(channelId,
					sessionIds);
			Map<Integer, List<CpanelTarifaRecord>> ratesBySession = rateDao.getRatesBySessionIds(sessionIds);
			List<Integer> venueTemplateIds = activeSessions.stream().map(SessionRecord::getVenueTemplateId).distinct()
					.toList();

			Map<Integer, List<SectorDTO>> sectorsByVenueTemplate = new HashMap<>();

			for (Integer venueTemplateId : venueTemplateIds) {
				List<SectorDTO> sectorsDTOs = venuesRepository.getSectorsByTemplateId(venueTemplateId);
				sectorsByVenueTemplate.put(venueTemplateId, sectorsDTOs);
			}

			for (SessionRecord session : activeSessions) {
				Integer sid = session.getIdsesion();

				ChannelSessionPricesDocument doc = priceDocs.get(sid.longValue());
				List<CpanelTarifaRecord> rates = ratesBySession.getOrDefault(sid, List.of());
				List<SectorDTO> sectors = sectorsByVenueTemplate.getOrDefault(session.getVenueTemplateId(), List.of());

				ExternalRateType externalRateType = EventRateConverter.fromChannelSessionPrices(
						doc, rates, externalRateTypeRecords, sectors);
				externalRateTypes.add(externalRateType);
				externalSessionRateCouchDao.remove(sid.toString());
			}
		}

		externalSessionRateCouchDao.bulkInsert(externalRateTypes.stream().toList());
	}

}
