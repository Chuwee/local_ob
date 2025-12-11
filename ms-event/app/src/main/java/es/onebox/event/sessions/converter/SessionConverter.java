package es.onebox.event.sessions.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.domain.CountryConfig;
import es.onebox.event.common.domain.Restrictions;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.domain.VenueTemplateType;
import es.onebox.event.events.dto.TimeZoneDTO;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.utils.EventStatusUtil;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.secondarymarket.dto.SessionSecondaryMarketConfigDTO;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dao.record.SessionsGroupDataRecord;
import es.onebox.event.sessions.domain.Session;
import es.onebox.event.sessions.domain.sessionconfig.PresalesRedirectionLinkMode;
import es.onebox.event.sessions.domain.sessionconfig.PresalesRedirectionPolicy;
import es.onebox.event.sessions.domain.sessionconfig.QueueItConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionExternalConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionPresalesConfig;
import es.onebox.event.sessions.dto.CreateSessionDTO;
import es.onebox.event.sessions.dto.CustomersLimitsDTO;
import es.onebox.event.sessions.dto.LinkedSessionDTO;
import es.onebox.event.sessions.dto.PreSaleConfigDTO;
import es.onebox.event.sessions.dto.PresalesLinkMode;
import es.onebox.event.sessions.dto.PresalesRedirectionPolicyDTO;
import es.onebox.event.sessions.dto.PriceTypeLimitDTO;
import es.onebox.event.sessions.dto.QueueItConfigDTO;
import es.onebox.event.sessions.dto.RestrictionsDTO;
import es.onebox.event.sessions.dto.SessionConfigDTO;
import es.onebox.event.sessions.dto.SessionConfigRefundConditionsDTO;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionDateDTO;
import es.onebox.event.sessions.dto.SessionDynamicPriceConfigDTO;
import es.onebox.event.sessions.dto.SessionGenerationStatus;
import es.onebox.event.sessions.dto.SessionGroupConfigDTO;
import es.onebox.event.sessions.dto.SessionPackDTO;
import es.onebox.event.sessions.dto.SessionPresalesConfigDTO;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.dto.SessionsGroupDTO;
import es.onebox.event.sessions.dto.SessionsGroupsDTO;
import es.onebox.event.sessions.dto.StreamingVendorConfigDTO;
import es.onebox.event.sessions.dto.UpdateSessionRequestDTO;
import es.onebox.event.sessions.dto.external.SessionExternalConfigDTO;
import es.onebox.event.sessions.enums.AccessScheduleType;
import es.onebox.event.sessions.enums.SessionGroupType;
import es.onebox.event.sessions.enums.SessionType;
import es.onebox.event.sessions.enums.SessionVirtualQueueVersion;
import es.onebox.event.sorting.SessionField;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigSesionGruposRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static es.onebox.core.utils.common.CommonUtils.timestampToZonedDateTime;
import static es.onebox.core.utils.common.CommonUtils.zonedDateTimeToTimestamp;
import static es.onebox.event.common.utils.ConverterUtils.updateField;

public class SessionConverter {

    private SessionConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    private static final String SKIP_QUEUE = "skipQueue";
    private static final int HIGH_DEMAND_BLOCKING_TIME = 300;
    private static final int HIGH_DEMAND_PRE_ORDER_TIME = 600;

    public static Session toEntity(CreateSessionDTO s) {
        Session result = new Session();
        result.setEventId(s.getEventId());
        result.setSessionId(s.getSessionId());
        result.setName(s.getName());
        result.setStatus(s.getStatus().getId());
        result.setSeasonPass(s.getSeasonPass());
        result.setPublished(s.getPublished());
        result.setOnSale(s.getOnSale());
        result.setBookings(s.getBookings());
        result.setVenueEntityConfigId(s.getVenueEntityConfigId());
        result.setAccessValidationSpaceId(s.getAccessValidationSpaceId());
        result.setSessionStartDate(s.getSessionStartDate());
        result.setPublishDate(s.getPublishDate());
        result.setSalesDate(s.getSalesStartDate());
        result.setSessionEndDate(s.getSalesEndDate());
        result.setSessionRealEndDate(s.getSessionEndDate());
        result.setBookingStartDate(s.getBookingStartDate());
        result.setBookingEndDate(s.getBookingEndDate());
        result.setSaleType(s.getSaleType());
        result.setUseTemplateAccess(s.getUseTemplateAccess());
        result.setUseLimitsQuotasTemplateEvent(s.getUseLimitsQuotasTemplateEvent());
        result.setTypeScheduleAccess(getTypeScheduleAccess(s));
        result.setTaxId(s.getTaxId());
        result.setChargeTaxId(s.getChargeTaxId());
        result.setCapacity(s.getCapacity());
        result.setCapacityGenerationStatus(s.getCapacityGenerationStatus());
        result.setExternalId(s.getExternalId());
        result.setExternal(s.getExternal());
        result.setFinalDate(s.getFinalDate());
        result.setColor(hexColorToNumber(s.getColor()));
        result.setAllowPartialRefund(CommonUtils.isTrue(s.getAllowPartialRefund()));
        result.setReference(s.getReference());
        result.setUseProducerTaxData(s.getUseProducerTaxData());
        result.setProducerId(s.getProducerId());
        result.setInvoicePrefixId(s.getInvoicePrefixId());
        if (s.getSettings() != null) {
            result.setEnableOrphanSeats(s.getSettings().getEnableOrphanSeats());
        }
        return result;
    }

    private static Integer getTypeScheduleAccess(CreateSessionDTO s) {
        if (s.getTypeScheduleAccess() == null) {
            return AccessScheduleType.DEFAULT.getType();
        }
        return s.getTypeScheduleAccess();
    }

    public static List<Session> toEntity(List<CreateSessionDTO> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return new ArrayList<>();
        }
        return sessions.stream().map(SessionConverter::toEntity).collect(Collectors.toList());
    }

    public static SessionDTO toSessionDTO(SessionRecord sessionRecord, List<String> fields, String olsonId) {
        return toSessionDTO(sessionRecord, fields, null, olsonId);
    }

    public static SessionDTO toSessionDTO(SessionRecord sessionRecord, List<String> fields, SessionSecondaryMarketConfigDTO secondaryMarket, String olsonId) {
        final boolean fieldsIsEmpty = CollectionUtils.isEmpty(fields);
        if (sessionRecord == null) {
            return null;
        }
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(sessionRecord.getIdsesion().longValue());
        if (sessionRecord.getIdexterno() != null) {
            sessionDTO.setExternalId(sessionRecord.getIdexterno().longValue());
        }
        sessionDTO.setName(sessionRecord.getNombre());
        fillSessionStatus(sessionRecord, sessionDTO);
        fillSessionType(sessionRecord, sessionDTO);
        if (sessionRecord.getEntityId() != null) {
            sessionDTO.setEntityId(sessionRecord.getEntityId().longValue());
            sessionDTO.setEntityName(sessionRecord.getEntityName());
        }
        sessionDTO.setEventId(sessionRecord.getIdevento() != null ? sessionRecord.getIdevento().longValue() : null);
        sessionDTO.setEventName(sessionRecord.getEventName());
        sessionDTO.setCapacity(sessionRecord.getAforo() != null ? sessionRecord.getAforo().longValue() : null);

        fillVenueTemplate(sessionRecord, sessionDTO);

        if (sessionRecord.getIdimpuesto() != null) {
            sessionDTO.setTicketTax(new IdNameDTO(sessionRecord.getIdimpuesto().longValue(), sessionRecord.getTaxTicketName()));
        }
        if (sessionRecord.getIdimpuestorecargo() != null) {
            sessionDTO.setChargesTax(new IdNameDTO(sessionRecord.getIdimpuestorecargo().longValue(), sessionRecord.getTaxChargesName()));
        }
        if (sessionRecord.getEventType() != null) {
            sessionDTO.setEventType(EventType.byId(sessionRecord.getEventType()));
        }
        sessionDTO.setSaleType(sessionRecord.getTipoventa());

        if (sessionRecord.getPublicado() != null) {
            sessionDTO.setEnableChannels(CommonUtils.isTrue(sessionRecord.getPublicado()));
        }
        if (fieldsIsEmpty) {
            sessionDTO.setEnableBookings(CommonUtils.isTrue(sessionRecord.getReservasactivas()));
            sessionDTO.setEnableSales(CommonUtils.isTrue((sessionRecord.getEnventa())));
            sessionDTO.setEnableCaptcha(CommonUtils.isTrue((sessionRecord.getCaptcha())));
            sessionDTO.setEnableShowDateInChannels(CommonUtils.isTrue(sessionRecord.getShowdate()));
            sessionDTO.setEnableShowTimeInChannels(CommonUtils.isTrue(sessionRecord.getShowdatetime()));
            sessionDTO.setEnableShowUnconfirmedDateInChannels(CommonUtils.isTrue(sessionRecord.getShowunconfirmeddate()));
            sessionDTO.setEnableOrphanSeats(CommonUtils.isTrue(sessionRecord.getCheckorphanseats()));
            sessionDTO.setShowUnconfirmedDate(CommonUtils.isTrue(sessionRecord.getFechanodefinitiva()));
            sessionDTO.setUnconfirmedDate(CommonUtils.isTrue(sessionRecord.getFechanodefinitiva()));
            sessionDTO.setAllowPartialRefund(CommonUtils.isTrue(sessionRecord.getAllowpartialrefund()));
            sessionDTO.setEnableProducerTaxData(CommonUtils.isTrue(sessionRecord.getUsardatosfiscalesproductor()));
            sessionDTO.setPresaleEnabled(CommonUtils.isTrue(sessionRecord.getPresaleenabled()));
            sessionDTO.setArchived(CommonUtils.isTrue(sessionRecord.getArchivado()));

            if (sessionRecord.getNummaxlocalidadessesion() != null && sessionRecord.getNummaxlocalidadessesion() > 0) {
                sessionDTO.setEnableSessionTicketLimit(Boolean.TRUE);
                sessionDTO.setSessionTicketLimit(sessionRecord.getNummaxlocalidadessesion());
            } else {
                sessionDTO.setEnableSessionTicketLimit(Boolean.FALSE);
            }

            if (sessionRecord.getIdlistasubscripcion() != null) {
                sessionDTO.setEnableSubscriptionList(Boolean.TRUE);
                sessionDTO.setSubscriptionListId(sessionRecord.getIdlistasubscripcion());
            } else {
                sessionDTO.setEnableSubscriptionList(Boolean.FALSE);
            }

            if (secondaryMarket != null && secondaryMarket.getDates() != null) {
                sessionDTO.setEnableSecondaryMarket(CommonUtils.isTrue(secondaryMarket.getDates().getEnabled()));
            }
        }
        if (sessionRecord.getSbsesionrelacionada() != null) {
            sessionDTO.setIsSmartBooking(isSmartBookingSession(sessionRecord));
            sessionDTO.setSmartBookingRelatedId(sessionRecord.getSbsesionrelacionada() != null ?
                    sessionRecord.getSbsesionrelacionada().longValue() : null);
        }

        if (fieldsIsEmpty || fields.stream().anyMatch(field ->
                field.equals(SessionField.START_DATE.getRequestField())
                        || field.equals(SessionField.RELEASE_DATE.getRequestField())
                        || field.equals(SessionField.SALE_DATE.getRequestField())
                        || field.equals(SessionField.SALE_END_DATE.getRequestField())
                        || field.equals(SessionField.ADMISSION_START.getRequestField())
                        || field.equals(SessionField.ADMISSION_END.getRequestField())
                        || field.equals(SessionField.ADMISSION.getRequestField()))) {
            fillDates(sessionRecord, sessionDTO, secondaryMarket, olsonId);
        }

        fillAccessControl(sessionRecord, sessionDTO);

        EventStatusUtil.applySessionFlagStatus(sessionDTO, sessionRecord);

        sessionDTO.setShowDate(sessionRecord.getShowdate());
        sessionDTO.setShowDatetime(sessionRecord.getShowdatetime());
        sessionDTO.setShowUnconfirmedDate(sessionRecord.getShowunconfirmeddate() != null ? CommonUtils.isTrue(sessionRecord.getShowunconfirmeddate()) : null);
        sessionDTO.setColor(numberColorToHex(sessionRecord.getColor()));

        sessionDTO.setProducerId(sessionRecord.getIdpromotor());
        sessionDTO.setInvoicePrefixId(sessionRecord.getInvoiceprefixid());
        sessionDTO.setExternal(sessionRecord.getIsexternal());

        sessionDTO.setReference(sessionRecord.getReference());
        sessionDTO.setPublicationCancelledReason(sessionRecord.getRazoncancelacionpublicacion());
        sessionDTO.setExternalReference(Objects.nonNull(sessionRecord.getExternalreference()) ? sessionRecord.getExternalreference() : null);
        sessionDTO.setHighDemand(sessionRecord.getHighdemand() != null ? CommonUtils.isTrue(sessionRecord.getHighdemand()) : null);
        sessionDTO.setEnableOrphanSeats(sessionRecord.getCheckorphanseats() != null ? CommonUtils.isTrue(sessionRecord.getCheckorphanseats()) : null);
        return sessionDTO;
    }

    public static SessionPackDTO toSessionPackDTO(SessionRecord sessionRecord) {
        if (sessionRecord == null) {
            return null;
        }
        SessionPackDTO sessionPackDTO = new SessionPackDTO();
        sessionPackDTO.setId(sessionRecord.getIdsesion().longValue());
        sessionPackDTO.setName(sessionRecord.getNombre());

        return sessionPackDTO;
    }

    public static LinkedSessionDTO toLinkedSessionDTO(SessionRecord sessionRecord) {
        if (sessionRecord == null) {
            return null;
        }
        LinkedSessionDTO sessionDTO = new LinkedSessionDTO();
        sessionDTO.setId(sessionRecord.getIdsesion().longValue());
        sessionDTO.setName(sessionRecord.getNombre());
        sessionDTO.setColor(numberColorToHex(sessionRecord.getColor()));

        return sessionDTO;
    }

    public static SessionConfigDTO toSessionConfigDTO(SessionConfig source) {
        SessionConfigDTO response = new SessionConfigDTO();
        response.setSessionId(source.getSessionId());
        response.setMaxMembers(source.getMaxMembers());
        response.setEntity(source.getEntity());
        response.setSeasonTicketMultiticket(source.getSeasonTicketMultiticket());

        if (source.getRestrictions() != null) {
            RestrictionsDTO restrictions = new RestrictionsDTO();
            restrictions.setPriceZones(source.getRestrictions().getPriceZones());
            restrictions.setCountryConfig(source.getRestrictions().getCountryConfig());
            restrictions.setSale(source.getRestrictions().getSale());
            response.setRestrictions(restrictions);
        }
        if (source.getSessionRefundConditions() != null) {
            SessionConfigRefundConditionsDTO refundConditions = new SessionConfigRefundConditionsDTO();
            refundConditions.setPrintRefundPrice(source.getSessionRefundConditions().getPrintRefundPrice());
            refundConditions.setSeasonPassRefundConditions(source.getSessionRefundConditions().getSeasonPassRefundConditions());
            response.setSessionRefundConditions(refundConditions);
        }
        if (source.getPreSaleConfig() != null) {
            PreSaleConfigDTO presale = new PreSaleConfigDTO();
            presale.setActive(source.getPreSaleConfig().isActive());
            presale.setActiveChannels(source.getPreSaleConfig().getActiveChannels());
            presale.setStartDate(source.getPreSaleConfig().getStartDate());
            presale.setEndDate(source.getPreSaleConfig().getEndDate());
            presale.setMemberTicketsLimit(source.getPreSaleConfig().getMemberTicketsLimit());
            presale.setGeneralTicketsLimit(source.getPreSaleConfig().getGeneralTicketsLimit());
            presale.setPresalePromotionId(source.getPreSaleConfig().getPresalePromotionId());
            presale.setMultiplePurchase(source.getPreSaleConfig().getMultiplePurchase());
            response.setPreSaleConfig(presale);
        }
        if (source.getQueueItConfig() != null) {
            QueueItConfigDTO queueItConfig = new QueueItConfigDTO();
            queueItConfig.setActive(source.getQueueItConfig().isActive());
            queueItConfig.setAlias(source.getQueueItConfig().getAlias());
            queueItConfig.setParameterActive(source.getQueueItConfig().isParameterActive());
            queueItConfig.setParameter(source.getQueueItConfig().getParameter());
            queueItConfig.setValue(source.getQueueItConfig().getValue());
            queueItConfig.setVersion(source.getQueueItConfig().getVersion());
            response.setQueueItConfig(queueItConfig);
        }
        if (source.getStreamingVendorConfig() != null) {
            StreamingVendorConfigDTO streaming = new StreamingVendorConfigDTO();
            streaming.setEnabled(source.getStreamingVendorConfig().getEnabled());
            streaming.setEmailMinutesBeforeStart(source.getStreamingVendorConfig().getEmailMinutesBeforeStart());
            streaming.setVendor(source.getStreamingVendorConfig().getVendor());
            streaming.setValue(source.getStreamingVendorConfig().getValue());
            response.setStreamingVendorConfig(streaming);
        }

        if (source.getSessionPresalesConfig() != null && source.getSessionPresalesConfig().getPresalesRedirectionPolicy() != null) {
            SessionPresalesConfigDTO sessionPresalesConfigDTO = new SessionPresalesConfigDTO();
            PresalesRedirectionPolicyDTO presalesRedirectionPolicyDTO = new PresalesRedirectionPolicyDTO();

            PresalesRedirectionPolicy presalesRedirectionPolicy = source.getSessionPresalesConfig().getPresalesRedirectionPolicy();

            presalesRedirectionPolicyDTO.setValue(presalesRedirectionPolicy.getValue());
            if (presalesRedirectionPolicy.getMode() != null) {
                presalesRedirectionPolicyDTO.setMode(PresalesLinkMode.valueOf(presalesRedirectionPolicy.getMode().name()));
            }

            sessionPresalesConfigDTO.setPresalesRedirectionPolicyDTO(presalesRedirectionPolicyDTO);

            response.setSessionPresalesConfig(sessionPresalesConfigDTO);
        }

        if (source.getSessionDynamicPriceConfig() != null) {
            SessionDynamicPriceConfigDTO dynamicPriceConfig = new SessionDynamicPriceConfigDTO();
            dynamicPriceConfig.setActive(source.getSessionDynamicPriceConfig().getActive());
            response.setSessionDynamicPriceConfigDTO(dynamicPriceConfig);
        }
        if (source.getSessionExternalConfig() != null) {
            response.setExternalConfig(new SessionExternalConfigDTO());
            response.getExternalConfig().setDigitalTicketMode(source.getSessionExternalConfig().getDigitalTicketMode());
            response.getExternalConfig().setAdditionalProperties(source.getSessionExternalConfig().getAdditionalProperties());
        }

        if (CollectionUtils.isNotEmpty(source.getPriceTypeLimits())) {
            response.setPriceTypeLimits(
                    source.getPriceTypeLimits().stream()
                            .map(limit -> new PriceTypeLimitDTO(limit.getId(), limit.getMax(), limit.getMin()))
                            .toList()
            );
        }

        if (source.getCustomersLimits() != null) {
            CustomersLimitsDTO customersLimitsDTO = new CustomersLimitsDTO();
            customersLimitsDTO.setMin(source.getCustomersLimits().getMin());
            customersLimitsDTO.setMax(source.getCustomersLimits().getMax());
            if(CollectionUtils.isNotEmpty(source.getCustomersLimits().getPriceTypeLimits())) {
                customersLimitsDTO.setPriceTypeLimits(
                        source.getCustomersLimits().getPriceTypeLimits().stream()
                                .map(limit -> new PriceTypeLimitDTO(limit.getId(), limit.getMax(), limit.getMin()))
                                .toList()
                );
            }
            response.setCustomersLimits(customersLimitsDTO);
        }

        return response;
    }

    public static void updateRecord(CpanelSesionRecord sesionRecord, UpdateSessionRequestDTO session, CpanelEventoRecord event) {
        updateField(sesionRecord::setNombre, session.getName());
        fillStatus(sesionRecord, session);

        updateField(sesionRecord::setIdimpuesto, session.getTicketTax() != null ? ConverterUtils.longToInt(session.getTicketTax().getId()) : null);
        updateField(sesionRecord::setIdimpuestorecargo, session.getChargesTax() != null ? ConverterUtils.longToInt(session.getChargesTax().getId()) : null);
        updateField(sesionRecord::setTipohorarioaccesos, session.getAccessScheduleType() != null ?
                ConverterUtils.intToByte(session.getAccessScheduleType().getType()) : null);
        updateField(sesionRecord::setAforo, session.getCapacity());
        updateField(sesionRecord::setPublicado, isTrueAsByte(session.getEnableChannels()));
        updateField(sesionRecord::setReservasactivas, isTrueAsByte(session.getEnableBookings()));
        updateField(sesionRecord::setEnventa, isTrueAsByte(session.getEnableSales()));
        updateField(sesionRecord::setCaptcha, isTrueAsByte(session.getEnableCaptcha()));
        updateField(sesionRecord::setTipoventa, session.getSaleType());
        updateField(sesionRecord::setUsalimitescuposplantillaevento, isTrueAsByte(session.getUseVenueConfigCapacity()));
        updateField(sesionRecord::setUsaaccesosplantilla, session.getUseTemplateAccess());
        updateField(sesionRecord::setShowdate, session.getEnableShowDateInChannels());
        updateField(sesionRecord::setShowdatetime, session.getEnableShowTimeInChannels());
        updateField(sesionRecord::setShowunconfirmeddate, isTrueAsByte(session.getEnableShowUnconfirmedDateInChannels()));
        updateField(sesionRecord::setCheckorphanseats, isTrueAsByte(session.getEnableOrphanSeats()));
        updateField(sesionRecord::setColor, hexColorToNumber(session.getColor()));
        updateField(sesionRecord::setReference, session.getReference());
        updateField(sesionRecord::setPresaleenabled, isTrueAsByte(session.getPresaleEnabled()));
        updateField(sesionRecord::setFechanodefinitiva, isTrueAsByte(session.getUnconfirmedDate()));
        updateField(sesionRecord::setUsardatosfiscalesproductor, isTrueAsByte(session.getEnableProducerTaxData()));
        updateField(sesionRecord::setExternalreference, session.getExternalReference());
        updateField(sesionRecord::setHighdemand, isTrueAsByte(session.getHighDemand()));

        if (BooleanUtils.isTrue(session.getHighDemand())) {
            updateField(sesionRecord::setBlockingtime, HIGH_DEMAND_BLOCKING_TIME);
            updateField(sesionRecord::setPreordertime, HIGH_DEMAND_PRE_ORDER_TIME);
        } else if (BooleanUtils.isFalse(session.getHighDemand())) {
            sesionRecord.setBlockingtime(null);
            sesionRecord.setPreordertime(null);
        }

        if (session.getSpace() != null) {
            sesionRecord.setEspaciovalidacionacceso(session.getSpace().getId() != null ?
                    session.getSpace().getId().intValue() : null);
        }
        if (BooleanUtils.isTrue(session.getEnableSessionTicketLimit()) && session.getSessionTicketLimit() != null && session.getSessionTicketLimit() > 0) {
            updateField(sesionRecord::setNummaxlocalidadessesion, session.getSessionTicketLimit());
        } else if (BooleanUtils.isFalse(session.getEnableSessionTicketLimit())) {
            updateField(sesionRecord::setNummaxlocalidadessesion, -1);
        }
        if (Boolean.FALSE.equals(session.getEnableSubscriptionList())) {
            sesionRecord.setIdlistasubscripcion(null);
        } else if (session.getSubscriptionListId() != null && (Boolean.TRUE.equals(session.getEnableSubscriptionList()) || sesionRecord.getIdlistasubscripcion() != null)) {
            updateField(sesionRecord::setIdlistasubscripcion, session.getSubscriptionListId());
        }
        if (BooleanUtils.isTrue(session.getEnableProducerTaxData())) {
            updateField(sesionRecord::setIdpromotor, session.getProducerId());
            sesionRecord.setInvoiceprefixid(session.getInvoicePrefixId());
        } else if (BooleanUtils.isFalse(session.getEnableProducerTaxData())) {
            updateField(sesionRecord::setIdpromotor, event.getIdpromotor());
            sesionRecord.setInvoiceprefixid(event.getInvoiceprefixid());
        }

        SessionDateDTO date = session.getDate();
        updateDates(sesionRecord, session, date);
    }

    public static void updateDates(CpanelSesionRecord sesionRecord, UpdateSessionRequestDTO session,
                                   SessionDateDTO date) {
        if (date != null) {
            Timestamp sessionStart = sesionRecord.getFechainiciosesion();
            Optional<Timestamp> targetSessionStart = Optional.ofNullable(CommonUtils.zonedDateTimeToTimestamp(session.getDate().getStart()));
            updateField(sesionRecord::setFechainiciosesion, zonedDateTimeToTimestamp(date.getStart()));
            updateField(sesionRecord::setFecharealfinsesion, zonedDateTimeToTimestamp(date.getEnd()));
            updateField(sesionRecord::setFechapublicacion, ConverterUtils.zonedDateTimeRelativeToTimestamp(date.getChannelPublication(), targetSessionStart.orElse(sessionStart)));
            updateField(sesionRecord::setFechainicioreserva, ConverterUtils.zonedDateTimeRelativeToTimestamp(date.getBookingsStart(), targetSessionStart.orElse(sessionStart)));
            updateField(sesionRecord::setFechafinreserva, ConverterUtils.zonedDateTimeRelativeToTimestamp(date.getBookingsEnd(), targetSessionStart.orElse(sessionStart)));
            updateField(sesionRecord::setFechaventa, ConverterUtils.zonedDateTimeRelativeToTimestamp(date.getSalesStart(), targetSessionStart.orElse(sessionStart)));
            updateField(sesionRecord::setFechafinsesion, ConverterUtils.zonedDateTimeRelativeToTimestamp(date.getSalesEnd(), targetSessionStart.orElse(sessionStart)));
            if (AccessScheduleType.SPECIFIC.equals(session.getAccessScheduleType())) {
                updateField(sesionRecord::setAperturaaccesos, ConverterUtils.zonedDateTimeRelativeToTimestamp(date.getAdmissionStart(), targetSessionStart.orElse(sessionStart)));
                updateField(sesionRecord::setCierreaccesos, ConverterUtils.zonedDateTimeRelativeToTimestamp(date.getAdmissionEnd(), targetSessionStart.orElse(sessionStart)));
            } else if (AccessScheduleType.DEFAULT.equals(session.getAccessScheduleType())) {
                sesionRecord.setAperturaaccesos(null);
                sesionRecord.setCierreaccesos(null);
            }
        }
    }

    private static void fillVenueTemplate(SessionRecord sessionRecord, SessionDTO sessionDTO) {
        sessionDTO.setVenueConfigName(sessionRecord.getVenueTemplateName());
        sessionDTO.setVenueConfigTemplateType(sessionRecord.getVenueTemplateType());
        sessionDTO.setVenueId(sessionRecord.getVenueId() != null ?
                sessionRecord.getVenueId().longValue() : null);
        sessionDTO.setVenueConfigId(sessionRecord.getVenueTemplateId() != null ?
                sessionRecord.getVenueTemplateId().longValue() : null);
        sessionDTO.setVenueConfigGraphic(sessionRecord.getVenueTemplateGraphic() != null ?
                CommonUtils.isTrue(sessionRecord.getVenueTemplateGraphic()) : null);
        sessionDTO.setVenueConfigSpaceId(sessionRecord.getVenueTemplateSpaceId() != null ?
                sessionRecord.getVenueTemplateSpaceId().longValue() : null);
        sessionDTO.setVenueConfigSpaceName(sessionRecord.getVenueTemplateSpaceName());
        sessionDTO.setUseVenueConfigCapacity(sessionRecord.getUsalimitescuposplantillaevento() != null ?
                CommonUtils.isTrue(sessionRecord.getUsalimitescuposplantillaevento()) : null);
        sessionDTO.setVenueName(sessionRecord.getVenueName());
        sessionDTO.setCity(sessionRecord.getVenueCity());
        sessionDTO.setCountryId(sessionRecord.getVenueCountryId() != null ?
                sessionRecord.getVenueCountryId().longValue() : null);

        TimeZoneDTO timeZoneDTO = new TimeZoneDTO();
        timeZoneDTO.setOlsonId(sessionRecord.getVenueTZ());
        timeZoneDTO.setName(sessionRecord.getVenueTZName());
        timeZoneDTO.setOffset(sessionRecord.getVenueTZOffset());
        sessionDTO.setTimeZone(timeZoneDTO.equals(new TimeZoneDTO()) ? null : timeZoneDTO);
    }

    private static void fillSessionType(SessionRecord sessionRecord, SessionDTO sessionDTO) {
        if (sessionRecord.getEsabono() != null) {
            if (CommonUtils.isTrue(sessionRecord.getEsabono())) {
                if (sessionRecord.getEventPackType() != null) {
                    sessionDTO.setSessionType(SessionType.getById(sessionRecord.getEventPackType().intValue()));
                }
            } else {
                sessionDTO.setSessionType(SessionType.SESSION);
            }
        }
    }

    private static void fillSessionStatus(SessionRecord sessionRecord, SessionDTO sessionDTO) {
        if (sessionRecord.getEstado() != null) {
            if (SessionStatus.READY.getId().equals(sessionRecord.getEstado()) &&
                    CommonUtils.isTrue(sessionRecord.getIspreview())) {
                sessionDTO.setStatus(SessionStatus.PREVIEW);
            } else {
                sessionDTO.setStatus(SessionStatus.byId(sessionRecord.getEstado()));
            }
        }
        if (sessionRecord.getEstadogeneracionaforo() != null) {
            sessionDTO.setGenerationStatus(SessionGenerationStatus.byId(sessionRecord.getEstadogeneracionaforo()));
        }
    }

    private static void fillStatus(CpanelSesionRecord sesionRecord, UpdateSessionRequestDTO session) {
        if (session.getStatus() != null) {
            if (!session.getStatus().getId().equals(sesionRecord.getEstado())) {
                sesionRecord.setRazoncancelacionpublicacion(null);
            }

            if (SessionStatus.PREVIEW.equals(session.getStatus())) {
                updateField(sesionRecord::setEstado, SessionStatus.READY.getId());
                sesionRecord.setIspreview(true);
            } else {
                updateField(sesionRecord::setEstado, session.getStatus().getId());
                sesionRecord.setIspreview(false);
            }
        }
    }

    private static void fillDates(SessionRecord sessionRecord, SessionDTO sessionDTO, SessionSecondaryMarketConfigDTO secondaryMarket, String olsonId) {
        sessionDTO.setDate(new SessionDateDTO());
        if (sessionRecord.getFechainiciosesion() != null) {
            if (olsonId != null) {
                sessionDTO.getDate().setStart(ZonedDateTime.ofInstant(sessionRecord.getFechainiciosesion().toInstant(), ZoneId.of(olsonId)));
            } else {
                sessionDTO.getDate().setStart(timestampToZonedDateTime(sessionRecord.getFechainiciosesion()));
            }
        }
        if (sessionRecord.getFecharealfinsesion() != null) {
            if (olsonId != null) {
                sessionDTO.getDate().setEnd(ZonedDateTime.ofInstant(sessionRecord.getFecharealfinsesion().toInstant(), ZoneId.of(olsonId)));
            } else {
                sessionDTO.getDate().setEnd(timestampToZonedDateTime(sessionRecord.getFecharealfinsesion()));
            }
        }
        if (sessionRecord.getFechapublicacion() != null) {
            if (olsonId != null) {
                sessionDTO.getDate().setChannelPublication(ZonedDateTimeWithRelative.of(ZonedDateTime.ofInstant(sessionRecord.getFechapublicacion().toInstant(), ZoneId.of(olsonId))));
            } else {
                sessionDTO.getDate().setChannelPublication(
                        ZonedDateTimeWithRelative.of(timestampToZonedDateTime(sessionRecord.getFechapublicacion())));
            }
        }
        if (sessionRecord.getFechainicioreserva() != null) {
            if (olsonId != null) {
                sessionDTO.getDate().setBookingsStart(ZonedDateTimeWithRelative.of(ZonedDateTime.ofInstant(sessionRecord.getFechainicioreserva().toInstant(), ZoneId.of(olsonId))));
            } else {
                sessionDTO.getDate().setBookingsStart(
                        ZonedDateTimeWithRelative.of(timestampToZonedDateTime(sessionRecord.getFechainicioreserva())));
            }
        }
        if (sessionRecord.getFechafinreserva() != null) {
            if (olsonId != null) {
                sessionDTO.getDate().setBookingsEnd(ZonedDateTimeWithRelative.of(ZonedDateTime.ofInstant(sessionRecord.getFechafinreserva().toInstant(), ZoneId.of(olsonId))));
            } else {
                sessionDTO.getDate().setBookingsEnd(
                        ZonedDateTimeWithRelative.of(timestampToZonedDateTime(sessionRecord.getFechafinreserva())));
            }
        }
        if (sessionRecord.getFechaventa() != null) {
            if (olsonId != null) {
                sessionDTO.getDate().setSalesStart(ZonedDateTimeWithRelative.of(ZonedDateTime.ofInstant(sessionRecord.getFechaventa().toInstant(), ZoneId.of(olsonId))));
            } else {
                sessionDTO.getDate().setSalesStart(
                        ZonedDateTimeWithRelative.of(timestampToZonedDateTime(sessionRecord.getFechaventa())));
            }
        }
        if (sessionRecord.getFechafinsesion() != null) {
            if (olsonId != null) {
                sessionDTO.getDate().setSalesEnd(ZonedDateTimeWithRelative.of(ZonedDateTime.ofInstant(sessionRecord.getFechafinsesion().toInstant(), ZoneId.of(olsonId))));
            } else {
                sessionDTO.getDate().setSalesEnd(
                        ZonedDateTimeWithRelative.of(timestampToZonedDateTime(sessionRecord.getFechafinsesion())));
            }
        }
        if (secondaryMarket != null && secondaryMarket.getDates() != null) {
            if (secondaryMarket.getDates().getStartDate() != null) {
                if (olsonId != null) {
                    sessionDTO.getDate().setSecondaryMarketStart(ZonedDateTimeWithRelative.of(ZonedDateTime.ofInstant(secondaryMarket.getDates().getStartDate().toInstant(), ZoneId.of(olsonId))));
                } else {
                    sessionDTO.getDate().setSecondaryMarketStart(ZonedDateTimeWithRelative.of(secondaryMarket.getDates().getStartDate()));
                }
            }

            if (secondaryMarket.getDates().getEndDate() != null) {
                if (olsonId != null) {
                    sessionDTO.getDate().setSecondaryMarketEnd(ZonedDateTimeWithRelative.of(ZonedDateTime.ofInstant(secondaryMarket.getDates().getEndDate().toInstant(), ZoneId.of(olsonId))));
                } else {
                    sessionDTO.getDate().setSecondaryMarketEnd(ZonedDateTimeWithRelative.of(secondaryMarket.getDates().getEndDate()));
                }
            }
        }

    }

    private static void fillAccessControl(SessionRecord sessionRecord, SessionDTO sessionDTO) {
        if (sessionRecord.getTipohorarioaccesos() != null) {
            sessionDTO.setAccessScheduleType(AccessScheduleType.byType(sessionRecord.getTipohorarioaccesos().intValue()));
            if (AccessScheduleType.SPECIFIC.getType().equals(sessionRecord.getTipohorarioaccesos().intValue())) {
                if (sessionRecord.getAperturaaccesos() != null) {
                    sessionDTO.getDate().setAdmissionStart(ZonedDateTimeWithRelative.of(timestampToZonedDateTime(sessionRecord.getAperturaaccesos())));
                }
                if (sessionRecord.getCierreaccesos() != null) {
                    sessionDTO.getDate().setAdmissionEnd(ZonedDateTimeWithRelative.of(timestampToZonedDateTime(sessionRecord.getCierreaccesos())));
                }
            }
        }
        if (sessionRecord.getEspaciovalidacionacceso() != null) {
            sessionDTO.setSpace(new IdNameDTO(sessionRecord.getEspaciovalidacionacceso().longValue(), sessionRecord.getSpaceName()));
        }
        sessionDTO.setUseTemplateAccess(sessionRecord.getUsaaccesosplantilla());
    }

    public static void updateSessionConfig(SessionConfig sessionConfig, UpdateSessionRequestDTO session,
                                           SessionRecord sessionRecord) {
        sessionConfig.setEntity(new IdNameDTO(sessionRecord.getEntityId().longValue(), sessionRecord.getEntityName()));
        if (session.getEnableMembersLoginsLimit() != null) {
            sessionConfig.setMaxMembers(
                    CommonUtils.isTrue(session.getEnableMembersLoginsLimit()) ? session.getMembersLoginsLimit() : null);
        }
        if (CollectionUtils.isNotEmpty(session.getCountries()) || session.getEnableCountryFilter() != null) {
            updateSessionConfigCountryFilter(sessionConfig, session);
        }
        if (session.getQueueAlias() != null || session.getEnableQueue() != null) {
            updateSessionConfigQueueIt(sessionConfig, session);
        }

        if (session.getPresalesRedirectionPolicy() != null) {
            updateSessionConfigPresalesRedirectionPolicy(sessionConfig, session);
        }
        if (session.getSessionExternalConfig() != null) {
            if (sessionConfig.getSessionExternalConfig() == null) {
                sessionConfig.setSessionExternalConfig(new SessionExternalConfig());
            }
            if (session.getSessionExternalConfig().getDigitalTicketMode() != null) {
                sessionConfig.getSessionExternalConfig().setDigitalTicketMode(session.getSessionExternalConfig().getDigitalTicketMode());
            }
        }
    }

    private static void updateSessionConfigCountryFilter(SessionConfig sessionConfig, UpdateSessionRequestDTO session) {
        if (sessionConfig.getRestrictions() == null) {
            sessionConfig.setRestrictions(new Restrictions());
        }
        CountryConfig countryConfig = sessionConfig.getRestrictions().getCountryConfig() == null ? new CountryConfig()
                : sessionConfig.getRestrictions().getCountryConfig();
        if (session.getEnableCountryFilter() != null) {
            countryConfig.setActive(session.getEnableCountryFilter());
        }
        if (session.getCountries() != null) {
            countryConfig.setCountries(session.getCountries());
        }
        sessionConfig.getRestrictions().setCountryConfig(countryConfig);
    }

    private static void updateSessionConfigQueueIt(SessionConfig sessionConfig, UpdateSessionRequestDTO session) {
        QueueItConfig queueItConfig;
        if (sessionConfig.getQueueItConfig() == null) {
            queueItConfig = new QueueItConfig();
            queueItConfig.setParameterActive(true);
            queueItConfig.setValue(UUID.randomUUID().toString());
            queueItConfig.setParameter(SKIP_QUEUE);
        } else {
            queueItConfig = sessionConfig.getQueueItConfig();
        }
        if (session.getEnableQueue() != null) {
            queueItConfig.setActive(session.getEnableQueue());
        }
        if (session.getQueueAlias() != null) {
            queueItConfig.setAlias(session.getQueueAlias());
        }
        if (session.getQueueVersion() != null) {
            queueItConfig.setVersion(getQueueVersion(session));
        }
        sessionConfig.setQueueItConfig(queueItConfig);
    }

    private static void updateSessionConfigPresalesRedirectionPolicy(SessionConfig sessionConfig, UpdateSessionRequestDTO session) {

        if (session.getPresalesRedirectionPolicy() != null) {
            SessionPresalesConfig sessionPresalesConfig = new SessionPresalesConfig();
            PresalesRedirectionPolicy presalesRedirectionPolicy = new PresalesRedirectionPolicy();

            presalesRedirectionPolicy.setValue(session.getPresalesRedirectionPolicy().getValue());
            if (session.getPresalesRedirectionPolicy().getMode() != null) {
                presalesRedirectionPolicy.setMode(PresalesRedirectionLinkMode.valueOf(session.getPresalesRedirectionPolicy().getMode().name()));
            }

            sessionPresalesConfig.setPresalesRedirectionPolicy(presalesRedirectionPolicy);
            sessionConfig.setSessionPresalesConfig(sessionPresalesConfig);
        }
    }

    private static String getQueueVersion(UpdateSessionRequestDTO session) {
        if (session == null || session.getQueueVersion() == null) {
            return null;
        } else {
            return SessionVirtualQueueVersion.V3.getName();
        }

    }

    private static Integer hexColorToNumber(String color) {
        if (color != null) {
            try {
                return Integer.parseInt(color, 16);
            } catch (NumberFormatException e) {
                throw OneboxRestException.builder(MsEventSessionErrorCode.HEX_COLOR_INCORRECT).build();
            }
        }
        return null;
    }

    private static String numberColorToHex(Integer color) {
        if (color != null) {
            return Integer.toHexString(color);
        }
        return null;
    }

    public static SessionGroupConfigDTO toGroupConfigDTO(CpanelConfigSesionGruposRecord source) {
        SessionGroupConfigDTO target = new SessionGroupConfigDTO();
        target.setId(source.getIdsesion().longValue());
        target.setMaxGroups(source.getMaxgrupos());
        target.setMinAttendees(source.getMinasistentes());
        target.setMaxAttendees(source.getMaxasistentes());
        target.setMinCompanions(source.getMinacompanyantes());
        target.setMaxCompanions(source.getMaxacompanyantes());
        target.setCompanionsOccupyCapacity(CommonUtils.isTrue(source.getAcompanyantesocupanaforo()));
        return target;
    }

    public static void updateSessionGroupRecord(CpanelConfigSesionGruposRecord record, SessionGroupConfigDTO request) {
        if (record == null || request == null) {
            return;
        }
        updateField(record::setMaxgrupos, request.getMaxGroups());
        updateField(record::setMinasistentes, request.getMinAttendees());
        updateField(record::setMaxasistentes, request.getMaxAttendees());
        updateField(record::setMinacompanyantes, request.getMinCompanions());
        updateField(record::setMaxacompanyantes, request.getMaxCompanions());
        updateField(record::setAcompanyantesocupanaforo, isTrueAsByte(request.getCompanionsOccupyCapacity()));
    }

    public static Byte isTrueAsByte(Boolean value) {
        if (value == null) {
            return null;
        }
        return (byte) (value ? 1 : 0);
    }

    public static SessionsGroupsDTO toDTO(List<SessionsGroupDataRecord> source, SessionGroupType groupType) {
        return source.stream().map(r -> toDTO(r, groupType)).collect(Collectors.toCollection(SessionsGroupsDTO::new));
    }

    private static SessionsGroupDTO toDTO(SessionsGroupDataRecord source, SessionGroupType groupType) {
        SessionsGroupDTO target = new SessionsGroupDTO();
        target.setTotal(source.getTotal());
        ZonedDateTime date;
        date = timestampToZonedDateTime(source.getDate());
        ZonedDateTime startDate = date.with(groupType.getChronoField(), date.range(groupType.getChronoField()).getMinimum());
        ZonedDateTime endDate = date.with(groupType.getChronoField(), date.range(groupType.getChronoField()).getMaximum());
        target.setStartDate(startDate.with(LocalTime.MIN));
        target.setEndDate(endDate.with(LocalTime.MAX));
        return target;
    }

    private static Boolean isSmartBookingSession(SessionRecord sessionRecord) {
        return sessionRecord.getSbsesionrelacionada() != null && VenueTemplateType.ACTIVITY.getId().equals(sessionRecord.getVenueTemplateType());
    }
}
