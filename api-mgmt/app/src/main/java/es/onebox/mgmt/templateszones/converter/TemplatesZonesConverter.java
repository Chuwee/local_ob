package es.onebox.mgmt.templateszones.converter;

import es.onebox.mgmt.common.CommunicationElementTextDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.entity.TemplatesZonesResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.TemplateZones;
import es.onebox.mgmt.datasources.ms.entity.dto.TemplatesZonesRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.TemplatesZonesRequestFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.TemplatesZonesUpdateRequest;
import es.onebox.mgmt.templateszones.dto.TemplateZonesDTO;
import es.onebox.mgmt.templateszones.dto.TemplatesZonesRequestDTO;
import es.onebox.mgmt.templateszones.dto.TemplatesZonesRequestFilterDTO;
import es.onebox.mgmt.templateszones.dto.TemplatesZonesResponseDTO;
import es.onebox.mgmt.templateszones.dto.TemplatesZonesUpdateRequestDTO;
import es.onebox.mgmt.templateszones.enums.TemplatesZonesTagType;
import org.apache.commons.collections.CollectionUtils;


import java.util.List;

public class TemplatesZonesConverter {

    private TemplatesZonesConverter() {
    }

    public static TemplatesZonesUpdateRequest toRequest(TemplatesZonesUpdateRequestDTO in) {
        TemplatesZonesUpdateRequest out = new TemplatesZonesUpdateRequest();
        out.setName(in.getName());
        out.setStatus(in.getStatus());
        if (CollectionUtils.isNotEmpty(in.getContentsTexts())) {
            out.setContentsTexts(in.getContentsTexts());
        }
        out.setWhitelabelSettings(in.getWhitelabelSettings());
        return out;
    }

    public static TemplatesZonesRequest toEntity(TemplatesZonesRequestDTO in) {
        TemplatesZonesRequest out = new TemplatesZonesRequest();
        out.setName(in.getName());
        out.setCode(in.getCode());
        return out;
    }

    public static TemplatesZonesRequestFilter toFilter(TemplatesZonesRequestFilterDTO in) {
        TemplatesZonesRequestFilter out = new TemplatesZonesRequestFilter();
        out.setQ(in.getQ());
        out.setStatus(in.getStatus());
        out.setLimit(in.getLimit());
        out.setOffset(in.getOffset());
        return out;
    }

    public static TemplateZonesDTO<TemplatesZonesTagType> toDTO(TemplateZones in) {
        TemplateZonesDTO<TemplatesZonesTagType> out = new TemplateZonesDTO<>();
        out.setId(in.getId());
        out.setName(in.getName());
        out.setCode(in.getCode());
        out.setStatus(in.getStatus());
        if (CollectionUtils.isNotEmpty(in.getContentsTexts())) {
            List<CommunicationElementTextDTO<TemplatesZonesTagType>> texts = in.getContentsTexts().stream()
                    .map(TemplatesZonesConverter::getCommunicationElementTextDTO).toList();
            out.setContentsTexts(texts);
        }
        out.setWhitelabelSettings(in.getWhitelabelSettings());
        return out;
    }

    private static CommunicationElementTextDTO<TemplatesZonesTagType> getCommunicationElementTextDTO(CommunicationElementTextDTO<TemplatesZonesTagType> e) {
        CommunicationElementTextDTO<TemplatesZonesTagType> dto = new CommunicationElementTextDTO<>();
        dto.setLanguage(ConverterUtils.toLanguageTag(e.getLanguage()));
        dto.setType(e.getType());
        dto.setValue(e.getValue());
        return dto;
    }

    public static TemplatesZonesResponseDTO toDTO(TemplatesZonesResponse in) {
        TemplatesZonesResponseDTO out = new TemplatesZonesResponseDTO();
        out.setMetadata(in.getMetadata());
        out.setData(in.getData().stream().map(TemplatesZonesConverter::toDTO).toList());
        return out;
    }
}
