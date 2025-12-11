package es.onebox.event.events.converter;

import es.onebox.event.events.dao.record.SaleGroupRecord;
import es.onebox.event.events.dto.SaleGroupDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SaleGroupsConverter {

    private SaleGroupsConverter() {

    }


    public static SaleGroupDTO fromEntity(SaleGroupRecord record, Long channelEventId) {
        if (record == null) {
            return null;
        }
        SaleGroupDTO saleGroupDTO = new SaleGroupDTO();
        saleGroupDTO.setId(record.getId());
        saleGroupDTO.setDescription(record.getDescription());
        saleGroupDTO.setTemplateId(record.getConfigId());
        saleGroupDTO.setTemplateName(record.getConfigName());
        saleGroupDTO.setSelected(channelEventId.equals(record.getChannelEventId()));
        return saleGroupDTO;
    }

    public static List<SaleGroupDTO> fromEntity(List<SaleGroupRecord> saleGroupRecords, Long channelEventId) {
        if (saleGroupRecords == null) {
            return new ArrayList<>();
        }
        return saleGroupRecords.stream()
                .map(r -> fromEntity(r, channelEventId))
                .collect(Collectors.toList());
    }

}
