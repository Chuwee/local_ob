package es.onebox.mgmt.export.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.salerequests.pricesimulation.dto.PriceSimulationExportFilter;
import es.onebox.mgmt.salerequests.pricesimulation.dto.PriceSimulationExportRequest;
import es.onebox.mgmt.users.dto.UserSelfDTO;

import static es.onebox.mgmt.export.converter.ExportConverter.extractEmail;

public class PriceSimulationExportConverter {

    private PriceSimulationExportConverter() {

    }

    public static PriceSimulationExportFilter convert(PriceSimulationExportRequest requestBody,
        UserSelfDTO user,
        String operatorTimeZone) {
        PriceSimulationExportFilter filter = new PriceSimulationExportFilter();
        filter.setFields(requestBody.getFields());
        filter.setFormat(requestBody.getFormat());
        filter.setEmail(extractEmail(requestBody.getDelivery()));
        filter.setUserId(user.getId());
        filter.setTimeZone(operatorTimeZone);
        if (requestBody.getSettings() != null) {
            if (requestBody.getSettings().getCharsetEncoding() != null) {
                filter.setCharset(requestBody.getSettings().getCharsetEncoding());
            }
            if (requestBody.getSettings().getCsvSeparatorFormat() != null) {
                filter.setCsvSeparatorFormat(requestBody.getSettings().getCsvSeparatorFormat());
            }
            if (requestBody.getSettings().getCsvFractionDigitsFormat() != null) {
                filter.setCsvFractionDigitsFormat(
                    requestBody.getSettings().getCsvFractionDigitsFormat());
            }
        }
        filter.setLanguage(ConverterUtils.toLocale(user.getLanguage()));
        filter.setTranslations(requestBody.getTranslations());

        return filter;
    }
}
