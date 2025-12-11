package es.onebox.mgmt.channels.notifications.converter;

import es.onebox.mgmt.channels.notifications.dto.ChannelEmailServerDTO;
import es.onebox.mgmt.channels.notifications.dto.ChannelEmailTestDTO;
import es.onebox.mgmt.channels.notifications.dto.EmailServerConfiguration;
import es.onebox.mgmt.channels.notifications.enums.EmailServerSecurityType;
import es.onebox.mgmt.channels.notifications.enums.EmailServerType;
import es.onebox.mgmt.datasources.ms.channel.dto.notifications.ChannelEmailServer;
import es.onebox.mgmt.datasources.ms.channel.dto.notifications.ChannelEmailServerConfiguration;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelEmailServerSecurityType;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelEmailServerType;
import es.onebox.mgmt.datasources.ms.delivery.dto.ChannelEmailTest;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class ChannelEmailServerConverter {

    private static final String OBFUSCATED_PASSWORD = "************";
    private static final String PASSWORD_NOT_CHANGED = "false";

    private ChannelEmailServerConverter(){
        throw new UnsupportedOperationException("Cannot instantiate utilities classs");
    }

    public static ChannelEmailServerDTO toDto(final ChannelEmailServer channelEmailServer){
        ChannelEmailServerDTO dto = new ChannelEmailServerDTO();
        dto.setType(EmailServerType.valueOf(channelEmailServer.getType().name()));
        dto.setConfiguration(buildConfiguration(channelEmailServer.getConfiguration()));
        return dto;
    }

    public static ChannelEmailTest toMs(final ChannelEmailTestDTO source, final ChannelEmailServer savedConfig) {
        ChannelEmailTest target = new ChannelEmailTest();
        target.setServer(source.getServer());
        target.setPort(source.getPort());
        target.setUser(source.getUser());
        if (StringUtils.isNotBlank(source.getPassword()) && source.getPassword().equals(OBFUSCATED_PASSWORD)
                && savedConfig != null && savedConfig.getConfiguration() != null) {
            target.setPassword(savedConfig.getConfiguration().getPassword());
        } else {
            target.setPassword(source.getPassword());
        }
        target.setSecurity(ChannelEmailServerSecurityType.valueOf(source.getSecurity().name()));
        target.setTargetEmail(source.getEmail());
        return target;
    }

    public static ChannelEmailTest toMs(final ChannelEmailServerConfiguration source) {
        ChannelEmailTest target = new ChannelEmailTest();
        target.setServer(source.getServer());
        target.setPort(source.getPort());
        target.setUser(source.getUser());
        target.setPassword(source.getPassword());
        target.setSecurity(ChannelEmailServerSecurityType.valueOf(source.getSecurity().name()));
        return target;
    }

    private static EmailServerConfiguration buildConfiguration(final ChannelEmailServerConfiguration config) {
        if(Objects.isNull(config)){
            return null;
        }

        EmailServerConfiguration dto = new EmailServerConfiguration();
        if(StringUtils.isNotBlank(config.getUser())){
            dto.setUser(config.getUser());
        }
        if(StringUtils.isNotBlank(config.getPassword())){
            dto.setPassword(OBFUSCATED_PASSWORD);
        }
        dto.setPort(config.getPort());
        dto.setRequireAuth(config.getRequireAuth());
        dto.setServer(config.getServer());
        if (config.getSecurity() != null) {
            dto.setSecurity(EmailServerSecurityType.valueOf(config.getSecurity().name()));
        }
        return dto;
    }


    public static ChannelEmailServer prepareUpdateRequest(final ChannelEmailServerDTO request,
                                                          final ChannelEmailServer currentConfig) {
        ChannelEmailServer obj = new ChannelEmailServer();
        obj.setType(ChannelEmailServerType.valueOf(request.getType().name()));
        if(EmailServerType.ONEBOX.equals(request.getType())){
            return obj;
        }
        obj.setConfiguration(buildConfiguration(request.getConfiguration(), currentConfig));
        return obj;
    }

    private static ChannelEmailServerConfiguration buildConfiguration(final EmailServerConfiguration newConfig,
                                                                      final ChannelEmailServer currentConfig) {
        ChannelEmailServerConfiguration obj = new ChannelEmailServerConfiguration();
        obj.setServer(newConfig.getServer());
        obj.setPort(newConfig.getPort());
        obj.setUser(newConfig.getUser());
        obj.setPassword(newConfig.getPassword());
        if (currentConfig != null && currentConfig.getConfiguration()!= null &&
                (ChannelEmailServerConverter.OBFUSCATED_PASSWORD.equals(newConfig.getPassword()) ||
                        ChannelEmailServerConverter.PASSWORD_NOT_CHANGED.equals(newConfig.getPassword()))) {
            obj.setPassword(currentConfig.getConfiguration().getPassword());
        }
        obj.setRequireAuth(newConfig.getRequireAuth());
        obj.setSecurity(ChannelEmailServerSecurityType.valueOf(newConfig.getSecurity().name()));
        return obj;
    }
}
