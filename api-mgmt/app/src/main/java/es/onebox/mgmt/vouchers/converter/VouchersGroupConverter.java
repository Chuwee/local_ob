package es.onebox.mgmt.vouchers.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.LimitlessValueDTO;
import es.onebox.mgmt.common.LimitlessValueType;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.ChannelsScope;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.CreateVoucherGroup;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherExpirationTimePeriod;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherExpirationType;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroup;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupGiftCard;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroups;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherValidationMethod;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.vouchers.dto.ChannelsScopeDTO;
import es.onebox.mgmt.vouchers.dto.CreateVoucherGroupRequestDTO;
import es.onebox.mgmt.vouchers.dto.PriceRangeDTO;
import es.onebox.mgmt.vouchers.dto.RelativeTimeDTO;
import es.onebox.mgmt.vouchers.dto.RelativeTimeTimePeriodDTO;
import es.onebox.mgmt.vouchers.dto.RelativeTimeTypeDTO;
import es.onebox.mgmt.vouchers.dto.UpdateVoucherGroupGiftCardDTO;
import es.onebox.mgmt.vouchers.dto.UpdateVoucherGroupRequestDTO;
import es.onebox.mgmt.vouchers.dto.VoucherGroupChannelsDTO;
import es.onebox.mgmt.vouchers.dto.VoucherGroupDTO;
import es.onebox.mgmt.vouchers.dto.VoucherGroupGiftCardDTO;
import es.onebox.mgmt.vouchers.dto.VoucherGroupSearchFilter;
import es.onebox.mgmt.vouchers.dto.VoucherGroupType;
import es.onebox.mgmt.vouchers.dto.VoucherGroupsDTO;
import es.onebox.mgmt.vouchers.dto.VoucherStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class VouchersGroupConverter {

    private VouchersGroupConverter(){}

    public static VoucherGroupsDTO fromMsChannel(VoucherGroups groups, Map<Long, Entity> entityMap, Operator operator) {
        VoucherGroupsDTO response = new VoucherGroupsDTO();
        response.setData(groups.getData().stream()
                .map(v -> fromMsChannel(v, entityMap.get(v.getEntityId()), operator))
                .collect(Collectors.toList()));
        response.setMetadata(groups.getMetadata());
        return response;
    }

    public static VoucherGroupDTO fromMsChannel(VoucherGroup group, Entity entity, Operator operator) {
        VoucherGroupDTO response = new VoucherGroupDTO();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setDescription(group.getDescription());
        response.setStatus(fromMsChannel(group.getStatus()));
        response.setType(fromMsChannel(group.getType()));
        response.setEntity(new IdNameDTO(entity.getId(), entity.getName()));
        response.setChannels(fromMsChannel(group.getChannelsScope(), group.getChannels()));
        response.setValidationMethod(convertValidationMethod(group.getValidationMethod()));
        response.setExpiration(fromMsChannel(group));
        response.setUsageLimit(new LimitlessValueDTO(group.getUsageLimit()));
        if(BooleanUtils.isTrue(operator.getUseMultiCurrency())) {
            List<Currency> currencies =  operator.getCurrencies().getSelected();
            if(operator.getCurrencies() != null && CollectionUtils.isNotEmpty(currencies)) {
                response.setCurrencyCode(CurrenciesUtils.getCurrencyCode(currencies, group.getCurrencyId()));
            } else if(operator.getCurrencies().getDefaultCurrency() != null) {
                response.setCurrencyCode(operator.getCurrencies().getDefaultCurrency());
            } else {
                response.setCurrencyCode(operator.getCurrency().getValue());
            }
        }
        return response;
    }

    public static RelativeTimeDTO fromMsChannel(VoucherGroup voucherGroup) {
        RelativeTimeDTO relativeTimeDTO = new RelativeTimeDTO();
        relativeTimeDTO.setType(RelativeTimeTypeDTO.valueOf(voucherGroup.getExpirationType().name()));
        relativeTimeDTO.setRelativeAmount(voucherGroup.getRelativeExpirationAmount());
        relativeTimeDTO.setFixedDate(voucherGroup.getFixedExpirationDate());
        RelativeTimeTimePeriodDTO timeTimePeriodDTO = voucherGroup.getExpirationTimePeriod() == null ?
                null : RelativeTimeTimePeriodDTO.valueOf(voucherGroup.getExpirationTimePeriod().name());
        relativeTimeDTO.setTimePeriod(timeTimePeriodDTO);
        return relativeTimeDTO;
    }

    public static VoucherGroup toMsChannel(UpdateVoucherGroupRequestDTO request) {
        VoucherGroup updateRequest = new VoucherGroup();
        updateRequest.setName(request.getName());
        updateRequest.setDescription(request.getDescription());
        updateRequest.setStatus(toMsChannel(request.getStatus()));
        updateRequest.setValidationMethod(convertValidationMethod(request.getValidationMethod()));

        if (request.getUsageLimit() != null) {
            updateRequest.setEnableUsageLimit(LimitlessValueType.FIXED.equals(request.getUsageLimit().getType()));
            updateRequest.setUsageLimit(request.getUsageLimit().getValue());
        }
        if (request.getChannels() != null) {
            updateRequest.setChannelsScope(ChannelsScope.valueOf(request.getChannels().getScope().name()));
            updateRequest.setChannelIds(request.getChannels().getIds());
        }
        if (request.getExpiration() != null) {
            updateRequest.setExpirationType(VoucherExpirationType.valueOf(request.getExpiration().getType().name()));
            updateRequest.setFixedExpirationDate(request.getExpiration().getFixedDate());
            updateRequest.setRelativeExpirationAmount(request.getExpiration().getRelativeAmount());
            VoucherExpirationTimePeriod period = request.getExpiration().getTimePeriod() == null ?
                    null : VoucherExpirationTimePeriod.valueOf(request.getExpiration().getTimePeriod().name());
            updateRequest.setExpirationTimePeriod(period);
        }
        return updateRequest;
    }

    public static VoucherGroupFilter convertFilter(VoucherGroupSearchFilter request) {
        VoucherGroupFilter filter = new VoucherGroupFilter();
        filter.setEntityId(request.getEntityId());
        filter.setEntityAdminId(request.getEntityAdminId());
        if (CollectionUtils.isNotEmpty(request.getStatus())) {
            filter.setStatus(request.getStatus().stream().map(VouchersGroupConverter::toMsChannel).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(request.getType())) {
            filter.setTypes(request.getType().stream().map(VouchersGroupConverter::toMsChannel).collect(Collectors.toList()));
        } else {
            filter.setTypes(Arrays.stream(VoucherGroupType.values()).map(VouchersGroupConverter::toMsChannel).collect(Collectors.toList()));
        }
        filter.setQ(request.getFreeSearch());
        filter.setOffset(request.getOffset());
        filter.setLimit(request.getLimit());
        return filter;
    }

    public static CreateVoucherGroup convertCreate(CreateVoucherGroupRequestDTO request) {
        CreateVoucherGroup createRequest = new CreateVoucherGroup();
        createRequest.setEntityId(request.getEntityId());
        createRequest.setName(request.getName());
        createRequest.setDescription(request.getDescription());
        createRequest.setType(toMsChannel(request.getType()));
        createRequest.setValidationMethod(convertValidationMethod(request.getValidationMethod()));
        return createRequest;
    }

    public static VoucherGroupGiftCardDTO fromMsChannel(VoucherGroupGiftCard from) {
        VoucherGroupGiftCardDTO target = new VoucherGroupGiftCardDTO();
        if (from.getMaxPrice() != null || from.getMinPrice() != null) {
            target.setPriceRange(new PriceRangeDTO(from.getMinPrice(), from.getMaxPrice()));
        }
        return target;
    }

    public static VoucherGroupGiftCard toMsChannel(UpdateVoucherGroupGiftCardDTO from) {
        VoucherGroupGiftCard target = new VoucherGroupGiftCard();
        if (from.getPriceRange() != null) {
            target.setMaxPrice(from.getPriceRange().getTo());
            target.setMinPrice(from.getPriceRange().getFrom());
        }
        return target;
    }

    private static VoucherGroupChannelsDTO fromMsChannel(ChannelsScope channelsScope, Set<IdNameDTO> channels) {
        if (channelsScope == null) {
            return null;
        }
        VoucherGroupChannelsDTO target = new VoucherGroupChannelsDTO();
        target.setScope(ChannelsScopeDTO.valueOf(channelsScope.name()));
        target.setItems(channels);
        return target;
    }

    private static es.onebox.mgmt.vouchers.dto.VoucherValidationMethod convertValidationMethod(VoucherValidationMethod validationMethod) {
        return validationMethod == null ? null : es.onebox.mgmt.vouchers.dto.VoucherValidationMethod.valueOf(validationMethod.name());
    }

    private static VoucherValidationMethod convertValidationMethod(es.onebox.mgmt.vouchers.dto.VoucherValidationMethod validationMethod) {
        return validationMethod == null ? null : VoucherValidationMethod.valueOf(validationMethod.name());
    }

    private static VoucherStatus fromMsChannel(es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherStatus in) {
        if (in == null) {
            return null;
        }
        return VoucherStatus.valueOf(in.name());
    }

    private static es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherStatus toMsChannel(VoucherStatus in) {
        if (in == null) {
            return null;
        }
        return es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherStatus.valueOf(in.name());
    }

    private static VoucherGroupType fromMsChannel(es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupType in) {
        if (in == null) {
            return null;
        }
        return VoucherGroupType.valueOf(in.name());
    }

    private static es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupType toMsChannel(VoucherGroupType in) {
        if (in == null) {
            return null;
        }
        return es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupType.valueOf(in.name());
    }


}
