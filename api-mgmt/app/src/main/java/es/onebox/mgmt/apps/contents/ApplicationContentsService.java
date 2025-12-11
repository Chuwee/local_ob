package es.onebox.mgmt.apps.contents;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.mgmt.apps.enums.AppNames;
import es.onebox.mgmt.channels.contents.converter.ChannelContentsConverter;
import es.onebox.mgmt.channels.contents.dto.ChannelLiteralsDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelLiterals;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelContentsRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationContentsService {

    private final ChannelContentsRepository channelContentsRepository;

    @Autowired
    public ApplicationContentsService(ChannelContentsRepository channelContentsRepository) {
        this.channelContentsRepository = channelContentsRepository;
    }

    public ChannelLiteralsDTO getChannelMasterLiterals(final String appName, final String languageCode, final String key) {
        if (AppNames.getApp(appName) == null) {
            throw ExceptionBuilder.build(ApiMgmtChannelsErrorCode.APPLICATION_UNSUPPORTED);
        }
        ChannelLiterals result = channelContentsRepository.getChannelMasterLiterals(appName, convertLanguage(languageCode), key);
        return ChannelContentsConverter.toDTO(result);
    }

    public void upsertChannelMasterLiterals(final String appName, final String languageCode, final ChannelLiteralsDTO body) {
        if (AppNames.getApp(appName) == null) {
            throw ExceptionBuilder.build(ApiMgmtChannelsErrorCode.APPLICATION_UNSUPPORTED);
        }
        ChannelLiterals out = ChannelContentsConverter.toDTO(body);
        channelContentsRepository.createOrUpdateChannelMasterLiterals(appName, convertLanguage(languageCode), out);
    }

    private static String convertLanguage(final String languageCode) {
        if (languageCode == null) {
            return null;
        }
        return ConverterUtils.toLocale(languageCode);
    }
}
