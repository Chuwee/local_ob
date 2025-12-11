package es.onebox.event.events.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.record.LimiteCupoRecord;
import es.onebox.event.events.dao.record.TierRecord;
import es.onebox.event.events.dto.TierCondition;
import es.onebox.event.events.dto.TierCreationRequestDTO;
import es.onebox.event.events.dto.TierDTO;
import es.onebox.event.events.dto.TierExtendedDTO;
import es.onebox.event.events.dto.TierSalesGroupLimitDTO;
import es.onebox.event.events.dto.TierUpdateRequestDTO;
import es.onebox.event.events.utils.EvaluableTierWrapper;
import es.onebox.jooq.cpanel.tables.records.CpanelTierRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTimeZoneGroupRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TierConverter {

    private TierConverter() {
    }

    public static TierDTO convert(CpanelTierRecord record) {
        if (record == null) {
            return null;
        }
        return convert(record, null);
    }

    public static TierDTO convert(CpanelTierRecord record, CpanelTimeZoneGroupRecord timezone) {
        if (record == null) {
            return null;
        }
        TierDTO dto = new TierDTO();
        dto.setId(record.getIdtier().longValue());
        dto.setName(record.getNombre());
        dto.setPriceTypeId(record.getIdzona().longValue());
        dto.setStartDate(CommonUtils.timestampToZonedDateTime(record.getFechaInicio()));
        dto.setPrice(record.getPrecio());
        dto.setOnSale(ConverterUtils.isByteAsATrue(record.getVenta()));
        dto.setLimit(record.getLimite());
        dto.setCondition(TierCondition.getById(record.getCondicion()));
        if(timezone != null){
            dto.setOlsonId(timezone.getOlsonid());
        }
        return dto;
    }


    public static List<TierDTO> convert(List<TierRecord> records) {
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        return records.stream().map(TierConverter::convert).collect(Collectors.toList());
    }

    public static TierExtendedDTO convertExtended(TierRecord record) {
        if (record == null) {
            return null;
        }
        TierExtendedDTO dto = new TierExtendedDTO();
        fillBaseTier(record, dto);
        if (record.getLimitesCupo() != null && !record.getLimitesCupo().isEmpty()) {
            dto.setSalesGroupLimit(record.getLimitesCupo().stream().map(TierConverter::convert).collect(Collectors.toList()));
        }
        return dto;
    }


    public static TierDTO convert(TierRecord record) {
        if (record == null) {
            return null;
        }
        TierDTO dto = new TierDTO();
        fillBaseTier(record, dto);
        return dto;
    }

    private static void fillBaseTier(TierRecord record, TierDTO dto) {
        dto.setId(record.getIdtier().longValue());
        dto.setName(record.getNombre());
        dto.setPriceTypeId(record.getIdzona().longValue());
        dto.setPriceTypeName(record.getPriceTypeName());
        dto.setStartDate(CommonUtils.timestampToZonedDateTime(record.getFechaInicio()));
        dto.setPrice(record.getPrecio());
        dto.setOnSale(ConverterUtils.isByteAsATrue(record.getVenta()));
        dto.setActive(false);
        dto.setLimit(record.getLimite());
        dto.setCondition(TierCondition.getById(record.getCondicion()));
        dto.setOlsonId(record.getTimeZoneOlsonId());
    }

    private static TierSalesGroupLimitDTO convert(LimiteCupoRecord record) {
        if (record == null) {
            return null;
        }
        TierSalesGroupLimitDTO dto = new TierSalesGroupLimitDTO();
        dto.setId(record.getIdcupo().longValue());
        dto.setName(record.getDescripcion());
        dto.setLimit(record.getLimite());
        return dto;
    }

    public static CpanelTierRecord convert(TierCreationRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        CpanelTierRecord record = new CpanelTierRecord();
        record.setNombre(dto.getName());
        record.setIdzona(ConverterUtils.longToInt(dto.getPriceTypeId()));
        record.setFechaInicio(CommonUtils.zonedDateTimeToTimestamp(dto.getStartDate()));
        record.setPrecio(dto.getPrice());
        return record;
    }


    public static CpanelTierRecord convert(TierDTO dto) {
        if (dto == null) {
            return null;
        }
        CpanelTierRecord record = new CpanelTierRecord();
        record.setIdtier(record.getIdtier());
        record.setNombre(dto.getName());
        record.setIdzona(ConverterUtils.longToInt(dto.getPriceTypeId()));
        record.setFechaInicio(CommonUtils.zonedDateTimeToTimestamp(dto.getStartDate()));
        record.setVenta(ConverterUtils.isTrueAsByte(dto.getOnSale()));
        record.setPrecio(dto.getPrice());
        record.setLimite(dto.getLimit());
        if (dto.getCondition() != null) {
            record.setCondicion(dto.getCondition().getId());
        }
        return record;
    }

    public static void updateRecord(CpanelTierRecord record, TierUpdateRequestDTO dto) {
        ConverterUtils.updateField(record::setNombre, dto.getName());
        ConverterUtils.updateField(record::setFechaInicio, CommonUtils.zonedDateTimeToTimestamp(dto.getStartDate()));
        ConverterUtils.updateField(record::setPrecio, dto.getPrice());
        ConverterUtils.updateField(record::setVenta, ConverterUtils.isTrueAsByte(dto.getOnSale()));
        ConverterUtils.updateField(record::setLimite, dto.getLimit());
        if (dto.getCondition() != null) {
            record.setCondicion(dto.getCondition().getId());
        }
    }

    public static List<EvaluableTierWrapper> fromRecords(List<CpanelTierRecord> records) {
        return records.stream()
                .map(EvaluableTierWrapper::new)
                .collect(Collectors.toList());
    }

    public static List<EvaluableTierWrapper> fromDTOs(List<TierDTO> dtos) {
        return dtos.stream()
                .map(EvaluableTierWrapper::new)
                .collect(Collectors.toList());
    }

    public static List<CpanelTierRecord> toRecords(List<EvaluableTierWrapper> wrappers) {
        return wrappers.stream()
                .map(EvaluableTierWrapper::getTierRecord)
                .collect(Collectors.toList());
    }

    public static List<TierDTO> toDTOs(List<EvaluableTierWrapper> wrappers) {
        return wrappers.stream()
                .map(EvaluableTierWrapper::getTierDTO)
                .collect(Collectors.toList());
    }
}
