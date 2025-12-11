package es.onebox.mgmt.channels.faqs.converter;

import es.onebox.mgmt.channels.faqs.dto.ChannelFAQDTO;
import es.onebox.mgmt.channels.faqs.dto.ChannelFAQUpsertRequestDTO;
import es.onebox.mgmt.channels.faqs.dto.ChannelFAQValueDTO;
import es.onebox.mgmt.channels.faqs.dto.ChannelFAQsDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.faqs.ChannelFAQ;
import es.onebox.mgmt.datasources.ms.channel.dto.faqs.ChannelFAQUpsertRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.faqs.ChannelFAQValue;
import es.onebox.mgmt.datasources.ms.channel.dto.faqs.ChannelFAQs;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChannelFAQsConverter {

    private ChannelFAQsConverter() {
    }

    public static ChannelFAQsDTO toDTO(ChannelFAQs in) {
        if (in == null) {
            return new ChannelFAQsDTO();
        }
        List<ChannelFAQDTO> out = in.stream().map(ChannelFAQsConverter::toDTO).filter(Objects::nonNull).toList();
        return new ChannelFAQsDTO(out);
    }

    public static ChannelFAQDTO toDTO(ChannelFAQ in) {
        if (in == null) {
            return null;
        }
        ChannelFAQDTO out = new ChannelFAQDTO();
        out.setKey(in.getKey());
        out.setTags(in.getTags());
        out.setValues(toValuesDTO(in.getValues()));
        return out;
    }

    private static Map<String, ChannelFAQValueDTO> toValuesDTO(Map<String, ChannelFAQValue> values) {
        Map<String, ChannelFAQValueDTO> out = new HashMap<>();
        if (MapUtils.isNotEmpty(values)) {
            values.forEach((k, v) -> out.put(ConverterUtils.toLanguageTag(k), toDTO(v)));
        }
        return out;
    }

    private static ChannelFAQValueDTO toDTO(ChannelFAQValue in) {
        ChannelFAQValueDTO out = new ChannelFAQValueDTO();
        out.setTitle(in.getTitle());
        out.setContent(in.getContent());
        return out;
    }

    public static ChannelFAQs toMs(ChannelFAQsDTO in) {
        if (in == null) {
            return null;
        }
        List<ChannelFAQ> out = in.stream().map(ChannelFAQsConverter::toMs).filter(Objects::nonNull).toList();
        return new ChannelFAQs(out);
    }

    public static ChannelFAQ toMs(ChannelFAQDTO in) {
        if (in == null) {
            return null;
        }
        ChannelFAQ out = new ChannelFAQ();
        out.setValues(toValuesMs(in.getValues()));
        out.setKey(in.getKey());
        out.setTags(in.getTags());
        return out;
    }

    private static Map<String, ChannelFAQValue> toValuesMs(Map<String, ChannelFAQValueDTO> values) {
        Map<String, ChannelFAQValue> out = new HashMap<>();
        if (MapUtils.isNotEmpty(values)) {
            values.forEach((k, v) -> out.put(ConverterUtils.toLocale(k), toMs(v)));
        }
        return out;
    }

    private static ChannelFAQValue toMs(ChannelFAQValueDTO in) {
        ChannelFAQValue out = new ChannelFAQValue();
        out.setTitle(in.getTitle());
        out.setContent(in.getContent());
        return out;
    }


    public static ChannelFAQUpsertRequest toMs(ChannelFAQUpsertRequestDTO faq) {
        ChannelFAQUpsertRequest out = new ChannelFAQUpsertRequest();
        out.setTags(faq.getTags());
        out.setValues(toValuesMs(faq.getValues()));
        return out;
    }
}
