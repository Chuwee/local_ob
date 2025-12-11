package es.onebox.mgmt.events.converter;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.common.NameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.categories.CategoryDTO;
import es.onebox.mgmt.common.CategoriesDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.accommodations.AccommodationsVendor;
import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.common.dto.ContactData;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.InvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.event.dto.event.Booking;
import es.onebox.mgmt.datasources.ms.event.dto.event.Category;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChangeSeatAllowedSessions;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChangeSeatAmountType;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChangeSeatChangeType;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChangeSeatExpiryTime;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChangeSeatNewTicketSelection;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChangeSeatPrice;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChangeSeatReallocationChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChangeSeatRefund;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChangeSeatRefundType;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChangeSeatTickets;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChangeSeatVoucherExpiry;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateEventData;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventAccommodationsConfig;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChangeSeat;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChangeSeatExpiry;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventExternalConfig;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventGroupPriceType;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSearchFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSeatSelection;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSessionSelection;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSessionSettings;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTransferTicket;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventVenueViewConfig;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventWhitelabelSettings;
import es.onebox.mgmt.datasources.ms.event.dto.event.SessionCalendar;
import es.onebox.mgmt.datasources.ms.event.dto.event.SessionList;
import es.onebox.mgmt.datasources.ms.event.dto.event.SessionPackType;
import es.onebox.mgmt.datasources.ms.event.dto.event.SessionTypeExpiration;
import es.onebox.mgmt.datasources.ms.event.dto.event.TaxMode;
import es.onebox.mgmt.datasources.ms.event.dto.event.TimespanOrderExpire;
import es.onebox.mgmt.datasources.ms.event.dto.event.TimespanSessionExpire;
import es.onebox.mgmt.datasources.ms.event.dto.event.Venue;
import es.onebox.mgmt.entities.factory.InventoryProviderEnum;
import es.onebox.mgmt.events.dto.AdditionalConfigDTO;
import es.onebox.mgmt.events.dto.BaseEventDTO;
import es.onebox.mgmt.events.dto.BookingExpiration;
import es.onebox.mgmt.events.dto.BookingOrderExpiration;
import es.onebox.mgmt.events.dto.BookingSessionExpiration;
import es.onebox.mgmt.events.dto.BookingSettingsDTO;
import es.onebox.mgmt.events.dto.ChangeSeatAllowedSessionsDTO;
import es.onebox.mgmt.events.dto.ChangeSeatAmountTypeDTO;
import es.onebox.mgmt.events.dto.ChangeSeatChangeTypeDTO;
import es.onebox.mgmt.events.dto.ChangeSeatExpiryTimeDTO;
import es.onebox.mgmt.events.dto.ChangeSeatNewTicketSelectionDTO;
import es.onebox.mgmt.events.dto.ChangeSeatPriceDTO;
import es.onebox.mgmt.events.dto.ChangeSeatReallocationChannelDTO;
import es.onebox.mgmt.events.dto.ChangeSeatRefundDTO;
import es.onebox.mgmt.events.dto.ChangeSeatTicketsDTO;
import es.onebox.mgmt.events.dto.ChangeSeatVoucherExpiryDTO;
import es.onebox.mgmt.events.dto.EventAccommodationsConfigDTO;
import es.onebox.mgmt.events.dto.EventChangeSeatExpiryDTO;
import es.onebox.mgmt.events.dto.EventChangeSeatSettingsDTO;
import es.onebox.mgmt.events.dto.EventContactDTO;
import es.onebox.mgmt.events.dto.EventDTO;
import es.onebox.mgmt.events.dto.EventExternalConfigDTO;
import es.onebox.mgmt.events.dto.EventSearchFilterDTO;
import es.onebox.mgmt.events.dto.EventSettingsDTO;
import es.onebox.mgmt.events.dto.EventSettingsGroupsDTO;
import es.onebox.mgmt.events.dto.EventTransferTicketDTO;
import es.onebox.mgmt.events.dto.EventUISeatSelectionDTO;
import es.onebox.mgmt.events.dto.EventUISessionSelectionDTO;
import es.onebox.mgmt.events.dto.EventUISessionSettingsDTO;
import es.onebox.mgmt.events.dto.EventUISettingsDTO;
import es.onebox.mgmt.events.dto.EventVenueDTO;
import es.onebox.mgmt.events.dto.EventVenueTemplateDTO;
import es.onebox.mgmt.events.dto.EventWhitelabelSettingsDTO;
import es.onebox.mgmt.events.dto.InvoicePrefixDTO;
import es.onebox.mgmt.events.dto.LanguagesDTO;
import es.onebox.mgmt.events.dto.RefundTypeDTO;
import es.onebox.mgmt.events.dto.SalesGoalDTO;
import es.onebox.mgmt.events.dto.SessionSelectCalendarDTO;
import es.onebox.mgmt.events.dto.SessionSelectListDTO;
import es.onebox.mgmt.events.dto.SettingsInteractiveVenueDTO;
import es.onebox.mgmt.events.dto.UpdateEventRequestDTO;
import es.onebox.mgmt.events.dto.UpdateEventSettingsDTO;
import es.onebox.mgmt.events.enums.EventAvetConfigType;
import es.onebox.mgmt.events.enums.EventField;
import es.onebox.mgmt.events.enums.EventGroupPricePolicy;
import es.onebox.mgmt.events.enums.EventSessionPack;
import es.onebox.mgmt.events.enums.EventStatus;
import es.onebox.mgmt.events.enums.EventType;
import es.onebox.mgmt.events.enums.SessionSelectType;
import es.onebox.mgmt.events.enums.SessionTimeFrame;
import es.onebox.mgmt.events.enums.TaxModeDTO;
import es.onebox.mgmt.events.enums.TimespanBookingOrderExpiration;
import es.onebox.mgmt.events.tours.dto.TourSettingsDTO;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.dto.EventSubscriptionListDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EventConverter {

    public static final String CONFIG_INVENTORY_PROVIDER = "inventoryProvider";
    public static final String EXTERNAL_EVENT_ID = "externalEventId";
    public static final String VENUE_TEMPLATE_ID = "venueTemplateId";
    public static final String STANDALONE = "standalone";

    private EventConverter() {
    }

    public static EventDTO fromMsEvent(Event event, List<Currency> currencies) {
        if (event == null) {
            return null;
        }

        return fromMsEvent(event, new EventDTO(), currencies);
    }

    public static EventSearchFilter toMsEvent(EventSearchFilterDTO source, Function<String, Integer> countryIdGetter, List<Currency> currencies) {
        EventSearchFilter target = new EventSearchFilter();
        target.setOperatorId(SecurityUtils.getUserOperatorId());
        target.setEntityId(source.getEntityId());
        target.setEntityAdminId(source.getEntityAdminId());
        target.setProducerId(source.getProducerId());
        target.setVenueId(source.getVenueId());
        target.setCity(source.getCity());
        target.setIncludeArchived(BooleanUtils.isTrue(source.getIncludeArchived()));
        target.setFreeSearch(StringUtils.isNotBlank(source.getFreeSearch()) ? source.getFreeSearch() : null);
        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());
        target.setSort(ConverterUtils.checkSortFields(source.getSort(), EventField::byName));
        target.setFields(ConverterUtils.checkFilterFields(source.getFields(), EventField::byName));
        target.setLimit(source.getLimit());
        target.setOffset(source.getOffset());

        if (source.getCurrencyCode() != null) {
            target.setCurrencyId(CurrenciesUtils.getCurrencyId(currencies, source.getCurrencyCode()));
        }
        if (source.getCountry() != null) {
            target.setCountryId(countryIdGetter.apply(source.getCountry()).longValue());
        }
        if (CollectionUtils.isNotEmpty(source.getStatus())) {
            target.setStatus(source.getStatus().stream().map(Enum::name).toList());
        }
        if (CollectionUtils.isNotEmpty(source.getType())) {
            target.setType(source.getType().stream().map(Enum::name).toList());
        } else {
            target.setType(Arrays.stream(EventType.values()).map(Enum::name).toList());
        }

        return target;
    }

    public static void addInvoicePrefix(EventDTO eventDTO, InvoicePrefix invoicePrefix) {
        InvoicePrefixDTO invoicePrefixDTO = new InvoicePrefixDTO();
        invoicePrefixDTO.setPrefix(invoicePrefix.getPrefix());
        invoicePrefixDTO.setSuffix(invoicePrefix.getSuffix());
        invoicePrefixDTO.setId(invoicePrefix.getId());
        invoicePrefixDTO.setProducerId(invoicePrefix.getProducerId());
        eventDTO.getSettings().setInvoicePrefix(invoicePrefixDTO);
    }

    public static BaseEventDTO fromMsEvent(Event event, BaseEventDTO target, List<Currency> currencies) {
        if (event != null) {
            fromMsEventBaseFields(event, target);

            if (event.getEntityId() != null) {
                target.setEntity(new IdNameDTO());
                target.getEntity().setId(event.getEntityId());
                target.getEntity().setName(event.getEntityName());
            }
            if (event.getProducer() != null && event.getProducer().getId() != null) {
                target.setProducer(new IdNameDTO());
                target.getProducer().setId(event.getProducer().getId());
                target.getProducer().setName(event.getProducer().getName());
            }
            if (!CommonUtils.isEmpty(event.getVenues())) {
                target.setVenueTemplates(new ArrayList<>());
                for (Venue venue : event.getVenues()) {
                    EventVenueTemplateDTO targetVenue = new EventVenueTemplateDTO();
                    targetVenue.setId(venue.getConfigId());
                    targetVenue.setName(venue.getConfigName());
                    List<NameDTO> accessControlSystems = null;
                    if (CollectionUtils.isNotEmpty(venue.getAccessControlSystems())) {
                        accessControlSystems = venue.getAccessControlSystems()
                                .stream().map(accessControlSystem -> new NameDTO(accessControlSystem.name())).toList();
                    }
                    targetVenue.setVenue(new EventVenueDTO(venue.getId(), venue.getName(), venue.getCity(), venue.getGooglePlaceId(), accessControlSystems));
                    target.getVenueTemplates().add(targetVenue);
                }
            }
            if (event.getCurrencyId() != null) {
                target.setCurrencyCode(
                        currencies != null
                                ? currencies.stream()
                                .filter(currency -> currency.getId().equals(event.getCurrencyId()))
                                .findFirst().map(Currency::getCode)
                                .orElse(null)
                                : null
                );
            }
        }
        return target;
    }

    public static EventDTO fromMsEvent(Event event, EventDTO target, List<Currency> currencies) {
        fromMsEvent(event, (BaseEventDTO) target, currencies);

        if (event != null) {
            target.setSettings(fillSettings(event));

            if (event.getContactPersonEmail() != null
                    || event.getContactPersonName() != null
                    || event.getContactPersonPhone() != null
                    || event.getContactPersonSurname() != null) {
                target.setContact(new EventContactDTO());
                target.getContact().setEmail(event.getContactPersonEmail());
                target.getContact().setName(event.getContactPersonName());
                target.getContact().setSurname(event.getContactPersonSurname());
                target.getContact().setPhoneNumber(event.getContactPersonPhone());
            }
            AdditionalConfigDTO additionalConfigDTO = new AdditionalConfigDTO();
            if (event.getInventoryProvider() != null
                    && InventoryProviderEnum.getByCode(event.getInventoryProvider().name()) != null) {
                additionalConfigDTO.setInventoryProvider(InventoryProviderEnum.getByCode(event.getInventoryProvider().name()));
            }
            if (es.onebox.mgmt.datasources.ms.event.dto.event.EventType.AVET.equals(event.getType())) {
                additionalConfigDTO.setAvetConfig(EventAvetConfigType.valueOf(event.getAvetConfig().name()));
                if (MapUtils.isNotEmpty(event.getExternalData())) {
                    target.setExternalData(event.getExternalData());
                }
            }
            if(event.getInventoryProvider() != null && MapUtils.isNotEmpty(event.getExternalData())) {
                target.setExternalData(event.getExternalData());
            }
            target.setAdditionalConfig(additionalConfigDTO);
        }

        return target;
    }

    private static void fromMsEventBaseFields(Event event, BaseEventDTO target) {
        target.setId(event.getId());
        target.setName(event.getName());
        target.setReference(event.getPromoterReference());
        target.setArchived(event.getArchived());
        target.setExternalReference(event.getExternalReference());
        if (event.getType() != null) {
            if (es.onebox.mgmt.datasources.ms.event.dto.event.EventType.PRODUCT.equals(event.getType())) {
                throw ExceptionBuilder.build(CoreErrorCode.NOT_FOUND);
            }
            target.setType(EventType.valueOf(event.getType().name()));
        }
        if (event.getStatus() != null) {
            target.setStatus(EventStatus.valueOf(event.getStatus().name()));
        }
        if (event.getDate() != null) {
            if (event.getDate().getStart() != null) {
                target.setStartDate(event.getDate().getStart().getDateTime());
                target.setStartDateTZ(event.getDate().getStart().getTimeZone().getOlsonId());
            }
            if (event.getDate().getEnd() != null) {
                target.setEndDate(event.getDate().getEnd().getDateTime());
                target.setEndDateTZ(event.getDate().getEnd().getTimeZone().getOlsonId());
            }
        }
        if (event.getPhoneVerificationRequired() != null) {
            target.setPhoneVerificationRequired(event.getPhoneVerificationRequired());
        }
        if (event.getAttendantVerificationRequired() != null) {
            target.setAttendantVerificationRequired(event.getAttendantVerificationRequired());
        }
    }

    public static Event toMsEvent(UpdateEventRequestDTO source) {
        Event target = new Event();
        target.setName(source.getName());
        target.setPromoterReference(source.getReference());
        target.setArchived(source.getArchived());
        if (source.getStatus() != null) {
            target.setStatus(es.onebox.mgmt.datasources.ms.event.dto.event.EventStatus.valueOf(source.getStatus().name()));
        }
        if (source.getContact() != null) {
            target.setContactPersonName(source.getContact().getName());
            target.setContactPersonSurname(source.getContact().getSurname());
            target.setContactPersonEmail(source.getContact().getEmail());
            target.setContactPersonPhone(source.getContact().getPhoneNumber());
        }
        if (source.getSettings() != null) {
            fillSettings(source, target);
        }
        target.setPhoneVerificationRequired(source.getPhoneVerificationRequired());
        target.setAttendantVerificationRequired(source.getAttendantVerificationRequired());

        return target;
    }

    private static EventSettingsDTO fillSettings(Event event) {
        EventSettingsDTO settings = new EventSettingsDTO();

        if (event.getCategory() != null || event.getCustomCategory() != null) {
            settings.setCategories(new CategoriesDTO());
            if (event.getCategory() != null) {
                settings.getCategories().setBase(new CategoryDTO());
                settings.getCategories().getBase().setId(event.getCategory().getId());
                settings.getCategories().getBase().setDescription(event.getCategory().getDescription());
                settings.getCategories().getBase().setCode(event.getCategory().getCode());
            }
            if (event.getCustomCategory() != null) {
                settings.getCategories().setCustom(new CategoryDTO());
                settings.getCategories().getCustom().setId(event.getCustomCategory().getId());
                settings.getCategories().getCustom().setDescription(event.getCustomCategory().getDescription());
                settings.getCategories().getCustom().setCode(event.getCustomCategory().getCode());
            }
        }
        if (event.getSalesGoalTickets() != null || event.getSalesGoalRevenue() != null) {
            settings.setSalesGoal(new SalesGoalDTO());
            settings.getSalesGoal().setTickets(event.getSalesGoalTickets());
            settings.getSalesGoal().setRevenue(
                    Optional.ofNullable(event.getSalesGoalRevenue()).map(BigDecimal::new).orElse(null));
        }
        if (!CommonUtils.isEmpty(event.getLanguages())) {
            settings.setLanguages(new LanguagesDTO());
            settings.getLanguages().setSelected(
                    event.getLanguages().stream().map(eventLanguage -> {
                        String langCode = ConverterUtils.toLanguageTag(eventLanguage.getCode());
                        if (CommonUtils.isTrue(eventLanguage.getDefault())) {
                            settings.getLanguages().setDefaultLanguage(langCode);
                        }
                        return langCode;
                    }).collect(Collectors.toList()));
        }
        if (event.getSessionPackType() != null) {
            settings.setSessionPack(EventSessionPack.valueOf(event.getSessionPackType().name()));
        }
        settings.setTour(new TourSettingsDTO(event.getTour() != null ? event.getTour().getId() : null));
        settings.setAllowVenueReports(event.getAllowVenueReport());
        settings.setUseProducerFiscalData(event.getUseProducerFiscalData());
        settings.setUseTieredPricing(event.getUseTieredPricing());
        settings.setBookings(bookingFromMsEvent(event));
        settings.setInvitationUseTicketTemplate(event.getInvitationUseTicketTemplate());
        settings.setIsFestival(event.getSupraEvent());
        if (event.getEnableSubscriptionList() != null || event.getSubscriptionListId() != null) {
            EventSubscriptionListDTO subscriptionListDTO = new EventSubscriptionListDTO();
            subscriptionListDTO.setEnable(event.getEnableSubscriptionList());
            subscriptionListDTO.setId(event.getSubscriptionListId());
            settings.setSubscriptionList(subscriptionListDTO);
        }

        EventSettingsGroupsDTO groups = new EventSettingsGroupsDTO();
        groups.setAllowed(event.getAllowGroups());
        if (CommonUtils.isTrue(groups.getAllowed())) {
            groups.setPricePolicy(EventGroupPricePolicy.fromId(event.getGroupPrice()));
            if (EventGroupPricePolicy.INDIVIDUAL.equals(groups.getPricePolicy())) {
                groups.setCompanionsPayment(CommonUtils.isTrue(event.getGroupCompanionPayment()));
            }
        }
        settings.setGroups(groups);

        SettingsInteractiveVenueDTO interactiveVenue = new SettingsInteractiveVenueDTO();
        if (event.getEventVenueViewConfig() != null) {
            EventVenueViewConfig venueViewConfig = event.getEventVenueViewConfig();

            interactiveVenue.setAllowSector3dView(event.getEventVenueViewConfig().isUseSector3dView());
            interactiveVenue.setAllowSeat3dView(event.getEventVenueViewConfig().isUseSeat3dView());

            if (venueViewConfig.getInteractiveVenueType() != null){
                interactiveVenue.setAllowInteractiveVenue(Boolean.TRUE);
                interactiveVenue.setInteractiveVenueType(venueViewConfig.getInteractiveVenueType());
                if (InteractiveVenueType.VENUE_3D_MMC_V1.equals(venueViewConfig.getInteractiveVenueType())) {
                    interactiveVenue.setAllowVenue3dView(BooleanUtils.isTrue(venueViewConfig.isUse3dVenueModule()));
                } else if (InteractiveVenueType.VENUE_3D_MMC_V2.equals(venueViewConfig.getInteractiveVenueType())) {
                    interactiveVenue.setAllowVenue3dView(BooleanUtils.isTrue(venueViewConfig.isUse3dVenueModuleV2()));
                } else if (InteractiveVenueType.VENUE_3D_PACIFA.equals(venueViewConfig.getInteractiveVenueType())) {
                    interactiveVenue.setAllowVenue3dView(BooleanUtils.isTrue(venueViewConfig.isUseVenue3dView()));
                }
            } else {
                interactiveVenue.setAllowVenue3dView(Boolean.FALSE);
                interactiveVenue.setAllowInteractiveVenue(Boolean.FALSE);

                if (BooleanUtils.isTrue(interactiveVenue.getAllowSeat3dView())
                        || BooleanUtils.isTrue(interactiveVenue.getAllowSector3dView())) {
                    interactiveVenue.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_MMC_V2);
                }
                if (BooleanUtils.isTrue(venueViewConfig.isUse3dVenueModule())){
                    interactiveVenue.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_MMC_V1);
                    interactiveVenue.setAllowVenue3dView(Boolean.TRUE);
                } else if (BooleanUtils.isTrue(venueViewConfig.isUse3dVenueModuleV2())){
                    interactiveVenue.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_MMC_V2);
                    interactiveVenue.setAllowVenue3dView(Boolean.TRUE);
                }
                if ((BooleanUtils.isTrue(venueViewConfig.isUseVenue3dView()) && BooleanUtils.isFalse(venueViewConfig.isUse3dVenueModule()))
                        || (BooleanUtils.isTrue(venueViewConfig.isUseVenue3dView()) && BooleanUtils.isFalse(venueViewConfig.isUse3dVenueModuleV2()))){
                    interactiveVenue.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_PACIFA);
                    interactiveVenue.setAllowVenue3dView(Boolean.TRUE);
                }

                if (BooleanUtils.isTrue(interactiveVenue.getAllowSeat3dView())
                        || BooleanUtils.isTrue(interactiveVenue.getAllowSector3dView())
                        || BooleanUtils.isTrue(interactiveVenue.getAllowVenue3dView())) {
                    interactiveVenue.setAllowInteractiveVenue(Boolean.TRUE);
                }
            }

            settings.setInteractiveVenue(interactiveVenue);

        } else {
            interactiveVenue.setAllowInteractiveVenue(Boolean.FALSE);
            settings.setInteractiveVenue(interactiveVenue);
        }

        if (event.getAccommodationsConfig() != null) {
            EventAccommodationsConfigDTO accommodationsConfig = new EventAccommodationsConfigDTO();
            accommodationsConfig.setEnabled(event.getAccommodationsConfig().getEnabled());
            if (event.getAccommodationsConfig().getVendor() != null) {
                accommodationsConfig.setVendor(AccommodationsVendor.valueOf(event.getAccommodationsConfig().getVendor().name()));
            }
            accommodationsConfig.setValue(event.getAccommodationsConfig().getValue());
            settings.setAccommodationsConfig(accommodationsConfig);
        }

        if (event.getWhitelabelSettings() != null) {
            settings.setEventWhiteLabelSettings(fromMs(event.getWhitelabelSettings()));
        }
        if (event.getEventExternalConfig() != null) {
            settings.setEventExternalConfig(fromMs(event.getEventExternalConfig()));
        }
        if (event.getAllowChangeSeat() != null || event.getChangeSeat() != null) {
            EventChangeSeatSettingsDTO changeSeatSettings = new EventChangeSeatSettingsDTO();
            changeSeatSettings.setEnable(event.getAllowChangeSeat());
            if (event.getChangeSeat() != null) {
                changeSeatSettings.setEventChangeSeatExpiry(fromMsEvent(event.getChangeSeat().getEventChangeSeatExpiry()));
                if (event.getChangeSeat().getChangeType() != null) {
                    changeSeatSettings.setChangeType(ChangeSeatChangeTypeDTO.byId(event.getChangeSeat().getChangeType().getId()));
                }
                changeSeatSettings.setNewTicketSelection(fromMsEvent(event.getChangeSeat().getNewTicketSelection()));
                changeSeatSettings.setReallocationChannel(fromMsEvent(event.getChangeSeat().getReallocationChannel()));
            }
            settings.setEventChangeSeatSettings(changeSeatSettings);
        }

        if (event.getAllowTransferTicket() != null) {
            EventTransferTicketDTO eventTransferTicketDTO = fromMsEvent(event);
            settings.setEventTransferTicket(eventTransferTicketDTO);
        }

        settings.setTaxMode(TaxModeDTO.fromMs(event.getTaxMode()));
        return settings;
    }

    private static EventChangeSeatExpiryDTO fromMsEvent(EventChangeSeatExpiry eventChangeSeatExpiry) {
        if (eventChangeSeatExpiry == null) {
            return null;
        } else {
            EventChangeSeatExpiryDTO changeSeatExpiryDTO = new EventChangeSeatExpiryDTO();
            changeSeatExpiryDTO.setTimeOffsetLimitAmount(eventChangeSeatExpiry.getTimeOffsetLimitAmount());
            changeSeatExpiryDTO.setTimeOffsetLimitUnit(eventChangeSeatExpiry.getTimeOffsetLimitUnit());
            return changeSeatExpiryDTO;
        }
    }

    private static ChangeSeatNewTicketSelectionDTO fromMsEvent(ChangeSeatNewTicketSelection changeSeatNewTicketSelection) {

        if (changeSeatNewTicketSelection == null) {
            return null;
        } else {
            ChangeSeatNewTicketSelectionDTO changeSeatNewTicketSelectionDTO = new ChangeSeatNewTicketSelectionDTO();
            changeSeatNewTicketSelectionDTO.setAllowedSessions(ChangeSeatAllowedSessionsDTO.byId(changeSeatNewTicketSelection.getAllowedSessions().getId()));
            changeSeatNewTicketSelectionDTO.setSameDateOnly(changeSeatNewTicketSelection.getSameDateOnly());
            changeSeatNewTicketSelectionDTO.setTickets(ChangeSeatTicketsDTO.byId(changeSeatNewTicketSelection.getTickets().getId()));
            ChangeSeatPriceDTO changeSeatPriceDTO = new ChangeSeatPriceDTO();
            changeSeatPriceDTO.setType(ChangeSeatAmountTypeDTO.byId(changeSeatNewTicketSelection.getPrice().getType().getId()));
            if (changeSeatNewTicketSelection.getPrice().getRefund() != null) {
                ChangeSeatRefundDTO changeSeatRefundDTO = new ChangeSeatRefundDTO();
                ChangeSeatRefund changeSeatRefund = changeSeatNewTicketSelection.getPrice().getRefund();
                changeSeatRefundDTO.setType(RefundTypeDTO.byId(changeSeatRefund.getType().getId()));
                if (changeSeatRefund.getVoucherExpiry() != null) {
                    ChangeSeatVoucherExpiryDTO changeSeatVoucherExpiryDTO = new ChangeSeatVoucherExpiryDTO();
                    changeSeatVoucherExpiryDTO.setEnabled(changeSeatRefund.getVoucherExpiry().getEnabled());
                    if (changeSeatRefund.getVoucherExpiry().getExpiryTime() != null) {
                        ChangeSeatExpiryTimeDTO changeSeatExpiryTimeDTO = new ChangeSeatExpiryTimeDTO();
                        changeSeatExpiryTimeDTO.setTimeOffsetLimitAmount(changeSeatRefund.getVoucherExpiry().getExpiryTime().getTimeOffsetLimitAmount());
                        changeSeatExpiryTimeDTO.setTimeOffsetLimitUnit(changeSeatRefund.getVoucherExpiry().getExpiryTime().getTimeOffsetLimitUnit());
                        changeSeatVoucherExpiryDTO.setExpiryTime(changeSeatExpiryTimeDTO);
                    }
                    changeSeatRefundDTO.setVoucherExpiry(changeSeatVoucherExpiryDTO);
                }
                changeSeatPriceDTO.setRefund(changeSeatRefundDTO);
            }
            changeSeatNewTicketSelectionDTO.setPrice(changeSeatPriceDTO);

            return changeSeatNewTicketSelectionDTO;
        }
    }

    private static ChangeSeatReallocationChannelDTO fromMsEvent(ChangeSeatReallocationChannel reallocationChannel) {
        if (reallocationChannel == null) {
            return null;
        } else {
            ChangeSeatReallocationChannelDTO reallocationChannelDTO = new ChangeSeatReallocationChannelDTO();
            reallocationChannelDTO.setId(reallocationChannel.getId());
            reallocationChannelDTO.setApplyToAllChannelTypes(reallocationChannel.getApplyToAllChannelTypes());

            return reallocationChannelDTO;
        }
    }

    private static EventExternalConfigDTO fromMs(EventExternalConfig source) {
        EventExternalConfigDTO target = new EventExternalConfigDTO();
        target.setDigitalTicketMode(source.getDigitalTicketMode());
        return target;
    }

    private static EventWhitelabelSettingsDTO fromMs(EventWhitelabelSettings in) {
        EventWhitelabelSettingsDTO out = new EventWhitelabelSettingsDTO();
        EventUISettingsDTO outSettings = new EventUISettingsDTO();

        // Session Settings
        EventUISessionSettingsDTO session = new EventUISessionSettingsDTO();
        if (in.getSessionSettings() != null) {
            session.setShowPriceFrom(in.getSessionSettings().getShowPriceFrom());
            outSettings.setEventUISessionSettings(session);
        }
        // Session Selection
        EventSessionSelection inSessionSelection = in.getSessionSelection();
        EventUISessionSelectionDTO sessionSelection = new EventUISessionSelectionDTO();
        if (in.getSessionSelection() != null) {
            sessionSelection.setSessionSelectType(inSessionSelection.getType());
            sessionSelection.setRestrictSelectionType(inSessionSelection.getRestrictType());
            sessionSelection.setShowAvailability(inSessionSelection.getShowAvailability());

            // Calendar settings
            if (inSessionSelection.getCalendar() != null) {
                SessionSelectCalendarDTO calendarDTO = new SessionSelectCalendarDTO();
                SessionCalendar inCalendar = inSessionSelection.getCalendar();
                calendarDTO.setType(inCalendar.getType());
                calendarDTO.setSessionCalendarSelectType(inCalendar.getCalendarSelectType());
                calendarDTO.setEnabled(inCalendar.getEnabled());
                sessionSelection.setSessionSelectCalendar(calendarDTO);
            }
            // List settings
            if (inSessionSelection.getList() != null) {
                SessionSelectListDTO listDTO = new SessionSelectListDTO();
                SessionList inList = inSessionSelection.getList();
                listDTO.setContainsImage(inList.getContainsImage());
                listDTO.setMedia(inList.getMedia());
                listDTO.setCardDesignType(inList.getCardDesignType());
                listDTO.setEnabled(inList.getEnabled());
                sessionSelection.setSessionSelectList(listDTO);
            }
        }

        outSettings.setEventUISessionSelection(sessionSelection);

        // Seat Selection
        EventUISeatSelectionDTO seatSelection = new EventUISeatSelectionDTO();
        if (in.getSeatSelection() != null) {
            SessionCalendar inCalendar = in.getSeatSelection().getCalendar();
            SessionList inList = in.getSeatSelection().getList();
            SessionSelectListDTO outList = new SessionSelectListDTO();
            SessionSelectCalendarDTO outCalendar = new SessionSelectCalendarDTO();

            seatSelection.setChangeSessionSelectType(in.getSeatSelection().getChangeSessionSelectType());
            seatSelection.setShowAvailability(in.getSeatSelection().getShowAvailability());
            seatSelection.setRestrictType(in.getSeatSelection().getRestrictType());
            seatSelection.setType(in.getSeatSelection().getType());
            if (inCalendar != null) {
                outCalendar.setSessionCalendarSelectType(inCalendar.getCalendarSelectType());
                outCalendar.setType(inCalendar.getType());
                outCalendar.setEnabled(inCalendar.getEnabled());
                seatSelection.setCalendar(outCalendar);
            }
            if (inList != null) {
                outList.setCardDesignType(inList.getCardDesignType());
                outList.setMedia(inList.getMedia());
                outList.setContainsImage(inList.getContainsImage());
                outList.setEnabled(inList.getEnabled());
                seatSelection.setList(outList);
            }
            outSettings.setEventUISeatSelection(seatSelection);
        }
        out.setEventUISettings(outSettings);
        return out;
    }

    private static void fillSettings(UpdateEventRequestDTO source, Event target) {
        UpdateEventSettingsDTO settings = source.getSettings();
        if (settings.getCategories() != null) {
            if (settings.getCategories().getBase() != null) {
                target.setCategory(new Category(settings.getCategories().getBase().getId()));
            }
            if (settings.getCategories().getCustom() != null) {
                target.setCustomCategory(new Category(settings.getCategories().getCustom().getId()));
            }
        }
        if (settings.getSalesGoal() != null) {
            SalesGoalDTO sales = settings.getSalesGoal();
            target.setSalesGoalTickets(sales.getTickets());
            target.setSalesGoalRevenue(sales.getRevenue() != null ? sales.getRevenue().doubleValue() : null);
        }
        TourSettingsDTO tour = settings.getTour();
        if (tour != null && tour.getEnable() != null) {
            if (BooleanUtils.isNotTrue(tour.getEnable())) {
                target.setTour(new IdNameDTO());
            } else if (tour.getId() != null) { //only enable if enable=true & tourId is in the request
                target.setTour(new IdNameDTO(tour.getId()));
            }
        }
        if (settings.getSessionPack() != null) {
            target.setSessionPackType(SessionPackType.valueOf(settings.getSessionPack().name()));
        }
        target.setBooking(bookingToMsEvent(settings));
        target.setAllowVenueReport(settings.getAllowVenueReports());
        target.setUseProducerFiscalData(settings.getUseProducerFiscalData());
        target.setUseTieredPricing(settings.getUseTieredPricing());
        target.setInvitationUseTicketTemplate(settings.getInvitationUseTicketTemplate());
        target.setSupraEvent(settings.getIsFestival());

        if (settings.getSubscriptionList() != null) {
            target.setEnableSubscriptionList(settings.getSubscriptionList().getEnable());
            target.setSubscriptionListId(settings.getSubscriptionList().getId());
        }

        if (settings.getGroups() != null) {
            target.setAllowGroups(settings.getGroups().getAllowed());
            if (settings.getGroups().getPricePolicy() != null) {
                target.setGroupPrice(EventGroupPriceType.getIdByName(settings.getGroups().getPricePolicy().name()));
            }
            target.setGroupCompanionPayment(settings.getGroups().getCompanionsPayment());
        }

        if (settings.getInteractiveVenue() != null && settings.getInteractiveVenue().getAllowInteractiveVenue() != null) {
            EventVenueViewConfig venueViewConfig = new EventVenueViewConfig();
            SettingsInteractiveVenueDTO interactiveVenue = settings.getInteractiveVenue();
            if (BooleanUtils.isTrue(interactiveVenue.getAllowInteractiveVenue())) {
                if (InteractiveVenueType.VENUE_3D_MMC_V1.equals(interactiveVenue.getInteractiveVenueType())) {
                    venueViewConfig.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_MMC_V1);
                    ConverterUtils.updateField(venueViewConfig::setUse3dVenueModule, interactiveVenue.getAllowVenue3dView());
                }
                if (InteractiveVenueType.VENUE_3D_MMC_V2.equals(interactiveVenue.getInteractiveVenueType())) {
                    venueViewConfig.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_MMC_V2);
                    ConverterUtils.updateField(venueViewConfig::setUse3dVenueModuleV2, interactiveVenue.getAllowVenue3dView());
                }
                if (InteractiveVenueType.VENUE_3D_PACIFA.equals(interactiveVenue.getInteractiveVenueType())) {
                    venueViewConfig.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_PACIFA);
                }
                ConverterUtils.updateField(venueViewConfig::setUseVenue3dView, interactiveVenue.getAllowVenue3dView());
                ConverterUtils.updateField(venueViewConfig::setUseSector3dView, interactiveVenue.getAllowSector3dView());
                ConverterUtils.updateField(venueViewConfig::setUseSeat3dView, interactiveVenue.getAllowSeat3dView());
            } else {
                venueViewConfig.setInteractiveVenueType(null);
                venueViewConfig.setUse3dVenueModule(Boolean.FALSE);
                venueViewConfig.setUse3dVenueModuleV2(Boolean.FALSE);
                venueViewConfig.setUseVenue3dView(Boolean.FALSE);
                venueViewConfig.setUseSector3dView(Boolean.FALSE);
                venueViewConfig.setUseSeat3dView(Boolean.FALSE);
            }
            target.setEventVenueViewConfig(venueViewConfig);
        }

        if (settings.getAccommodationsConfig() != null) {
            EventAccommodationsConfig accommodationsConfig = new EventAccommodationsConfig();
            accommodationsConfig.setEnabled(settings.getAccommodationsConfig().getEnabled());
            if (settings.getAccommodationsConfig().getVendor() != null) {
                accommodationsConfig.setVendor(es.onebox.mgmt.datasources.common.enums.AccommodationsVendor.valueOf(settings.getAccommodationsConfig().getVendor().name()));
            }
            accommodationsConfig.setValue(settings.getAccommodationsConfig().getValue());
            target.setAccommodationsConfig(accommodationsConfig);
        }

        target.setInvoicePrefixId(settings.getInvoicePrefixId());

        if (settings.getEventWhiteLabelSettings() != null && settings.getEventWhiteLabelSettings().getEventUISettings() != null) {
            target.setWhitelabelSettings(toMsEvent(settings.getEventWhiteLabelSettings().getEventUISettings()));
        }
        if (settings.getEventExternalConfig() != null) {
            EventExternalConfig eventExternalConfig = new EventExternalConfig();
            eventExternalConfig.setDigitalTicketMode(settings.getEventExternalConfig().getDigitalTicketMode());
            target.setEventExternalConfig(eventExternalConfig);
        }
        if (settings.getEventChangeSeatSettings() != null) {
            EventChangeSeatSettingsDTO changeSeatSettings = settings.getEventChangeSeatSettings();
            target.setAllowChangeSeat(changeSeatSettings.getEnable());
            if (changeSeatSettings.getChangeType() != null || changeSeatSettings.getEventChangeSeatExpiry() != null) {
                EventChangeSeat changeSeat = new EventChangeSeat();
                changeSeat.setChangeType(ChangeSeatChangeType.byId(changeSeatSettings.getChangeType().getId()));
                changeSeat.setEventChangeSeatExpiry(toMsEvent(changeSeatSettings.getEventChangeSeatExpiry()));
                changeSeat.setNewTicketSelection(toMsEvent(changeSeatSettings.getNewTicketSelection()));
                changeSeat.setReallocationChannel(toMsEvent(changeSeatSettings.getReallocationChannel()));
                target.setChangeSeat(changeSeat);
            }
        }

        EventTransferTicketDTO transferTicketDTO = settings.getEventTransferTicket();
        if (transferTicketDTO != null){
            EventTransferTicket transferTicket = toMsEvent(transferTicketDTO);
            target.setTransfer(transferTicket);
            target.setAllowTransferTicket(transferTicketDTO.isEnabled());
        }

        target.setTaxMode(TaxMode.fromDTO(settings.getTaxMode()));

    }

    private static EventChangeSeatExpiry toMsEvent(EventChangeSeatExpiryDTO source) {
        if (source == null) {
            return null;
        } else {
            EventChangeSeatExpiry target = new EventChangeSeatExpiry();
            target.setTimeOffsetLimitAmount(source.getTimeOffsetLimitAmount());
            target.setTimeOffsetLimitUnit(source.getTimeOffsetLimitUnit());
            return target;
        }
    }

    private static ChangeSeatNewTicketSelection toMsEvent(ChangeSeatNewTicketSelectionDTO changeSeatNewTicketSelectionDTO) {

        if (changeSeatNewTicketSelectionDTO == null) {
            return null;
        } else {
            ChangeSeatNewTicketSelection changeSeatNewTicketSelection = new ChangeSeatNewTicketSelection();
            changeSeatNewTicketSelection.setAllowedSessions(ChangeSeatAllowedSessions.byId(changeSeatNewTicketSelectionDTO.getAllowedSessions().getId()));
            changeSeatNewTicketSelection.setSameDateOnly(changeSeatNewTicketSelectionDTO.getSameDateOnly());
            changeSeatNewTicketSelection.setTickets(ChangeSeatTickets.byId(changeSeatNewTicketSelectionDTO.getTickets().getId()));
            changeSeatNewTicketSelection.setPrice(new ChangeSeatPrice());
            changeSeatNewTicketSelection.getPrice().setType(ChangeSeatAmountType.byId(changeSeatNewTicketSelectionDTO.getPrice().getType().getId()));
            if (changeSeatNewTicketSelectionDTO.getPrice().getRefund() != null) {
                ChangeSeatRefund changeSeatRefund = new ChangeSeatRefund();
                ChangeSeatRefundDTO changeSeatRefundDTO = changeSeatNewTicketSelectionDTO.getPrice().getRefund();
                changeSeatRefund.setType(ChangeSeatRefundType.byId(changeSeatRefundDTO.getType().getId()));
                if (changeSeatRefundDTO.getVoucherExpiry() != null) {
                    ChangeSeatVoucherExpiry changeSeatVoucherExpiry = new ChangeSeatVoucherExpiry();
                    changeSeatVoucherExpiry.setEnabled(changeSeatRefundDTO.getVoucherExpiry().getEnabled());
                    if (changeSeatRefundDTO.getVoucherExpiry().getExpiryTime() != null) {
                        ChangeSeatExpiryTime changeSeatExpiryTime = new ChangeSeatExpiryTime();
                        changeSeatExpiryTime.setTimeOffsetLimitAmount(changeSeatRefundDTO.getVoucherExpiry().getExpiryTime().getTimeOffsetLimitAmount());
                        changeSeatExpiryTime.setTimeOffsetLimitUnit(changeSeatRefundDTO.getVoucherExpiry().getExpiryTime().getTimeOffsetLimitUnit());
                        changeSeatVoucherExpiry.setExpiryTime(changeSeatExpiryTime);
                    }
                    changeSeatRefund.setVoucherExpiry(changeSeatVoucherExpiry);
                }
                changeSeatNewTicketSelection.getPrice().setRefund(changeSeatRefund);
            }

            return changeSeatNewTicketSelection;
        }
    }

    private static ChangeSeatReallocationChannel toMsEvent(ChangeSeatReallocationChannelDTO reallocationChannelDTO) {
        if (reallocationChannelDTO == null) {
            return null;
        } else {
            ChangeSeatReallocationChannel reallocationChannel = new ChangeSeatReallocationChannel();
            reallocationChannel.setId(reallocationChannelDTO.getId());
            reallocationChannel.setApplyToAllChannelTypes(reallocationChannelDTO.getApplyToAllChannelTypes());

            return reallocationChannel;
        }
    }

    private static EventWhitelabelSettings toMsEvent(EventUISettingsDTO in) {
        EventWhitelabelSettings out = new EventWhitelabelSettings();
        EventSessionSelection sessionSelection = new EventSessionSelection();
        EventSeatSelection seatSelection = new EventSeatSelection();
        EventSessionSettings sessionSettings = new EventSessionSettings();

        if (in.getEventUISessionSettings() != null) {
            sessionSettings.setShowPriceFrom(in.getEventUISessionSettings().getShowPriceFrom());
        }
        if (in.getEventUISessionSelection() != null) {
            sessionSelection.setType(in.getEventUISessionSelection().getSessionSelectType());
            sessionSelection.setShowAvailability(in.getEventUISessionSelection().getShowAvailability());
            sessionSelection.setRestrictType(in.getEventUISessionSelection().getRestrictSelectionType());
            if (in.getEventUISessionSelection().getSessionSelectCalendar() != null) {
                SessionSelectCalendarDTO inCalendar = in.getEventUISessionSelection().getSessionSelectCalendar();
                SessionCalendar calendar = new SessionCalendar();
                calendar.setType(inCalendar.getType());
                calendar.setCalendarSelectType(inCalendar.getSessionCalendarSelectType());
                calendar.setEnabled(inCalendar.getEnabled());
                sessionSelection.setCalendar(calendar);
            }
            if (in.getEventUISessionSelection().getSessionSelectList() != null) {
                SessionSelectListDTO inList = in.getEventUISessionSelection().getSessionSelectList();
                SessionList list = new SessionList();
                list.setContainsImage(inList.getContainsImage());
                list.setMedia(inList.getMedia());
                list.setCardDesignType(inList.getCardDesignType());
                list.setEnabled(inList.getEnabled());
                sessionSelection.setList(list);
            }
        }

        if (in.getEventUISeatSelection() != null) {
            EventUISeatSelectionDTO inSeatSelection = in.getEventUISeatSelection();
            seatSelection.setChangeSessionSelectType(inSeatSelection.getChangeSessionSelectType());
            seatSelection.setShowAvailability(inSeatSelection.getShowAvailability());
            if (inSeatSelection.getCalendar() != null) {
                seatSelection.setType(SessionSelectType.CALENDAR);
                SessionCalendar outCalendar = new SessionCalendar();
                outCalendar.setEnabled(inSeatSelection.getCalendar().getEnabled());
                outCalendar.setType(inSeatSelection.getCalendar().getType());
                outCalendar.setCalendarSelectType(inSeatSelection.getCalendar().getSessionCalendarSelectType());
                seatSelection.setCalendar(outCalendar);
            }
            if (inSeatSelection.getList() != null) {
                seatSelection.setType(SessionSelectType.LIST);
                SessionList outList = new SessionList();
                outList.setEnabled(inSeatSelection.getList().getEnabled());
                outList.setMedia(inSeatSelection.getList().getMedia());
                outList.setCardDesignType(inSeatSelection.getList().getCardDesignType());
                seatSelection.setList(outList);
            }
        }
        out.setSessionSelection(sessionSelection);
        out.setSeatSelection(seatSelection);
        out.setSessionSettings(sessionSettings);
        return out;
    }

    private static Booking bookingToMsEvent(UpdateEventSettingsDTO source) {
        Booking booking = null;
        if (source.getBookings() != null) {
            booking = new Booking();
            BookingSettingsDTO sourceBookings = source.getBookings();
            booking.setAllowed(sourceBookings.getEnable());
            BookingExpiration bookingExpiration = source.getBookings().getBookingExpiration();
            if (bookingExpiration != null) {
                BookingOrderExpiration orderExpiration = bookingExpiration.getBookingOrderExpiration();
                if (orderExpiration != null) {
                    if (orderExpiration.getTimespan() != null) {
                        booking.setOrderExpirationTimespan(TimespanOrderExpire.valueOf(orderExpiration.getTimespan().name()));
                    }
                    booking.setOrderExpirationTimespanAmount(orderExpiration.getTimespanAmount());
                    booking.setOrderExpirationHour(orderExpiration.getExpirationTime());
                    booking.setOrderExpirationType(orderExpiration.getOrderExpirationType());
                }
                booking.setExpirationType(bookingExpiration.getExpirationDeadlineType());
                booking.setFixedDate(bookingExpiration.getDate());
                BookingSessionExpiration sessionExpiration = bookingExpiration.getBookingSessionExpiration();
                if (sessionExpiration != null) {
                    booking.setSessionExpirationTimespanAmount(Math.abs(sessionExpiration.getTimespanAmount()));
                    if (sessionExpiration.getSessionTimeFrame() != null) {
                        booking.setSessionExpirationTimespan(TimespanSessionExpire.valueOf(sessionExpiration.getSessionTimeFrame().name()));
                    }
                    booking.setSessionExpirationType(sessionExpiration.getTimespanAmount() >= 0 ?
                            SessionTypeExpiration.AFTER : SessionTypeExpiration.BEFORE);
                    booking.setSessionExpirationHour(sessionExpiration.getExpirationTime());
                }
            }
        }
        return booking;
    }

    private static BookingSettingsDTO bookingFromMsEvent(Event event) {
        BookingSettingsDTO bookingResponse = null;
        if (event.getBooking() != null) {
            bookingResponse = new BookingSettingsDTO();

            bookingResponse.setEnable(event.getBooking().getAllowed());

            if (event.getBooking() != null) {
                bookingResponse.setBookingExpiration(getBookingExpiration(event));
            }
        }
        return bookingResponse;
    }

    private static BookingExpiration getBookingExpiration(Event event) {
        BookingExpiration bookingExpiration = new BookingExpiration();

        if (event == null || event.getBooking() == null) {
            return bookingExpiration;
        }

        if (event.getBooking().getOrderExpirationTimespan() != null || event.getBooking().getOrderExpirationHour() != null
                || event.getBooking().getOrderExpirationTimespanAmount() != null || event.getBooking().getOrderExpirationType() != null) {
            BookingOrderExpiration bookingOrderExpiration = new BookingOrderExpiration();
            String orderExpirationTimespan = event.getBooking().getOrderExpirationTimespan() != null
                    ? event.getBooking().getOrderExpirationTimespan().name()
                    : null;
            if (orderExpirationTimespan != null) {
                bookingOrderExpiration.setTimespan(TimespanBookingOrderExpiration.valueOf(orderExpirationTimespan));
            }
            bookingOrderExpiration.setTimespanAmount(event.getBooking().getOrderExpirationTimespanAmount());
            bookingOrderExpiration.setExpirationTime(event.getBooking().getOrderExpirationHour());
            bookingOrderExpiration.setOrderExpirationType(event.getBooking().getOrderExpirationType());
            bookingExpiration.setBookingOrderExpiration(bookingOrderExpiration);
        }

        bookingExpiration.setDate(event.getBooking().getFixedDate());
        bookingExpiration.setExpirationDeadlineType(event.getBooking().getExpirationType());

        if (event.getBooking().getSessionExpirationTimespan() != null || event.getBooking().getSessionExpirationTimespanAmount() != null
                || event.getBooking().getSessionExpirationType() != null || event.getBooking().getSessionExpirationHour() != null) {
            BookingSessionExpiration bookingSessionExpiration = new BookingSessionExpiration();
            bookingSessionExpiration.setExpirationTime(event.getBooking().getSessionExpirationHour());
            bookingSessionExpiration.setSessionTimeFrame(SessionTimeFrame.valueOf(event.getBooking().getSessionExpirationTimespan().name()));

            if (event.getBooking().getSessionExpirationType() == SessionTypeExpiration.AFTER) {
                bookingSessionExpiration.setTimespanAmount(event.getBooking().getSessionExpirationTimespanAmount());
            } else {
                bookingSessionExpiration.setTimespanAmount(event.getBooking().getSessionExpirationTimespanAmount() * (-1));
            }

            bookingExpiration.setBookingSessionExpiration(bookingSessionExpiration);
        }
        return bookingExpiration;
    }

    public static void addAuthContact(User authUser, ContactData contactData) {
        if (authUser != null) {
            contactData.setContactPersonName(authUser.getName());
            contactData.setContactPersonSurname(authUser.getLastName());
            contactData.setContactPersonEmail(authUser.getEmail());
            contactData.setContactPersonPhone(authUser.getMainPhone());
        }
    }

    public static CreateEventData prepareCreateEventData(String name, String promoterReference, es.onebox.mgmt.datasources.ms.event.dto.event.EventType type,
                                                         Long entityId, Long producerId, Long invoicePrefixId, Integer categoryId, Integer defaultLangId, List<Long> entityFavoriteChannels,
                                                         Integer avetCompetitionId, EventAvetConfigType avetConfig, Long currencyId, Map<String, Object> additionalConfig) {
        return new CreateEventData(name, promoterReference, type, entityId, producerId,
                invoicePrefixId, categoryId, defaultLangId, entityFavoriteChannels, avetCompetitionId, avetConfig,
                currencyId, additionalConfig);
    }

    public static Map<String, Object> convertToDatasource(es.onebox.mgmt.events.dto.AdditionalConfigDTO source) {
        if (source == null) {
            return null;
        }
        Map<String, Object> additionalConfig = new HashMap<>();
        if (source.getInventoryProvider() != null) {
            additionalConfig.put(CONFIG_INVENTORY_PROVIDER, source.getInventoryProvider().getCode());
        }
        if (StringUtils.isNotBlank(source.getExternalEventId())) {
            additionalConfig.put(EXTERNAL_EVENT_ID, source.getExternalEventId());
        }

        if (StringUtils.isNotBlank(source.getExternalEventId())) {
            additionalConfig.put(VENUE_TEMPLATE_ID, source.getVenueTemplateId());
        }

        if (source.getStandalone() != null) {
            additionalConfig.put(STANDALONE, source.getStandalone());
        }

        return additionalConfig;
    }

    private static EventTransferTicketDTO fromMsEvent(Event target) {
        EventTransferTicketDTO eventTransferTicketDTO = new EventTransferTicketDTO();
        eventTransferTicketDTO.setEnabled(target.getAllowTransferTicket());
        if (target.getTransfer() != null) {
            EventTransferTicket transfer = target.getTransfer();
            eventTransferTicketDTO.setTransferPolicy(transfer.getTransferPolicy());
            eventTransferTicketDTO.setTransferTicketMaxDelayTime(transfer.getTransferTicketMaxDelayTime());
            eventTransferTicketDTO.setRecoveryTicketMaxDelayTime(transfer.getRecoveryTicketMaxDelayTime());
            eventTransferTicketDTO.setEnableMaxTicketTransfers(transfer.getEnableMaxTicketTransfers());
            eventTransferTicketDTO.setMaxTicketTransfers(transfer.getMaxTicketTransfers());
            eventTransferTicketDTO.setTransferTicketMinDelayTime(transfer.getTransferTicketMinDelayTime());
            eventTransferTicketDTO.setRestrictTransferBySessions(transfer.getRestrictTransferBySessions());
            eventTransferTicketDTO.setEnableMultipleTransfers(transfer.getAllowMultipleTransfers());
            eventTransferTicketDTO.setAllowedTransferSessions(transfer.getAllowedTransferSessions());
        }
        return eventTransferTicketDTO;
    }

    private static EventTransferTicket toMsEvent
            (EventTransferTicketDTO transferTicketDTO) {
        EventTransferTicket eventTransferTicket = new EventTransferTicket();
        eventTransferTicket.setTransferPolicy(transferTicketDTO.getTransferPolicy());
        eventTransferTicket.setTransferTicketMaxDelayTime(transferTicketDTO.getTransferTicketMaxDelayTime());
        eventTransferTicket.setRecoveryTicketMaxDelayTime(transferTicketDTO.getRecoveryTicketMaxDelayTime());
        eventTransferTicket.setEnableMaxTicketTransfers(transferTicketDTO.getEnableMaxTicketTransfers());
        eventTransferTicket.setAllowMultipleTransfers(transferTicketDTO.getEnableMultipleTransfers());
        eventTransferTicket.setMaxTicketTransfers(transferTicketDTO.getMaxTicketTransfers());
        eventTransferTicket.setTransferTicketMinDelayTime(transferTicketDTO.getTransferTicketMinDelayTime());
        eventTransferTicket.setRestrictTransferBySessions(transferTicketDTO.getRestrictTransferBySessions());
        eventTransferTicket.setAllowedTransferSessions(transferTicketDTO.getAllowedTransferSessions());
        return eventTransferTicket;

    }

}
