package es.onebox.event.events.converter;

import es.onebox.event.events.manager.ChannelEventSurchargeManager;
import es.onebox.event.surcharges.dto.Range;
import es.onebox.event.surcharges.dto.RangeDTO;
import es.onebox.event.surcharges.dto.RangeValueDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;

import java.util.ArrayList;
import java.util.List;

public class ChannelEventRangeConverter {
    private ChannelEventRangeConverter(){
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    private static RangeDTO fromRecord(CpanelRangoRecord rangeRecord) {
        if (rangeRecord == null) {
            return null;
        }

        RangeDTO range = new RangeDTO();
        RangeValueDTO rangeValueDTO = new RangeValueDTO();
        rangeValueDTO.setFixed(rangeRecord.getValor());
        rangeValueDTO.setMax(rangeRecord.getValormaximo());
        rangeValueDTO.setMin(rangeRecord.getValorminimo());
        rangeValueDTO.setPercentage(rangeRecord.getPorcentaje());
        range.setValues(rangeValueDTO);
        range.setFrom(rangeRecord.getRangominimo());
        range.setTo(rangeRecord.getRangomaximo());

        return range;
    }

    public static List<RangeDTO> fromRecords(List<CpanelRangoRecord> cpanelRangoRecords) {
        List<RangeDTO> ranges = new ArrayList<>();
        for (CpanelRangoRecord rangeRecord : cpanelRangoRecords) {
            RangeDTO rangeResponse = fromRecord(rangeRecord);
            ranges.add(rangeResponse);
        }
        return ranges;
    }

    public static List<CpanelRangoRecord> fromChannelEventSurchargeManager(ChannelEventSurchargeManager source) {
        List<CpanelRangoRecord> cpanelRangoRecords = new ArrayList<>();

        for (int i = 0; i < source.getRanges().size(); i++) {
            CpanelRangoRecord rangoRecord = new CpanelRangoRecord();

            Range range = source.getRanges().get(i);
            rangoRecord.setNombrerango(source.getRangeName(i));
            rangoRecord.setRangominimo(range.getFrom());
            rangoRecord.setRangomaximo(range.getTo());
            rangoRecord.setValor(range.getFixed());
            rangoRecord.setPorcentaje(range.getPercentage());
            rangoRecord.setValorminimo(range.getMin());
            rangoRecord.setValormaximo(range.getMax());
            rangoRecord.setIdcurrency(range.getCurrencyId());

            cpanelRangoRecords.add(rangoRecord);
        }

        return cpanelRangoRecords;
    }
}
