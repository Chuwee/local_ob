package es.onebox.event.common.utils;

import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;

public class RangesUtils {

    public static CpanelRangoRecord defaultRanges(Integer currencyId) {
        CpanelRangoRecord range = new CpanelRangoRecord();
        range.setRangomaximo(0d);
        range.setRangominimo(0d);
        range.setValor(0d);
        range.setPorcentaje(0d);
        range.setValormaximo(0d);
        range.setValorminimo(0d);
        range.setNombrerango("0.0-0.0");
        range.setIdcurrency(currencyId);

        return range;
    }
}
