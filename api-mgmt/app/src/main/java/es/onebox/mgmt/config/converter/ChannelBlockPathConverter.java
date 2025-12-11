package es.onebox.mgmt.config.converter;

import es.onebox.mgmt.channels.contents.enums.ChannelBlockCategory;
import org.springframework.core.convert.converter.Converter;

public class ChannelBlockPathConverter implements Converter<String, ChannelBlockCategory> {

    @Override
    public ChannelBlockCategory convert(String source) {
        return ChannelBlockCategory.fromPath(source);
    }

}
