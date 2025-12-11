package es.onebox.event.catalog.service;

import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.session.presaleconfig.PresaleConfig;
import es.onebox.event.datasources.integration.dispatcher.repository.IntDispatcherRepository;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.catalog.dto.presales.PresaleResolverType;
import net.snowflake.client.jdbc.internal.com.nimbusds.oauth2.sdk.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class SgaPresaleProviderResolver implements PresaleProviderResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(SgaPresaleProviderResolver.class);
    private final IntDispatcherRepository intDispatcherRepository;

    public SgaPresaleProviderResolver(IntDispatcherRepository intDispatcherRepository) {
        this.intDispatcherRepository = intDispatcherRepository;
    }

    @Override
    public PresaleResolverType getType() {
        return PresaleResolverType.SGA;
    }

    @Override
    public Map<Long, Provider> resolvePresaleProviders(Session session) {
        if (CollectionUtils.isEmpty(session.getPresales())) {
            return Collections.emptyMap();
        }

        Map<Long, Provider> presaleProviders = new HashMap<>();
        for (PresaleConfig presale : session.getPresales()) {
            try {
                String externalPresaleId = intDispatcherRepository.getExternalPresale(
                        session.getEntityId(),
                        session.getEventId(),
                        session.getSessionId(),
                        presale.getId().longValue()
                );
                if (externalPresaleId != null) {
                    presaleProviders.put(presale.getId().longValue(), Provider.SGA);
                }
            } catch (Exception e) {
                LOGGER.warn("Can not retrieve SGA external presale info", e);
            }
        }
        return presaleProviders;
    }
}
