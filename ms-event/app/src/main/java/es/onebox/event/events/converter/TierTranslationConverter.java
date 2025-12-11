package es.onebox.event.events.converter;

import es.onebox.event.events.domain.TierConfig;
import es.onebox.event.events.domain.TierTranslation;
import es.onebox.event.events.dto.TierCommunicationElementDTO;
import es.onebox.event.events.enums.CommunicationElementType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TierTranslationConverter {

    public static void fromDTO(TierConfig tierConfig, TierCommunicationElementDTO[] communicationElements) {
        if (communicationElements == null) {
            return;
        }
        if (communicationElements.length == 0) {
            tierConfig.setTierTranslation(new TierTranslation());
            return;
        }
        if(tierConfig.getTierTranslation() == null){
            tierConfig.setTierTranslation(new TierTranslation());
        }
        for (TierCommunicationElementDTO commElement : communicationElements) {
            if(CommunicationElementType.NAME.equals(commElement.getCommunicationElementType())){
                if(tierConfig.getTierTranslation().getName() == null){
                    tierConfig.getTierTranslation().setName(new HashMap<>());
                }
                if(commElement.getValue() == null || commElement.getValue().isEmpty()){
                    tierConfig.getTierTranslation().getName().remove(commElement.getLang());
                }else{
                    tierConfig.getTierTranslation().getName().put(commElement.getLang(), commElement.getValue());
                }
            }else if(CommunicationElementType.DESCRIPTION.equals(commElement.getCommunicationElementType())){
                if(tierConfig.getTierTranslation().getDescription() == null){
                    tierConfig.getTierTranslation().setDescription(new HashMap<>());
                }
                if(commElement.getValue() == null || commElement.getValue().isEmpty()){
                    tierConfig.getTierTranslation().getDescription().remove(commElement.getLang());
                }else{
                    tierConfig.getTierTranslation().getDescription().put(commElement.getLang(), commElement.getValue());
                }

            }
        }
    }

    public static List<TierCommunicationElementDTO> toDTO(TierTranslation tierTranslation) {
        List<TierCommunicationElementDTO> result = new ArrayList<>();
        if (tierTranslation == null) {
            return result;
        }
        convertAndAddCommElems(tierTranslation.getDescription(), CommunicationElementType.DESCRIPTION, result);
        convertAndAddCommElems(tierTranslation.getName(), CommunicationElementType.NAME, result);
        return result;
    }

    private static void convertAndAddCommElems(Map<String, String> commElements,
                                               CommunicationElementType type,
                                               List<TierCommunicationElementDTO> list) {
        if (commElements == null || list == null) {
            return;
        }
        for (Map.Entry<String, String> entry : commElements.entrySet()) {
            list.add(new TierCommunicationElementDTO(entry.getKey(), entry.getValue(), type));
        }
    }


}
