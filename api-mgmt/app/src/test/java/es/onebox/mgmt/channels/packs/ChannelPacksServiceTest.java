package es.onebox.mgmt.channels.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.dal.dto.couch.order.OrderPackDTO;
import es.onebox.dal.dto.couch.order.OrderProductDTO;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.packs.service.ChannelPacksService;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.Language;
import es.onebox.mgmt.datasources.ms.channel.enums.PackItemType;
import es.onebox.mgmt.datasources.ms.channel.enums.PackSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.PackType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.packs.Pack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackDetail;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItem;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionDate;
import es.onebox.mgmt.datasources.ms.event.dto.session.Sessions;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchRequest;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.mgmt.datasources.ms.order.repository.OrderProductsRepository;
import es.onebox.mgmt.packs.dto.BasePackItemDTO;
import es.onebox.mgmt.packs.dto.CreatePackDTO;
import es.onebox.mgmt.packs.dto.CreatePackItemDTO;
import es.onebox.mgmt.packs.dto.CreatePackItemsDTO;
import es.onebox.mgmt.packs.dto.PackDTO;
import es.onebox.mgmt.packs.dto.PackPeriodDTO;
import es.onebox.mgmt.packs.dto.UpdatePackDTO;
import es.onebox.mgmt.packs.enums.PackRangeType;
import es.onebox.mgmt.packs.enums.PackTypeDTO;
import es.onebox.mgmt.packs.helper.PacksHelper;
import es.onebox.mgmt.packs.service.PacksValidationService;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChannelPacksServiceTest {

    private static final Long CHANNEL_ID = 1L;
    private static final Long PACK_ID = 1L;
    private static final Long ITEM_ID = 1L;
    private static final Long PRODUCTS_LIMIT = 10L;

    @Mock
    private ChannelsHelper channelsHelper;

    @Mock
    private ChannelsRepository channelsRepository;

    @Mock
    private PacksHelper packsHelper;

    @Mock
    private PacksValidationService packsValidationService;

    @Mock
    private OrderProductsRepository orderProductsRepository;

    @InjectMocks
    private ChannelPacksService packsService;

    @BeforeEach
    public void initOpenApi() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getPacksTest() {
        validateChannel();
        Pack pack = buildPack();

        when(channelsRepository.getPacks(CHANNEL_ID)).thenReturn(List.of(pack));

        List<PackDTO> packDTOS = packsService.getPacks(CHANNEL_ID);
        assertNotNull(packDTOS);
        assertEquals(packDTOS.get(0).getId(), PACK_ID);
        verify(channelsHelper).getAndCheckChannel(CHANNEL_ID);
        verify(channelsRepository).getPacks(CHANNEL_ID);
    }

    @Test
    public void getPackTestWithSales() {
        validateChannel();
        PackDetail pack = buildPackDetail();
        ProductSearchResponse productSearchRsp = buildProductSearchResponse(PACK_ID, 1L);
        ProductSearchRequest filter = buildProductSearchReq(PACK_ID);

        when(channelsRepository.getPack(CHANNEL_ID, PACK_ID)).thenReturn(pack);
        when(orderProductsRepository.searchProducts(filter)).thenReturn(productSearchRsp);

        PackDTO packDTO = packsService.getPack(CHANNEL_ID, PACK_ID);
        assertNotNull(packDTO);
        assertEquals(packDTO.getId(), PACK_ID);
        assertEquals(packDTO.getHasSales(), true);
        verify(channelsHelper).getAndCheckChannel(CHANNEL_ID);
        verify(channelsRepository).getPack(CHANNEL_ID, PACK_ID);
    }

    @Test
    public void getPackTestWithoutSales() {
        validateChannel();
        PackDetail pack = buildPackDetail();
        ProductSearchResponse productSearchRsp = buildProductSearchResponse(PACK_ID, 0L);
        ProductSearchRequest filter = buildProductSearchReq(PACK_ID);

        when(channelsRepository.getPack(CHANNEL_ID, PACK_ID)).thenReturn(pack);
        when(orderProductsRepository.searchProducts(filter)).thenReturn(productSearchRsp);

        PackDTO packDTO = packsService.getPack(CHANNEL_ID, PACK_ID);
        assertNotNull(packDTO);
        assertEquals(packDTO.getId(), PACK_ID);
        assertEquals(packDTO.getHasSales(), false);
        verify(channelsHelper).getAndCheckChannel(CHANNEL_ID);
        verify(channelsRepository).getPack(CHANNEL_ID, PACK_ID);
    }

    @Test
    public void createPackTest() {
        validateChannel();
        CreatePackDTO createPackDTO = new CreatePackDTO();
        createPackDTO.setName(ObjectRandomizer.randomString());
        createPackDTO.setType(PackTypeDTO.MANUAL);
        Pack pack = buildPack();

        when(packsHelper.getAndCheckMainPackSessions(Mockito.eq(createPackDTO), any())).thenReturn(null);
        when(channelsRepository.createPack(Mockito.eq(CHANNEL_ID), any())).thenReturn(pack);

        PackDTO packDTO = packsService.createPack(CHANNEL_ID, createPackDTO);
        assertNotNull(packDTO);
        assertEquals(packDTO.getId(), PACK_ID);
        verify(channelsRepository).createPack(Mockito.eq(CHANNEL_ID), any());
    }

    @Test
    public void updatePackTest() {
        validateChannel();
        UpdatePackDTO updatePackDTO = new UpdatePackDTO();
        PackPeriodDTO packPeriodDTO = new PackPeriodDTO();
        packPeriodDTO.setType(PackRangeType.CUSTOM);
        packPeriodDTO.setStartDate(ZonedDateTime.now());
        packPeriodDTO.setEndDate(ZonedDateTime.now().plusMonths(1));
        updatePackDTO.setPackPeriod(packPeriodDTO);
        PackDetail pack = buildPackDetail();

        when(channelsRepository.getPack(CHANNEL_ID, PACK_ID)).thenReturn(pack);
        when(channelsRepository.getPackItems(CHANNEL_ID, PACK_ID)).thenReturn(null);
        doNothing().when(channelsRepository).updatePack(Mockito.eq(CHANNEL_ID), Mockito.eq(PACK_ID), any());
        doNothing().when(packsValidationService).validateUpdateChannelPack(any(), any(), any());

        packsService.updatePack(CHANNEL_ID, PACK_ID, updatePackDTO);
        verify(packsValidationService).validateUpdateChannelPack(CHANNEL_ID, PACK_ID, updatePackDTO);
        verify(channelsRepository).updatePack(Mockito.eq(CHANNEL_ID), Mockito.eq(PACK_ID), any());
    }

    @Test
    public void deletePackTest() {
        validateChannel();

        doNothing().when(channelsRepository).deletePack(CHANNEL_ID, PACK_ID);

        packsService.deletePack(CHANNEL_ID, PACK_ID);
        verify(channelsHelper).getAndCheckChannel(CHANNEL_ID);
        verify(channelsRepository).deletePack(CHANNEL_ID, PACK_ID);
    }

    @Test
    public void getPackItemsTest() {
        validateChannel();
        PackDetail pack = buildPackDetail();
        pack.setType(PackType.MANUAL);
        PackItem packItem = new PackItem();
        packItem.setPackItemId(ITEM_ID);
        packItem.setItemId(ITEM_ID);
        packItem.setType(PackItemType.SESSION);
        packItem.setMain(Boolean.TRUE);
        List<PackItem> items = List.of(packItem);
        Session session = new Session();
        session.setId(ITEM_ID);
        session.setDate(new SessionDate());
        Sessions sessions = new Sessions();
        sessions.setData(List.of(session));

        when(channelsRepository.getPackItems(CHANNEL_ID, PACK_ID)).thenReturn(items);
        when(channelsRepository.getPack(CHANNEL_ID, PACK_ID)).thenReturn(pack);
        when(packsHelper.getPackItemSessionMap(any())).thenReturn(Map.of(packItem.getPackItemId(), session));
        when(packsHelper.getPackItemPriceTypesMap(any(), any(), any(), any())).thenReturn(Map.of(packItem.getPackItemId(), List.of(new IdNameDTO())));

        List<BasePackItemDTO> itemsDTO = packsService.getPackItems(CHANNEL_ID, PACK_ID);
        assertNotNull(itemsDTO);
        assertEquals(itemsDTO.size(), 1);
        assertEquals(itemsDTO.get(0).getId(), ITEM_ID);
        verify(channelsHelper).getAndCheckChannel(CHANNEL_ID);
        verify(channelsRepository).getPackItems(CHANNEL_ID, PACK_ID);
    }

    @Test
    public void createPackItemsTest() {
        validateChannel();
        CreatePackItemsDTO itemsDTO = new CreatePackItemsDTO();
        CreatePackItemDTO createPackItemDTO = new CreatePackItemDTO();
        createPackItemDTO.setItemId(ITEM_ID);
        createPackItemDTO.setType(PackItemType.SESSION);
        itemsDTO.add(createPackItemDTO);

        when(packsHelper.getAndCheckPackSessions(Mockito.eq(List.of(ITEM_ID)), any())).thenReturn(null);
        when(packsHelper.getAndCheckPack(any(), any())).thenReturn(buildPackDetail());
        when(channelsRepository.getPackItems(CHANNEL_ID, PACK_ID)).thenReturn(null);
        doNothing().when(packsValidationService).validateCreatePackItems(any(), any(), any());
        doNothing().when(channelsRepository).createPackItems(Mockito.eq(CHANNEL_ID), Mockito.eq(PACK_ID), any());

        packsService.createPackItems(CHANNEL_ID, PACK_ID, itemsDTO);
        verify(packsValidationService).validateCreatePackItems(PackType.MANUAL, null, itemsDTO);
        verify(channelsRepository).createPackItems(Mockito.eq(CHANNEL_ID), Mockito.eq(PACK_ID), any());
    }

    @Test
    public void deletePackItemTest() {
        validateChannel();
        PackItem packItem = new PackItem();
        packItem.setPackItemId(ITEM_ID);
        packItem.setItemId(ITEM_ID);

        when(channelsRepository.getPackItems(CHANNEL_ID, PACK_ID)).thenReturn(List.of(packItem));
        doNothing().when(channelsRepository).deletePackItem(CHANNEL_ID, PACK_ID, ITEM_ID);

        packsService.deletePackItem(CHANNEL_ID, PACK_ID, ITEM_ID);
        verify(packsValidationService).validateDeletePackItem(CHANNEL_ID, PACK_ID, ITEM_ID);
        verify(channelsRepository).deletePackItem(CHANNEL_ID, PACK_ID, ITEM_ID);
    }

    private void validateChannel() {
        ChannelResponse channelResponse = ObjectRandomizer.random(ChannelResponse.class);
        Language language = new Language();
        language.setSelectedLanguages(List.of(1L));
        channelResponse.setLanguages(language);

        when(channelsHelper.getAndCheckChannel(CHANNEL_ID)).thenReturn(channelResponse);
    }

    private PackDetail buildPackDetail() {
        return buildPack(new PackDetail());
    }

    private Pack buildPack() {
        return buildPack(new Pack());
    }

    private <T extends Pack> T buildPack(T pack) {
        pack.setId(PACK_ID);
        pack.setSubtype(PackSubtype.CHANNEL);
        pack.setType(PackType.MANUAL);
        pack.setName("PACK NAME");
        pack.setChannelId(CHANNEL_ID);
        pack.setPackRangeType(es.onebox.mgmt.datasources.ms.channel.enums.PackRangeType.AUTOMATIC);

        return pack;
    }

    private ProductSearchResponse buildProductSearchResponse(Long packId, Long amountOfSales) {
        ProductSearchResponse resp = new ProductSearchResponse();
        List<OrderProductDTO> orderProductDTOList = new ArrayList<>();
        OrderProductDTO orderProductDTO = new OrderProductDTO();
        OrderPackDTO orderPackDTO = new OrderPackDTO();
        orderPackDTO.setId(packId);
        orderProductDTO.setPack(orderPackDTO);
        orderProductDTOList.add(orderProductDTO);
        resp.setData(orderProductDTOList);
        Metadata metadata = new Metadata();
        metadata.setTotal(amountOfSales);
        resp.setMetadata(metadata);
        return resp;
    }

    private ProductSearchRequest buildProductSearchReq(Long packId) {
        ProductSearchRequest filter = new ProductSearchRequest();
        filter.setPackIds(Collections.singletonList(packId));
        filter.setOffset(0L);
        filter.setLimit(PRODUCTS_LIMIT);
        return filter;
    }
}
