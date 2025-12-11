package es.onebox.event.seasontickets.converter;

import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.seasontickets.dto.SeasonTicketRateDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;

import static es.onebox.utils.ObjectRandomizer.random;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeasonTicketRateRecordConverterTest {

    @Test
    public void convertTest() {

        RateRecord rateRecord = random(RateRecord.class);
        SeasonTicketRateDTO seasonTicketRateDTO = SeasonTicketRateConverter.convert(rateRecord);

        assertEquals(rateRecord.getIdTarifa().intValue(), seasonTicketRateDTO.getId().intValue());
        assertEquals(rateRecord.getNombre(), seasonTicketRateDTO.getName());
        assertEquals(rateRecord.getDescripcion(), seasonTicketRateDTO.getDescription());

        assertEquals(NumberUtils.INTEGER_ONE.equals(rateRecord.getAccesoRestrictivo()), seasonTicketRateDTO.getRestrictive());
        assertEquals(NumberUtils.INTEGER_ONE.equals(rateRecord.getDefecto()), seasonTicketRateDTO.getDefaultRate());

        assertEquals(rateRecord.getTranslations(), seasonTicketRateDTO.getTranslations());
    }

    @Test
    public void updateRecordTest(){
        CpanelTarifaRecord rateRecord = new CpanelTarifaRecord();
        SeasonTicketRateDTO seasonTicketRateDTO = random(SeasonTicketRateDTO.class);

        SeasonTicketRateConverter.updateRecord(rateRecord, seasonTicketRateDTO);

        assertEquals(seasonTicketRateDTO.getName(), rateRecord.getNombre());
        assertEquals(seasonTicketRateDTO.getDescription(), rateRecord.getDescripcion());
        assertEquals(seasonTicketRateDTO.getDefaultRate(), ConverterUtils.isByteAsATrue(rateRecord.getDefecto()));
        assertEquals(seasonTicketRateDTO.getRestrictive(), ConverterUtils.isByteAsATrue(rateRecord.getAccesorestrictivo()));
    }
}
