package es.onebox.mgmt.products.converter;

import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductAttribute;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttribute;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttributes;
import es.onebox.mgmt.products.dto.CreateProductAttributeDTO;
import es.onebox.mgmt.products.dto.ProductAttributeDTO;
import es.onebox.mgmt.products.dto.ProductAttributesDTO;
import es.onebox.mgmt.products.dto.UpdateProductAttributeDTO;

public class ProductAttributeConverter {
    private ProductAttributeConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static CreateProductAttribute convert(CreateProductAttributeDTO createProductAttributeDTO) {
        if(createProductAttributeDTO == null) {
            return null;
        }

        CreateProductAttribute createProductAttribute = new CreateProductAttribute();

        createProductAttribute.setName(createProductAttributeDTO.getName());
        createProductAttribute.setPosition(createProductAttributeDTO.getPosition());

        return createProductAttribute;
    }

    public static ProductAttributeDTO toDto(ProductAttribute productAttribute) {
        if(productAttribute == null) {
            return null;
        }

        ProductAttributeDTO productAttributeDTO = new ProductAttributeDTO();
        productAttributeDTO.setAttributeId(productAttribute.getAttributeId());
        productAttributeDTO.setName(productAttribute.getName());
        productAttributeDTO.setPosition(productAttribute.getPosition());

        return productAttributeDTO;
    }

    public static ProductAttributesDTO toDto(ProductAttributes productAttributes) {
        ProductAttributesDTO productAttributesDTO = new ProductAttributesDTO();
        for (ProductAttribute productAttribute : productAttributes) {
            productAttributesDTO.add(toDto(productAttribute));
        }

        return productAttributesDTO;
    }

    public static ProductAttribute toEntity(UpdateProductAttributeDTO updateProductAttributeDTO) {
        if(updateProductAttributeDTO == null) {
            return null;
        }

        ProductAttribute updatedProductAttribute = new ProductAttribute();

        updatedProductAttribute.setName(updateProductAttributeDTO.getName());
        updatedProductAttribute.setPosition(updateProductAttributeDTO.getPosition());

        return updatedProductAttribute;
    }
}
