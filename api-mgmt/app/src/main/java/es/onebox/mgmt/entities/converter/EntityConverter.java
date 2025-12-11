package es.onebox.mgmt.entities.converter;

import es.onebox.core.security.EntityTypes;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.Operator;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.accommodations.AccommodationsVendor;
import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import es.onebox.mgmt.datasources.common.dto.AuthConfig;
import es.onebox.mgmt.datasources.ms.client.dto.AuthVendorEntityConfig;
import es.onebox.mgmt.datasources.ms.client.dto.PhoneValidatorEntityConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.Entities;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.EntitySearchFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTax;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalBarcodeEntityConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.PostBookingQuestions;
import es.onebox.mgmt.datasources.ms.entity.dto.QueueConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.WhatsappConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.user.realm.DonationsConfig;
import es.onebox.mgmt.entities.dto.AccountSettingsDTO;
import es.onebox.mgmt.entities.dto.AuthVendor;
import es.onebox.mgmt.entities.dto.Categories;
import es.onebox.mgmt.entities.dto.CustomManagementDTO;
import es.onebox.mgmt.entities.dto.CustomManagementsDTO;
import es.onebox.mgmt.entities.dto.DonationsConfigDTO;
import es.onebox.mgmt.entities.dto.EntityAccommodationsConfigDTO;
import es.onebox.mgmt.entities.dto.EntityContactDTO;
import es.onebox.mgmt.entities.dto.EntityCustomersDTO;
import es.onebox.mgmt.entities.dto.EntityDTO;
import es.onebox.mgmt.entities.dto.EntityInvoiceDataDTO;
import es.onebox.mgmt.entities.dto.EntitySearchFilterDTO;
import es.onebox.mgmt.entities.dto.EntitySettingsBIUsers;
import es.onebox.mgmt.entities.dto.EntitySettingsDTO;
import es.onebox.mgmt.entities.dto.EntityTaxApiDTO;
import es.onebox.mgmt.entities.dto.EntityTaxApiDTOBuilder;
import es.onebox.mgmt.entities.dto.ExternalBarcode;
import es.onebox.mgmt.entities.dto.ExternalIntegration;
import es.onebox.mgmt.entities.dto.LanguagesDTO;
import es.onebox.mgmt.entities.dto.PhoneValidatorDTO;
import es.onebox.mgmt.entities.dto.PostBookingQuestionsDTO;
import es.onebox.mgmt.entities.dto.QueueConfigDTO;
import es.onebox.mgmt.entities.dto.SettingsCustomizationDTO;
import es.onebox.mgmt.entities.dto.SettingsEmailNotificationsDTO;
import es.onebox.mgmt.entities.dto.SettingsInteractiveVenueDTO;
import es.onebox.mgmt.entities.dto.SettingsLiveStreamingDTO;
import es.onebox.mgmt.entities.dto.SettingsNotificationsDTO;
import es.onebox.mgmt.entities.dto.StreamingVendor;
import es.onebox.mgmt.entities.dto.UpdateEntityRequestDTO;
import es.onebox.mgmt.entities.dto.WhatsappConfigDTO;
import es.onebox.mgmt.entities.enums.AccommodationsChannelEnablingMode;
import es.onebox.mgmt.entities.enums.CustomManagementType;
import es.onebox.mgmt.entities.enums.EntityStatus;
import es.onebox.mgmt.entities.enums.EntityType;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityConverter {

    private EntityConverter() {
    }

    public static EntityDTO fromMsEntity(Entity source,
                                         Map<Long, String> languages) {
        return internalFromMsEntity(source, languages, null, null,
                null, false, null, null, null);
    }

    public static EntityDTO fromMsEntityReduced(Entity source) {
        return internalFromMsEntityReduced(source);
    }

    public static EntityDTO fromMsEntity(Entity source,
                                         Map<Long, String> languages,
                                         AuthVendorEntityConfig authVendorEntityConfiguration,
                                         PhoneValidatorEntityConfig phoneValidatorEntityConfig,
                                         ExternalBarcodeEntityConfig externalBarcodeEntityConfig,
                                         Boolean hasAttributes,
                                         ExternalConfig externalEntityConfig,
                                         AuthConfig authConfig) {
        return internalFromMsEntity(source, languages, authVendorEntityConfiguration,
                phoneValidatorEntityConfig, externalBarcodeEntityConfig, true, hasAttributes, externalEntityConfig, authConfig);
    }

    private static EntityDTO internalFromMsEntity(Entity source,
                                                  Map<Long, String> languages,
                                                  AuthVendorEntityConfig authVendorEntityConfiguration,
                                                  PhoneValidatorEntityConfig phoneValidatorEntityConfig,
                                                  ExternalBarcodeEntityConfig externalBarcodeEntityConfig,
                                                  boolean loadCategories,
                                                  Boolean hasAttributes,
                                                  ExternalConfig externalConfig,
                                                  AuthConfig authConfig) {
        if (source == null) {
            return null;
        }

        EntityDTO entityDTO = new EntityDTO(source.getId());
        entityDTO.setName(source.getName());
        entityDTO.setShortName(source.getShortName());
        entityDTO.setReference(source.getReference());
        entityDTO.setNif(source.getNif());
        entityDTO.setSocialReason(source.getSocialReason());
        entityDTO.setNotes(source.getNotes());
        if (source.getState() != null) {
            entityDTO.setStatus(EntityStatus.getById(source.getState().getState()));
        }
        if (source.getOperator() != null) {
            entityDTO.setOperator(new IdNameDTO(source.getOperator().getId(), source.getOperator().getName()));
        }

        entityDTO.setContact(fillContact(source));
        entityDTO.setInvoiceData(fillInvoiceData(source));
        entityDTO.setExternalReference(source.getExternalReference());
        entityDTO.setSettings(fillSettings(source, languages, authVendorEntityConfiguration, phoneValidatorEntityConfig,
                externalBarcodeEntityConfig, loadCategories, hasAttributes, externalConfig, authConfig));
		entityDTO.setInventoryProviders(source.getInventoryProviders());
        return entityDTO;
    }

    private static EntityDTO internalFromMsEntityReduced(Entity source) {
        if (source == null) {
            return null;
        }

        EntityDTO entityDTO = new EntityDTO(source.getId());
        entityDTO.setName(source.getName());
        entityDTO.setShortName(source.getShortName());

        return entityDTO;
    }

    public static EntityType convertEntityTypes(EntityTypes type) {
        if (EntityTypes.MULTI_PROMOTER.equals(type)) {
            return EntityType.MULTI_PRODUCER;
        }
        return EntityType.valueOf(type.name());
    }

    public static List<IdNameDTO> fromMs(Entities source) {

        return source.getData().stream()
                .map(EntityConverter::managedEntityFromMs)
                .toList();
    }

    private static IdNameDTO managedEntityFromMs(Entity source) {
        IdNameDTO target = new IdNameDTO();
        target.setId(source.getId());
        target.setName(source.getName());
        return target;
    }

    public static EntityTypes convertEntityType(EntityType type) {
        if (EntityType.MULTI_PRODUCER.equals(type)) {
            return EntityTypes.MULTI_PROMOTER;
        }
        return EntityTypes.valueOf(type.name());
    }

    private static EntitySettingsDTO fillSettings(Entity source,
                                                  Map<Long, String> languages,
                                                  AuthVendorEntityConfig authVendorEntityConfiguration,
                                                  PhoneValidatorEntityConfig phoneValidatorEntityConfig,
                                                  ExternalBarcodeEntityConfig externalBarcodeEntityConfig,
                                                  boolean loadCategories,
                                                  Boolean hasAttributes, ExternalConfig externalConfig, AuthConfig authConfig) {
        EntitySettingsDTO settings = new EntitySettingsDTO();
        settings.setTypes(fillEntityTypes(source.getTypes()));
        settings.setCorporateColor(source.getCorporateColor());
        settings.setAllowActivityEvents(source.getUseActivityEvent());
        settings.setAllowAvetIntegration(source.getUseExternalAvetIntegration());
        settings.setEnableMultieventCart(source.getUseMultieventCart());
        settings.setEnableB2B(source.getModuleB2BEnabled());
        settings.setAllowB2BPublishing(source.getAllowB2BPublishing());
        settings.setAllowInvitations(source.getAllowInvitations());
        settings.setAllowMultiAvetCart(source.getAllowMultiAvetCart());
        settings.setAllowSecondaryMarket(source.getUseSecondaryMarket());
        settings.setAllowAttributes(hasAttributes);
        settings.setAllowVipViews(source.getAllowVipViews());
        settings.setAllowDataProtectionFields(source.getAllowDataProtectionFields());
        settings.setAllowMembers(source.getAllowMembers());
        settings.setAllowDigitalSeasonTicket(source.getAllowDigitalSeasonTicket());
        settings.setAllowTicketHidePrice(source.getAllowTicketHidePrice());
        settings.setAllowHardTicketPDF(source.getAllowHardTicketPDF());
        settings.setManagedEntities(source.getManagedEntities());
        settings.setAllowLoyaltyPoints(BooleanUtils.isTrue(source.getAllowLoyaltyPoints()));
        settings.setAllowFriends(BooleanUtils.isTrue(source.getAllowFriends()));
        settings.setAllowPngConversion(BooleanUtils.isTrue(source.getAllowPngConversion()));
        settings.setAllowFeverZone(source.getAllowFeverZone());
        settings.setCustomersDomainSettings(source.getCustomersDomainSettings());
        settings.setPostBookingQuestions(toDto(source.getPostBookingQuestions()));
        settings.setQueueProvider(source.getQueueProvider());
        settings.setAllowGatewayBenefits(source.getAllowGatewayBenefits());
        settings.setMemberIdGeneration(source.getMemberIdGeneration());
        settings.setAllowConfigMultipleTemplates(source.getAllowConfigMultipleTemplates());
        settings.setAllowDestinationChannels(source.getAllowDestinationChannels());

        EntitySettingsBIUsers entityBIUsers = new EntitySettingsBIUsers(source.getBasicBIUsersLimit(), source.getAdvancedBIUsersLimit());
        if (entityBIUsers.getAdvancedLimit() != null || entityBIUsers.getBasicLimit() != null) {
            settings.setBiUsers(entityBIUsers);
        }

        if (languages != null && CollectionUtils.isNotEmpty(source.getSelectedLanguages())) {
            settings.setLanguages(new LanguagesDTO());
            settings.getLanguages().setAvailableLanguages(
                    source.getSelectedLanguages().stream()
                            .filter(lang -> languages.containsKey(lang.getId()))
                            .map(lang -> ConverterUtils.toLanguageTag(languages.get(lang.getId())))
                            .collect(Collectors.toList())
            );
            settings.getLanguages().setDefaultLanguage(ConverterUtils.toLanguageTag(languages.get(source.getLanguage().getId())));
        }
        if (source.getStreaming() != null && source.getStreaming().getEnabled() != null) {
            SettingsLiveStreamingDTO liveStreaming = new SettingsLiveStreamingDTO();
            liveStreaming.setEnabled(source.getStreaming().getEnabled());
            if (!CommonUtils.isEmpty(source.getStreaming().getVendor())) {
                liveStreaming.setVendors(source.getStreaming().getVendor().stream().
                        map(v -> StreamingVendor.valueOf(v.name())).collect(Collectors.toList()));
            }
            settings.setLiveStreaming(liveStreaming);
        }
        if (source.getInteractiveVenue() != null && source.getInteractiveVenue().getEnabled() != null) {
            SettingsInteractiveVenueDTO interactiveVenue = new SettingsInteractiveVenueDTO();
            interactiveVenue.setEnabled(source.getInteractiveVenue().getEnabled());
            if (!CommonUtils.isEmpty(source.getInteractiveVenue().getAllowedVenues())) {
                interactiveVenue.setAllowedVenues(source.getInteractiveVenue().getAllowedVenues().stream().
                        map(vt -> InteractiveVenueType.valueOf(vt.name())).collect(Collectors.toList()));
            }
            settings.setInteractiveVenue(interactiveVenue);
        }
        if (source.getAccommodationsConfig() != null && source.getAccommodationsConfig().getEnabled() != null) {
            EntityAccommodationsConfigDTO accommodationsConfig = new EntityAccommodationsConfigDTO();
            accommodationsConfig.setEnabled(source.getAccommodationsConfig().getEnabled());
            if (!CommonUtils.isEmpty(source.getAccommodationsConfig().getAllowedVendors())) {
                accommodationsConfig.setAllowedVendors(
                        source.getAccommodationsConfig().getAllowedVendors()
                                .stream()
                                .map(vendor -> AccommodationsVendor.valueOf(vendor.name()))
                                .collect(Collectors.toList())
                );
            }
            if (source.getAccommodationsConfig().getChannelEnablingMode() != null) {
                accommodationsConfig.setChannelEnablingMode(
                        AccommodationsChannelEnablingMode.valueOf(source.getAccommodationsConfig().getChannelEnablingMode().name())
                );
            }
            if (!CommonUtils.isEmpty(source.getAccommodationsConfig().getEnabledChannelIds())) {
                accommodationsConfig.setEnabledChannelIds(source.getAccommodationsConfig().getEnabledChannelIds());
            }
            settings.setAccommodationsConfig(accommodationsConfig);
        }

        if (source.getWhatsappConfig() != null) {
            settings.setWhatsappConfig(toDto(source.getWhatsappConfig()));
        }

        if (!CommonUtils.isEmpty(source.getDonationsConfig())) {
            Set<DonationsConfigDTO> donationsConfigDTO = source.getDonationsConfig().stream()
                    .map(EntityConverter::convertToDonationsConfigDTO)
                    .collect(Collectors.toSet());
            settings.setDonationsConfigDTO(donationsConfigDTO);
        }

        fillNotifications(source, settings);
        fillCustomization(source, settings);
        fillExternalConfiguration(source, authVendorEntityConfiguration, phoneValidatorEntityConfig, externalBarcodeEntityConfig, settings, externalConfig);
        if (loadCategories) {
            fillCategories(source, settings);
        }

        settings.setEnableV4Configs(source.getEnableV4Configs());

        fillAccountSettings(source, settings, authConfig);

        if (settings.equals(new EntitySettingsDTO())) {
            return null;
        }

        if (source.getCustomers() != null) {
            EntityCustomersDTO customers = new EntityCustomersDTO();
            customers.setAutoAssignOrders(source.getCustomers().getAutoAssignOrders());
            settings.setCustomers(customers);
        }

        return settings;
    }

    private static DonationsConfigDTO convertToDonationsConfigDTO(DonationsConfig donationsConfig) {
        DonationsConfigDTO config = new DonationsConfigDTO();
        config.setEnabled(donationsConfig.getEnabled());
        config.setApiKey(donationsConfig.getApiKey());
        config.setProviderId(donationsConfig.getProviderId());
        return config;
    }

    private static void fillCategories(Entity source, EntitySettingsDTO settings) {
        settings.setCategories(new Categories());
        settings.getCategories().setAllowCustomCategories(CommonUtils.isTrue(source.getUseCustomCategories()));
        settings.getCategories().setSelected(source.getSelectedCategories());
    }

    private static void fillExternalConfiguration(Entity source, AuthVendorEntityConfig authVendorEntityConfiguration,
                                                  PhoneValidatorEntityConfig phoneValidatorEntityConfig,
                                                  ExternalBarcodeEntityConfig externalBarcodeEntityConfig,
                                                  EntitySettingsDTO settings, ExternalConfig externalConfig) {
        ExternalIntegration externalIntegration = new ExternalIntegration();
        externalIntegration.setCustomManagements(fillCustomManagement(source, externalConfig));
        if (authVendorEntityConfiguration != null) {
            externalIntegration.setAuthVendor(new AuthVendor());
            externalIntegration.getAuthVendor().setEnabled(authVendorEntityConfiguration.getAllowed());
            if (CommonUtils.isTrue(authVendorEntityConfiguration.getAllowed())) {
                externalIntegration.getAuthVendor().setVendorId(authVendorEntityConfiguration.getvendors());
            }
        }
        if (phoneValidatorEntityConfig != null) {
            externalIntegration.setPhoneValidator(new PhoneValidatorDTO());
            externalIntegration.getPhoneValidator().setEnabled(phoneValidatorEntityConfig.getEnabled());
            if (CommonUtils.isTrue(phoneValidatorEntityConfig.getEnabled())) {
                externalIntegration.getPhoneValidator().setValidatorId(phoneValidatorEntityConfig.getValidatorId());
            }
            externalIntegration.getPhoneValidator().setValidatorIds(phoneValidatorEntityConfig.getValidatorIds());
        }
        if (externalBarcodeEntityConfig != null) {
            externalIntegration.setExternalBarcode(new ExternalBarcode());
            externalIntegration.getExternalBarcode().setEnabled(externalBarcodeEntityConfig.getAllowExternalBarcode());
            if (CommonUtils.isTrue(externalBarcodeEntityConfig.getAllowExternalBarcode())) {
                externalIntegration.getExternalBarcode().setIntegrationId(externalBarcodeEntityConfig.getExternalBarcodeFormatId());
            }
        }

        if (authVendorEntityConfiguration != null || externalBarcodeEntityConfig != null
                || phoneValidatorEntityConfig != null || CollectionUtils.isNotEmpty(externalIntegration.getCustomManagements())) {
            settings.setExternalIntegration(externalIntegration);
        }
    }

    private static CustomManagementsDTO fillCustomManagement(Entity source, ExternalConfig externalConfig) {
        CustomManagementsDTO target = new CustomManagementsDTO();
        if (BooleanUtils.isTrue(source.getAllowExternalManagement())) {
            target.add(new CustomManagementDTO(source.getAllowExternalManagement(), CustomManagementType.INCOMPATIBILITY_ENGINE));
        }
        if (BooleanUtils.isTrue(source.getUseExternalAvetIntegration())) {
            target.add(new CustomManagementDTO(source.getUseExternalAvetIntegration(), CustomManagementType.AVET_INTEGRATION));
        }
        if (externalConfig != null && externalConfig.getSmartBooking() != null
                && BooleanUtils.isTrue(externalConfig.getSmartBooking().getEnabled())) {
            target.add(new CustomManagementDTO(externalConfig.getSmartBooking().getEnabled(), CustomManagementType.SMART_BOOKING_INTEGRATION));
        }
        if (externalConfig != null && externalConfig.getSga() != null
                && BooleanUtils.isTrue(externalConfig.getSga().getEnabled())) {
            target.add(new CustomManagementDTO(externalConfig.getSga().getEnabled(), CustomManagementType.SGA_INTEGRATION));
        }
        return target;
    }

    private static List<EntityType> fillEntityTypes(List<EntityTypes> types) {
        List<EntityType> entityTypes = null;
        if (!CommonUtils.isEmpty(types)) {
            entityTypes = new ArrayList<>();
            for (EntityTypes type : types) {
                entityTypes.add(convertEntityTypes(type));
            }
        }
        return entityTypes;
    }

    private static EntityInvoiceDataDTO fillInvoiceData(Entity source) {
        EntityInvoiceDataDTO invoiceData = new EntityInvoiceDataDTO();
        invoiceData.setAddress(source.getInvoiceAddress());
        invoiceData.setCity(source.getInvoiceCity());
        invoiceData.setPostalCode(source.getInvoicePostalCode());
        invoiceData.setBankAccount(source.getBankAccount());

        if (invoiceData.equals(new EntityInvoiceDataDTO())) {
            return null;
        }

        return invoiceData;
    }

    private static EntityContactDTO fillContact(Entity source) {
        EntityContactDTO contact = new EntityContactDTO();
        contact.setAddress(source.getAddress());
        contact.setCity(source.getCity());
        contact.setPostalCode(source.getPostalCode());
        contact.setEmail(source.getEmail());
        contact.setPhone(source.getPhone());
        return contact;
    }

    private static void fillNotifications(Entity source, EntitySettingsDTO target) {
        if (source.getAllowMassiveEmail() != null) {
            SettingsNotificationsDTO notifications = new SettingsNotificationsDTO();
            notifications.setEmail(new SettingsEmailNotificationsDTO());
            notifications.getEmail().setEnabled(source.getAllowMassiveEmail());
            if (source.getNotifications() != null) {
                if (source.getNotifications().getEmail() != null) {
                    notifications.getEmail().setSendLimit(NumberUtils.zeroIfNull(source.getNotifications().getEmail().getSendLimit()));
                }
            }
            target.setNotifications(notifications);
        }
    }

    private static void fillCustomization(Entity source, EntitySettingsDTO target) {
        if (source.getCustomization() != null) {
            SettingsCustomizationDTO customization = new SettingsCustomizationDTO();
            customization.setEnabled(source.getCustomization().getEnabled());
            customization.setFaviconUrl(source.getCustomization().getFaviconUrl());
            customization.setLogoUrl(source.getCustomization().getLogoUrl());
            customization.setTinyUrl(source.getCustomization().getTinyUrl());
            target.setCustomization(customization);
        }
    }

    public static Entity toMsEntity(UpdateEntityRequestDTO source) {
        Entity entityDTO = new Entity();
        entityDTO.setName(source.getName());
        entityDTO.setReference(source.getReference());
        entityDTO.setSocialReason(source.getSocialReason());
        entityDTO.setNif(source.getNif());
        entityDTO.setNotes(source.getNotes());

        if (source.getContact() != null) {
            entityDTO.setAddress(source.getContact().getAddress());
            entityDTO.setCity(source.getContact().getCity());
            entityDTO.setPostalCode(source.getContact().getPostalCode());
            entityDTO.setPhone(source.getContact().getPhone());
            entityDTO.setEmail(source.getContact().getEmail());
        }
        if (source.getInvoiceData() != null) {
            entityDTO.setInvoiceAddress(source.getInvoiceData().getAddress());
            entityDTO.setInvoiceCity(source.getInvoiceData().getCity());
            entityDTO.setInvoicePostalCode(source.getInvoiceData().getPostalCode());
            entityDTO.setBankAccount(source.getInvoiceData().getBankAccount());
        }

        return entityDTO;
    }

    public static Entity toMsEntity(EntityDTO source) {
        Entity entity = new Entity();
        entity.setName(source.getName());
        entity.setShortName(source.getShortName());
        entity.setSocialReason(source.getSocialReason());
        entity.setNif(source.getNif());
        entity.setReference(source.getReference());
        if (source.getSettings() != null && source.getSettings().getManagedEntities() != null) {
            entity.setManagedEntities(source.getSettings().getManagedEntities());
        }
        if (source.getContact() != null) {
            entity.setCity(source.getContact().getCity());
        }
        return entity;
    }

    public static List<EntityTaxApiDTO> fromDTO(List<EntityTax> entityTaxes) {
        if (entityTaxes == null) {
            return new ArrayList<>();
        }

        return entityTaxes.stream().map(EntityConverter::fromDTO).collect(Collectors.toList());
    }

    // private because EntityTaxApiDTO doesn't have metadata and shouldn't be used as a response.
    private static EntityTaxApiDTO fromDTO(EntityTax entityTaxDTO) {
        if (entityTaxDTO == null) {
            return null;
        }

        return new EntityTaxApiDTOBuilder()
                .setId(entityTaxDTO.getIdImpuesto())
                .setName(entityTaxDTO.getNombre())
                .setDescription(entityTaxDTO.getDescripcion())
                .setValue(entityTaxDTO.getValor())
                .setDefaultTax(entityTaxDTO.getDefecto() != null
                        ? entityTaxDTO.getDefecto()
                        : Boolean.FALSE)
                .createEntityTaxApiDTO();
    }


    public static EntitySearchFilter buildEntitiesFilter(EntitySearchFilterDTO source) {
        EntitySearchFilter target = new EntitySearchFilter();
        target.setAllowAvetIntegration(source.getAllowAvetIntegration());
        target.setAllowMembers(source.getAllowMembers());
        target.setB2bEnabled(source.getB2bEnabled());
        target.setAllowDigitalSeasonTicket(source.getAllowDigitalSeasonTicket());
        target.setAllowMassiveEmail(source.getAllowMassiveEmail());
        target.setFreeSearch(source.getFreeSearch());
        target.setIncludeEntityAdmin(source.getIncludeEntityAdmin());
        target.setSort(source.getSort());
        target.setFields(source.getFields());
        target.setLimit(source.getLimit());
        target.setOffset(source.getOffset());
        target.setOperatorId(source.getOperatorId());

        if (source.getType() != null) {
            target.setType(EntityType.MULTI_PRODUCER.equals(source.getType()) ? EntityTypes.MULTI_PROMOTER.name() : source.getType().name());
        }
        if (source.getStatus() != null) {
            target.setStatus(FilterWithOperator.build(Operator.EQUALS, source.getStatus().name()));
        }

        return target;
    }

    public static void fillEntityAdmin(EntityDTO entity, Function<EntitySearchFilter, Entities> entitiesGetter) {
        if (SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)) {
            entity.getSettings().setEnableB2B(hasB2B(entity, entitiesGetter));
            if (hasMassiveEmail(entity, entitiesGetter)) {
                entity.getSettings().setNotifications(new SettingsNotificationsDTO());
                entity.getSettings().getNotifications().setEmail(new SettingsEmailNotificationsDTO());
                entity.getSettings().getNotifications().getEmail().setEnabled(Boolean.TRUE);
            }
            if (hasAVETIntegrations(entity, entitiesGetter)) {
                ExternalIntegration externalIntegration = new ExternalIntegration();
                externalIntegration.setCustomManagements(new CustomManagementsDTO());
                externalIntegration.getCustomManagements().add(new CustomManagementDTO(true, CustomManagementType.AVET_INTEGRATION));
                entity.getSettings().setExternalIntegration(externalIntegration);
            }
            entity.getSettings().setAllowAvetIntegration(hasAVETIntegrations(entity, entitiesGetter));

        }
    }

    private static boolean hasB2B(EntityDTO entity, Function<EntitySearchFilter, Entities> entitiesGetter) {
        EntitySearchFilter filter = new EntitySearchFilter();
        filter.setEntityAdminId(entity.getId());
        filter.setB2bEnabled(true);
        return entitiesGetter.apply(filter).getMetadata().getTotal() > 0;
    }

    private static boolean hasMassiveEmail(EntityDTO entity, Function<EntitySearchFilter, Entities> entitiesGetter) {
        EntitySearchFilter filter = new EntitySearchFilter();
        filter.setEntityAdminId(entity.getId());
        filter.setAllowMassiveEmail(true);
        return entitiesGetter.apply(filter).getMetadata().getTotal() > 0;
    }

    private static boolean hasAVETIntegrations(EntityDTO entity, Function<EntitySearchFilter, Entities> entitiesGetter) {
        EntitySearchFilter filter = new EntitySearchFilter();
        filter.setEntityAdminId(entity.getId());
        filter.setAllowAvetIntegration(true);
        return entitiesGetter.apply(filter).getMetadata().getTotal() > 0;
    }

    private static void fillAccountSettings(Entity source, EntitySettingsDTO settings, AuthConfig authConfig) {
        if (source.getAccountSettings() != null && authConfig != null && Boolean.TRUE.equals(authConfig.getEnabled())) {
            AccountSettingsDTO accountSettingsDTO = new AccountSettingsDTO();
            fillQueueSettings(source.getAccountSettings().getQueueConfig(), accountSettingsDTO);
            settings.setAccountSettings(accountSettingsDTO);
        }
    }

    private static void fillQueueSettings(QueueConfig source, AccountSettingsDTO accountSettingsDTO) {
        if (source != null) {
            QueueConfigDTO queueConfigDTO = new QueueConfigDTO();
            queueConfigDTO.setActive(source.getActive());
            queueConfigDTO.setAlias(source.getAlias());
            accountSettingsDTO.setQueueConfig(queueConfigDTO);
        }
    }

    public static WhatsappConfig toMsEntity(WhatsappConfigDTO whatsappConfig) {
        if (whatsappConfig == null) {
            return null;
        }

        WhatsappConfig config = new WhatsappConfig();
        config.setEnabled(whatsappConfig.getEnabled());
        config.setWhatsappTemplate(whatsappConfig.getWhatsappTemplate());

        return config;
    }

    public static WhatsappConfigDTO toDto(WhatsappConfig whatsappConfig) {
        if (whatsappConfig == null) {
            return null;
        }

        WhatsappConfigDTO dto = new WhatsappConfigDTO();
        dto.setEnabled(whatsappConfig.getEnabled());
        dto.setWhatsappTemplate(whatsappConfig.getWhatsappTemplate());

        return dto;
    }

    public static PostBookingQuestions toMsEntity(PostBookingQuestionsDTO  postBookingQuestionsDTO) {

        if (postBookingQuestionsDTO == null) {
            return null;
        }

        PostBookingQuestions postBookingQuestions = new PostBookingQuestions();
        postBookingQuestions.setEnabled(postBookingQuestionsDTO.getEnabled());

        return postBookingQuestions;
    }

    public static PostBookingQuestionsDTO toDto(PostBookingQuestions postBookingQuestions) {

        if (postBookingQuestions == null) {
            return null;
        }

        PostBookingQuestionsDTO dto = new PostBookingQuestionsDTO();
        dto.setEnabled(postBookingQuestions.getEnabled());

        return dto;
    }
}
