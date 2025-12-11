package es.onebox.mgmt.events.converter;

import es.onebox.mgmt.common.channelcontents.ChannelContentTextDTO;
import es.onebox.mgmt.common.channelcontents.TierChannelContentTextType;
import es.onebox.mgmt.datasources.common.enums.CommunicationElementType;
import es.onebox.mgmt.datasources.ms.event.dto.Tier;
import es.onebox.mgmt.datasources.ms.event.dto.TierCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.TierCondition;
import es.onebox.mgmt.datasources.ms.event.dto.TierExtended;
import es.onebox.mgmt.datasources.ms.event.dto.TierSalesGroupLimit;
import es.onebox.mgmt.datasources.ms.event.dto.Tiers;
import es.onebox.mgmt.events.dto.CreateEventTierRequestDTO;
import es.onebox.mgmt.events.dto.PriceTypeTierDTO;
import es.onebox.mgmt.events.dto.TierChannelContentsListDTO;
import es.onebox.mgmt.events.dto.TierDTO;
import es.onebox.mgmt.events.dto.TierExtendedDTO;
import es.onebox.mgmt.events.dto.TierQuotasLimitDTO;
import es.onebox.mgmt.events.dto.TiersDTO;
import es.onebox.mgmt.events.dto.UpdateTierRequestDTO;
import es.onebox.mgmt.events.enums.TierConditionDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TierConverter {

    private TierConverter() {
    }

    public static Tier toMsEvent(CreateEventTierRequestDTO source) {
        Tier target = new Tier();
        target.setName(source.getName());
        target.setPriceTypeId(source.getPriceTypeId());
        target.setStartDate(source.getStartDate());
        target.setPrice(source.getPrice());
        return target;
    }

    public static Tier toMsEvent(UpdateTierRequestDTO source) {
        Tier target = new Tier();
        target.setName(source.getName());
        target.setStartDate(source.getStartDate());
        target.setPrice(source.getPrice());
        target.setOnSale(source.getOnSale());
        target.setLimit(source.getLimit());
        if (source.getCondition() != null) {
            target.setCondition(TierCondition.getById(source.getCondition().getId()));
        }
        return target;
    }

    public static TierExtendedDTO fromMsEvent(TierExtended tier){
        if(tier == null){
            return null;
        }
        TierExtendedDTO dto = new TierExtendedDTO();
        convert(tier, dto);
        if(tier.getSalesGroupLimit() != null && !tier.getSalesGroupLimit().isEmpty()){
            dto.setQuotasLimit(tier.getSalesGroupLimit().stream()
                    .map(TierConverter::convert)
                    .collect(Collectors.toList()));
        }
        return dto;
    }


    public static TierDTO fromMsEvent(Tier tier) {
        if(tier == null){
            return null;
        }
        TierDTO dto = new TierDTO();
        convert(tier, dto);
        return dto;
    }

    public static List<TierDTO> fromMsEvent(List<Tier> tiers) {
        if (tiers == null || tiers.isEmpty()) {
            return new ArrayList<>();
        }
        return tiers.stream().map(TierConverter::fromMsEvent).collect(Collectors.toList());
    }

    public static TiersDTO fromMsEvent(Tiers tiers) {
        TiersDTO dto = new TiersDTO();
        if (tiers == null) {
            return dto;
        }
        dto.setData(fromMsEvent(tiers.getData()));
        dto.setMetadata(tiers.getMetadata());
        return dto;
    }


    public static List<TierCommunicationElement> toMsEvent(TierChannelContentsListDTO source) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }
        return source.stream()
                .map(TierConverter::toMsEvent)
                .collect(Collectors.toList());
    }

    public static TierChannelContentsListDTO convertTierCommElements(List<TierCommunicationElement> source) {
        TierChannelContentsListDTO target = new TierChannelContentsListDTO();
        if (source == null || source.isEmpty()) {
            return target;
        }

        target.addAll(source.stream()
                .map(TierConverter::fromMsEvent)
                .collect(Collectors.toList()));
        return target;
    }

    private static ChannelContentTextDTO<TierChannelContentTextType> fromMsEvent(TierCommunicationElement source) {
        if (source == null) {
            return null;
        }
        ChannelContentTextDTO<TierChannelContentTextType> target = new ChannelContentTextDTO<>();
        target.setLanguage(source.getLang());
        target.setValue(source.getValue());
        target.setType(TierChannelContentTextType.valueOf(source.getCommunicationElementType().name()));
        return target;
    }

    private static TierCommunicationElement toMsEvent(ChannelContentTextDTO<TierChannelContentTextType> source) {
        if (source == null) {
            return null;
        }
        TierCommunicationElement target = new TierCommunicationElement();
        target.setLang(source.getLanguage());
        target.setValue(source.getValue());
        target.setCommunicationElementType(CommunicationElementType.valueOf(source.getType().name()));
        return target;
    }


    private static void convert(Tier tier, TierDTO dto) {
        dto.setId(tier.getId());
        dto.setName(tier.getName());
        dto.setStartDate(tier.getStartDate());
        dto.setPrice(tier.getPrice());
        dto.setOnSale(tier.getOnSale());
        dto.setPriceType(new PriceTypeTierDTO(tier.getPriceTypeId(), tier.getPriceTypeName()));
        dto.setActive(tier.getActive());
        dto.setLimit(tier.getLimit());
        if (tier.getCondition() != null) {
            dto.setCondition(TierConditionDTO.getById(tier.getCondition().getId()));
        }
        dto.setOlsonId(tier.getOlsonId());
    }

    private static TierQuotasLimitDTO convert(TierSalesGroupLimit tierSalesGroupLimit){
        TierQuotasLimitDTO dto = new TierQuotasLimitDTO();
        dto.setId(tierSalesGroupLimit.getId());
        dto.setName(tierSalesGroupLimit.getName());
        dto.setLimit(tierSalesGroupLimit.getLimit());
        return dto;
    }


}
