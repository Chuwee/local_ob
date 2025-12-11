package es.onebox.mgmt.channels.externaltools.converter;

import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolDTO;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolFieldDTO;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolIdentifierDTO;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolsDTO;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolsNamesDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.externaltools.ChannelExternalTool;
import es.onebox.mgmt.datasources.ms.channel.dto.externaltools.ChannelExternalToolFieldIdentifier;
import es.onebox.mgmt.datasources.ms.channel.dto.externaltools.ChannelExternalTools;
import es.onebox.mgmt.datasources.ms.channel.dto.externaltools.ChannelExternalToolsNames;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChannelExternalToolsConverter {

    private ChannelExternalToolsConverter() {
    }

    public static ChannelExternalToolsDTO fromMs(ChannelExternalTools source) {
        return source
                .stream()
                .map(ChannelExternalToolsConverter::fromMs)
                .collect(Collectors.toCollection(ChannelExternalToolsDTO::new));
    }

    public static ChannelExternalToolDTO fromMs(ChannelExternalTool source) {
        if (source == null) {
            return null;
        }
        ChannelExternalToolDTO target = new ChannelExternalToolDTO();

        if (MapUtils.isNotEmpty(source.getAdditionalConfig())) {
            final List<ChannelExternalToolFieldDTO> additionalConfig = source.getAdditionalConfig().entrySet()
                    .stream()
                    .map(ChannelExternalToolsConverter::fromMs)
                    .collect(Collectors.toList());
            target.setAdditionalConfig(additionalConfig);
        }

        target.setEnabled(source.getEnabled() != null && source.getEnabled());
        target.setName(ChannelExternalToolsNamesDTO.valueOf(source.getName().name()));
        target.setSgtmFacebookCredentials(source.getSgtmFacebookCredentials());
        target.setSgtmGoogleCredentials(source.getSgtmGoogleCredentials());

        return target;
    }

    public static ChannelExternalToolFieldDTO fromMs(Map.Entry<ChannelExternalToolFieldIdentifier, String> source) {
        ChannelExternalToolFieldDTO target = new ChannelExternalToolFieldDTO();

        target.setId(ChannelExternalToolIdentifierDTO.getByCode(source.getKey().name()));
        target.setValue(source.getValue());

        return target;
    }

    public static ChannelExternalTool toMs(ChannelExternalToolDTO source, ChannelExternalToolsNamesDTO toolName) {
        ChannelExternalTool result = new ChannelExternalTool();
        result.setEnabled(source.getEnabled());
        result.setName(ChannelExternalToolsNames.valueOf(toolName.name()));
        if (source.getAdditionalConfig() != null) {
            Map<ChannelExternalToolFieldIdentifier, String> map = source.getAdditionalConfig()
                    .stream()
                    .collect(Collectors.toMap(c -> ChannelExternalToolFieldIdentifier.valueOf(c.getId().name()), ChannelExternalToolFieldDTO::getValue));
            result.setAdditionalConfig(map);
        }
        if (source.getSgtmFacebookCredentials() != null) {
            result.setSgtmFacebookCredentials(source.getSgtmFacebookCredentials());
        }
        if (source.getSgtmGoogleCredentials() != null) {
            result.setSgtmGoogleCredentials(source.getSgtmGoogleCredentials());
        }

        return result;
    }
}
