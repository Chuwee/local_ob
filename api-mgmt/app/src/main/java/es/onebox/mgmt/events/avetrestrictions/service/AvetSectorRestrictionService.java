package es.onebox.mgmt.events.avetrestrictions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventType;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.events.avetrestrictions.converter.AvetSectorRestrictionConverter;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionCreateDTO;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionDetailDTO;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionUpdateDTO;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionsDTO;
import es.onebox.mgmt.events.avetrestrictions.mapper.AvetSectorRestriction;
import es.onebox.mgmt.events.avetrestrictions.mapper.AvetSectorRestrictionCreate;
import es.onebox.mgmt.events.avetrestrictions.mapper.AvetSectorRestrictions;
import es.onebox.mgmt.events.avetrestrictions.mapper.UpdateAvetSectorRestriction;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvetSectorRestrictionService {
    private final ValidationService validationService;
    private final EventsRepository eventsRepository;

    @Autowired
    public AvetSectorRestrictionService(ValidationService validationService, EventsRepository eventsRepository) {
        this.validationService = validationService;
        this.eventsRepository = eventsRepository;
    }

    public String createAvetSectorRestriction(Long eventId, AvetSectorRestrictionCreateDTO avetSectorRestrictionCreateDTO) {
        checkAvetEvent(eventId);
        AvetSectorRestrictionCreate createAvetSectorRestriction = AvetSectorRestrictionConverter.convert(avetSectorRestrictionCreateDTO);

        return eventsRepository.createAvetSectorRestriction(eventId, createAvetSectorRestriction);
    }

    public AvetSectorRestrictionDetailDTO getAvetSectorRestriction(Long eventId, String restrictionId) {
        checkAvetEvent(eventId);

        AvetSectorRestriction avetSectorRestriction = eventsRepository.getAvetSectorRestriction(eventId, restrictionId);

        return AvetSectorRestrictionConverter.toAvetSectorRestrictionDetailDTO(avetSectorRestriction);
    }

    public AvetSectorRestrictionsDTO getAvetSectorRestrictions(Long eventId) {
        checkAvetEvent(eventId);

        AvetSectorRestrictions avetSectorRestrictions = eventsRepository.getAvetSectorRestrictions(eventId);

        return AvetSectorRestrictionConverter.toAvetSectorRestrictionsDTO(avetSectorRestrictions);
    }

    public void updateAvetSectorRestriction(Long eventId, String restrictionId,
                                            AvetSectorRestrictionUpdateDTO avetSectorRestrictionUpdateDTO) {
        checkAvetEvent(eventId);
        AvetSectorRestriction avetSectorRestriction = eventsRepository.getAvetSectorRestriction(eventId, restrictionId);

        if (avetSectorRestriction == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.AVET_SECTOR_RESTRICTION_NOT_FOUND);
        }
        if (BooleanUtils.isTrue(avetSectorRestrictionUpdateDTO.getActivated())
                || avetSectorRestrictionUpdateDTO.getActivated() == null) {
            checkExistentRestriction(avetSectorRestriction);
        }

        UpdateAvetSectorRestriction updateAvetSectorRestriction =
                AvetSectorRestrictionConverter.toUpdateAvetSectorRestriction(avetSectorRestriction.getType(),
                        avetSectorRestrictionUpdateDTO);

        eventsRepository.updateAvetSectorRestriction(eventId, restrictionId, updateAvetSectorRestriction);
    }

    public void deleteAvetSectorRestriction(Long eventId, String restrictionId) {
        Event event = validationService.getAndCheckEvent(eventId);

        if (!event.getType().equals(EventType.AVET)) {
            throw new OneboxRestException(ApiMgmtErrorCode.NOT_AVET_EVENT_RESTRICTION);
        }

        eventsRepository.deleteAvetSectorRestriction(eventId, restrictionId);
    }

    private void checkAvetEvent(Long eventId) {
        Event event = validationService.getAndCheckEvent(eventId);

        if (!event.getType().equals(EventType.AVET)) {
            throw new OneboxRestException(ApiMgmtErrorCode.NOT_AVET_EVENT_RESTRICTION);
        }
    }

    private void checkExistentRestriction(AvetSectorRestriction avetSectorRestriction) {
        if (avetSectorRestriction.getName() == null || avetSectorRestriction.getType() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.RESTRICTION_CANNOT_BE_UPDATED);
        }
    }
}
