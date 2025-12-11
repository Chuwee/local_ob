package es.onebox.event.report.converter;

import es.onebox.core.file.exporter.generator.export.Translation;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.common.amqp.eventsreport.SeasonTicketRenewalsReportMessage;
import es.onebox.event.datasources.ms.client.dto.Customer;
import es.onebox.event.report.enums.MsEventReportType;
import es.onebox.event.report.model.filter.SeasonTicketRenewalsReportSearchRequest;
import es.onebox.event.report.model.report.SeasonTicketRenewalsReportDTO;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewal;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeat;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketSeatType;
import es.onebox.event.seasontickets.dto.renewals.SeatMappingStatus;
import es.onebox.event.seasontickets.dto.renewals.SeatRenewal;
import es.onebox.event.seasontickets.dto.renewals.SeatRenewalStatus;

import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import static es.onebox.core.file.exporter.generator.export.TranslationUtils.searchTranslation;

public class SeasonTicketRenewalsReportConverter {

    private SeasonTicketRenewalsReportConverter() {
        throw new UnsupportedOperationException();
    }

    public static SeasonTicketRenewalsReportMessage toMessage(final SeasonTicketRenewalsReportSearchRequest filter,
                                                              final String exportId) {
        if (filter == null) {
            return null;
        }
        SeasonTicketRenewalsReportMessage message = new SeasonTicketRenewalsReportMessage();
        message.setSeasonTicketRenewalSeatsFilter(filter.getSeasonTicketRenewalSeatsFilter());

        message.setFields(filter.getFields());

        message.setTranslations(filter.getTranslations());
        message.setFields(filter.getFields());
        message.setFormat(filter.getFormat());
        message.setEmail(filter.getEmail());
        message.setUserId(filter.getUserId());
        message.setExportType(MsEventReportType.SEASON_TICKETS_RENEWALS);
        message.setExportId(exportId);
        message.setTimeZone(filter.getTimeZone());
        message.setCharset(filter.getCharset());
        message.setCsvSeparatorFormat(filter.getCsvSeparatorFormat());
        message.setCsvfractionDigitsSeparatorFormat(filter.getCsvfractionDigitsSeparatorFormat());
        message.setLanguage(filter.getLanguage());

        return message;
    }

    public static SeasonTicketRenewalsReportDTO toReport(SeasonTicketRenewalSeat in, String timeZone, Set<Translation> translations) {
        SeasonTicketRenewalsReportDTO out = new SeasonTicketRenewalsReportDTO();
        out.setId(in.getId());
        out.setUserId(in.getUserId());
        out.setMemberId(in.getMemberId());
        out.setProductClientId(in.getProductClientId());
        out.setEmail(in.getEmail());
        out.setName(in.getName());
        out.setSurname(in.getSurname());
        out.setBirthday(in.getBirthday());
        out.setPhoneNumber(in.getPhoneNumber());
        out.setSeasonTicketId(in.getSeasonTicketId());
        out.setSeasonTicketName(in.getSeasonTicketName());
        out.setPostalCode(in.getPostalCode());
        out.setGender(in.getGender());
        out.setLanguage(in.getLanguage());
        out.setCountry(in.getCountry());
        out.setCountrySubdivision(in.getCountrySubdivision());
        out.setCity(in.getCity());
        out.setIdCard(in.getIdCard());
        out.setSignUpDate(in.getSignUpDate());
        out.setAddress(in.getAddress());
        out.setEntityId(in.getEntityId());
        out.setBalance(NumberUtils.zeroIfNull(in.getBalance()));

        SeatRenewal historicSeat = in.getHistoricSeat();
        if (historicSeat != null) {
            out.setHistoricSeatType(
                    searchTranslation(
                            translations,
                            Optional.ofNullable(historicSeat.getSeatType())
                                    .map(SeasonTicketSeatType::name)
                                    .orElse(null)
                    )
            );
            out.setHistoricSeatNotNumberedZoneId(historicSeat.getNotNumberedZoneId());
            out.setHistoricSeatSectorId(historicSeat.getSectorId());
            out.setHistoricSeatRowId(historicSeat.getRowId());
            out.setHistoricSeatSeatId(historicSeat.getSeatId());
            out.setHistoricSeatSector(historicSeat.getSector());
            out.setHistoricSeatRow(historicSeat.getRow());
            out.setHistoricSeatSeat(historicSeat.getSeat());
            out.setHistoricSeatPriceZone(historicSeat.getPrizeZone());
            out.setHistoricSeatNotNumberedZone(historicSeat.getNotNumberedZone());
        }

        out.setHistoricRate(in.getHistoricRate());
        out.setHistoricRateId(in.getHistoricRateId());

        SeatRenewal actualSeat = in.getActualSeat();
        if (actualSeat != null) {
            out.setActualSeatType(
                    searchTranslation(
                            translations,
                            Optional.ofNullable(actualSeat.getSeatType())
                                    .map(SeasonTicketSeatType::name)
                                    .orElse(null)
                    )
            );
            out.setActualSeatNotNumberedZoneId(actualSeat.getNotNumberedZoneId());
            out.setActualSeatSectorId(actualSeat.getSectorId());
            out.setActualSeatRowId(actualSeat.getRowId());
            out.setActualSeatSeatId(actualSeat.getSeatId());
            out.setActualSeatSector(actualSeat.getSector());
            out.setActualSeatRow(actualSeat.getRow());
            out.setActualSeatSeat(actualSeat.getSeat());
            out.setActualSeatPriceZone(actualSeat.getPrizeZone());
            out.setActualSeatNotNumberedZone(actualSeat.getNotNumberedZone());
        }

        out.setActualRate(in.getActualRate());
        out.setActualRateId(in.getActualRateId());
        out.setMappingStatus(
                searchTranslation(
                        translations,
                        Optional.ofNullable(in.getMappingStatus())
                                .map(SeatMappingStatus::name)
                                .orElse(null)
                )
        );

        out.setRenewalStatus(
                searchTranslation(
                        translations,
                        Optional.ofNullable(in.getRenewalStatus())
                                .map(SeatRenewalStatus::name)
                                .orElse(null)
                )
        );

        SeasonTicketRenewal seasonTicketRenewal = in.getRenewalSettings();
        if (seasonTicketRenewal != null) {
            if (seasonTicketRenewal.getRenewalStartingDate() != null) {
                out.setRenewalsSettingsStartDate(
                        seasonTicketRenewal.getRenewalStartingDate().toInstant().atZone(ZoneId.of(timeZone)).toLocalDate()
                );
            }
            if (seasonTicketRenewal.getRenewalEndDate() != null) {
                out.setRenewalsSettingsEndDate(
                        seasonTicketRenewal.getRenewalEndDate().toInstant().atZone(ZoneId.of(timeZone)).toLocalDate()
                );
            }
            out.setRenewalsSettingsEnable(seasonTicketRenewal.getRenewalEnabled());
            out.setRenewalsSettingsInProcess(seasonTicketRenewal.getRenewalInProcess());
            out.setRenewalsSettingsAutoRenewal(seasonTicketRenewal.getAutoRenewal());
        }

        out.setOrderCode(in.getOrderCode());
        out.setAutoRenewal(in.getAutoRenewal());

        return out;
    }

}