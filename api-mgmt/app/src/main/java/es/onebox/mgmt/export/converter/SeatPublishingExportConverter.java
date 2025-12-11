package es.onebox.mgmt.export.converter;

import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingExportFilter;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingsExportRequest;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingsFilter;
import es.onebox.mgmt.users.dto.UserSelfDTO;

import static es.onebox.mgmt.export.converter.ExportConverter.extractEmail;

public class SeatPublishingExportConverter {

    public SeatPublishingExportConverter() {
    }


    public static SeatPublishingExportFilter convert(SeatPublishingsFilter msFilter, SeatPublishingsExportRequest body, UserSelfDTO user, String operatorTimeZone) {
        SeatPublishingExportFilter filter = new SeatPublishingExportFilter();
        filter.setOperatorId(msFilter.getOperatorId());
        filter.setEntityIds(msFilter.getEntityIds());
        filter.setChannelIds(msFilter.getChannelIds());
        filter.setClientEntityIds(msFilter.getClientEntityIds());
        filter.setEventIds(msFilter.getEventIds());
        filter.setSessionIds(msFilter.getSessionIds());
        filter.setClientIds(msFilter.getClientIds());
        filter.setTypes(msFilter.getTypes());
        filter.setDateFrom(msFilter.getDateFrom());
        filter.setDateTo(msFilter.getDateTo());
        filter.setStatus(msFilter.getStatus());
        filter.setQ(msFilter.getQ());
        filter.setSort(msFilter.getSort());
        if (body.getSettings() != null) {
            if (body.getSettings().getCharsetEncoding() != null) {
                filter.setCharset(body.getSettings().getCharsetEncoding());
            }
            if (body.getSettings().getCsvSeparatorFormat() != null) {
                filter.setCsvSeparatorFormat(body.getSettings().getCsvSeparatorFormat());
            }
            if (body.getSettings().getCsvFractionDigitsFormat() != null) {
                filter.setCsvfractionDigitsSeparatorFormat(body.getSettings().getCsvFractionDigitsFormat());
            }
        }

        filter.setEmail(extractEmail(body.getDelivery()));
        filter.setLanguage(ConverterUtils.toLocale(user.getLanguage()));
        filter.setUserId(user.getId());
        filter.setTranslations(body.getTranslations());
        filter.setFormat(body.getFormat());
        filter.setTimeZone(operatorTimeZone);
        filter.setFields(body.getFields());
        return filter;
    }
}
