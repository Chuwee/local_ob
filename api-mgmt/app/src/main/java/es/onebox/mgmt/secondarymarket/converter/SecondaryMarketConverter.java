package es.onebox.mgmt.secondarymarket.converter;


import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.AdditionalSettings;
import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.Commission;
import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.CustomerLimits;
import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.ResalePrice;
import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.ResalePriceType;
import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.Restrictions;
import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.SeasonTicketSecondaryMarketConfig;
import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.SecondaryMarketConfig;
import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.SessionSecondaryMarketConfig;
import es.onebox.mgmt.secondarymarket.dto.AdditionalSettingsDTO;
import es.onebox.mgmt.secondarymarket.dto.CommissionDTO;
import es.onebox.mgmt.secondarymarket.dto.CustomerLimitsDTO;
import es.onebox.mgmt.secondarymarket.dto.ResalePriceDTO;
import es.onebox.mgmt.secondarymarket.dto.ResalePriceTypeDTO;
import es.onebox.mgmt.secondarymarket.dto.RestrictionsDTO;
import es.onebox.mgmt.secondarymarket.dto.SeasonTicketSecondaryMarketConfigDTO;
import es.onebox.mgmt.secondarymarket.dto.SecondaryMarketConfigDTO;
import es.onebox.mgmt.secondarymarket.dto.SecondaryMarketType;

public class SecondaryMarketConverter {

    public static <T extends SecondaryMarketConfig> T toMs(SecondaryMarketConfigDTO secondaryMarketConfigDTO, Class<T> clazz) {
        T config = createInstance(clazz);

        if (secondaryMarketConfigDTO != null) {
            if (secondaryMarketConfigDTO.getEnabled() != null) {
                config.setEnabled(secondaryMarketConfigDTO.getEnabled());
            }

            if (secondaryMarketConfigDTO.getPrice() != null) {
                config.setPrice(createResalePrice(secondaryMarketConfigDTO.getPrice()));
            }

            if (secondaryMarketConfigDTO.getCommission() != null) {
                config.setCommission(createCommission(secondaryMarketConfigDTO.getCommission()));
            }

            if (secondaryMarketConfigDTO.getCustomerLimits() != null) {
                config.setCustomerLimits(createCustomerLimits(secondaryMarketConfigDTO.getCustomerLimits()));
            }

            if (secondaryMarketConfigDTO.getCustomerLimitsEnabled() != null) {
                config.setCustomerLimitsEnabled(secondaryMarketConfigDTO.getCustomerLimitsEnabled());
            }
        }

        return config;
    }

    public static SeasonTicketSecondaryMarketConfig toMs(SeasonTicketSecondaryMarketConfigDTO seasonTicketSecondaryMarketConfigDTO) {
        SeasonTicketSecondaryMarketConfig config = new SeasonTicketSecondaryMarketConfig();

        if (seasonTicketSecondaryMarketConfigDTO != null) {

            if (seasonTicketSecondaryMarketConfigDTO.getEnabled() != null) {
                config.setEnabled(seasonTicketSecondaryMarketConfigDTO.getEnabled());
            }

            if (seasonTicketSecondaryMarketConfigDTO.getPrice() != null) {
                config.setPrice(createResalePrice(seasonTicketSecondaryMarketConfigDTO.getPrice()));
            }

            if (seasonTicketSecondaryMarketConfigDTO.getCommission() != null) {
                config.setCommission(createCommission(seasonTicketSecondaryMarketConfigDTO.getCommission()));
            }

            if (seasonTicketSecondaryMarketConfigDTO.getNumSessions() != null) {
                config.setNumSessions(seasonTicketSecondaryMarketConfigDTO.getNumSessions());
            }

            if (seasonTicketSecondaryMarketConfigDTO.getSaleType() != null) {
                config.setSaleType(seasonTicketSecondaryMarketConfigDTO.getSaleType());
            }

            if (seasonTicketSecondaryMarketConfigDTO.getAdditionalSettings() != null) {
                AdditionalSettings additionalSettings = new AdditionalSettings();
                additionalSettings.setHideBasePrice(seasonTicketSecondaryMarketConfigDTO.getAdditionalSettings().getHideBasePrice());
                additionalSettings.setPayToBalance(seasonTicketSecondaryMarketConfigDTO.getAdditionalSettings().getPayToBalance());
                config.setAdditionalSettings(additionalSettings);
            }

            config.setIsSeasonTicket(true);

            return config;
        }

        return config;
    }

    public static SecondaryMarketConfigDTO toDTO(SecondaryMarketConfig secondaryMarketConfig, SecondaryMarketType secondaryMarketType) {
        SecondaryMarketConfigDTO dto = mapCommonFields(secondaryMarketConfig);
        if (secondaryMarketConfig != null) {
            dto.setType(secondaryMarketType);
        }
        return dto;
    }

    public static SecondaryMarketConfigDTO toDTO(SessionSecondaryMarketConfig secondaryMarketConfig) {
        SecondaryMarketConfigDTO dto = mapCommonFields(secondaryMarketConfig);
        if (secondaryMarketConfig != null) {
            dto.setType(secondaryMarketConfig.getType());
        }
        return dto;
    }

    private static SecondaryMarketConfigDTO mapCommonFields(SecondaryMarketConfig secondaryMarketConfig) {
        SecondaryMarketConfigDTO dto = new SecondaryMarketConfigDTO();

        if (secondaryMarketConfig != null) {
            if (secondaryMarketConfig.getEnabled() != null) {
                dto.setEnabled(secondaryMarketConfig.getEnabled());
            }

            if (secondaryMarketConfig.getPrice() != null) {
                dto.setPrice(createResalePriceDTO(secondaryMarketConfig.getPrice()));
            }

            if (secondaryMarketConfig.getCommission() != null) {
                dto.setCommission(createCommissionDTO(secondaryMarketConfig.getCommission()));
            }

            if (secondaryMarketConfig.getCustomerLimits() != null) {
                dto.setCustomerLimits(createCustomerLimitsDTO(secondaryMarketConfig.getCustomerLimits()));
            }
            dto.setCustomerLimitsEnabled(secondaryMarketConfig.getCustomerLimitsEnabled());
        }

        return dto;
    }

    public static SeasonTicketSecondaryMarketConfigDTO toDTO(SeasonTicketSecondaryMarketConfig seasonTicketSecondaryMarketConfig) {

        SeasonTicketSecondaryMarketConfigDTO dto = new SeasonTicketSecondaryMarketConfigDTO();

        if (seasonTicketSecondaryMarketConfig != null) {
            if (seasonTicketSecondaryMarketConfig.getEnabled() != null) {
                dto.setEnabled(seasonTicketSecondaryMarketConfig.getEnabled());
            }

            if (seasonTicketSecondaryMarketConfig.getPrice() != null) {
                dto.setPrice(createResalePriceDTO(seasonTicketSecondaryMarketConfig.getPrice()));
            }

            if (seasonTicketSecondaryMarketConfig.getCommission() != null) {
                dto.setCommission(createCommissionDTO(seasonTicketSecondaryMarketConfig.getCommission()));
            }

            if (seasonTicketSecondaryMarketConfig.getNumSessions() != null) {
                dto.setNumSessions(seasonTicketSecondaryMarketConfig.getNumSessions());
            }

            if (seasonTicketSecondaryMarketConfig.getAdditionalSettings() != null) {
                dto.setAdditionalSettings(createAdditionalSettings(seasonTicketSecondaryMarketConfig.getAdditionalSettings()));
            }

            if (seasonTicketSecondaryMarketConfig.getSaleType() != null) {
                dto.setSaleType(seasonTicketSecondaryMarketConfig.getSaleType());
            }
        }

        return dto;
    }

    private static AdditionalSettingsDTO createAdditionalSettings(AdditionalSettings additionalSettings) {
        AdditionalSettingsDTO additionalSettingsDTO = new AdditionalSettingsDTO();

        if (additionalSettings.getHideBasePrice() != null) {
            additionalSettingsDTO.setHideBasePrice(additionalSettings.getHideBasePrice());
        }

        if (additionalSettings.getPayToBalance() != null) {
            additionalSettingsDTO.setPayToBalance(additionalSettings.getPayToBalance());
        }

        return additionalSettingsDTO;
    }

    private static ResalePriceDTO createResalePriceDTO(ResalePrice resalePrice) {
        ResalePriceDTO resalePriceDTO = new ResalePriceDTO();

        if (resalePrice.getType() != null) {
            resalePriceDTO.setType(ResalePriceTypeDTO.valueOf(resalePrice.getType().name()));
        }

        if (resalePrice.getRestrictions() != null) {
            resalePriceDTO.setRestrictions(createRestrictionsDTO(resalePrice.getRestrictions()));
        }

        return resalePriceDTO;
    }

    private static RestrictionsDTO createRestrictionsDTO(Restrictions restrictions) {
        RestrictionsDTO restrictionsDTO = new RestrictionsDTO();

        if (restrictions.getMin() != null) {
            restrictionsDTO.setMin(restrictions.getMin());
        }

        if (restrictions.getMax() != null) {
            restrictionsDTO.setMax(restrictions.getMax());
        }

        return restrictionsDTO;
    }

    private static CommissionDTO createCommissionDTO(Commission commission) {
        CommissionDTO commissionDTO = new CommissionDTO();

        if (commission.getPercentage() != null) {
            commissionDTO.setPercentage(commission.getPercentage());
        }

        return commissionDTO;
    }

    private static CustomerLimitsDTO createCustomerLimitsDTO(CustomerLimits customerLimits) {
        CustomerLimitsDTO customerLimitsDTO = new CustomerLimitsDTO();

        customerLimitsDTO.setExcludedCustomerTypes(customerLimits.getExcludedCustomerTypes());
        customerLimitsDTO.setLimit(customerLimits.getLimit());

        return customerLimitsDTO;
    }

    private static <T extends SecondaryMarketConfig> T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error creating instance of " + clazz.getName(), e);
        }
    }

    private static ResalePrice createResalePrice(ResalePriceDTO resalePriceDTO) {
        ResalePrice resalePrice = new ResalePrice();
        resalePrice.setType(ResalePriceType.valueOf(resalePriceDTO.getType().name()));

        if (resalePriceDTO.getRestrictions() != null) {
            Restrictions restrictions = new Restrictions();
            restrictions.setMin(resalePriceDTO.getRestrictions().getMin());
            restrictions.setMax(resalePriceDTO.getRestrictions().getMax());
            resalePrice.setRestrictions(restrictions);
        }

        return resalePrice;
    }

    private static Commission createCommission(CommissionDTO commissionDTO) {
        Commission commission = new Commission();
        commission.setPercentage(commissionDTO.getPercentage());
        return commission;
    }

    private static CustomerLimits createCustomerLimits(CustomerLimitsDTO customerLimitsDTO) {
        CustomerLimits customerLimits = new CustomerLimits();
        customerLimits.setLimit(customerLimitsDTO.getLimit());
        customerLimits.setExcludedCustomerTypes(customerLimitsDTO.getExcludedCustomerTypes());
        return customerLimits;
    }

}
