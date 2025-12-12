package es.onebox.flc.invoices.service;

import es.onebox.common.datasources.ms.order.dto.invoice.InvoiceDTO;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.ms.order.request.InvoiceSearchParam;
import es.onebox.common.datasources.ms.order.request.Pagination;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneRequiredParameterException;
import es.onebox.flc.invoices.converter.InvoiceConverter;
import es.onebox.flc.invoices.dto.InvoiceInfoDTO;
import es.onebox.flc.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private MsOrderRepository msOrderRepository;

    public List<InvoiceInfoDTO> getInvoices(ZonedDateTime invoiceGenerationFrom,
                                            ZonedDateTime invoiceGenerationTo, List<Long> channelIds, String invoiceCode, Integer page, Integer pageSize) {

        List<Object> params = new ArrayList<>(Arrays.asList(invoiceGenerationFrom, invoiceGenerationTo, channelIds));
        if (!params.stream().anyMatch(o -> o != null)) {
            throw new OneRequiredParameterException("Bad Parameters, you need at least one parameter", params);
        } else if (invoiceGenerationFrom != null && invoiceGenerationTo != null && invoiceGenerationFrom.isAfter(invoiceGenerationTo)) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.START_DATE_AFTER_END_DATE);
        } else {
            InvoiceSearchParam param = new InvoiceSearchParam();
            if (invoiceGenerationFrom != null) {
                param.setInvoiceGenerationDateFrom(invoiceGenerationFrom);
            }
            if (invoiceGenerationTo != null) {
                param.setInvoiceGenerationDateTo(invoiceGenerationTo);
            }
            if (channelIds != null) {
                param.setChannelIds(channelIds);
            }
            if (invoiceCode != null) {
                param.setInvoiceCode(invoiceCode);
            }

            param.setEntityId(Long.valueOf((Integer) AuthenticationUtils.getAttribute("entityId")));
            param.setPagination(new Pagination(page, pageSize));

            List<InvoiceDTO> invoices = msOrderRepository.searchInvoices(param);

            if(invoices != null && !invoices.isEmpty()) {
                return InvoiceConverter.convert(invoices);
            } else {
                throw ExceptionBuilder.build(ApiExternalErrorCode.NO_CONTENT);
            }
        }
    }
}
