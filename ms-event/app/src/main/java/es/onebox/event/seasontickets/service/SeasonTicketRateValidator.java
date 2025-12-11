package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.seasontickets.dao.SeasonTicketEventDao;
import es.onebox.event.seasontickets.dto.SeasonTicketRateDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeasonTicketRateValidator {

    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    private final SeasonTicketEventDao seasonTicketEventDao;

    @Autowired
    public SeasonTicketRateValidator(SeasonTicketEventDao seasonTicketEventDao) {
        this.seasonTicketEventDao = seasonTicketEventDao;
    }

    void checkSeasonTicket(Integer seasonTicketId) {
        try {
            seasonTicketEventDao.getById(seasonTicketId);
        } catch (EntityNotFoundException ex) {
            throw OneboxRestException.builder(MsEventSeasonTicketErrorCode.SEASON_TICKET_NOT_FOUND).build();
        }
    }

    void checkSeasonTicketRateNames(String newRateName, List<CpanelTarifaRecord> seasonTicketRates) {
        checkSeasonTicketRateNames(newRateName, seasonTicketRates.stream().
                map(CpanelTarifaRecord::getNombre).collect(Collectors.toList()));
    }

    void checkSeasonTicketRateNames(String newRateName, Collection<String> rateNames) {
        if(StringUtils.isEmpty(newRateName)) {
            throw OneboxRestException.builder(MsEventErrorCode.INVALID_NAME_FORMAT).
                    setMessage("Rate name is required").build();
        }

        if(newRateName.length() > MAX_NAME_LENGTH) {
            throw OneboxRestException.builder(MsEventErrorCode.INVALID_NAME_FORMAT).
                    setMessage("Rate name length too large").build();
        }

        if (rateNames.stream().anyMatch(newRateName::equals)) {
            throw OneboxRestException.builder(MsEventErrorCode.REPEATED_NAME).
                    setMessage("Rate name already in use").build();
        }
    }

    void checkDescription(SeasonTicketRateDTO seasonTicketRateDTO) {
        if(seasonTicketRateDTO.getDescription() != null && seasonTicketRateDTO.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw OneboxRestException.builder(MsEventErrorCode.INVALID_DESCRIPTION_LENGTH).build();
        }
    }
}
