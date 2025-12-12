package es.onebox.internal.automaticsales.utils;

import es.onebox.common.datasources.distribution.dto.ItemWarning;
import es.onebox.common.datasources.distribution.dto.OrderResponse;
import es.onebox.common.datasources.distribution.dto.order.items.ItemAllocationType;
import es.onebox.common.datasources.distribution.dto.order.items.ItemSeatAllocation;
import es.onebox.common.datasources.distribution.dto.order.items.OrderItem;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.OrderTicketDataDTO;
import es.onebox.common.datasources.ms.order.dto.PreOrderDTO;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.internal.automaticsales.processsales.service.ProcessSalesService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

public class AdjacentSeatsTest {

    @Mock
    private MsOrderRepository msOrderRepository;

    @Mock
    private OrderResponse mockOrderResponse;

    @InjectMocks
    private ProcessSalesService processSalesService;

    @Before
    public void setUpMocks() {
        MockitoAnnotations.openMocks(this);
        
        List<OrderItem> mockItems = new ArrayList<>();
        OrderItem mockItem = mock(OrderItem.class);
        ItemSeatAllocation mockAllocation = mock(ItemSeatAllocation.class);
        
        when(mockItem.getItemWarnings()).thenReturn(List.of(ItemWarning.SESSION_NON_CONSECUTIVE_SEAT));
        when(mockItem.getAllocation()).thenReturn(mockAllocation);
        when(mockAllocation.getType()).thenReturn(ItemAllocationType.NUMBERED);
        
        mockItems.add(mockItem);
        when(mockOrderResponse.getItems()).thenReturn(mockItems);
    }

    @Test
    public void testValidateAdjacentSeats() {
        PreOrderDTO preOrder = new PreOrderDTO();
        List<OrderProductDTO> products = new ArrayList<>();
        products.add(createProduct(1, 4, "Sector1", "Zone1", 1));
        products.add(createProduct(2, 4, "Sector1", "Zone1", 1));
        products.add(createProduct(3, 4, "Sector1", "Zone1", 1));
        products.add(createProduct(4, 4, "Sector1", "Zone1", 1));
        preOrder.setProducts(products);

        when(msOrderRepository.getPreOrderInfo(any())).thenReturn(preOrder);
        Assert.assertTrue(processSalesService.validateAdjacentSeats(mockOrderResponse));
    }

    @Test
    public void testValidateNonAdjacentSeats() {
        PreOrderDTO preOrder = new PreOrderDTO();
        List<OrderProductDTO> products = new ArrayList<>();
        products.add(createProduct(4, 1,"Sector1", "Zone1", 1));
        products.add(createProduct(6, 1, "Sector1", "Zone1", 1));
        products.add(createProduct(7, 1, "Sector1", "Zone1", 1));
        products.add(createProduct(8, 1, "Sector1", "Zone1", 1));
        preOrder.setProducts(products);

        when(msOrderRepository.getPreOrderInfo(any())).thenReturn(preOrder);
        Assert.assertFalse(processSalesService.validateAdjacentSeats(mockOrderResponse));
    }

    @Test
    public void testValidateNonAdjacentSeatsByRow() {
        PreOrderDTO preOrder = new PreOrderDTO();
        List<OrderProductDTO> products = new ArrayList<>();
        products.add(createProduct(1, 4, "Sector1", "Zone1", 1));
        products.add(createProduct(2, 5, "Sector1", "Zone1", 1));
        products.add(createProduct(3, 4, "Sector1", "Zone1", 1));
        products.add(createProduct(4, 4, "Sector1", "Zone1", 1));
        preOrder.setProducts(products);

        when(msOrderRepository.getPreOrderInfo(any())).thenReturn(preOrder);
        Assert.assertFalse(processSalesService.validateAdjacentSeats(mockOrderResponse));
    }

    @Test
    public void testValidateAdjacentSeatsMultiSector() {
        PreOrderDTO preOrder = new PreOrderDTO();
        List<OrderProductDTO> products = new ArrayList<>();
        products.add(createProduct(1, 2, "Sector1", "Zone1", 1));
        products.add(createProduct(2, 2, "Sector1", "Zone1", 1));
        products.add(createProduct(10, 6, "Sector2", "Zone1", 1));
        products.add(createProduct(11, 6, "Sector2", "Zone1", 1));
        preOrder.setProducts(products);

        when(msOrderRepository.getPreOrderInfo(any())).thenReturn(preOrder);
        Assert.assertTrue(processSalesService.validateAdjacentSeats(mockOrderResponse));
    }

    @Test
    public void testValidateNonAdjacentSeatsMultiSector() {
        PreOrderDTO preOrder = new PreOrderDTO();
        List<OrderProductDTO> products = new ArrayList<>();
        products.add(createProduct(1, 2, "Sector1", "Zone1", 1));
        products.add(createProduct(2, 2, "Sector1", "Zone1", 1));
        products.add(createProduct(10, 6, "Sector2", "Zone1", 1));
        products.add(createProduct(11, 6, "Sector2", "Zone1", 1));
        products.add(createProduct(13, 6, "Sector2", "Zone1", 1));
        preOrder.setProducts(products);

        when(msOrderRepository.getPreOrderInfo(any())).thenReturn(preOrder);
        Assert.assertFalse(processSalesService.validateAdjacentSeats(mockOrderResponse));
    }

    @Test
    public void testValidateAdjacentSeatsMultiZone() {
        PreOrderDTO preOrder = new PreOrderDTO();
        List<OrderProductDTO> products = new ArrayList<>();
        products.add(createProduct(1, 1,"Sector1", "Zone1", 2));
        products.add(createProduct(2, 1,"Sector1", "Zone1", 2));
        products.add(createProduct(15, 1, "Sector1", "Zone2", 2));
        products.add(createProduct(14, 1, "Sector1", "Zone2", 2));
        preOrder.setProducts(products);

        when(msOrderRepository.getPreOrderInfo(any())).thenReturn(preOrder);
        Assert.assertTrue(processSalesService.validateAdjacentSeats(mockOrderResponse));
    }

    @Test
    public void testValidateNonAdjacentSeatsMultiZone() {
        PreOrderDTO preOrder = new PreOrderDTO();
        List<OrderProductDTO> products = new ArrayList<>();
        products.add(createProduct(1, 2, "Sector1", "Zone1", 2));
        products.add(createProduct(2, 8, "Sector1", "Zone1", 2));
        products.add(createProduct(15, 3, "Sector1", "Zone2", 2));
        products.add(createProduct(14, 2, "Sector1", "Zone2", 2));
        preOrder.setProducts(products);

        when(msOrderRepository.getPreOrderInfo(any())).thenReturn(preOrder);
        Assert.assertFalse(processSalesService.validateAdjacentSeats(mockOrderResponse));
    }

    @Test
    public void testValidateAdjacentSeatsNnZone() {
        PreOrderDTO preOrder = new PreOrderDTO();
        List<OrderProductDTO> products = new ArrayList<>();
        products.add(createNnProduct("Sector1", "Zone1", 1, 3));
        products.add(createNnProduct("Sector1", "Zone1", 1, 3));
        products.add(createNnProduct("Sector1", "Zone1", 1, 3));
        products.add(createNnProduct("Sector1", "Zone1", 1, 3));
        preOrder.setProducts(products);

        when(msOrderRepository.getPreOrderInfo(any())).thenReturn(preOrder);
        Assert.assertTrue(processSalesService.validateAdjacentSeats(mockOrderResponse));
    }

    @Test
    public void testValidateNonAdjacentSeatsNnZone() {
        PreOrderDTO preOrder = new PreOrderDTO();
        List<OrderProductDTO> products = new ArrayList<>();
        products.add(createNnProduct("Sector2", "Zone2", 2, 3));
        products.add(createNnProduct("Sector2", "Zone2", 2, 4));
        products.add(createNnProduct("Sector2", "Zone2", 2, 3));
        products.add(createNnProduct("Sector2", "Zone2", 2, 3));
        preOrder.setProducts(products);

        when(msOrderRepository.getPreOrderInfo(any())).thenReturn(preOrder);
        Assert.assertFalse(processSalesService.validateAdjacentSeats(mockOrderResponse));
    }

    private OrderProductDTO createProduct(Integer rowOrder, Integer rowId, String sectorName, String zoneName, Integer sessionId) {
        OrderProductDTO product = new OrderProductDTO();
        OrderTicketDataDTO ticketData = new OrderTicketDataDTO();
        
        ticketData.setRowOrder(rowOrder);
        ticketData.setSectorName(sectorName);
        ticketData.setPriceZoneName(zoneName);
        ticketData.setRowId(rowId);
        
        product.setTicketData(ticketData);
        product.setSessionId(sessionId);
        
        return product;
    }

    private OrderProductDTO createNnProduct(String sectorName, String zoneName, Integer sessionId, Integer nnAreaId) {
        OrderProductDTO product = createProduct(null, null, sectorName, zoneName, sessionId);
        product.getTicketData().setNotNumberedAreaId(nnAreaId);
        return product;
    }
}