package es.onebox.mgmt.operators.converter;

import es.onebox.core.serializer.dto.common.IdCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOperatorCurrenciesDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOperatorCurrenciesRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOperatorRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOperatorTaxRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOperatorsResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.dto.OperatorsSearchFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateOperatorRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateOperatorTax;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateOperatorTaxesRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.Visibility;
import es.onebox.mgmt.datasources.ms.entity.dto.WalletConfigDTO;
import es.onebox.mgmt.entities.dto.EntitySettingsDTO;
import es.onebox.mgmt.entities.dto.SettingsCustomizationDTO;
import es.onebox.mgmt.operators.dto.CreateOperatorRequestDTO;
import es.onebox.mgmt.operators.dto.CreateOperatorTaxRequestDTO;
import es.onebox.mgmt.operators.dto.CreateOperatorsResponseDTO;
import es.onebox.mgmt.operators.dto.OperatorCurrencies;
import es.onebox.mgmt.operators.dto.OperatorCurrenciesDTO;
import es.onebox.mgmt.operators.dto.OperatorCurrencyDTO;
import es.onebox.mgmt.operators.dto.OperatorDTO;
import es.onebox.mgmt.operators.dto.OperatorsDTO;
import es.onebox.mgmt.operators.dto.OperatorsSearchRequestDTO;
import es.onebox.mgmt.operators.dto.UpdateOperatorCurrencyRequest;
import es.onebox.mgmt.operators.dto.UpdateOperatorCurrencyRequestDTO;
import es.onebox.mgmt.operators.dto.UpdateOperatorRequestDTO;
import es.onebox.mgmt.operators.dto.UpdateOperatorTaxDTO;
import es.onebox.mgmt.operators.dto.UpdateOperatorTaxesRequestDTO;
import es.onebox.mgmt.users.dto.VisibilityDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class OperatorsConverter {

    private OperatorsConverter(){
    }

    public static OperatorsDTO toDTO(List<Operator> in, Metadata metadata) {
        return new OperatorsDTO(in.stream().map(OperatorsConverter::toDTO).collect(Collectors.toList()), metadata);
    }

    public static OperatorDTO toDTO(Entity in) {
        EntitySettingsDTO settings = null;
        if (in.getCustomization() != null) {
            settings = new EntitySettingsDTO();
            settings.setCustomization(new SettingsCustomizationDTO());
            settings.getCustomization().setEnabled(in.getCustomization().getEnabled());
        }
        OperatorDTO operator = new OperatorDTO();
        operator.setId(in.getId());
        operator.setName(in.getName());
        operator.setSettings(settings);
        return operator;
    }

    public static OperatorDTO toDTO(Operator in) {
        String languageCode = ConverterUtils.toLanguageTag(in.getLanguage().getCode());
        IdNameCodeDTO language = new IdNameCodeDTO(in.getLanguage().getId(), in.getLanguage().getValue(), languageCode);
        IdCodeDTO timezone = null;
        if (in.getTimezone() != null && in.getTimezone().getId() != null) {
            timezone = new IdCodeDTO();
            timezone.setId(in.getTimezone().getId().longValue());
            timezone.setCode(in.getTimezone().getValue());
        }

        OperatorDTO operator = new OperatorDTO();
        operator.setId(in.getId());
        operator.setName(in.getName());
        operator.setShortName(in.getShortName());
        operator.setLanguage(language);
        operator.setShard(in.getShard());
        operator.setAllowFeverZone(in.getAllowFeverZone());

        IdCodeDTO currency = getCurrencyDTO(in.getCurrency());
        operator.setCurrency(currency);
        if (BooleanUtils.isTrue(in.getUseMultiCurrency())){
            operator.setCurrencies(toCurrenciesDTO(in.getCurrencies()));
        }
        if (CollectionUtils.isNotEmpty(in.getWallets())) {
            operator.setWallets(in.getWallets());
        }
        operator.setTimezone(timezone);
        operator.setGateways(in.getGateways());
        operator.setAllowGatewayBenefits(in.getAllowGatewayBenefits());

        return operator;
    }

    public static OperatorCurrenciesDTO toCurrenciesDTO(OperatorCurrencies source) {
        OperatorCurrenciesDTO operatorCurrencies = new OperatorCurrenciesDTO();
        operatorCurrencies.setDefaultCurrency(source.getDefaultCurrency());
        operatorCurrencies.setSelected(source.getSelected().stream()
                .map(OperatorsConverter::toCurrencyDTO)
                .toList());
        return operatorCurrencies;
    }

    private static OperatorCurrencyDTO toCurrencyDTO(Currency source) {
        OperatorCurrencyDTO currency = new OperatorCurrencyDTO();
        if (source != null && source.getId() != null) {
            currency.setId(source.getId());
            currency.setCode(source.getCode());
            currency.setDescription(source.getDescription());
        }
        return currency;
    }
    public static UpdateOperatorCurrencyRequest toCurrenciesMs(UpdateOperatorCurrencyRequestDTO source) {
        UpdateOperatorCurrencyRequest target = new UpdateOperatorCurrencyRequest();
        target.setCurrencyCodes(source.currencyCodes());
        return target;
    }

    //DELETE WHEN MULTICURRENCY MIGRATION IS DONE
    private static IdCodeDTO getCurrencyDTO(IdValueDTO in) {
        IdCodeDTO currency = new IdCodeDTO();
        if (in != null && in.getId() != null) {
            currency.setId(in.getId().longValue());
            currency.setCode(in.getValue());
        }
        return currency;
    }

    private static CreateOperatorCurrenciesDTO toDTO(CreateOperatorCurrenciesRequest in) {
        CreateOperatorCurrenciesDTO out = new CreateOperatorCurrenciesDTO();
        if (in != null) {
            out.setCurrencyCodes(in.getCurrencyCodes());
            out.setDefaultCurrency(in.getDefaultCurrency());
        }
        return out;
    }

    public static CreateOperatorsResponseDTO toDTO(CreateOperatorsResponse in) {
        CreateOperatorsResponseDTO out = new CreateOperatorsResponseDTO();
        out.setPassword(in.password());
        out.setId(in.id());
        return out;
    }

    public static CreateOperatorRequest toMs(CreateOperatorRequestDTO in) {
        CreateOperatorRequest out = new CreateOperatorRequest();
        out.setName(in.name());
        out.setShortName(in.shortName());
        out.setCurrencyCode(in.currencyCode());
        out.setOlsonId(in.olsonId());
        out.setLanguageCode(convertLanguage(in.languageCode()));
        out.setShard(in.shard().name());
        out.setGateways(in.gateways());
        out.setCurrencies(toDTO(in.currencies()));
        out.setWallets(in.wallets());
        return out;
    }

    public static VisibilityDTO toDTO(Visibility visibility) {
        VisibilityDTO out = new VisibilityDTO();
        out.setType(visibility.getType());
        return out;
    }

    public static OperatorsSearchFilter toMs(OperatorsSearchRequestDTO in) {
        OperatorsSearchFilter out = new OperatorsSearchFilter();
        out.setId(in.getId());
        out.setName(in.getName());
        out.setShortName(in.getShortName());
        out.setFreeSearch(in.getFreeSearch());
        out.setSort(in.getSort());
        out.setFields(in.getFields());
        out.setIds(in.getIds());
        out.setOffset(in.getOffset());
        out.setLimit(in.getLimit());
        return out;
    }

    public static UpdateOperatorRequest toMs(UpdateOperatorRequestDTO in) {
        UpdateOperatorRequest out = new UpdateOperatorRequest();
        out.setName(in.name());
        out.setCurrencyCode(in.currencyCode());
        out.setOlsonId(in.olsonId());
        out.setLanguageCode(convertLanguage(in.languageCode()));
        out.setGateways(in.gateways());
        out.setWallets(in.wallets());
        out.setAllowFeverZone(in.allowFeverZone());
        out.setAllowGatewayBenefits(in.allowGatewayBenefits());
        return out;
    }

    private static String convertLanguage(final String languageCode) {
        if (languageCode == null) {
            return null;
        }
        return ConverterUtils.toLocale(languageCode);
    }

    public static CreateOperatorTaxRequest toMs(CreateOperatorTaxRequestDTO createOperatorTaxRequestDTO) {
        CreateOperatorTaxRequest createOperatorTaxRequest = new CreateOperatorTaxRequest();
        createOperatorTaxRequest.setNombre(createOperatorTaxRequestDTO.getName());
        createOperatorTaxRequest.setDescripcion(createOperatorTaxRequestDTO.getDescription());
        createOperatorTaxRequest.setValor(createOperatorTaxRequestDTO.getValue());
        return createOperatorTaxRequest;
    }

    public static UpdateOperatorTaxesRequest toMs(UpdateOperatorTaxesRequestDTO updateOperatorTaxesRequestDTO) {
        UpdateOperatorTaxesRequest updateOperatorTaxesRequest = new UpdateOperatorTaxesRequest();
        for (UpdateOperatorTaxDTO dto : updateOperatorTaxesRequestDTO) {
            UpdateOperatorTax toUpdate = new UpdateOperatorTax();
            toUpdate.setIdImpuesto(dto.getId());
            toUpdate.setNombre(dto.getName());
            toUpdate.setDescripcion(dto.getDescription());
            toUpdate.setDefecto(dto.getDefaultTax());
            updateOperatorTaxesRequest.add(toUpdate);
        }
        return updateOperatorTaxesRequest;
    }

}
