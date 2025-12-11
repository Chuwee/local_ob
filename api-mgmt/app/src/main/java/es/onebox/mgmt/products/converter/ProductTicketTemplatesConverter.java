package es.onebox.mgmt.products.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProduct;
import es.onebox.mgmt.datasources.ms.event.dto.products.producttickettemplate.ProductTicketTemplateLiteral;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.dto.ProductTicketTemplateDTO;
import es.onebox.mgmt.products.enums.ProductTicketTemplateType;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateContentLiteralFilter;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateContentLiteralListDTO;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateLiteralElementFilter;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormat;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormatPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static es.onebox.mgmt.common.ConverterUtils.toLocale;

public class ProductTicketTemplatesConverter {

    private ProductTicketTemplatesConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<ProductTicketTemplateDTO> convert(Long ticketTemplateId) {
        if (ticketTemplateId == null) {
            return Collections.emptyList();
        }
        List<ProductTicketTemplateDTO> ticketTemplates = new ArrayList<>();
        addTicketTemplate(ticketTemplates, ticketTemplateId, ProductTicketTemplateType.SINGLE, TicketTemplateFormat.PDF);
        return ticketTemplates;
    }

    private static void addTicketTemplate(List<ProductTicketTemplateDTO> templates, Long ticketTemplateId, ProductTicketTemplateType type, TicketTemplateFormat format) {
        if (ticketTemplateId != null) {
            ProductTicketTemplateDTO template = new ProductTicketTemplateDTO();
            template.setId(ticketTemplateId);
            template.setType(type);
            template.setFormat(format);
            templates.add(template);
        }
    }

    public static UpdateProduct toUpdateProduct(ProductTicketTemplateType type, TicketTemplateFormatPath templateFormat, IdDTO templateId) {
        UpdateProduct productUpdate = new UpdateProduct();
        switch (type) {
            case SINGLE -> setTemplateId(templateId, templateFormat, productUpdate::setTicketTemplateId);
            default -> throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);

        }
        return productUpdate;
    }

    private static void setTemplateId(IdDTO templateId, TicketTemplateFormatPath format, Consumer<Long> setter) {
        if (TicketTemplateFormatPath.PDF.equals(format)) {
            setter.accept(templateId.getId());
        }
    }

    public static ProductTicketTemplateLiteralElementFilter fromProductTicketTemplateTextFilter(ProductTicketTemplateContentLiteralFilter request) {
        if (request == null) {
            return null;
        }
        ProductTicketTemplateLiteralElementFilter literalFilter = new ProductTicketTemplateLiteralElementFilter();
        if (request.getLanguage() != null) {
            literalFilter.setLanguage(toLocale(request.getLanguage()));
        }
        return literalFilter;
    }

    public static List<ProductTicketTemplateLiteral> toMsProductTicketLiteralList(ProductTicketTemplateContentLiteralListDTO literalListDTO) {
        return literalListDTO.stream().map(literalDTO -> {
            ProductTicketTemplateLiteral templateLiteral = new ProductTicketTemplateLiteral();
            templateLiteral.setLanguage(literalDTO.getLanguage());
            templateLiteral.setCode(literalDTO.getType());
            templateLiteral.setValue(literalDTO.getValue());
            return templateLiteral;
        }).collect(Collectors.toList());
    }
}
