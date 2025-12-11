package es.onebox.mgmt.seasontickets.converter;

import es.onebox.core.utils.common.UrlBuilder;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.channels.utils.ChannelsUrlUtils;
import es.onebox.mgmt.common.BaseLinkDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.HashUtils;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelLinks;
import org.apache.commons.lang3.BooleanUtils;

import java.time.Instant;

public class SeasonTicketChannelContentsConverter {

    private static final String COMMUNICATION_PROTOCOL = "https://";
    private static final String URL_SEPARATOR = "/";
    private static final String ENTRADAS = "entradas";
    private static final String EVENTO = "evento";
    private static final String SESSION = "session";
    private static final String SELECT = "select";
    private static final String PREVIEW_TOKEN = "?previewToken=";

    public static SeasonTicketChannelLinks convert(ChannelLanguagesDTO languages, Session session, String urlPortal, String urlIntegration, Long seasonTicketId, Boolean v4Enabled) {
        SeasonTicketChannelLinks result = new SeasonTicketChannelLinks();
        if (session.getStatus().equals(SessionStatus.READY)) {
            result.setEnabled(session.getDate().getChannelPublication().absolute().toInstant().compareTo(Instant.now()) <= 0);
            result.setPublished(true);
        } else if (session.getStatus().equals(SessionStatus.PREVIEW)) {
            result.setEnabled(true);
            result.setPublished(false);
        } else {
            result.setEnabled(false);
            result.setPublished(false);
        }
        if (BooleanUtils.isTrue(v4Enabled)) {
            result.setLinks(languages.getSelectedLanguageCode().stream()
                    .map(language -> ChannelsUrlUtils.buildUrlByChannelsToBaseLink(urlPortal, urlIntegration, session.getId(), language)).toList());
        } else {
            result.setLinks(languages.getSelectedLanguageCode().stream()
                    .map(language -> convertToBaseLink(session, urlPortal, urlIntegration, language, seasonTicketId)).toList());
        }

        return result;
    }

    public static BaseLinkDTO convertToBaseLink(Session session, String urlPortal, String urlIntegration, String language, Long seasonTicketId) {
        BaseLinkDTO result = new BaseLinkDTO();
        result.setLanguage(language);

        String select = SELECT;
        if (SessionStatus.PREVIEW.equals(session.getStatus())) {
            String hash = HashUtils.encodeHashIds(session.getId());
            select = SELECT + PREVIEW_TOKEN + hash;
        }
        result.setLink(UrlBuilder.builder()
                .protocol(COMMUNICATION_PROTOCOL)
                .pathParts(urlPortal, urlIntegration, ConverterUtils.toLocale(language), ENTRADAS, EVENTO, seasonTicketId, SESSION, session.getId(), select)
                .separator(URL_SEPARATOR)
                .build());

        return result;
    }

}
