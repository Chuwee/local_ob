package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTagType;
import es.onebox.mgmt.datasources.ms.event.dto.tour.Tour;
import es.onebox.mgmt.datasources.ms.event.dto.tour.TourEventFilter;
import es.onebox.mgmt.datasources.ms.event.dto.tour.Tours;
import es.onebox.mgmt.events.enums.TourStatus;
import es.onebox.mgmt.events.tours.dto.TourFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Predicate;

@Repository
public class ToursRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public ToursRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public Tour getTour(Long tourId, TourEventFilter filter) {
        return msEventDatasource.getTour(tourId, filter);
    }

    public Tours getTours(TourFilter filter) {
        return msEventDatasource.getTours(filter);
    }

    public Long createTour(String tourName, Long entityId) {
        return msEventDatasource.createTour(tourName, entityId);
    }

    public void updateTour(Long tourId, String tourName, TourStatus status) {
        msEventDatasource.updateTour(tourId, tourName, status);
    }

    public void deleteTour(Long tourId) {
        msEventDatasource.deleteTour(tourId);
    }

    public List<EventCommunicationElement> getTourCommunicationElements(Long tourId, CommunicationElementFilter<EventTagType> filter, Predicate<EventTagType> tagType) {
        ChannelContentsUtils.addEventTagsToFilter(filter, tagType);
        return msEventDatasource.getTourCommunicationElements(tourId, filter);

    }

    public void updateTourCommunicationElements(Long tourId, List<EventCommunicationElement> elements) {
        msEventDatasource.updateTourCommunicationElements(tourId, elements);
    }
}
