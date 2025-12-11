package es.onebox.event.catalog.service;

import es.onebox.event.catalog.dto.presales.PresaleResolverType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PresaleProviderResolverFactory {

    private final Map<PresaleResolverType, PresaleProviderResolver> resolversByProvider;

    public PresaleProviderResolverFactory(List<PresaleProviderResolver> resolverList) {
        this.resolversByProvider = resolverList.stream()
                .filter(resolver -> resolver.getType() != null)
                .collect(Collectors.toMap(PresaleProviderResolver::getType, Function.identity()));
    }

    public PresaleProviderResolver getPresaleResolver(PresaleResolverType provider) {
        if (provider == null) {
            return resolversByProvider.get(PresaleResolverType.DEFAULT);
        }
        return resolversByProvider.getOrDefault(provider, resolversByProvider.get(PresaleResolverType.DEFAULT));
    }
}
