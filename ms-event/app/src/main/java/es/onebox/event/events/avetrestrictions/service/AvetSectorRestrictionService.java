package es.onebox.event.events.avetrestrictions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.avetrestrictions.converter.AvetSectorRestrictionConverter;
import es.onebox.event.events.avetrestrictions.dto.AvetSectorRestrictionCreateDTO;
import es.onebox.event.events.avetrestrictions.dto.AvetSectorRestrictionDetailDTO;
import es.onebox.event.events.avetrestrictions.dto.AvetSectorRestrictionsDTO;
import es.onebox.event.events.avetrestrictions.dto.UpdateAvetSectorRestrictionDTO;
import es.onebox.event.events.dao.EventAvetConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.domain.eventconfig.EventAvetConfig;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.couch.AvetSectorRestriction;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static es.onebox.event.events.avetrestrictions.converter.AvetSectorRestrictionConverter.updateFields;

@Service
public class AvetSectorRestrictionService {
    private final EventAvetConfigCouchDao eventAvetConfigCouchDao;
    private final EventDao eventDao;

    @Autowired
    public AvetSectorRestrictionService(EventAvetConfigCouchDao eventAvetConfigCouchDao, EventDao eventDao) {
        this.eventAvetConfigCouchDao = eventAvetConfigCouchDao;
        this.eventDao = eventDao;
    }

    public String createAvetSectorRestriction(Long eventId,
                                              AvetSectorRestrictionCreateDTO avetSectorRestrictionCreateDTO) {
        checkAvetEvent(eventId);
        EventAvetConfig eventAvetConfig = eventAvetConfigCouchDao.get(String.valueOf(eventId));
        if (eventAvetConfig == null) {
            eventAvetConfig = new EventAvetConfig();
            eventAvetConfig.setEventId(eventId.intValue());
        }

        //Map to document
        AvetSectorRestriction avetSectorRestriction =
                AvetSectorRestrictionConverter.toAvetSectorRestriction(avetSectorRestrictionCreateDTO);

        //Get restrictions and insert a new one in avet configs
        List<AvetSectorRestriction> restrictions = Optional.ofNullable(eventAvetConfig.getRestrictions())
                .orElseGet(ArrayList::new);

        //Avoid duplicated values
        checkDuplicates(restrictions, avetSectorRestriction);

        restrictions.add(avetSectorRestriction);
        eventAvetConfig.setRestrictions(restrictions);

        //Upsert couch document
        eventAvetConfigCouchDao.upsert(String.valueOf(eventId), eventAvetConfig);

        return avetSectorRestriction.getSid();
    }

    public AvetSectorRestrictionDetailDTO getAvetSectorRestriction(Long eventId, String restrictionId) {
        checkAvetEvent(eventId);

        EventAvetConfig eventAvetConfig =
                eventAvetConfigCouchDao.get(eventId.toString());

        if (eventAvetConfig != null && eventAvetConfig.getRestrictions() != null &&
                !eventAvetConfig.getRestrictions().isEmpty()) {
            AvetSectorRestriction avetSectorRestriction = eventAvetConfig.getRestrictions().stream()
                    .filter(dto -> dto.getSid().equals(restrictionId))
                    .findFirst().orElseThrow(() -> new OneboxRestException(MsEventErrorCode.AVET_SECTOR_RESTRICTION_NOT_FOUND));

            return AvetSectorRestrictionConverter.toAvetSectorRestrictionDetailDTO(avetSectorRestriction);
        }
        throw new OneboxRestException(MsEventErrorCode.AVET_SECTOR_RESTRICTION_NOT_FOUND);
    }

    public AvetSectorRestrictionsDTO getAvetSectorRestrictions(Long eventId, Boolean fullPayload) {
        checkAvetEvent(eventId);

        EventAvetConfig eventAvetConfig =
                eventAvetConfigCouchDao.get(eventId.toString());

        if (eventAvetConfig != null && eventAvetConfig.getRestrictions() != null &&
                !eventAvetConfig.getRestrictions().isEmpty()) {
            return AvetSectorRestrictionConverter.toAvetSectorRestrictionsDTO(eventAvetConfig.getRestrictions(), fullPayload);
        }

        return new AvetSectorRestrictionsDTO();
    }

    public void updateAvetSectorRestriction(Long eventId, String restrictionId, UpdateAvetSectorRestrictionDTO updateAvetSectorRestrictionDTO) {
        checkAvetEvent(eventId);

        EventAvetConfig eventAvetConfig =
                eventAvetConfigCouchDao.get(eventId.toString());

        AvetSectorRestriction currentRestriction = null;

        if (eventAvetConfig == null || eventAvetConfig.getRestrictions() == null) {
            throw new OneboxRestException(MsEventErrorCode.AVET_SECTOR_RESTRICTION_NOT_FOUND);
        }

        for (AvetSectorRestriction avetSectorRestriction : eventAvetConfig.getRestrictions()) {
            if (avetSectorRestriction.getName().equals(updateAvetSectorRestrictionDTO.getName()) &&
                    !avetSectorRestriction.getSid().equals(restrictionId)) {
                throw new OneboxRestException(MsEventErrorCode.DUPLICATE_AVET_SECTOR_RESTRICTION_NAME);
            }
            if (avetSectorRestriction.getSid().equals(restrictionId)) {
                currentRestriction = avetSectorRestriction;
            }
        }

        if (currentRestriction == null) {
            throw new OneboxRestException(MsEventErrorCode.AVET_SECTOR_RESTRICTION_NOT_FOUND);
        }

        updateFields(currentRestriction, updateAvetSectorRestrictionDTO);

        eventAvetConfigCouchDao.upsert(String.valueOf(eventId), eventAvetConfig);
    }

    public void deleteAvetSectorRestriction(Long eventId, String restrictionId) {
        checkAvetEvent(eventId);

        eventAvetConfigCouchDao.removeRestrictionByEventIdAndRestrictionId(eventId, restrictionId);
    }

    private void checkAvetEvent(Long eventId) {
        CpanelEventoRecord cpanelEventoRecord = eventDao.findById(eventId.intValue());
        if (cpanelEventoRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_NOT_FOUND);
        }

        if (!cpanelEventoRecord.getTipoevento().equals(EventType.AVET.getId())) {
            throw new OneboxRestException(MsEventErrorCode.NOT_AVET_EVENT);
        }
    }

    private void checkDuplicates(List<AvetSectorRestriction> restrictions, AvetSectorRestriction avetSectorRestriction) {
        for (AvetSectorRestriction restriction : restrictions) {
            if (restriction.getName().equals(avetSectorRestriction.getName())) {
                throw new OneboxRestException(MsEventErrorCode.DUPLICATE_AVET_SECTOR_RESTRICTION_NAME);
            }
            if (restriction.getSid().equals(avetSectorRestriction.getSid())) {
                throw new OneboxRestException(MsEventErrorCode.DUPLICATE_AVET_SECTOR_RESTRICTION_SID);
            }
        }
    }
}
