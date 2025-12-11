package es.onebox.mgmt.seasontickets.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.common.NameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.categories.CategoryDTO;
import es.onebox.mgmt.common.CategoriesDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.InvoicePrefix;
import es.onebox.mgmt.datasources.ms.event.dto.event.Booking;
import es.onebox.mgmt.datasources.ms.event.dto.event.Category;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventVenueViewConfig;
import es.onebox.mgmt.datasources.ms.event.dto.event.SessionTypeExpiration;
import es.onebox.mgmt.datasources.ms.event.dto.event.TimespanOrderExpire;
import es.onebox.mgmt.datasources.ms.event.dto.event.TimespanSessionExpire;
import es.onebox.mgmt.datasources.ms.event.dto.event.Venue;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketChangeSeat;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRenewal;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketStatus;
import es.onebox.mgmt.datasources.ms.event.dto.session.PresalesLinkMode;
import es.onebox.mgmt.datasources.ms.event.dto.session.PresalesRedirectionPolicy;
import es.onebox.mgmt.events.converter.EventConverter;
import es.onebox.mgmt.events.dto.BookingExpiration;
import es.onebox.mgmt.events.dto.BookingOrderExpiration;
import es.onebox.mgmt.events.dto.BookingSessionExpiration;
import es.onebox.mgmt.events.dto.BookingSettingsDTO;
import es.onebox.mgmt.events.dto.EventContactDTO;
import es.onebox.mgmt.events.dto.EventVenueDTO;
import es.onebox.mgmt.events.dto.EventVenueTemplateDTO;
import es.onebox.mgmt.events.dto.InvoicePrefixDTO;
import es.onebox.mgmt.events.dto.LanguagesDTO;
import es.onebox.mgmt.events.dto.SalesGoalDTO;
import es.onebox.mgmt.events.dto.SettingsInteractiveVenueDTO;
import es.onebox.mgmt.events.enums.SessionTimeFrame;
import es.onebox.mgmt.events.enums.TimespanBookingOrderExpiration;
import es.onebox.mgmt.events.tours.dto.TourSettingsDTO;
import es.onebox.mgmt.seasontickets.dto.AdditionalConfigDTO;
import es.onebox.mgmt.seasontickets.dto.BaseSeasonTicketDTO;
import es.onebox.mgmt.seasontickets.dto.MaxBuyingLimitDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketChangeSeatDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketOperativeDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketRenewalDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketSearchResultDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketsSettingsDTO;
import es.onebox.mgmt.seasontickets.dto.SettingsBookingsDTO;
import es.onebox.mgmt.seasontickets.dto.SettingsSalesDTO;
import es.onebox.mgmt.seasontickets.dto.SettingsSecondaryMarketDTO;
import es.onebox.mgmt.seasontickets.dto.SetttingsReleaseDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketOperativeDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketRequestDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketsSettingsDTO;
import es.onebox.mgmt.seasontickets.enums.RenewalType;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketStatusDTO;
import es.onebox.mgmt.sessions.dto.PresalesRedirectionPolicyDTO;
import es.onebox.mgmt.sessions.dto.SeasonTicketSubscriptionListDTO;
import es.onebox.mgmt.sessions.enums.PresalesLinkDestinationMode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SeasonTicketConverter {

    private static final String UTC = "UTC";

    private SeasonTicketConverter() {
    }

    public static SeasonTicketDTO fromMsEvent(SeasonTicket seasonTicket, List<Currency> currencies) {
        if (seasonTicket == null) {
            return null;
        }

        SeasonTicketDTO target = (SeasonTicketDTO) fromMsEvent(seasonTicket, new SeasonTicketDTO(), currencies);
        if (seasonTicket.getCurrencyId() != null) {
            target.setCurrencyCode(CurrenciesUtils.getCurrencyCode(currencies, seasonTicket.getCurrencyId()));
        }
        target.setContact(fillContact(seasonTicket));
        target.setSettings(fillSettings(seasonTicket));

        return target;
    }

    public static BaseSeasonTicketDTO fromMsEvent(SeasonTicket seasonTicket, BaseSeasonTicketDTO target, List<Currency> currencies) {
        target.setId(seasonTicket.getId());
        target.setName(seasonTicket.getName());
        target.setReference(seasonTicket.getPromoterReference());
        if (seasonTicket.getCurrencyId() != null) {
            target.setCurrencyCode(CurrenciesUtils.getCurrencyCode(currencies, seasonTicket.getCurrencyId()));
        }

        if (seasonTicket.getDate() != null) {
            if (seasonTicket.getDate().getStart() != null) {
                target.setStartDate(seasonTicket.getDate().getStart().getDateTime());
                target.setStartDateTZ(seasonTicket.getDate().getStart().getTimeZone().getOlsonId());
            }
            if (seasonTicket.getDate().getEnd() != null) {
                target.setEndDate(seasonTicket.getDate().getEnd().getDateTime());
                target.setEndDateTZ(seasonTicket.getDate().getEnd().getTimeZone().getOlsonId());
            }
        }
        if (seasonTicket.getEntityId() != null) {
            target.setEntity(new IdNameDTO());
            target.getEntity().setId(seasonTicket.getEntityId());
            target.getEntity().setName(seasonTicket.getEntityName());
        }

        if (seasonTicket.getProducer() != null && seasonTicket.getProducer().getId() != null) {
            target.setProducer(new IdNameDTO());
            target.getProducer().setId(seasonTicket.getProducer().getId());
            target.getProducer().setName(seasonTicket.getProducer().getName());
        }
        if (!CommonUtils.isEmpty(seasonTicket.getVenues())) {
            target.setVenueTemplates(new ArrayList<>());
            for (Venue venue : seasonTicket.getVenues()) {
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
        target.setSessionId(seasonTicket.getSessionId());

        return target;
    }

    public static SeasonTicketSearchResultDTO fromMsEvent(SeasonTicket seasonTicket, SeasonTicketSearchResultDTO target, List<Currency> currencies) {
        fromMsEvent(seasonTicket, (BaseSeasonTicketDTO) target, currencies);
        if (seasonTicket.getAllowRenewal() != null) {
            target.setAllowRenewal(seasonTicket.getAllowRenewal());
        }
        if (seasonTicket.getAllowChangeSeat() != null) {
            target.setAllowChangeSeat(seasonTicket.getAllowChangeSeat());
        }
        if (seasonTicket.getStatus() != null) {
            target.setStatus(SeasonTicketStatusDTO.valueOf(seasonTicket.getStatus().name()));
        }
        if (seasonTicket.getGenerationStatus() != null) {
            target.setGenerationStatus(SeasonTicketStatusConverter.convertInternalGenerationStatus(seasonTicket.getGenerationStatus()));
        }
        return target;
    }

    private static EventContactDTO fillContact(SeasonTicket seasonTicket) {
        EventContactDTO contactDTO = null;
        if (Objects.nonNull(seasonTicket.getContactPersonName())
                || Objects.nonNull(seasonTicket.getContactPersonSurname())
                || Objects.nonNull(seasonTicket.getContactPersonEmail())
                || Objects.nonNull(seasonTicket.getContactPersonPhone())) {
            contactDTO = new EventContactDTO();
            contactDTO.setName(seasonTicket.getContactPersonName());
            contactDTO.setSurname(seasonTicket.getContactPersonSurname());
            contactDTO.setEmail(seasonTicket.getContactPersonEmail());
            contactDTO.setPhoneNumber(seasonTicket.getContactPersonPhone());
        }
        return contactDTO;
    }

    private static SeasonTicketsSettingsDTO fillSettings(SeasonTicket seasonTicket) {
        SeasonTicketsSettingsDTO settings = new SeasonTicketsSettingsDTO();
        createOperativeIfNullOnSettings(settings);

        settings.setBookings(bookingFromMsEvent(seasonTicket));
        settings.setTour(new TourSettingsDTO(seasonTicket.getTour() != null ? seasonTicket.getTour().getId() : null));
        if (!CommonUtils.isEmpty(seasonTicket.getLanguages())) {
            settings.setLanguages(new LanguagesDTO());
            settings.getLanguages().setSelected(
                    seasonTicket.getLanguages().stream().map(eventLanguage -> {
                        String langCode = ConverterUtils.toLanguageTag(eventLanguage.getCode());
                        if (CommonUtils.isTrue(eventLanguage.getDefault())) {
                            settings.getLanguages().setDefaultLanguage(langCode);
                        }
                        return langCode;
                    }).collect(Collectors.toList()));
        }

        if (seasonTicket.getCategory() != null || seasonTicket.getCustomCategory() != null) {
            settings.setCategories(new CategoriesDTO());
            if (seasonTicket.getCategory() != null) {
                settings.getCategories().setBase(new CategoryDTO());
                settings.getCategories().getBase().setId(seasonTicket.getCategory().getId());
                settings.getCategories().getBase().setDescription(seasonTicket.getCategory().getDescription());
                settings.getCategories().getBase().setCode(seasonTicket.getCategory().getCode());
            }
            if (seasonTicket.getCustomCategory() != null) {
                settings.getCategories().setCustom(new CategoryDTO());
                settings.getCategories().getCustom().setId(seasonTicket.getCustomCategory().getId());
                settings.getCategories().getCustom().setDescription(seasonTicket.getCustomCategory().getDescription());
                settings.getCategories().getCustom().setCode(seasonTicket.getCustomCategory().getCode());
            }
        }

        if (seasonTicket.getVenueTimeZone() != null) {
            settings.setZoneId(seasonTicket.getVenueTimeZone().getOlsonId());
        }

        if (seasonTicket.getSalesStartingDate() != null ||
                seasonTicket.getSalesEndDate() != null ||
                seasonTicket.getEnableSales() != null) {
            settings.getOperative().setSale(new SettingsSalesDTO());
            settings.getOperative().getSale().setEndDate(seasonTicket.getSalesEndDate());
            settings.getOperative().getSale().setStartDate(seasonTicket.getSalesStartingDate());
            settings.getOperative().getSale().setEnable(seasonTicket.getEnableSales());
        }
        if (seasonTicket.getChannelPublishingDate() != null ||
                seasonTicket.getEnableChannels() != null) {
            settings.getOperative().setRelease(new SetttingsReleaseDTO());
            settings.getOperative().getRelease().setDate(seasonTicket.getChannelPublishingDate());
            settings.getOperative().getRelease().setEnable(seasonTicket.getEnableChannels());
        }
        if (seasonTicket.getBookingEndDate() != null ||
                seasonTicket.getBookingStartingDate() != null ||
                seasonTicket.getBookingEnabled() != null) {
            settings.getOperative().setBooking(new SettingsBookingsDTO());
            settings.getOperative().getBooking().setEndDate(seasonTicket.getBookingEndDate());
            settings.getOperative().getBooking().setStartDate(seasonTicket.getBookingStartingDate());
            settings.getOperative().getBooking().setEnable(seasonTicket.getBookingEnabled());
        }

        if (seasonTicket.getMaxBuyingLimit() != null) {
            MaxBuyingLimitDTO maxBuyingLimitDTO = new MaxBuyingLimitDTO();
            maxBuyingLimitDTO.setValue(seasonTicket.getMaxBuyingLimit().getValue());
            settings.getOperative().setMaxBuyingLimit(maxBuyingLimitDTO);
        }

        if (seasonTicket.getMemberMandatory() != null) {
            settings.getOperative().setMemberMandatory(seasonTicket.getMemberMandatory());
        }

        if (seasonTicket.getEnableSubscriptionList() != null || seasonTicket.getSubscriptionListId() != null) {
            SeasonTicketSubscriptionListDTO subscriptionListDTO = new SeasonTicketSubscriptionListDTO();
            subscriptionListDTO.setEnable(seasonTicket.getEnableSubscriptionList());
            subscriptionListDTO.setId(seasonTicket.getSubscriptionListId());
            settings.setSubscriptionList(subscriptionListDTO);
        }

        if (seasonTicket.getAllowRenewal() != null) {
            settings.getOperative().setAllowRenewal(seasonTicket.getAllowRenewal());
            if (seasonTicket.getRenewal() != null) {
                SeasonTicketRenewal renewal = seasonTicket.getRenewal();
                SeasonTicketRenewalDTO seasonTicketRenewalDTO = new SeasonTicketRenewalDTO();
                seasonTicketRenewalDTO.setEnable(renewal.getRenewalEnabled());
                seasonTicketRenewalDTO.setStartDate(renewal.getRenewalStartingDate());
                seasonTicketRenewalDTO.setEndDate(renewal.getRenewalEndDate());
                seasonTicketRenewalDTO.setInProcess(renewal.getRenewalInProcess());
                seasonTicketRenewalDTO.setAutomatic(renewal.getAutoRenewal());
                seasonTicketRenewalDTO.setAutomaticMandatory(renewal.getAutoRenewalMandatory());

                if (renewal.getRenewalType() != null) {
                    seasonTicketRenewalDTO.setRenewalType(RenewalType.valueOf(renewal.getRenewalType()));
                    seasonTicketRenewalDTO.setRenewalTypeConfig(renewal.getRenewalTypeConfig());
                    seasonTicketRenewalDTO.setBankAccountId(renewal.getBankAccountId());
                    seasonTicketRenewalDTO.setGroupByReference(renewal.getGroupByReference());
                }

                if (seasonTicket.getVenueTimeZone() != null) {
                    seasonTicketRenewalDTO.setZoneId(seasonTicket.getVenueTimeZone().getOlsonId());
                }

                settings.getOperative().setRenewal(seasonTicketRenewalDTO);
            }
        }
        if (seasonTicket.getAllowChangeSeat() != null) {
            settings.getOperative().setAllowChangeSeat(seasonTicket.getAllowChangeSeat());
        }
        if (seasonTicket.getChangeSeat() != null) {
                SeasonTicketChangeSeat changeSeat = seasonTicket.getChangeSeat();
                SeasonTicketChangeSeatDTO seasonTicketChangeSeatDTO = new SeasonTicketChangeSeatDTO();
                seasonTicketChangeSeatDTO.setEnable(changeSeat.getChangeSeatEnabled());
                seasonTicketChangeSeatDTO.setStartDate(changeSeat.getChangeSeatStartingDate());
                seasonTicketChangeSeatDTO.setEndDate(changeSeat.getChangeSeatEndDate());
                seasonTicketChangeSeatDTO.setMaxValue(changeSeat.getMaxChangeSeatValue());

                settings.getOperative().setChangeSeat(seasonTicketChangeSeatDTO);
        }
        if (seasonTicket.getAllowTransferTicket() != null) {
            settings.getOperative().setAllowTransfer(seasonTicket.getAllowTransferTicket());
        }
        if (seasonTicket.getAllowReleaseSeat() != null) {
            settings.getOperative().setAllowReleaseSeat(Boolean.TRUE.equals(seasonTicket.getAllowReleaseSeat()));
        }
        if (seasonTicket.getSalesGoalTickets() != null || seasonTicket.getSalesGoalRevenue() != null) {
            settings.setSalesGoal(new SalesGoalDTO());
            settings.getSalesGoal().setTickets(seasonTicket.getSalesGoalTickets());
            settings.getSalesGoal().setRevenue(seasonTicket.getSalesGoalRevenue());
        }

            SettingsInteractiveVenueDTO interactiveVenue = new SettingsInteractiveVenueDTO();
        if (seasonTicket.getEventVenueViewConfig() != null) {
            EventVenueViewConfig venueViewConfig = seasonTicket.getEventVenueViewConfig();

            interactiveVenue.setAllowSector3dView(venueViewConfig.isUseSector3dView());
            interactiveVenue.setAllowSeat3dView(venueViewConfig.isUseSeat3dView());

            if (venueViewConfig.getInteractiveVenueType() != null) {
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

                interactiveVenue.setAllowInteractiveVenue(Boolean.FALSE);
                interactiveVenue.setAllowVenue3dView(Boolean.FALSE);

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

        if (seasonTicket.getSecondaryMarketSaleEndDate() != null ||
                seasonTicket.getSecondaryMarketSaleStartingDate() != null ||
                seasonTicket.getEnableSecondaryMarketSale() != null) {
            settings.getOperative().setSecondaryMarket(new SettingsSecondaryMarketDTO());
            settings.getOperative().getSecondaryMarket().setEndDate(seasonTicket.getSecondaryMarketSaleEndDate());
            settings.getOperative().getSecondaryMarket().setStartDate(seasonTicket.getSecondaryMarketSaleStartingDate());
            settings.getOperative().getSecondaryMarket().setEnable(seasonTicket.getEnableSecondaryMarketSale());
        }
        settings.setUseProducerFiscalData(seasonTicket.getUseProducerFiscalData());
        settings.setInvitationUseTicketTemplate(seasonTicket.getInvitationUseTicketTemplate());

        settings.getOperative().setRegisterMandatory(BooleanUtils.isTrue(seasonTicket.getRegisterMandatory()));
        if (BooleanUtils.isTrue(seasonTicket.getRegisterMandatory())) {
            settings.getOperative().setCustomerMaxSeats(seasonTicket.getCustomerMaxSeats());
        }

        if (seasonTicket.getPresalesRedirectionPolicy() != null) {
            PresalesRedirectionPolicyDTO presalesRedirectionPolicyDTO = new PresalesRedirectionPolicyDTO();
            presalesRedirectionPolicyDTO.setValue(seasonTicket.getPresalesRedirectionPolicy().getValue());
            if (seasonTicket.getPresalesRedirectionPolicy().getMode() != null)
                presalesRedirectionPolicyDTO.setMode(PresalesLinkDestinationMode.valueOf(seasonTicket.getPresalesRedirectionPolicy().getMode().name()));

            settings.setPresalesRedirectionPolicy(presalesRedirectionPolicyDTO);
        }

        return settings;
    }

    public static void addInvoicePrefix(SeasonTicketDTO seasonTicket, InvoicePrefix invoicePrefix) {
        InvoicePrefixDTO invoicePrefixDTO = new InvoicePrefixDTO();
        invoicePrefixDTO.setPrefix(invoicePrefix.getPrefix());
        invoicePrefixDTO.setSuffix(invoicePrefix.getSuffix());
        invoicePrefixDTO.setId(invoicePrefix.getId());
        invoicePrefixDTO.setProducerId(invoicePrefix.getProducerId());
        seasonTicket.getSettings().setInvoicePrefix(invoicePrefixDTO);
    }

    public static Map<String, Object> convertToDatasource(AdditionalConfigDTO source) {
        if (source == null) {
            return null;
        }
        Map<String, Object> additionalConfig = new HashMap<>();
        if (source.getInventoryProvider() != null) {
            additionalConfig.put(EventConverter.CONFIG_INVENTORY_PROVIDER, source.getInventoryProvider().getCode());
        }
        if (StringUtils.isNotBlank(source.getExternalEventId())) {
            additionalConfig.put(EventConverter.EXTERNAL_EVENT_ID, source.getExternalEventId());
        }

        if (source.getVenueTemplateId() != null) {
            additionalConfig.put(EventConverter.VENUE_TEMPLATE_ID, source.getVenueTemplateId());
        }

        return additionalConfig;
    }

    private static void createOperativeIfNullOnSettings(SeasonTicketsSettingsDTO settings) {
        if (settings.getOperative() == null) {
            SeasonTicketOperativeDTO operativeDTO = new SeasonTicketOperativeDTO();
            settings.setOperative(operativeDTO);
        }
    }

    public static SeasonTicket toMsEvent(UpdateSeasonTicketRequestDTO seasonTicketRequest) {
        if (seasonTicketRequest == null) {
            return null;
        }
        SeasonTicket seasonTicket = new SeasonTicket();
        seasonTicket.setName(seasonTicketRequest.getName());
        seasonTicket.setPromoterReference(seasonTicketRequest.getReference());
        if (Objects.nonNull(seasonTicketRequest.getContact())) {
            seasonTicket.setContactPersonName(seasonTicketRequest.getContact().getName());
            seasonTicket.setContactPersonSurname(seasonTicketRequest.getContact().getSurname());
            seasonTicket.setContactPersonEmail(seasonTicketRequest.getContact().getEmail());
            seasonTicket.setContactPersonPhone(seasonTicketRequest.getContact().getPhoneNumber());
        }

        fillSettings(seasonTicketRequest, seasonTicket);


        return seasonTicket;
    }

    public static SeasonTicketStatus toMsEvent(SeasonTicketStatusDTO seasonTicketStatusDTO){
        return SeasonTicketStatus.valueOf(seasonTicketStatusDTO.name());
    }

    private static void fillSettings(UpdateSeasonTicketRequestDTO seasonTicketRequest, SeasonTicket seasonTicket) {
        if (Objects.nonNull(seasonTicketRequest.getSettings())) {
            seasonTicket.setBooking(bookingToMsEvent(seasonTicketRequest.getSettings()));
            if (Objects.nonNull(seasonTicketRequest.getSettings().getSubscriptionList())) {
                seasonTicket.setEnableSubscriptionList(seasonTicketRequest.getSettings().getSubscriptionList().getEnable());
                seasonTicket.setSubscriptionListId(seasonTicketRequest.getSettings().getSubscriptionList().getId());
            }
            if (seasonTicketRequest.getSettings().getSalesGoal() != null) {
                seasonTicket.setSalesGoalTickets(seasonTicketRequest.getSettings().getSalesGoal().getTickets());
                seasonTicket.setSalesGoalRevenue(seasonTicketRequest.getSettings().getSalesGoal().getRevenue());
            }
            if (seasonTicketRequest.getSettings().getCategories() != null) {
                if (seasonTicketRequest.getSettings().getCategories().getBase() != null) {
                    seasonTicket.setCategory(new Category(seasonTicketRequest.getSettings().getCategories().getBase().getId()));
                }
                if (seasonTicketRequest.getSettings().getCategories().getCustom() != null) {
                    seasonTicket.setCustomCategory(new Category(seasonTicketRequest.getSettings().getCategories().getCustom().getId()));
                }
            }
            TourSettingsDTO tour = seasonTicketRequest.getSettings().getTour();
            if (tour != null && tour.getEnable() != null) {
                if (!tour.getEnable()) {
                    seasonTicket.setTour(new IdNameDTO());
                } else if (tour.getId() != null) { //only enable if enable=true & tourId is in the request
                    seasonTicket.setTour(new IdNameDTO(tour.getId()));
                }
            }
            seasonTicket.setUseProducerFiscalData(seasonTicketRequest.getSettings().getUseProducerFiscalData());
            UpdateSeasonTicketOperativeDTO operative = seasonTicketRequest.getSettings().getOperative();
            if (operative != null) {
                seasonTicketRequest.getSettings().setZoneId(UTC);
                seasonTicketRequest.convertDates();
                if (operative.getBooking() != null) {
                    seasonTicket.setBookingEnabled(operative.getBooking().getEnable());
                    seasonTicket.setBookingEndDate(operative.getBooking().getEndDate());
                    seasonTicket.setBookingStartingDate(operative.getBooking().getStartDate());
                }
                if (operative.getRelease() != null) {
                    seasonTicket.setChannelPublishingDate(operative.getRelease().getDate());
                    seasonTicket.setEnableChannels(operative.getRelease().getEnable());
                }
                if (operative.getSale() != null) {
                    seasonTicket.setEnableSales(operative.getSale().getEnable());
                    seasonTicket.setSalesEndDate(operative.getSale().getEndDate());
                    seasonTicket.setSalesStartingDate(operative.getSale().getStartDate());
                }
                if (operative.getSecondaryMarket() != null) {
                    seasonTicket.setEnableSecondaryMarketSale(operative.getSecondaryMarket().getEnable());
                    seasonTicket.setSecondaryMarketSaleStartingDate(operative.getSecondaryMarket().getStartDate());
                    seasonTicket.setSecondaryMarketSaleEndDate(operative.getSecondaryMarket().getEndDate());
                }
                seasonTicket.setRegisterMandatory(operative.getRegisterMandatory());
                seasonTicket.setCustomerMaxSeats(operative.getCustomerMaxSeats());
                seasonTicket.setAllowTransferTicket(operative.getAllowTransfer());
                seasonTicket.setAllowReleaseSeat(operative.getAllowReleaseSeat());
            }

            if (seasonTicketRequest.getSettings().getInteractiveVenue() != null
                    && seasonTicketRequest.getSettings().getInteractiveVenue().getAllowInteractiveVenue() != null) {
                EventVenueViewConfig venueViewConfig = new EventVenueViewConfig();
                SettingsInteractiveVenueDTO interactiveVenue = seasonTicketRequest.getSettings().getInteractiveVenue();

                if (BooleanUtils.isTrue(interactiveVenue.getAllowInteractiveVenue())) {
                    if (InteractiveVenueType.VENUE_3D_MMC_V1.equals(interactiveVenue.getInteractiveVenueType())) {
                        ConverterUtils.updateField(venueViewConfig::setUse3dVenueModule, interactiveVenue.getAllowVenue3dView());
                        venueViewConfig.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_MMC_V1);
                        }
                    if (InteractiveVenueType.VENUE_3D_MMC_V2.equals(interactiveVenue.getInteractiveVenueType())) {
                        ConverterUtils.updateField(venueViewConfig::setUse3dVenueModuleV2, interactiveVenue.getAllowVenue3dView());
                        venueViewConfig.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_MMC_V2);
                    }
                    if (InteractiveVenueType.VENUE_3D_PACIFA.equals(interactiveVenue.getInteractiveVenueType())) {
                        venueViewConfig.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_PACIFA);
                }
                    ConverterUtils.updateField(venueViewConfig::setUseVenue3dView, interactiveVenue.getAllowVenue3dView());
                venueViewConfig.setUseSector3dView(CommonUtils.isTrue(interactiveVenue.getAllowSector3dView()));
                venueViewConfig.setUseSeat3dView(CommonUtils.isTrue(interactiveVenue.getAllowSeat3dView()));
                } else {
                    venueViewConfig.setInteractiveVenueType(null);
                    venueViewConfig.setUse3dVenueModule(Boolean.FALSE);
                    venueViewConfig.setUse3dVenueModuleV2(Boolean.FALSE);
                    venueViewConfig.setUseVenue3dView(Boolean.FALSE);
                    venueViewConfig.setUseSector3dView(Boolean.FALSE);
                    venueViewConfig.setUseSeat3dView(Boolean.FALSE);
                }

                seasonTicket.setEventVenueViewConfig(venueViewConfig);
            }
            seasonTicket.setInvitationUseTicketTemplate(seasonTicketRequest.getSettings().getInvitationUseTicketTemplate());

            if (seasonTicketRequest.getSettings().getPresalesRedirectionPolicy() != null) {
                PresalesRedirectionPolicy presalesRedirectionPolicy = new PresalesRedirectionPolicy();

                presalesRedirectionPolicy.setValue(seasonTicketRequest.getSettings().getPresalesRedirectionPolicy().getValue());
                if (seasonTicketRequest.getSettings().getPresalesRedirectionPolicy().getMode() != null)
                    presalesRedirectionPolicy.setMode(PresalesLinkMode.valueOf(seasonTicketRequest.getSettings().getPresalesRedirectionPolicy().getMode().name()));

                seasonTicket.setPresalesRedirectionPolicy(presalesRedirectionPolicy);
            }
            seasonTicket.setInvoicePrefixId(seasonTicketRequest.getSettings().getSimplifiedInvoicePrefix());
        }
    }

    private static Booking bookingToMsEvent(UpdateSeasonTicketsSettingsDTO source) {
        Booking booking = null;
        if (source != null && source.getBookings() != null) {
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


    private static BookingSettingsDTO bookingFromMsEvent(SeasonTicket seasonTicket) {
        BookingSettingsDTO bookingResponse = null;
        if (seasonTicket.getBooking() != null) {
            bookingResponse = new BookingSettingsDTO();

            bookingResponse.setEnable(seasonTicket.getBooking().getAllowed());

            if (seasonTicket.getBooking() != null) {
                bookingResponse.setBookingExpiration(getBookingExpiration(seasonTicket));
            }
        }
        return bookingResponse;
    }

    private static BookingExpiration getBookingExpiration(SeasonTicket seasonTicket) {
        BookingExpiration bookingExpiration = new BookingExpiration();

        if (seasonTicket == null || seasonTicket.getBooking() == null) {
            return bookingExpiration;
        }

        if (seasonTicket.getBooking().getOrderExpirationTimespan() != null || seasonTicket.getBooking().getOrderExpirationHour() != null
                || seasonTicket.getBooking().getOrderExpirationTimespanAmount() != null || seasonTicket.getBooking().getOrderExpirationType() != null) {
            BookingOrderExpiration bookingOrderExpiration = new BookingOrderExpiration();
            String orderExpirationTimespan = seasonTicket.getBooking().getOrderExpirationTimespan() != null
                    ? seasonTicket.getBooking().getOrderExpirationTimespan().name()
                    : null;
            if (orderExpirationTimespan != null) {
                bookingOrderExpiration.setTimespan(TimespanBookingOrderExpiration.valueOf(orderExpirationTimespan));
            }
            bookingOrderExpiration.setTimespanAmount(seasonTicket.getBooking().getOrderExpirationTimespanAmount());
            bookingOrderExpiration.setExpirationTime(seasonTicket.getBooking().getOrderExpirationHour());
            bookingOrderExpiration.setOrderExpirationType(seasonTicket.getBooking().getOrderExpirationType());
            bookingExpiration.setBookingOrderExpiration(bookingOrderExpiration);
        }

        bookingExpiration.setDate(seasonTicket.getBooking().getFixedDate());
        bookingExpiration.setExpirationDeadlineType(seasonTicket.getBooking().getExpirationType());

        if (seasonTicket.getBooking().getSessionExpirationTimespan() != null || seasonTicket.getBooking().getSessionExpirationTimespanAmount() != null
                || seasonTicket.getBooking().getSessionExpirationType() != null || seasonTicket.getBooking().getSessionExpirationHour() != null) {
            BookingSessionExpiration bookingSessionExpiration = new BookingSessionExpiration();
            bookingSessionExpiration.setExpirationTime(seasonTicket.getBooking().getSessionExpirationHour());
            bookingSessionExpiration.setSessionTimeFrame(SessionTimeFrame.valueOf(seasonTicket.getBooking().getSessionExpirationTimespan().name()));

            if (seasonTicket.getBooking().getSessionExpirationType() == SessionTypeExpiration.AFTER) {
                bookingSessionExpiration.setTimespanAmount(seasonTicket.getBooking().getSessionExpirationTimespanAmount());
            } else {
                bookingSessionExpiration.setTimespanAmount(seasonTicket.getBooking().getSessionExpirationTimespanAmount() * (-1));
            }

            bookingExpiration.setBookingSessionExpiration(bookingSessionExpiration);
        }
        return bookingExpiration;
    }
}