package es.onebox.event.products.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.products.domain.ProductChannelRecord;
import es.onebox.event.products.dto.ChannelEntityDTO;
import es.onebox.event.products.dto.ProductChannelDTO;
import es.onebox.event.products.dto.ProductChannelInfoDTO;
import es.onebox.event.products.dto.ProductChannelsDTO;
import es.onebox.event.products.enums.ChannelSubtype;
import es.onebox.event.products.enums.SaleRequestsStatus;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ProductChannelsConverter {

    private ProductChannelsConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ProductChannelsDTO toEntity(List<ProductChannelRecord> productChannelRecordList, String s3Url) {
        ProductChannelsDTO productChannelsDTO = new ProductChannelsDTO();

        for (ProductChannelRecord productChannelRecord : productChannelRecordList) {
            productChannelsDTO.add(toEntity(productChannelRecord, s3Url));
        }

        return productChannelsDTO;
    }

    public static ProductChannelDTO toEntity(ProductChannelRecord productChannelRecord, String s3Url) {
        ProductChannelDTO productChannelDTO = new ProductChannelDTO();
        productChannelDTO.setProduct(new IdNameDTO(productChannelRecord.getProductid().longValue(), productChannelRecord.getProductName()));

        ProductChannelInfoDTO productChannelInfoDTO = new ProductChannelInfoDTO();

        if (productChannelRecord.getEntityId() != null) {
            ChannelEntityDTO channelEntityDTO = new ChannelEntityDTO();
            if (StringUtils.isNotEmpty(productChannelRecord.getEntityLogoPath())) {
                channelEntityDTO.setLogo(getLogoUrl(productChannelRecord, s3Url));
            }
            channelEntityDTO.setId(productChannelRecord.getEntityId() != null ? productChannelRecord.getEntityId().longValue() : null);
            channelEntityDTO.setName(productChannelRecord.getEntityName() != null ? productChannelRecord.getEntityName() : null);

            productChannelInfoDTO.setEntity(channelEntityDTO);
        }

        if (productChannelRecord.getChannelSubtypeId() != null) {
            ChannelSubtype channelSubtype = ChannelSubtype.getById(productChannelRecord.getChannelSubtypeId());
            productChannelInfoDTO.setType(channelSubtype);
        }
        productChannelInfoDTO.setId(productChannelRecord.getChannelid() != null ? productChannelRecord.getChannelid().longValue() : null);
        productChannelInfoDTO.setName(productChannelRecord.getChannelName() != null ? productChannelRecord.getChannelName() : null);

        productChannelDTO.setChannel(productChannelInfoDTO);
        productChannelDTO.setCheckoutSuggestionEnabled(ConverterUtils.isByteAsATrue(productChannelRecord.getCheckoutsuggestionenabled()));
        productChannelDTO.setStandaloneEnabled(ConverterUtils.isByteAsATrue(productChannelRecord.getStandaloneenabled()));

        //set sale status
        productChannelDTO.setSaleRequestsStatus(SaleRequestsStatus.PENDING_REQUEST);
        if (productChannelRecord.getProductSaleRequestsStatusId() != null) {
            productChannelDTO.setSaleRequestsStatus(SaleRequestsStatus.get(productChannelRecord.getProductSaleRequestsStatusId()));
        }

        return productChannelDTO;
    }

    private static String getLogoUrl(ProductChannelRecord productChannelRecord, String s3url) {
        return S3URLResolver.builder()
                .withUrl(s3url)
                .withType(S3URLResolver.S3ImageType.ENTITY_IMAGE)
                .withEntityId(productChannelRecord.getEntityId())
                .withOperatorId(productChannelRecord.getEntityOperatorId())
                .build()
                .buildPath(productChannelRecord.getEntityLogoPath());
    }

}
