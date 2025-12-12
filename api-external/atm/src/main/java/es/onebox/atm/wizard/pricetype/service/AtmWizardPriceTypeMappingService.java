package es.onebox.atm.wizard.pricetype.service;

import es.onebox.atm.wizard.pricetype.dto.PriceTypeMapping;
import es.onebox.atm.wizard.pricetype.dto.PriceTypeMappingRequest;
import es.onebox.atm.wizard.pricetype.enums.PriceTypeCode;
import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.venue.dto.BasePriceType;
import es.onebox.common.datasources.ms.venue.dto.PriceTypeRequestDTO;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.service.EventValidationService;
import es.onebox.common.service.VenueTemplateValidationService;
import es.onebox.core.exception.ExceptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AtmWizardPriceTypeMappingService {

    private static final String ATM_TICKET_SELECTION_TEMPLATE = "tour-select";

    @Value("${atm.entity.entityId}")
    private Long atmEntityId;

    private final EventValidationService eventValidationService;
    private final VenueTemplateValidationService venueTemplateValidationService;
    private final VenueTemplateRepository venueTemplateRepository;
    private final MsEventRepository msEventRepository;

    @Autowired
    public AtmWizardPriceTypeMappingService(final EventValidationService eventValidationService,
                                            final VenueTemplateValidationService venueTemplateValidationService,
                                            final VenueTemplateRepository venueTemplateRepository,
                                            final MsEventRepository msEventRepository) {
        this.eventValidationService = eventValidationService;
        this.venueTemplateValidationService = venueTemplateValidationService;
        this.venueTemplateRepository = venueTemplateRepository;
        this.msEventRepository = msEventRepository;
    }

    public void setUpPriceTypeCodes(final Long eventId, final Long venueTemplateId, final PriceTypeMappingRequest request) {
        eventValidationService.validate(eventId, atmEntityId);
        venueTemplateValidationService.validate(atmEntityId, eventId, venueTemplateId);

        List<BasePriceType> venueTemplatePriceTypes = venueTemplateRepository.getPriceTypes(venueTemplateId);
        validateRequest(request, venueTemplatePriceTypes);

        venueTemplatePriceTypes.forEach(pt -> resetAllPriceTypeCodes(venueTemplateId,pt));

        Map<Long, PriceTypeCode> priceTypeCodesMap = request.getPriceTypeCodes().stream()
                .collect(Collectors.toMap(PriceTypeMapping::getPriceTypeId, PriceTypeMapping::getCode));

        venueTemplatePriceTypes.forEach(pt -> updatePriceTypeCode(venueTemplateId, priceTypeCodesMap, pt));

        // if the price type codes are being updated, we know is an ATM tour event, so we set the selection template accordingly
        EventDTO eventUpdate = new EventDTO();
        eventUpdate.setId(eventId);
        eventUpdate.setCustomSelectTemplate(ATM_TICKET_SELECTION_TEMPLATE);
        msEventRepository.updateEvent(eventId, eventUpdate);
    }

    private void validateRequest(final PriceTypeMappingRequest request, final List<BasePriceType> venueTemplatePriceTypes) {
        if (CollectionUtils.isEmpty(venueTemplatePriceTypes)) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.INVALID_VENUE_TEMPLATE);
        }

        int totalCodes = PriceTypeCode.values().length;
        Set<PriceTypeCode> totalPriceTypeCodes = request.getPriceTypeCodes().stream()
                .map(PriceTypeMapping::getCode)
                .collect(Collectors.toSet());
        Set<Long> totalPriceTypeIds = request.getPriceTypeCodes().stream()
                .map(PriceTypeMapping::getPriceTypeId)
                .collect(Collectors.toSet());

        if (request.getPriceTypeCodes().size() != totalCodes ||
                totalPriceTypeCodes.size() != totalCodes ||
                totalPriceTypeIds.size() != totalCodes) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.INVALID_PRICETYPE_MAPINGS);
        }

        Set<Long> venueTemplatePriceTypeIds = venueTemplatePriceTypes.stream().map(BasePriceType::getId).collect(Collectors.toSet());
        boolean isInvalidPriceTypeId = request.getPriceTypeCodes().stream()
                .anyMatch(ptm -> !venueTemplatePriceTypeIds.contains(ptm.getPriceTypeId()));

        if (isInvalidPriceTypeId) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.INVALID_PRICETYPE_MAPINGS);
        }
    }

    private void updatePriceTypeCode(final Long venueTemplateId, final Map<Long, PriceTypeCode> priceTypeCodesMap, final BasePriceType pt) {
        if(priceTypeCodesMap.containsKey(pt.getId())){
            venueTemplateRepository.updatePriceType(venueTemplateId, pt.getId(),
                    buildUpdateRequest(priceTypeCodesMap.get(pt.getId()).name()));
        }
    }

    private void resetAllPriceTypeCodes(final Long venueTemplateId, final BasePriceType pt) {
        venueTemplateRepository.updatePriceType(venueTemplateId, pt.getId(), buildUpdateRequest(""));
    }

    private PriceTypeRequestDTO buildUpdateRequest(String code) {
        PriceTypeRequestDTO request = new PriceTypeRequestDTO();
        request.setCode(code);
        return request;
    }
}
