package es.onebox.mgmt.productsalerequest.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.products.ChannelSaleRequestDetail;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSaleRequestDetail;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSaleRequests;
import es.onebox.mgmt.products.dto.ProductSaleRequestsDetailDTO;
import es.onebox.mgmt.products.dto.ProductSaleRequestsResponseDTO;
import es.onebox.mgmt.products.dto.SearchProductSaleRequestFilterDTO;
import es.onebox.mgmt.products.enums.ProductSaleRequestsStatus;
import es.onebox.mgmt.productsalerequest.converter.ProductSaleRequestConverter;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.validation.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ProductSaleRequestServiceTest {

    @Mock
    private ValidationService validationService;

    @Mock
    private ChannelsRepository channelsRepository;

    @Mock
    private SecurityManager securityManager;

    @Mock
    private MasterdataService masterdataService;

    @InjectMocks
    private ProductSaleRequestService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetSaleRequestDetail() {
        Long saleRequestId = 1L;
        Long channelId = 1L;

        ProductSaleRequestDetail mockDetail = new ProductSaleRequestDetail();
        mockDetail.setId(saleRequestId);
        mockDetail.setStatus(ProductSaleRequestsStatus.ACCEPTED);
        mockDetail.setDate(ZonedDateTime.now());

        ChannelSaleRequestDetail channelSaleRequestDetail = new ChannelSaleRequestDetail();
        channelSaleRequestDetail.setId(channelId);

        mockDetail.setChannel(channelSaleRequestDetail);

        ProductSaleRequestsDetailDTO expectedDTO = new ProductSaleRequestsDetailDTO();
        expectedDTO.setId(saleRequestId);
        expectedDTO.setStatus(ProductSaleRequestsStatus.ACCEPTED);
        expectedDTO.setDate(mockDetail.getDate());

        when(validationService.getAndCheckSaleRequest(saleRequestId)).thenReturn(mockDetail);
        when(channelsRepository.getChannel(channelId)).thenReturn(new ChannelResponse());
        try (MockedStatic<ProductSaleRequestConverter> converterMockedStatic = mockStatic(ProductSaleRequestConverter.class)) {
            converterMockedStatic.when(() ->
                    ProductSaleRequestConverter.toDTO(mockDetail, null, new ArrayList<>())
            ).thenReturn(expectedDTO);

            ProductSaleRequestsDetailDTO result = service.getSaleRequestDetail(saleRequestId);

            assertNotNull(result);
            assertEquals(expectedDTO.getId(), result.getId());
            assertEquals(expectedDTO.getStatus(), result.getStatus());
            assertEquals(expectedDTO.getDate(), result.getDate());
        }
    }

    @Test
    void testUpdateSaleRequest() {

        Long saleRequestId = 1L;
        ProductSaleRequestDetail detail = new ProductSaleRequestDetail();
        detail.setStatus(ProductSaleRequestsStatus.PENDING); // current status

        when(validationService.getAndCheckSaleRequest(saleRequestId)).thenReturn(detail);

        assertThrows(OneboxRestException.class, () ->
                service.updateSaleRequest(saleRequestId, ProductSaleRequestsStatus.PENDING)
        );
    }

    @Test
    void testSearchProductSaleRequests() {
        SearchProductSaleRequestFilterDTO filterDTO = new SearchProductSaleRequestFilterDTO();
        filterDTO.setEntityId(30L);
        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getUserOperatorId).thenReturn(8L);
            when(SecurityUtils.getUserEntityId()).thenReturn(20L);
            ProductSaleRequests productSaleRequests = new ProductSaleRequests();
            when(channelsRepository.searchProductSaleRequests(any())).thenReturn(productSaleRequests);

            ProductSaleRequestsResponseDTO result = service.searchProductSaleRequests(filterDTO);

            assertNotNull(result);
        }
    }

    @Test
    void testSearchNonOperatorWithEntityId() {
        SearchProductSaleRequestFilterDTO filterDTO = new SearchProductSaleRequestFilterDTO();
        filterDTO.setEntityId(10L);

        ProductSaleRequests productSaleRequests = new ProductSaleRequests();
        when(channelsRepository.searchProductSaleRequests(any())).thenReturn(productSaleRequests);
        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getUserOperatorId).thenReturn(8L);
            when(SecurityUtils.getUserEntityId()).thenReturn(20L);

            ProductSaleRequestsResponseDTO result = service.searchProductSaleRequests(filterDTO);

            assertNotNull(result);
        }
    }

    @Test
    void testIsValidProductStatusChange() {
        assertTrue(service.isValidProductStatusChange(ProductSaleRequestsStatus.ACCEPTED, ProductSaleRequestsStatus.REJECTED));
        assertTrue(service.isValidProductStatusChange(ProductSaleRequestsStatus.REJECTED, ProductSaleRequestsStatus.ACCEPTED));
        assertTrue(service.isValidProductStatusChange(ProductSaleRequestsStatus.PENDING, ProductSaleRequestsStatus.ACCEPTED));
        assertTrue(service.isValidProductStatusChange(ProductSaleRequestsStatus.ACCEPTED, ProductSaleRequestsStatus.REJECTED));
        assertTrue(service.isValidProductStatusChange(ProductSaleRequestsStatus.PENDING, ProductSaleRequestsStatus.REJECTED));

        assertFalse(service.isValidProductStatusChange(ProductSaleRequestsStatus.ACCEPTED, ProductSaleRequestsStatus.ACCEPTED));
        assertFalse(service.isValidProductStatusChange(ProductSaleRequestsStatus.REJECTED, ProductSaleRequestsStatus.REJECTED));
    }
}