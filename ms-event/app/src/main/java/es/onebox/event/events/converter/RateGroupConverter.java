package es.onebox.event.events.converter;

import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.record.RateGroupRecord;
import es.onebox.event.events.dto.CreateRateGroupRequestDTO;
import es.onebox.event.events.dto.RateGroupDTO;
import es.onebox.event.events.dto.UpdateRateGroupRequestDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelGrupoTarifaRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RateGroupConverter {

    private RateGroupConverter() {
    }

    public static RateGroupDTO convert(RateGroupRecord rate) {
        if (rate == null) {
            return null;
        }
        RateGroupDTO rateDTO = new RateGroupDTO();
        rateDTO.setId(rate.getIdGrupoTarifa().longValue());
        rateDTO.setName(rate.getNombre());
        rateDTO.setDefaultRate(NumberUtils.INTEGER_ONE.equals(rate.getDefecto()));
        rateDTO.setTranslations(rate.getTranslations());
        rateDTO.setExternalDescription(rate.getDescripcionExterna());
        rateDTO.setPosition(rate.getPosition());
        return rateDTO;
    }

    public static RateGroupDTO convert(RateGroupRecord rate, String name) {
        if (rate == null) {
            return null;
        }
        RateGroupDTO rateDTO = new RateGroupDTO();
        rateDTO.setId(rate.getIdGrupoTarifa().longValue());
        rateDTO.setName(name);
        rateDTO.setDefaultRate(NumberUtils.INTEGER_ONE.equals(rate.getDefecto()));
        rateDTO.setTranslations(rate.getTranslations());
        rateDTO.setExternalDescription(rate.getDescripcionExterna());
        return rateDTO;
    }

    public static void updateRecord(CpanelGrupoTarifaRecord rateRecord, RateGroupDTO rateDTO) {
        ConverterUtils.updateField(rateRecord::setNombre, rateDTO.getName());
        ConverterUtils.updateField(rateRecord::setDefecto, ConverterUtils.isTrueAsByte(rateDTO.getDefaultRate()));
        ConverterUtils.updateField(rateRecord::setDescripcionexterna, rateDTO.getExternalDescription());
        ConverterUtils.updateField(rateRecord::setPosition, rateDTO.getPosition());
    }

    public static RateGroupDTO toDTO(CreateRateGroupRequestDTO createRateGroupDTO) {
        RateGroupDTO rateGroupDTO = new RateGroupDTO();
        rateGroupDTO.setExternalDescription(createRateGroupDTO.getExternalDescription());
        rateGroupDTO.setName(createRateGroupDTO.getName());
        rateGroupDTO.setTranslations(createRateGroupDTO.getTranslations());
        rateGroupDTO.setType(createRateGroupDTO.getType());
        return rateGroupDTO;
    }

    public static List<RateGroupDTO> toDTO(List <UpdateRateGroupRequestDTO> createRateGroupDTO) {
        if (CollectionUtils.isEmpty(createRateGroupDTO)) {
            return new ArrayList<>();
        }

        return createRateGroupDTO.stream()
                .map(RateGroupConverter::toDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static RateGroupDTO toDTO(UpdateRateGroupRequestDTO updateRateGroupRequestDTO) {
        RateGroupDTO rateGroupDTO = new RateGroupDTO();
        rateGroupDTO.setId(updateRateGroupRequestDTO.getId());
        rateGroupDTO.setExternalDescription(updateRateGroupRequestDTO.getExternalDescription());
        rateGroupDTO.setName(updateRateGroupRequestDTO.getName());
        rateGroupDTO.setTranslations(updateRateGroupRequestDTO.getTranslations());
        rateGroupDTO.setPosition(updateRateGroupRequestDTO.getPosition());
        return rateGroupDTO;
    }

    public static RateGroupDTO toDTO(CpanelGrupoTarifaRecord cpanelGrupoTarifaRecord) {
        RateGroupDTO rateGroupDTO = new RateGroupDTO();
        rateGroupDTO.setId(Long.valueOf(cpanelGrupoTarifaRecord.getIdgrupotarifa()));
        rateGroupDTO.setExternalDescription(cpanelGrupoTarifaRecord.getDescripcionexterna());
        rateGroupDTO.setName(cpanelGrupoTarifaRecord.getNombre());
        return rateGroupDTO;
    }
}
