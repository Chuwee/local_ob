package es.onebox.event.sessions.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.datasources.integration.avet.config.dto.SessionMatch;
import es.onebox.event.sessions.domain.sessionconfig.PreSaleConfig;
import es.onebox.event.sessions.dto.CreateSessionPreSaleConfigDTO;
import es.onebox.event.sessions.dto.PresaleLoyaltyProgram;
import es.onebox.event.sessions.dto.SessionPreSaleConfigDTO;
import es.onebox.event.sessions.dto.UpdateSessionPreSaleConfigDTO;
import es.onebox.event.sessions.enums.PresaleStatus;
import es.onebox.event.sessions.enums.PresaleValidationRangeType;
import es.onebox.event.sessions.enums.PresaleValidatorType;
import es.onebox.jooq.cpanel.tables.records.CpanelPreventaLoyaltyProgramRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPreventaRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class SessionPreSaleConfigConverter {

    private SessionPreSaleConfigConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static SessionPreSaleConfigDTO toDTO(CpanelPreventaRecord record, List<Integer> activeChannels,
                                                List<Integer> activeCustomerTypes, CpanelPreventaLoyaltyProgramRecord loyaltyProgram) {
        if (isNull(record)) {
            return null;
        }
        SessionPreSaleConfigDTO result = new SessionPreSaleConfigDTO();
        result.setId(record.getIdpreventa().longValue());
        result.setActive(PresaleStatus.ACTIVE.getId().equals(record.getEstado()));
        result.setName(record.getNombre());
        result.setValidationRangeType(PresaleValidationRangeType.byId(record.getTiporangovalidacion()));
        result.setStartDate(CommonUtils.timestampToZonedDateTime(record.getFechainiciopreventa()));
        result.setEndDate(CommonUtils.timestampToZonedDateTime(record.getFechafinpreventa()));
        if (CollectionUtils.isNotEmpty(activeChannels)) {
            result.setActiveChannels(activeChannels);
        } else {
            result.setActiveChannels(new ArrayList<>());
        }
        if (CollectionUtils.isNotEmpty(activeCustomerTypes)) {
            result.setActiveCustomerTypes(activeCustomerTypes);
        } else {
            result.setActiveCustomerTypes(new ArrayList<>());
        }
        result.setValidatorType(PresaleValidatorType.byId(record.getTipovalidador()));
        if (PresaleValidatorType.COLLECTIVE.getId().equals(record.getTipovalidador())) {
            result.setValidatorId(record.getIdvalidador().longValue());
        }

        result.setMemberTicketsLimitEnabled(record.getLimiteticketssocio() != null);
        result.setMemberTicketsLimit(record.getLimiteticketssocio());
        result.setGeneralTicketsLimit(record.getLimiteacompanantes());
        if (loyaltyProgram != null) {
            result.setLoyaltyProgram(new PresaleLoyaltyProgram());
            result.getLoyaltyProgram().setEnabled(Boolean.TRUE);
            result.getLoyaltyProgram().setPoints(loyaltyProgram.getPoints().longValue());
        }
        result.setMultiplePurchase(BooleanUtils.toBoolean(record.getPermitirrecomprar()));

        return result;
    }

    public static void fillPresale(CpanelPreventaRecord preventaRecord, CreateSessionPreSaleConfigDTO request, Long sessionId, SessionMatch sessionMatch) {
        preventaRecord.setEstado(PresaleStatus.INACTIVE.getId());
        preventaRecord.setIdsesion(sessionId.intValue());
        preventaRecord.setNombre(request.getName());
        preventaRecord.setTipovalidador(request.getValidatorType().getId());
        preventaRecord.setPermitirrecomprar((byte) 0);
        if (PresaleValidatorType.COLLECTIVE.equals(request.getValidatorType())) {
            preventaRecord.setIdvalidador(request.getValidatorId().intValue());
        }

        if (request.getAdditionalConfig() != null) {
            ConverterUtils.updateField(preventaRecord::setFechainiciopreventa, CommonUtils.zonedDateTimeToTimestamp(request.getAdditionalConfig().getStartDate()));
            ConverterUtils.updateField(preventaRecord::setFechafinpreventa, CommonUtils.zonedDateTimeToTimestamp(request.getAdditionalConfig().getEndDate()));
        }

        if (sessionMatch != null) {
            preventaRecord.setLimiteticketssocio(sessionMatch.getPartnerTicketsLimit());
            preventaRecord.setLimiteacompanantes(sessionMatch.getPartnerCompanionTicketsLimit());
        }
    }

    public static void fillPresale(CpanelPreventaRecord preventaRecord, UpdateSessionPreSaleConfigDTO request, Boolean isAvetSession) {
        if (request.getActive() != null) {
            preventaRecord.setEstado(request.getActive() ? PresaleStatus.ACTIVE.getId() : PresaleStatus.INACTIVE.getId());
        }
        ConverterUtils.updateField(preventaRecord::setNombre, request.getName());
        if (request.getValidationRangeType() != null) {
            preventaRecord.setTiporangovalidacion(request.getValidationRangeType().getId());
        }
        if (PresaleValidationRangeType.DATE_RANGE.equals(request.getValidationRangeType())) {
            ConverterUtils.updateField(preventaRecord::setFechainiciopreventa, CommonUtils.zonedDateTimeToTimestamp(request.getStartDate()));
            ConverterUtils.updateField(preventaRecord::setFechafinpreventa, CommonUtils.zonedDateTimeToTimestamp(request.getEndDate()));
        }
        if (!isAvetSession) {
            if (request.getMemberTicketsLimitEnabled() != null) {
                preventaRecord.setLimiteticketssocio(request.getMemberTicketsLimitEnabled() ? request.getMemberTicketsLimit() : null);
            }
            ConverterUtils.updateField(preventaRecord::setLimiteacompanantes, request.getGeneralTicketsLimit());
        }
        if (request.getMultiplePurchase() != null) {
            preventaRecord.setPermitirrecomprar((byte) (request.getMultiplePurchase() ? 1 : 0));
        }
    }

    public static void updateEntityLegacy(PreSaleConfig preSaleConfig, CpanelPreventaRecord preventaRecord, List<Integer> activeChannels) {
        preSaleConfig.setId(preventaRecord.getIdpreventa().longValue());
        preSaleConfig.setActive(PresaleStatus.ACTIVE.getId().equals(preventaRecord.getEstado()));
        if (PresaleValidationRangeType.ALL.getId().equals(preventaRecord.getTiporangovalidacion())) {
            preSaleConfig.setStartDate(ZonedDateTime.now());
            preSaleConfig.setEndDate(ZonedDateTime.now().plusYears(10));
        } else if (PresaleValidationRangeType.DATE_RANGE.getId().equals(preventaRecord.getTiporangovalidacion())) {
            preSaleConfig.setStartDate(CommonUtils.timestampToZonedDateTime(preventaRecord.getFechainiciopreventa()));
            preSaleConfig.setEndDate(CommonUtils.timestampToZonedDateTime(preventaRecord.getFechafinpreventa()));
        }
        ConverterUtils.updateField(preSaleConfig::setActiveChannels, activeChannels);
        ConverterUtils.updateField(preSaleConfig::setGeneralTicketsLimit, preventaRecord.getLimiteacompanantes());
        ConverterUtils.updateField(preSaleConfig::setMemberTicketsLimit, preventaRecord.getLimiteticketssocio());
        if (preventaRecord.getPermitirrecomprar() != null) {
            ConverterUtils.updateField(preSaleConfig::setMultiplePurchase, BooleanUtils.toBoolean(preventaRecord.getPermitirrecomprar()));
        }
    }
}
