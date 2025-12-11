package es.onebox.event.common.amqp.eventsreport;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.event.datasources.ms.client.repository.CustomerRepository;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.report.model.filter.SeasonTicketRenewalsReportSearchRequest;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeat;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsFilter;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsResponse;
import es.onebox.event.seasontickets.service.renewals.SeasonTicketRenewalsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SeasonTicketRenewalsReportProviderTest {

    private static final long MAX_REPORT_SIZE = 100000;
    @Mock
    private SeasonTicketRenewalsService seasonTicketRenewalsService;
    @Mock
    private ArrayList<SeasonTicketRenewalSeat> mockArrayList;
    @Mock
    private CustomerRepository customerRepository;
    private SeasonTicketRenewalsReportProvider provider;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.provider = new SeasonTicketRenewalsReportProvider((int) MAX_REPORT_SIZE, seasonTicketRenewalsService);
    }

    @Test
    public void testNoRecords() {
        SeasonTicketRenewalsReportSearchRequest request = new SeasonTicketRenewalsReportSearchRequest();
        SeasonTicketRenewalSeatsFilter renewalsFilter = new SeasonTicketRenewalSeatsFilter();
        Long seasonTicketId = 1L;
        renewalsFilter.setSeasonTicketId(seasonTicketId);
        request.setSeasonTicketRenewalSeatsFilter(renewalsFilter);

        SeasonTicketRenewalSeatsResponse response = new SeasonTicketRenewalSeatsResponse();
        Metadata metadata = new Metadata();
        metadata.setTotal(5L);
        response.setMetadata(metadata);
        response.setData(mockArrayList);

        when(this.seasonTicketRenewalsService.getSeasonTicketRenewalSeats(seasonTicketId, renewalsFilter)).thenReturn(response);

        assertEquals(0, this.provider.fetchAll(request).size());
    }

    @Test
    public void testPagination() {
        SeasonTicketRenewalsReportSearchRequest request = new SeasonTicketRenewalsReportSearchRequest();
        SeasonTicketRenewalSeatsFilter renewalsFilter = new SeasonTicketRenewalSeatsFilter();
        Long seasonTicketId = 1L;
        renewalsFilter.setSeasonTicketId(seasonTicketId);
        renewalsFilter.setOffset(0L);
        renewalsFilter.setLimit(5L);
        request.setSeasonTicketRenewalSeatsFilter(renewalsFilter);

        SeasonTicketRenewalSeatsResponse response = new SeasonTicketRenewalSeatsResponse();
        Metadata metadata = new Metadata();
        metadata.setTotal(11L);
        response.setMetadata(metadata);
        response.setData(mockArrayList);

        when(mockArrayList.size()).thenReturn(0);
        when(this.seasonTicketRenewalsService.getSeasonTicketRenewalSeats(seasonTicketId, renewalsFilter)).thenReturn(response);

        assertEquals(0, this.provider.fetchAll(request).size());
        verify(this.seasonTicketRenewalsService, times(3)).getSeasonTicketRenewalSeats(seasonTicketId, renewalsFilter);
    }

    @Test
    public void testTooManyRecords() {
        SeasonTicketRenewalsReportSearchRequest request = new SeasonTicketRenewalsReportSearchRequest();
        SeasonTicketRenewalSeatsFilter renewalsFilter = new SeasonTicketRenewalSeatsFilter();
        Long seasonTicketId = 1L;
        renewalsFilter.setSeasonTicketId(seasonTicketId);
        request.setSeasonTicketRenewalSeatsFilter(renewalsFilter);
        SeasonTicketRenewalSeatsResponse response = new SeasonTicketRenewalSeatsResponse();
        Metadata metadata = new Metadata();
        metadata.setTotal(MAX_REPORT_SIZE + 1L);
        response.setMetadata(metadata);

        when(this.seasonTicketRenewalsService.getSeasonTicketRenewalSeats(seasonTicketId, renewalsFilter)).thenReturn(response);

        try {
            this.provider.validate(request);
        } catch (OneboxRestException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
            assertEquals(String.format(MsEventErrorCode.EXPORT_WITH_TOO_MANY_RECORDS.getMessage(), MAX_REPORT_SIZE), e.getMessage());
            return;
        }
        fail();
    }

}
