package es.onebox.mgmt.venues.service;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.NameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.datasources.ms.venue.dto.Venue;
import es.onebox.mgmt.datasources.ms.venue.dto.Venues;
import es.onebox.mgmt.datasources.ms.venue.dto.VenuesFilter;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplatesFilter;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtVenueErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.venues.converter.VenueConverter;
import es.onebox.mgmt.venues.converter.VenuesFilterConverter;
import es.onebox.mgmt.venues.dto.SearchCityVenuesResponse;
import es.onebox.mgmt.venues.dto.SearchCountryVenuesResponse;
import es.onebox.mgmt.venues.dto.SearchVenuesResponse;
import es.onebox.mgmt.venues.dto.VenueDetailsDTO;
import es.onebox.mgmt.venues.dto.VenueItemPostRequestDTO;
import es.onebox.mgmt.venues.dto.VenueItemPutRequestDTO;
import es.onebox.mgmt.venues.dto.VenueSearchAggFilter;
import es.onebox.mgmt.venues.dto.VenueSearchFilter;
import es.onebox.mgmt.venues.enums.VenueLogoImageType;
import es.onebox.mgmt.venues.utils.VenueValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static es.onebox.mgmt.venues.enums.VenueField.CITY;
import static es.onebox.mgmt.venues.enums.VenueField.COUNTRYCODE;

@Service
public class VenuesService {

    private static final String VENUES_AGG_CITIES_CACHE_KEY = "venues.cities";
    private static final String VENUES_AGG_COUNTRIES_CACHE_KEY = "venues.countries";
    private static final int VENUES_AGG_CACHE_TTL = 5 * 60;

    private final VenuesRepository venuesRepository;
    private final VenueValidationUtils venueValidationUtils;
    private final SecurityManager securityManager;
    private final CacheRepository cacheRepository;

    @Autowired
    public VenuesService(VenuesRepository venuesRepository,
                         VenueValidationUtils venueValidationUtils,
                         SecurityManager securityManager, CacheRepository cacheRepository) {
        this.venuesRepository = venuesRepository;
        this.venueValidationUtils = venueValidationUtils;
        this.securityManager = securityManager;
        this.cacheRepository = cacheRepository;
    }


    public VenueDetailsDTO getVenue(Long venueId, VenueSearchFilter filter) {
        Venue venue = venueValidationUtils.validateVenueId(venueId);
        if (!CommonUtils.isTrue(venue.getPublic()) || !securityManager.isSameOperator(venue.getEntity().getId())) {
            VenueTemplatesFilter templateFilter = new VenueTemplatesFilter();
            if (filter != null && filter.getEntityId() != null && SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)){
                securityManager.checkEntityAccessible(filter);
                templateFilter.setEntityId(filter.getEntityId());
            } else {
                templateFilter.setEntityId(SecurityUtils.getUserEntityId());
            }

            templateFilter.setVenueId(venueId);
            if (venuesRepository.getVenueTemplates(SecurityUtils.getUserOperatorId(), templateFilter, null, null)
                    .getMetadata().getTotal() == 0) {
                if (filter != null && filter.getEntityId() != null && filter.getEntityAdminId() != null){
                    securityManager.checkVisibleEntitiesfromManagedEntity(venue.getEntity().getId(), filter);
                } else {
                    securityManager.checkEntityAccessibleWithVisibility(venue.getEntity().getId());
                }
            }
        }

        return VenueConverter.fromMsDTO(venue, new VenueDetailsDTO());
    }

    public SearchVenuesResponse getVenues(VenueSearchFilter filter) {
        securityManager.checkEntityAccessible(filter);

        List<Long> visibleEntities = null;
        if (CommonUtils.isTrue(filter.getIncludeThirdPartyVenues())) {
            visibleEntities = securityManager.getVisibleEntities(SecurityUtils.getUserEntityId());
        }

        VenuesFilter venuesFilter = VenuesFilterConverter.convert(filter, visibleEntities);
        Venues venues = venuesRepository.getVenues(SecurityUtils.getUserOperatorId(),
                venuesFilter, filter.getSort(), filter.getFields());

        SearchVenuesResponse response = new SearchVenuesResponse();
        response.setData(venues.getData().stream()
                .map(VenueConverter::fromMsEntityToGridDetail)
                .collect(Collectors.toList())
        );
        response.setMetadata(venues.getMetadata());

        return response;
    }

    public SearchCityVenuesResponse getCitiesVenues(VenueSearchAggFilter filter) {
        securityManager.checkEntityAccessible(filter);

        VenuesFilter venuesFilter = VenuesFilterConverter.convertAgg(filter);

        long userOperatorId = SecurityUtils.getUserOperatorId();
        Venues venues = cacheRepository.cached(VENUES_AGG_CITIES_CACHE_KEY, VENUES_AGG_CACHE_TTL, TimeUnit.SECONDS, () ->
                        venuesRepository.getVenues(userOperatorId, venuesFilter, filter.getSort(), List.of(CITY.getDtoName())),
                buildVenuesAggCacheKey(filter, userOperatorId));

        SearchCityVenuesResponse response = new SearchCityVenuesResponse();
        response.setData(venues.getData().stream()
                .map(venue -> new NameDTO(venue.getCity()))
                .collect(Collectors.toList())
        );
        response.setMetadata(venues.getMetadata());

        return response;
    }

    public SearchCountryVenuesResponse getCountriesVenues(VenueSearchAggFilter filter) {
        securityManager.checkEntityAccessible(filter);

        VenuesFilter venuesFilter = VenuesFilterConverter.convertAgg(filter);
        long userOperatorId = SecurityUtils.getUserOperatorId();
        Venues venues = cacheRepository.cached(VENUES_AGG_COUNTRIES_CACHE_KEY, VENUES_AGG_CACHE_TTL, TimeUnit.SECONDS, () ->
                        venuesRepository.getVenues(userOperatorId, venuesFilter, filter.getSort(), List.of(COUNTRYCODE.getDtoName())),
                buildVenuesAggCacheKey(filter, userOperatorId));

        SearchCountryVenuesResponse response = new SearchCountryVenuesResponse();
        response.setData(venues.getData().stream()
                .map(venue -> new CodeDTO(venue.getCountry().getCode()))
                .collect(Collectors.toList())
        );
        response.setMetadata(venues.getMetadata());

        return response;
    }

    public IdDTO createVenue(VenueItemPostRequestDTO newVenue) {
        if (SecurityUtils.isOperatorEntity() || SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)) {
            if (newVenue.getEntityId() == null) {
                throw OneboxRestException.builder(ApiMgmtVenueErrorCode.ENTITYID_MANDATORY).build();
            }
        } else {
            newVenue.setEntityId(SecurityUtils.getUserEntityId());
        }
        securityManager.checkEntityAccessible(newVenue.getEntityId());
        return venuesRepository.createVenue(VenueConverter.toVenueItemRequestPost(newVenue));
    }

    public void updateVenue(VenueItemPutRequestDTO request) {
        Venue foundVenue = venueValidationUtils.validateVenueId(request.getId());

        securityManager.checkEntityAccessible(foundVenue.getEntity().getId());
        if (request.getImageLogo() != null && request.getImageLogo().isPresent()) {
            FileUtils.checkImage(request.getImageLogo().get(), VenueLogoImageType.VENUE_LOGO, VenueLogoImageType.VENUE_LOGO.name());
        }
        venuesRepository.updateVenue(VenueConverter.toVenueItemRequestPut(request));
    }

    public void deleteVenue(Long venueId) {
        Venue foundVenue = venueValidationUtils.validateVenueId(venueId);

        securityManager.checkEntityAccessible(foundVenue.getEntity().getId());
        venuesRepository.deleteVenue(venueId, foundVenue.getEntity().getId());
    }

    private Object[] buildVenuesAggCacheKey(VenueSearchAggFilter filter, long userOperatorId) {
        return new Object[]{userOperatorId, filter.getEntityId(), filter.getEntityAdminId(),
                filter.getIncludeThirdPartyVenues(), filter.getIncludeOwnTemplateVenues(), filter.getOnlyInUseVenues()};
    }

}
