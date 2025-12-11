package es.onebox.mgmt.products.converter;

import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductAttributeValue;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttributeValue;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttributeValues;
import es.onebox.mgmt.products.dto.CreateProductAttributeValueDTO;
import es.onebox.mgmt.products.dto.ProductAttributeValueDTO;
import es.onebox.mgmt.products.dto.ProductAttributeValuesDTO;
import es.onebox.mgmt.products.dto.UpdateProductAttributeValueDTO;

import java.util.ArrayList;
import java.util.List;

public class ProductAttributeValueConverter {
    private ProductAttributeValueConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static CreateProductAttributeValue convert(CreateProductAttributeValueDTO createProductAttributeValueDTO) {
        if (createProductAttributeValueDTO == null) {
            return null;
        }

        CreateProductAttributeValue createProductAttributeValue = new CreateProductAttributeValue();

        createProductAttributeValue.setName(createProductAttributeValueDTO.getName());
        createProductAttributeValue.setPosition(createProductAttributeValueDTO.getPosition());

        return createProductAttributeValue;
    }

    public static ProductAttributeValueDTO toDto(ProductAttributeValue productAttributeValue) {
        if (productAttributeValue == null) {
            return null;
        }

        ProductAttributeValueDTO productAttributeValueDTO = new ProductAttributeValueDTO();
        productAttributeValueDTO.setAttributeId(productAttributeValue.getAttributeId());
        productAttributeValueDTO.setValueId(productAttributeValue.getValueId());
        productAttributeValueDTO.setName(productAttributeValue.getName());
        productAttributeValueDTO.setPosition(productAttributeValue.getPosition());

        return productAttributeValueDTO;
    }

    public static ProductAttributeValuesDTO toDtos(ProductAttributeValues productAttributeValues) {
        ProductAttributeValuesDTO productAttributeValuesDTO = new ProductAttributeValuesDTO();
        List<ProductAttributeValueDTO> productsDTOList = new ArrayList<>();
        if (productAttributeValues.getData() != null) {
            productAttributeValues.getData().forEach(p -> productsDTOList.add(toDto(p)));
        }
        productAttributeValuesDTO.setData(productsDTOList);
        productAttributeValuesDTO.setMetadata(productAttributeValues.getMetadata());
        return productAttributeValuesDTO;
    }

    public static ProductAttributeValue toEntity(UpdateProductAttributeValueDTO updateProductAttributeValueDTO) {
        if (updateProductAttributeValueDTO == null) {
            return null;
        }
        ProductAttributeValue updatedProductAttributeValue = new ProductAttributeValue();

        updatedProductAttributeValue.setName(updateProductAttributeValueDTO.getName());
        updatedProductAttributeValue.setPosition(updateProductAttributeValueDTO.getPosition());

        return updatedProductAttributeValue;
    }
}
