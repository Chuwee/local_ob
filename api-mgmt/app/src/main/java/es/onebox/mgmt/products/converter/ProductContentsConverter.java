package es.onebox.mgmt.products.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.event.dto.ProductLiteral;
import es.onebox.mgmt.datasources.ms.event.dto.ProductLiterals;
import es.onebox.mgmt.datasources.ms.event.dto.ProductTicketLiteral;
import es.onebox.mgmt.datasources.ms.event.dto.ProductTicketLiterals;
import es.onebox.mgmt.datasources.ms.event.dto.ProductValueLiteral;
import es.onebox.mgmt.datasources.ms.event.dto.ProductValueLiterals;
import es.onebox.mgmt.products.dto.ProductLiteralDTO;
import es.onebox.mgmt.products.dto.ProductLiteralsDTO;
import es.onebox.mgmt.products.dto.ProductTicketLiteralDTO;
import es.onebox.mgmt.products.dto.ProductTicketLiteralsDTO;
import es.onebox.mgmt.products.dto.ProductValueLiteralDTO;
import es.onebox.mgmt.products.dto.ProductValueLiteralsDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProductContentsConverter {

    private ProductContentsConverter() {
    }

    public static ProductLiteralsDTO toDTO(ProductLiterals in) {
        if(CollectionUtils.isEmpty(in)) {
            return new ProductLiteralsDTO();
        }
        List<ProductLiteralDTO> out = in.stream().map(el -> new ProductLiteralDTO(el.getKey(), el.getValue(), el.getRichText(), el.getAuditable(), ConverterUtils.toLanguageTag(el.getLanguageCode())))
                .collect(Collectors.toList());
        out.sort(Comparator.comparing(ProductLiteralDTO::getKey));
        return new ProductLiteralsDTO(out);
    }

    public static ProductValueLiteralsDTO toBulkDTO(ProductValueLiterals in) {
        if(CollectionUtils.isEmpty(in)) {
            return new ProductValueLiteralsDTO();
        }
        List<ProductValueLiteralDTO> out = in.stream().map(el -> new ProductValueLiteralDTO(el.getValueId(), el.getKey(), el.getValue(), el.getRichText(), el.getAuditable(), ConverterUtils.toLanguageTag(el.getLanguageCode())))
                .collect(Collectors.toList());
        out.sort(Comparator.comparing(ProductValueLiteralDTO::getKey));
        return new ProductValueLiteralsDTO(out);
    }

    public static ProductLiterals toEntity(ProductLiteralsDTO in) {
        List<ProductLiteral> out = in.stream().map(el -> new ProductLiteral(el.getKey(), el.getValue(), ConverterUtils.toLocale(el.getLanguage())))
                .collect(Collectors.toList());
        return new ProductLiterals(out);
    }

    public static ProductValueLiterals toBulkEntity(ProductValueLiteralsDTO productValueLiteralDTOS) {
        List<ProductValueLiteral> out = productValueLiteralDTOS.stream().map(el -> new ProductValueLiteral(el.getValueId(), el.getKey(), el.getValue(), ConverterUtils.toLocale(el.getLanguage())))
                .collect(Collectors.toList());
        return new ProductValueLiterals(out);
    }

    public static ProductTicketLiteralsDTO toDTO(ProductTicketLiterals in) {
        List<ProductTicketLiteralDTO> out = in.stream()
                .map(el -> new ProductTicketLiteralDTO(el.getKey(), el.getValue(), el.getRichText(), el.getAuditable()))
                .sorted(Comparator.comparing(ProductTicketLiteralDTO::getKey)).collect(Collectors.toList());
        return new ProductTicketLiteralsDTO(out);
    }

    public static ProductTicketLiterals toDTO(ProductTicketLiteralsDTO in) {
        List<ProductTicketLiteral> out = in.stream().map(el -> new ProductTicketLiteral(el.getKey(), el.getValue()))
                .collect(Collectors.toList());
        return new ProductTicketLiterals(out);
    }

}
