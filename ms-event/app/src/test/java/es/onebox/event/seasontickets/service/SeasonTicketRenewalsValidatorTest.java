package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.dto.VenueDTO;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketInternalGenerationStatus;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.seasontickets.dto.UpdateSeasonTicketRequestDTO;
import es.onebox.event.seasontickets.dto.renewals.UpdateSeasonTicketRenewal;
import es.onebox.event.seasontickets.service.renewals.SeasonTicketRenewalsValidator;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Collections;

import static es.onebox.core.utils.common.CommonUtils.zonedDateTimeToTimestamp;
import static es.onebox.event.common.utils.ConverterUtils.isTrueAsByte;

public class SeasonTicketRenewalsValidatorTest {

    ZonedDateTime date1;
    ZonedDateTime date2;

    @BeforeEach
    public void init() {
        date1 = ZonedDateTime.now();
        date2 = date1.plusDays(1);
    }

    @Test
    public void isRenewalModifiedTest_noInfo() {
        UpdateSeasonTicketRenewal seasonTicketRenewal = new UpdateSeasonTicketRenewal();
        CpanelSeasonTicketRecord cpanelSeasonTicketRecord = new CpanelSeasonTicketRecord();

        boolean result = SeasonTicketRenewalsValidator.isRenewalModified(seasonTicketRenewal, cpanelSeasonTicketRecord);
        Assertions.assertFalse(result);
    }

    @Test
    public void isRenewalModifiedTest_noModification() {
        UpdateSeasonTicketRenewal seasonTicketRenewal = new UpdateSeasonTicketRenewal();
        seasonTicketRenewal.setRenewalEnabled(Boolean.TRUE);
        seasonTicketRenewal.setRenewalStartingDate(date1);
        seasonTicketRenewal.setRenewalEndDate(date2);

        CpanelSeasonTicketRecord cpanelSeasonTicketRecord = new CpanelSeasonTicketRecord();
        cpanelSeasonTicketRecord.setRenewalenabled(Boolean.TRUE);
        cpanelSeasonTicketRecord.setRenewalinitdate(zonedDateTimeToTimestamp(date1));
        cpanelSeasonTicketRecord.setRenewalenddate(zonedDateTimeToTimestamp(date2));

        boolean result = SeasonTicketRenewalsValidator.isRenewalModified(seasonTicketRenewal, cpanelSeasonTicketRecord);
        Assertions.assertFalse(result);
    }

    @Test
    public void isRenewalModifiedTest_modification() {
        UpdateSeasonTicketRenewal seasonTicketRenewal = new UpdateSeasonTicketRenewal();
        seasonTicketRenewal.setRenewalEnabled(Boolean.TRUE);
        seasonTicketRenewal.setRenewalStartingDate(date1);
        seasonTicketRenewal.setRenewalEndDate(date2);

        CpanelSeasonTicketRecord cpanelSeasonTicketRecord = new CpanelSeasonTicketRecord();
        cpanelSeasonTicketRecord.setRenewalenabled(Boolean.FALSE);
        cpanelSeasonTicketRecord.setRenewalinitdate(zonedDateTimeToTimestamp(date1));
        cpanelSeasonTicketRecord.setRenewalenddate(zonedDateTimeToTimestamp(date2));

        boolean result = SeasonTicketRenewalsValidator.isRenewalModified(seasonTicketRenewal, cpanelSeasonTicketRecord);
        Assertions.assertTrue(result);

        cpanelSeasonTicketRecord.setRenewalenabled(Boolean.TRUE);
        cpanelSeasonTicketRecord.setRenewalinitdate(zonedDateTimeToTimestamp(date2));
        result = SeasonTicketRenewalsValidator.isRenewalModified(seasonTicketRenewal, cpanelSeasonTicketRecord);
        Assertions.assertTrue(result);

        cpanelSeasonTicketRecord.setRenewalinitdate(zonedDateTimeToTimestamp(date1));
        cpanelSeasonTicketRecord.setRenewalenddate(zonedDateTimeToTimestamp(date1));
        result = SeasonTicketRenewalsValidator.isRenewalModified(seasonTicketRenewal, cpanelSeasonTicketRecord);
        Assertions.assertTrue(result);
    }

    @Test
    public void validateRenewalTest_incompleteFields() {
        UpdateSeasonTicketRequestDTO updatedSeasonTicket = new UpdateSeasonTicketRequestDTO();
        UpdateSeasonTicketRenewal seasonTicketRenewal = new UpdateSeasonTicketRenewal();
        seasonTicketRenewal.setRenewalEnabled(null);
        seasonTicketRenewal.setRenewalStartingDate(date1);
        seasonTicketRenewal.setRenewalEndDate(date2);
        updatedSeasonTicket.setRenewal(seasonTicketRenewal);

        CpanelSeasonTicketRecord cpanelSeasonTicketRecord = new CpanelSeasonTicketRecord();
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.FALSE);

        SessionRecord sessionRecord = new SessionRecord();

        OneboxRestException capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalOnUpdateSeasonTicket(updatedSeasonTicket, cpanelSeasonTicketRecord, sessionRecord);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED.getMessage(), capturedException.getMessage());


        seasonTicketRenewal.setRenewalEnabled(Boolean.FALSE);
        seasonTicketRenewal.setRenewalStartingDate(null);
        seasonTicketRenewal.setRenewalEndDate(date2);
        capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalOnUpdateSeasonTicket(updatedSeasonTicket, cpanelSeasonTicketRecord, sessionRecord);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED.getMessage(), capturedException.getMessage());


        seasonTicketRenewal.setRenewalEnabled(Boolean.FALSE);
        seasonTicketRenewal.setRenewalStartingDate(date1);
        seasonTicketRenewal.setRenewalEndDate(null);
        capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalOnUpdateSeasonTicket(updatedSeasonTicket, cpanelSeasonTicketRecord, sessionRecord);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED.getMessage(), capturedException.getMessage());


        seasonTicketRenewal.setRenewalEnabled(Boolean.FALSE);
        seasonTicketRenewal.setRenewalStartingDate(date1);
        seasonTicketRenewal.setRenewalEndDate(date2);
        capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalOnUpdateSeasonTicket(updatedSeasonTicket, cpanelSeasonTicketRecord, sessionRecord);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
    }

    @Test
    public void validateRenewalTest_initAfterBefore() {
        UpdateSeasonTicketRequestDTO updatedSeasonTicket = new UpdateSeasonTicketRequestDTO();
        UpdateSeasonTicketRenewal seasonTicketRenewal = new UpdateSeasonTicketRenewal();
        seasonTicketRenewal.setRenewalEnabled(Boolean.FALSE);
        seasonTicketRenewal.setRenewalStartingDate(date2);
        seasonTicketRenewal.setRenewalEndDate(date1);
        updatedSeasonTicket.setRenewal(seasonTicketRenewal);

        CpanelSeasonTicketRecord cpanelSeasonTicketRecord = new CpanelSeasonTicketRecord();
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.TRUE);

        SessionRecord sessionRecord = new SessionRecord();

        OneboxRestException capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalOnUpdateSeasonTicket(updatedSeasonTicket, cpanelSeasonTicketRecord, sessionRecord);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_INVALID_RENEWAL_DATES.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_INVALID_RENEWAL_DATES.getMessage(), capturedException.getMessage());


        seasonTicketRenewal.setRenewalStartingDate(date1);
        seasonTicketRenewal.setRenewalEndDate(date2);
        capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalOnUpdateSeasonTicket(updatedSeasonTicket, cpanelSeasonTicketRecord, sessionRecord);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(capturedException);
    }

    @Test
    public void validateRenewalTest_publishingNotEnabled() {
        UpdateSeasonTicketRequestDTO updatedSeasonTicket = new UpdateSeasonTicketRequestDTO();
        UpdateSeasonTicketRenewal seasonTicketRenewal = new UpdateSeasonTicketRenewal();
        seasonTicketRenewal.setRenewalEnabled(Boolean.TRUE);
        seasonTicketRenewal.setRenewalStartingDate(date1);
        seasonTicketRenewal.setRenewalEndDate(date2);
        updatedSeasonTicket.setRenewal(seasonTicketRenewal);

        CpanelSeasonTicketRecord cpanelSeasonTicketRecord = new CpanelSeasonTicketRecord();
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.TRUE);

        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setPublicado(isTrueAsByte(Boolean.FALSE));

        OneboxRestException capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalOnUpdateSeasonTicket(updatedSeasonTicket, cpanelSeasonTicketRecord, sessionRecord);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_PUBLISHING_NOT_ENABLED.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_PUBLISHING_NOT_ENABLED.getMessage(), capturedException.getMessage());


        sessionRecord.setPublicado(isTrueAsByte(Boolean.TRUE));
        capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalOnUpdateSeasonTicket(updatedSeasonTicket, cpanelSeasonTicketRecord, sessionRecord);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(capturedException);
    }

    @Test
    public void validateRenewalTest_publishingDate() {
        UpdateSeasonTicketRequestDTO updatedSeasonTicket = new UpdateSeasonTicketRequestDTO();
        UpdateSeasonTicketRenewal seasonTicketRenewal = new UpdateSeasonTicketRenewal();
        seasonTicketRenewal.setRenewalEnabled(Boolean.TRUE);
        seasonTicketRenewal.setRenewalStartingDate(date1);
        seasonTicketRenewal.setRenewalEndDate(date2);
        updatedSeasonTicket.setRenewal(seasonTicketRenewal);

        CpanelSeasonTicketRecord cpanelSeasonTicketRecord = new CpanelSeasonTicketRecord();
        cpanelSeasonTicketRecord.setAllowrenewal(Boolean.TRUE);

        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setPublicado(isTrueAsByte(Boolean.TRUE));
        ZonedDateTime publishDate = date1.plusDays(10);
        sessionRecord.setFechapublicacion(zonedDateTimeToTimestamp(publishDate));

        OneboxRestException capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalOnUpdateSeasonTicket(updatedSeasonTicket, cpanelSeasonTicketRecord, sessionRecord);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_PUBLISHING_DATES.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_PUBLISHING_DATES.getMessage(), capturedException.getMessage());


        publishDate = date1.minusDays(10);
        sessionRecord.setFechapublicacion(zonedDateTimeToTimestamp(publishDate));
        capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalOnUpdateSeasonTicket(updatedSeasonTicket, cpanelSeasonTicketRecord, sessionRecord);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(capturedException);
    }

    @Test
    public void validateRenewalSeasonTicketTest_sameSeasonTickets() {
        SeasonTicketDTO originSeasonTicketDTO = new SeasonTicketDTO();
        originSeasonTicketDTO.setId(1L);

        SeasonTicketDTO renewalSeasonTicketDTO = new SeasonTicketDTO();
        renewalSeasonTicketDTO.setId(1L);

        OneboxRestException capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalSeasonTicket(renewalSeasonTicketDTO, originSeasonTicketDTO, null, null);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_SAME_SEASON_TICKETS.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_SAME_SEASON_TICKETS.getMessage(), capturedException.getMessage());
    }

    @Test
    public void validateRenewalSeasonTicketTest_renewalNotAllowed() {
        SeasonTicketDTO originSeasonTicketDTO = new SeasonTicketDTO();
        originSeasonTicketDTO.setId(1L);

        SeasonTicketDTO renewalSeasonTicketDTO = new SeasonTicketDTO();
        renewalSeasonTicketDTO.setId(2L);
        renewalSeasonTicketDTO.setAllowRenewal(Boolean.FALSE);

        OneboxRestException capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalSeasonTicket(renewalSeasonTicketDTO, originSeasonTicketDTO, null, null);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED.getMessage(), capturedException.getMessage());
    }

    @Test
    public void validateRenewalSeasonTicketTest_differentVenues() {
        SeasonTicketDTO originSeasonTicketDTO = new SeasonTicketDTO();
        originSeasonTicketDTO.setId(1L);
        VenueDTO originVenue = new VenueDTO();
        originVenue.setId(1L);
        originSeasonTicketDTO.setVenues(Collections.singletonList(originVenue));

        SeasonTicketDTO renewalSeasonTicketDTO = new SeasonTicketDTO();
        renewalSeasonTicketDTO.setId(2L);
        renewalSeasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        VenueDTO renewalVenue = new VenueDTO();
        renewalVenue.setId(2L);
        renewalSeasonTicketDTO.setVenues(Collections.singletonList(renewalVenue));

        OneboxRestException capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalSeasonTicket(renewalSeasonTicketDTO, originSeasonTicketDTO, null, null);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DIFFERENT_VENUES.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DIFFERENT_VENUES.getMessage(), capturedException.getMessage());
    }

    @Test
    public void validateRenewalSeasonTicketTest_differentEntity() {
        SeasonTicketDTO originSeasonTicketDTO = new SeasonTicketDTO();
        originSeasonTicketDTO.setId(1L);
        VenueDTO originVenue = new VenueDTO();
        originVenue.setId(1L);
        originSeasonTicketDTO.setVenues(Collections.singletonList(originVenue));
        originSeasonTicketDTO.setEntityId(1L);

        SeasonTicketDTO renewalSeasonTicketDTO = new SeasonTicketDTO();
        renewalSeasonTicketDTO.setId(2L);
        renewalSeasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        VenueDTO renewalVenue = new VenueDTO();
        renewalVenue.setId(1L);
        renewalSeasonTicketDTO.setVenues(Collections.singletonList(renewalVenue));
        renewalSeasonTicketDTO.setEntityId(2L);

        OneboxRestException capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalSeasonTicket(renewalSeasonTicketDTO, originSeasonTicketDTO, null, null);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DIFFERENT_ENTITY.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DIFFERENT_ENTITY.getMessage(), capturedException.getMessage());
    }

    @Test
    public void validateRenewalSeasonTicketTest_inSale() {
        SeasonTicketDTO originSeasonTicketDTO = new SeasonTicketDTO();
        originSeasonTicketDTO.setId(1L);
        VenueDTO originVenue = new VenueDTO();
        originVenue.setId(1L);
        originSeasonTicketDTO.setVenues(Collections.singletonList(originVenue));
        originSeasonTicketDTO.setEntityId(1L);
        originSeasonTicketDTO.setStatus(SeasonTicketStatusDTO.READY);

        SeasonTicketDTO renewalSeasonTicketDTO = new SeasonTicketDTO();
        renewalSeasonTicketDTO.setId(2L);
        renewalSeasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        VenueDTO renewalVenue = new VenueDTO();
        renewalVenue.setId(1L);
        renewalSeasonTicketDTO.setVenues(Collections.singletonList(renewalVenue));
        renewalSeasonTicketDTO.setEntityId(1L);
        renewalSeasonTicketDTO.setStatus(SeasonTicketStatusDTO.CANCELLED);

        OneboxRestException capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalSeasonTicket(renewalSeasonTicketDTO, originSeasonTicketDTO, null, null);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_ON_SALE.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_ON_SALE.getMessage(), capturedException.getMessage());


        originSeasonTicketDTO.setStatus(SeasonTicketStatusDTO.CANCELLED);
        renewalSeasonTicketDTO.setStatus(SeasonTicketStatusDTO.READY);

        capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalSeasonTicket(renewalSeasonTicketDTO, originSeasonTicketDTO, null, null);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_ON_SALE.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_ON_SALE.getMessage(), capturedException.getMessage());
    }

    @Test
    public void validateRenewalSeasonTicketTest_differentMemberMandatory() {
        SeasonTicketDTO originSeasonTicketDTO = new SeasonTicketDTO();
        originSeasonTicketDTO.setId(1L);
        VenueDTO originVenue = new VenueDTO();
        originVenue.setId(1L);
        originSeasonTicketDTO.setVenues(Collections.singletonList(originVenue));
        originSeasonTicketDTO.setEntityId(1L);
        originSeasonTicketDTO.setStatus(SeasonTicketStatusDTO.PENDING_PUBLICATION);
        originSeasonTicketDTO.setMemberMandatory(Boolean.FALSE);

        SeasonTicketDTO renewalSeasonTicketDTO = new SeasonTicketDTO();
        renewalSeasonTicketDTO.setId(2L);
        renewalSeasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        VenueDTO renewalVenue = new VenueDTO();
        renewalVenue.setId(1L);
        renewalSeasonTicketDTO.setVenues(Collections.singletonList(renewalVenue));
        renewalSeasonTicketDTO.setEntityId(1L);
        renewalSeasonTicketDTO.setStatus(SeasonTicketStatusDTO.CANCELLED);
        renewalSeasonTicketDTO.setMemberMandatory(Boolean.TRUE);

        OneboxRestException capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalSeasonTicket(renewalSeasonTicketDTO, originSeasonTicketDTO, null, null);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DIFFERENT_MEMBER_MANDATORY.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DIFFERENT_MEMBER_MANDATORY.getMessage(), capturedException.getMessage());
    }

    @Test
    public void validateRenewalSeasonTicketTest_notReady() {
        SeasonTicketDTO originSeasonTicketDTO = new SeasonTicketDTO();
        originSeasonTicketDTO.setId(1L);
        VenueDTO originVenue = new VenueDTO();
        originVenue.setId(1L);
        originSeasonTicketDTO.setVenues(Collections.singletonList(originVenue));
        originSeasonTicketDTO.setEntityId(1L);
        originSeasonTicketDTO.setStatus(SeasonTicketStatusDTO.PENDING_PUBLICATION);
        originSeasonTicketDTO.setMemberMandatory(Boolean.FALSE);

        SeasonTicketDTO renewalSeasonTicketDTO = new SeasonTicketDTO();
        renewalSeasonTicketDTO.setId(2L);
        renewalSeasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        VenueDTO renewalVenue = new VenueDTO();
        renewalVenue.setId(1L);
        renewalSeasonTicketDTO.setVenues(Collections.singletonList(renewalVenue));
        renewalSeasonTicketDTO.setEntityId(1L);
        renewalSeasonTicketDTO.setStatus(SeasonTicketStatusDTO.CANCELLED);
        renewalSeasonTicketDTO.setMemberMandatory(Boolean.FALSE);

        SeasonTicketInternalGenerationStatus originGenerationStatus = SeasonTicketInternalGenerationStatus.CREATED;
        SeasonTicketInternalGenerationStatus renewalGenerationStatus = SeasonTicketInternalGenerationStatus.READY;

        OneboxRestException capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalSeasonTicket(renewalSeasonTicketDTO, originSeasonTicketDTO, originGenerationStatus, renewalGenerationStatus);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_READY.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_READY.getMessage(), capturedException.getMessage());


        originGenerationStatus = SeasonTicketInternalGenerationStatus.READY;
        renewalGenerationStatus = SeasonTicketInternalGenerationStatus.CREATED;
        capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalSeasonTicket(renewalSeasonTicketDTO, originSeasonTicketDTO, originGenerationStatus, renewalGenerationStatus);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNotNull(capturedException);
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_READY.getErrorCode(), capturedException.getErrorCode());
        Assertions.assertEquals(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_READY.getMessage(), capturedException.getMessage());
    }

    @Test
    public void validateRenewalSeasonTicketTest_noErrors() {
        SeasonTicketDTO originSeasonTicketDTO = new SeasonTicketDTO();
        originSeasonTicketDTO.setId(1L);
        VenueDTO originVenue = new VenueDTO();
        originVenue.setId(1L);
        originSeasonTicketDTO.setVenues(Collections.singletonList(originVenue));
        originSeasonTicketDTO.setEntityId(1L);
        originSeasonTicketDTO.setStatus(SeasonTicketStatusDTO.PENDING_PUBLICATION);
        originSeasonTicketDTO.setMemberMandatory(Boolean.FALSE);

        SeasonTicketDTO renewalSeasonTicketDTO = new SeasonTicketDTO();
        renewalSeasonTicketDTO.setId(2L);
        renewalSeasonTicketDTO.setAllowRenewal(Boolean.TRUE);
        VenueDTO renewalVenue = new VenueDTO();
        renewalVenue.setId(1L);
        renewalSeasonTicketDTO.setVenues(Collections.singletonList(renewalVenue));
        renewalSeasonTicketDTO.setEntityId(1L);
        renewalSeasonTicketDTO.setStatus(SeasonTicketStatusDTO.CANCELLED);
        renewalSeasonTicketDTO.setMemberMandatory(Boolean.FALSE);

        SeasonTicketInternalGenerationStatus originGenerationStatus = SeasonTicketInternalGenerationStatus.READY;
        SeasonTicketInternalGenerationStatus renewalGenerationStatus = SeasonTicketInternalGenerationStatus.READY;

        OneboxRestException capturedException = null;
        try {
            SeasonTicketRenewalsValidator.validateRenewalSeasonTicket(renewalSeasonTicketDTO, originSeasonTicketDTO, originGenerationStatus, renewalGenerationStatus);
        } catch (OneboxRestException e) {
            capturedException = e;
        }
        Assertions.assertNull(capturedException);
    }
}
