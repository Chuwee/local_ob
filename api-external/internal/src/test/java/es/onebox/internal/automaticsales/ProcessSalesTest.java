package es.onebox.internal.automaticsales;

import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.dto.MsSaleRequestDTO;
import es.onebox.common.datasources.ms.channel.dto.MsSaleRequestsResponseDTO;
import es.onebox.common.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.common.datasources.ms.channel.enums.MsSaleRequestsStatus;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.venue.dto.BasePriceType;
import es.onebox.common.datasources.ms.venue.dto.SectorDTO;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.internal.automaticsales.filemanagement.dto.SaleRequestListDTO;
import es.onebox.internal.automaticsales.processsales.dto.ProcessSalesRequest;
import es.onebox.internal.automaticsales.processsales.dto.SaleDTO;
import es.onebox.internal.automaticsales.processsales.service.ProcessSalesService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


public class ProcessSalesTest {

    @Mock
    private MsEventRepository eventRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private VenueTemplateRepository venuesRepository;

    @InjectMocks
    private ProcessSalesService processSalesService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidateSalesWithConfig() throws IOException {

        ProcessSalesRequest request = TestUtils.getObjectFromFile("ProcessSalesRequest.json", ProcessSalesRequest.class);
        Long sessionId = 1L;
        ChannelDTO channel = getChannel();
        MsSaleRequestsResponseDTO saleRequest = getSaleRequest();
        List<BasePriceType> basePriceTypes = new ArrayList<>();
        basePriceTypes.add(getPriceType("Zona base"));
        List<SectorDTO> sectors = new ArrayList<>();
        sectors.add(getSector("120"));
        when(eventRepository.getSession(anyLong())).thenReturn(getSession());
        when(channelRepository.getChannel(anyLong())).thenReturn(channel);
        when(channelRepository.getSaleRequests(anyLong(), anyLong())).thenReturn(saleRequest);
        when(venuesRepository.getPriceTypes(anyLong())).thenReturn(basePriceTypes);
        when(venuesRepository.getSectors(anyLong())).thenReturn(sectors);

        String fileName = request.getProcessSalesConfigurationRequest().getFilename();
        request.getProcessSalesConfigurationRequest().setFilename(null);

        assertThrowException("ERROR_VOID_FILENAME", request, sessionId);

        request.getProcessSalesConfigurationRequest().setFilename(fileName);

        SaleRequestListDTO saleRequestListDTO = request.getSaleRequestListDTO();
        request.setSaleRequestListDTO(new SaleRequestListDTO());

        assertThrowException("ERROR_VOID_PROCESSING_FILE", request, sessionId);

        request.setSaleRequestListDTO(saleRequestListDTO);

        request.getProcessSalesConfigurationRequest().setUseLocators(true);

        assertThrowException("ORIGINAL_LOCATOR_NULL", request, sessionId);

        request.getProcessSalesConfigurationRequest().setUseLocators(false);

        request.getProcessSalesConfigurationRequest().setUseSeatMappings(true);

        assertThrowException("SEAT_ID_NULL", request, sessionId);

        request.getProcessSalesConfigurationRequest().setUseSeatMappings(false);

        request.getProcessSalesConfigurationRequest().setAllowSkipNonAdjacentSeats(true);
        request.getProcessSalesConfigurationRequest().setAllowBreakAdjacentSeats(true);

        assertThrowException("INVALID_ADJACENCY_CONFIGURATION", request, sessionId);

        request.getProcessSalesConfigurationRequest().setAllowSkipNonAdjacentSeats(false);
        request.getProcessSalesConfigurationRequest().setAllowBreakAdjacentSeats(false);

        Long channelId = request.getProcessSalesConfigurationRequest().getChannelId();
        request.getProcessSalesConfigurationRequest().setChannelId(null);

        assertThrowException("CHANNEL_ID_REQUIRED", request, sessionId);

        request.getProcessSalesConfigurationRequest().setChannelId(channelId);

        channel.setStatus(ChannelStatus.BLOCKED);

        assertThrowException("WRONG_CHANNEL_STATUS", request, sessionId);

        channel.setStatus(ChannelStatus.ACTIVE);

        List<MsSaleRequestDTO> data = saleRequest.getData();
        saleRequest.setData(new ArrayList<>());

        assertThrowException("WRONG_SALES_REQUEST_STATUS", request, sessionId);

        saleRequest.setData(data);

        request.getProcessSalesConfigurationRequest().setDefaultPurchaseLanguage(false);

        assertThrowException("LANGUAGE_CODE_NULL", request, sessionId);

        request.getProcessSalesConfigurationRequest().setDefaultPurchaseLanguage(true);

        assertThrowException("PRICE_ZONE_ID_NOT_EXISTS", request, sessionId);

        basePriceTypes.add(getPriceType("Zona base N"));

        assertThrowException("SECTOR_ID_NOT_EXISTS", request, sessionId);

        sectors.add(getSector("116"));

        String email = request.getSaleRequestListDTO().get(0).getEmail();
        request.getSaleRequestListDTO().get(0).setEmail(null);

        assertThrowException("WRONG_EMAIL_IN_ROWS", request, sessionId);

        request.getSaleRequestListDTO().get(0).setEmail(email);

        request.getSaleRequestListDTO().forEach(sale -> sale.setProcessed(true));

        assertThrowException("ERROR_VOID_PROCESSING_FILE", request, sessionId);
    }

    private void assertThrowException(String errorCode, ProcessSalesRequest request, Long sessionId) {
        OneboxRestException exception = assertThrows(OneboxRestException.class,
                () -> processSalesService.validateSalesWithConfig(sessionId, getSales(request), request.getProcessSalesConfigurationRequest()));
        assertEquals(errorCode, exception.getErrorCode());
    }


    private List<SaleDTO> getSales(ProcessSalesRequest request) {
        return request.getSaleRequestListDTO().stream().map(ProcessSalesService::getSaleDTO).collect(Collectors.toList());
    }

    private SessionDTO getSession() {
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(1L);
        sessionDTO.setEventId(1L);
        sessionDTO.setVenueConfigId(1L);
        return sessionDTO;
    }

    private ChannelDTO getChannel() {
        ChannelDTO channelDTO = new ChannelDTO();
        channelDTO.setStatus(ChannelStatus.ACTIVE);
        return channelDTO;
    }

    private MsSaleRequestsResponseDTO getSaleRequest() {
        MsSaleRequestsResponseDTO saleRequest = new MsSaleRequestsResponseDTO();
        MsSaleRequestDTO requestDTO = new MsSaleRequestDTO();
        requestDTO.setStatus(MsSaleRequestsStatus.ACCEPTED);
        List<MsSaleRequestDTO> msSaleRequestDTOList = List.of(requestDTO);
        saleRequest.setData(msSaleRequestDTOList);
        return saleRequest;
    }

    private BasePriceType getPriceType(String name) {
        BasePriceType basePriceType = new BasePriceType();
        basePriceType.setName(name);
        return basePriceType;
    }

    private SectorDTO getSector(String name) {
        SectorDTO sectorDTO = new SectorDTO();
        sectorDTO.setName(name);
        return sectorDTO;
    }
}
