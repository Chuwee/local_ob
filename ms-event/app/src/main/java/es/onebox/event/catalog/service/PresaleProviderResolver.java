package es.onebox.event.catalog.service;

import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.catalog.dto.presales.PresaleResolverType;

import java.util.Map;

public interface PresaleProviderResolver {
    Map<Long, Provider> resolvePresaleProviders(Session session);
    PresaleResolverType getType();
}
