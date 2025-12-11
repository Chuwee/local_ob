package es.onebox.mgmt.products.service;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProduct;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.products.converter.ProductTicketTemplatesConverter;
import es.onebox.mgmt.products.dto.ProductTicketTemplateDTO;
import es.onebox.mgmt.products.enums.ProductTicketTemplateType;
import es.onebox.mgmt.tickettemplates.TicketTemplatesService;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormatPath;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductTicketTemplatesService {

    private final ProductsRepository productsRepository;
    private final ValidationService validationService;

    @Autowired
    public ProductTicketTemplatesService(ProductsRepository productsRepository, ValidationService validationService) {
        this.productsRepository = productsRepository;
        this.validationService = validationService;
    }

    public List<ProductTicketTemplateDTO> getProductTicketTemplates(Long productId) {
        Product product = validationService.getAndCheckProduct(productId);
        return ProductTicketTemplatesConverter.convert(product.getTicketTemplateId());
    }

    public void saveProductTicketTemplate(Long productId, ProductTicketTemplateType type, TicketTemplateFormatPath templateFormat,
                                          IdDTO templateId) {
        validationService.getAndCheckProduct(productId);
        if (templateId != null && templateId.getId() != null) {
            UpdateProduct eventToUpdate = ProductTicketTemplatesConverter.toUpdateProduct(type, templateFormat, templateId);
            productsRepository.updateProduct(productId, eventToUpdate);
        }
    }
}
