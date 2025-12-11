package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.core.utils.common.DateUtils;
import es.onebox.event.catalog.elasticsearch.dto.JoinField;
import es.onebox.event.catalog.elasticsearch.dto.seasonticket.ReleaseConfig;
import es.onebox.event.catalog.elasticsearch.dto.seasonticket.RenewalConfig;
import es.onebox.event.catalog.elasticsearch.dto.seasonticket.SeasonTicket;
import es.onebox.event.catalog.elasticsearch.dto.seasonticket.SeasonTicketData;
import es.onebox.event.catalog.elasticsearch.dto.seasonticket.SeatReallocationConfig;
import es.onebox.event.catalog.elasticsearch.dto.seasonticket.SeatReallocationPrice;
import es.onebox.event.catalog.elasticsearch.dto.seasonticket.TransferConfig;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import es.onebox.event.events.dao.record.SeasonTicketChangeSeatPricesRecord;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketReleaseSeat;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfig;
import es.onebox.event.seasontickets.dto.transferseat.TransferPolicy;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;

import static es.onebox.event.catalog.elasticsearch.utils.EventDataUtils.KEY_SEASON_TICKET;

public class SeasonTicketDataBuilder {

    private final SeasonTicket data;

    private CpanelSeasonTicketRecord seasonTicketRecord;
    private CpanelSesionRecord sessionRecord;
    private SeasonTicketRenewalConfig renewalConfig;
    private List<SeasonTicketChangeSeatPricesRecord> seatReallocationPrices;
    private SeasonTicketReleaseSeat releaseSeatConfig;

    public SeasonTicketDataBuilder(SeasonTicket data) {
        super();
        this.data = data;
    }

    public static SeasonTicketDataBuilder builder() {
        return new SeasonTicketDataBuilder(new SeasonTicket());
    }

    public static SeasonTicketDataBuilder builder(SeasonTicket data) {
        return new SeasonTicketDataBuilder(data);
    }

    public static SeasonTicketData buildSeasonTicketData(Long seasonTicketId) {
        SeasonTicketData seasonTicketData = new SeasonTicketData();
        seasonTicketData.setId(EventDataUtils.getSeasonTicketKey(seasonTicketId));
        seasonTicketData.setJoin(new JoinField(KEY_SEASON_TICKET, null));
        return seasonTicketData;
    }

    public SeasonTicketData build() {
        SeasonTicketData seasonTicketData = buildSeasonTicketData(seasonTicketRecord.getIdevento().longValue());

        buildSeasonTicketInfo();
        seasonTicketData.setSeasonTicket(data);
        return seasonTicketData;
    }

    public void buildSeasonTicketInfo() {
        // basic
        data.setSeasonTicketId(seasonTicketRecord.getIdevento().longValue());
        if (sessionRecord != null) {
            data.setSessionId(sessionRecord.getIdsesion().longValue());
        }
        data.setCustomerMaxSeats(seasonTicketRecord.getCustomermaxseats());
        data.setRegisterMandatory(BooleanUtils.isTrue(seasonTicketRecord.getRegistermandatory()));

        // configs
        data.setRenewalConfig(buildRenewalConfig(seasonTicketRecord, renewalConfig));
        data.setSeatReallocationConfig(buildSeatReallocationConfig(seasonTicketRecord, seatReallocationPrices));
        data.setTransferConfig(buildTransferConfig(seasonTicketRecord));
        data.setReleaseConfig(buildReleaseConfig(seasonTicketRecord, releaseSeatConfig));
    }

    private static RenewalConfig buildRenewalConfig(CpanelSeasonTicketRecord seasonTicket, SeasonTicketRenewalConfig config) {
        RenewalConfig renewalConfig = new RenewalConfig();
        renewalConfig.setEnabled(BooleanUtils.isTrue(seasonTicket.getAllowrenewal()) && BooleanUtils.isTrue(seasonTicket.getRenewalenabled()));
        renewalConfig.setAutomatic(BooleanUtils.isTrue(seasonTicket.getAutorenewal()));

        if (BooleanUtils.isTrue(seasonTicket.getRenewalenabled())) {
            renewalConfig.setStartDate(DateUtils.getZonedDateTime(seasonTicket.getRenewalinitdate()));
            renewalConfig.setEndDate(DateUtils.getZonedDateTime(seasonTicket.getRenewalenddate()));
        }

        if (config != null) {
            if (config.getRenewalType() != null) {
                renewalConfig.setType(config.getRenewalType().name());
            }
            renewalConfig.setBankAccountId(config.getBankAccountId());
            renewalConfig.setAutomaticMandatory(BooleanUtils.isTrue(config.getAutoRenewalMandatory()));
        }

        return renewalConfig;
    }

    private static SeatReallocationConfig buildSeatReallocationConfig(CpanelSeasonTicketRecord seasonTicket, List<SeasonTicketChangeSeatPricesRecord> prices) {
        SeatReallocationConfig seatReallocationConfig = new SeatReallocationConfig();
        seatReallocationConfig.setEnabled(BooleanUtils.isTrue(seasonTicket.getAllowchangeseat()) && BooleanUtils.isTrue(seasonTicket.getChangeseatenabled()));
        seatReallocationConfig.setFixedSurcharge(seasonTicket.getChangeseatfixedsurcharge());
        seatReallocationConfig.setReleasedSeatQuotaId(seasonTicket.getChangedseatquotaid());

        if (BooleanUtils.isTrue(seasonTicket.getChangeseatenabled())) {
            seatReallocationConfig.setStartDate(DateUtils.getZonedDateTime(seasonTicket.getChangeseatinitdate()));
            seatReallocationConfig.setEndDate(DateUtils.getZonedDateTime(seasonTicket.getChangeseatenddate()));
            if (BooleanUtils.isTrue(seasonTicket.getMaxchangeseatvalueenabled())) {
                seatReallocationConfig.setMaxChanges(seasonTicket.getMaxchangeseatvalue());
            }
        }

        List<SeatReallocationPrice> reallocationPrices = prices.stream()
                .map(p -> {
                    SeatReallocationPrice price =  new SeatReallocationPrice();
                    price.setRateId(p.getIdrate());
                    price.setSourcePriceType(p.getIdsourcepricetype());
                    price.setTargetPriceType(p.getIdtargetpricetype());
                    price.setValue(p.getValue());
                    return price;
                })
                .toList();
        seatReallocationConfig.setPrices(reallocationPrices);

        return seatReallocationConfig;
    }

    private static TransferConfig buildTransferConfig(CpanelSeasonTicketRecord seasonTicket) {
        TransferConfig transferConfig = new TransferConfig();
        transferConfig.setEnabled(BooleanUtils.isTrue(seasonTicket.getAllowtransferticket()));
        if (seasonTicket.getTransferpolicy() != null) {
            transferConfig.setTransferPolicy(TransferPolicy.getById(seasonTicket.getTransferpolicy()).name());
        }
        transferConfig.setTransferMaxDelayTime(seasonTicket.getTransferticketmaxdelaytime());
        transferConfig.setTransferMinDelayTime(seasonTicket.getTransferticketmindelaytime());
        transferConfig.setRecoveryMaxDelayTime(seasonTicket.getRecoveryticketmaxdelaytime());
        if (BooleanUtils.isTrue(seasonTicket.getEnablemaxtickettransfers())) {
            transferConfig.setMaxTransfers(seasonTicket.getMaxtickettransfers());
        }
        return transferConfig;
    }

    private static ReleaseConfig buildReleaseConfig(CpanelSeasonTicketRecord seasonTicket, SeasonTicketReleaseSeat config) {
        ReleaseConfig releaseConfig = new ReleaseConfig();
        if (config != null) {
            releaseConfig.setEnabled(BooleanUtils.isTrue(seasonTicket.getAllowreleaseseat()));
            releaseConfig.setReleaseMaxDelayTime(config.getReleaseSeatMaxDelayTime());
            releaseConfig.setReleaseMinDelayTime(config.getReleaseSeatMinDelayTime());
            releaseConfig.setRecoveryMaxDelayTime(config.getRecoverReleasedSeatMaxDelayTime());
            releaseConfig.setPercentage(config.getCustomerPercentage());
            releaseConfig.setExcludedSessions(config.getExcludedSessions());
            if (config.getEarningsLimit() != null && BooleanUtils.isTrue(config.getEarningsLimit().getEnabled())) {
                releaseConfig.setLimit(config.getEarningsLimit().getPercentage());
            }
            if (BooleanUtils.isTrue(config.getMaxReleasesEnabled())) {
                releaseConfig.setMaxReleases(config.getMaxReleases());
            }
        } else {
            releaseConfig.setEnabled(Boolean.FALSE);
        }
        return releaseConfig;
    }

    public SeasonTicketDataBuilder seasonTicketRecord(CpanelSeasonTicketRecord seasonTicketRecord) {
        this.seasonTicketRecord = seasonTicketRecord;
        return this;
    }

    public SeasonTicketDataBuilder sessionRecord(CpanelSesionRecord sessionRecord) {
        this.sessionRecord = sessionRecord;
        return this;
    }

    public SeasonTicketDataBuilder renewalConfig(SeasonTicketRenewalConfig renewalConfig) {
        this.renewalConfig = renewalConfig;
        return this;
    }

    public SeasonTicketDataBuilder seatReallocationPrices(List<SeasonTicketChangeSeatPricesRecord> seatReallocationPrices) {
        this.seatReallocationPrices = seatReallocationPrices;
        return this;
    }

    public SeasonTicketDataBuilder releaseSeatConfig(SeasonTicketReleaseSeat releaseSeatConfig) {
        this.releaseSeatConfig = releaseSeatConfig;
        return this;
    }
}