package es.onebox.mgmt.export.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalsExportFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketsRenewalsExportRequest;
import es.onebox.mgmt.users.dto.UserSelfDTO;

import static es.onebox.mgmt.export.converter.ExportConverter.extractEmail;

public class SeasonTicketRenewalsExportConverter {

    private SeasonTicketRenewalsExportConverter() {
    }
    
    public static SeasonTicketRenewalsExportFilter convert(SeasonTicketsRenewalsExportRequest requestBody,
                                                           UserSelfDTO user,
                                                           String operatorTimeZone) {
        SeasonTicketRenewalsExportFilter filter = new SeasonTicketRenewalsExportFilter();
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
                filter.setCsvfractionDigitsSeparatorFormat(requestBody.getSettings().getCsvFractionDigitsFormat());
            }
        }
        filter.setLanguage(ConverterUtils.toLocale(user.getLanguage()));
        filter.setTranslations(requestBody.getTranslations());

        return filter;
    }

}
