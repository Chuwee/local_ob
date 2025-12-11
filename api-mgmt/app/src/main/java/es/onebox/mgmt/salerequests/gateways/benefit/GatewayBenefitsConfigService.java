package es.onebox.mgmt.salerequests.gateways.benefit;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.payment.dto.benefits.BenefitType;
import es.onebox.mgmt.datasources.ms.payment.dto.benefits.GatewayBenefitConfiguration;
import es.onebox.mgmt.datasources.ms.payment.repositories.GatewayBenefitsConfigRepository;
import es.onebox.mgmt.exception.ApiMgmtCustomersErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.converter.GatewayBenefitsConfigConverter;
import es.onebox.mgmt.salerequests.dto.SaleRequestDetailDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.GatewayBenefitsConfigDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.GatewayBenefitsConfigRequest;
import es.onebox.mgmt.salerequests.service.SaleRequestService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GatewayBenefitsConfigService {

    private final SaleRequestService saleRequestService;
    private final GatewayBenefitsConfigRepository gatewayBenefitsConfigRepository;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public GatewayBenefitsConfigService(GatewayBenefitsConfigRepository gatewayBenefitsConfigRepository,
                                        SaleRequestService saleRequestService,
                                        EntitiesRepository entitiesRepository) {
        this.gatewayBenefitsConfigRepository = gatewayBenefitsConfigRepository;
        this.saleRequestService = saleRequestService;
        this.entitiesRepository = entitiesRepository;
    }

    public GatewayBenefitsConfigDTO getGatewayBenefitsConfig(Long saleRequestId, String gatewaySid, String confSid) {
        SaleRequestDetailDTO saleRequestDetailDTO = saleRequestService.getSaleRequestDetail(saleRequestId);
        Long channelId = saleRequestDetailDTO.getChannel().getId();
        Long eventId = saleRequestDetailDTO.getEvent().getId();

        GatewayBenefitConfiguration configuration =
                gatewayBenefitsConfigRepository.getGatewayBenefitConfiguration(gatewaySid, confSid, channelId, eventId);

        if (configuration == null) {
            GatewayBenefitsConfigDTO emptyConfig = new GatewayBenefitsConfigDTO();
            emptyConfig.setBenefits(Collections.emptyList());
            return emptyConfig;
        }

        return GatewayBenefitsConfigConverter.convert(configuration);
    }

    public List<GatewayBenefitsConfigDTO> getListGatewayBenefitsConfigs(Long saleRequestId) {
        SaleRequestDetailDTO saleRequestDetailDTO = saleRequestService.getSaleRequestDetail(saleRequestId);
        Long channelId = saleRequestDetailDTO.getChannel().getId();
        Long eventId = saleRequestDetailDTO.getEvent().getId();

        List<GatewayBenefitConfiguration> configurations =
                gatewayBenefitsConfigRepository.getListGatewayBenefitConfigurations(channelId, eventId);
        return GatewayBenefitsConfigConverter.convert(configurations);
    }

    public GatewayBenefitsConfigDTO createGatewayBenefitsConfig(Long saleRequestId, String gatewaySid, String confSid,
                                                                GatewayBenefitsConfigRequest request) {
        SaleRequestDetailDTO saleRequestDetailDTO = saleRequestService.getSaleRequestDetail(saleRequestId);
        Long channelId = saleRequestDetailDTO.getChannel().getId();
        Long eventId = saleRequestDetailDTO.getEvent().getId();

        validateAllowsBenefits(saleRequestDetailDTO);
        validateGatewayBenefitsConfig(request);
        GatewayBenefitConfiguration entity = GatewayBenefitsConfigConverter.convert(channelId, eventId, gatewaySid, confSid, request);
        GatewayBenefitConfiguration created = gatewayBenefitsConfigRepository.createGatewayBenefitConfiguration(entity);
        return GatewayBenefitsConfigConverter.convert(created);
    }

    public GatewayBenefitsConfigDTO updateGatewayBenefitsConfig(Long saleRequestId, String gatewaySid, String confSid,
                                                                GatewayBenefitsConfigRequest request) {
        SaleRequestDetailDTO saleRequestDetailDTO = saleRequestService.getSaleRequestDetail(saleRequestId);
        Long channelId = saleRequestDetailDTO.getChannel().getId();
        Long eventId = saleRequestDetailDTO.getEvent().getId();

        validateAllowsBenefits(saleRequestDetailDTO);
        validateGatewayBenefitsConfig(request);
        GatewayBenefitConfiguration entity = GatewayBenefitsConfigConverter.convert(channelId, eventId, gatewaySid, confSid, request);
        GatewayBenefitConfiguration updated =
                gatewayBenefitsConfigRepository.updateGatewayBenefitConfiguration(gatewaySid, confSid, channelId, eventId, entity);
        return GatewayBenefitsConfigConverter.convert(updated);
    }

    public void deleteGatewayBenefitsConfig(Long saleRequestId, String gatewaySid, String confSid) {
        SaleRequestDetailDTO saleRequestDetailDTO = saleRequestService.getSaleRequestDetail(saleRequestId);
        Long channelId = saleRequestDetailDTO.getChannel().getId();
        Long eventId = saleRequestDetailDTO.getEvent().getId();

        gatewayBenefitsConfigRepository.deleteGatewayBenefitConfiguration(gatewaySid, confSid, channelId, eventId);
    }

    private void validateAllowsBenefits(SaleRequestDetailDTO saleRequestDetailDTO) {
        Long entityId = saleRequestDetailDTO.getChannel().getEntity().getId();
        Entity entity = entitiesRepository.getCachedEntity(entityId);

        if (entity == null || !Boolean.TRUE.equals(entity.getAllowGatewayBenefits())) {
            throw new OneboxRestException(ApiMgmtErrorCode.GATEWAY_BENEFITS_NOT_ALLOWED_BY_ENTITY);
        }
    }

    private void validateGatewayBenefitsConfig(GatewayBenefitsConfigRequest request) {
        if (CollectionUtils.isNotEmpty(request.getBenefits())) {
            request.getBenefits().forEach(benefit -> {
                if (CollectionUtils.isEmpty(benefit.getBinGroups()) && CollectionUtils.isEmpty(benefit.getBrandGroups())) {
                    throw new OneboxRestException(ApiMgmtCustomersErrorCode.BIN_OR_BRAND_GROUPS_ARE_MANDATORY);
                }

                boolean invalidPeriodInBinGroups = CollectionUtils.isNotEmpty(benefit.getBinGroups()) && benefit.getBinGroups().stream()
                        .anyMatch(bg -> Boolean.TRUE.equals(bg.getCustomValidPeriod()) && (bg.getValidityPeriod() == null ||
                                (bg.getValidityPeriod().getStartDate() == null || bg.getValidityPeriod().getEndDate() == null)));
                boolean invalidPeriodInBrandGroups = CollectionUtils.isNotEmpty(benefit.getBrandGroups()) && benefit.getBrandGroups().stream()
                        .anyMatch(bg -> Boolean.TRUE.equals(bg.getCustomValidPeriod()) && (bg.getValidityPeriod() == null ||
                                (bg.getValidityPeriod().getStartDate() == null || bg.getValidityPeriod().getEndDate() == null)));

                if (invalidPeriodInBinGroups || invalidPeriodInBrandGroups) {
                    throw new OneboxRestException(ApiMgmtCustomersErrorCode.VALID_PERIOD_IS_MANDATORY);
                }
            });
        }
    }
}
