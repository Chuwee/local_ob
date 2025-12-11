package es.onebox.mgmt.salerequests.validation;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.gateways.dto.BaseChannelGatewayDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfig;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.dto.SubscriptionListSalesRequestDTO;
import es.onebox.mgmt.salerequests.dto.UpdateSaleRequestDTO;
import es.onebox.mgmt.salerequests.enums.SaleRequestFilter;
import es.onebox.mgmt.salerequests.enums.SaleRequestsStatus;
import es.onebox.mgmt.salerequests.gateways.dto.GatewayConfigUpdateRequestDTO;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.stream.Collectors;

import static es.onebox.core.security.Roles.ROLE_ENT_ADMIN;
import static es.onebox.core.security.Roles.ROLE_OPR_MGR;
import static java.util.Objects.isNull;

public class SaleRequestsValidations {

    private SaleRequestsValidations() { throw new UnsupportedOperationException("Cannot instantiate utilities class");}

    public static void validateFilter(String filterType) {
        validateFilterType(filterType);
        validateFilterWithRole(filterType);
    }

    public static void validateSaleRequestId(Long saleRequestId){
        if (isNull(saleRequestId) || saleRequestId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }
    }

    public static void validateUpdatableSaleRequestStatus(UpdateSaleRequestDTO updateSaleRequestDTO){
        if (isNull(updateSaleRequestDTO.getStatus())
                || (!SaleRequestsStatus.ACCEPTED.equals(updateSaleRequestDTO.getStatus()) && !SaleRequestsStatus.REJECTED.equals(updateSaleRequestDTO.getStatus()))) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }
    }

    public static void validateUpdatableSaleRequestSubscriptionList(SubscriptionListSalesRequestDTO subscriptionListSalesRequestDTO){
        if(Boolean.TRUE.equals(subscriptionListSalesRequestDTO.getEnable() && subscriptionListSalesRequestDTO.getId() == null)){
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }
    }

    private static void validateFilterType(String filterType) {
        if (Arrays.stream(SaleRequestFilter.values())
                .map(SaleRequestFilter::getFilterType)
                .noneMatch(item -> item.equals(filterType))) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.FILTER_NOT_FOUND, filterType);
        }
    }

    private static void validateFilterWithRole(String filterType) {
        if(!SecurityUtils.hasAnyRole(ROLE_OPR_MGR, ROLE_ENT_ADMIN)
                && SaleRequestFilter.CHANNELS_ENTITIES.getFilterType().equals(filterType)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.FORBIDDEN);
        }
    }

    public static MsSaleRequestDTO validateAnGetSaleRequestAndEntityAccess(Long saleRequestId,
                                                               LongFunction<MsSaleRequestDTO> getSaleRequestDetail,
                                                               LongConsumer checkEntityAccessible) {
        validateSaleRequestId(saleRequestId);
        MsSaleRequestDTO msSaleRequestDTO = getSaleRequestDetail.apply(saleRequestId);
        if (isNull(msSaleRequestDTO)) {
            throw new OneboxRestException(ApiMgmtErrorCode.SALE_REQUESTS_NOT_FOUND);
        }
        checkEntityAccessible.accept(msSaleRequestDTO.getChannel().getEntity().getId());
        return msSaleRequestDTO;
    }

    public static void validateGatewayConfigUpdateRequest(GatewayConfigUpdateRequestDTO request, List<ChannelGatewayConfig> channelGatewayConfigs ){
        if(Boolean.TRUE.equals(request.getCustom())){
            if(CollectionUtils.isEmpty(request.getChannelGateways())){
                throw new OneboxRestException(ApiMgmtErrorCode.SALE_REQUESTS_GATEWAY_CONFIG_MANDATORY);
            }
            List<BaseChannelGatewayDTO>defaultGateways =  request.getChannelGateways().stream().filter(g -> Boolean.TRUE.equals(g.getDefaultGateway())).collect(Collectors.toList());
            if(defaultGateways.size() < 1){
                throw new OneboxRestException(ApiMgmtErrorCode.SALE_REQUESTS_GATEWAY_CONFIG_DEFAULT_GATEWAY_MANDATORY);
            } else if(defaultGateways.size() > 1){
                throw new OneboxRestException(ApiMgmtErrorCode.SALE_REQUESTS_GATEWAY_CONFIG_DEFAULT_GATEWAY_LIMIT_EXCEEDED);
            }
            if(request.getChannelGateways().stream().noneMatch(g -> Boolean.TRUE.equals(g.getActive()))){
                throw new OneboxRestException(ApiMgmtErrorCode.SALE_REQUESTS_GATEWAY_CONFIG_ACTIVE_GATEWAY_MANDATORY);
            }
            if(Boolean.FALSE.equals(defaultGateways.stream().filter(g -> Boolean.TRUE.equals(g.getDefaultGateway())).findFirst().map(BaseChannelGatewayDTO::getActive).orElse(Boolean.FALSE))){
                throw new OneboxRestException(ApiMgmtErrorCode.SALE_REQUESTS_GATEWAY_CONFIG_DEFAULT_GATEWAY_MUST_BE_ACTIVE);
            }
            validateGatewayBelongsToChannel(request.getChannelGateways(), channelGatewayConfigs);
        } else {
            if(CollectionUtils.isNotEmpty(request.getChannelGateways())){
                throw new OneboxRestException(ApiMgmtErrorCode.SALE_REQUESTS_GATEWAY_CONFIG_NOT_ALLOWED);
            }
        }
    }

    private static void validateGatewayBelongsToChannel(List<BaseChannelGatewayDTO> requestGateways, List<ChannelGatewayConfig> channelGateways) {
        requestGateways.stream().forEach(requestGateway ->{
            if (channelGateways.stream().noneMatch(channelGateway -> channelGateway.getGatewaySid().equals(requestGateway.getGatewaySid())
                && channelGateway.getConfSid().equals(requestGateway.getConfigurationSid()))){
                throw new OneboxRestException(ApiMgmtErrorCode.SALE_REQUESTS_GATEWAY_NOT_FROM_CHANNEL);
            }
        });

    }
}
