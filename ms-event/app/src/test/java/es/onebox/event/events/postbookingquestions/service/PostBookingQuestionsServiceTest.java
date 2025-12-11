package es.onebox.event.events.postbookingquestions.service;

import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.postbookingquestions.converter.PostBookingQuestionsConverter;
import es.onebox.event.events.postbookingquestions.dao.EventChannelPostBookingQuestionDao;
import es.onebox.event.events.postbookingquestions.dao.EventPostBookingQuestionDao;
import es.onebox.event.events.postbookingquestions.dao.PostBookingQuestionCouchDao;
import es.onebox.event.events.postbookingquestions.dao.PostBookingQuestionDao;
import es.onebox.event.events.postbookingquestions.domain.PostBookingQuestion;
import es.onebox.event.events.postbookingquestions.domain.Translation;
import es.onebox.event.events.postbookingquestions.dto.TranslationDTO;
import es.onebox.event.events.postbookingquestions.dto.UpdatePostBookingQuestionDTO;
import es.onebox.event.events.postbookingquestions.dto.UpdatePostBookingQuestionsDTO;
import es.onebox.event.events.service.EventConfigService;
import es.onebox.event.language.dao.LanguageDao;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.jooq.cpanel.tables.records.CpanelPostBookingQuestionRecord;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PostBookingQuestionsServiceTest {

    @Mock
    private PostBookingQuestionCouchDao postBookingQuestionCouchDao;

    @Mock
    private PostBookingQuestionDao postBookingQuestionDao;

    @Mock
    private EventChannelPostBookingQuestionDao eventChannelPostBookingQuestionDao;

    @Mock
    private ChannelEventDao channelEventDao;

    @Mock
    private EventPostBookingQuestionDao eventPostBookingQuestionDao;

    @Mock
    private EventConfigService eventConfigService;

    @Mock
    private EventDao eventDao;

    @Mock
    private LanguageDao languageDao;

    @Captor
    private ArgumentCaptor<CpanelPostBookingQuestionRecord> postBookingQuestionRecordCaptor;

    @Captor
    private ArgumentCaptor<List<PostBookingQuestion>> postBookingQuestionListCaptor;

    @InjectMocks
    private PostBookingQuestionsService postBookingQuestionsService;

    private static MockedStatic<PostBookingQuestionsConverter> postBookingQuestionsConverterMockedStatic;

    @BeforeAll
    public static void beforeAll() {
        postBookingQuestionsConverterMockedStatic = Mockito.mockStatic(PostBookingQuestionsConverter.class);
    }

    @AfterAll
    public static void afterAll() {
        postBookingQuestionsConverterMockedStatic.close();
    }

    @BeforeEach
    public void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void updatePostBookingQuestions_ok() {

        UpdatePostBookingQuestionsDTO request = getUpdatePostBookingQuestionsDTO();

        CpanelPostBookingQuestionRecord rec1 = new CpanelPostBookingQuestionRecord();
        rec1.setIdexterno("id1");
        CpanelPostBookingQuestionRecord rec2 = new CpanelPostBookingQuestionRecord();
        rec2.setIdexterno("id2");
        List<CpanelPostBookingQuestionRecord> recordList = List.of(rec1, rec2);

        List<PostBookingQuestion> couchList = getPostBookingQuestions();

        postBookingQuestionsConverterMockedStatic.when(() -> PostBookingQuestionsConverter.toPostBookingQuestionRecord(anyList()))
                .thenReturn(recordList);
        postBookingQuestionsConverterMockedStatic.when(() -> PostBookingQuestionsConverter.convertToList(anyList()))
                .thenReturn(couchList);

        postBookingQuestionsService.updatePostBookingQuestions(request);
        verify(postBookingQuestionDao).changePostBookingQuestionsStatus();
        verify(postBookingQuestionDao, times(2)).upsert(postBookingQuestionRecordCaptor.capture());

        assertEquals("id1", postBookingQuestionRecordCaptor.getAllValues().get(0).getIdexterno());
        assertEquals("id2", postBookingQuestionRecordCaptor.getAllValues().get(1).getIdexterno());

        verify(eventPostBookingQuestionDao).deleteInactivePostBookingQuestions();
        verify(postBookingQuestionCouchDao).bulkUpsert(postBookingQuestionListCaptor.capture());

        assertSame(couchList, postBookingQuestionListCaptor.getValue());
    }

    @NotNull
    private static List<PostBookingQuestion> getPostBookingQuestions() {
        Translation translation = new Translation();
        translation.setDefaultValue("Test");

        PostBookingQuestion pbq1 = new PostBookingQuestion();
        pbq1.setId("id1");
        pbq1.setLabel(translation);
        pbq1.setMessage(translation);
        PostBookingQuestion pbq2 = new PostBookingQuestion();
        pbq2.setId("id2");
        pbq2.setLabel(translation);
        pbq2.setMessage(translation);
        List<PostBookingQuestion> couchList = new ArrayList<>(List.of(pbq1, pbq2));
        return couchList;
    }

    @NotNull
    private static UpdatePostBookingQuestionsDTO getUpdatePostBookingQuestionsDTO() {

        TranslationDTO translationDTO = new TranslationDTO();
        translationDTO.setDefaultValue("Test");

        UpdatePostBookingQuestionDTO dto1 = new UpdatePostBookingQuestionDTO();
        dto1.setId("id1");
        dto1.setLabel(translationDTO);
        dto1.setMessage(translationDTO);

        UpdatePostBookingQuestionDTO dto2 = new UpdatePostBookingQuestionDTO();
        dto2.setId("id2");
        dto2.setLabel(translationDTO);
        dto2.setMessage(translationDTO);

        UpdatePostBookingQuestionsDTO request = new UpdatePostBookingQuestionsDTO();
        request.setPostBookingQuestions(List.of(dto1,dto2));

        return request;
    }
}
