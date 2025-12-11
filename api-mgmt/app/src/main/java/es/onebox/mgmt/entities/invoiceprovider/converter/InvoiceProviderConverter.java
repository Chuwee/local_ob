package es.onebox.mgmt.entities.invoiceprovider.converter;

import es.onebox.mgmt.datasources.ms.entity.dto.InvoiceProviderInfo;
import es.onebox.mgmt.datasources.ms.entity.dto.RequestInvoiceProvider;
import es.onebox.mgmt.entities.invoiceprovider.dto.InvoiceProviderInfoDTO;
import es.onebox.mgmt.entities.invoiceprovider.dto.RequestInvoiceProviderDTO;
import es.onebox.mgmt.entities.invoiceprovider.enums.InvoiceProvider;
import es.onebox.mgmt.entities.invoiceprovider.enums.RequestStatus;

public class InvoiceProviderConverter {

    private InvoiceProviderConverter() {}

    public static InvoiceProviderInfoDTO toDTO(InvoiceProviderInfo source) {
        InvoiceProviderInfoDTO target = new InvoiceProviderInfoDTO();
        if (source.getProvider() != null) {
            target.setProvider(InvoiceProvider.valueOf(source.getProvider()));
        }
        target.setStatus(RequestStatus.valueOf(source.getStatus()));
        return target;
    }

    public static RequestInvoiceProvider toMs(RequestInvoiceProviderDTO source) {
        RequestInvoiceProvider target = new RequestInvoiceProvider();
        target.setProvider(source.getProvider().name());
        return target;
    }
}
