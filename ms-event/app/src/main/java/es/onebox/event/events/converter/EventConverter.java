package es.onebox.event.events.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.record.EventLanguageRecord;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.domain.eventconfig.ChangeSeatExpiryTime;
import es.onebox.event.events.domain.eventconfig.ChangeSeatNewTicketSelection;
import es.onebox.event.events.domain.eventconfig.ChangeSeatPrice;
import es.onebox.event.events.domain.eventconfig.ChangeSeatRefund;
import es.onebox.event.events.domain.eventconfig.ChangeSeatVoucherExpiry;
import es.onebox.event.events.domain.eventconfig.EventChangeSeatConfig;
import es.onebox.event.events.domain.eventconfig.EventChangeSeatExpiry;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.domain.eventconfig.EventTransferTicketConfig;
import es.onebox.event.events.domain.eventconfig.ReallocationChannel;
import es.onebox.event.events.dto.AccommodationsConfigDTO;
import es.onebox.event.events.dto.AccommodationsVendor;
import es.onebox.event.events.dto.BookingDTO;
import es.onebox.event.events.dto.CategoryDTO;
import es.onebox.event.events.dto.ChangeSeatExpiryTimeDTO;
import es.onebox.event.events.dto.ChangeSeatNewTicketSelectionDTO;
import es.onebox.event.events.dto.ChangeSeatPriceDTO;
import es.onebox.event.events.dto.ChangeSeatRefundDTO;
import es.onebox.event.events.dto.ChangeSeatVoucherExpiryDTO;
import es.onebox.event.events.dto.CreateEventRequestDTO;
import es.onebox.event.events.dto.DateDTO;
import es.onebox.event.events.dto.DatesDTO;
import es.onebox.event.events.dto.EventChangeSeatDTO;
import es.onebox.event.events.dto.EventChangeSeatExpiryDTO;
import es.onebox.event.events.dto.EventConfigDTO;
import es.onebox.event.events.dto.EventDTO;
import es.onebox.event.events.dto.EventExternalConfigDTO;
import es.onebox.event.events.dto.EventTicketTemplatesDTO;
import es.onebox.event.events.dto.EventTransferTicketDTO;
import es.onebox.event.events.dto.ReallocationChannelDTO;
import es.onebox.event.events.dto.TimeZoneDTO;
import es.onebox.event.events.dto.UpdateEventRequestDTO;
import es.onebox.event.events.dto.VenueDTO;
import es.onebox.event.events.enums.BookingExpirationType;
import es.onebox.event.events.enums.BookingOrderExpiration;
import es.onebox.event.events.enums.BookingOrderTimespan;
import es.onebox.event.events.enums.BookingSessionExpiration;
import es.onebox.event.events.enums.BookingSessionTimespan;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.SessionPackType;
import es.onebox.event.events.enums.TaxModeDTO;
import es.onebox.event.events.postbookingquestions.dto.PostBookingQuestionsConfigDTO;
import es.onebox.event.sessions.dto.RestrictionsDTO;
import es.onebox.event.sessions.dto.TicketTemplateSettingsDTO;
import es.onebox.event.sessions.enums.TicketTemplateExtraPageType;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static es.onebox.core.utils.common.CommonUtils.isTrue;
import static es.onebox.event.common.utils.ConverterUtils.isTrueAsByte;
import static es.onebox.event.common.utils.ConverterUtils.longToInt;
import static es.onebox.event.common.utils.ConverterUtils.updateField;

public class EventConverter {

    public static final Byte ONE = (byte) 1;
    public static final Integer DEFAULT_GROUP_TYPE = 1;

    private EventConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static EventDTO fromEntity(List<EventLanguageRecord> records, EventDTO target) {
        if (records != null) {
            target.setLanguages(records.stream()
                    .map(EventLanguageConverter::fromEntity)
                    .collect(Collectors.toList()));
        }
        return target;
    }

    public static EventDTO fromEntity(Map.Entry<EventRecord, List<VenueRecord>> record) {
        if (record == null) {
            return null;
        }

        EventRecord eventRecord = record.getKey();

        EventDTO target = fromEntity(eventRecord);

        target.setEntityName(eventRecord.getEntityName());

        //dates
        target.setDate(new DatesDTO());
        target.getDate().setStart(fillDate(eventRecord.getFechainicio(), eventRecord.getFechainiciotz(),
                eventRecord.getStartDateTZ(), eventRecord.getStartDateTZDesc(), eventRecord.getStartDateTZOffset()));
        target.getDate().setEnd(fillDate(eventRecord.getFechafin(), eventRecord.getFechafintz(),
                eventRecord.getEndDateTZ(), eventRecord.getEndDateTZDesc(), eventRecord.getEndDateTZOffset()));
        target.setUseTieredPricing(eventRecord.getUsetieredpricing() != null ? ConverterUtils.isByteAsATrue(eventRecord.getUsetieredpricing()) : null);
        //categories
        target.setCategory(getCategory(
                eventRecord.getIdtaxonomia(), eventRecord.getCategoryDescription(), eventRecord.getCategoryCode()));
        target.setCustomCategory(getCategory(
                eventRecord.getIdtaxonomiapropia(), eventRecord.getCustomCategoryDescription(), eventRecord.getCustomCategoryRef()));
        //tour
        if (eventRecord.getIdgira() != null) {
            target.setTour(new IdNameDTO(eventRecord.getIdgira().longValue(), eventRecord.getTourName()));
        }
        //producer
        if (eventRecord.getIdpromotor() != null) {
            target.setProducer(new IdNameDTO(eventRecord.getIdpromotor().longValue(), eventRecord.getPromoterName()));
        }

        if (eventRecord.getIdlistasubscripcion() != null) {
            target.setEnableSubscriptionList(Boolean.TRUE);
            target.setSubscriptionListId(eventRecord.getIdlistasubscripcion());
        } else {
            target.setEnableSubscriptionList(Boolean.FALSE);
        }

        fillVenues(record.getValue(), target);

        target.setTicketTemplates(buildEventTicketTemplates(eventRecord));

        return target;
    }

    public static CpanelEventoRecord toRecord(CreateEventRequestDTO event) {
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();

        eventRecord.setNombre(event.getName());
        eventRecord.setReferenciapromotor(event.getPromoterReference());
        eventRecord.setTipoevento(event.getType().getId());
        eventRecord.setIdentidad(event.getEntityId().intValue());
        eventRecord.setIdpromotor(event.getProducerId().intValue());
        eventRecord.setIdtaxonomia(event.getCategoryId());
        eventRecord.setPreciogrupos(DEFAULT_GROUP_TYPE);
        eventRecord.setPermitegrupos((byte) 0);
        eventRecord.setNombreresponsable(event.getContactPersonName());
        eventRecord.setApellidosresponsable(event.getContactPersonSurname());
        eventRecord.setEmailresponsable(event.getContactPersonEmail());
        eventRecord.setTelefonoresponsable(event.getContactPersonPhone());
        eventRecord.setEstado(EventStatus.PLANNED.getId());
        eventRecord.setFechaalta(new Timestamp(new Date().getTime()));
        eventRecord.setTipoabono(SessionPackType.DISABLED.getId());
        eventRecord.setAforo(0);
        eventRecord.setArchivado((byte) 0);
        eventRecord.setInvitacionusaplantillaticket((byte) 1);
        eventRecord.setUsardatosfiscalesproductor((byte) 1);
        eventRecord.setUsetieredpricing((byte) 0);
        eventRecord.setInvoiceprefixid(event.getInvoicePrefixId());
        eventRecord.setIdcurrency((event.getCurrencyId() != null) ? event.getCurrencyId().intValue() : null);
        return eventRecord;
    }

    public static EventConfigDTO toEventConfigDTO(EventConfig source) {
        EventConfigDTO response = new EventConfigDTO();
        response.setEventId(source.getEventId());
        response.setInventoryProvider(source.getInventoryProvider());
        response.setUseSector3dView(source.isUseSector3dView() ? true : null);
        response.setUseSeat3dView(source.isUseSeat3dView() ? true : null);
        response.setUse3dVenueModule(source.isUse3dVenueModule() ? true : null);
        response.setUse3dVenueModuleV2(source.isUse3dVenueModuleV2() ? true : null);
        response.setUseVenue3dView(source.isUseVenue3dView() ? true : null);
        response.setInteractiveVenueType(source.getInteractiveVenueType());
        response.setVenue3dId(source.getVenue3dId());
        response.setCustomSelectTemplate(source.getCustomSelectTemplate());
        response.setMaxMembers(source.getMaxMembers());
        response.setUpsellingPriceZones(source.getUpsellingPriceZones());
        if (source.getPostBookingQuestionsConfig() != null) {
            PostBookingQuestionsConfigDTO postBookingQuestionsConfigDTO = new PostBookingQuestionsConfigDTO();
            postBookingQuestionsConfigDTO.setEnabled(source.getPostBookingQuestionsConfig().getEnabled());
            postBookingQuestionsConfigDTO.setType(source.getPostBookingQuestionsConfig().getType());
            response.setPostBookingQuestionsConfig(postBookingQuestionsConfigDTO);
        }
        if (source.getTicketTemplateSettings() != null) {
            TicketTemplateSettingsDTO ticketTemplateSettingsDTO = new TicketTemplateSettingsDTO();
            if (CollectionUtils.isNotEmpty(source.getTicketTemplateSettings().getExtraPages())) {
                ticketTemplateSettingsDTO.setExtraPages(
                        source.getTicketTemplateSettings().getExtraPages().stream()
                                .map(type -> TicketTemplateExtraPageType.valueOf(type.name()))
                                .collect(Collectors.toSet())
                );
            }
            response.setTicketTemplateSettings(ticketTemplateSettingsDTO);
        }
        if (source.getRestrictions() != null) {
            RestrictionsDTO restrictions = new RestrictionsDTO();
            restrictions.setPriceZones(source.getRestrictions().getPriceZones());
            restrictions.setCountryConfig(source.getRestrictions().getCountryConfig());
            restrictions.setSale(source.getRestrictions().getSale());
            response.setRestrictions(restrictions);
        }
        if (source.getAccommodationsConfig() != null) {
            AccommodationsConfigDTO accommodations = new AccommodationsConfigDTO();
            accommodations.setEnabled(source.getAccommodationsConfig().getEnabled());
            accommodations.setVendor(AccommodationsVendor.valueOf(source.getAccommodationsConfig().getVendor().name()));
            accommodations.setValue(source.getAccommodationsConfig().getValue());
            response.setAccommodationsConfig(accommodations);
        }
        if (source.getEventExternalConfig() != null) {
            response.setEventExternalConfig(new EventExternalConfigDTO());
            response.getEventExternalConfig().setDigitalTicketMode(source.getEventExternalConfig().getDigitalTicketMode());
        }

        if (source.getEventTransferTicketConfig() != null) {
            EventTransferTicketConfig eventTransferTicketConfig = source.getEventTransferTicketConfig();
            response.setAllowTransferTicket(eventTransferTicketConfig.getAllowTransferTicket());
            EventTransferTicketDTO eventTransferTicketDTO = new EventTransferTicketDTO();
            eventTransferTicketDTO.setMaxTicketTransfers(eventTransferTicketConfig.getMaxTicketTransfers());
            eventTransferTicketDTO.setEnableMaxTicketTransfers(eventTransferTicketConfig.getEnableMaxTicketTransfers());
            eventTransferTicketDTO.setTransferTicketMinDelayTime(eventTransferTicketConfig.getTransferTicketMinDelayTime());
            eventTransferTicketDTO.setTransferTicketMaxDelayTime(eventTransferTicketConfig.getTransferTicketMaxDelayTime());
            eventTransferTicketDTO.setRecoveryTicketMaxDelayTime(eventTransferTicketConfig.getRecoveryTicketMaxDelayTime());
            eventTransferTicketDTO.setTransferPolicy(eventTransferTicketConfig.getTransferPolicy());
            eventTransferTicketDTO.setRestrictTransferBySessions(eventTransferTicketConfig.getRestrictTransferBySessions());
            eventTransferTicketDTO.setAllowMultipleTransfers(eventTransferTicketConfig.getAllowMultipleTransfers());
            eventTransferTicketDTO.setAllowedTransferSessions(eventTransferTicketConfig.getAllowedTransferSessions());
            response.setTransfer(eventTransferTicketDTO);
        }

        response.setPhoneVerificationRequired(source.getPhoneVerificationRequired());
        return response;
    }

    public static void updateRecord(CpanelEventoRecord eventRecord, UpdateEventRequestDTO event) {
        if (eventRecord.getUsardatosfiscalesproductor() != null && ConverterUtils.isByteAsATrue(eventRecord.getUsardatosfiscalesproductor())
                && event.getUseProducerFiscalData() != null && !event.getUseProducerFiscalData()) {
            eventRecord.setInvoiceprefixid(null);
        } else {
            updateField(eventRecord::setInvoiceprefixid, event.getInvoicePrefixId());
        }

        updateField(eventRecord::setNombre, event.getName());
        updateField(eventRecord::setReferenciapromotor, event.getPromoterReference());
        updateField(eventRecord::setEstado, event.getStatus() != null ? event.getStatus().getId() : null);
        updateField(eventRecord::setNombreresponsable, event.getContactPersonName());
        updateField(eventRecord::setApellidosresponsable, event.getContactPersonSurname());
        updateField(eventRecord::setEmailresponsable, event.getContactPersonEmail());
        updateField(eventRecord::setTelefonoresponsable, event.getContactPersonPhone());
        updateField(eventRecord::setIdtaxonomia, event.getCategory() != null ? event.getCategory().getId().intValue() : null);
        updateField(eventRecord::setIdtaxonomiapropia, event.getCustomCategory() != null ? event.getCustomCategory().getId().intValue() : null);
        updateField(eventRecord::setObjetivosobreentradas, event.getSalesGoalTickets());
        updateField(eventRecord::setObjetivosobreventas, event.getSalesGoalRevenue());
        updateField(eventRecord::setTipoabono, event.getSessionPackType() != null ? event.getSessionPackType().getId() : null);
        updateField(eventRecord::setPermitirinformesrecinto, isTrueAsByte(event.getAllowVenueReport()));
        updateField(eventRecord::setUsardatosfiscalesproductor, isTrueAsByte(event.getUseProducerFiscalData()));
        updateField(eventRecord::setUsetieredpricing, event.getUseTieredPricing() != null ? isTrueAsByte(event.getUseTieredPricing()) : null);
        updateField(eventRecord::setInvitacionusaplantillaticket, isTrueAsByte(event.getInvitationUseTicketTemplate()));
        updateField(eventRecord::setEssupraevento, event.getSupraEvent() != null ? isTrueAsByte(event.getSupraEvent()) : null);
        updateField(eventRecord::setIdlistasubscripcion, event.getSubscriptionListId());
        updateField(eventRecord::setPreciogrupos, eventRecord.getPreciogrupos() == null && BooleanUtils.isTrue(event.getAllowGroups()) && event.getGroupPrice() == null
                ? DEFAULT_GROUP_TYPE
                : event.getGroupPrice());
        updateField(eventRecord::setPermitegrupos, isTrueAsByte(event.getAllowGroups()));
        updateField(eventRecord::setAcompanyantesgrupopagan, event.getGroupCompanionPayment() != null ? isTrueAsByte(event.getGroupCompanionPayment()) : null);
        updateField(eventRecord::setArchivado, isTrueAsByte(event.getArchived()));
        updateField(eventRecord::setIdcurrency, event.getCurrencyId() != null ? event.getCurrencyId().intValue() : null);
        updateField(eventRecord::setExternalreference, event.getExternalReference());

        if (Boolean.FALSE.equals(event.getEnableSubscriptionList())) {
            eventRecord.setIdlistasubscripcion(null);
        } else if (event.getSubscriptionListId() != null && (Boolean.TRUE.equals(event.getEnableSubscriptionList()) || eventRecord.getIdlistasubscripcion() != null)) {
            eventRecord.setIdlistasubscripcion(event.getSubscriptionListId());
        }

        updateBookingFields(eventRecord, event);
        if (event.getTour() != null) {
            eventRecord.setIdgira(event.getTour().getId() != null ? event.getTour().getId().intValue() : null);
        }
        if (event.getStatus() != null && !eventRecord.getEstado().equals(event.getStatus().getId())) {
            eventRecord.setFechacambioestado(Timestamp.from(ZonedDateTime.now().toInstant()));
        }
        EventTicketTemplatesDTO templates = event.getTicketTemplates();
        if (templates != null) {
            updateField(eventRecord::setIdplantillaticket, longToInt(templates.getIndividualTicketPdfTemplateId()));
            updateField(eventRecord::setIdplantillatickettaquilla, longToInt(templates.getIndividualTicketPrinterTemplateId()));
            updateField(eventRecord::setIdplantillaticketgrupos, longToInt(templates.getGroupTicketPdfTemplateId()));
            updateField(eventRecord::setIdplantillatickettaquillagrupos, longToInt(templates.getGroupTicketPrinterTemplateId()));
            updateField(eventRecord::setIdplantillaticketinvitacion, longToInt(templates.getIndividualInvitationPdfTemplateId()));
            updateField(eventRecord::setIdplantillatickettaquillainvitacion, longToInt(templates.getIndividualInvitationPrinterTemplateId()));
            updateField(eventRecord::setIdplantillaticketinvitaciongrupos, longToInt(templates.getGroupInvitationPdfTemplateId()));
            updateField(eventRecord::setIdplantillatickettaquillainvitaciongrupos, longToInt(templates.getGroupInvitationPrinterTemplateId()));
        }

        if (EventStatus.DELETED.equals(event.getStatus()) || EventStatus.FINISHED.equals(event.getStatus())) {
            eventRecord.setIdexterno(null);
        }

        if (event.getTaxMode() != null ) {
            eventRecord.setTaxmode(event.getTaxMode().getId());
        }
    }

    public static EventChangeSeatDTO toEventChangeSeatDTO(EventChangeSeatConfig eventChangeSeatConfig) {

        EventChangeSeatDTO changeSeatDTO = new EventChangeSeatDTO();

        if (eventChangeSeatConfig != null) {
            if (eventChangeSeatConfig.getEventChangeSeatExpiry() != null) {
                EventChangeSeatExpiryDTO seatExpiryDTO = new EventChangeSeatExpiryDTO();
                EventChangeSeatExpiry expiry = eventChangeSeatConfig.getEventChangeSeatExpiry();
                seatExpiryDTO.setTimeOffsetLimitAmount(expiry.getTimeOffsetLimitAmount());
                seatExpiryDTO.setTimeOffsetLimitUnit(expiry.getTimeOffsetLimitUnit());
                changeSeatDTO.setEventChangeSeatExpiry(seatExpiryDTO);
            }
            if (eventChangeSeatConfig.getChangeType() != null) {
                changeSeatDTO.setChangeType(eventChangeSeatConfig.getChangeType());
            }
            if (eventChangeSeatConfig.getNewTicketSelection() != null) {
                ChangeSeatNewTicketSelectionDTO eventNewTicketSelectionDTO = new ChangeSeatNewTicketSelectionDTO();
                ChangeSeatNewTicketSelection changeSeatNewTicketSelection = eventChangeSeatConfig.getNewTicketSelection();
                eventNewTicketSelectionDTO.setAllowedSessions(changeSeatNewTicketSelection.getAllowedSessions());
                eventNewTicketSelectionDTO.setSameDateOnly(changeSeatNewTicketSelection.getSameDateOnly());
                eventNewTicketSelectionDTO.setTickets(changeSeatNewTicketSelection.getTickets());

                eventNewTicketSelectionDTO.setPrice(getPrice(changeSeatNewTicketSelection));
                changeSeatDTO.setNewTicketSelection(eventNewTicketSelectionDTO);
            }
            if (eventChangeSeatConfig.getReallocationChannel() != null) {
                ReallocationChannelDTO reallocationChannelDTO =  new ReallocationChannelDTO();
                reallocationChannelDTO.setId(eventChangeSeatConfig.getReallocationChannel().getId());
                reallocationChannelDTO.setApplyToAllChannelTypes(eventChangeSeatConfig.getReallocationChannel().getApplyToAllChannelTypes());
                changeSeatDTO.setReallocationChannel(reallocationChannelDTO);
            }
        }

        return changeSeatDTO;
    }

    private static ChangeSeatPriceDTO getPrice(ChangeSeatNewTicketSelection changeSeatNewTicketSelection) {
        ChangeSeatPriceDTO priceDTO = new ChangeSeatPriceDTO();
        priceDTO.setType(changeSeatNewTicketSelection.getPrice().getType());
        if (changeSeatNewTicketSelection.getPrice().getRefund() != null) {
            ChangeSeatRefundDTO refundDTO = new ChangeSeatRefundDTO();
            ChangeSeatRefund refund = changeSeatNewTicketSelection.getPrice().getRefund();
            refundDTO.setType(refund.getType());
            refundDTO.setVoucherExpiry(getVoucherExpiricy(refund));
            priceDTO.setRefund(refundDTO);
        }
        return priceDTO;
    }

    private static ChangeSeatVoucherExpiryDTO getVoucherExpiricy(ChangeSeatRefund refund) {
        ChangeSeatVoucherExpiryDTO eventVoucherExpiryDTO = new ChangeSeatVoucherExpiryDTO();
        if (refund.getVoucherExpiry() != null) {
            eventVoucherExpiryDTO.setEnabled(refund.getVoucherExpiry().getEnabled());
            if (refund.getVoucherExpiry().getExpiryTime() != null) {
                ChangeSeatExpiryTimeDTO expiryTimeDTO = new ChangeSeatExpiryTimeDTO();
                expiryTimeDTO.setTimeOffsetLimitAmount(refund.getVoucherExpiry().getExpiryTime().getTimeOffsetLimitAmount());
                expiryTimeDTO.setTimeOffsetLimitUnit(refund.getVoucherExpiry().getExpiryTime().getTimeOffsetLimitUnit());
                eventVoucherExpiryDTO.setExpiryTime(expiryTimeDTO);
            }
        }
        return eventVoucherExpiryDTO;
    }

    public static EventChangeSeatConfig toEventChangeSeat(EventChangeSeatDTO changeSeatDTO) {

        EventChangeSeatConfig changeSeatConfig = new EventChangeSeatConfig();

        if (changeSeatDTO.getEventChangeSeatExpiry() != null) {
            EventChangeSeatExpiry changeSeatExpiry = new EventChangeSeatExpiry();
            changeSeatExpiry.setTimeOffsetLimitAmount(changeSeatDTO.getEventChangeSeatExpiry().getTimeOffsetLimitAmount());
            changeSeatExpiry.setTimeOffsetLimitUnit(changeSeatDTO.getEventChangeSeatExpiry().getTimeOffsetLimitUnit());
            changeSeatConfig.setEventChangeSeatExpiry(changeSeatExpiry);
        }
        changeSeatConfig.setChangeType(changeSeatDTO.getChangeType());
        ChangeSeatNewTicketSelection changeSeatNewTicketSelection = new ChangeSeatNewTicketSelection();
        ChangeSeatNewTicketSelectionDTO eventNewTicketSelectionDTO = changeSeatDTO.getNewTicketSelection();
        changeSeatNewTicketSelection.setAllowedSessions(eventNewTicketSelectionDTO.getAllowedSessions());
        changeSeatNewTicketSelection.setSameDateOnly(eventNewTicketSelectionDTO.getSameDateOnly());
        changeSeatNewTicketSelection.setTickets(eventNewTicketSelectionDTO.getTickets());

        ChangeSeatPrice price = new ChangeSeatPrice();
        price.setType(eventNewTicketSelectionDTO.getPrice().getType());
        price.setRefund(getRefund(eventNewTicketSelectionDTO));
        changeSeatNewTicketSelection.setPrice(price);
        changeSeatConfig.setNewTicketSelection(changeSeatNewTicketSelection);
        ReallocationChannel reallocationChannel = new ReallocationChannel();
        reallocationChannel.setId(changeSeatDTO.getReallocationChannel().getId());
        reallocationChannel.setApplyToAllChannelTypes(changeSeatDTO.getReallocationChannel().getApplyToAllChannelTypes());
        changeSeatConfig.setReallocationChannel(reallocationChannel);

        return changeSeatConfig;
    }

    private static ChangeSeatRefund getRefund(ChangeSeatNewTicketSelectionDTO eventNewTicketSelectionDTO) {
        if (eventNewTicketSelectionDTO.getPrice().getRefund() == null) {
            return null;
        }

        ChangeSeatRefund refund = new ChangeSeatRefund();
        ChangeSeatRefundDTO refundDTO = eventNewTicketSelectionDTO.getPrice().getRefund();
        refund.setType(refundDTO.getType());
        if (refundDTO.getVoucherExpiry() != null) {
            ChangeSeatVoucherExpiry changeSeatVoucherExpiry = new ChangeSeatVoucherExpiry();
            changeSeatVoucherExpiry.setEnabled(refundDTO.getVoucherExpiry().getEnabled());
            if (refundDTO.getVoucherExpiry().getExpiryTime() != null) {
                ChangeSeatExpiryTime expiryTime = new ChangeSeatExpiryTime();
                expiryTime.setTimeOffsetLimitAmount(refundDTO.getVoucherExpiry().getExpiryTime().getTimeOffsetLimitAmount());
                expiryTime.setTimeOffsetLimitUnit(refundDTO.getVoucherExpiry().getExpiryTime().getTimeOffsetLimitUnit());
                changeSeatVoucherExpiry.setExpiryTime(expiryTime);
            }
            refund.setVoucherExpiry(changeSeatVoucherExpiry);
        }
        return refund;
    }

    private static void updateBookingFields(CpanelEventoRecord eventRecord, UpdateEventRequestDTO event) {
        if (event.getBooking() != null) {
            BookingDTO booking = event.getBooking();
            updateField(eventRecord::setPermitereservas, isTrueAsByte(booking.getAllowed()));

            updateField(eventRecord::setTipocaducidadreserva,
                    booking.getOrderExpirationType() != null ? booking.getOrderExpirationType().getTipo() : null);
            updateField(eventRecord::setNumunidadescaducidad, booking.getOrderExpirationTimespanAmount());
            updateField(eventRecord::setTipounidadescaducidad,
                    booking.getOrderExpirationTimespan() != null ? booking.getOrderExpirationTimespan().getTipo() : null);
            updateField(eventRecord::setNumhorascaducidadreserva, booking.getOrderExpirationHour());

            if (booking.getExpirationType() != null) {
                updateField(eventRecord::setTipofechalimitereserva, booking.getExpirationType().getTipo());
                updateBookingExpirationFields(eventRecord, booking);
            }
        }
    }

    private static void updateBookingExpirationFields(CpanelEventoRecord eventRecord, BookingDTO booking) {
        if (BookingExpirationType.SESSION.equals(booking.getExpirationType())) {
            updateField(eventRecord::setNumunidadeslimite, booking.getSessionExpirationTimespanAmount());
            updateField(eventRecord::setTipounidadeslimite,
                    booking.getSessionExpirationTimespan() != null ? booking.getSessionExpirationTimespan().getTipo() : null);
            updateField(eventRecord::setTipolimite,
                    booking.getSessionExpirationType() != null ? booking.getSessionExpirationType().getTipo() : null);
            updateField(eventRecord::setNumhoraslimitereserva, booking.getSessionExpirationHour());
        } else if (BookingExpirationType.DATE.equals(booking.getExpirationType())) {
            updateField(eventRecord::setFechalimite,
                    booking.getFixedDate() != null ? Timestamp.from(booking.getFixedDate().toInstant()) : null);
        }
    }

    private static EventDTO fromEntity(CpanelEventoRecord record) {
        EventDTO target = new EventDTO();
        target.setId(record.getIdevento().longValue());
        target.setName(record.getNombre());
        target.setPromoterReference(record.getReferenciapromotor());
        target.setType(EventType.byId(record.getTipoevento()));
        target.setStatus(EventStatus.byId(record.getEstado()));
        if (record.getIdentidad() != null) {
            target.setEntityId(record.getIdentidad().longValue());
        }
        target.setExternalId(record.getIdexterno() != null ? record.getIdexterno().longValue() : null);
        target.setContactPersonEmail(record.getEmailresponsable());
        target.setContactPersonName(record.getNombreresponsable());
        target.setContactPersonSurname(record.getApellidosresponsable());
        target.setContactPersonPhone(record.getTelefonoresponsable());
        target.setSalesGoalTickets(record.getObjetivosobreentradas());
        target.setSalesGoalRevenue(record.getObjetivosobreventas());

        target.setAllowVenueReport(record.getPermitirinformesrecinto() != null ? isTrue(record.getPermitirinformesrecinto()) : null);
        target.setUseProducerFiscalData(record.getUsardatosfiscalesproductor() != null ? isTrue(record.getUsardatosfiscalesproductor()) : null);
        target.setSessionPackType(SessionPackType.byId(record.getTipoabono()));
        target.setSupraEvent(record.getEssupraevento() != null ? isTrue(record.getEssupraevento()) : null);
        target.setGiftTicket(record.getEntradaregalo() != null ? isTrue(record.getEntradaregalo()) : null);
        target.setInvitationUseTicketTemplate(record.getInvitacionusaplantillaticket() != null ? isTrue(record.getInvitacionusaplantillaticket()) : null);

        target.setAllowGroups(record.getPermitegrupos() != null ? isTrue(record.getPermitegrupos()) : null);
        target.setGroupPrice(record.getPreciogrupos());
        target.setGroupCompanionPayment(record.getAcompanyantesgrupopagan() != null ? isTrue(record.getAcompanyantesgrupopagan()) : null);
        target.setArchived(isTrue(record.getArchivado()));
        target.setCurrencyId(record.getIdcurrency());
        target.setExternalReference(record.getExternalreference());
        if (record.getInvoiceprefixid() != null) {
            target.setInvoicePrefixId(record.getInvoiceprefixid());
        }

        if (ONE.equals(record.getPermitereservas())) {
            target.setBooking(new BookingDTO());
            target.getBooking().setAllowed(true);
            target.getBooking().setOrderExpirationTimespanAmount(record.getNumunidadescaducidad());
            target.getBooking().setOrderExpirationHour(record.getNumhorascaducidadreserva());
            if (record.getTipocaducidadreserva() != null) {
                target.getBooking().setOrderExpirationType(BookingOrderExpiration.byId(record.getTipocaducidadreserva()));
            }
            if (record.getTipounidadescaducidad() != null) {
                target.getBooking().setOrderExpirationTimespan(BookingOrderTimespan.byId(record.getTipounidadescaducidad()));
            }

            target.getBooking().setSessionExpirationTimespanAmount(record.getNumunidadeslimite());
            target.getBooking().setSessionExpirationHour(record.getNumhoraslimitereserva());
            if (record.getTipofechalimitereserva() != null) {
                target.getBooking().setExpirationType(BookingExpirationType.byId(record.getTipofechalimitereserva()));
            }
            if (record.getTipounidadeslimite() != null) {
                target.getBooking().setSessionExpirationTimespan(BookingSessionTimespan.byId(record.getTipounidadeslimite()));
            }
            if (record.getTipolimite() != null) {
                target.getBooking().setSessionExpirationType(BookingSessionExpiration.byId(record.getTipolimite()));
            }
            target.getBooking().setFixedDate(CommonUtils.timestampToZonedDateTime(record.getFechalimite()));
        }
        target.setTaxMode(TaxModeDTO.fromId(record.getTaxmode()));

        return target;
    }

    private static CategoryDTO getCategory(Integer id, String description, String code) {
        CategoryDTO result = null;
        if (id != null) {
            result = new CategoryDTO();
            result.setId(id);
            result.setDescription(description);
            result.setCode(code);
        }
        return result;
    }

    private static DateDTO fillDate(Timestamp timestamp, Integer timeZoneId, String olsonId, String timeZoneName, Integer offset) {
        DateDTO result = null;
        if (timestamp != null) {
            result = new DateDTO();
            result.setDate(CommonUtils.timestampToZonedDateTime(timestamp));
            if (timeZoneId != null || olsonId != null) {
                TimeZoneDTO timeZoneFin = new TimeZoneDTO();
                timeZoneFin.setOlsonId(olsonId);
                timeZoneFin.setName(timeZoneName);
                timeZoneFin.setOffset(offset);
                result.setTimeZone(timeZoneFin);
            }
        }
        return result;
    }


    private static void fillVenues(List<VenueRecord> venueRecords, EventDTO target) {
        if (!CommonUtils.isEmpty(venueRecords)) {
            List<VenueDTO> venuesDTO = venueRecords
                    .stream()
                    .filter(venueRecord -> venueRecord.getVenueConfigId() != null)
                    .map(venueRecord -> {
                        VenueDTO venue = new VenueDTO();

                        venue.setId(venueRecord.getIdrecinto().longValue());
                        venue.setName(venueRecord.getNombre());
                        venue.setCountryId(venueRecord.getPais());
                        venue.setCity(venueRecord.getMunicipio());
                        venue.setGooglePlaceId(venueRecord.getGoogleplaceid());
                        venue.setConfigId(venueRecord.getVenueConfigId());
                        venue.setConfigName(venueRecord.getVenueConfigName());
                        venue.setConfigType(venueRecord.getVenueConfigType());
                        return venue;
                    })
                    .collect(Collectors.toList());
            target.setVenues(venuesDTO);
        }
    }


    private static EventTicketTemplatesDTO buildEventTicketTemplates(EventRecord eventRecord) {
        EventTicketTemplatesDTO templates = new EventTicketTemplatesDTO();
        boolean hasValues = setEventTicketTemplateId(eventRecord.getIdplantillaticket(), templates::setIndividualTicketPdfTemplateId);
        hasValues |= setEventTicketTemplateId(eventRecord.getIdplantillatickettaquilla(), templates::setIndividualTicketPrinterTemplateId);
        hasValues |= setEventTicketTemplateId(eventRecord.getIdplantillaticketgrupos(), templates::setGroupTicketPdfTemplateId);
        hasValues |= setEventTicketTemplateId(eventRecord.getIdplantillatickettaquillagrupos(), templates::setGroupTicketPrinterTemplateId);
        hasValues |= setEventTicketTemplateId(eventRecord.getIdplantillaticketinvitacion(), templates::setIndividualInvitationPdfTemplateId);
        hasValues |= setEventTicketTemplateId(eventRecord.getIdplantillatickettaquillainvitacion(), templates::setIndividualInvitationPrinterTemplateId);
        hasValues |= setEventTicketTemplateId(eventRecord.getIdplantillaticketinvitaciongrupos(), templates::setGroupInvitationPdfTemplateId);
        hasValues |= setEventTicketTemplateId(eventRecord.getIdplantillatickettaquillainvitaciongrupos(), templates::setGroupInvitationPrinterTemplateId);
        if (hasValues) {
            return templates;
        }
        return null;
    }

    private static boolean setEventTicketTemplateId(Integer templateId, Consumer<Long> setter) {
        if (templateId == null) {
            return false;
        }
        setter.accept(templateId.longValue());
        return true;
    }

}
