package es.onebox.mgmt.sessions.dynamicprice;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPrice;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceStatusRequest;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceZone;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicRatesPrice;
import es.onebox.mgmt.datasources.ms.event.repository.DynamicPriceRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.BaseTag;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.sessions.SessionsService;
import es.onebox.mgmt.sessions.dto.SessionPriceTypesAvailabilityDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicPriceConfigDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicPriceStatusRequestDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicPriceZoneDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicRatesPriceDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.RequestDynamicPriceDTO;
import es.onebox.mgmt.validation.ValidationDynamicPrices;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DynamicPriceService {

    private final DynamicPriceRepository dynamicPriceRepository;
    private final VenuesRepository venuesRepository;
    private final ValidationService validationService;
    private final SessionsService sessionsService;

    @Autowired
    public DynamicPriceService(DynamicPriceRepository dynamicPriceRepository, VenuesRepository venuesRepository,
                               ValidationService validationService, SessionsService sessionsService) {
        this.dynamicPriceRepository = dynamicPriceRepository;
        this.venuesRepository = venuesRepository;
        this.validationService = validationService;
        this.sessionsService = sessionsService;
    }

    public DynamicPriceConfigDTO getDynamicPriceConfig(Long eventId, Long sessionId, Boolean initialize) {
        DynamicPriceConfig dynamicPriceConfig = dynamicPriceRepository.getDynamicPriceConfig(eventId, sessionId, initialize);
        if (dynamicPriceConfig != null && CollectionUtils.isNotEmpty(dynamicPriceConfig.getDynamicPriceZoneDTO())) {
            fillPriceZoneName(eventId, sessionId, dynamicPriceConfig.getDynamicPriceZoneDTO());
            fillEditableZoneFlag(eventId, sessionId, dynamicPriceConfig.getDynamicPriceZoneDTO());
        }
        return DynamicPriceConverter.toDynamicPriceConfigDTO(dynamicPriceConfig);
    }

    public DynamicPriceZoneDTO getDynamicPriceZone(Long eventId, Long sessionId, Long idPriceZone) {
        DynamicPriceZone dynamicPriceZone = dynamicPriceRepository.getDynamicPriceZone(eventId, sessionId, idPriceZone);
        fillPriceZoneName(eventId, sessionId, List.of(dynamicPriceZone));

        DynamicPriceZoneDTO dynamicPriceZoneDTO = DynamicPriceConverter.toDynamicPriceZoneDTO(dynamicPriceZone);
        Session session = validationService.getAndCheckOnlySession(eventId, sessionId);
        dynamicPriceZoneDTO.setEditable(isSessionZoneEditable(session));

        setCapacityValues(dynamicPriceZoneDTO, eventId, sessionId, idPriceZone);

        return dynamicPriceZoneDTO;
    }

    private boolean isSessionZoneEditable(Session session) {
        return !session.getStatus().equals(SessionStatus.READY) &&
                (session.getDate().getSalesStart() == null ||
                        !session.getDate().getSalesStart().absolute().isBefore(ZonedDateTime.now()));
    }

    private void fillEditableZoneFlag(Long eventId, Long sessionId, List<DynamicPriceZone> dynamicPriceZoneList) {
        Session session = validationService.getAndCheckOnlySession(eventId, sessionId);
        boolean editable = isSessionZoneEditable(session);
        if (CollectionUtils.isNotEmpty(dynamicPriceZoneList)) {
            dynamicPriceZoneList.forEach(dynamicPriceZoneDTO -> dynamicPriceZoneDTO.setEditable(editable));
        }
    }

    /*
     * To avoid calling session repository multiple times and venue multiple times
     *
     */
    private void fillPriceZoneName(Long eventId, Long sessionId, List<DynamicPriceZone> dynamicPriceZoneList) {
        Long venueTemplateId = validationService.getAndCheckOnlySession(eventId, sessionId).getVenueConfigId();
        if (venueTemplateId != null) {
            List<PriceType> priceTypes = venuesRepository.getPriceTypes(venueTemplateId);
            if (CollectionUtils.isNotEmpty(priceTypes)) {
                dynamicPriceZoneList.forEach(dynamicPriceZone -> {
                    String name = priceTypes.stream()
                            .filter(priceType -> priceType.getId().equals(dynamicPriceZone.getIdPriceZone()))
                            .map(BaseTag::getName)
                            .findFirst()
                            .orElse(null);
                    dynamicPriceZone.setPriceZoneName(name);
                });
            }
        }
    }

    public List<DynamicRatesPriceDTO> getDynamicRatePrice(Long eventId, Long sessionId, Long idPriceZone) {
        List<DynamicRatesPrice> dynamicRatePriceList = dynamicPriceRepository.getDynamicRatePrice(eventId, sessionId, idPriceZone);
        List<DynamicRatesPriceDTO> target = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dynamicRatePriceList)) {
            dynamicRatePriceList.forEach(rate -> target.add(DynamicPriceConverter.toDynamicRatePriceDTO(rate)));
        }
        return target;
    }

    public void createOrUpdateDynamicPriceConfig(Long eventId, Long sessionId, Long idPriceZone, List<RequestDynamicPriceDTO> request) {
        ValidationDynamicPrices.validateCapacityDynamicPrice(idPriceZone, request, sessionsService.getSessionPriceTypesAvailability(eventId, sessionId));
        List<DynamicPrice> list = new ArrayList<>();
        request.forEach(requestDynamicPriceDTO -> {
            DynamicPrice dynamicPrice = DynamicPriceConverter.fromDynamicPriceDTO(requestDynamicPriceDTO);
            list.add(dynamicPrice);
        });
        dynamicPriceRepository.createOrUpdateDynamicPrice(eventId, sessionId, idPriceZone, list);
    }

    public void deleteDynamicPriceConfig(Long eventId, Long sessionId, Long idPriceZone, Integer orderId) {
        dynamicPriceRepository.deleteDynamicPriceConfig(eventId, sessionId, idPriceZone, orderId);
    }

    public void activateDynamicPriceConfig(Long eventId, Long sessionId, DynamicPriceStatusRequestDTO request) {
        DynamicPriceStatusRequest status = DynamicPriceConverter.fromDynamicPriceStatusRequestDTO(request);
        dynamicPriceRepository.activateDynamicPriceConfig(eventId, sessionId, status);
    }

    private void setCapacityValues(DynamicPriceZoneDTO dynamicPriceZoneDTO, Long eventId, Long sessionId, Long priceZoneId) {
        List<SessionPriceTypesAvailabilityDTO> availabilities = sessionsService.getSessionPriceTypesAvailability(eventId, sessionId);

        SessionPriceTypesAvailabilityDTO availability = availabilities.stream()
                .filter(a -> a.getPriceType() != null &&
                        priceZoneId.equals(a.getPriceType().getId()))
                .findFirst()
                .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.DYNAMIC_PRICE_ZONE_NO_CAPACITY));

        dynamicPriceZoneDTO.setAvailableCapacity(availability.getAvailability().getAvailable());
        dynamicPriceZoneDTO.setCapacity(availability.getAvailability().getTotal().getValue());
    }
}
