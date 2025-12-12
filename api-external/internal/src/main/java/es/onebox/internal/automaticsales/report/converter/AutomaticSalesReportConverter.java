package es.onebox.internal.automaticsales.report.converter;

import es.onebox.internal.automaticsales.eip.report.AutomaticSalesReportMessageFilter;
import es.onebox.internal.automaticsales.report.dto.AutomaticSaleDTO;
import es.onebox.internal.automaticsales.report.enums.ApiExternalExportType;
import es.onebox.internal.automaticsales.report.model.AutomaticSaleReport;
import es.onebox.internal.automaticsales.report.model.AutomaticSalesReportFilter;

public class AutomaticSalesReportConverter {

    private AutomaticSalesReportConverter() {
        throw new UnsupportedOperationException();
    }

    public static AutomaticSalesReportMessageFilter toMessage(final String exportId, final Long sessionId,
                                                              final AutomaticSalesReportFilter filter) {
        AutomaticSalesReportMessageFilter message = new AutomaticSalesReportMessageFilter();
        message.setSessionId(sessionId);
        message.setLanguage(filter.getLanguage());
        message.setQ(filter.getQ());
        message.setFields(filter.getFields());
        message.setFormat(filter.getFormat());
        message.setEmail(filter.getEmail());
        message.setUserId(filter.getUserId());
        message.setExportType(ApiExternalExportType.AUTOMATIC_SALES);
        message.setCsvSeparatorFormat(filter.getCsvSeparatorFormat());
        message.setExportId(exportId);
        message.setTimeZone(filter.getTimeZone());
        message.setTranslations(filter.getTranslations());
        return message;
    }

    public static AutomaticSaleReport toReport(AutomaticSaleDTO in) {
        AutomaticSaleReport out = new AutomaticSaleReport();
        out.setGroup(in.getGroup());
        out.setNum(in.getNum());
        out.setName(in.getName());
        out.setFirstSurname(in.getFirstSurname());
        out.setSecondSurname(in.getSecondSurname());
        out.setDni(in.getDni());
        out.setPhone(in.getPhone());
        out.setEmail(in.getEmail());
        out.setSector(in.getSector());
        out.setPriceZone(in.getPriceZone());
        out.setOwner(in.isOwner());
        out.setSeatId(in.getSeatId());
        out.setOriginalLocator(in.getOriginalLocator());
        out.setLanguage(in.getLanguage());
        out.setProcessed(in.isProcessed());
        out.setErrorCode(in.getErrorCode());
        out.setErrorDescription(in.getErrorDescription());
        out.setOrderId(in.getOrderId());
        out.setTraceId(in.getTraceId());
        out.setExtraField(in.getExtraField());
        return out;
    }

    public static AutomaticSaleDTO toMs(AutomaticSaleDTO in) {
        AutomaticSaleDTO out = new AutomaticSaleDTO();
        out.setGroup(in.getGroup());
        out.setNum(in.getNum());
        out.setName(in.getName());
        out.setFirstSurname(in.getFirstSurname());
        out.setSecondSurname(in.getSecondSurname());
        out.setDni(in.getDni());
        out.setPhone(in.getPhone());
        out.setEmail(in.getEmail());
        out.setSector(in.getSector());
        out.setPriceZone(in.getPriceZone());
        out.setOwner(in.isOwner());
        out.setSeatId(in.getSeatId());
        out.setOriginalLocator(in.getOriginalLocator());
        out.setLanguage(in.getLanguage());
        out.setProcessed(in.isProcessed());
        out.setErrorCode(in.getErrorCode());
        out.setErrorDescription(in.getErrorDescription());
        out.setOrderId(in.getOrderId());
        out.setTraceId(in.getTraceId());
        out.setExtraField(in.getExtraField());
        return out;
    }

}
