package es.onebox.mgmt.entities;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.RangeDTO;
import es.onebox.mgmt.common.surcharges.converter.SurchargeConverter;
import es.onebox.mgmt.common.surcharges.dto.EntitySurchargesDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.common.dto.Surcharge;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EntitySurchargesService {

    private final EntitiesRepository entitiesRepository;
    private final SecurityManager securityManager;
    private final MasterdataService masterdataService;

    @Autowired
    public EntitySurchargesService(EntitiesRepository entitiesRepository, SecurityManager securityManager,
                                   MasterdataService masterdataService){
        this.entitiesRepository = entitiesRepository;
        this.securityManager = securityManager;
        this.masterdataService = masterdataService;
    }

    public EntitySurchargesDTO getSurcharges(Long entityId, List<SurchargeTypeDTO> types, String currencyCode) {
        if (!CommonUtils.isEmpty(types) && types.stream().anyMatch( type ->
                SurchargeTypeDTO.INVITATION.equals(type)  ||
                    SurchargeTypeDTO.PROMOTION.equals(type) ||
                        SurchargeTypeDTO.CHANGE_SEAT.equals(type))) {
            throw new OneboxRestException(ApiMgmtErrorCode.SURCHARGE_TYPE_NOT_ALLOWED_AT_ENTITY_LEVEL);
        }

        securityManager.checkEntityAccessible(entityId);
        Operator operator = entitiesRepository.getCachedOperator(entityId);

        if (currencyCode != null && BooleanUtils.isNotTrue(operator.getUseMultiCurrency())) {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_ALLOWED);
        }

        List<Surcharge> surcharges = entitiesRepository.getSurcharges(
                entityId,
                SurchargeConverter.toSurchargeTypes(types),
                CurrenciesUtils.getCurrencyIds(currencyCode, operator.getCurrencies())
        );

        Currency defaultCurrency = CurrenciesUtils.getDefaultCurrency(operator);
        if (Objects.isNull(operator.getCurrencies())) {
            surcharges.forEach( surcharge -> surcharge.getRanges().removeIf(
                range -> range.getCurrencyId() != null && !defaultCurrency.getId().equals(range.getCurrencyId()))
            );
        }

        return new EntitySurchargesDTO(SurchargeConverter.toSurchargeDTO(surcharges, masterdataService.getCurrencies(), defaultCurrency));
    }

    public void setSurcharges(Long entityId, EntitySurchargesDTO surcharges) {
        if (CommonUtils.isEmpty(surcharges)) {
            throw new OneboxRestException(ApiMgmtErrorCode.AT_LEAST_ONE_RANGE);
        }
        List<SurchargeTypeDTO> allowedTypes = Arrays.asList(SurchargeTypeDTO.GENERIC, SurchargeTypeDTO.SECONDARY_MARKET_PROMOTER);
        surcharges.stream().map(SurchargeDTO::getType).forEach(surchargeType -> {
            if (Objects.isNull(surchargeType)) {
                throw new OneboxRestException(ApiMgmtErrorCode.TYPE_MANDATORY);
            }
            if (!allowedTypes.contains(surchargeType)) {
                throw new OneboxRestException(ApiMgmtErrorCode.SURCHARGE_TYPE_NOT_ALLOWED_AT_ENTITY_LEVEL);
            }
        });
        if(surcharges.stream().anyMatch(surcharge -> hasFixedAndPercentageNull(surcharge.getRanges()))) {
            throw new OneboxRestException(ApiMgmtErrorCode.FIXED_OR_PERCENTAGE_MANDATORY);
        }

        securityManager.checkEntityAccessible(entityId);

        Operator operator = entitiesRepository.getCachedOperator(entityId);

        Set<String> surchargeRangesCurrencies = surcharges.stream().flatMap(
                            surcharge -> surcharge.getRanges().stream().map(RangeDTO::getCurrency))
                    .collect(Collectors.toSet());

        if(Objects.isNull(operator.getCurrencies()) && surchargeRangesCurrencies.size() > 1) {
            throw new OneboxRestException(ApiMgmtErrorCode.MULTI_CURRENCY_NOT_ALLOWED);
        }

        if(Objects.nonNull(operator.getCurrencies()) && surchargeRangesCurrencies.stream().anyMatch(currency ->
                currency != null && !operator.getCurrencies().getSelected().stream().map(Currency::getCode).collect(Collectors.toSet()).contains(currency))) {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_ALLOWED);
        }

        List<Surcharge> requests = surcharges.stream().map( surcharge ->
            SurchargeConverter.fromDTO(surcharge, masterdataService.getCurrencies(),CurrenciesUtils.getDefaultCurrency(operator))
        ).collect(Collectors.toList());

        entitiesRepository.setSurcharges(entityId, requests);
    }

    private boolean hasFixedAndPercentageNull(List<RangeDTO> ranges) {
        if (CollectionUtils.isNotEmpty(ranges)) {
            return ranges.stream()
                    .anyMatch(rangeDTO -> rangeDTO.getValues().getFixed() == null && rangeDTO.getValues().getPercentage() == null);
        } else {
            throw new OneboxRestException(ApiMgmtErrorCode.SURCHARGE_RANGE_MANDATORY);
        }
    }
}
