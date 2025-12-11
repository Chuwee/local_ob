package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.services.CommonSurchargesService;
import es.onebox.event.events.dao.EventChangeSeatSurchargeRangeDao;
import es.onebox.event.events.dao.EventInvSurchargeRangeDao;
import es.onebox.event.events.dao.EventPromotionSurchargeRangeDao;
import es.onebox.event.events.dao.EventSurchargeRangeDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.surcharges.dao.SurchargeRangeDao;
import es.onebox.event.seasontickets.dao.SeasonTicketEventDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEntityDao;
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
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoEventoCambioLocalidadRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.event.surcharges.dto.SurchargeTypeDTO.CHANGE_SEAT;
import static es.onebox.event.surcharges.dto.SurchargeTypeDTO.GENERIC;
import static es.onebox.event.surcharges.dto.SurchargeTypeDTO.INVITATION;
import static es.onebox.event.surcharges.dto.SurchargeTypeDTO.PROMOTION;
import static es.onebox.event.surcharges.dto.SurchargeTypeDTO.SECONDARY_MARKET_PROMOTER;

@Service
public class SeasonTicketSurchargesService extends CommonSurchargesService {

    private final SurchargeManagerFactory surchargeManagerFactory;
    private final SeasonTicketEventDao seasonTicketEventDao;
    private final EventChangeSeatSurchargeRangeDao eventChangeSeatSurchargeRangeDao;
    private final SeasonTicketHelper seasonTicketHelper;

    @Autowired
    public SeasonTicketSurchargesService(SurchargeManagerFactory surchargeManagerFactory,
                                         SeasonTicketEventDao seasonTicketEventDao,
                                         RangeSurchargeEventChangeSeatDao rangeSurchargeEventChangeSeatDao,
                                         EventChangeSeatSurchargeRangeDao eventChangeSeatSurchargeRangeDao,
                                         RangeSurchargeEventDao rangeSurchargeEventDao, RangeSurchargeEntityDao rangeSurchargeEntityDao,
                                         RangeSurchargeEventInvitationDao rangeSurchargeEventInvitationDao,
                                         RangeSurchargeEventPromotionDao rangeSurchargeEventPromotionDao,
                                         SurchargeRangeDao surchargeRangeDao, EventSurchargeRangeDao eventSurchargeRangeDao,
                                         EventInvSurchargeRangeDao eventInvSurchargeRangeDao,
                                         EventPromotionSurchargeRangeDao eventPromotionSurchargeRangeDao,
                                         RangeSurchargeEventSecondaryMarketDao rangeSurchargeEventSecMktDao,
                                         SeasonTicketHelper seasonTicketHelper) {
        super(rangeSurchargeEventDao, rangeSurchargeEntityDao, rangeSurchargeEventInvitationDao, rangeSurchargeEventPromotionDao,
                surchargeRangeDao, eventSurchargeRangeDao, eventInvSurchargeRangeDao, eventPromotionSurchargeRangeDao, rangeSurchargeEventSecMktDao,
                rangeSurchargeEventChangeSeatDao);
        this.surchargeManagerFactory = surchargeManagerFactory;
        this.seasonTicketEventDao = seasonTicketEventDao;
        this.eventChangeSeatSurchargeRangeDao = eventChangeSeatSurchargeRangeDao;
        this.seasonTicketHelper = seasonTicketHelper;
    }

    public List<SurchargesDTO> getSeasonTicketSurcharges(Long seasonTicketId, List<SurchargeTypeDTO> types) {
        return getSurchargesByTypes(seasonTicketId, types);
    }

    private List<SurchargesDTO> getSurchargesByTypes(Long seasonTicketId, List<SurchargeTypeDTO> types) {
        List<SurchargesDTO> surcharges = new ArrayList<>();
        CpanelEventoRecord seasonTicketEventoRecord = seasonTicketEventDao.getById(seasonTicketId.intValue());
        if (types == null || types.isEmpty()) {
            types = Arrays.asList(GENERIC, PROMOTION, INVITATION, CHANGE_SEAT, SECONDARY_MARKET_PROMOTER);
        }
        EventRecord eventRecord = seasonTicketHelper.getAndCheckSeasonTicket(seasonTicketId);
        if (!eventRecord.getAllowChangeSeat()) {
            types = types.stream().filter(type -> type != CHANGE_SEAT).collect(Collectors.toList());
        }
        for (SurchargeTypeDTO type : types) {
            switch (type) {
                case GENERIC, PROMOTION, INVITATION, SECONDARY_MARKET_PROMOTER-> getSurcharges(seasonTicketEventoRecord, type, surcharges);
                case CHANGE_SEAT -> getSeasonTicketSurcharges(seasonTicketEventoRecord, type, surcharges);
                default -> throw new OneboxRestException(MsEventErrorCode.SURCHARGE_TYPE_NOT_SUPPORTED);
            }
        }
        return surcharges;
    }

    @MySQLWrite
    public void setSeasonTicketSurcharges(Long seasonTicketId, SurchargeListDTO surchargeListDTO) {
        if (surchargeListDTO.stream().anyMatch(surchargeDTO -> surchargeDTO.getType() == null)) {
            throw new OneboxRestException(MsEventErrorCode.TYPE_MANDATORY);
        }

        if (hasTypesDuplicated(surchargeListDTO)) {
            throw new OneboxRestException(MsEventErrorCode.SURCHARGE_TYPE_DUPLICATED);
        }

        surchargeListDTO.forEach(surchargeDTO -> setSurcharge(seasonTicketId, surchargeDTO));
    }

    @MySQLWrite
    public void setSurcharge(Long seasonTicketId, SurchargesDTO surchargesDTO) {
        SurchargeManager surchargeManager = surchargeManagerFactory.create(surchargesDTO);
        CpanelEventoRecord seasonTicketEventDaoById = seasonTicketEventDao.getById(seasonTicketId.intValue());
        validateSetSurcharge(seasonTicketEventDaoById, surchargeManager, surchargesDTO.getType());

        surchargeManager.deleteSurchargesAndRanges(seasonTicketId);
        surchargeManager.insert(seasonTicketId);
    }

    public void initSeasonTicketSurcharges(CpanelEventoRecord eventoRecord) {
        initEventSurcharges(eventoRecord);
        initChangeSeatSurcharges(eventoRecord);
    }

    private void initChangeSeatSurcharges(CpanelEventoRecord eventoRecord) {
        CpanelRangoRecord newEventPromotionRange = insertRangeRecord(eventoRecord.getIdcurrency());
        CpanelRangoRecargoEventoCambioLocalidadRecord cpanelRangoRecargoEventoCambioLocalidadRecord = new CpanelRangoRecargoEventoCambioLocalidadRecord();
        cpanelRangoRecargoEventoCambioLocalidadRecord.setIdevento(eventoRecord.getIdevento());
        cpanelRangoRecargoEventoCambioLocalidadRecord.setIdrango(newEventPromotionRange.getIdrango());
        eventChangeSeatSurchargeRangeDao.insert(cpanelRangoRecargoEventoCambioLocalidadRecord);
    }

    private void getSeasonTicketSurcharges(CpanelEventoRecord seasonTicket, SurchargeTypeDTO type, List<SurchargesDTO> surcharges) {
        if (!SurchargeTypeDTO.CHANGE_SEAT.equals(type)) {
            throw new OneboxRestException(MsEventErrorCode.SURCHARGE_TYPE_NOT_SUPPORTED);
        }
        surcharges.add(getChangeSeatSurcharges(seasonTicket));
    }
}
