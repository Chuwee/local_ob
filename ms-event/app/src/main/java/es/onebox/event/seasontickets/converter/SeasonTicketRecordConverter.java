package es.onebox.event.seasontickets.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.converter.EventLanguageConverter;
import es.onebox.event.events.dao.record.EventLanguageRecord;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.dto.BookingDTO;
import es.onebox.event.events.dto.CategoryDTO;
import es.onebox.event.events.dto.DateDTO;
import es.onebox.event.events.dto.DatesDTO;
import es.onebox.event.events.dto.TimeZoneDTO;
import es.onebox.event.events.dto.VenueDTO;
import es.onebox.event.events.enums.BookingExpirationType;
import es.onebox.event.events.enums.BookingOrderExpiration;
import es.onebox.event.events.enums.BookingOrderTimespan;
import es.onebox.event.events.enums.BookingSessionExpiration;
import es.onebox.event.events.enums.BookingSessionTimespan;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.SessionPackType;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfig;
import es.onebox.event.seasontickets.dto.BaseSeasonTicketDTO;
import es.onebox.event.seasontickets.dto.CreateSeasonTicketRequestDTO;
import es.onebox.event.seasontickets.dto.MaxBuyingLimitDTO;
import es.onebox.event.seasontickets.dto.SearchSeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketTicketTemplatesDTO;
import es.onebox.event.seasontickets.dto.UpdateSeasonTicketRequestDTO;
import es.onebox.event.seasontickets.dto.changeseat.UpdateSeasonTicketChangeSeat;
import es.onebox.event.seasontickets.dto.renewals.RenewalType;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketChangeSeat;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewal;
import es.onebox.event.seasontickets.dto.renewals.UpdateSeasonTicketRenewal;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;
import org.apache.commons.lang3.BooleanUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static es.onebox.core.utils.common.CommonUtils.timestampToZonedDateTime;
import static es.onebox.core.utils.common.CommonUtils.zonedDateTimeToTimestamp;
import static es.onebox.event.common.utils.ConverterUtils.isTrueAsByte;
import static es.onebox.event.common.utils.ConverterUtils.longToInt;
import static es.onebox.event.common.utils.ConverterUtils.updateField;

public class SeasonTicketRecordConverter {

    private SeasonTicketRecordConverter(){ throw new UnsupportedOperationException("Cannot instantiate utilities class");}

    public static final Byte ONE = (byte) 1;

    public static SeasonTicketDTO fromEntity(List<EventLanguageRecord> records, SeasonTicketDTO target) {
        if (records != null) {
            target.setLanguages(records.stream()
                    .map(EventLanguageConverter::fromEntity)
                    .collect(Collectors.toList()));
        }
        return target;
    }

    public static CpanelEventoRecord toRecord(CreateSeasonTicketRequestDTO dto) {
        CpanelEventoRecord eventRecord = new CpanelEventoRecord();

        eventRecord.setNombre(dto.getName());
        eventRecord.setTipoevento(EventType.SEASON_TICKET.getId());
        eventRecord.setIdentidad(dto.getEntityId().intValue());
        eventRecord.setIdpromotor(dto.getProducerId().intValue());
        eventRecord.setIdtaxonomia(dto.getCategoryId());
        eventRecord.setIdtaxonomiapropia(dto.getCustomCategoryId());

        eventRecord.setNombreresponsable(dto.getContactPersonName());
        eventRecord.setApellidosresponsable(dto.getContactPersonSurname());
        eventRecord.setEmailresponsable(dto.getContactPersonEmail());
        eventRecord.setTelefonoresponsable(dto.getContactPersonPhone());

        eventRecord.setEstado(EventStatus.READY.getId());
        eventRecord.setFechaalta(new Timestamp(new Date().getTime()));
        eventRecord.setTipoabono(SessionPackType.UNRESTRICTED.getId());
        eventRecord.setAforo(0);
        eventRecord.setArchivado((byte) 0);
        eventRecord.setInvitacionusaplantillaticket((byte) 1);
        eventRecord.setUsardatosfiscalesproductor((byte) 1);
        eventRecord.setIdcurrency(dto.getCurrencyId());
        eventRecord.setInvoiceprefixid(dto.getInvoicePrefixId());

        return eventRecord;
    }

    public static SeasonTicketDTO fromEntity(Map.Entry<EventRecord, List<VenueRecord>> eventRecord, SessionRecord sessionRecord,
                                             SeasonTicketRenewalConfig renewalsConfig) {
        if (eventRecord == null) {
            return null;
        }

        //Event related fields
        SeasonTicketDTO target = (SeasonTicketDTO) fromEventRecord(eventRecord, new SeasonTicketDTO());
        //Season Related fields
        if (sessionRecord != null) {
            fromSessionRecord(target, sessionRecord);
            target.setSessionId(sessionRecord.getIdsesion());
        }

        if(eventRecord.getKey() != null) {
            target.setSeasonTicketTicketTemplatesDTO(buildSeasonTicketTicketTemplates(eventRecord.getKey()));
            setSeasonTicketCustomData(eventRecord, target, renewalsConfig);
        }

        return target;
    }

    public static void fromSessionRecord(BaseSeasonTicketDTO target, SessionRecord sessionRecord) {
        if(Objects.nonNull(sessionRecord.getVenueTZName()) || Objects.nonNull(sessionRecord.getVenueTZOffset()) || Objects.nonNull(sessionRecord.getVenueTZ())){
            TimeZoneDTO timeZoneDTO = new TimeZoneDTO();
            timeZoneDTO.setName(sessionRecord.getVenueTZName());
            timeZoneDTO.setOffset(sessionRecord.getVenueTZOffset());
            timeZoneDTO.setOlsonId(sessionRecord.getVenueTZ());
            target.setVenueTimeZone(timeZoneDTO);
        }
        target.setStatus(SeasonTicketStatusConverter.fromSessionStatus(SessionStatus.byId(sessionRecord.getEstado()),
                sessionRecord.getIspreview()));
        target.setChannelPublishingDate(timestampToZonedDateTime(sessionRecord.getFechapublicacion()));
        target.setSalesStartingDate(timestampToZonedDateTime(sessionRecord.getFechaventa()));
        target.setSalesEndDate(timestampToZonedDateTime(sessionRecord.getFechafinsesion()));
        target.setEnableSales(CommonUtils.isTrue(sessionRecord.getEnventa()));
        target.setEnableChannels(CommonUtils.isTrue(sessionRecord.getPublicado()));
        target.setBookingStartingDate(timestampToZonedDateTime(sessionRecord.getFechainicioreserva()));
        target.setBookingEndDate(timestampToZonedDateTime(sessionRecord.getFechafinreserva()));
        target.setBookingEnabled(CommonUtils.isTrue(sessionRecord.getReservasactivas()));

        if(Objects.nonNull(sessionRecord.getNummaxlocalidadescompra())) {
            MaxBuyingLimitDTO maxBuyingLimitDTO = new MaxBuyingLimitDTO();
            maxBuyingLimitDTO.setValue(sessionRecord.getNummaxlocalidadescompra());
            target.setMaxBuyingLimit(maxBuyingLimitDTO);
        }
    }

    public static SearchSeasonTicketDTO fromEventToSeasons(Map.Entry<EventRecord, List<VenueRecord>> record, SearchSeasonTicketDTO target) {
        if (record == null) {
            return null;
        }

        //Event related fields
        SearchSeasonTicketDTO seasonTicketDTO = (SearchSeasonTicketDTO) fromEventRecord(record, target);
        seasonTicketDTO.setAllowRenewal(record.getKey().getAllowRenewal());
        seasonTicketDTO.setAllowChangeSeat(record.getKey().getAllowChangeSeat());

        return target;
    }
    public static BaseSeasonTicketDTO fromEventRecord(Map.Entry<EventRecord, List<VenueRecord>> record, BaseSeasonTicketDTO target) {
        if (record == null) {
            return null;
        }

        EventRecord eventRecord = record.getKey();

        fromEntity(eventRecord, target);

        setContactData(eventRecord, target);
        target.setEntityName(eventRecord.getEntityName());
        target.setCurrencyId(eventRecord.getCurrencyId());

        //dates
        target.setDate(new DatesDTO());
        target.getDate().setStart(fillDate(eventRecord.getFechainicio(), eventRecord.getFechainiciotz(),
                eventRecord.getStartDateTZ(), eventRecord.getStartDateTZDesc(), eventRecord.getStartDateTZOffset()));
        target.getDate().setEnd(fillDate(eventRecord.getFechafin(), eventRecord.getFechafintz(),
                eventRecord.getEndDateTZ(), eventRecord.getEndDateTZDesc(), eventRecord.getEndDateTZOffset()));
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
        //category
        target.setCategory(getCategory(
                eventRecord.getIdtaxonomia(), eventRecord.getCategoryDescription(), eventRecord.getCategoryCode()));
        target.setCustomCategory(getCategory(
                eventRecord.getIdtaxonomiapropia(), eventRecord.getCustomCategoryDescription(), eventRecord.getCustomCategoryRef()));
        //tour
        if (eventRecord.getIdgira() != null) {
            target.setTour(new IdNameDTO(eventRecord.getIdgira().longValue(), eventRecord.getTourName()));
        }

        fillVenues(record.getValue(), target);

        target.setMemberMandatory(eventRecord.getMemberMandatory());
        target.setCustomerMaxSeats(eventRecord.getCustomerMaxSeats());
        target.setRegisterMandatory(eventRecord.getRegisterMandatory());
        if (eventRecord.getInvoiceprefixid() != null) {
            target.setInvoicePrefixId(eventRecord.getInvoiceprefixid().longValue());
        }
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

    private static BaseSeasonTicketDTO fromEntity(CpanelEventoRecord record, BaseSeasonTicketDTO target) {
        target.setId(record.getIdevento().longValue());
        target.setName(record.getNombre());
        target.setPromoterReference(record.getReferenciapromotor());
        if (record.getIdentidad() != null) {
            target.setEntityId(record.getIdentidad().longValue());
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
        target.setSalesGoalRevenue(NumberUtils.doubleToBigDecimal(record.getObjetivosobreventas()));
        target.setSalesGoalTickets(record.getObjetivosobreentradas());

        target.setUseProducerFiscalData(ONE.equals(record.getUsardatosfiscalesproductor()));
        target.setInvitationUseTicketTemplate(ONE.equals(record.getInvitacionusaplantillaticket()));
        return target;
    }

    private static void setContactData(CpanelEventoRecord record, BaseSeasonTicketDTO target) {
        target.setContactPersonName(record.getNombreresponsable());
        target.setContactPersonSurname(record.getApellidosresponsable());
        target.setContactPersonEmail(record.getEmailresponsable());
        target.setContactPersonPhone(record.getTelefonoresponsable());
    }

    private static DateDTO fillDate(Timestamp timestamp, Integer timeZoneId, String olsonId, String timeZoneName, Integer offset) {
        if (timestamp == null) {
            return null;
        }
        DateDTO result = new DateDTO();
        result.setDate(CommonUtils.timestampToZonedDateTime(timestamp));
        if (timeZoneId == null) {
            return result;
        }
        TimeZoneDTO timeZoneFin = new TimeZoneDTO();
        timeZoneFin.setOlsonId(olsonId);
        timeZoneFin.setName(timeZoneName);
        timeZoneFin.setOffset(offset);
        result.setTimeZone(timeZoneFin);
        return result;
    }

    private static void fillVenues(List<VenueRecord> venueRecords, BaseSeasonTicketDTO target) {
        if (CommonUtils.isEmpty(venueRecords)) {
            return;
        }
        target.setVenues(new ArrayList<>());
        for (VenueRecord venueRecord : venueRecords) {
            if (venueRecord.getVenueConfigId() == null) {
                continue;
            }
            VenueDTO venue = new VenueDTO();
            venue.setId(venueRecord.getIdrecinto().longValue());
            venue.setName(venueRecord.getNombre());
            venue.setCountryId(venueRecord.getPais());
            venue.setCity(venueRecord.getMunicipio());
            venue.setConfigId(venueRecord.getVenueConfigId());
            venue.setConfigName(venueRecord.getVenueConfigName());
            target.getVenues().add(venue);
        }
    }

    public static void updateEventRecord(CpanelEventoRecord eventRecord, UpdateSeasonTicketRequestDTO seasonTicket) {
        if (eventRecord.getUsardatosfiscalesproductor() != null && ConverterUtils.isByteAsATrue(eventRecord.getUsardatosfiscalesproductor())
                && seasonTicket.getUseProducerFiscalData() != null && !seasonTicket.getUseProducerFiscalData()) {
            eventRecord.setInvoiceprefixid(null);
        }
        updateField(eventRecord::setNombre, seasonTicket.getName());
        updateField(eventRecord::setReferenciapromotor, seasonTicket.getPromoterReference());
        updateField(eventRecord::setNombreresponsable, seasonTicket.getContactPersonName());
        updateField(eventRecord::setApellidosresponsable, seasonTicket.getContactPersonSurname());
        updateField(eventRecord::setEmailresponsable, seasonTicket.getContactPersonEmail());
        updateField(eventRecord::setTelefonoresponsable, seasonTicket.getContactPersonPhone());
        updateField(eventRecord::setIdtaxonomia, seasonTicket.getCategory() != null ? seasonTicket.getCategory().getId().intValue() : null);
        updateField(eventRecord::setIdtaxonomiapropia, seasonTicket.getCustomCategory() != null ? seasonTicket.getCustomCategory().getId().intValue() : null);
        updateField(eventRecord::setObjetivosobreentradas, seasonTicket.getSalesGoalTickets());
        updateField(eventRecord::setObjetivosobreventas, seasonTicket.getSalesGoalRevenue());
        updateField(eventRecord::setTipoabono, seasonTicket.getSessionPackType() != null ? seasonTicket.getSessionPackType().getId() : null);
        updateField(eventRecord::setPermitirinformesrecinto, isTrueAsByte(seasonTicket.getAllowVenueReport()));
        updateField(eventRecord::setUsardatosfiscalesproductor, isTrueAsByte(seasonTicket.getUseProducerFiscalData()));
        updateField(eventRecord::setInvitacionusaplantillaticket, isTrueAsByte(seasonTicket.getInvitationUseTicketTemplate()));
        updateField(eventRecord::setIdcurrency, seasonTicket.getCurrencyId());
        if (Boolean.FALSE.equals(seasonTicket.getEnableSubscriptionList())) {
            eventRecord.setIdlistasubscripcion(null);
        } else if(seasonTicket.getSubscriptionListId() != null &&
                (Boolean.TRUE.equals(seasonTicket.getEnableSubscriptionList()) || eventRecord.getIdlistasubscripcion() != null)) {
            eventRecord.setIdlistasubscripcion(seasonTicket.getSubscriptionListId());
        }

        if (seasonTicket.getTour() != null) {
            eventRecord.setIdgira(seasonTicket.getTour().getId() != null ? seasonTicket.getTour().getId().intValue() : null);
        }

        setSeasonTicketTicketTemplates(eventRecord, seasonTicket);

        updateBookingFields(eventRecord, seasonTicket);

        if (seasonTicket.getInvoicePrefixId() != null) {
            eventRecord.setInvoiceprefixid(seasonTicket.getInvoicePrefixId().intValue());
        }
    }

    public static void updateSessionRecord(SessionRecord sessionRecord, UpdateSeasonTicketRequestDTO seasonTicket) {
        updateField(sessionRecord::setFechaventa, zonedDateTimeToTimestamp(seasonTicket.getSalesStartingDate()));
        updateField(sessionRecord::setFechafinsesion, zonedDateTimeToTimestamp(seasonTicket.getSalesEndDate()));
        updateField(sessionRecord::setFechapublicacion, zonedDateTimeToTimestamp(seasonTicket.getChannelPublishingDate()));
        updateField(sessionRecord::setFechainicioreserva, zonedDateTimeToTimestamp(seasonTicket.getBookingStartingDate()));
        updateField(sessionRecord::setFechafinreserva, zonedDateTimeToTimestamp(seasonTicket.getBookingEndDate()));
        updateField(sessionRecord::setPublicado, isTrueAsByte(seasonTicket.getEnableChannels()));
        updateField(sessionRecord::setEnventa, isTrueAsByte(seasonTicket.getEnableSales()));
        updateField(sessionRecord::setReservasactivas, isTrueAsByte(seasonTicket.getBookingEnabled()));
        updateMaxBuyingLimit(sessionRecord, seasonTicket);
        if (seasonTicket.getInvoicePrefixId() != null) {
            sessionRecord.setInvoiceprefixid(seasonTicket.getInvoicePrefixId().intValue());
        }
    }

    private static void updateBookingFields(CpanelEventoRecord eventRecord, UpdateSeasonTicketRequestDTO seasonTicket) {
        if (seasonTicket.getBooking() != null) {
            BookingDTO booking = seasonTicket.getBooking();
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

    private static void updateMaxBuyingLimit(SessionRecord sessionRecord, UpdateSeasonTicketRequestDTO seasonTicket) {
        if(seasonTicket != null && seasonTicket.getMaxBuyingLimit() != null) {
            sessionRecord.setNummaxlocalidadescompra(seasonTicket.getMaxBuyingLimit().getValue());
        }
    }

    private static SeasonTicketTicketTemplatesDTO buildSeasonTicketTicketTemplates(EventRecord eventRecord) {
        SeasonTicketTicketTemplatesDTO templates = new SeasonTicketTicketTemplatesDTO();
        boolean hasValues = setEventTicketTemplateId(eventRecord.getIdplantillaticket(), templates::setTicketPdfTemplateId);
        hasValues |= setEventTicketTemplateId(eventRecord.getIdplantillatickettaquilla(), templates::setTicketPrinterTemplateId);
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

    private static void setSeasonTicketTicketTemplates(CpanelEventoRecord eventRecord, UpdateSeasonTicketRequestDTO seasonTicket) {
        if(seasonTicket != null && seasonTicket.getSeasonTicketTicketTemplatesDTO() != null) {
            SeasonTicketTicketTemplatesDTO templates = seasonTicket.getSeasonTicketTicketTemplatesDTO();
            if (templates != null) {
                updateField(eventRecord::setIdplantillaticket, longToInt(templates.getTicketPdfTemplateId()));
                updateField(eventRecord::setIdplantillatickettaquilla, longToInt(templates.getTicketPrinterTemplateId()));
                updateField(eventRecord::setIdplantillaticketgrupos, longToInt(templates.getGroupTicketPdfTemplateId()));
                updateField(eventRecord::setIdplantillatickettaquillagrupos, longToInt(templates.getGroupTicketPrinterTemplateId()));
                updateField(eventRecord::setIdplantillaticketinvitacion, longToInt(templates.getIndividualInvitationPdfTemplateId()));
                updateField(eventRecord::setIdplantillatickettaquillainvitacion, longToInt(templates.getIndividualInvitationPrinterTemplateId()));
                updateField(eventRecord::setIdplantillaticketinvitaciongrupos, longToInt(templates.getGroupInvitationPdfTemplateId()));
                updateField(eventRecord::setIdplantillatickettaquillainvitaciongrupos, longToInt(templates.getGroupInvitationPrinterTemplateId()));
            }
        }
    }

    private static void setSeasonTicketCustomData(Map.Entry<EventRecord, List<VenueRecord>> eventRecord, SeasonTicketDTO target,
                                                  SeasonTicketRenewalConfig renewalsConfig) {
        EventRecord eventRecordKey = eventRecord.getKey();

        target.setMemberMandatory(BooleanUtils.isTrue(eventRecordKey.getMemberMandatory()));

        target.setAllowRenewal(BooleanUtils.isTrue(eventRecordKey.getAllowRenewal()));

        SeasonTicketRenewal renewal = new SeasonTicketRenewal();
        renewal.setRenewalEnabled(eventRecordKey.getRenewalEnabled());
        renewal.setRenewalStartingDate(timestampToZonedDateTime(eventRecordKey.getRenewalStartingDate()));
        renewal.setRenewalEndDate(timestampToZonedDateTime(eventRecordKey.getRenewalEndDate()));
        renewal.setAutoRenewal(eventRecordKey.getAutoRenewal());
        if (renewalsConfig != null && renewalsConfig.getRenewalType() != null) {
            renewal.setRenewalType(RenewalType.valueOf(renewalsConfig.getRenewalType().name()));
            renewal.setBankAccountId(renewalsConfig.getBankAccountId());
            renewal.setGroupByReference(renewalsConfig.getGroupByReference());
            renewal.setAutoRenewalMandatory(renewalsConfig.getAutoRenewalMandatory());
        }
        target.setRenewal(renewal);

        target.setAllowChangeSeat(BooleanUtils.isTrue(eventRecordKey.getAllowChangeSeat()));

        SeasonTicketChangeSeat changeSeat = new SeasonTicketChangeSeat();
        changeSeat.setChangeSeatEnabled(eventRecordKey.getChangeSeatEnabled());
        changeSeat.setChangeSeatStartingDate(timestampToZonedDateTime(eventRecordKey.getChangeSeatStartingDate()));
        changeSeat.setChangeSeatEndDate(timestampToZonedDateTime(eventRecordKey.getChangeSeatEndDate()));
        changeSeat.setMaxChangeSeatValue(eventRecordKey.getMaxChangeSeatValue());
        changeSeat.setMaxChangeSeatValueEnabled(eventRecordKey.getMaxChangeSeatValueEnabled());
        target.setChangeSeat(changeSeat);

        target.setAllowReleaseSeat(BooleanUtils.isTrue(eventRecordKey.getAllowReleaseSeat()));
        target.setAllowTransferTicket(BooleanUtils.isTrue(eventRecordKey.getAllowTransferTicket()));
    }

    public static void updateSeasonTicketCustomData(CpanelSeasonTicketRecord record, UpdateSeasonTicketRequestDTO body) {
        if (record != null && body != null) {
            updateField(record::setIsmembermandatory, body.getMemberMandatory());
            updateField(record::setAllowrenewal, body.getAllowRenewal());
            updateField(record::setAllowchangeseat, body.getAllowChangeSeat());
            updateField(record::setAllowtransferticket, body.getAllowTransferTicket());
            updateField(record::setAllowreleaseseat, body.getAllowReleaseSeat());
            updateField(record::setRegistermandatory, body.getRegisterMandatory());
            updateField(record::setCustomermaxseats, body.getCustomerMaxSeats());

            if(Boolean.FALSE.equals(body.getAllowRenewal())) {
                record.setRenewalenabled(Boolean.FALSE);
                record.setRenewalinitdate(null);
                record.setRenewalenddate(null);
            } else if(body.getRenewal() != null) {
                UpdateSeasonTicketRenewal renewal = body.getRenewal();
                updateField(record::setRenewalenabled, renewal.getRenewalEnabled());
                updateField(record::setRenewalinitdate, zonedDateTimeToTimestamp(renewal.getRenewalStartingDate()));
                updateField(record::setRenewalenddate, zonedDateTimeToTimestamp(renewal.getRenewalEndDate()));
                updateField(record::setAutorenewal, renewal.getAutoRenewal());
            }
            if(body.getChangeSeat() != null) {
                UpdateSeasonTicketChangeSeat changeSeat = body.getChangeSeat();
                updateField(record::setChangeseatenabled, changeSeat.getChangeSeatEnabled());
                updateField(record::setChangeseatinitdate, zonedDateTimeToTimestamp(changeSeat.getChangeSeatStartingDate()));
                updateField(record::setChangeseatenddate, zonedDateTimeToTimestamp(changeSeat.getChangeSeatEndDate()));
                updateField(record::setMaxchangeseatvalueenabled, changeSeat.getMaxChangeSeatValueEnabled());
                updateField(record::setMaxchangeseatvalue, changeSeat.getMaxChangeSeatValue());
            }
        }
    }
}
