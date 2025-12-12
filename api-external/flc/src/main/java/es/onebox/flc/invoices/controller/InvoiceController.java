package es.onebox.flc.invoices.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.flc.invoices.dto.InvoiceInfoDTO;
import es.onebox.flc.invoices.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping(ApiConfig.FLCApiConfig.BASE_URL + "/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping()
    public List<InvoiceInfoDTO> getInvoices(
            @RequestParam(required = false, value = "invoice_generation_from") ZonedDateTime invoiceGenerationFrom,
            @RequestParam(required = false, value = "invoice_generation_to") ZonedDateTime invoiceGenerationTo,
            @RequestParam(required = false, value = "channel_ids") List<Long> channelIds,
            @RequestParam(required = false, value = "invoice_code") String invoiceCode,
            @RequestParam(required = false, value = "page") Integer page,
            @RequestParam(required = false, value = "page_size") Integer pageSize) {
        return invoiceService.getInvoices(invoiceGenerationFrom, invoiceGenerationTo, channelIds, invoiceCode, page, pageSize);
    }
}
