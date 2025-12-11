package es.onebox.mgmt.vouchers.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.CreateVoucherGroup;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroup;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupType;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroups;
import es.onebox.mgmt.datasources.ms.channel.repositories.VouchersRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.vouchers.converter.VouchersGroupConverter;
import es.onebox.mgmt.vouchers.dto.ChannelsScopeDTO;
import es.onebox.mgmt.vouchers.dto.CreateVoucherGroupRequestDTO;
import es.onebox.mgmt.vouchers.dto.RelativeTimeDTO;
import es.onebox.mgmt.vouchers.dto.RelativeTimeTypeDTO;
import es.onebox.mgmt.vouchers.dto.UpdateVoucherGroupChannelsDTO;
import es.onebox.mgmt.vouchers.dto.UpdateVoucherGroupGiftCardDTO;
import es.onebox.mgmt.vouchers.dto.UpdateVoucherGroupRequestDTO;
import es.onebox.mgmt.vouchers.dto.VoucherGroupDTO;
import es.onebox.mgmt.vouchers.dto.VoucherGroupGiftCardDTO;
import es.onebox.mgmt.vouchers.dto.VoucherGroupSearchFilter;
import es.onebox.mgmt.vouchers.dto.VoucherGroupsDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.INVALID_VOUCHER_GROUP;

@Service
public class VoucherGroupsService {

    private final SecurityManager securityManager;
    private final VouchersRepository vouchersRepository;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public VoucherGroupsService(SecurityManager securityManager, VouchersRepository vouchersRepository,
                                EntitiesRepository entitiesRepository) {
        this.securityManager = securityManager;
        this.vouchersRepository = vouchersRepository;
        this.entitiesRepository = entitiesRepository;
    }

    public VoucherGroupsDTO searchVoucherGroups(VoucherGroupSearchFilter request) {
        securityManager.checkEntityAccessible(request);

        Operator operator = entitiesRepository.getCachedOperator(SecurityUtils.getUserOperatorId());


        VoucherGroupFilter filter = VouchersGroupConverter.convertFilter(request);

        if(BooleanUtils.isTrue(operator.getUseMultiCurrency()) && Objects.nonNull(request.getCurrencyCode())) {
            List<Currency> operatorCurrencies = operator.getCurrencies().getSelected();
            filter.setCurrencyId(CurrenciesUtils.getCurrencyId(operatorCurrencies,request.getCurrencyCode()));
        }

        VoucherGroups groups = vouchersRepository.searchVoucherGroups(SecurityUtils.getUserOperatorId(),
                filter, request.getSort());

        List<Long> entityIds = groups.getData().stream().map(VoucherGroup::getEntityId).toList();
        Map<Long, Entity> entityMap = entityIds.stream().distinct()
                .map(entitiesRepository::getEntity).collect(Collectors.toMap(Entity::getId, Function.identity()));

        return VouchersGroupConverter.fromMsChannel(groups, entityMap, operator);
    }

    public VoucherGroupDTO getVoucherGroup(Long voucherGroupId) {
        VoucherGroup group = getAndCheckVoucherGroup(voucherGroupId);
        Entity entity = entitiesRepository.getEntity(group.getEntityId());
        Operator operator = entitiesRepository.getCachedOperator(SecurityUtils.getUserOperatorId());

        return VouchersGroupConverter.fromMsChannel(group, entity, operator);
    }

    public Long createVoucherGroup(CreateVoucherGroupRequestDTO request) {
        if (SecurityUtils.hasAnyRole(Roles.ROLE_OPR_MGR, Roles.ROLE_ENT_ADMIN)) {
            if (request.getEntityId() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                        "entity_id is mandatory for operator user", null);
            }
            securityManager.checkEntityAccessible(request.getEntityId());
        } else {
            request.setEntityId(SecurityUtils.getUserEntityId());
        }

        CreateVoucherGroup createVoucherGroup = VouchersGroupConverter.convertCreate(request);
        Operator operator = entitiesRepository.getCachedOperator(SecurityUtils.getUserOperatorId());

        if(BooleanUtils.isTrue(operator.getUseMultiCurrency())) {
            if(request.getCurrencyCode() == null){
                throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_MANDATORY);
            }
            List<Currency> operatorCurrencies = operator.getCurrencies().getSelected();
            createVoucherGroup.setCurrencyId(CurrenciesUtils.getCurrencyId(operatorCurrencies,request.getCurrencyCode()));
        }

        return vouchersRepository.createVoucherGroup(createVoucherGroup).getId();
    }

    public void updateVoucherGroup(Long voucherGroupId, UpdateVoucherGroupRequestDTO request) {
        validateUpdate(request);
        getAndCheckVoucherGroup(voucherGroupId);

        VoucherGroup voucherGroup = VouchersGroupConverter.toMsChannel(request);
        voucherGroup.setId(voucherGroupId);

        vouchersRepository.updateVoucherGroup(voucherGroup);
    }

    public void deleteVoucherGroup(Long voucherGroupId) {
        getAndCheckVoucherGroup(voucherGroupId);

        vouchersRepository.deleteVoucherGroup(voucherGroupId);
    }

    public VoucherGroup getAndCheckVoucherGroup(Long voucherGroupId) {
        VoucherGroup group = vouchersRepository.getVoucherGroup(voucherGroupId);
        if (VoucherGroupType.SEASON_TICKET.equals(group.getType()) || VoucherGroupType.LOYALTY_POINTS.equals(group.getType())) {
            throw new OneboxRestException(INVALID_VOUCHER_GROUP);
        }

        securityManager.checkEntityAccessible(group.getEntityId());
        return group;
    }

    public VoucherGroupGiftCardDTO getVoucherGroupGiftCardConfig(Long voucherGroupId) {
        VoucherGroup voucherGroup = getAndCheckVoucherGroup(voucherGroupId);
        if (!es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupType.GIFT_CARD.equals(voucherGroup.getType())) {
            throw OneboxRestException.builder(ApiMgmtChannelsErrorCode.VOUCHER_IS_NOT_GIFT_CARD).build();
        }
        return VouchersGroupConverter.fromMsChannel(vouchersRepository.getVoucherGroupGiftCard(voucherGroupId));
    }

    public void updateVoucherGroupGiftCardConfig(Long voucherGroupId, UpdateVoucherGroupGiftCardDTO voucherGroupGiftCardConfig) {
        VoucherGroup voucherGroup = getAndCheckVoucherGroup(voucherGroupId);
        if (!es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupType.GIFT_CARD.equals(voucherGroup.getType())) {
            throw OneboxRestException.builder(ApiMgmtChannelsErrorCode.VOUCHER_IS_NOT_GIFT_CARD).build();
        }
        vouchersRepository.updateVoucherGroupGiftCard(voucherGroupId, VouchersGroupConverter.toMsChannel(voucherGroupGiftCardConfig));
    }

    private void validateVoucherGroupChannels(UpdateVoucherGroupChannelsDTO channels) {
        if(channels == null) {
            return;
        }
        if (ChannelsScopeDTO.FILTERED.equals(channels.getScope()) && CollectionUtils.isEmpty(channels.getIds())) {
            throw OneboxRestException.builder(ApiMgmtChannelsErrorCode.CHANNEL_IDS_IS_REQUIRED).build();
        }
        if (ChannelsScopeDTO.ALL.equals(channels.getScope()) && CollectionUtils.isNotEmpty(channels.getIds())) {
            throw OneboxRestException.builder(ApiMgmtChannelsErrorCode.CHANNEL_IDS_MUST_BE_NULL).build();
        }
    }

    private void validateUpdate(UpdateVoucherGroupRequestDTO request) {
        validateVoucherGroupChannels(request.getChannels());

        RelativeTimeDTO expiration = request.getExpiration();
        if (expiration != null && RelativeTimeTypeDTO.FIXED.equals(expiration.getType()) && ZonedDateTime.now().isAfter(expiration.getFixedDate())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.VOUCHER_GROUP_EXPIRATION_DATE_AFTER_NOW);
        }
    }
}