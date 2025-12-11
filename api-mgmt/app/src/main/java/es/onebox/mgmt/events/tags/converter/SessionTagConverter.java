package es.onebox.mgmt.events.tags.converter;

import es.onebox.mgmt.datasources.ms.event.dto.tags.ChannelsSessionTags;
import es.onebox.mgmt.datasources.ms.event.dto.tags.ChannelsSessionTagsRequest;
import es.onebox.mgmt.datasources.ms.event.dto.tags.SessionTag;
import es.onebox.mgmt.datasources.ms.event.dto.tags.SessionTagLanguage;
import es.onebox.mgmt.datasources.ms.event.dto.tags.SessionTagRequest;
import es.onebox.mgmt.datasources.ms.event.dto.tags.SessionTagResponse;
import es.onebox.mgmt.datasources.ms.event.dto.tags.SessionTagsResponse;
import es.onebox.mgmt.events.tags.dto.ChannelSessionTagDTO;
import es.onebox.mgmt.events.tags.dto.ChannelsSessionTagsDTO;
import es.onebox.mgmt.events.tags.dto.ChannelsSessionTagsRequestDTO;
import es.onebox.mgmt.events.tags.dto.SessionTagDTO;
import es.onebox.mgmt.events.tags.dto.SessionTagLanguageDTO;
import es.onebox.mgmt.events.tags.dto.SessionTagRequestDTO;
import es.onebox.mgmt.events.tags.dto.SessionTagResponseDTO;
import es.onebox.mgmt.events.tags.dto.SessionTagsResponseDTO;

import java.util.List;


public class SessionTagConverter {
    private SessionTagConverter() {
    }

    public static SessionTagsResponseDTO toDTO(SessionTagsResponse in) {
        SessionTagsResponseDTO out = new SessionTagsResponseDTO();
        out.addAll(in.stream()
                .map(tag -> toDTO(tag))
                .toList());
        return out;
    }

    public static SessionTagRequest toMs(SessionTagRequestDTO in) {
        SessionTagRequest out = new SessionTagRequest();
        out.setEnabled(in.getEnabled());
        out.setBackgroundColor(in.getBackgroundColor());
        out.setTextColor(in.getTextColor());
        out.setChannels(mapToChannelsTagMs(in.getChannels()));
        out.setLanguages(mapToLanguageMs(in.getLanguages()));
        return out;
    }

    public static SessionTagResponseDTO toDTO(SessionTagResponse in) {
        SessionTagResponseDTO out = new SessionTagResponseDTO();
        out.setPosition(in.getPosition());
        return out;
    }

    private static SessionTagDTO toDTO(SessionTag in) {
        SessionTagDTO out = new SessionTagDTO();
        out.setPosition(in.getPosition());
        out.setEnabled(in.getEnabled());
        out.setBackgroundColor(in.getBackgroundColor());
        out.setTextColor(in.getTextColor());
        out.setChannels(mapToChannelTagDTO(in.getChannels()));
        out.setLanguages(mapToLanguagesDTO(in.getLanguages()));
        return out;
    }

    private static ChannelsSessionTagsDTO mapToChannelTagDTO(ChannelsSessionTags in) {
        ChannelsSessionTagsDTO out = new ChannelsSessionTagsDTO();
        out.setAllChannels(in.getAllChannels());
        if (out.getAllChannels()) {
            return out;
        }
        List<ChannelSessionTagDTO> list = in.getSelectedChannels().stream()
                .map(e -> {
                    ChannelSessionTagDTO channelSessionTagDTO = new ChannelSessionTagDTO();
                    channelSessionTagDTO.setId(e.getId());
                    channelSessionTagDTO.setName(e.getName());
                    return channelSessionTagDTO;
                })
                .toList();
        out.setSelectedChannels(list);
        return out;
    }

    private static ChannelsSessionTagsRequest mapToChannelsTagMs(ChannelsSessionTagsRequestDTO in) {
        ChannelsSessionTagsRequest out = new ChannelsSessionTagsRequest();
        out.setAllChannels(in.getAllChannels());
        if (!out.getAllChannels()) {
            out.setSelectedChannels(in.getSelectedChannels());
        }
        return out;
    }

    private static List<SessionTagLanguageDTO> mapToLanguagesDTO(List<SessionTagLanguage> in) {
        return in.stream()
                .map(e -> {
                    SessionTagLanguageDTO dto = new SessionTagLanguageDTO();
                    dto.setLanguage(e.getLanguage());
                    dto.setText(e.getText());
                    return dto;
                })
                .toList();
    }

    private static List<SessionTagLanguage> mapToLanguageMs(List<SessionTagLanguageDTO> in) {
        return in.stream()
                .map(e -> {
                    SessionTagLanguage dto = new SessionTagLanguage();
                    dto.setLanguage(e.getLanguage());
                    dto.setText(e.getText());
                    return dto;
                })
                .toList();
    }
}
