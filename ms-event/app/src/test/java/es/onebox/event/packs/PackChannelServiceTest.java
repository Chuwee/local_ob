package es.onebox.event.packs;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.dao.ChannelCurrenciesDao;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.EntityDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.exception.MsEventPackErrorCode;
import es.onebox.event.packs.dao.PackChannelDao;
import es.onebox.event.packs.dto.PackChannelDTO;
import es.onebox.event.packs.dto.PackChannelSearchFilter;
import es.onebox.event.packs.dto.PackChannelsDTO;
import es.onebox.event.packs.dto.PackDetailDTO;
import es.onebox.event.packs.dto.UpdatePackChannelDTO;
import es.onebox.event.packs.enums.PackChannelStatus;
import es.onebox.event.packs.record.PackChannelRecord;
import es.onebox.event.packs.service.PackChannelService;
import es.onebox.event.packs.service.PackService;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEntidadRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackCanalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PackChannelServiceTest {

    @Mock
    PackChannelDao packChannelDao;
    @Mock
    ChannelDao channelDao;
    @Mock
    EventDao eventDao;
    @Mock
    EntityDao entityDao;
    @Mock
    ChannelCurrenciesDao channelCurrenciesDao;
    @Mock
    PackService packService;

    @InjectMocks
    private PackChannelService service;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPackChannels_shouldReturnMappedList() {
        Long packId = 1L;

        PackChannelRecord r1 = new PackChannelRecord();
        r1.setChannelId(1L);
        PackChannelRecord r2 = new PackChannelRecord();
        r2.setChannelId(2L);
        when(packChannelDao.findPackChannels(eq(packId), any())).thenReturn(List.of(r1, r2));
        when(packChannelDao.countByFilter(eq(packId), any())).thenReturn(2L);

        PackChannelsDTO result = service.getPackChannels(packId, new PackChannelSearchFilter());

        assertNotNull(result);
        assertEquals(2, result.getMetadata().getTotal());
        assertEquals(2, result.getData().size());
        assertEquals(1, result.getData().get(0).getChannel().getId());
        assertEquals(2, result.getData().get(1).getChannel().getId());
    }

    @Test
    void getPackChannel_shouldReturnChannel() {
        Long packId = 1L;
        Long channelId = 2L;
        PackChannelRecord record = new PackChannelRecord();
        record.setPackId(packId);
        record.setChannelId(channelId);
        when(packChannelDao.getPackChannelDetailed(packId, channelId)).thenReturn(record);

        PackChannelDTO result = service.getPackChannel(packId, channelId);

        assertNotNull(result);
        assertEquals(packId, result.getPack().getId());
        assertEquals(channelId, result.getChannel().getId());
    }

    @Test
    void createPackChannel_shouldInsertNewRecord() {
        Long packId = 1L;
        Long channelId = 2L;
        Integer entityId = 3;
        Integer operatorId = 1;

        when(packChannelDao.getPackChannels(packId)).thenReturn(List.of());
        PackDetailDTO pack = new PackDetailDTO();
        pack.setEntityId(entityId.longValue());
        when(packService.getPackById(packId)).thenReturn(pack);
        ChannelInfo channel = new ChannelInfo(channelId, "", entityId.longValue(), ChannelSubtype.PORTAL_WEB.getIdSubtipo(), null);
        when(channelDao.getByIds(anyList())).thenReturn(List.of(channel));
        when(channelCurrenciesDao.getCurrenciesByChannelId(channelId)).thenReturn(Collections.emptyList());
        when(entityDao.getEntityInfo(eq(entityId))).thenReturn(new EntityDao.EntityInfo(entityId, 1, 1));
        CpanelEntidadRecord entity = new CpanelEntidadRecord();
        entity.setIdoperadora(operatorId);
        when(entityDao.getById(eq(operatorId))).thenReturn(entity);

        service.createPackChannels(packId, List.of(channelId));

        verify(packChannelDao).insert(any(CpanelPackCanalRecord.class));
    }

    @Test
    void createPackChannel_packChannelAlreadyExists() {
        Long packId = 1L;
        Integer entityId = 3;

        CpanelPackCanalRecord record = new CpanelPackCanalRecord();
        record.setIdcanal(1);
        when(packChannelDao.getPackChannels(packId)).thenReturn(List.of(record));

        PackDetailDTO pack = new PackDetailDTO();
        pack.setEntityId(entityId.longValue());
        when(packService.getPackById(packId)).thenReturn(pack);

        try {
            service.createPackChannels(packId, List.of(1L, 2L));
            fail();
        } catch (OneboxRestException e) {
            assertEquals(MsEventPackErrorCode.PACK_CHANNEL_EXISTS.getErrorCode(), e.getErrorCode());
        }

        verify(packChannelDao, times(0)).insert(any(CpanelPackCanalRecord.class));
    }

    @Test
    void requestChannelApproval_shouldUpdateToPending() {
        Long packId = 1L;
        Long channelId = 2L;
        Long userId = 3L;
        CpanelPackCanalRecord record = new CpanelPackCanalRecord();
        record.setEstado(PackChannelStatus.PENDING_REQUEST.getId());
        Integer entityId = 3;
        Integer operatorId = 1;

        when(packChannelDao.getPackChannel(packId, channelId)).thenReturn(Optional.of(record));
        PackDetailDTO pack = new PackDetailDTO();
        pack.setEntityId(entityId.longValue());
        when(packService.getPackById(packId)).thenReturn(pack);
        when(channelCurrenciesDao.getCurrenciesByChannelId(channelId)).thenReturn(Collections.emptyList());
        when(entityDao.getEntityInfo(eq(entityId))).thenReturn(new EntityDao.EntityInfo(entityId, 1, 1));
        CpanelEntidadRecord entity = new CpanelEntidadRecord();
        entity.setIdoperadora(operatorId);
        when(entityDao.getById(eq(operatorId))).thenReturn(entity);

        service.requestChannelApproval(packId, channelId, userId);

        assertEquals(PackChannelStatus.PENDING.getId(), record.getEstado());
        verify(packChannelDao).update(record);
    }


    @Test
    void requestChannelApproval_invalidCurrency() {
        Long packId = 1L;
        Long channelId = 2L;
        Long userId = 3L;
        CpanelPackCanalRecord record = new CpanelPackCanalRecord();
        record.setEstado(PackChannelStatus.PENDING_REQUEST.getId());
        Integer entityId = 3;
        Integer operatorId = 1;

        when(packChannelDao.getPackChannel(packId, channelId)).thenReturn(Optional.of(record));
        PackDetailDTO pack = new PackDetailDTO();
        pack.setId(packId);
        pack.setEntityId(entityId.longValue());
        when(packService.getPackById(packId)).thenReturn(pack);
        when(packService.getPackMainItemEventId(packId.intValue())).thenReturn(1L);
        when(channelCurrenciesDao.getCurrenciesByChannelId(channelId)).thenReturn(Collections.emptyList());
        when(entityDao.getEntityInfo(eq(entityId))).thenReturn(new EntityDao.EntityInfo(entityId, 1, 1));
        CpanelEntidadRecord entity = new CpanelEntidadRecord();
        entity.setIdoperadora(operatorId);
        entity.setUsemulticurrency((byte) 1);
        when(entityDao.getById(eq(operatorId))).thenReturn(entity);
        when(eventDao.getById(eq(operatorId))).thenReturn(new CpanelEventoRecord());
        CpanelCanalRecord canalRecord = new CpanelCanalRecord();
        canalRecord.setCurrency(1);
        when(channelDao.getById(channelId.intValue())).thenReturn(canalRecord);


        try {
            service.requestChannelApproval(packId, channelId, userId);
            fail();
        } catch (OneboxRestException e) {
            assertEquals(MsEventPackErrorCode.PACK_CHANNEL_CURRENCY_NOT_MATCH.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    void updatePackChannel(){
        UpdatePackChannelDTO updatePackChannelDTO = new UpdatePackChannelDTO();
        updatePackChannelDTO.setSuggested(true);
        updatePackChannelDTO.setOnSaleForLoggedUsers(true);

        CpanelPackCanalRecord cpanelPackCanalRecord = new CpanelPackCanalRecord();
        when(packChannelDao.getPackChannel(any(),any())).thenReturn(Optional.of(cpanelPackCanalRecord));

        service.updatePackChannel(1L,1L,updatePackChannelDTO);
        verify(packChannelDao, atLeastOnce()).update(any());
    }
}
