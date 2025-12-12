package es.onebox.flc.invoices.converter;

import es.onebox.common.datasources.ms.order.dto.invoice.InvoiceDTO;
import es.onebox.common.datasources.ms.order.dto.invoice.InvoiceEventDataDTO;
import es.onebox.common.datasources.ms.order.dto.invoice.InvoiceSessionDataDTO;
import es.onebox.common.datasources.ms.order.dto.invoice.InvoiceTaxDataDTO;
import es.onebox.flc.invoices.dto.InvoiceEventDTO;
import es.onebox.flc.invoices.dto.InvoiceInfoDTO;
import es.onebox.flc.invoices.dto.InvoiceSessionDTO;
import es.onebox.flc.invoices.dto.InvoiceTaxDTO;

import java.util.List;
import java.util.stream.Collectors;

public class InvoiceConverter {
    public static List<InvoiceInfoDTO> convert(List<InvoiceDTO> invoices) {
        return invoices.stream()
                .map(invoiceDTO -> convert(invoiceDTO))
                .collect(Collectors.toList());
    }

    private static InvoiceInfoDTO convert(InvoiceDTO invoiceDTO) {
        InvoiceInfoDTO invoiceInfoDTO = new InvoiceInfoDTO();

        invoiceInfoDTO.setInvoiceDate(invoiceDTO.getOrderInvoice().getDate());
        invoiceInfoDTO.setInvoiceGenerationDate(invoiceDTO.getOrderInvoice().getGenerationDate());
        invoiceInfoDTO.setInvoiceNumber(invoiceDTO.getOrderInvoice().getPromoterCode()
                + String.format("%05d", invoiceDTO.getOrderInvoice().getNumber()));
        invoiceInfoDTO.setInvoiceOrderCode(invoiceDTO.getOrder().getOrderCode());

        invoiceInfoDTO.setEntityLogo(invoiceDTO.getEntity().getImagePath());
        invoiceInfoDTO.setEntityAddress(invoiceDTO.getEntity().getAddress());
        invoiceInfoDTO.setEntityAddressCity(invoiceDTO.getEntity().getCity());
        invoiceInfoDTO.setEntityAddressZipCode(invoiceDTO.getEntity().getPostalCode());
        invoiceInfoDTO.setEntityDocument(invoiceDTO.getEntity().getDocumentId());
        invoiceInfoDTO.setEntityName(invoiceDTO.getEntity().getName());

        invoiceInfoDTO.setClientDocument(invoiceDTO.getOrderInvoice().getClientDocument());
        invoiceInfoDTO.setClientFullName(invoiceDTO.getOrderInvoice().getClientFullName());
        invoiceInfoDTO.setClientAddress(invoiceDTO.getOrderInvoice().getClientAddress());
        invoiceInfoDTO.setClientAddressCity(invoiceDTO.getOrderInvoice().getClientCity());
        invoiceInfoDTO.setClientAddressZipCode(invoiceDTO.getOrderInvoice().getClientZipCode());

        invoiceInfoDTO.setObservations(invoiceDTO.getOrderInvoice().getObservations());

        invoiceInfoDTO.getInvoiceTaxes().addAll(
                invoiceDTO.getInvoiceTaxesData().stream()
                        .map(invoiceTaxDataDTO -> convert(invoiceTaxDataDTO))
                        .collect(Collectors.toList()));

        invoiceInfoDTO.getInvoiceEventsData().addAll(
                invoiceDTO.getInvoiceEventsData().stream()
                        .map(invoiceEventDataDTO -> convert(invoiceEventDataDTO))
                        .collect(Collectors.toList()));

        return invoiceInfoDTO;
    }

    private static InvoiceTaxDTO convert(InvoiceTaxDataDTO invoiceTaxDataDTO) {
        InvoiceTaxDTO invoiceTaxDTO = new InvoiceTaxDTO();
        invoiceTaxDTO.setBase(invoiceTaxDataDTO.getTaxBase());
        invoiceTaxDTO.setPercentage(invoiceTaxDataDTO.getVat());
        invoiceTaxDTO.setTotal(invoiceTaxDataDTO.getTotal());
        invoiceTaxDTO.setValue(invoiceTaxDataDTO.getTotalVAT());
        return invoiceTaxDTO;
    }

    private static InvoiceEventDTO convert(InvoiceEventDataDTO invoiceEventDataDTO) {
        InvoiceEventDTO invoiceEventDTO = new InvoiceEventDTO();
        invoiceEventDTO.setEventId(invoiceEventDataDTO.getEventId());
        invoiceEventDTO.setEventName(invoiceEventDataDTO.getEventName());
        invoiceEventDTO.setGroupId(invoiceEventDataDTO.getGroupId());
        invoiceEventDTO.setGroupName(invoiceEventDataDTO.getGroupName());
        invoiceEventDTO.getInvoiceSessionsData().addAll(
                invoiceEventDataDTO.getInvoiceSessionsData().stream()
                        .map(invoiceSessionDataDTO -> convert(invoiceSessionDataDTO))
                        .collect(Collectors.toList())
        );

        return invoiceEventDTO;
    }

    private static InvoiceSessionDTO convert(InvoiceSessionDataDTO invoiceSessionDataDTO) {
        InvoiceSessionDTO invoiceSessionDTO = new InvoiceSessionDTO();
        invoiceSessionDTO.setDate(invoiceSessionDataDTO.getDate());
        invoiceSessionDTO.setAmount(invoiceSessionDataDTO.getAmount());
        invoiceSessionDTO.setPrice(invoiceSessionDataDTO.getPrice());
        invoiceSessionDTO.setTax(invoiceSessionDataDTO.getTax());
        invoiceSessionDTO.setName(invoiceSessionDataDTO.getName());
        invoiceSessionDTO.setTotalPrice(invoiceSessionDataDTO.getTotalPrice());
        return invoiceSessionDTO;
    }
}
