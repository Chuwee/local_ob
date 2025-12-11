package es.onebox.mgmt.config;

import com.google.protobuf.util.JsonFormat;
import es.onebox.core.webmvc.configuration.ApiWebConfiguration;
import es.onebox.core.webmvc.converter.message.EntityHttpMessageConverter;
import es.onebox.mgmt.config.converter.ChannelBlockPathConverter;
import es.onebox.mgmt.config.converter.EntityBlockPathConverter;
import es.onebox.mgmt.config.converter.FormTypePathConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Configuration
public class WebConfiguration extends ApiWebConfiguration {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ChannelBlockPathConverter());
        registry.addConverter(new EntityBlockPathConverter());
        registry.addConverter(new FormTypePathConverter());
        super.addFormatters(registry);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new ProtobufJsonFormatHttpMessageConverter(null, JsonFormat.printer().preservingProtoFieldNames()));
        converters.add(EntityHttpMessageConverter.ofParsablesDates());
        converters.add(getByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter(StandardCharsets.ISO_8859_1));
    }

    private HttpMessageConverter<?> getByteArrayHttpMessageConverter() {
        ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(ProtobufHttpMessageConverter.PROTOBUF));
        return converter;
    }
}
