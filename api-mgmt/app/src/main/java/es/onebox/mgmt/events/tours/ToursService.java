package es.onebox.mgmt.events.tours;

import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.BaseChannelContentImageType;
import es.onebox.mgmt.common.channelcontents.ChannelContentConverter;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageFilter;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.channelcontents.TourChannelContentImageType;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTagType;
import es.onebox.mgmt.datasources.ms.event.dto.tour.Tour;
import es.onebox.mgmt.datasources.ms.event.dto.tour.Tours;
import es.onebox.mgmt.datasources.ms.event.repository.ToursRepository;
import es.onebox.mgmt.events.converter.TourConverter;
import es.onebox.mgmt.events.dto.EventChannelContentTextFilter;
import es.onebox.mgmt.events.dto.SearchToursResponse;
import es.onebox.mgmt.events.dto.channel.EventChannelContentTextType;
import es.onebox.mgmt.events.tours.dto.BaseTourDTO;
import es.onebox.mgmt.events.tours.dto.TourDTO;
import es.onebox.mgmt.events.tours.dto.TourEventFilterDTO;
import es.onebox.mgmt.events.tours.dto.TourFilter;
import es.onebox.mgmt.events.tours.dto.TourSearchFilter;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ToursService {

    @Autowired
    private ToursRepository toursRepository;
    @Autowired
    private EntitiesRepository entitiesRepository;
    @Autowired
    private SecurityManager securityManager;
    @Autowired
    private MasterdataService masterdataService;

    public TourDTO get(Long tourId, TourEventFilterDTO filter) {
        Tour tour = getAndCheckTour(tourId, filter);

        return TourConverter.fromMsEvent(tour);
    }

    public SearchToursResponse search(TourSearchFilter filter) {
        securityManager.checkEntityAccessible(filter);

        TourFilter tourFilter = ToursConverter.toMs(SecurityUtils.getUserOperatorId(), filter);
        Tours tours = toursRepository.getTours(tourFilter);

        SearchToursResponse response = new SearchToursResponse();
        response.setData(tours.getData().stream().map(TourConverter::fromMsEventToBase).collect(Collectors.toList()));
        response.setMetadata(tours.getMetadata());

        return response;
    }

    public Long create(String tourName, Long entityId) {
        securityManager.checkEntityAccessible(entityId);

        return toursRepository.createTour(tourName, entityId);
    }

    public void update(Long tourId, BaseTourDTO tourDTO) {
        getAndCheckTour(tourId, new TourEventFilterDTO());

        toursRepository.updateTour(tourId, tourDTO.getName(), tourDTO.getStatus());
    }

    public void delete(Long tourId) {
        getAndCheckTour(tourId, new TourEventFilterDTO());

        toursRepository.deleteTour(tourId);
    }

    public ChannelContentTextListDTO<EventChannelContentTextType> getChannelContentTexts(Long tourId, EventChannelContentTextFilter filter) {
        getAndCheckTour(tourId, new TourEventFilterDTO());

        CommunicationElementFilter communicationElementFilter
                = ChannelContentConverter.fromEventFilter(filter, masterdataService);

        List<EventCommunicationElement> comElements = toursRepository.getTourCommunicationElements(tourId, communicationElementFilter,
                EventTagType::isText);

        comElements.sort(Comparator.comparing(EventCommunicationElement::getLanguage).
                thenComparing(EventCommunicationElement::getTagId));

        return ChannelContentConverter.fromMsEventText(comElements);
    }

    public void updateChannelContentTexts(Long tourId, List<ChannelContentTextDTO<EventChannelContentTextType>> texts) {
        Tour tour = getAndCheckTour(tourId, new TourEventFilterDTO());

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Entity entity = entitiesRepository.getCachedEntity(tour.getEntity().getId());
        for (ChannelContentTextDTO element : texts) {
            element.setLanguage(ChannelContentsUtils.checkElementLanguageForEntity(entity, languages, element.getLanguage()));
        }

        toursRepository.updateTourCommunicationElements(tourId, ChannelContentConverter.toMsEventText(texts));
    }

    public ChannelContentImageListDTO<TourChannelContentImageType> getChannelContentImages(Long tourId,
                                                                                           ChannelContentImageFilter<TourChannelContentImageType> filter) {
        getAndCheckTour(tourId, new TourEventFilterDTO());

        CommunicationElementFilter communicationElementFilter
                = ChannelContentConverter.fromEventFilter(filter, masterdataService);

        List<EventCommunicationElement> comElements = toursRepository.getTourCommunicationElements(
                tourId, communicationElementFilter, EventTagType::isImage);

        comElements.sort(Comparator.comparing(EventCommunicationElement::getLanguage).
                thenComparing(EventCommunicationElement::getTagId));

        return ChannelContentConverter.fromMsTourImage(comElements);
    }

    public void updateChannelContentImages(Long tourId, List<ChannelContentImageDTO<TourChannelContentImageType>> images) {
        Tour tour = getAndCheckTour(tourId, new TourEventFilterDTO());

        Entity entity = entitiesRepository.getEntity(tour.getEntity().getId());
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        for (ChannelContentImageDTO<TourChannelContentImageType> element : images) {
            element.setLanguage(ChannelContentsUtils.checkElementLanguageForEntity(entity, languages, element.getLanguage()));
        }

        toursRepository.updateTourCommunicationElements(tourId, ChannelContentConverter.toMsEventImageList(images));
    }

    public void deleteChannelContentImages(Long tourId, String language, BaseChannelContentImageType type) {
        getAndCheckTour(tourId, new TourEventFilterDTO());

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        EventCommunicationElement dto = ChannelContentConverter.buildEventCommunicationElementToDelete(language, type, null, languages);

        toursRepository.updateTourCommunicationElements(tourId, Collections.singletonList(dto));
    }

    private Tour getAndCheckTour(Long tourId, TourEventFilterDTO filter) {

        Tour tour = toursRepository.getTour(tourId, TourConverter.toMs(filter));
        securityManager.checkEntityAccessible(tour.getEntity().getId());
        return tour;
    }
}
