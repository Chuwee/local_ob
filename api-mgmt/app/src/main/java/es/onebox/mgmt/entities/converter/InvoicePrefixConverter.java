package es.onebox.mgmt.entities.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateProducerInvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.InvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.Producer;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerInvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateProducerInvoicePrefix;
import es.onebox.mgmt.entities.dto.CreateProducerInvoicePrefixRequestDTO;
import es.onebox.mgmt.entities.dto.ProducerInvoicePrefixDTO;
import es.onebox.mgmt.entities.dto.ProducerInvoicePrefixesDTO;
import es.onebox.mgmt.entities.dto.UpdateProducerInvoicePrefixRequestDTO;
import org.apache.commons.lang3.BooleanUtils;

public class InvoicePrefixConverter {

    private InvoicePrefixConverter() {}

    public static ProducerInvoicePrefixesDTO fromMsEntity(ProducerInvoicePrefix source, Producer producer) {
        if (source == null) {
            return null;
        }
        ProducerInvoicePrefixesDTO producerInvoicePrefixesDTO = new ProducerInvoicePrefixesDTO();
        producerInvoicePrefixesDTO.setMetadata(source.getMetadata());
        producerInvoicePrefixesDTO.setData(source.getData().stream().map(InvoicePrefixConverter::convert)
                .peek(elem -> elem.setProducer(new IdNameDTO(producer.getId(), producer.getName()))).toList());
        return producerInvoicePrefixesDTO;
    }

    private static ProducerInvoicePrefixDTO convert(InvoicePrefix invoicePrefix) {
        ProducerInvoicePrefixDTO producerInvoicePrefixDTO = new ProducerInvoicePrefixDTO();
        producerInvoicePrefixDTO.setInvoicePrefixId(invoicePrefix.getId());
        producerInvoicePrefixDTO.setPrefix(invoicePrefix.getPrefix());
        producerInvoicePrefixDTO.setSuffix(invoicePrefix.getSuffix());
        producerInvoicePrefixDTO.setDefaultPrefix(BooleanUtils.isTrue(invoicePrefix.getDefaultPrefix()));
        return producerInvoicePrefixDTO;
    }

    public static CreateProducerInvoicePrefix toMsEntity(CreateProducerInvoicePrefixRequestDTO source) {
        CreateProducerInvoicePrefix target = new CreateProducerInvoicePrefix();
        target.setPrefix(source.getPrefix());
        return target;
    }


    public static UpdateProducerInvoicePrefix toMsEntity(UpdateProducerInvoicePrefixRequestDTO source) {
        UpdateProducerInvoicePrefix target = new UpdateProducerInvoicePrefix();
        target.setDefaultPrefix(source.getDefaultPrefix());
        return target;
    }
}
