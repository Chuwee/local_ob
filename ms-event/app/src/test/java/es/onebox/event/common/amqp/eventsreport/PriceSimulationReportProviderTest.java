package es.onebox.event.common.amqp.eventsreport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.domain.VenueConfigPricesSimulation;
import es.onebox.event.pricesengine.PriceSimulationService;
import es.onebox.event.report.model.filter.PriceSimulationReportRequest;
import es.onebox.event.seasontickets.dto.pricesimulation.PriceSimulationResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class PriceSimulationReportProviderTest {

    private static final long MAX_REPORT_SIZE = 100000;

    @Mock
    private PriceSimulationReportProvider provider;

    @Mock
    private PriceSimulationService service;
    @Mock
    private ArrayList<VenueConfigPricesSimulation> mockArrayList;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        provider = new PriceSimulationReportProvider((int) MAX_REPORT_SIZE, service);
    }

    @Test
    void testNoRecords() {
        PriceSimulationReportRequest request = new PriceSimulationReportRequest();
        Long saleRequestId = 1L;
        request.setSaleRequestId(saleRequestId);


        PriceSimulationResponse response = new PriceSimulationResponse();

        doReturn(response).when(service).getPriceSimulation(saleRequestId);

        assertEquals(0, provider.fetchAll(request).size());
    }

    @Test
    void testPagination() {
        PriceSimulationReportRequest request = new PriceSimulationReportRequest();
        request.setSaleRequestId(1L);
        PriceSimulationResponse response = new PriceSimulationResponse();


        when(mockArrayList.size()).thenReturn(0);
        doReturn(response).when(service).getPriceSimulation(1L);
        assertEquals(0, provider.fetchAll(request).size());
        verify(service, times(1)).getPriceSimulation(1L);
    }

    @Test
    void testTooManyRecords() {
        PriceSimulationReportRequest request = new PriceSimulationReportRequest();
        Long saleRequestId = 1L;
        request.setSaleRequestId(saleRequestId);

        PriceSimulationResponse response = new PriceSimulationResponse();
        List<VenueConfigPricesSimulation> venueConfigPricesSimulationList = setTooManyElements();
        response.setData(venueConfigPricesSimulationList);

        doReturn(response).when(service).getPriceSimulation(saleRequestId);

        try {
            provider.validate(request);
        } catch (OneboxRestException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
            assertEquals(String.format(MsEventErrorCode.EXPORT_WITH_TOO_MANY_RECORDS.getMessage(), MAX_REPORT_SIZE), e.getMessage());
            return;
        }
        fail();
    }

    private List<VenueConfigPricesSimulation> setTooManyElements() {
        return Arrays.asList(new VenueConfigPricesSimulation[100001]);
    }

}