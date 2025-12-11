package es.onebox.event.seasontickets.amqp.session;

import es.onebox.event.common.amqp.streamprogress.ConsumerType;
import es.onebox.event.common.amqp.streamprogress.ProgressService;
import es.onebox.event.common.amqp.streamprogress.model.SeasonTicketMessage;
import es.onebox.event.common.amqp.streamprogress.model.SeasonTicketTypeMessage;
import es.onebox.event.common.amqp.streamprogress.model.StatusMessage;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.events.enums.VenueStatusDTO;
import es.onebox.event.seasontickets.dao.VenueConfigDao;
import es.onebox.event.seasontickets.service.SeasonTicketService;
import es.onebox.event.sessions.dto.CreateSessionDTO;
import es.onebox.event.sessions.dto.RateDTO;
import es.onebox.event.sessions.dto.SessionSalesType;
import es.onebox.event.sessions.service.SessionService;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CreateSeasonTicketSessionProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateSeasonTicketSessionProcessor.class);

    @Autowired
    private SeasonTicketService seasonTicketService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private VenueConfigDao venueConfigDao;
    @Autowired
    private RateDao rateDao;
    @Autowired
    private ProgressService progressService;

    @Override
    public void execute(Exchange exchange) {
        Long attempts = exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER, Long.class);

        CreateSeasonTicketSessionMessage message = exchange.getIn().getBody(CreateSeasonTicketSessionMessage.class);
        LOGGER.info("[CREATE SEASON TICKET SESSION] Started processing a message. Retry: {} seasonTicketId: {}",
                attempts, message.getSeasonTicketId());

        VenueStatusDTO venueConfigStatus = VenueStatusDTO.byId(venueConfigDao
                .getVenueConfigStatus(message.getVenueConfigId().intValue()).getEstado());

        LOGGER.info("[CREATE SEASON TICKET SESSION] VenueStatus is: {} seasonTicketId: {}", venueConfigStatus.name(),
                message.getSeasonTicketId());
        switch (venueConfigStatus) {
            case ACTIVE:
                try {
                    boolean seasonTicketSessionCreated = seasonTicketService
                            .isSeasonTicketSessionCreated(message.getSeasonTicketId());
                    if (!seasonTicketSessionCreated) {
                        createSession(message);
                    } else {
                        LOGGER.info("[CREATE SEASON TICKET SESSION] Session already created. seasonTicketId: {}",
                                message.getSeasonTicketId());
                    }
                } catch (Exception e) {
                    sendErrorMessage(message.getSeasonTicketId());
                    LOGGER.error("[CREATE SEASON TICKET SESSION] ERROR Creating Session. seasonTicketId: {}",
                            message.getSeasonTicketId(), e);
                }
                break;
            case PROCESSING:
                throw new UnsupportedOperationException();
            case ERROR:
                LOGGER.error("[CREATE SEASON TICKET SESSION] Venue config on error. seasonTicketId: {}",
                        message.getSeasonTicketId());
                sendErrorMessage(message.getSeasonTicketId());
                break;
            default:
        }
    }

    private void createSession(CreateSeasonTicketSessionMessage message) {
        CreateSessionDTO creationData = new CreateSessionDTO();
        creationData.setEventId(message.getSeasonTicketId());
        creationData.setName(message.getName());
        creationData.setTaxId(message.getTaxId());
        creationData.setChargeTaxId(message.getChargeTaxId());
        creationData.setTicketTaxIds(message.getTicketTaxIds());
        creationData.setChargeTaxIds(message.getChargeTaxIds());
        creationData.setVenueConfigId(message.getVenueConfigId());
        ZonedDateTime now = ZonedDateTime.now();
        creationData.setSessionStartDate(now);
        creationData.setSalesEndDate(now.plusYears(1));
        creationData.setSalesStartDate(now);
        creationData.setPublishDate(now);
        creationData.setRates(buildRates(message.getSeasonTicketId()));
        creationData.setSeasonPass(Boolean.TRUE);
        creationData.setSeasonTicket(Boolean.TRUE);
        creationData.setSaleType(SessionSalesType.INDIVIDUAL.getType());

        Long sessionId = sessionService.createSession(message.getSeasonTicketId(), creationData);

        sessionService.postCreateSession(message.getSeasonTicketId(), sessionId, creationData);
        LOGGER.info("[CREATE SEASON TICKET SESSION] Season ticket session created successfully. seasonTicketId: {}",
                message.getSeasonTicketId());
    }

    private List<RateDTO> buildRates(Long seasonTicketId) {
        List<RateRecord> rates = rateDao.getRatesByEventId(seasonTicketId.intValue());
        if (rates == null || rates.isEmpty()) {
            // This should never happen as a default rate is created in api-mgmt
            CpanelTarifaRecord record = new CpanelTarifaRecord();
            record.setNombre("Rate");
            record.setDefecto((byte) 1);
            record.setIdevento(seasonTicketId.intValue());
            rateDao.insert(record);
            rates = rateDao.getRatesByEventId(seasonTicketId.intValue());
        }
        return rates.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private RateDTO toDTO(RateRecord r) {
        RateDTO rate = new RateDTO();
        rate.setId(r.getIdTarifa().longValue());
        rate.setDefault(r.getDefecto() == 1);
        return rate;
    }

    private void sendErrorMessage(Long seasonTicketId) {
        SeasonTicketMessage seasonTicketMessage = new SeasonTicketMessage();
        seasonTicketMessage.setType(SeasonTicketTypeMessage.CREATION);
        seasonTicketMessage.setSeasonTicketId(seasonTicketId);
        progressService.sendNotificationProgress(seasonTicketMessage, 100, StatusMessage.ERROR, ConsumerType.SEASON_TICKET);
    }
}
