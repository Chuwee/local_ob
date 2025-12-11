package es.onebox.mgmt.productsalerequest.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.event.dto.products.ChannelSaleRequestDetail;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductDetail;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSaleRequest;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSaleRequestDetail;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSaleRequests;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductSaleRequestFilter;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.dto.ProductDetailDTO;
import es.onebox.mgmt.products.dto.ProductSaleRequestDTO;
import es.onebox.mgmt.products.dto.ProductSaleRequestsResponseDTO;
import es.onebox.mgmt.products.dto.ProductSaleRequestsDetailDTO;
import es.onebox.mgmt.products.dto.SearchProductSaleRequestFilterDTO;
import es.onebox.mgmt.salerequests.dto.BaseCategorySaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.CategoriesSaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.ChannelSaleRequestDetailDTO;
import es.onebox.mgmt.salerequests.dto.ContactPersonDTO;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static es.onebox.core.security.Roles.ROLE_ENT_ADMIN;
import static es.onebox.core.security.Roles.ROLE_OPR_MGR;

public class ProductSaleRequestConverter {

    private ProductSaleRequestConverter() {
    }

    public static ProductSaleRequestsDetailDTO toDTO(ProductSaleRequestDetail saleRequestDetail,
                                                     ChannelLanguagesDTO languages, List<Currency> currencies) {
        if (saleRequestDetail == null) {
            return null;
        }
        ProductSaleRequestsDetailDTO productSaleRequestsDetailDTO = new ProductSaleRequestsDetailDTO();

        productSaleRequestsDetailDTO.setId(saleRequestDetail.getId());
        productSaleRequestsDetailDTO.setDate(saleRequestDetail.getDate());
        productSaleRequestsDetailDTO.setStatus(saleRequestDetail.getStatus());
        productSaleRequestsDetailDTO.setChannel(convertChannel(saleRequestDetail.getChannel()));
        productSaleRequestsDetailDTO.setProduct(convertProduct(saleRequestDetail.getProduct(), currencies));
        productSaleRequestsDetailDTO.setLanguages(languages);

        return productSaleRequestsDetailDTO;
    }

    public static ProductSaleRequestsResponseDTO toDtoList(ProductSaleRequests productSaleRequests) {
        ProductSaleRequestsResponseDTO productSaleRequestsResponseDTO = new ProductSaleRequestsResponseDTO();
        List<ProductSaleRequestDTO> productSaleRequestsDTOS = new ArrayList<>();
        if (productSaleRequests.getData() != null) {
            productSaleRequests.getData().forEach(p -> productSaleRequestsDTOS.add(toProductSaleRequestDto(p)));
        }
        productSaleRequestsResponseDTO.setData(productSaleRequestsDTOS);
        productSaleRequestsResponseDTO.setMetadata(productSaleRequests.getMetadata());

        return productSaleRequestsResponseDTO;
    }

    public static ProductSaleRequestDTO toProductSaleRequestDto(ProductSaleRequest productSaleRequest) {
        ProductSaleRequestDTO productSaleRequestDTO = new ProductSaleRequestDTO();

        productSaleRequestDTO.setId(productSaleRequest.getId());
        productSaleRequestDTO.setStatus(productSaleRequest.getStatus());
        productSaleRequestDTO.setRequestDate(productSaleRequest.getRequestDate());
        productSaleRequestDTO.setChannel(productSaleRequest.getChannel());
        productSaleRequestDTO.setProduct(productSaleRequest.getProduct());
        productSaleRequestDTO.setProducer(productSaleRequest.getProducer());

        return productSaleRequestDTO;
    }

    public static SearchProductSaleRequestFilter convertFilter(SearchProductSaleRequestFilterDTO filter, List<Long> entityIds) {
        SearchProductSaleRequestFilter searchProductSaleRequestFilter = new SearchProductSaleRequestFilter();
        searchProductSaleRequestFilter.setOperatorId(SecurityUtils.getUserOperatorId());
        searchProductSaleRequestFilter.setRequestDate(filter.getRequestDate());
        searchProductSaleRequestFilter.setStatus(filter.getStatus());
        searchProductSaleRequestFilter.setQ(filter.getQ());
        searchProductSaleRequestFilter.setSort(filter.getSort());
        searchProductSaleRequestFilter.setChannelId(filter.getChannelId());
        searchProductSaleRequestFilter.setPromoterId(filter.getPromoterId());
        searchProductSaleRequestFilter.setEntityIds(entityIds);
        searchProductSaleRequestFilter.setOffset(filter.getOffset());
        searchProductSaleRequestFilter.setLimit(filter.getLimit());

        searchProductSaleRequestFilter.setProductEntityId(filter.getProductEntityId());

        if (SecurityUtils.hasAnyRole(ROLE_OPR_MGR)) {
            searchProductSaleRequestFilter.setChannelEntityId(filter.getChannelEntityId());
        } else if (SecurityUtils.hasAnyRole(ROLE_ENT_ADMIN)) {
            searchProductSaleRequestFilter.setChannelEntityId(filter.getChannelEntityId());
            searchProductSaleRequestFilter.setEntityAdminId(SecurityUtils.getUserEntityId());
        } else if (!SecurityUtils.hasAnyRole(ROLE_OPR_MGR, ROLE_ENT_ADMIN) && CollectionUtils.isEmpty(filter.getChannelEntityId())) {
            searchProductSaleRequestFilter.setChannelEntityId(Collections.singletonList(SecurityUtils.getUserEntityId()));
        } else {
            throw new AccessDeniedException("Can't access resources from other entities");
        }

        return searchProductSaleRequestFilter;
    }

    private static ChannelSaleRequestDetailDTO convertChannel(ChannelSaleRequestDetail channel) {
        if (channel == null) {
            return null;
        }

        ChannelSaleRequestDetailDTO dto = new ChannelSaleRequestDetailDTO();

        dto.setId(channel.getId());
        dto.setName(channel.getName());
        dto.setType(channel.getType());
        dto.setEntity(channel.getEntity());

        if (channel.getCategory() != null) {
            CategoriesSaleRequestDTO categoriesDTO = new CategoriesSaleRequestDTO();

            if (channel.getCategory().getParent() != null) {
                BaseCategorySaleRequestDTO parentDTO = new BaseCategorySaleRequestDTO();
                parentDTO.setId(channel.getCategory().getParent().getId());
                parentDTO.setCode(channel.getCategory().getParent().getCode());
                parentDTO.setDescription(channel.getCategory().getParent().getDescription());
                categoriesDTO.setParent(parentDTO);
            }

            if (channel.getCategory().getCustom() != null) {
                BaseCategorySaleRequestDTO customDTO = new BaseCategorySaleRequestDTO();
                customDTO.setId(channel.getCategory().getCustom().getId());
                customDTO.setCode(channel.getCategory().getCustom().getCode());
                customDTO.setDescription(channel.getCategory().getCustom().getDescription());
                categoriesDTO.setCustom(customDTO);
            }

            dto.setCategory(categoriesDTO);
        }

        return dto;
    }

    private static ProductDetailDTO convertProduct(ProductDetail product, List<Currency> currencies) {
        if (product == null) {
            return null;
        }

        ProductDetailDTO dto = new ProductDetailDTO();

        dto.setProductId(product.getProductId());
        dto.setProductName(product.getName());
        dto.setProductType(product.getProductType());
        if (product.getCurrencyId() != null) {
            dto.setCurrencyCode(getCurrencyCode(product.getCurrencyId(), currencies));
        }

        dto.setEntity(product.getEntity());

        if (product.getContactPerson() != null) {
            ContactPersonDTO contactDTO = new ContactPersonDTO();
            contactDTO.setName(product.getContactPerson().getName());
            contactDTO.setSurname(product.getContactPerson().getSurname());
            contactDTO.setEmail(product.getContactPerson().getEmail());
            contactDTO.setPhone(product.getContactPerson().getPhone());
            dto.setContactPerson(contactDTO);
        }

        return dto;
    }

    private static String getCurrencyCode(Long currencyId, List<Currency> currencies) {
        String currencyCode = currencies.stream()
                .filter(currency -> currency.getId().equals(currencyId))
                .map(Currency::getCode)
                .findFirst().orElse(null);
        if (currencyCode == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_FOUND);
        }
        return currencyCode;
    }
}
