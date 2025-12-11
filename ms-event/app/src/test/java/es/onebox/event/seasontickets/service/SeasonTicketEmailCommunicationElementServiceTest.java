package es.onebox.event.seasontickets.service;

import es.onebox.event.common.services.CommonEmailCommunicationElementService;
import es.onebox.event.communicationelements.enums.EmailCommunicationElementTagType;
import es.onebox.event.events.dto.EmailCommunicationElementDTO;
import es.onebox.event.events.request.EmailCommunicationElementFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class SeasonTicketEmailCommunicationElementServiceTest {

    @Mock
    private SeasonTicketService seasonTicketService;
    @Mock
    private CommonEmailCommunicationElementService commonEmailCommunicationElementService;
    @InjectMocks
    private SeasonTicketEmailCommunicationElementService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findCommunicationElementsTest() {
        Long seasonTicketId = 1L;
        EmailCommunicationElementFilter filter = new EmailCommunicationElementFilter();

        List<EmailCommunicationElementDTO> emailCommunicationElementDTOList = new ArrayList<>();
        Mockito.when(commonEmailCommunicationElementService.findCommunicationElements(Mockito.any(), Mockito.any())).thenReturn(emailCommunicationElementDTOList);

        List<EmailCommunicationElementDTO> result = service.findCommunicationElements(seasonTicketId, filter);
        Mockito.verify(commonEmailCommunicationElementService, times(1)).findCommunicationElements(any(), any());
    }

    @Test
    public void updateSeasonTicketCommunicationElementsTest() {
        Long seasonTicketId = 1L;
        EmailCommunicationElementDTO emailCommunicationElementDTO1 = new EmailCommunicationElementDTO();
        emailCommunicationElementDTO1.setValue("foo");
        emailCommunicationElementDTO1.setTag(EmailCommunicationElementTagType.CHANNEL_BANNER);
        emailCommunicationElementDTO1.setLanguage("es-ES");
        emailCommunicationElementDTO1.setId(1L);
        Set<EmailCommunicationElementDTO> elements = new HashSet<>();
        elements.add(emailCommunicationElementDTO1);

        service.updateSeasonTicketCommunicationElements(seasonTicketId, elements);

        Mockito.verify(seasonTicketService, times(1)).getAndCheckSeasonTicket(any());
        Mockito.verify(commonEmailCommunicationElementService, times(1)).updateEventCommunicationElements(any(), any());
    }

    @Test
    public void deleteCommunicationElementTest() {
        Long seasonTicketId = 1L;
        EmailCommunicationElementTagType tag = EmailCommunicationElementTagType.CHANNEL_BANNER;
        String language = "es-ES";

        service.deleteCommunicationElement(seasonTicketId, tag, language);

        Mockito.verify(seasonTicketService, times(1)).getAndCheckSeasonTicket(any());
        Mockito.verify(commonEmailCommunicationElementService, times(1)).deleteCommunicationElement(any(), any(), any());
    }
}
