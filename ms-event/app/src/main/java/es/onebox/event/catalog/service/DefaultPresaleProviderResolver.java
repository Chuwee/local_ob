package es.onebox.event.catalog.service;

import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.catalog.dto.presales.PresaleResolverType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class DefaultPresaleProviderResolver implements PresaleProviderResolver {

    @Override
    public PresaleResolverType getType() {
        return PresaleResolverType.DEFAULT;
    }

    @Override
    public Map<Long, Provider> resolvePresaleProviders(Session session) {
        return Collections.emptyMap();
    }
}
