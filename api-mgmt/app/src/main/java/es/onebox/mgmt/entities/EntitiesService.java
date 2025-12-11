package es.onebox.mgmt.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.auth.converter.AuthConverter;
import es.onebox.mgmt.common.auth.dto.AuthConfigDTO;
import es.onebox.mgmt.common.auth.validator.AuthValidator;
import es.onebox.mgmt.datasources.common.dto.AuthConfig;
import es.onebox.mgmt.datasources.common.enums.AccommodationsVendor;
import es.onebox.mgmt.datasources.common.enums.InteractiveVenueType;
import es.onebox.mgmt.datasources.common.enums.StreamingVendor;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.CapacityDTO;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.VenueTemplateDTO;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.VenueTemplateScope;
import es.onebox.mgmt.datasources.integration.avetconfig.repository.AvetConfigRepository;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.AforoInfo;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.InventoriesList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.InventoryDTO;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.RolInfoList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.TermInfoList;
import es.onebox.mgmt.datasources.integration.dispatcher.repository.DispatcherRepository;
import es.onebox.mgmt.datasources.ms.client.dto.AuthVendorEntityConfig;
import es.onebox.mgmt.datasources.ms.client.dto.PhoneValidatorEntityConfig;
import es.onebox.mgmt.datasources.ms.client.repositories.AuthVendorEntityRepository;
import es.onebox.mgmt.datasources.ms.client.repositories.PhoneValidatorEntityRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.AccountSettings;
import es.onebox.mgmt.datasources.ms.entity.dto.ClubConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.DonationProvider;
import es.onebox.mgmt.datasources.ms.entity.dto.DonationProviders;
import es.onebox.mgmt.datasources.ms.entity.dto.Entities;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityAccommodationsConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityCustomers;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityCustomization;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityEmailNotifications;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityInteractiveVenue;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityNotifications;
import es.onebox.mgmt.datasources.ms.entity.dto.EntitySearchFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityStreaming;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTax;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalBarcodeEntityConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueCodeDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.dto.PostBookingQuestions;
import es.onebox.mgmt.datasources.ms.entity.dto.Producers;
import es.onebox.mgmt.datasources.ms.entity.dto.QueueConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.UserLimits;
import es.onebox.mgmt.datasources.ms.entity.dto.user.realm.DonationsConfig;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityStatus;
import es.onebox.mgmt.datasources.ms.entity.enums.ProducerStatus;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.EntityExternalBarcodeConfigRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSearchFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.Events;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.insurance.dto.donationprovider.AvailableCampaigns;
import es.onebox.mgmt.datasources.ms.insurance.repository.ExternalDonationProvidersRepository;
import es.onebox.mgmt.donations.DonationProvidersRepository;
import es.onebox.mgmt.entities.converter.AvailableCampaignsConverter;
import es.onebox.mgmt.entities.converter.CapacityConverter;
import es.onebox.mgmt.entities.converter.EntityConverter;
import es.onebox.mgmt.entities.converter.EntityExternalConverter;
import es.onebox.mgmt.entities.converter.EntitySgaConverter;
import es.onebox.mgmt.entities.converter.UserLimitsConverter;
import es.onebox.mgmt.entities.dto.AccountSettingsDTO;
import es.onebox.mgmt.entities.dto.AvailableCampaignsDTO;
import es.onebox.mgmt.entities.dto.CreateEntityRequestDTO;
import es.onebox.mgmt.entities.dto.DonationsConfigDTO;
import es.onebox.mgmt.entities.dto.EntityAccommodationsConfigDTO;
import es.onebox.mgmt.entities.dto.EntityContactDTO;
import es.onebox.mgmt.entities.dto.EntityDTO;
import es.onebox.mgmt.entities.dto.EntityDonationProviderDTO;
import es.onebox.mgmt.entities.dto.EntityDonationProvidersDTO;
import es.onebox.mgmt.entities.dto.EntityInvoiceDataDTO;
import es.onebox.mgmt.entities.dto.EntitySearchFilterDTO;
import es.onebox.mgmt.entities.dto.EntitySettingsDTO;
import es.onebox.mgmt.entities.dto.EntityTaxApiDTO;
import es.onebox.mgmt.entities.dto.LanguagesDTO;
import es.onebox.mgmt.entities.dto.QueueConfigDTO;
import es.onebox.mgmt.entities.dto.SearchEntitiesResponse;
import es.onebox.mgmt.entities.dto.SearchManagedEntitiesResponse;
import es.onebox.mgmt.entities.dto.SettingsInteractiveVenueDTO;
import es.onebox.mgmt.entities.dto.SettingsLiveStreamingDTO;
import es.onebox.mgmt.entities.dto.SettingsNotificationsDTO;
import es.onebox.mgmt.entities.dto.UpdateEntityRequestDTO;
import es.onebox.mgmt.entities.dto.UserLimitsDTO;
import es.onebox.mgmt.entities.enums.AccommodationsChannelEnablingMode;
import es.onebox.mgmt.entities.enums.CreateEntityType;
import es.onebox.mgmt.entities.enums.EntityType;
import es.onebox.mgmt.entities.externalconfiguration.service.ExternalEntityConfigurationService;
import es.onebox.mgmt.entities.factory.InventoryProviderService;
import es.onebox.mgmt.entities.factory.InventoryProviderServiceFactory;
import es.onebox.mgmt.events.dto.CapacityExternalDTO;
import es.onebox.mgmt.events.dto.LoadedCapacityExternalDTO;
import es.onebox.mgmt.events.dto.UpdateCapacityExternalDTO;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;

@Service
public class EntitiesService {

    private final EntitiesRepository entitiesRepository;
    private final AuthVendorEntityRepository authVendorEntityRepository;
    private final PhoneValidatorEntityRepository phoneValidatorEntityRepository;
    private final EntityExternalBarcodeConfigRepository entityExternalBarcodeConfigRepository;
    private final MasterdataService masterdataService;
    private final SecurityManager securityManager;
    private final AvetConfigRepository avetConfigRepository;
    private final DispatcherRepository dispatcherRepository;
    private final EventsRepository eventsRepository;
    private final ExternalEntityConfigurationService externalEntityConfigurationService;
    private final InventoryProviderServiceFactory inventoryProviderServiceFactory;
    private final DonationProvidersRepository donationProvidersRepository;
    private final ExternalDonationProvidersRepository externalDonationProvidersRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(EntitiesService.class);

    @Autowired
    public EntitiesService(EntitiesRepository entitiesRepository,
                           AuthVendorEntityRepository authVendorEntityRepository,
                           PhoneValidatorEntityRepository phoneValidatorEntityRepository,
                           EntityExternalBarcodeConfigRepository entityExternalBarcodeConfigRepository,
                           MasterdataService masterdataService,
                           SecurityManager securityManager,
                           AvetConfigRepository avetConfigRepository,
                           DispatcherRepository dispatcherRepository,
                           EventsRepository eventsRepository,
                           ExternalEntityConfigurationService externalEntityConfigurationService,
                           InventoryProviderServiceFactory inventoryProviderServiceFactory,
                           DonationProvidersRepository donationProvidersRepository,
                           ExternalDonationProvidersRepository externalDonationProvidersRepository) {
        this.entitiesRepository = entitiesRepository;
        this.authVendorEntityRepository = authVendorEntityRepository;
        this.phoneValidatorEntityRepository = phoneValidatorEntityRepository;
        this.entityExternalBarcodeConfigRepository = entityExternalBarcodeConfigRepository;
        this.masterdataService = masterdataService;
        this.securityManager = securityManager;
        this.avetConfigRepository = avetConfigRepository;
        this.dispatcherRepository = dispatcherRepository;
        this.eventsRepository = eventsRepository;
        this.externalEntityConfigurationService = externalEntityConfigurationService;
        this.inventoryProviderServiceFactory = inventoryProviderServiceFactory;
        this.donationProvidersRepository = donationProvidersRepository;
        this.externalDonationProvidersRepository = externalDonationProvidersRepository;
    }

    public EntityDTO getEntity(Long entityId) {
        securityManager.checkEntityAccessibleIncludeEntityAdmin(entityId);

        Entity entity = entitiesRepository.getEntity(entityId);

        if (entity == null || entity.getState().equals(EntityStatus.DELETED)) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_NOT_FOUND, "No entity found with id: " + entityId, null);
        }

        AuthVendorEntityConfig authVendorEntityConfiguration = authVendorEntityRepository.getAuthVendorEntityConfiguration(entityId);
        PhoneValidatorEntityConfig phoneValidatorEntityConfig = phoneValidatorEntityRepository.getPhoneValidatorEntityConfiguration(entityId);
        ExternalBarcodeEntityConfig externalBarcodeEntityConfig = entityExternalBarcodeConfigRepository.getExternalBarcodeEntityConfig(entityId);
        boolean hasAttributes = CollectionUtils.isNotEmpty(entitiesRepository.getAttributes(entityId, null));

        ExternalConfig externalEntityConfig = null;
        if (BooleanUtils.isTrue(entity.getUseExternalAvetIntegration())) {
            externalEntityConfig = entitiesRepository.getExternalConfig(entity.getId());
        }
        AuthConfig authConfig = entitiesRepository.getAuthConfig(entityId);

        EntityDTO entityDTO = EntityConverter.fromMsEntity(entity, masterdataService.getLanguagesByIds(),
                authVendorEntityConfiguration, phoneValidatorEntityConfig, externalBarcodeEntityConfig, hasAttributes, externalEntityConfig, authConfig);
        fillEntityDTO(entity, entityDTO);

        return entityDTO;
    }

    public SearchEntitiesResponse getEntities(EntitySearchFilterDTO filter) {

        EntitySearchFilter entitySearchFilter = EntityConverter.buildEntitiesFilter(filter);
        if (!SecurityUtils.hasAnyRole(Roles.ROLE_SYS_MGR, Roles.ROLE_SYS_ANS)
                && filter.getOperatorId() != null && !filter.getOperatorId().equals(SecurityUtils.getUserOperatorId())) {
            throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_RESOURCE);
        } else if (!SecurityUtils.hasAnyRole(Roles.ROLE_SYS_MGR, Roles.ROLE_SYS_ANS)
                && SecurityUtils.hasEntityType(EntityTypes.OPERATOR)) {
            entitySearchFilter.setOperatorId(SecurityUtils.getUserOperatorId());
        } else if (SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)) {
            entitySearchFilter.setEntityAdminId(SecurityUtils.getUserEntityId());
        }

        Entities entities = entitiesRepository.getEntities(entitySearchFilter);

        Map<Long, String> languages = masterdataService.getLanguagesByIds();

        SearchEntitiesResponse response = new SearchEntitiesResponse();
        response.setData(entities.getData().stream()
                .map(entity -> {
                    EntityDTO entityDTO = EntityConverter.fromMsEntity(entity, languages);
                    fillEntityDTO(entity, entityDTO);
                    return entityDTO;
                })
				.toList()
        );
        response.setMetadata(entities.getMetadata());

        return response;
    }

    public Long create(CreateEntityRequestDTO entity) {
        validate(entity);
        Entity entityDTO = EntityConverter.toMsEntity(entity);
        entityDTO.setEmail(entity.getEmail());
        entityDTO.setOperator(new Entity(SecurityUtils.getUserOperatorId()));
        entityDTO.setTypes(entity.getTypes().stream().map(e ->
		EntityTypes.valueOf(e.name())).toList());
        entityDTO.setAvetClubCode(entity.getExternalAvetClubCode());

        fillContactData(entity.getContact(), entityDTO);

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String locale = ConverterUtils.checkLanguage(entity.getDefaultLanguage(), languages);
        entityDTO.setLanguage(new IdValueCodeDTO(languages.get(locale)));

        return entitiesRepository.create(entityDTO);
    }

	public void update(Long entityId, UpdateEntityRequestDTO updateRequest) {
        securityManager.checkEntityAccessibleIncludeEntityAdmin(entityId);

		validateUpdate(updateRequest, entityId);

		Entity entityDTO = EntityConverter.toMsEntity(updateRequest);
        entityDTO.setId(entityId);

        if (SecurityUtils.hasEntityType(EntityTypes.OPERATOR)) {
			if (updateRequest.getStatus() != null) {
				entityDTO.setState(EntityStatus.getById(updateRequest.getStatus().getState()));
            }
			fillEntitySettings(updateRequest, entityDTO);
        } else {
			validateInvoiceExternalNotification(updateRequest);
        }

        if (SecurityUtils.hasEntityType(EntityTypes.SUPER_OPERATOR)) {
			fillEntitySettingsSuperOperator(updateRequest, entityDTO);
        }

		fillEntity(updateRequest, entityDTO);
        entitiesRepository.update(entityDTO);

		updateExternalIntegrationSettings(entityId, updateRequest);
    }

	private void validateInvoiceExternalNotification(UpdateEntityRequestDTO entity) {
        if (entity.getInvoiceData() != null && entity.getInvoiceData().getAllowExternalInvoiceNotification() != null) {
            throw new OneboxRestException(ApiMgmtErrorCode.USER_CANNOT_UPDATE_EXTERNAL_INVOICE_NOTIFICATION);
        }
    }

    private void validateUpdate(UpdateEntityRequestDTO entity, Long entityId) {
        if (entity.getSettings() != null) {
			validateInteractiveVenue(entity);
			validateManagedEntities(entity);
			validateAccommodationsConfig(entity);
			validateAccountSettings(entity, entityId);
			validateFeverZoneAllowed(entity, entityId);
			validateGatewayBenefits(entity, entityId);
		}
	}

	private void validateGatewayBenefits(UpdateEntityRequestDTO entity, Long entityId) {
		if (entity.getSettings().getAllowGatewayBenefits() != null) {
			Operator operator = entitiesRepository.getCachedOperator(entityId);
			if (!Boolean.TRUE.equals(operator.getAllowGatewayBenefits())) {
				throw new OneboxRestException(ApiMgmtErrorCode.GATEWAY_BENEFITS_NOT_ALLOWED_BY_OPERATOR);
			}
		}
	}

	private void validateAccountSettings(UpdateEntityRequestDTO entity, Long entityId) {
		if (entity.getSettings().getAccountSettings() != null) {
			AuthConfig authConfig = entitiesRepository.getAuthConfig(entityId);
			if (authConfig == null || !Boolean.TRUE.equals(authConfig.getEnabled())) {
				throw new OneboxRestException((ApiMgmtErrorCode.INVALID_AUTH_CONFIG_ACCOUNT));
			}
		}
	}

	private void validateInteractiveVenue(UpdateEntityRequestDTO entity) {
		SettingsInteractiveVenueDTO interactiveVenue = entity.getSettings().getInteractiveVenue();
		if (interactiveVenue != null && interactiveVenue.getEnabled()
				&& CommonUtils.isEmpty(interactiveVenue.getAllowedVenues())) {
			throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_INTERACTIVE_VENUE_ALLOWED_VENUES_MANDATORY);
		}
	}

	private void validateFeverZoneAllowed(UpdateEntityRequestDTO entity, Long entityId) {
		if (entity.getSettings().getAllowFeverZone() != null) {
			Operator operator = entitiesRepository.getCachedOperator(entityId);
			if (!Boolean.TRUE.equals(operator.getAllowFeverZone())) {
				throw new OneboxRestException((ApiMgmtErrorCode.FEVER_ZONE_NOT_ALLOWED_BY_OPERATOR));
			}
		}
	}

	private void validateManagedEntities(UpdateEntityRequestDTO entity) {
		if (entity.getSettings().getManagedEntities() != null) {
			if (CollectionUtils.isEmpty(entity.getSettings().getManagedEntities())) {
				throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_ADMIN_MANDATORY_MANAGED);
			}
			entity.getSettings().getManagedEntities().forEach(e -> {
				if (e.getId().equals(SecurityUtils.getUserOperatorId())) {
					throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Can't use an operator type",
							null);
				}
				securityManager.checkEntityAccessible(e.getId());
			});
		}
	}

	private void validateAccommodationsConfig(UpdateEntityRequestDTO entity) {
		if (entity.getSettings().getAccommodationsConfig() != null
				&& CommonUtils.isTrue(entity.getSettings().getAccommodationsConfig().getEnabled())) {
			if (CommonUtils.isEmpty(entity.getSettings().getAccommodationsConfig().getAllowedVendors())) {
				throw new OneboxRestException(
						ApiMgmtEntitiesErrorCode.ENTITY_ACCOMMODATIONS_CONFIG_NOT_PROPERLY_ENABLED);
			}
			if (entity.getSettings().getAccommodationsConfig().getChannelEnablingMode() == null) {
				throw new OneboxRestException(
						ApiMgmtEntitiesErrorCode.ENTITY_ACCOMMODATIONS_CONFIG_NOT_PROPERLY_ENABLED);
			}
			if (AccommodationsChannelEnablingMode.RESTRICTED.equals(
					entity.getSettings().getAccommodationsConfig().getChannelEnablingMode())
					&& CommonUtils.isEmpty(entity.getSettings().getAccommodationsConfig().getEnabledChannelIds())) {
				throw new OneboxRestException(
						ApiMgmtEntitiesErrorCode.ENTITY_ACCOMMODATIONS_CONFIG_NOT_PROPERLY_ENABLED);
			}
		}

	}

	private void fillEntitySettings(UpdateEntityRequestDTO in, Entity out) {
        EntitySettingsDTO settings = in.getSettings();
        if (settings != null) {
            out.setUseMultieventCart(settings.getEnableMultieventCart());
            out.setAllowMultiAvetCart(settings.getAllowMultiAvetCart());
            out.setUseActivityEvent(settings.getAllowActivityEvents());
            out.setUseSecondaryMarket(settings.getAllowSecondaryMarket());
            out.setModuleB2BEnabled(settings.getEnableB2B());
            out.setCorporateColor(settings.getCorporateColor());
            out.setAllowDataProtectionFields(settings.getAllowDataProtectionFields());
            out.setAllowVipViews(settings.getAllowVipViews());
            out.setAllowMembers(settings.getAllowMembers());
            out.setAllowDigitalSeasonTicket(settings.getAllowDigitalSeasonTicket());
            out.setAllowTicketHidePrice(settings.getAllowTicketHidePrice());
            out.setManagedEntities(settings.getManagedEntities());
            out.setWhatsappConfig(EntityConverter.toMsEntity(settings.getWhatsappConfig()));
            out.setAllowB2BPublishing(settings.getAllowB2BPublishing());
            out.setAllowInvitations(settings.getAllowInvitations());
            out.setAllowLoyaltyPoints(settings.getAllowLoyaltyPoints());
            out.setAllowFriends(settings.getAllowFriends());
            out.setAllowPngConversion(settings.getAllowPngConversion());
            out.setAllowFeverZone(settings.getAllowFeverZone());
            out.setCustomersDomainSettings(settings.getCustomersDomainSettings());
            out.setAllowGatewayBenefits(settings.getAllowGatewayBenefits());
            out.setMemberIdGeneration(settings.getMemberIdGeneration());
            out.setAllowConfigMultipleTemplates(settings.getAllowConfigMultipleTemplates());
            out.setAllowHardTicketPDF(settings.getAllowHardTicketPDF());

			fillLiveStreaming(out, settings);
			fillInteractiveVenue(out, settings);
			fillNotifications(out, settings);
			fillAccommodationsConfig(out, settings);
            if (!CommonUtils.isEmpty(settings.getDonationsConfigDTO())) {
                Set<DonationsConfig> donationsConfigDTO = settings.getDonationsConfigDTO().stream()
						.map(this::convertToDonationsConfig)
                        .collect(Collectors.toSet());
                out.setDonationsConfig(donationsConfigDTO);
            }
            if(settings.getSessionDuration() != null){
                out.setSessionDuration(in.getSettings().getSessionDuration());
            }
            if(settings.getQueueProvider() != null){
                out.setQueueProvider(settings.getQueueProvider());
            }
            if (settings.getPostBookingQuestions() != null && settings.getPostBookingQuestions().getEnabled() != null) {
                PostBookingQuestions postBookingQuestions = new PostBookingQuestions();
                postBookingQuestions.setEnabled(settings.getPostBookingQuestions().getEnabled());

                out.setPostBookingQuestions(postBookingQuestions);
            }
			fillAccountSettings(out, settings);
			if (settings.getCustomers() != null) {
				EntityCustomers customers = new EntityCustomers();
				customers.setAutoAssignOrders(settings.getCustomers().getAutoAssignOrders());
				out.setCustomers(customers);
			}
		}
	}

	private static void fillLiveStreaming(Entity out, EntitySettingsDTO settings) {
		SettingsLiveStreamingDTO sourceStreaming = settings.getLiveStreaming();
		if (sourceStreaming != null) {
			EntityStreaming streaming = new EntityStreaming();
			streaming.setEnabled(sourceStreaming.getEnabled());
			if (sourceStreaming.getVendors() != null) {
				streaming.setVendor(
						sourceStreaming.getVendors().stream().map(v -> StreamingVendor.valueOf(v.name())).toList());
			}
			out.setStreaming(streaming);
		}
	}

	private static void fillInteractiveVenue(Entity out, EntitySettingsDTO settings) {
		SettingsInteractiveVenueDTO sourceInteractiveVenue = settings.getInteractiveVenue();
		if (sourceInteractiveVenue != null) {
			EntityInteractiveVenue interactiveVenue = new EntityInteractiveVenue();
			interactiveVenue.setEnabled(sourceInteractiveVenue.getEnabled());
			if (sourceInteractiveVenue.getAllowedVenues() != null) {
				interactiveVenue.setAllowedVenues(sourceInteractiveVenue.getAllowedVenues().stream()
						.map(vt -> InteractiveVenueType.valueOf(vt.name())).toList());
			}
			out.setInteractiveVenue(interactiveVenue);
		}
	}

	private static void fillNotifications(Entity out, EntitySettingsDTO settings) {
		SettingsNotificationsDTO sourceNotifications = settings.getNotifications();
		if (sourceNotifications != null) {
			EntityNotifications notifications = new EntityNotifications();
			if (sourceNotifications.getEmail() != null) {
				notifications.setEmail(new EntityEmailNotifications());
				notifications.getEmail().setSendLimit(sourceNotifications.getEmail().getSendLimit());
				out.setAllowMassiveEmail(sourceNotifications.getEmail().getEnabled());
			}
			out.setNotifications(notifications);
		}
	}

	private void fillAccountSettings(Entity out, EntitySettingsDTO settings) {
		AccountSettingsDTO sourceAccount = settings.getAccountSettings();
		if (sourceAccount != null) {
			AccountSettings accountSettings = new AccountSettings();
			if (sourceAccount.getQueueConfig() != null) {
				validateQueueSettings(sourceAccount.getQueueConfig());
				QueueConfig queueConfig = new QueueConfig();
				if (sourceAccount.getQueueConfig().getActive() != null) {
					queueConfig.setActive(sourceAccount.getQueueConfig().getActive());
				}
				if (sourceAccount.getQueueConfig().getAlias() != null) {
					queueConfig.setAlias(sourceAccount.getQueueConfig().getAlias());
				}
				accountSettings.setQueueConfig(queueConfig);
			}
			out.setAccountSettings(accountSettings);
		}
	}

	private void fillAccommodationsConfig(Entity out, EntitySettingsDTO settings) {
		EntityAccommodationsConfigDTO sourceAccommodations = settings.getAccommodationsConfig();
		if (sourceAccommodations != null) {
			EntityAccommodationsConfig accommodations = new EntityAccommodationsConfig();
			accommodations.setEnabled(sourceAccommodations.getEnabled());
			if (!CommonUtils.isEmpty(sourceAccommodations.getAllowedVendors())) {
				accommodations.setAllowedVendors(
						sourceAccommodations.getAllowedVendors().stream()
								.map(vendor -> AccommodationsVendor.valueOf(vendor.name()))
								.toList());
			}
			if (sourceAccommodations.getChannelEnablingMode() != null) {
				accommodations.setChannelEnablingMode(
						es.onebox.mgmt.datasources.common.enums.AccommodationsChannelEnablingMode.valueOf(
								sourceAccommodations.getChannelEnablingMode().name()));
			}
			if (!CommonUtils.isEmpty(sourceAccommodations.getEnabledChannelIds())) {
				accommodations.setEnabledChannelIds(sourceAccommodations.getEnabledChannelIds());
			}
			out.setAccommodationsConfig(accommodations);
		}
	}

	private void validateQueueSettings(QueueConfigDTO queueConfig) {
        if (BooleanUtils.isTrue(queueConfig.getActive()) && StringUtils.isEmpty(queueConfig.getAlias())) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_QUEUE_ALIAS_CONFIG);
        }
    }


	private void fillEntitySettingsSuperOperator(UpdateEntityRequestDTO in, Entity out) {
        EntitySettingsDTO settings = in.getSettings();
        if (settings != null) {
            if (settings.getBiUsers() != null) {
            out.setBasicBIUsersLimit(settings.getBiUsers().getBasicLimit());
                out.setAdvancedBIUsersLimit(settings.getBiUsers().getAdvancedLimit());
            }
            out.setEnableV4Configs(settings.getEnableV4Configs());
        }
    }

    private void updateExternalIntegrationSettings(Long entityId, UpdateEntityRequestDTO entity) {
        if (entity.getSettings() != null && entity.getSettings().getExternalIntegration() != null) {
            if (entity.getSettings().getExternalIntegration().getAuthVendor() != null) {
                AuthVendorEntityConfig authVendorEntityConfig = new AuthVendorEntityConfig();
                authVendorEntityConfig.setAllowed(entity.getSettings().getExternalIntegration().getAuthVendor().getEnabled());
                authVendorEntityConfig.setEntityId(entityId.intValue());
                authVendorEntityConfig.setVendors(entity.getSettings().getExternalIntegration().getAuthVendor().getVendorId());
                authVendorEntityRepository.putAuthVendorEntityConfiguration(entityId, authVendorEntityConfig);
            }

            if (entity.getSettings().getExternalIntegration().getPhoneValidator() != null) {
				PhoneValidatorEntityConfig phoneValidatorEntityConfig = getPhoneValidatorEntityConfig(
						entityId, entity);
                phoneValidatorEntityRepository.updatePhoneValidatorEntityConfiguration(entityId, phoneValidatorEntityConfig);
            }

            if (entity.getSettings().getExternalIntegration().getExternalBarcode() != null) {
                ExternalBarcodeEntityConfig externalBarcodeEntityConfig = new ExternalBarcodeEntityConfig();
                externalBarcodeEntityConfig.setAllowExternalBarcode(entity.getSettings().getExternalIntegration().getExternalBarcode().getEnabled());
                externalBarcodeEntityConfig.setExternalBarcodeFormatId(entity.getSettings().getExternalIntegration().getExternalBarcode().getIntegrationId());
                entityExternalBarcodeConfigRepository.putExternalBarcodeEntityConfig(entityId, externalBarcodeEntityConfig);
            }
        }
    }

	private PhoneValidatorEntityConfig getPhoneValidatorEntityConfig(Long entityId,
			UpdateEntityRequestDTO entity) {
		PhoneValidatorEntityConfig phoneValidatorEntityConfig = new PhoneValidatorEntityConfig();
		phoneValidatorEntityConfig.setEnabled(
				entity.getSettings().getExternalIntegration().getAuthVendor().getEnabled());
		phoneValidatorEntityConfig.setEntityId(entityId.intValue());
		phoneValidatorEntityConfig.setValidatorId(
				entity.getSettings().getExternalIntegration().getPhoneValidator().getValidatorId());
		phoneValidatorEntityConfig.setValidatorIds(
				entity.getSettings().getExternalIntegration().getPhoneValidator().getValidatorIds());
		return phoneValidatorEntityConfig;
	}

    public void delete(Long entityId) {
        securityManager.checkEntityAccessibleIncludeEntityAdmin(entityId);

        Entity entityToDelete = entitiesRepository.getEntity(entityId);
        if (entityId.equals(SecurityUtils.getUserEntityId())) {
            throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_RESOURCE, "user cant remove their own entity", null);
        }
        if (entityToDelete.getTypes().contains(EntityTypes.OPERATOR) ||
                entityToDelete.getTypes().contains(EntityTypes.SUPER_OPERATOR)) {
            throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_RESOURCE, "operator entities cannot be removed", null);
        }

        entityToDelete.setState(EntityStatus.DELETED);
        entitiesRepository.update(entityToDelete);

        Producers producers = entitiesRepository.getProducersByEntityId(entityId);
        if (producers != null && producers.getData() != null) {
            producers.getData().forEach(pr -> {
                if (!pr.getStatus().equals(ProducerStatus.DELETED)) {
                    pr.setStatus(ProducerStatus.DELETED);
                    entitiesRepository.updateProducer(pr);
                }
            });
        }
    }

    public List<EntityType> getAvailableEntityTypes(Long entityId) {
        securityManager.checkEntityAccessibleIncludeEntityAdmin(entityId);

        Entity entity = entitiesRepository.getEntity(entityId);

        //Filter OPERATOR types from available if entity doesnt has already it
        List<EntityTypes> allEntityTypes = new ArrayList<>(Arrays.asList(EntityTypes.values()));
        if (!CommonUtils.isEmpty(entity.getTypes())) {
            if (!entity.getTypes().contains(EntityTypes.OPERATOR)) {
                allEntityTypes.remove(EntityTypes.OPERATOR);
            }
            if (!entity.getTypes().contains(EntityTypes.SUPER_OPERATOR)) {
                allEntityTypes.remove(EntityTypes.SUPER_OPERATOR);
            }
        }

        return allEntityTypes.stream().
				map(EntityConverter::convertEntityTypes).toList();
    }

    public List<EntityType> getEntityTypes(Long entityId) {
        securityManager.checkEntityAccessibleIncludeEntityAdmin(entityId);

        return entitiesRepository.getEntityTypes(entityId).stream().
				map(EntityConverter::convertEntityTypes).toList();
    }

    public void setEntityType(Long entityId, EntityType entityType) {
        securityManager.checkEntityAccessibleIncludeEntityAdmin(entityId);

        if (entityType.equals(EntityType.OPERATOR) || entityType.equals(EntityType.SUPER_OPERATOR)) {
            throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_RESOURCE, "entity cannot set OPERATOR/SUPER_OPERATOR roles", null);
        }

        entitiesRepository.setEntityType(entityId, EntityConverter.convertEntityType(entityType));
    }

    public SearchManagedEntitiesResponse searchManagedEntities(Long entityId, BaseRequestFilter filter){
        securityManager.checkEntityAccessibleIncludeEntityAdmin(entityId);
        securityManager.checkIsEntityAdminEntity(entityId);

        EntitySearchFilter searchFilter = new EntitySearchFilter();
        searchFilter.setEntityAdminId(entityId);
        searchFilter.setLimit(filter.getLimit());
        searchFilter.setOffset(filter.getOffset());

        Entities entities = entitiesRepository.getEntityAdminEntities(searchFilter);

        SearchManagedEntitiesResponse response = new SearchManagedEntitiesResponse();
        response.setData(EntityConverter.fromMs(entities));
        response.setMetadata(entities.getMetadata());
        return response;
    }

    public void unsetEntityType(Long entityId, EntityType entityType) {
        securityManager.checkEntityAccessibleIncludeEntityAdmin(entityId);

        entitiesRepository.unsetEntityType(entityId, EntityConverter.convertEntityType(entityType));
    }

    public List<EntityTaxApiDTO> findTaxes(Long entityId) {
        securityManager.checkEntityAccessibleIncludeEntityAdmin(entityId);

        List<EntityTax> result = entitiesRepository.getTaxes(entityId);
        return EntityConverter.fromDTO(result);
    }


    public List<LoadedCapacityExternalDTO> getLoadedExternalCapacities(Long entityId) {
        externalEntityConfigurationService.validateEntityAndCheckAvet(entityId);
        List<CapacityDTO> capacities = avetConfigRepository.getCapacities(entityId);
		return capacities.stream().map(CapacityConverter::toDTO).toList();
    }

    public List<InventoryDTO> getExternalInventory(Long entityId, String providerId, Boolean skipUsed) {
        if (StringUtils.isEmpty(providerId)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "providerId is mandatory", null);
        }

        securityManager.checkEntityAccessibleIncludeEntityAdmin(entityId);

        InventoryProviderService inventoryProviderService =
                inventoryProviderServiceFactory.getIntegrationService(entityId, providerId);

        InventoriesList inventoriesList = inventoryProviderService.getExternalInventories(entityId, skipUsed);

        return EntitySgaConverter.fromMsInventory(inventoriesList);
    }

    public Set<CapacityExternalDTO> getExternalCapacities(Long entityId) {
        externalEntityConfigurationService.validateEntityAndCheckAvet(entityId);
        List<CapacityDTO> loadedCapacities = avetConfigRepository.getCapacities(entityId);
        List<Integer> enabledCapacities = ListUtils.emptyIfNull(avetConfigRepository.getClubConfigByEntity(entityId).getCapacities());
        List<AforoInfo> availableCapacities;
        try {
            availableCapacities = dispatcherRepository.getAforosInfo(entityId).getAforo();
        } catch (Exception ise) {
			LOGGER.error(
					"Available capacities for entityId {} couldn't be fetched from the dispatcher repository due to the following error: {}",
					entityId, ise.getMessage(), ise);
            availableCapacities = new ArrayList<>();
        }
        // two capacities are equal when their ID match, ignore the rest of properties
        Set<CapacityExternalDTO> result = new TreeSet<>(Comparator.comparing(CapacityExternalDTO::getId));

        availableCapacities.forEach(availableCapacity -> {
            CapacityExternalDTO capacity = new CapacityExternalDTO();
            capacity.setId(availableCapacity.getIdAforo().longValue());
            capacity.setName(availableCapacity.getDescription());
            capacity.setAvailable(true);
            capacity.setEnabled(enabledCapacities.contains(availableCapacity.getIdAforo()));
            CapacityDTO capacityDTO = loadedCapacities.stream().filter(loadedCapacity ->
                    availableCapacity.getIdAforo().equals(Math.abs(loadedCapacity.getCapacityCode()))).findAny().orElse(null);
            capacity.setLoaded(capacityDTO != null);
            if (capacityDTO != null) {
                capacity.setSeasonId(capacityDTO.getSeasonId());
            }
            result.add(capacity);
        });

        // make a second pass in case the dispatcher repository wasn't available or didn't return all the capacities that are loaded
        enabledCapacities.forEach(enabledCapacity -> {
            CapacityExternalDTO capacity = new CapacityExternalDTO();
            capacity.setId(enabledCapacity.longValue());
            // no name can be set, since it's only present when it comes from the dispatcher repository
            capacity.setAvailable(false);
            capacity.setEnabled(true);
            capacity.setLoaded(loadedCapacities.stream().anyMatch(loadedCapacity ->
                    enabledCapacity.equals(Math.abs(loadedCapacity.getCapacityCode()))));
            result.add(capacity);
        });
        return result;
    }

    public void updateCapacityExternal(Long entityId, Integer capacityId, UpdateCapacityExternalDTO updateCapacityExternalDTO) {
        externalEntityConfigurationService.validateEntityAndCheckAvet(entityId);
        avetConfigRepository.putCapacity(entityId, capacityId, updateCapacityExternalDTO);
    }

    public List<IdNameDTO> getExternalPeriodicities(Long entityId) {
        externalEntityConfigurationService.validateEntityAndCheckAvet(entityId);
        if (BooleanUtils.isNotTrue(externalEntityConfigurationService.isMembersEnabled(entityId))) {
            return Collections.emptyList();
        }
        TermInfoList termInfos = dispatcherRepository.getTermsInfo(entityId);
        return EntityExternalConverter.toPeriodicities(termInfos);
    }

    public List<IdNameDTO> getExternalTerms(Long entityId) {
        externalEntityConfigurationService.validateEntityAndCheckAvet(entityId);
        if (BooleanUtils.isNotTrue(externalEntityConfigurationService.isMembersEnabled(entityId))) {
            return Collections.emptyList();
        }
        TermInfoList termInfos = dispatcherRepository.getTermsInfo(entityId);
        return EntityExternalConverter.toTerms(termInfos);
    }

    public List<IdNameDTO> getExternalRoles(Long entityId) {
        externalEntityConfigurationService.validateEntityAndCheckAvet(entityId);
        ClubConfig clubConfig = avetConfigRepository.getClubConfigByEntity(entityId);
        if (clubConfig == null || BooleanUtils.isNotTrue(clubConfig.getMembersEnabled())){
            return Collections.emptyList();
        }
        RolInfoList rolesInfo = dispatcherRepository.getRolesInfo(entityId, clubConfig.getMembersCapacityId());
        return EntityExternalConverter.toRoles(rolesInfo);
    }

    public UserLimitsDTO getUserLimits() {
        UserLimits limits = entitiesRepository.getUserLimits();
        return UserLimitsConverter.fromMs(limits);
    }

    public boolean isCapacityIdFromEntity(Integer capacityId, Long entityId) {
        List<CapacityDTO> capacities = avetConfigRepository.getCapacities(entityId);
        if (CollectionUtils.isEmpty(capacities)) {
            return false;
        } else {
            return capacities.stream().map(CapacityDTO::getId).anyMatch(capacityId::equals);
        }
    }

    public void loadCapacityExternal(Long entityId, Integer capacityId) {
        externalEntityConfigurationService.validateEntityAndCheckAvet(entityId);
        avetConfigRepository.loadCapacity(entityId, capacityId);
    }

    public void updateExternalEntityEvents(Long entityId) {
        externalEntityConfigurationService.validateEntityAndCheckAvet(entityId);
        avetConfigRepository.updateEvents(entityId);
    }

    public void updateExternalEntityEvents(Long entityId, Integer capacityId) {
        externalEntityConfigurationService.validateEntityAndCheckAvet(entityId);
        avetConfigRepository.updateEvents(entityId, capacityId);
    }

    public void deleteCapacityExternal(Long entityId, Integer capacityId) {
        LOGGER.info("[EXTERNAL CAPACITY] - Deleting external capacity for capacityId: {} and entityId: {}", capacityId, entityId);
        externalEntityConfigurationService.validateEntityAndCheckAvet(entityId);
        validateCapacityUsage(entityId, capacityId);
        avetConfigRepository.deleteCapacity(entityId, capacityId);
    }

    public void createCapacityMappings (Long entityId, Integer capacityId) {
        externalEntityConfigurationService.validateEntityAndCheckAvet(entityId);

        List<AforoInfo> availableCapacities = dispatcherRepository.getAforosInfo(entityId).getAforo();
        List<Integer> enabledCapacities = ListUtils.emptyIfNull(avetConfigRepository.getClubConfigByEntity(entityId).getCapacities());
        List<CapacityDTO> loadedCapacities = avetConfigRepository.getCapacities(entityId);

        boolean available = availableCapacities.stream().anyMatch(ac -> ac.getIdAforo().equals(capacityId));
        boolean enabled = enabledCapacities.contains(capacityId);
        boolean loaded = loadedCapacities.stream().anyMatch(lc -> capacityId.equals(Math.abs(lc.getCapacityCode())));

        if (!available){
            throw new OneboxRestException(ApiMgmtErrorCode.AVET_CAPACITY_NOT_ALLOWED);
        }
        if (!enabled){
            throw new OneboxRestException(ApiMgmtErrorCode.AVET_CAPACITY_NOT_ENABLED);
        }
        if (!loaded){
            throw new OneboxRestException(ApiMgmtErrorCode.AVET_CAPACITY_NOT_LOADED);
        }
        avetConfigRepository.createSessionsMappingsFull(entityId, capacityId);
        avetConfigRepository.createSessionsTicketsMappings(entityId, capacityId);
    }

    private void validateCapacityUsage(Long entityId, Integer capacityId) {
        LOGGER.info("[EXTERNAL CAPACITY] - Validating capacity usage for entityId {} and capacityId {}", entityId, capacityId);

        List<VenueTemplateDTO> venueTemplateList = avetConfigRepository.getVenueTemplateIdsByCapacityId(entityId, capacityId);
        if (venueTemplateList.isEmpty()) {
            return;
        }

        List<Integer> entityVenueTemplates = new ArrayList<>();
        List<Integer> eventVenueTemplates = new ArrayList<>();

        venueTemplateList.forEach(venueTemplateDTO -> {
            if(VenueTemplateScope.EVENT.equals(venueTemplateDTO.getScope())){
                eventVenueTemplates.add(venueTemplateDTO.getId().intValue());
            } else {
                entityVenueTemplates.add(venueTemplateDTO.getId().intValue());
            }
        });

        if( CollectionUtils.isNotEmpty(eventVenueTemplates) ){
            EventSearchFilter filter = new EventSearchFilter();
            filter.setVenueTemplateIds(eventVenueTemplates);
            filter.setStatus(es.onebox.mgmt.events.enums.EventStatus.actives().stream().map(Enum::name).toList());
            filter.setOperatorId(SecurityUtils.getUserOperatorId());

            Events events = eventsRepository.getEvents(filter);
            if (events != null && CollectionUtils.isNotEmpty(events.getData())) {
                LOGGER.info("[EXTERNAL CAPACITY] - Capacity is being used by the following events: {}",
						events.getData().stream().map(Event::getId).toList());
                throw new OneboxRestException(ApiMgmtErrorCode.EXTERNAL_CAPACITY_IN_USE_BY_EVENT);
            }

        }

        if( CollectionUtils.isNotEmpty(entityVenueTemplates) ){
            LOGGER.info("[EXTERNAL CAPACITY] - Capacity is being used by the following Venue Templates: {}",
                    entityVenueTemplates);
            throw new OneboxRestException(ApiMgmtErrorCode.EXTERNAL_CAPACITY_IN_USE_BY_VENUE_TEMPLATE);
        }

    }

    public EntityDonationProvidersDTO getActiveDonationProviders(Long entityId) {
        Entity entity = entitiesRepository.getEntity(entityId);
        DonationProviders donationsProviders = donationProvidersRepository.getDonationProviders();

        Map<Long, String> providerMap = donationsProviders.stream()
                .collect(Collectors.toMap(DonationProvider::getId, DonationProvider::getProviderName));

        EntityDonationProvidersDTO donationProvidersDTO = new EntityDonationProvidersDTO();

        entity.getDonationsConfig().stream()
                .filter(DonationsConfig::getEnabled)
                .filter(config -> providerMap.containsKey(config.getProviderId()))
                .forEach(config -> {
                    EntityDonationProviderDTO dto = new EntityDonationProviderDTO();
                    dto.setId(config.getProviderId());
                    dto.setName(providerMap.get(config.getProviderId()));
                    dto.setEnabled(config.getEnabled());
                    donationProvidersDTO.add(dto);
                });

        return donationProvidersDTO;
    }

    public AvailableCampaignsDTO getAvailableCampaigns(Long entityId, Long providerId) {
        AvailableCampaigns availableCampaigns = externalDonationProvidersRepository.getDonationCampaigns(entityId, providerId);
        return AvailableCampaignsConverter.buildAvailableCampaignsDTO(availableCampaigns);
    }

    private void fillEntity(UpdateEntityRequestDTO entity, Entity entityDTO) {
        fillContactData(entity.getContact(), entityDTO);
        fillInvoiceData(entity.getInvoiceData(), entityDTO);
        fillConfigData(entity.getSettings(), entityDTO);
        fillCategoriesData(entity.getSettings(), entityDTO);
        fillCustomizationData(entity.getSettings(), entityDTO);
    }

	private void fillCustomizationData(EntitySettingsDTO settings, Entity entityDTO) {
        if (settings != null && settings.getCustomization() != null) {
            entityDTO.setCustomization(new EntityCustomization());
            entityDTO.getCustomization().setEnabled(settings.getCustomization().getEnabled() );
        }
    }

	private void fillCategoriesData(EntitySettingsDTO settings, Entity entityDTO) {
        if (settings != null && settings.getCategories() != null) {
            entityDTO.setUseCustomCategories(settings.getCategories().getAllowCustomCategories());
            entityDTO.setSelectedCategories(settings.getCategories().getSelected());
        }
    }

    private void fillConfigData(EntitySettingsDTO settings, Entity entityDTO) {
        if (settings != null) {
            Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
            LanguagesDTO entityLanguages = settings.getLanguages();
            if (entityLanguages != null) {
                if (entityLanguages.getDefaultLanguage() != null) {
                    String locale = ConverterUtils.checkLanguage(entityLanguages.getDefaultLanguage(), languages);
                    IdDTO defaultLanguage = new IdDTO(languages.get(locale));
                    entityDTO.setLanguage(new IdValueCodeDTO(defaultLanguage.getId()));
                }
                if (!CommonUtils.isEmpty(entityLanguages.getAvailableLanguages())) {
                    entityDTO.setSelectedLanguages(new ArrayList<>());
                    for (String selectedLang : entityLanguages.getAvailableLanguages()) {
                        String locale = ConverterUtils.checkLanguage(selectedLang, languages);
                        entityDTO.getSelectedLanguages().add(new IdValueCodeDTO(languages.get(locale)));
                    }
                }
            }
        }
    }

    private void fillContactData(EntityContactDTO contact, Entity target) {
        if (contact != null) {
            if (contact.getCountry() != null && contact.getCountry().getCode() != null) {
                target.setCountryId(masterdataService.getCountryIdByCode(contact.getCountry().getCode()));
            }
            if (contact.getCountrySubdivision() != null && contact.getCountrySubdivision().getCode() != null) {
                target.setCountrySubdivisionId(masterdataService.getCountrySubdivisionIdByCode(contact.getCountrySubdivision().getCode()));
            }
        }
    }

    private void fillInvoiceData(EntityInvoiceDataDTO invoiceData, Entity entityDTO) {
        if (invoiceData != null) {
            if (invoiceData.getCountry() != null && invoiceData.getCountry().getCode() != null) {
                entityDTO.setInvoiceCountryId(masterdataService.getCountryIdByCode(invoiceData.getCountry().getCode()));
            }
            if (invoiceData.getCountrySubdivision() != null && invoiceData.getCountrySubdivision().getCode() != null) {
                entityDTO.setInvoiceCountrySubdivisionId(masterdataService.getCountrySubdivisionIdByCode(invoiceData.getCountrySubdivision().getCode()));
            }
            if (invoiceData.getAllowExternalInvoiceNotification() != null) {
                entityDTO.setAllowExternalInvoiceNotification(invoiceData.getAllowExternalInvoiceNotification());
            }
        }
    }

    private void fillEntityDTO(Entity entity, EntityDTO entityDTO) {
        if (entity.getCountryId() != null) {
            MasterdataValue country = masterdataService.getCountry(entity.getCountryId().longValue());
            entityDTO.getContact().setCountry(new CodeNameDTO(country.getCode(), null));
        }
        if (entity.getCountrySubdivisionId() != null) {
            MasterdataValue country = masterdataService.getCountrySubdivision(entity.getCountrySubdivisionId().longValue());
            entityDTO.getContact().setCountrySubdivision(new CodeNameDTO(country.getCode(), null));
        }
        if (entity.getInvoiceCountryId() != null) {
            MasterdataValue country = masterdataService.getCountry(entity.getInvoiceCountryId().longValue());
            initializeInvoiceData(entityDTO);
            entityDTO.getInvoiceData().setCountry(new CodeNameDTO(country.getCode(), null));
        }
        if (entity.getInvoiceCountrySubdivisionId() != null) {
            MasterdataValue country = masterdataService.getCountrySubdivision(entity.getInvoiceCountrySubdivisionId().longValue());
            initializeInvoiceData(entityDTO);
            entityDTO.getInvoiceData().setCountrySubdivision(new CodeNameDTO(country.getCode(), null));
        }
        if (entity.getAllowExternalInvoiceNotification() != null) {
            initializeInvoiceData(entityDTO);
            entityDTO.getInvoiceData().setAllowExternalInvoiceNotification(entity.getAllowExternalInvoiceNotification());
        }
        if(entity.getSessionDuration() != null){
            entityDTO.getSettings().setSessionDuration(entity.getSessionDuration());
        }
    }

    private void initializeInvoiceData(EntityDTO entityDTO) {
        if (entityDTO.getInvoiceData() == null) {
            entityDTO.setInvoiceData(new EntityInvoiceDataDTO());
        }
    }

    private void validate(CreateEntityRequestDTO request) {
        if (request.getTypes().contains(CreateEntityType.ENTITY_ADMIN)) {
            if (request.getTypes().size() > 1) {
                throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_ADMIN_TYPES_CONFLICT);
            }
            if (request.getSettings() != null && CollectionUtils.isNotEmpty(request.getSettings().getManagedEntities())) {
                request.getSettings().getManagedEntities().forEach(entity -> securityManager.checkEntityAccessible(entity.getId()));
            } else {
                throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_ADMIN_MANDATORY_MANAGED);
            }
        }
    }

	private DonationsConfig convertToDonationsConfig(DonationsConfigDTO dto) {
        DonationsConfig config = new DonationsConfig();
        config.setEnabled(dto.getEnabled());
        config.setApiKey(dto.getApiKey());
        config.setProviderId(dto.getProviderId());
        return config;
    }

    public AuthConfigDTO getAuthConfig(Long entityId) {
        securityManager.checkEntityAccessible(entityId);
        AuthConfig authConfig = entitiesRepository.getAuthConfig(entityId);
        PhoneValidatorEntityConfig phoneValidatorEntityConfig = phoneValidatorEntityRepository.getPhoneValidatorEntityConfiguration(entityId);
        return AuthConverter.toAuthConfigDTO(authConfig, phoneValidatorEntityConfig);
    }

    public void updateAuthConfig(Long entityId, AuthConfigDTO authConfigDTO) {
        securityManager.checkEntityAccessible(entityId);
        authConfigDTO.setUseEntityConfig(null);
        AuthValidator.validateAuthConfig(authConfigDTO);
        AuthConfig updatedAuthConfig = AuthConverter.toAuthConfig(authConfigDTO);
        entitiesRepository.updateAuthConfig(entityId, updatedAuthConfig);
        if(authConfigDTO.getSettings() != null && authConfigDTO.getSettings().getPhoneValidator() != null) {
            PhoneValidatorEntityConfig phoneValidatorEntityConfig = new PhoneValidatorEntityConfig();
            phoneValidatorEntityConfig.setEntityId(entityId.intValue());
            phoneValidatorEntityConfig.setEnabled(authConfigDTO.getSettings().getPhoneValidator().getEnabled());
            phoneValidatorEntityConfig.setValidatorId(authConfigDTO.getSettings().getPhoneValidator().getValidatorId());
            phoneValidatorEntityRepository.updatePhoneValidatorEntityConfiguration(entityId, phoneValidatorEntityConfig);
        }
    }

}
