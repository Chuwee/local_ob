package es.onebox.event.seasontickets.service;

import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.events.dto.EventLanguageDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SeasonTicketServiceHelper {

    List<EventLanguageDTO> generateDefaultLanguageList(EntityDTO entity, Map<Long, String> allLanguages) {
        List<EventLanguageDTO> eventLanguageDTOList = entity.getSelectedLanguages()
                .stream()
                .map(selectedLanguage -> {
                    EventLanguageDTO eventLanguageDTO = new EventLanguageDTO();
                    eventLanguageDTO.setId(selectedLanguage.getId());
                    eventLanguageDTO.setCode(allLanguages.get(selectedLanguage.getId()));

                    if(entity.getLanguage().getId().equals(selectedLanguage.getId())) {
                        eventLanguageDTO.setDefault(Boolean.TRUE);
                    } else {
                        eventLanguageDTO.setDefault(Boolean.FALSE);
                    }
                    return eventLanguageDTO;
                })
                .collect(Collectors.toList());

        // We check that entity default language is added to list. Otherwise we put it manually
        List<EventLanguageDTO> mainLanguageList = eventLanguageDTOList
                .stream()
                .filter(eventLanguage -> eventLanguage.getId().equals(entity.getLanguage().getId()))
                .collect(Collectors.toList());
        if(mainLanguageList.isEmpty()) {
            EventLanguageDTO eventLanguageDTO = new EventLanguageDTO();
            eventLanguageDTO.setId(entity.getLanguage().getId());
            eventLanguageDTO.setCode(allLanguages.get(entity.getLanguage().getId()));
            eventLanguageDTO.setDefault(Boolean.TRUE);
            eventLanguageDTOList.add(eventLanguageDTO);
        }

        return eventLanguageDTOList;
    }
}
