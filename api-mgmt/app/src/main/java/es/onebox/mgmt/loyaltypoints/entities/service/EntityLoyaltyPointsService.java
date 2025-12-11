package es.onebox.mgmt.loyaltypoints.entities.service;

import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroups;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupType;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroup;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.ResetVoucherRequest;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.channel.repositories.VouchersRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.LoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateLoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.loyaltypoints.entities.converter.LoyaltyPointsConverter;
import es.onebox.mgmt.loyaltypoints.entities.dto.LoyaltyPointsConfigDTO;
import es.onebox.mgmt.loyaltypoints.entities.dto.UpdateLoyaltyPointsConfigDTO;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collections;

@Service
public class EntityLoyaltyPointsService {

    private final EntitiesRepository entitiesRepository;
    private final SecurityManager securityManager;
    private final ChannelsRepository channelsRepository;
    private final VouchersRepository vouchersRepository;

    @Autowired
    public EntityLoyaltyPointsService(EntitiesRepository entitiesRepository, SecurityManager securityManager, ChannelsRepository channelsRepository,
                                      VouchersRepository vouchersRepository) {
        this.entitiesRepository = entitiesRepository;
        this.securityManager = securityManager;
        this.channelsRepository = channelsRepository;
        this.vouchersRepository = vouchersRepository;

    }

    public LoyaltyPointsConfigDTO getLoyaltyPoints(Long entityId) {
        securityManager.checkEntityAccessible(entityId);
        LoyaltyPointsConfig loyaltyPoints = entitiesRepository.getLoyaltyPoints(entityId);
        return LoyaltyPointsConverter.toDTO(loyaltyPoints);
    }

    public void updateLoyaltyPoints(Long entityId, UpdateLoyaltyPointsConfigDTO updateLoyaltyPointsDTO) {
        securityManager.checkEntityAccessible(entityId);
        entitiesRepository.updateLoyaltyPoints(entityId, LoyaltyPointsConverter.toMs(updateLoyaltyPointsDTO));
    }

    public void resetLoyaltyPoints(Long entityId) {
        securityManager.checkEntityAccessible(entityId);
        UpdateLoyaltyPointsConfig updateLoyaltyPointsConfig = new UpdateLoyaltyPointsConfig();
        updateLoyaltyPointsConfig.setLastReset(ZonedDateTime.now());
        entitiesRepository.updateLoyaltyPoints(entityId, updateLoyaltyPointsConfig);
        Long voucherGroupId = findLoyaltyPointsVoucherGroup(entityId);
        if (voucherGroupId == null) {
            return;
        }
        ResetVoucherRequest requestBody = new ResetVoucherRequest();
        requestBody.setUserId(SecurityUtils.getUserId());
        channelsRepository.resetVoucher(voucherGroupId, requestBody);

    }
    private Long findLoyaltyPointsVoucherGroup(Long entityId) {
        VoucherGroupFilter filter = new VoucherGroupFilter();
        filter.setEntityId(entityId);
        filter.setTypes(Collections.singletonList(VoucherGroupType.LOYALTY_POINTS));
        VoucherGroups voucherGroups = vouchersRepository.searchVoucherGroups(SecurityUtils.getUserOperatorId(), filter, null);

        return voucherGroups.getData().stream()
                .map(VoucherGroup::getId)
                .findFirst()
                .orElse(null);
    }
}