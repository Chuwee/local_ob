package es.onebox.mgmt.channels.faqs;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.faqs.dto.ChannelFAQUpsertRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.faqs.ChannelFAQ;
import es.onebox.mgmt.datasources.ms.channel.dto.faqs.ChannelFAQs;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelFAQsRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ChannelFAQsServiceTest {

    private static final Long CHANNEL_ID = ObjectRandomizer.randomLong();
    private static final String LANGUAGE = "en_US";
    private static final String FAQ_KEY = "sdf-dfgfd4-fgtfg-43wee";

    @Mock
    private ChannelsHelper channelsHelper;

    @Mock
    private ChannelFAQsRepository channelFAQsRepository;

    @InjectMocks
    private ChannelFAQsService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getChannelFAQs() {
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);
        ChannelFAQs channelFAQS = new ChannelFAQs();
        channelFAQS.add(new ChannelFAQ());
        channelFAQS.add(new ChannelFAQ());

        when(channelsHelper.getAndCheckChannel(CHANNEL_ID)).thenReturn(channelResponse);
        when(channelFAQsRepository.getChannelFAQs(eq(CHANNEL_ID), any(), any(), any())).thenReturn(channelFAQS);

        assertEquals(channelFAQS.size(), service.getChannelFAQs(CHANNEL_ID, LANGUAGE, null, null).size());

        verify(channelsHelper).validateLanguage(channelResponse, LANGUAGE);
    }

    @Test
    void getChannelFAQs_validatesChannelType() {
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.EXTERNAL);

        when(channelsHelper.getAndCheckChannel(CHANNEL_ID)).thenReturn(channelResponse);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> service.getChannelFAQs(CHANNEL_ID, LANGUAGE, null, null));
        assertEquals(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION.toString(), ex.getErrorCode());

        verifyNoInteractions(channelFAQsRepository);}

    @Test
    void addChannelFAQ() {
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);
        ChannelFAQUpsertRequestDTO channelFAQDTO = ObjectRandomizer.random(ChannelFAQUpsertRequestDTO.class);

        when(channelsHelper.getAndCheckChannel(CHANNEL_ID)).thenReturn(channelResponse);

        service.addChannelFAQ(CHANNEL_ID, channelFAQDTO);

        verify(channelFAQsRepository).addChannelFAQ(eq(CHANNEL_ID), any());
    }

    @Test
    void updateChannelFAQs() {
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);
        ChannelFAQUpsertRequestDTO channelFAQDTO = ObjectRandomizer.random(ChannelFAQUpsertRequestDTO.class);

        when(channelsHelper.getAndCheckChannel(CHANNEL_ID)).thenReturn(channelResponse);

        service.updateChannelFAQs(CHANNEL_ID, channelFAQDTO, FAQ_KEY);

        verify(channelFAQsRepository).updateChannelFAQs(eq(CHANNEL_ID), any(), any());
    }


}