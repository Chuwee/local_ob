package es.onebox.event.events.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.Arrays;
import java.util.List;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import jakarta.validation.Valid;

import es.onebox.event.events.request.TourEventsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.onebox.event.common.CommonIdResponse;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.BaseTourDTO;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.dto.TourDTO;
import es.onebox.event.events.dto.ToursDTO;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.event.events.request.ToursFilter;
import es.onebox.event.events.service.TourService;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/tours")
public class TourController {

    private final TourService tourService;

    @Autowired
    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @RequestMapping(method = GET, value = "/{tourId}")
    public TourDTO getTour(@PathVariable(value = "tourId") Long tourId, TourEventsFilter filter) {
        return tourService.getTour(tourId, filter);
    }

    @RequestMapping(method = GET)
    public ToursDTO getTours(ToursFilter filter) {
        return tourService.findTours(filter);
    }

    @RequestMapping(method = POST)
    public CommonIdResponse createTour(@RequestBody BaseTourDTO tourDTO) {

        Integer tourId = tourService.createTour(tourDTO);

        return new CommonIdResponse(tourId);
    }

    @RequestMapping(method = PUT, value = "/{tourId}")
    public void updateTour(@RequestBody BaseTourDTO tourDTO,
                           @PathVariable(value = "tourId") Long tourId) {

        tourService.updateTour(tourId, tourDTO);

        tourService.postUpdateTourEvents(tourId, null);
    }

    @RequestMapping(method = GET, value = "/{tourId}/communication-elements")
    public List<EventCommunicationElementDTO> getEventCommunicationElements(
            @PathVariable Long tourId, @Valid EventCommunicationElementFilter filter) {
        return tourService.findCommunicationElements(tourId, filter);
    }

    @RequestMapping(method = POST, value = "/{tourId}/communication-elements")
    public void updateEventCommunicationElements(@PathVariable Long tourId,
                                                 @Valid @RequestBody EventCommunicationElementDTO[] elements) {

        tourService.updateCommunicationElements(tourId, Arrays.asList(elements));

        tourService.postUpdateTourEvents(tourId, EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

}
