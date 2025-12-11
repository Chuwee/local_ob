package es.onebox.event.tags.converter;

import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.tags.domain.SessionTagCB;
import es.onebox.event.tags.domain.SessionTagLanguage;
import es.onebox.event.tags.domain.SessionTagsCB;
import es.onebox.event.tags.dto.ChannelSessionTagDTO;
import es.onebox.event.tags.dto.ChannelsSessionTagsDTO;
import es.onebox.event.tags.dto.SessionTagDTO;
import es.onebox.event.tags.dto.SessionTagLanguageDTO;
import es.onebox.event.tags.dto.SessionTagsResponseDTO;


import java.util.List;
import java.util.stream.Collectors;

public class SessionTagConverter {
    private SessionTagConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static SessionTagsResponseDTO toDTO(SessionTagsCB sessionTagsCB, List<ChannelSessionTagDTO> channelInfo) {
        SessionTagsResponseDTO out = new SessionTagsResponseDTO();
        if (sessionTagsCB == null || sessionTagsCB.getTags() == null) {
            return out;
        }
        out.addAll(sessionTagsCB.getTags().stream()
                .map(tag -> mapToDTO(tag, channelInfo))
                .toList());
        return out;
    }

    public static SessionTagDTO mapToDTO(SessionTagCB in, List<ChannelSessionTagDTO> channelInfo) {
        SessionTagDTO out = new SessionTagDTO();
        out.setPosition(in.getPosition());
        out.setEnabled(in.getEnabled());
        out.setBackgroundColor(in.getBackgroundColor());
        out.setTextColor(in.getTextColor());
        out.setChannels(mapChannels(in, channelInfo));
        out.setLanguages(mapToLanguagesDTO(in.getLanguages()));
        return out;
    }

    public static ChannelSessionTagDTO mapChannelSessionTag(ChannelInfo channelInfo) {
        ChannelSessionTagDTO channelSessionTagDTO = new ChannelSessionTagDTO();
        channelSessionTagDTO.setId(channelInfo.getId().longValue());
        channelSessionTagDTO.setName(channelInfo.getName());
        return channelSessionTagDTO;
    }

    public static List<SessionTagLanguage> mapToLanguageMs(List<SessionTagLanguageDTO> in) {
        return in.stream()
                .map(e -> {
                    SessionTagLanguage dto = new SessionTagLanguage();
                    dto.setLanguage(e.getLanguage());
                    dto.setText(e.getText());
                    return dto;
                })
                .toList();
    }

    private static ChannelsSessionTagsDTO mapChannels(SessionTagCB in, List<ChannelSessionTagDTO> channelInfo) {
        ChannelsSessionTagsDTO dto = new ChannelsSessionTagsDTO();
        dto.setAllChannels(in.getChannels().getAllChannels());
        if (!dto.getAllChannels()) {
            List<ChannelSessionTagDTO> list = channelInfo.stream()
                    .filter(tag -> in.getChannels().getSelectedChannels().contains(tag.getId()))
                    .map(tag -> {
                        ChannelSessionTagDTO channelSessionTagDTO = new ChannelSessionTagDTO();
                        channelSessionTagDTO.setId(tag.getId());
                        channelSessionTagDTO.setName(tag.getName());
                        return channelSessionTagDTO;
                    })
                    .collect(Collectors.toList());
            dto.setSelectedChannels(list);
        }
        return dto;
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
}
