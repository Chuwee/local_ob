package es.onebox.event.seasontickets.converter;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.seasontickets.dto.SeasonTicketRateDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import org.apache.commons.lang3.math.NumberUtils;

public class SeasonTicketRateConverter {

    private SeasonTicketRateConverter(){ throw new UnsupportedOperationException("Cannot instantiate utilities class");}

    public static SeasonTicketRateDTO convert(RateRecord rate) {
        if (rate == null) {
            return null;
        }
        SeasonTicketRateDTO seasonTicketRateDTO = new SeasonTicketRateDTO();
        seasonTicketRateDTO.setId(rate.getIdTarifa().longValue());
        seasonTicketRateDTO.setName(rate.getNombre());
        seasonTicketRateDTO.setRestrictive(NumberUtils.INTEGER_ONE.equals(rate.getAccesoRestrictivo()));
        seasonTicketRateDTO.setDefaultRate(NumberUtils.INTEGER_ONE.equals(rate.getDefecto()));
        seasonTicketRateDTO.setDescription(rate.getDescripcion());
        seasonTicketRateDTO.setTranslations(rate.getTranslations());
        seasonTicketRateDTO.setPosition(rate.getPosition());
        if (rate.getExternalRateTypeId() != null) {
            seasonTicketRateDTO.setExternalRateType(new IdNameCodeDTO(rate.getExternalRateTypeId().longValue(), rate.getExternalRateTypeCode(), rate.getExternalRateTypeName()));
        }
        return seasonTicketRateDTO;
    }

    public static void updateRecord(CpanelTarifaRecord rateRecord, SeasonTicketRateDTO seasonTicketRateDTO) {
        ConverterUtils.updateField(rateRecord::setNombre, seasonTicketRateDTO.getName());
        ConverterUtils.updateField(rateRecord::setDescripcion, seasonTicketRateDTO.getDescription());
        ConverterUtils.updateField(rateRecord::setDefecto, ConverterUtils.isTrueAsByte(seasonTicketRateDTO.getDefaultRate()));
        ConverterUtils.updateField(rateRecord::setAccesorestrictivo, ConverterUtils.isTrueAsByte(seasonTicketRateDTO.getRestrictive()));
        ConverterUtils.updateField(rateRecord::setPosition, seasonTicketRateDTO.getPosition());
        if (seasonTicketRateDTO.getExternalRateType() != null && seasonTicketRateDTO.getExternalRateType().getId() != null) {
            ConverterUtils.updateField(rateRecord::setExternalratetypeid, seasonTicketRateDTO.getExternalRateType().getId().intValue());
        }
    }
}
