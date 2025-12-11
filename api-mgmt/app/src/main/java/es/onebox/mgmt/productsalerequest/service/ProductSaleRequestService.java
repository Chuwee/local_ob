package es.onebox.mgmt.productsalerequest.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSaleRequestDetail;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSaleRequests;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductSaleRequestFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSaleRequest;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.dto.ProductSaleRequestsResponseDTO;
import es.onebox.mgmt.products.dto.ProductSaleRequestsDetailDTO;
import es.onebox.mgmt.products.dto.SearchProductSaleRequestFilterDTO;
import es.onebox.mgmt.products.enums.ProductSaleRequestsStatus;
import es.onebox.mgmt.productsalerequest.converter.ProductSaleRequestConverter;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.users.service.UsersService;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductSaleRequestService {

    private final ValidationService validationService;
    private final ChannelsRepository channelsRepository;
    private final MasterdataService masterdataService;
    private final SecurityManager securityManager;
    private final UsersService usersService;

    @Autowired
    public ProductSaleRequestService(ValidationService validationService,
                                     ChannelsRepository channelsRepository,
                                     SecurityManager securityManager, UsersService usersService,
                                     MasterdataService masterdataService) {
        this.validationService = validationService;
        this.channelsRepository = channelsRepository;
        this.securityManager = securityManager;
        this.usersService = usersService;
        this.masterdataService = masterdataService;
    }

    public ProductSaleRequestsDetailDTO getSaleRequestDetail(Long saleRequestId) {
        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();

        ProductSaleRequestDetail saleRequestDetail = validationService.getAndCheckSaleRequest(saleRequestId);

        Long channelId = saleRequestDetail.getChannel().getId();
        ChannelResponse channelResponse = channelsRepository.getChannel(channelId);
        ChannelLanguagesDTO languages = ChannelConverter.convertToLanguageDTO(channelResponse.getLanguages(), languagesByIds);

        return ProductSaleRequestConverter.toDTO(saleRequestDetail, languages, masterdataService.getCurrencies());
    }

    public void deleteSaleRequest(Long saleRequestId) {
        channelsRepository.deleteProductSaleRequest(saleRequestId);
    }

    public void updateSaleRequest(Long saleRequestId, ProductSaleRequestsStatus saleRequestsStatus) {
        ProductSaleRequestDetail saleRequestDetail = validationService.getAndCheckSaleRequest(saleRequestId);

        checkValidStatusTransition(saleRequestDetail.getStatus(), saleRequestsStatus);

        UpdateProductSaleRequest updateProductSaleRequest = new UpdateProductSaleRequest();
        updateProductSaleRequest.setStatus(saleRequestsStatus);
        updateProductSaleRequest.setUserId(usersService.getAuthUser().getId().intValue());

        if (Boolean.TRUE.equals(isValidProductStatusChange(saleRequestDetail.getStatus(), saleRequestsStatus))) {
            channelsRepository.updateProductSaleRequest(saleRequestId, updateProductSaleRequest);
        }
    }

    public ProductSaleRequestsResponseDTO searchProductSaleRequests(SearchProductSaleRequestFilterDTO searchProductSaleRequestFilterDTO) {
        securityManager.checkEntityAccessible(searchProductSaleRequestFilterDTO);
        List<Long> entityIds = new ArrayList<>();

        if (!SecurityUtils.isOperatorEntity()) {
            if (searchProductSaleRequestFilterDTO.getEntityId() != null) {
                securityManager.checkEntityAccessible(searchProductSaleRequestFilterDTO.getEntityId());
                entityIds.add(searchProductSaleRequestFilterDTO.getEntityId());
            } else {
                entityIds.add(SecurityUtils.getUserEntityId());
            }
        } else {
            entityIds.add(SecurityUtils.getUserEntityId());
        }
        SearchProductSaleRequestFilter filter = ProductSaleRequestConverter.convertFilter(searchProductSaleRequestFilterDTO, entityIds);


        ProductSaleRequests productSaleRequests = channelsRepository.searchProductSaleRequests(filter);

        return ProductSaleRequestConverter.toDtoList(productSaleRequests);
    }

    private void checkValidStatusTransition(ProductSaleRequestsStatus currentStatus, ProductSaleRequestsStatus newStatus) {
        if (ProductSaleRequestsStatus.PENDING.equals(newStatus) || newStatus.equals(currentStatus)) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_SALE_REQUEST_STATUS_TRANSITION);
        }
    }

    public Boolean isValidProductStatusChange(ProductSaleRequestsStatus currentStatus, ProductSaleRequestsStatus updateStatus) {
        if (ProductSaleRequestsStatus.PENDING.equals(currentStatus)) {
            return ProductSaleRequestsStatus.ACCEPTED.equals(updateStatus) || ProductSaleRequestsStatus.REJECTED.equals(updateStatus);
        }

        if (ProductSaleRequestsStatus.ACCEPTED.equals(currentStatus)) {
            return ProductSaleRequestsStatus.REJECTED.equals(updateStatus);
        }

        if (ProductSaleRequestsStatus.REJECTED.equals(currentStatus)) {
            return ProductSaleRequestsStatus.ACCEPTED.equals(updateStatus);
        }

        return false;
    }
}
