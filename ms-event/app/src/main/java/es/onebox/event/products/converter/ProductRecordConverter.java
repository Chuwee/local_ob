package es.onebox.event.products.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dto.CategoryDTO;
import es.onebox.event.priceengine.simulation.record.ProductRecord;
import es.onebox.event.priceengine.simulation.record.ProductDetailRecord;
import es.onebox.event.products.dto.CreateProductDTO;
import es.onebox.event.products.dto.ProductDTO;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.ProductType;
import es.onebox.event.products.enums.TaxModeDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;

import java.util.ArrayList;
import java.util.List;

public class ProductRecordConverter {
    private ProductRecordConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static CpanelProductRecord toRecord(CreateProductDTO product) {
        CpanelProductRecord productRecord = new CpanelProductRecord();
        productRecord.setState(ProductState.INACTIVE.getId());
        productRecord.setType(product.getProductType().getId());
        productRecord.setName(product.getName());
        productRecord.setEntityid(product.getEntityId().intValue());
        productRecord.setProducerid(product.getProducerId().intValue());
        productRecord.setStocktype(product.getStockType().getId());
        productRecord.setIdcurrency(product.getCurrencyId().intValue());
        return productRecord;
    }

    public static ProductDTO fromEntity(ProductRecord productRecord) {
        if (productRecord == null) {
            return null;
        }
        ProductDTO target = new ProductDTO();

        target.setProductId(productRecord.getProductid().longValue());
        target.setEntity(new IdNameDTO(productRecord.getEntityid().longValue(), productRecord.getEntityName()));
        target.setProductState(ProductState.get(productRecord.getState()));
        target.setProducer(new IdNameDTO(productRecord.getProducerid().longValue(), productRecord.getProducerName()));
        target.setName(productRecord.getName());
        target.setProductType(ProductType.get(productRecord.getType()));
        target.setCreateDate(CommonUtils.timestampToZonedDateTime(productRecord.getCreateDate()));
        target.setUpdateDate(CommonUtils.timestampToZonedDateTime(productRecord.getUpdateDate()));
        target.setStockType(ProductStockType.get(productRecord.getStocktype()));
        target.setCurrencyId(productRecord.getIdcurrency().longValue());
        if (productRecord.getTaxid() != null) {
            target.setTax(new IdNameDTO(productRecord.getTaxid().longValue(), productRecord.getTaxName()));
        }
        if (productRecord.getSurchagetaxid() != null) {
            target.setSurchargeTax(new IdNameDTO(productRecord.getSurchagetaxid().longValue(), productRecord.getSurchargeTaxName()));
        }
        if (productRecord.getTickettemplateid() != null) {
            target.setTicketTemplateId(productRecord.getTickettemplateid().longValue());
        }
        target.setHideDeliveryPoint(ConverterUtils.isByteAsATrue(productRecord.getHidedeliverypoint()));
        target.setHideDeliveryDateTime(ConverterUtils.isByteAsATrue(productRecord.getHidedeliverydatetime()));
        target.setTaxMode(TaxModeDTO.fromId(productRecord.getTaxmode()));

        return target;
    }


    public static ProductDTO fromEntity(ProductDetailRecord productRecord) {
        ProductDTO target = fromEntity((ProductRecord) productRecord);
        target.setCategory(buildCategory(
                productRecord.getTaxonomyid(),
                productRecord.getCategoryDescription(),
                productRecord.getCategoryCode()));
        target.setCustomCategory(buildCategory(
                productRecord.getCustomtaxonomyid(),
                productRecord.getCustomCategoryDescription(),
                productRecord.getCustomCategoryRef()));
        return target;
    }

    private static CategoryDTO buildCategory(Integer id, String description, String code) {
        if (id == null) return null;

        CategoryDTO out = new CategoryDTO();
        out.setId(id);
        out.setDescription(description);
        out.setCode(code);
        return out;
    }

    public static List<ProductDTO> toDTOs(List<ProductRecord> productRecords) {
        List<ProductDTO> productDTOList = new ArrayList<>();
        productRecords.forEach(p -> productDTOList.add(fromEntity(p)));

        return productDTOList;
    }
}
