package es.onebox.channels.catalog;

import es.onebox.common.config.context.AppContext;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.channel.dto.config.CustomDomain;
import es.onebox.common.datasources.ms.channel.dto.config.DomainSettings;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ChannelFeedsService {

    private static final String GENERIC_CATALOG_CONVERTER = "GenericCatalogConverter";
    private static final Long DEFAULT_LIMIT = 50L;
    private static final Long DEFAULT_OFFSET = 0L;

    private final ChannelCatalogService channelCatalogService;
    private final String env;

    @Autowired
    public ChannelFeedsService(ChannelCatalogService channelCatalogService, @Value("${onebox.environment}") String env) {
        this.channelCatalogService = channelCatalogService;
        this.env = env;
    }

    public ChannelCatalog getChannelFeeds(HttpServletRequest request) {
        var channelIdParam = request.getParameter("channel-id");
        var channelPathParam = request.getParameter("channel-path");


        var channelId = StringUtils.isNumeric(channelIdParam) ? Long.valueOf(channelIdParam) : null;
        var channelPath = StringUtils.isNotEmpty(channelPathParam) ? channelPathParam : null;

        if (channelId == null && channelPath == null) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        }

        var channelConfig = channelCatalogService.getChannelConfig(channelId, channelPath);
        String converter = channelConfig.getCustomEventConverter();

        if (converter != null && !AppContext.getApplicationContext().containsBean(converter)) {
            throw new RuntimeException(String.format("Converter not found: %s", converter));
        } else if (converter == null) {
            converter = GENERIC_CATALOG_CONVERTER;
        }

        final ChannelCatalogConverter<?> customCatalogConverter = (ChannelCatalogConverter<?>) AppContext.getApplicationContext().getBean(converter);
        Long limit = getLongParameter(request, customCatalogConverter.getLimitParameter(), DEFAULT_LIMIT);
        Long offset = getLongParameter(request, customCatalogConverter.getOffsetParameter(), DEFAULT_OFFSET);

        var context = buildContext(channelConfig, env);

        return customCatalogConverter.convert(context, limit, offset, request.getParameterMap());
    }

    private ChannelCatalogContext buildContext(ChannelConfigDTO channelConfig, String env) {
        var builder = ChannelCatalogContextBuilder.builder()
                .id(channelConfig.getId())
                .v4(channelConfig.getV4Enabled())
                .path(channelConfig.getUrl())
                .defaultLanguage(channelConfig.getDefaultLanguageCode())
                .env(env)
                .apiKey(channelConfig.getApiKey());
        DomainSettings domainSettings = channelConfig.getDomainSettings();
        if (domainSettings != null && BooleanUtils.isTrue(domainSettings.useCustomDomain())
                && CollectionUtils.isNotEmpty(domainSettings.domains())) {
            var defaultDomain = domainSettings.domains().stream().filter(CustomDomain::defaultDomain).findFirst().map(CustomDomain::domain).orElse(null);
            if (StringUtils.isNotBlank(defaultDomain)) {
                builder.customDomain(defaultDomain);
            }
        }
        return builder.build();
    }

    private static Long getLongParameter(HttpServletRequest request, String parameterName, Long defaultValue) {
        String value = request.getParameter(parameterName);
        if (StringUtils.isNumeric(value)) {
            return Long.parseLong(value);
        }
        return defaultValue;
    }

}
