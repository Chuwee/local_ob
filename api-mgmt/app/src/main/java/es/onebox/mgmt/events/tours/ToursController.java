package es.onebox.mgmt.events.tours;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.channelcontents.TourChannelContentImageType;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.EventChannelContentTextFilter;
import es.onebox.mgmt.events.dto.EventChannelContentTextListDTO;
import es.onebox.mgmt.events.dto.SearchToursResponse;
import es.onebox.mgmt.events.dto.channel.EventChannelContentTextType;
import es.onebox.mgmt.events.tours.dto.BaseTourDTO;
import es.onebox.mgmt.events.tours.dto.CreateTourRequestDTO;
import es.onebox.mgmt.events.tours.dto.TourChannelContentImageFilter;
import es.onebox.mgmt.events.tours.dto.TourChannelContentImageListDTO;
import es.onebox.mgmt.events.tours.dto.TourDTO;
import es.onebox.mgmt.events.tours.dto.TourEventFilterDTO;
import es.onebox.mgmt.events.tours.dto.TourSearchFilter;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = ToursController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ToursController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/tours";

    public static final String CHANNEL_CONTENTS_URI = "/{tourId}/channel-contents";

    private static final String AUDIT_COLLECTION = "TOURS";
    private static final String AUDIT_SUBCOLLECTION_TEXT_CONTENTS = "TEXT_CONTENTS";
    private static final String AUDIT_SUBCOLLECTION_IMAGE_CONTENTS = "IMAGE_CONTENTS";

    @Autowired
    private ToursService toursService;

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{tourId}")
    public TourDTO getTour(@PathVariable @Min(value = 1, message = "tourId must be above 0") Long tourId,
                           @BindUsingJackson TourEventFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return toursService.get(tourId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public SearchToursResponse getTours(@BindUsingJackson @Valid TourSearchFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return toursService.search(filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createTour(@RequestBody @Valid CreateTourRequestDTO tourData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return new IdDTO(toursService.create(tourData.getName(), tourData.getEntityId()));
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{tourId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTour(@PathVariable @Min(value = 1, message = "tourId must be above 0") Long tourId,
                                                   @RequestBody @Valid BaseTourDTO tourData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        if (tourData.getId() != null && !tourData.getId().equals(tourId)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "tourId is different between pathVariable and requestBody", null);
        }
        toursService.update(tourId, tourData);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{tourId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTour(@PathVariable @Min(value = 1, message = "tourId must be above 0") Long tourId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        toursService.delete(tourId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = CHANNEL_CONTENTS_URI + "/texts")
    public ChannelContentTextListDTO<EventChannelContentTextType> getChannelContentsTexts(@PathVariable @Min(value = 1, message = "tourId must be above 0") Long tourId,
                                                                                          @BindUsingJackson @Valid EventChannelContentTextFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TEXT_CONTENTS, AuditTag.AUDIT_ACTION_SEARCH);
        return toursService.getChannelContentTexts(tourId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = CHANNEL_CONTENTS_URI + "/texts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelContentsTexts(@PathVariable @Min(value = 1, message = "tourId must be above 0") Long tourId,
                                           @Valid @RequestBody EventChannelContentTextListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TEXT_CONTENTS, AuditTag.AUDIT_ACTION_UPDATE);
        toursService.updateChannelContentTexts(tourId, contents.getTexts());
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = CHANNEL_CONTENTS_URI + "/images")
    public ChannelContentImageListDTO<TourChannelContentImageType> getChannelContentsImages(@PathVariable @Min(value = 1, message = "tourId must be above 0") Long tourId,
                                                                                            @BindUsingJackson @Valid TourChannelContentImageFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_IMAGE_CONTENTS, AuditTag.AUDIT_ACTION_SEARCH);
        return toursService.getChannelContentImages(tourId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = CHANNEL_CONTENTS_URI + "/images",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelContentsImages(@PathVariable @Min(value = 1, message = "tourId must be above 0") Long tourId,
                                                                    @Valid @RequestBody TourChannelContentImageListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_IMAGE_CONTENTS, AuditTag.AUDIT_ACTION_UPDATE);
        ChannelContentsUtils.validateEventContents(contents, false);
        toursService.updateChannelContentImages(tourId, contents.getImages());
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE,
            value = CHANNEL_CONTENTS_URI + "/images/languages/{language}/types/{type}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannelContentsImage(@PathVariable @Min(value = 1, message = "tourId must be above 0") Long tourId,
                                                                   @PathVariable String language,
                                                                   @PathVariable TourChannelContentImageType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_IMAGE_CONTENTS, AuditTag.AUDIT_ACTION_DELETE);
        toursService.deleteChannelContentImages(tourId, language, type);
    }

}
