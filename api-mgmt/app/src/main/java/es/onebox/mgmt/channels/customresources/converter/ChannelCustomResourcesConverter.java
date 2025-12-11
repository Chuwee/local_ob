package es.onebox.mgmt.channels.customresources.converter;

import es.onebox.mgmt.channels.customresources.dto.CSSCustomResourceDTO;
import es.onebox.mgmt.channels.customresources.dto.CustomResourcesDTO;
import es.onebox.mgmt.channels.customresources.dto.HTMLCustomResourceDTO;
import es.onebox.mgmt.channels.customresources.dto.UpdateCustomResourcesDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.CSSCustomResourceMsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.CustomResourcesMsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.HTMLCustomResourceMsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.UpdateCustomResourcesMsDTO;
import es.onebox.mgmt.datasources.ms.channel.enums.CustomResourceCSSType;
import es.onebox.mgmt.datasources.ms.channel.enums.CustomResourceHTMLType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelCustomResourcesConverter {

    private ChannelCustomResourcesConverter() {
    }

    public static CustomResourcesDTO fromMs(CustomResourcesMsDTO customResourcesMsDTO) {
        return new CustomResourcesDTO(
                fromMsHTML(customResourcesMsDTO.htmlResources()),
                fromMsCSS(customResourcesMsDTO.cssResources())
        );
    }

    private static List<HTMLCustomResourceDTO> fromMsHTML(List<HTMLCustomResourceMsDTO> htmlCustomResourceMsDTOs) {
        if (CollectionUtils.isNotEmpty(htmlCustomResourceMsDTOs)) {
            return htmlCustomResourceMsDTOs.stream().map(htmlCustomResourceDTO -> new HTMLCustomResourceDTO(
                    fromMs(htmlCustomResourceDTO.type()),
                    htmlCustomResourceDTO.language(),
                    htmlCustomResourceDTO.content())
            ).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private static List<CSSCustomResourceDTO> fromMsCSS(List<CSSCustomResourceMsDTO> cssCustomResourceMsDTOs) {
        if (CollectionUtils.isNotEmpty(cssCustomResourceMsDTOs)) {
            return cssCustomResourceMsDTOs.stream().map(cssCustomResourceDTO -> new CSSCustomResourceDTO(
                    fromMs(cssCustomResourceDTO.type()),
                    cssCustomResourceDTO.content())
            ).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private static es.onebox.mgmt.channels.customresources.enums.CustomResourceHTMLType fromMs(CustomResourceHTMLType type) {
        return switch (type) {
            case FOOTER_HTML -> es.onebox.mgmt.channels.customresources.enums.CustomResourceHTMLType.FOOTER_HTML;
            case HEADER_HTML -> es.onebox.mgmt.channels.customresources.enums.CustomResourceHTMLType.HEADER_HTML;
        };
    }

    private static es.onebox.mgmt.channels.customresources.enums.CustomResourceCSSType fromMs(CustomResourceCSSType type) {
        return switch (type) {
            case CUSTOM_STYLES_CSS ->
                    es.onebox.mgmt.channels.customresources.enums.CustomResourceCSSType.CUSTOM_STYLES_CSS;
        };
    }

    public static UpdateCustomResourcesMsDTO toMs(UpdateCustomResourcesDTO updateCustomResourcesDTO) {
        if (updateCustomResourcesDTO == null) {
            return null;
        } else {
            return new UpdateCustomResourcesMsDTO(
                    toMsHTML(updateCustomResourcesDTO.htmlResources()),
                    toMsCSS(updateCustomResourcesDTO.cssResources())
            );
        }
    }

    private static List<HTMLCustomResourceMsDTO> toMsHTML(List<HTMLCustomResourceDTO> htmlCustomResourceDTOs) {
        if (CollectionUtils.isNotEmpty(htmlCustomResourceDTOs)) {
            return htmlCustomResourceDTOs.stream().map(htmlCustomResourceDTO -> new HTMLCustomResourceMsDTO(
                    toMs(htmlCustomResourceDTO.type()),
                    htmlCustomResourceDTO.language(),
                    htmlCustomResourceDTO.content())
            ).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private static List<CSSCustomResourceMsDTO> toMsCSS(List<CSSCustomResourceDTO> cssCustomResourceDTOs) {
        if (CollectionUtils.isNotEmpty(cssCustomResourceDTOs)) {
            return cssCustomResourceDTOs.stream().map(cssCustomResourceDTO -> new CSSCustomResourceMsDTO(
                    toMs(cssCustomResourceDTO.type()),
                    cssCustomResourceDTO.content())
            ).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private static CustomResourceHTMLType toMs(es.onebox.mgmt.channels.customresources.enums.CustomResourceHTMLType type) {
        return switch (type) {
            case FOOTER_HTML -> CustomResourceHTMLType.FOOTER_HTML;
            case HEADER_HTML -> CustomResourceHTMLType.HEADER_HTML;
        };
    }

    private static CustomResourceCSSType toMs(es.onebox.mgmt.channels.customresources.enums.CustomResourceCSSType type) {
        return switch (type) {
            case CUSTOM_STYLES_CSS -> CustomResourceCSSType.CUSTOM_STYLES_CSS;
        };
    }
}
