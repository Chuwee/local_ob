package es.onebox.mgmt.packs.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.PackItemType;
import es.onebox.mgmt.datasources.ms.channel.enums.PackSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.PackType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.packs.Pack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackDetail;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus;
import es.onebox.mgmt.datasources.ms.event.repository.PacksRepository;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.mgmt.datasources.ms.order.repository.OrderProductsRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.packs.dto.CreatePackDTO;
import es.onebox.mgmt.packs.dto.CreatePackMainItemDTO;
import es.onebox.mgmt.packs.dto.channels.UpdatePackChannelDTO;
import es.onebox.mgmt.packs.enums.PackTypeDTO;
import es.onebox.mgmt.packs.helper.PacksHelper;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.sessions.dto.SessionSaleFlagStatus;
import es.onebox.mgmt.validation.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PacksServiceTest {

    @Mock
    private PacksHelper packsHelper;
    @Mock
    private PacksRepository packsRepository;
    @Mock
    private OrderProductsRepository orderProductsRepository;
    @Mock
    private ChannelsRepository channelsRepository;
    @Mock
    private SecurityManager securityManager;
    @Mock
    private ValidationService validationService;

    private PacksService packsService;
    private PacksValidationService packsValidationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        packsValidationService = new PacksValidationService(channelsRepository, null, null,
                packsRepository, null, validationService, securityManager, null, packsHelper);

        packsService = new PacksService(packsRepository, packsHelper, packsValidationService,
                orderProductsRepository, channelsRepository, securityManager);
    }

    @Test
    void createPack_createAutomaticPack_checkValidations() {
        long itemId = 1L;

        CreatePackDTO request = new CreatePackDTO();
        request.setName("Test");
        request.setEntityId(100L);
        request.setType(PackTypeDTO.AUTOMATIC);
        request.setMainItem(new CreatePackMainItemDTO());

        request.getMainItem().setItemId(itemId);
        request.getMainItem().setType(PackItemType.SESSION);
        Pack created = new Pack();
        created.setType(PackType.AUTOMATIC);
        created.setSubtype(PackSubtype.PROMOTER);
        when(packsRepository.createPack(any())).thenReturn(created);

        Session item = new Session();
        item.setStatus(SessionStatus.READY);
        item.setSale(SessionSaleFlagStatus.SALE);
        when(validationService.getAndCheckVisibilitySession(any())).thenReturn(item);

        packsService.createPack(request);

        verify(securityManager).checkEntityAccessible(100L);
        verify(packsRepository, times(1)).createPack(any());
    }

    @Test
    void createPack_shouldNotCreateManualPack() {
        CreatePackDTO request = new CreatePackDTO();
        request.setEntityId(100L);
        request.setType(PackTypeDTO.MANUAL);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> packsService.createPack(request));
        assertEquals(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER.getErrorCode(), ex.getErrorCode());

        verify(securityManager).checkEntityAccessible(100L);
        verify(packsRepository, times(0)).createPack(any());
    }

    @Test
    void deletePack_shouldThrowException_whenPackHasSales() {
        Long packId = 1L;
        PackDetail dto = new PackDetail();
        dto.setEntityId(123L);

        ProductSearchResponse response = new ProductSearchResponse();
        Metadata metadata = new Metadata();
        metadata.setTotal(5L); // With sales
        response.setMetadata(metadata);

        when(packsRepository.getPack(packId)).thenReturn(dto);
        when(orderProductsRepository.searchProducts(any())).thenReturn(response);

        doNothing().when(securityManager).checkEntityAccessible(anyLong());

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> packsService.deletePack(packId));
        assertEquals(ApiMgmtChannelsErrorCode.PACK_HAS_SALES.getErrorCode(), ex.getErrorCode());

        verify(packsRepository, times(0)).deletePack(packId);
    }

    @Test
    void deletePack_shouldDelete_whenNoSales() {
        Long packId = 2L;
        PackDetail dto = new PackDetail();
        dto.setEntityId(123L);

        ProductSearchResponse response = new ProductSearchResponse();
        Metadata metadata = new Metadata();
        metadata.setTotal(0L); // No sales
        response.setMetadata(metadata);

        when(packsRepository.getPack(packId)).thenReturn(dto);
        when(orderProductsRepository.searchProducts(any())).thenReturn(response);

        doNothing().when(securityManager).checkEntityAccessible(anyLong());

        packsService.deletePack(packId);

        verify(packsRepository, times(1)).deletePack(packId);
    }

    @Test
    void deletePackChannel_shouldThrowException_whenPackHasSales() {
        Long packId = 1L;
        Long channelId = 10L;

        PackDetail dto = new PackDetail();
        dto.setEntityId(123L);
        when(packsHelper.getAndCheckPack(packId)).thenReturn(dto);

        when(channelsRepository.getChannel(eq(channelId))).thenReturn(new ChannelResponse());

        ProductSearchResponse response = new ProductSearchResponse();
        Metadata metadata = new Metadata();
        metadata.setTotal(1000L); // With sales
        response.setMetadata(metadata);
        when(orderProductsRepository.searchProducts(any())).thenReturn(response);

        doNothing().when(securityManager).checkEntityAccessible(anyLong());

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> packsService.deletePackChannel(packId, channelId));
        assertEquals(ApiMgmtChannelsErrorCode.PACK_HAS_SALES.getErrorCode(), ex.getErrorCode());

        verify(packsRepository, times(0)).deletePackChannel(packId, channelId);
    }

    @Test
    void deletePackChannel_shouldDelete_whenNoSales() {
        Long packId = 1L;
        Long channelId = 10L;

        PackDetail dto = new PackDetail();
        dto.setEntityId(123L);
        when(packsHelper.getAndCheckPack(packId)).thenReturn(dto);

        when(channelsRepository.getChannel(eq(channelId))).thenReturn(new ChannelResponse());

        ProductSearchResponse response = new ProductSearchResponse();
        Metadata metadata = new Metadata();
        metadata.setTotal(0L); // No sales
        response.setMetadata(metadata);
        when(orderProductsRepository.searchProducts(any())).thenReturn(response);

        doNothing().when(securityManager).checkEntityAccessible(anyLong());

        packsService.deletePackChannel(packId, channelId);

        verify(packsRepository, times(1)).deletePackChannel(packId, channelId);
    }

    @Test
    void updatePackChannel() {
        Long packId = 1L;
        Long channelId = 10L;
        PackDetail dto = new PackDetail();
        dto.setEntityId(123L);

        when(packsHelper.getAndCheckPack(packId)).thenReturn(dto);
        doNothing().when(packsRepository).updatePackChannel(anyLong(), anyLong(), any());
        packsService.updatePackChannel(packId, channelId, new UpdatePackChannelDTO());
        verify(packsRepository, atLeastOnce()).updatePackChannel(anyLong(), anyLong(), any());
    }
}
