package es.onebox.mgmt.events.avetrestrictions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventType;
import es.onebox.mgmt.events.avetrestrictions.converter.AvetSectorRestrictionConverter;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionStructureDTO;
import es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvetSectorRestrictionStructureService {
    private final ValidationService validationService;

    @Autowired
    public AvetSectorRestrictionStructureService(ValidationService validationService) {
        this.validationService = validationService;
    }

    public List<AvetSectorRestrictionStructureDTO> getAvetSectorRestrictionsStructure(AvetSectorRestrictionType restrictionType,
                                                                                      Long eventId) {
        checkAvetEvent(eventId);

        List<AvetSectorRestrictionStructureDTO> filtered;
        if (restrictionType != null) {
            AvetSectorRestrictionType result = AvetSectorRestrictionType.valueOf(restrictionType.name());
            filtered = AvetSectorRestrictionConverter.toStructure(result, null);
        } else {
            AvetSectorRestrictionType[] restrictionTypes = AvetSectorRestrictionType.values();
            filtered = AvetSectorRestrictionConverter.toStructure(restrictionTypes, null);
        }
        if (restrictionType != null) {
            return filtered.stream()
                    .filter(fi -> fi.getRestrictionType() == null || (fi.getRestrictionType() != null && fi.equals(restrictionType)))
                    .collect(Collectors.toList());
        } else {
            return filtered;
        }
    }

    private void checkAvetEvent(Long eventId) {
        Event event = validationService.getAndCheckEvent(eventId);

        if (!event.getType().equals(EventType.AVET)) {
            throw new OneboxRestException(ApiMgmtErrorCode.NOT_AVET_EVENT_RESTRICTION);
        }
    }
}
