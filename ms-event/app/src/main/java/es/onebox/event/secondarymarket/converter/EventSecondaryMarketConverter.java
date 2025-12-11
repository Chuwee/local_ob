package es.onebox.event.secondarymarket.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.secondarymarket.domain.AdditionalSettings;
import es.onebox.event.secondarymarket.domain.Commission;
import es.onebox.event.secondarymarket.domain.CustomerLimits;
import es.onebox.event.secondarymarket.domain.EnabledChannel;
import es.onebox.event.secondarymarket.domain.EventSecondaryMarketConfig;
import es.onebox.event.secondarymarket.domain.ResalePrice;
import es.onebox.event.secondarymarket.domain.ResalePriceType;
import es.onebox.event.secondarymarket.domain.Restrictions;
import es.onebox.event.secondarymarket.dto.AdditionalSettingsDTO;
import es.onebox.event.secondarymarket.dto.CommissionDTO;
import es.onebox.event.secondarymarket.dto.CreateEventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.CustomerLimitsDTO;
import es.onebox.event.secondarymarket.dto.EnabledChannelDTO;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.ResalePriceDTO;
import es.onebox.event.secondarymarket.dto.ResalePriceTypeDTO;
import es.onebox.event.secondarymarket.dto.RestrictionsDTO;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Optional;

public class EventSecondaryMarketConverter {

    private EventSecondaryMarketConverter() {
    }

    public static EventSecondaryMarketConfigDTO toDTO(EventSecondaryMarketConfig entity) {
        if (entity == null) {
            return null;
        }
        EventSecondaryMarketConfigDTO eventSecondaryMarketConfigDTO = new EventSecondaryMarketConfigDTO();
        eventSecondaryMarketConfigDTO.setEnabledChannels(
                Optional.ofNullable(entity.getEnabledChannels())
                        .stream()
                        .flatMap(optList -> optList.stream().map(EventSecondaryMarketConverter::toDTO))
                        .toList()
        );
        eventSecondaryMarketConfigDTO.setEnabled(entity.getEnabled());
        eventSecondaryMarketConfigDTO.setIsSeasonTicket(entity.getIsSeasonTicket());
        eventSecondaryMarketConfigDTO.setNumSessions(entity.getNumSessions());
        if(entity.getSaleType() != null) {
            eventSecondaryMarketConfigDTO.setSaleType(entity.getSaleType());
        }

        if (entity.getPrice() != null) {
            ResalePriceDTO resalePriceDTO = new ResalePriceDTO();
            resalePriceDTO.setType(ResalePriceTypeDTO.valueOf(entity.getPrice().getType().name()));

            if (entity.getPrice().getRestrictions() != null) {
                RestrictionsDTO restrictionsDTO = new RestrictionsDTO();
                restrictionsDTO.setMin(entity.getPrice().getRestrictions().getMin());
                restrictionsDTO.setMax(entity.getPrice().getRestrictions().getMax());
                resalePriceDTO.setRestrictions(restrictionsDTO);
            }

            eventSecondaryMarketConfigDTO.setPrice(resalePriceDTO);
        }

        if (entity.getCommission() != null) {
            CommissionDTO commissionDTO = new CommissionDTO();
            commissionDTO.setPercentage(entity.getCommission().getPercentage());
            eventSecondaryMarketConfigDTO.setCommission(commissionDTO);
        }

        if (entity.getAdditionalSettings() != null) {
            AdditionalSettingsDTO additionalSettingsDTO = new AdditionalSettingsDTO();
            additionalSettingsDTO.setHideBasePrice(entity.getAdditionalSettings().getHideBasePrice());
            additionalSettingsDTO.setPayToBalance(entity.getAdditionalSettings().getPayToBalance());
            eventSecondaryMarketConfigDTO.setAdditionalSettings(additionalSettingsDTO);
        }

        if (entity.getCustomerLimits() != null) {
            CustomerLimitsDTO customerLimitsDTO = new CustomerLimitsDTO();
            customerLimitsDTO.setExcludedCustomerTypes(entity.getCustomerLimits()
                .getExcludedCustomerTypes());
            customerLimitsDTO.setLimit(entity.getCustomerLimits().getLimit());
            eventSecondaryMarketConfigDTO.setCustomerLimits(customerLimitsDTO);
            eventSecondaryMarketConfigDTO.setCustomerLimitsEnabled(true);
        } else {
            eventSecondaryMarketConfigDTO.setCustomerLimitsEnabled(false);
        }

        return eventSecondaryMarketConfigDTO;
    }

    public static EnabledChannelDTO toDTO(EnabledChannel entity) {
        if (entity == null) {
            return null;
        }
        EnabledChannelDTO dto = new EnabledChannelDTO(entity.getId());
            dto.setStartDate(entity.getStartDate());
            dto.setEndDate(entity.getEndDate());
        return dto;
    }

    public static EventSecondaryMarketConfig toEntity(CreateEventSecondaryMarketConfigDTO dto,
                                                      List<EnabledChannel> enabledChannels,
                                                      EventSecondaryMarketConfigDTO currentConfig) {
        EventSecondaryMarketConfig entity = new EventSecondaryMarketConfig();

        entity.setEnabled(dto.getEnabled());
        entity.setEnabledChannels(enabledChannels);

        if (!CommonUtils.isEmpty(dto.getEnabledChannels())) {
            entity.setEnabledChannels(dto.getEnabledChannels().stream().map(EventSecondaryMarketConverter::toEntity).toList());
        }

        ResalePrice resalePrice = new ResalePrice();
        resalePrice.setType(ResalePriceType.valueOf(dto.getPrice().getType().name()));

        if (dto.getPrice().getRestrictions() != null
            && ResalePriceTypeDTO.PRICE_WITH_RESTRICTIONS.equals(dto.getPrice().getType())) {
                Restrictions restrictions = new Restrictions();
                restrictions.setMin(dto.getPrice().getRestrictions().getMin());
                restrictions.setMax(dto.getPrice().getRestrictions().getMax());
                resalePrice.setRestrictions(restrictions);
        }

        entity.setPrice(resalePrice);
        if (BooleanUtils.isTrue(dto.getCustomerLimitsEnabled())) {
            entity.setCustomerLimits(toDTO(dto.getCustomerLimits()));
        } else if (BooleanUtils.isFalse(dto.getCustomerLimitsEnabled())) {
            entity.setCustomerLimits(null);
        } else if (currentConfig != null) {
            entity.setCustomerLimits(toDTO(currentConfig.getCustomerLimits()));
        }


        Commission commission = new Commission();
        commission.setPercentage(dto.getCommission().getPercentage());
        entity.setCommission(commission);

        if(dto.getIsSeasonTicket() != null) {
            entity.setIsSeasonTicket(dto.getIsSeasonTicket());
        }

        if(dto.getNumSessions() != null) {
            entity.setNumSessions(dto.getNumSessions());
        }

        if (dto.getAdditionalSettings() != null) {
            AdditionalSettingsDTO additionalSettingsDTO = dto.getAdditionalSettings();
            AdditionalSettings additionalSettings = new AdditionalSettings();
            additionalSettings.setHideBasePrice(additionalSettingsDTO.getHideBasePrice());
            additionalSettings.setPayToBalance(additionalSettingsDTO.getPayToBalance());
            entity.setAdditionalSettings(additionalSettings);
        }

        if (dto.getSaleType() != null) {
            entity.setSaleType(dto.getSaleType());
        }



        return entity;
    }

    private static CustomerLimits toDTO(CustomerLimitsDTO customerLimitsDTO) {
        if (customerLimitsDTO == null) {
            return null;
        }
        CustomerLimits customerLimits = new CustomerLimits();
        customerLimits.setExcludedCustomerTypes(customerLimitsDTO.getExcludedCustomerTypes());
        customerLimits.setLimit(customerLimitsDTO.getLimit());
        return customerLimits;
    }

    public static EnabledChannel toEntity(EnabledChannelDTO dto) {
        if (dto == null) {
            return null;
        }
        EnabledChannel entity = new EnabledChannel(dto.getId());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        return entity;
    }

}
