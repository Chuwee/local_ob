package es.onebox.event.common.converters;

import es.onebox.event.surcharges.dto.Range;
import es.onebox.event.surcharges.dto.RangeDTO;
import es.onebox.event.surcharges.dto.RangeValueDTO;
import es.onebox.event.surcharges.manager.SurchargeManager;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;

import java.util.ArrayList;
import java.util.List;

public class CommonRangeConverter {

    private CommonRangeConverter() {
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
        if (rangeRecord.getIdcurrency() != null) {
            range.setCurrencyId(rangeRecord.getIdcurrency());
        }

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

    public static List<CpanelRangoRecord> fromSurchargeManager(SurchargeManager source) {
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

    public static List<CpanelRangoRecord> fromProductSurchargeManager(List<Range> rangeList) {
        List<CpanelRangoRecord> cpanelRangoRecords = new ArrayList<>();

        for (Range range : rangeList) {
            CpanelRangoRecord rangoRecord = new CpanelRangoRecord();
            rangoRecord.setNombrerango(getRangeName(rangoRecord));
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

    public static String getRangeName(CpanelRangoRecord range) {
        return ((range.getRangominimo() != null) ? range.getRangominimo().toString() : "0") + "-" +
                ((range.getRangomaximo() != null) ? range.getRangomaximo().toString() : "0");
    }
}
