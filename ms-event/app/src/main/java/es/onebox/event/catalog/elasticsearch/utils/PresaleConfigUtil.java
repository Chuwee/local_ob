package es.onebox.event.catalog.elasticsearch.utils;


import es.onebox.core.utils.common.DateUtils;
import es.onebox.event.catalog.dto.presales.PresaleInputsValidatorType;
import es.onebox.event.catalog.dto.presales.PresaleValidatorType;
import es.onebox.event.catalog.elasticsearch.enums.PeriodRangeType;
import es.onebox.event.catalog.elasticsearch.dto.session.presaleconfig.PresaleConfig;
import es.onebox.event.catalog.elasticsearch.dto.session.presaleconfig.PresaleLoyaltyProgram;
import es.onebox.event.catalog.elasticsearch.dto.session.presaleconfig.PresaleValidityPeriod;
import es.onebox.event.sessions.dao.enums.PresaleStatus;
import es.onebox.event.sessions.dao.record.PresaleRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelColectivoRecord;
import org.apache.commons.collections4.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class PresaleConfigUtil {

    private static final Integer CUSTOMERS_NUM_INPUTS = 1;

    private PresaleConfigUtil(){ throw new UnsupportedOperationException("Can not instantiate utility class");}


    private static Boolean isValidOrFutureAndEnabled(PresaleRecord presale) {
        return presale != null
                && CollectionUtils.isNotEmpty(presale.getChannelIds())
                && PresaleStatus.ENABLED.equals(PresaleStatus.getById(presale.getEstado()))
                && validateDatesAndRangeType(presale);
    }

    private static boolean validateDatesAndRangeType(PresaleRecord presale) {
        if (PeriodRangeType.ALL.equals(PeriodRangeType.getById(presale.getTiporangovalidacion()))) {
            return true;
        } else if (PeriodRangeType.RANGE.equals(PeriodRangeType.getById(presale.getTiporangovalidacion()))) {
            ZonedDateTime currentDate = DateUtils.now();
            return currentDate.isBefore(DateUtils.getZonedDateTime(presale.getFechafinpreventa()));
        } else {
            return false;
        }
    }

    public static List<PresaleRecord> getPresalesConfigActives(List<PresaleRecord> sessionPresales) {
        return sessionPresales.stream()
                .filter( PresaleConfigUtil::isValidOrFutureAndEnabled)
                .toList();
    }

    public static Boolean channelHasValidOrFutureAndEnabledPresales(List<PresaleRecord> sessionPresales, Long channelId) {
        return sessionPresales.stream()
                .anyMatch( presale -> CollectionUtils.isNotEmpty(presale.getChannelIds()) && presale.getChannelIds().contains(channelId)
                        && isValidOrFutureAndEnabled(presale));

    }

    public static List<PresaleConfig> convertToPresaleConfigs(List<PresaleRecord> presalesConfig,
                                                              List<CpanelColectivoRecord> presaleCollectives) {
        if (CollectionUtils.isEmpty(presalesConfig)) {
            return null;
        }
        return presalesConfig.stream()
                .map(presaleConfig -> convertToPresaleConfig(presaleConfig, presaleCollectives))
                .toList();
    }

    private static PresaleConfig convertToPresaleConfig(PresaleRecord presaleRecord, List<CpanelColectivoRecord> presaleCollectives) {
        PresaleConfig presaleConfig = new PresaleConfig();
        if (presaleRecord != null) {
            presaleConfig.setId(presaleRecord.getIdpreventa());
            presaleConfig.setName(presaleRecord.getNombre());
            presaleConfig.setStatus(presaleRecord.getEstado());
            presaleConfig.setValidatorId(presaleRecord.getIdvalidador());
            presaleConfig.setValidatorType(presaleRecord.getTipovalidador());
            presaleConfig.setMemberTicketsLimit(presaleRecord.getLimiteticketssocio());
            presaleConfig.setGeneralTicketsLimit(presaleRecord.getLimiteacompanantes());
            presaleConfig.setValidityPeriod(convertToValidityPeriod(presaleRecord));
            presaleConfig.setChannelIds(presaleRecord.getChannelIds());
            presaleConfig.setCustomerTypes(presaleRecord.getCustomerTypeIds());
            presaleConfig.setLoyaltyProgram(convertToLoyaltyProgram(presaleRecord));
            if (PresaleValidatorType.COLLECTIVE.getId().equals(presaleRecord.getTipovalidador())) {
                Integer validationType = presaleCollectives.stream()
                                .filter( collective -> collective.getIdcolectivo().equals(presaleRecord.getIdvalidador()))
                                .map(CpanelColectivoRecord::getIdsubtipocolectivo)
                                .findFirst()
                                .orElse(null);
                presaleConfig.setNumInputs(PresaleInputsValidatorType.getById(validationType).getNumInputs());
            } else if (PresaleValidatorType.CUSTOMERS.getId().equals(presaleRecord.getTipovalidador())) {
                presaleConfig.setNumInputs(CUSTOMERS_NUM_INPUTS);
            }
        }
        return presaleConfig;
    }

    private static PresaleValidityPeriod convertToValidityPeriod(PresaleRecord presaleRecord) {
        PresaleValidityPeriod period = new PresaleValidityPeriod();
        period.setTo(DateUtils.getZonedDateTime(presaleRecord.getFechafinpreventa()));
        period.setFrom(DateUtils.getZonedDateTime(presaleRecord.getFechainiciopreventa()));
        period.setType(PeriodRangeType.getById(presaleRecord.getTiporangovalidacion()));
        return period;
    }

    private static PresaleLoyaltyProgram convertToLoyaltyProgram(PresaleRecord presaleRecord) {
        PresaleLoyaltyProgram loyaltyProgram = new PresaleLoyaltyProgram();
        Optional.ofNullable(presaleRecord.getPoints()).ifPresentOrElse(points -> {
            loyaltyProgram.setPoints(points);
            loyaltyProgram.setEnabled(Boolean.TRUE);
        }, () -> loyaltyProgram.setEnabled(Boolean.FALSE));
        return loyaltyProgram;
    }

}
