package es.onebox.event.surcharges;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.services.CommonSurchargesService;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.surcharges.dao.RangeSurchargeEventChangeSeatDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventInvitationDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventPromotionDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventSecondaryMarketDao;
import es.onebox.event.surcharges.dto.SurchargeListDTO;
import es.onebox.event.surcharges.dto.SurchargeTypeDTO;
import es.onebox.event.surcharges.dto.SurchargesDTO;
import es.onebox.event.surcharges.manager.SurchargeManager;
import es.onebox.event.surcharges.manager.SurchargeManagerFactory;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SurchargesService {

    private final SurchargeManagerFactory surchargeManagerFactory;
    private final CommonSurchargesService commonSurchargesService;

    private final RangeSurchargeEventInvitationDao rangeSurchargeEventInvitationDao;
    private final RangeSurchargeEventPromotionDao rangeSurchargeEventPromotionDao;
    private final RangeSurchargeEventChangeSeatDao rangeSurchargeEventChangeSeatDao;
    private final RangeSurchargeEventSecondaryMarketDao rangeSurchargeEventSecMktDao;
    private final RangeSurchargeEventDao rangeSurchargeEventDao;
    private final EventDao eventDao;
    private final EventConfigCouchDao eventConfigCouchDao;

    @Autowired
    public SurchargesService(SurchargeManagerFactory surchargeManagerFactory,
                             CommonSurchargesService commonSurchargesService,
                             RangeSurchargeEventInvitationDao rangeSurchargeEventInvitationDao,
                             RangeSurchargeEventPromotionDao rangeSurchargeEventPromotionDao,
                             RangeSurchargeEventChangeSeatDao rangeSurchargeEventChangeSeatDao,
                             RangeSurchargeEventSecondaryMarketDao rangeSurchargeEventSecMktDao,
                             RangeSurchargeEventDao rangeSurchargeEventDao,
                             EventDao eventDao,
                             EventConfigCouchDao eventConfigCouchDao) {
        this.surchargeManagerFactory = surchargeManagerFactory;
        this.commonSurchargesService = commonSurchargesService;
        this.rangeSurchargeEventInvitationDao = rangeSurchargeEventInvitationDao;
        this.rangeSurchargeEventPromotionDao = rangeSurchargeEventPromotionDao;
        this.rangeSurchargeEventChangeSeatDao = rangeSurchargeEventChangeSeatDao;
        this.rangeSurchargeEventSecMktDao = rangeSurchargeEventSecMktDao;
        this.rangeSurchargeEventDao = rangeSurchargeEventDao;
        this.eventDao = eventDao;
        this.eventConfigCouchDao = eventConfigCouchDao;
    }

    public List<SurchargesDTO> getRanges(Long eventId, List<SurchargeTypeDTO> types) {
        return getSurchargesByTypes(eventId, types);
    }

    private List<SurchargesDTO> getSurchargesByTypes(Long eventId, List<SurchargeTypeDTO> types) {
        List<SurchargesDTO> surcharges = new ArrayList<>();
        CpanelEventoRecord eventoRecord = eventDao.getById(eventId.intValue());
        if (types == null || types.isEmpty()) {
            types = Arrays.asList(SurchargeTypeDTO.values());
        }
        for (SurchargeTypeDTO type : types) {
            switch (type) {
                case GENERIC, PROMOTION, INVITATION, SECONDARY_MARKET_PROMOTER, SECONDARY_MARKET_CHANNEL ->
                        commonSurchargesService.getSurcharges(eventoRecord, type, surcharges);
                case CHANGE_SEAT -> {
                    // The event cannot be season ticket, but must allow relocations
                    if (checkEventAllowRelocation(eventId) &&
                            !EventType.SEASON_TICKET.equals(EventType.byId(eventoRecord.getTipoevento()))) {
                        commonSurchargesService.getSurcharges(eventoRecord, type, surcharges);
                    }
                }
                default -> throw new OneboxRestException(MsEventErrorCode.SURCHARGE_TYPE_NOT_SUPPORTED);
            }
        }
        return surcharges;
    }

    @MySQLWrite
    public void setSurcharges(Long eventId, SurchargeListDTO surchargeListDTO) {
        if (surchargeListDTO.stream().anyMatch(surchargeDTO -> surchargeDTO.getType() == null)) {
            throw new OneboxRestException(MsEventErrorCode.TYPE_MANDATORY);
        }

        if (commonSurchargesService.hasTypesDuplicated(surchargeListDTO)) {
            throw new OneboxRestException(MsEventErrorCode.SURCHARGE_TYPE_DUPLICATED);
        }

        surchargeListDTO.forEach(surchargeDTO -> this.setSurcharge(eventId, surchargeDTO));
    }

    @MySQLWrite
    public void setSurcharge(Long eventId, SurchargesDTO surchargesDTO) {
        SurchargeManager surchargeManager = surchargeManagerFactory.create(surchargesDTO);
        CpanelEventoRecord eventRecord = eventDao.getById(eventId.intValue());
        commonSurchargesService.validateSetSurcharge(eventRecord, surchargeManager, surchargesDTO.getType());

        surchargeManager.deleteSurchargesAndRanges(eventId);
        surchargeManager.insert(eventId);
    }

    @MySQLWrite
    public void deleteSurchargesAndRanges(Long eventId) {
        CpanelEventoRecord event = eventDao.getById(eventId.intValue());
        event.setRecargopromocionminimo((double)0);
        event.setRecargopromocionmaximo((double)0);
        event.setRecargominimo((double)0);
        event.setRecargomaximo((double)0);
        event.setRecargoinvmaximo((double)0);
        event.setRecargoinvminimo((double)0);
        eventDao.update(event);
        rangeSurchargeEventChangeSeatDao.deleteByEventId(eventId.intValue());
        rangeSurchargeEventInvitationDao.deleteByEventId(eventId.intValue());
        rangeSurchargeEventPromotionDao.deleteByEventId(eventId.intValue());
        rangeSurchargeEventSecMktDao.deleteByEventId(eventId.intValue());
        rangeSurchargeEventDao.deleteByEventId(eventId.intValue());
    }

    private boolean checkEventAllowRelocation(Long eventId) {
        // Check if the event allow relocations
        EventConfig eventConfig = eventConfigCouchDao.getOrInitEventConfig(eventId);
        return eventConfig.getEventChangeSeatConfig() != null &&
                eventConfig.getEventChangeSeatConfig().getAllowChangeSeat();
    }
}
