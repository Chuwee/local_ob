package es.onebox.event.events.postbookingquestions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.postbookingquestions.converter.PostBookingQuestionsConverter;
import es.onebox.event.events.postbookingquestions.dao.EventChannelPostBookingQuestionDao;
import es.onebox.event.events.postbookingquestions.dao.EventPostBookingQuestionDao;
import es.onebox.event.events.postbookingquestions.dao.PostBookingQuestionCouchDao;
import es.onebox.event.events.postbookingquestions.dao.PostBookingQuestionDao;
import es.onebox.event.events.postbookingquestions.dao.record.EventPostBookingQuestionRecord;
import es.onebox.event.events.postbookingquestions.domain.PostBookingQuestion;
import es.onebox.event.events.postbookingquestions.dto.ChoiceDTO;
import es.onebox.event.events.postbookingquestions.dto.EventChannelsPostBookingQuestionsDTO;
import es.onebox.event.events.postbookingquestions.dto.PostBookingQuestionDTO;
import es.onebox.event.events.postbookingquestions.dto.PostBookingQuestionsChannelsDTO;
import es.onebox.event.events.postbookingquestions.dto.PostBookingQuestionsDTO;
import es.onebox.event.events.postbookingquestions.dto.TranslationDTO;
import es.onebox.event.events.postbookingquestions.dto.UpdateEventPostBookingQuestionsDTO;
import es.onebox.event.events.postbookingquestions.dto.UpdatePostBookingQuestionDTO;
import es.onebox.event.events.postbookingquestions.dto.UpdatePostBookingQuestionsDTO;
import es.onebox.event.events.postbookingquestions.enums.EventChannelsPBQType;
import es.onebox.event.events.postbookingquestions.request.PostBookingQuestionsFilter;
import es.onebox.event.events.service.EventConfigService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.language.dao.LanguageDao;
import es.onebox.event.priceengine.request.ChannelSubtype;
import es.onebox.event.priceengine.request.EventChannelSearchFilter;
import es.onebox.event.priceengine.request.StatusRequestType;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.priceengine.simulation.record.EventChannelRecord;
import es.onebox.event.tags.utils.LanguageUtils;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPostBookingQuestionRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostBookingQuestionsService {

    private final PostBookingQuestionCouchDao postBookingQuestionCouchDao;
    private final PostBookingQuestionDao postBookingQuestionDao;
    private final EventChannelPostBookingQuestionDao eventChannelPostBookingQuestionDao;
    private final ChannelEventDao channelEventDao;
    private final EventPostBookingQuestionDao eventPostBookingQuestionDao;
    private final EventConfigService eventConfigService;
    private final EventDao eventDao;
    private final StaticDataContainer staticDataContainer;
    private final LanguageDao languageDao;

    @Autowired
    public PostBookingQuestionsService(PostBookingQuestionCouchDao postBookingQuestionCouchDao, PostBookingQuestionDao postBookingQuestionDao
            , EventChannelPostBookingQuestionDao eventChannelPostBookingQuestionDao, ChannelEventDao channelEventDao
            , EventPostBookingQuestionDao eventPostBookingQuestionDao, EventConfigService eventConfigService, EventDao eventDao
            , StaticDataContainer staticDataContainer, LanguageDao languageDao) {
        this.postBookingQuestionCouchDao = postBookingQuestionCouchDao;
        this.postBookingQuestionDao = postBookingQuestionDao;
        this.eventChannelPostBookingQuestionDao = eventChannelPostBookingQuestionDao;
        this.channelEventDao = channelEventDao;
        this.eventPostBookingQuestionDao = eventPostBookingQuestionDao;
        this.eventConfigService = eventConfigService;
        this.eventDao = eventDao;
        this.staticDataContainer = staticDataContainer;
        this.languageDao = languageDao;
    }

    @MySQLRead
    public PostBookingQuestionsDTO getPostBookingQuestions(PostBookingQuestionsFilter filter) {

        Map<String, Integer> postBookingQuestionRecords = PostBookingQuestionsConverter.convert(postBookingQuestionDao.getActivePostBookingQuestions());

        if (postBookingQuestionRecords.isEmpty()) {
            return new PostBookingQuestionsDTO();
        }

        List<PostBookingQuestion> postBookingQuestions = postBookingQuestionCouchDao.bulkGet(postBookingQuestionRecords.keySet().stream().toList());

        if (filter.getQ() != null) {
            postBookingQuestions = postBookingQuestions.stream()
                    .filter(p -> p.getName().toLowerCase().contains(filter.getQ().toLowerCase())).toList();
        }
        long total = postBookingQuestions.size();
        postBookingQuestions = paginate(postBookingQuestions, filter.getLimit(),  filter.getOffset());
        List<PostBookingQuestionDTO> postBookingQuestionDTOS = postBookingQuestions.stream()
                .map(p -> PostBookingQuestionsConverter.convertToPBQuestionDTO(p, postBookingQuestionRecords.get(p.getId()))).toList();

        return PostBookingQuestionsConverter.convert(postBookingQuestionDTOS, filter, total);
    }

    @MySQLWrite
    public void updatePostBookingQuestions(UpdatePostBookingQuestionsDTO request) {

        checkLanguageFormat(request.getPostBookingQuestions());
        checkQuestionIds(request.getPostBookingQuestions().stream().map(UpdatePostBookingQuestionDTO::getId).toList());
        List<PostBookingQuestion> postBookingQuestions = PostBookingQuestionsConverter.convertToList(request.getPostBookingQuestions());
        List<CpanelPostBookingQuestionRecord> postBookingQuestionRecords = PostBookingQuestionsConverter
                .toPostBookingQuestionRecord(request.getPostBookingQuestions());

        postBookingQuestionDao.changePostBookingQuestionsStatus();
        postBookingQuestionRecords.forEach(postBookingQuestionDao::upsert);
        eventPostBookingQuestionDao.deleteInactivePostBookingQuestions();
        postBookingQuestionCouchDao.bulkUpsert(postBookingQuestions);
    }

    @MySQLRead
    public EventChannelsPostBookingQuestionsDTO getEventPostBookingQuestions(Integer eventId) {

        if (BooleanUtils.isFalse(eventDao.existEventById(Long.valueOf(eventId)))) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_NOT_FOUND);
        }
        List<EventPostBookingQuestionRecord> eventPostBookingQuestionsRecords = eventPostBookingQuestionDao.getEventPostBookingQuestions(eventId);
        EventConfig eventConfig = eventConfigService.getEventConfig(Long.valueOf(eventId));
        Map<String, Integer> tupleIds = new HashMap<>();

        EventChannelsPBQType selectionType = EventChannelsPBQType.ALL;
        boolean enabled;

        if (eventConfig == null || eventConfig.getPostBookingQuestionsConfig() == null) {
            enabled = false;
        } else {
            enabled = eventConfig.getPostBookingQuestionsConfig().getEnabled();
            if (eventConfig.getPostBookingQuestionsConfig().getType() != null) {
                selectionType = eventConfig.getPostBookingQuestionsConfig().getType();
            }
        }

        List<PostBookingQuestion> postBookingQuestions = new ArrayList<>();
        if (!eventPostBookingQuestionsRecords.isEmpty()) {
            postBookingQuestions = postBookingQuestionCouchDao.bulkGet(eventPostBookingQuestionsRecords
                    .stream().map(EventPostBookingQuestionRecord::getIdExterno).distinct().toList());
        }
        eventPostBookingQuestionsRecords.forEach(record -> tupleIds.put(record.getIdExterno(), record.getIdPostBookingQuestion()));
        List<PostBookingQuestionDTO> postBookingQuestionDTOS = postBookingQuestions.stream()
                .map(p -> PostBookingQuestionsConverter.convertToPBQuestionDTO(p, tupleIds.get(p.getId()))).toList();

        List<Integer> channelIds = eventChannelPostBookingQuestionDao.getEventChannelsPostBookingQuestionsChannels(eventId);
        PostBookingQuestionsChannelsDTO postBookingQuestionsChannelsDTOS = new PostBookingQuestionsChannelsDTO(channelIds, selectionType);

        return new EventChannelsPostBookingQuestionsDTO(enabled, postBookingQuestionDTOS, postBookingQuestionsChannelsDTOS);
    }

    @MySQLWrite
    public void updateEventPostBookingQuestions(Integer eventId, UpdateEventPostBookingQuestionsDTO request) {

        if (BooleanUtils.isFalse(eventDao.existEventById(Long.valueOf(eventId)))) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_NOT_FOUND);
        }
        if (request.getChannels().getType().equals(EventChannelsPBQType.ALL) && request.getChannels().getIds() != null) {
            throw new OneboxRestException(MsEventErrorCode.BAD_REQUEST_PARAMETER);
        }

        Set<Integer> activeEventQuestions = postBookingQuestionDao.getActivePostBookingQuestions()
                .stream().map(CpanelPostBookingQuestionRecord::getIdpostbookingquestion).collect(Collectors.toSet());

        if (!activeEventQuestions.containsAll(request.getPostBookingQuestions())) {
            throw new OneboxRestException(MsEventErrorCode.QUESTION_NOT_FOUND);
        }
        eventPostBookingQuestionDao.deleteByEventId(eventId);
        eventPostBookingQuestionDao.insertBatch(PostBookingQuestionsConverter.toEventPostBookingQuestionRecord(eventId, request));

        if (request.getChannels().getType().equals(EventChannelsPBQType.LIST)){
            eventChannelPostBookingQuestionDao.deleteByEventId(eventId);

            Set<Integer> allChannelsObPortal = getAllEventPortalChannels(eventId);

            if (!allChannelsObPortal.containsAll(request.getChannels().getIds())) {
                throw new OneboxRestException(MsEventErrorCode.EVENT_CHANNEL_NOT_FOUND);
            }
            eventChannelPostBookingQuestionDao.insertBatch(PostBookingQuestionsConverter.toEventChannelPostBookingQuestionRecord(eventId, request));
        }

        eventConfigService.updateEventConfigPostBookingQuestions((long) eventId, request.getEnabled(), request.getChannels().getType());
    }

    private Set<Integer> getAllEventPortalChannels(Integer eventId) {

        EventChannelSearchFilter filter = new EventChannelSearchFilter();
        filter.setRequestStatus(EnumSet.of(StatusRequestType.PENDING, StatusRequestType.ACCEPTED, StatusRequestType.PENDING_REQUEST));
        filter.setSubtype(List.of(ChannelSubtype.PORTAL_WEB));

        return channelEventDao.findChannelEvents(Long.valueOf(eventId), filter)
                .stream().map(EventChannelRecord::getChannelId).map(Long::intValue).collect(Collectors.toSet());
    }

    public void deleteChannelPostBookingQuestionsRelation(Integer channelId) {
        eventChannelPostBookingQuestionDao.deleteByChannelId(channelId);
    }

    private List<PostBookingQuestion> paginate(List<PostBookingQuestion> postBookingQuestions, Long limit, Long offset) {

        if (CollectionUtils.isNotEmpty(postBookingQuestions)) {
            return postBookingQuestions.subList(
                    Math.min(postBookingQuestions.size(), offset.intValue()),
                    Math.min(postBookingQuestions.size(), limit.intValue() + offset.intValue())
            );
        }
        else {
            return new ArrayList<>();
        }
    }

    private void checkQuestionIds (List<String> request) {

        if (request.size() != new HashSet<>(request).size()) {
            throw new OneboxRestException(MsEventErrorCode.QUESTION_ID_ALREADY_EXISTS);
        }
    }

    private void checkLanguageFormat(List<UpdatePostBookingQuestionDTO> postBookingQuestions) {

        for (UpdatePostBookingQuestionDTO postBookingQuestion: postBookingQuestions) {
            List<String> languageCodes = languageDao.getAll().stream()
                    .map(CpanelIdiomaRecord::getCodigo)
                    .map(code -> code.replace('_', '-'))
                    .toList();
            if (postBookingQuestion.getLabel().getTranslations() != null) {
                isValidLocale(postBookingQuestion.getLabel().getTranslations().keySet(), languageCodes);
            }
            if (postBookingQuestion.getMessage().getTranslations() != null) {
                isValidLocale(postBookingQuestion.getMessage().getTranslations().keySet(), languageCodes);
            }
            if (postBookingQuestion.getChoices() != null) {
                for (ChoiceDTO choiceDTO: postBookingQuestion.getChoices()) {
                    if (choiceDTO.getLabel() != null && choiceDTO.getLabel().getTranslations() != null) {
                        isValidLocale(choiceDTO.getLabel().getTranslations().keySet(), languageCodes);
                    }
                    if (choiceDTO.getAdditionalFreeTextChoiceQuestion()!= null && choiceDTO.getAdditionalFreeTextChoiceQuestion().getTranslations() != null) {
                        isValidLocale(choiceDTO.getAdditionalFreeTextChoiceQuestion().getTranslations().keySet(), languageCodes);
                    }
                }
            }
        }
    }

    private void isValidLocale(Set<String> languages, List<String> languageCodes) {

        for (String language: languages) {
            Locale locale = Locale.forLanguageTag(language);
            //Language provided is "en" instead of "en-GB" or "en-US"
            if (locale.getCountry().isEmpty()) {
                boolean exists = languageCodes.stream().map(Locale::forLanguageTag)
                        .anyMatch(l -> l.getLanguage().equals(locale.getLanguage()));
                if (!exists) {
                    throw new OneboxRestException(MsEventErrorCode.LANGUAGE_NOT_AVAILABLE);
                }
            } else {
                if (staticDataContainer.getLanguageByCode(language.replace('-', '_')) == null){
                    throw new OneboxRestException(MsEventErrorCode.LANGUAGE_NOT_AVAILABLE);
                }
            }
        }
    }
}
