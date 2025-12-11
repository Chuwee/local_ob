package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.seasontickets.dao.SeasonTicketEventDao;
import es.onebox.event.seasontickets.dto.SeasonTicketRateDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SeasonTicketRateValidatorTest {

    @InjectMocks
    private SeasonTicketRateValidator seasonTicketRateValidator;

    @Mock
    private SeasonTicketEventDao seasonTicketEventDao;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void checkSeasonTicketNotFoundTest() {
        Integer seasonTicketId = 1;
        when(seasonTicketEventDao.getById(any()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(OneboxRestException.class, () ->
                seasonTicketRateValidator.checkSeasonTicket(seasonTicketId));
    }

    @Test
    public void checkSeasonTicketFoundTest() {
        Integer seasonTicketId = 1;
        CpanelEventoRecord eventoRecord = new CpanelEventoRecord();
        when(seasonTicketEventDao.getById(any()))
                .thenReturn(eventoRecord);

        seasonTicketRateValidator.checkSeasonTicket(seasonTicketId);
    }

    @Test
    public void checkDescriptionNullTest() {
        SeasonTicketRateDTO seasonTicketRateDTO = new SeasonTicketRateDTO();
        seasonTicketRateValidator.checkDescription(seasonTicketRateDTO);
    }

    @Test
    public void checkDescriptionVoidTest() {
        SeasonTicketRateDTO seasonTicketRateDTO = new SeasonTicketRateDTO();
        seasonTicketRateDTO.setDescription("");
        seasonTicketRateValidator.checkDescription(seasonTicketRateDTO);
    }

    @Test
    public void checkDescriptionNotVoidTest() {
        SeasonTicketRateDTO seasonTicketRateDTO = new SeasonTicketRateDTO();
        seasonTicketRateDTO.setDescription("foo");
        seasonTicketRateValidator.checkDescription(seasonTicketRateDTO);
    }

    @Test
    public void checkDescription200charactersTest() {
        SeasonTicketRateDTO seasonTicketRateDTO = new SeasonTicketRateDTO();
        seasonTicketRateDTO.setDescription(random(200));

        seasonTicketRateValidator.checkDescription(seasonTicketRateDTO);
    }

    @Test
    public void checkDescription201charactersTest() {
        SeasonTicketRateDTO seasonTicketRateDTO = new SeasonTicketRateDTO();
        seasonTicketRateDTO.setDescription(random(201));

        Assertions.assertThrows(OneboxRestException.class, () ->
                seasonTicketRateValidator.checkDescription(seasonTicketRateDTO));
    }

    private CpanelTarifaRecord createTarifaRecord(String nombre) {
        CpanelTarifaRecord rate = new CpanelTarifaRecord();
        rate.setNombre(nombre);
        return rate;
    }

    @Test
    public void checkSeasonTicketRateNamesNullNameTest() {
        String newRateName = null;

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord("foo"));
        seasonTicketRates.add(createTarifaRecord("boo"));
        seasonTicketRates.add(createTarifaRecord("woo"));

        Assertions.assertThrows(OneboxRestException.class, () ->
                seasonTicketRateValidator.checkSeasonTicketRateNames(newRateName, seasonTicketRates));
    }

    @Test
    public void checkSeasonTicketRateNamesEmptyNameTest() {
        String newRateName = "";

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord("foo"));
        seasonTicketRates.add(createTarifaRecord("boo"));
        seasonTicketRates.add(createTarifaRecord("woo"));

        Assertions.assertThrows(OneboxRestException.class, () ->
                seasonTicketRateValidator.checkSeasonTicketRateNames(newRateName, seasonTicketRates));
    }

    @Test
    public void checkSeasonTicketRateNames51charactersTest() {
        String newRateName = random(51);

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord("foo"));
        seasonTicketRates.add(createTarifaRecord("boo"));
        seasonTicketRates.add(createTarifaRecord("woo"));

        Assertions.assertThrows(OneboxRestException.class, () ->
                seasonTicketRateValidator.checkSeasonTicketRateNames(newRateName, seasonTicketRates));
    }

    @Test
    public void checkSeasonTicketRateNamesRepeatedNameTest() {
        String newRateName = "boo";

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord("foo"));
        seasonTicketRates.add(createTarifaRecord("boo"));
        seasonTicketRates.add(createTarifaRecord("woo"));

        Assertions.assertThrows(OneboxRestException.class, () ->
                seasonTicketRateValidator.checkSeasonTicketRateNames(newRateName, seasonTicketRates));
    }

    @Test
    public void checkSeasonTicketRateNamesOtherNameTest() {
        String newRateName = "yoo";

        List<CpanelTarifaRecord> seasonTicketRates = new ArrayList<>();
        seasonTicketRates.add(createTarifaRecord("foo"));
        seasonTicketRates.add(createTarifaRecord("boo"));
        seasonTicketRates.add(createTarifaRecord("woo"));

        seasonTicketRateValidator.checkSeasonTicketRateNames(newRateName, seasonTicketRates);
    }
}
