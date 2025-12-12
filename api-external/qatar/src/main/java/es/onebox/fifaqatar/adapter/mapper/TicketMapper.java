package es.onebox.fifaqatar.adapter.mapper;

import es.onebox.common.datasources.ms.event.dto.response.catalog.event.EventCatalog;
import es.onebox.common.datasources.ms.event.dto.response.catalog.session.SessionCatalog;
import es.onebox.common.datasources.ms.event.dto.response.session.passbook.SessionPassbookCommElement;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public interface TicketMapper {

    default String buildSessionPassbookUrl(String lang, List<SessionPassbookCommElement> sessionPassbookCommElements) {
        lang = lang != null ? lang.replaceAll("-", "_") : lang;
        if(CollectionUtils.isEmpty(sessionPassbookCommElements)) {
            return null;
        }
        String finalLang = lang;
        List<SessionPassbookCommElement> collect = sessionPassbookCommElements.stream().filter(element -> element.getLanguage().equals(finalLang) && "STRIP".equals(element.getTag())).collect(Collectors.toList());

        return CollectionUtils.isNotEmpty(collect) ? collect.get(0).getValue() : null;
    }

    default String buildSessionImageUrl(String elementTag, SessionCatalog sessionCatalog, String lang, String eventDefaultLanguage) {
        var value = MapperUtils.findCommElement(elementTag, sessionCatalog.getCommunicationElements(), lang, eventDefaultLanguage);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return null;
    }

    default String buildEventImageUrl(String elementTag, EventCatalog eventCatalog, String lang, String eventDefaultLanguage) {
        var value = MapperUtils.findCommElement(elementTag, eventCatalog.getCommunicationElements(), lang, eventDefaultLanguage);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return null;
    }

    default String buildSessionName(String elementTag, SessionCatalog sessionCatalog, String lang, String eventDefaultLanguage) {
        var value = MapperUtils.findCommElement(elementTag, sessionCatalog.getCommunicationElements(), lang, eventDefaultLanguage);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return null;
    }

    default String buildEventName(String elementTag, EventCatalog eventCatalog, String lang, String eventDefaultLanguage) {
        var value = MapperUtils.findCommElement(elementTag, eventCatalog.getCommunicationElements(), lang, eventDefaultLanguage);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return null;
    }

}
