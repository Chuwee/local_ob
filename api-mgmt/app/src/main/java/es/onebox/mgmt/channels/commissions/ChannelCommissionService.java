package es.onebox.mgmt.channels.commissions;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.commissions.converter.CommissionConverter;
import es.onebox.mgmt.channels.commissions.dto.CommissionDTO;
import es.onebox.mgmt.channels.commissions.dto.CommissionListDTO;
import es.onebox.mgmt.channels.commissions.dto.CommissionTypeDTO;
import es.onebox.mgmt.channels.dto.ChannelDetailDTO;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.RangeDTO;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.channel.commission.repositories.CommissionRepository;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelCommission;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChannelCommissionService {

    private final CommissionRepository commissionRepository;
    private final ChannelsHelper channelsHelper;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public ChannelCommissionService(CommissionRepository commissionRepository, ChannelsHelper channelsHelper,
                                    EntitiesRepository entitiesRepository, MasterdataService masterdataService) {
        this.commissionRepository = commissionRepository;
        this.channelsHelper = channelsHelper;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;
    }

    public List<CommissionDTO> getCommissions(Long channelId, List<CommissionTypeDTO> types) {

        ChannelDetailDTO channelDetailDTO = channelsHelper.getChannel(channelId, null);
        Operator operator = entitiesRepository.getCachedOperator(channelDetailDTO.getEntity().getId());

        List<ChannelCommission> channelRanges = commissionRepository.getChannelCommissions(channelId, types);

        Currency defaultCurrency = CurrenciesUtils.getDefaultCurrency(operator);
        if(Objects.isNull(operator.getCurrencies())) {
            channelRanges.forEach(cr -> cr.getRanges().removeIf(range -> (range.getCurrencyId()!= null && !defaultCurrency.getId().equals(range.getCurrencyId())
                    || CurrenciesUtils.hasDefaultCurrencyRange(range, defaultCurrency.getId(), cr.getRanges()))));
        } else {
            channelRanges.forEach(cr -> cr.getRanges().removeIf(range -> CurrenciesUtils.hasDefaultCurrencyRange(range, defaultCurrency.getId(), cr.getRanges())));
        }
        channelRanges = channelRanges.stream().filter(cr -> !cr.getRanges().isEmpty()).collect(Collectors.toList());
        return CommissionConverter.fromMsChannelsCommissionRangeResponse(channelRanges, masterdataService.getCurrencies(),
                defaultCurrency);
    }

    public void setCommissions(Long channelId, CommissionListDTO commissionListDTO) {

        if (commissionListDTO.stream().anyMatch(commissionDTO -> commissionDTO.getType() == null)) {
            throw new OneboxRestException(ApiMgmtErrorCode.TYPE_MANDATORY);
        }

        if (commissionListDTO.stream().anyMatch(this::hasFixedAndPercentageNull)) {
            throw new OneboxRestException(ApiMgmtErrorCode.FIXED_OR_PERCENTAGE_MANDATORY);
        }

        if (hasTypesDuplicated(commissionListDTO)) {
            throw new OneboxRestException(ApiMgmtErrorCode.COMMISSION_TYPE_DUPLICATED);
        }

        ChannelDetailDTO channelDetailDTO = channelsHelper.getChannel(channelId, null);

        if (channelDetailDTO.getType().equals(ChannelSubtype.EXTERNAL)) {
            throw new OneboxRestException(ApiMgmtErrorCode.COMMISSION_TYPE_NOT_SUPPORTED,
                    "External channels has not commissions.", null);
        }

        Operator operator = entitiesRepository.getCachedOperator(channelDetailDTO.getEntity().getId());

        //TODO Remove after multicurrency migration
        Set<String> commissionCurrencies = commissionListDTO.stream().map(CommissionDTO::getRanges)
                .flatMap(Collection::stream).map(RangeDTO::getCurrency).collect(Collectors.toSet());
        if(Objects.isNull(operator.getCurrencies()) && commissionCurrencies.size()>1) {
            throw new OneboxRestException(ApiMgmtErrorCode.MULTI_CURRENCY_NOT_ALLOWED);
        }

        if(Objects.nonNull(operator.getCurrencies()) && commissionCurrencies.stream().anyMatch(currency -> currency!=null
                && !channelDetailDTO.getCurrencies().stream().map(CodeNameDTO::getCode).collect(Collectors.toSet()).contains(currency))) {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_ALLOWED);
        }

        List<ChannelCommission> requests = commissionListDTO.stream()
                .map(channelCommission -> CommissionConverter.fromDTO(channelCommission, masterdataService.getCurrencies(),
                        CurrenciesUtils.getDefaultCurrency(operator)))
                .collect(Collectors.toList());

        commissionRepository.setCommission(channelId, requests);
    }

    private boolean hasFixedAndPercentageNull(CommissionDTO commissionDTO) {
        return commissionDTO.getRanges().stream()
                .anyMatch(rangeDTO -> rangeDTO.getValues().getFixed() == null
                        && rangeDTO.getValues().getPercentage() == null);
    }

    private static boolean hasTypesDuplicated(CommissionListDTO commissionListDTO) {
        return commissionListDTO.stream()
                .anyMatch(commissionDTO ->
                        commissionListDTO.stream()
                                .filter(singleCommission -> singleCommission.getType().equals(commissionDTO.getType()))
                                .count() > 1);
    }
}
