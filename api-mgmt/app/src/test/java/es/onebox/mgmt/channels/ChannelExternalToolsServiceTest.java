package es.onebox.mgmt.channels;

import es.onebox.mgmt.channels.externaltools.ChannelExternalToolsService;
import es.onebox.mgmt.channels.externaltools.converter.ChannelExternalToolsConverter;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolDTO;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolFieldDTO;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolIdentifierDTO;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolsDTO;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolsNamesDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.externaltools.ChannelExternalTool;
import es.onebox.mgmt.datasources.ms.channel.dto.externaltools.ChannelExternalToolFieldIdentifier;
import es.onebox.mgmt.datasources.ms.channel.dto.externaltools.ChannelExternalTools;
import es.onebox.mgmt.datasources.ms.channel.dto.externaltools.ChannelExternalToolsNames;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ChannelExternalToolsServiceTest {

    @InjectMocks
    private ChannelExternalToolsService channelsService;

    @Mock
    private ChannelsRepository channelsRepository;

    @Mock
    private ChannelsHelper channelsHelper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getById() {
        Boolean enabled = true;
        ChannelExternalToolsNames name = ChannelExternalToolsNames.GTM;
        ChannelExternalToolFieldIdentifier fieldId = ChannelExternalToolFieldIdentifier.GTM_CONFIG;
        String channelValue = "value";
        Map<ChannelExternalToolFieldIdentifier, String> fields = new HashMap<>();
        fields.put(fieldId, channelValue);

        ChannelResponse channelResponse = new ChannelResponse();
        ChannelType channelType = ChannelType.MEMBER;
        channelResponse.setType(channelType);
        Mockito.when(channelsHelper.getAndCheckChannel(Mockito.anyLong())).thenReturn(channelResponse);

        ChannelExternalTool externalTool = new ChannelExternalTool();
        externalTool.setEnabled(enabled);
        externalTool.setName(name);
        externalTool.setAdditionalConfig(fields);

        ChannelExternalTools channelExternalTools = new ChannelExternalTools();
        channelExternalTools.add(externalTool);
        Mockito.when(channelsRepository.getChannelExternalTools(Mockito.anyLong())).thenReturn(channelExternalTools);

        ChannelExternalToolsDTO result = channelsService.getById(Mockito.anyLong());

        Assertions.assertEquals(enabled, result.get(0).getEnabled());
        Assertions.assertEquals(name.toString(), result.get(0).getName().toString());
        Assertions.assertEquals(fieldId.toString(), result.get(0).getAdditionalConfig().get(0).getId().toString());
        Assertions.assertEquals(channelValue, result.get(0).getAdditionalConfig().get(0).getValue());

    }

    @Test
    void updateById() {
        Boolean enabled = true;
        ChannelExternalToolFieldIdentifier fieldId = ChannelExternalToolFieldIdentifier.GTM_CONFIG;
        String channelValue = "value";
        Map<ChannelExternalToolFieldIdentifier, String> fields = new HashMap<>();
        fields.put(fieldId, channelValue);

        ChannelResponse channelResponse = new ChannelResponse();
        ChannelType channelType = ChannelType.MEMBER;
        channelResponse.setType(channelType);
        Mockito.when(channelsHelper.getAndCheckChannel(Mockito.anyLong())).thenReturn(channelResponse);

        ChannelExternalToolDTO request = new ChannelExternalToolDTO();
        request.setEnabled(enabled);
        ChannelExternalToolsNamesDTO namesDTO = ChannelExternalToolsNamesDTO.GTM;
        request.setName(namesDTO);
        ChannelExternalToolFieldDTO externalToolFieldDTO = new ChannelExternalToolFieldDTO();
        ChannelExternalToolIdentifierDTO id = ChannelExternalToolIdentifierDTO.GTM_CONFIG;
        externalToolFieldDTO.setId(id);
        externalToolFieldDTO.setValue(channelValue);

        List<ChannelExternalToolFieldDTO> fieldDTOS = new ArrayList<>();
        fieldDTOS.add(externalToolFieldDTO);
        request.setAdditionalConfig(fieldDTOS);
        ChannelExternalToolsNamesDTO toolName = ChannelExternalToolsNamesDTO.GTM;
        ChannelExternalTool msRequest = ChannelExternalToolsConverter.toMs(request, toolName);

        Assertions.assertEquals(enabled, msRequest.getEnabled());

        ChannelExternalToolFieldIdentifier key = msRequest.getAdditionalConfig().keySet().iterator().next();
        String valueResult = msRequest.getAdditionalConfig().get(key);
        Assertions.assertEquals(id.toString(), key.name());
        Assertions.assertEquals(channelValue, valueResult);

    }
}


