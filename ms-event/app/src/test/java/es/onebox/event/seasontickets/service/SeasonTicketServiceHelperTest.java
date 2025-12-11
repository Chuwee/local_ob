package es.onebox.event.seasontickets.service;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.events.dto.EventLanguageDTO;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.hamcrest.Matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;

public class SeasonTicketServiceHelperTest {

    private SeasonTicketServiceHelper helper = new SeasonTicketServiceHelper();

    @Test
    public void generateListTest() {

        EntityDTO entity = createEntityDTO();
        Map<Long, String> allLanguages = createAllLanguages();

        List<EventLanguageDTO> result = helper.generateDefaultLanguageList(entity, allLanguages);

        List<EventLanguageDTO> defaultLanguagesList = result
                .stream()
                .filter(language -> language.getDefault().equals(Boolean.TRUE))
                .collect(Collectors.toList());

        List<EventLanguageDTO> noDefaultLanguagesList = result
                .stream()
                .filter(language -> language.getDefault().equals(Boolean.FALSE))
                .collect(Collectors.toList());

        assertThat(defaultLanguagesList, Matchers.hasItem(
                Matchers.allOf(
                        Matchers.hasProperty("id", Matchers.equalTo(2L)),
                        Matchers.hasProperty("code", Matchers.equalTo("language2"))
                )
        ));

        assertThat(noDefaultLanguagesList, Matchers.hasItems(
                Matchers.allOf(
                        Matchers.hasProperty("id", Matchers.equalTo(1L)),
                        Matchers.hasProperty("code", Matchers.equalTo("language1"))
                ),
                Matchers.allOf(
                        Matchers.hasProperty("id", Matchers.equalTo(3L)),
                        Matchers.hasProperty("code", Matchers.equalTo("language3"))
                )
        ));
    }

    @Test
    public void generateListFromEntityWithNoSelectedLanguagesTest() {

        EntityDTO entity = createEntityDTOWithNoSelectedLanguages();
        Map<Long, String> allLanguages = createAllLanguages();

        List<EventLanguageDTO> result = helper.generateDefaultLanguageList(entity, allLanguages);

        List<EventLanguageDTO> defaultLanguagesList = result
                .stream()
                .filter(language -> language.getDefault().equals(Boolean.TRUE))
                .collect(Collectors.toList());

        List<EventLanguageDTO> noDefaultLanguagesList = result
                .stream()
                .filter(language -> language.getDefault().equals(Boolean.FALSE))
                .collect(Collectors.toList());

        assertThat(defaultLanguagesList, Matchers.hasItem(
                Matchers.allOf(
                        Matchers.hasProperty("id", Matchers.equalTo(2L)),
                        Matchers.hasProperty("code", Matchers.equalTo("language2"))
                )
        ));

        assertTrue(noDefaultLanguagesList.isEmpty());
    }

    private Map<Long, String> createAllLanguages() {
        Map<Long, String> languages = new HashMap<>();
        languages.put(1L, "language1");
        languages.put(2L, "language2");
        languages.put(3L, "language3");
        languages.put(4L, "language4");

        return languages;
    }

    private EntityDTO createEntityDTO() {
        IdDTO language1 = new IdDTO();
        language1.setId(1L);

        IdDTO language2 = new IdDTO();
        language2.setId(2L);

        IdDTO language3 = new IdDTO();
        language3.setId(3L);

        List<IdDTO> selectedLanguages = new ArrayList<>();
        selectedLanguages.add(language1);
        selectedLanguages.add(language2);
        selectedLanguages.add(language3);

        EntityDTO entity = new EntityDTO();
        entity.setLanguage(language2);
        entity.setSelectedLanguages(selectedLanguages);

        return entity;
    }

    private EntityDTO createEntityDTOWithNoSelectedLanguages() {
        IdDTO language2 = new IdDTO();
        language2.setId(2L);

        List<IdDTO> selectedLanguages = new ArrayList<>();

        EntityDTO entity = new EntityDTO();
        entity.setLanguage(language2);
        entity.setSelectedLanguages(selectedLanguages);

        return entity;
    }
}
