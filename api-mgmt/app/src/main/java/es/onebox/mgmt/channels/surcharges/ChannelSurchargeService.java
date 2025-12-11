package es.onebox.mgmt.channels.surcharges;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.mgmt.channels.ChannelsService;
import es.onebox.mgmt.channels.dto.ChannelDetailDTO;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.RangeDTO;
import es.onebox.mgmt.common.surcharges.converter.SurchargeConverter;
import es.onebox.mgmt.common.surcharges.dto.SurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeListDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.common.dto.Surcharge;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.channel.repositories.SurchargeRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChannelSurchargeService {

    private final SurchargeRepository surchargeRepository;
    private final ChannelsService channelsService;
    private final ChannelsRepository channelsRepository;
    private final SecurityManager securityManager;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public ChannelSurchargeService(SurchargeRepository surchargeRepository,ChannelsService channelsService,
                                   ChannelsRepository channelsRepository, SecurityManager securityManager,
                                   MasterdataService masterdataService, EntitiesRepository entitiesRepository){
        this.surchargeRepository = surchargeRepository;
        this.channelsService = channelsService;
        this.channelsRepository = channelsRepository;
        this.securityManager = securityManager;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;
    }

    public void setSurcharge(Long channelId, SurchargeListDTO surchargeListDTO) {

        if (surchargeListDTO.stream().anyMatch(surchargeDTO -> surchargeDTO.getType() == null)) {
            throw new OneboxRestException(ApiMgmtErrorCode.TYPE_MANDATORY);
        }

        if (surchargeListDTO.stream().anyMatch(this::hasFixedAndPercentageNull)) {
            throw new OneboxRestException(ApiMgmtErrorCode.FIXED_OR_PERCENTAGE_MANDATORY);
        }

        if (hasTypesDuplicated(surchargeListDTO)) {
            throw new OneboxRestException(ApiMgmtErrorCode.SURCHARGE_TYPE_DUPLICATED);
        }

        ChannelDetailDTO channelDetailDTO = channelsService.getChannel(channelId);

        if (channelDetailDTO.getType().equals(ChannelSubtype.EXTERNAL)) {
            throw new OneboxRestException(ApiMgmtErrorCode.SURCHARGE_TYPE_NOT_SUPPORTED, "External channels has not surcharges.", null);
        }

        if (surchargeListDTO.stream().anyMatch(this::isInvitation) && !isBoxOffice(channelDetailDTO)) {
            throw new OneboxRestException(ApiMgmtErrorCode.SURCHARGE_TYPE_NOT_SUPPORTED, "Invitation surcharges can only be applied to boxoffice channels.", null);
        }
        Operator operator = entitiesRepository.getCachedOperator(channelDetailDTO.getEntity().getId());

        Set<String> surchargeCurrencies = surchargeListDTO.stream().map(SurchargeDTO::getRanges)
                .flatMap(Collection::stream).map(RangeDTO::getCurrency).collect(Collectors.toSet());
        if(Objects.isNull(operator.getCurrencies()) && surchargeCurrencies.size()>1) {
            throw new OneboxRestException(ApiMgmtErrorCode.MULTI_CURRENCY_NOT_ALLOWED);
        }

        if(Objects.nonNull(operator.getCurrencies()) && surchargeCurrencies.stream().anyMatch(currency -> currency!=null
                && !channelDetailDTO.getCurrencies().stream().map(CodeNameDTO::getCode).collect(Collectors.toSet()).contains(currency))) {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_ALLOWED);
        }

        List<Surcharge> requests = surchargeListDTO.stream()
                .map(surcharge -> SurchargeConverter.fromDTO(surcharge, masterdataService.getCurrencies(),
                        CurrenciesUtils.getDefaultCurrency(operator)))
                .collect(Collectors.toList());

        surchargeRepository.setSurcharge(channelId, requests);
    }

    private boolean hasTypesDuplicated(SurchargeListDTO surchargeListDTO) {
        return surchargeListDTO.stream()
                .anyMatch(surchargeDTO ->
                        surchargeListDTO.stream()
                                .filter(singleSurcharge -> singleSurcharge.getType().equals(surchargeDTO.getType()))
                                .count() > 1);
    }

    private boolean hasFixedAndPercentageNull(SurchargeDTO surchargeDTO) {
        return surchargeDTO.getRanges().stream()
                .anyMatch(rangeDTO -> rangeDTO.getValues().getFixed() == null && rangeDTO.getValues().getPercentage() == null);
    }

    public List<SurchargeDTO> getSurcharges(Long channelId, List<SurchargeTypeDTO> types) {

        ChannelResponse channelResponse = channelsRepository.getChannel(channelId);

        if (channelResponse == null || channelResponse.getStatus() == ChannelStatus.DELETED) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
        }

        Operator operator = entitiesRepository.getCachedOperator(channelResponse.getEntityId());

        securityManager.checkEntityAccessible(channelResponse.getEntityId());

        List<Surcharge> channelRanges = channelsRepository.getChannelRanges(channelId, types);
        Currency defaultCurrency = CurrenciesUtils.getDefaultCurrency(operator);
        if(Objects.isNull(operator.getCurrencies())) {
            channelRanges.forEach(cr -> cr.getRanges().removeIf(range -> (range.getCurrencyId() != null && !defaultCurrency.getId().equals(range.getCurrencyId())
                    || CurrenciesUtils.hasDefaultCurrencyRange(range, defaultCurrency.getId(), cr.getRanges()))));
        } else {
            channelRanges.forEach(cr -> cr.getRanges().removeIf(range -> CurrenciesUtils.hasDefaultCurrencyRange(range, defaultCurrency.getId(), cr.getRanges())));
        }
        channelRanges = channelRanges.stream().filter(cr -> !cr.getRanges().isEmpty()).collect(Collectors.toList());
        return SurchargeConverter.toSurchargeDTO(channelRanges, masterdataService.getCurrencies(), defaultCurrency);
    }

    private boolean isBoxOffice(ChannelDetailDTO channelDetailDTO) {
        return channelDetailDTO.getType().equals(ChannelSubtype.BOX_OFFICE);
    }

    private boolean isInvitation(SurchargeDTO surchargeDTO) {
        return surchargeDTO.getType().equals(SurchargeTypeDTO.INVITATION);
    }
}
