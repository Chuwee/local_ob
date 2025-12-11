package es.onebox.event.sessions.utils;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.common.request.PriceTypeFilter;
import es.onebox.event.datasources.ms.entity.dto.CustomerType;
import es.onebox.event.datasources.ms.entity.dto.CustomerTypes;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dto.PriceTypeDTO;
import es.onebox.event.events.dto.PriceTypesDTO;
import es.onebox.event.events.dto.RatePriceZoneRestrictionDTO;
import es.onebox.event.events.dto.RateRelationsRestrictionDTO;
import es.onebox.event.events.dto.UpdateRateRestrictionsDTO;
import es.onebox.event.events.service.EventPriceTypeService;
import es.onebox.event.exception.MsEventRateErrorCode;
import es.onebox.event.priceengine.request.ChannelSubtype;
import es.onebox.event.priceengine.request.EventChannelSearchFilter;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.priceengine.simulation.record.EventChannelRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RateRestrictionsValidator {

    private final RateDao rateDao;
    private final EventPriceTypeService eventPriceTypeService;
    private final EntitiesRepository entitiesRepository;
    private final ChannelEventDao channelEventDao;


    public RateRestrictionsValidator(RateDao rateDao,
                                     EventPriceTypeService eventPriceTypeService,
                                     EntitiesRepository entitiesRepository,
                                     ChannelEventDao channelEventDao){
        this.rateDao = rateDao;
        this.eventPriceTypeService = eventPriceTypeService;
        this.entitiesRepository = entitiesRepository;
        this.channelEventDao = channelEventDao;
    }


    public void validateRateRestrictions(UpdateRateRestrictionsDTO restrictionDTO) {

        if (BooleanUtils.isTrue(restrictionDTO.getDateRestrictionEnabled()) && ObjectUtils.isEmpty(restrictionDTO.getDateRestriction())) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
        }

        if (restrictionDTO.getDateRestriction() != null) {
            ZonedDateTime from = restrictionDTO.getDateRestriction().getFrom();
            ZonedDateTime to = restrictionDTO.getDateRestriction().getTo();
            if ((from != null && from.isAfter(ZonedDateTime.now())) || (to != null && to.isAfter(ZonedDateTime.now()))) {
                throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
            }
            if (from != null && to != null && !from.isBefore(to)) {
                throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
            }
        }

        if (BooleanUtils.isTrue(restrictionDTO.getCustomerTypeRestrictionEnabled()) && CollectionUtils.isEmpty(restrictionDTO.getCustomerTypeRestriction())) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
        }

        if (BooleanUtils.isTrue(restrictionDTO.getRateRelationsRestrictionEnabled()) && ObjectUtils.isEmpty(restrictionDTO.getRateRelationsRestriction())) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
        }

        if ((BooleanUtils.isTrue(restrictionDTO.getPriceZoneRestrictionEnabled())) && (restrictionDTO.getPriceZoneRestriction() == null || CollectionUtils.isEmpty(restrictionDTO.getPriceZoneRestriction().getRestrictedPriceZoneIds()))) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
        }

        if (BooleanUtils.isTrue(restrictionDTO.getPeriodRestrictionEnabled()) && CollectionUtils.isEmpty(restrictionDTO.getPeriodRestriction())) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
        }

        if (BooleanUtils.isTrue(restrictionDTO.getChannelRestrictionEnabled()) && CollectionUtils.isEmpty(restrictionDTO.getChannelRestriction())) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
        }


        if (BooleanUtils.isTrue(restrictionDTO.getMaxItemRestrictionEnabled())
                && (restrictionDTO.getMaxItemRestriction() == null || restrictionDTO.getMaxItemRestriction() <= 0)) {
            throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS)
                    .setMessage("Max item restriction must be greater than 0 when enabled.")
                    .build();
        }
    }

    public List<IdNameDTO> validateRateRelationsRestriction(RateRelationsRestrictionDTO relationsRestrictionDTO, Integer eventId, Integer rateId) {

        if (relationsRestrictionDTO == null) {
            return null;
        }

        if (CollectionUtils.isEmpty(relationsRestrictionDTO.getRequiredRates())) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
        }

        List <IdNameDTO> requiredRates = new ArrayList<>();
        relationsRestrictionDTO.getRequiredRates().forEach(requiredRateId -> {
            CpanelTarifaRecord rate = rateDao.getEventRate(eventId, requiredRateId);
            if (rate == null || Objects.equals(requiredRateId, rateId)) {
                throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
            }
            requiredRates.add(new IdNameDTO(Long.valueOf(rate.getIdtarifa()),rate.getNombre()));
        });

        PriceTypeFilter filter = new PriceTypeFilter();
        filter.setLimit(2000L);
        PriceTypesDTO priceTypes = eventPriceTypeService.getEventPriceTypes(Long.valueOf(eventId), filter);

        if (relationsRestrictionDTO.getRestrictedPriceZones() != null && priceTypes == null) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
        }

        if (CollectionUtils.isNotEmpty(relationsRestrictionDTO.getRestrictedPriceZones())) {
            List<PriceTypeDTO> priceTypeDTOs = priceTypes.getData();
            if (priceTypeDTOs == null) {
                throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
            }
            List<Long> priceTypeIds = priceTypeDTOs.stream().map(PriceTypeDTO::getId).toList();
            relationsRestrictionDTO.getRestrictedPriceZones().forEach(priceZoneId -> {
                if (!priceTypeIds.contains(priceZoneId.longValue())) {
                    throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
                }
            });
        }
        return requiredRates;
    }

    public void validatePriceZoneRestriction(RatePriceZoneRestrictionDTO priceZoneRestrictionDTO, Integer eventId) {
        if (priceZoneRestrictionDTO == null || priceZoneRestrictionDTO.isEmpty()) return;

        PriceTypeFilter filter = new PriceTypeFilter();
        filter.setLimit(2000L);
        PriceTypesDTO priceTypes = eventPriceTypeService.getEventPriceTypes(Long.valueOf(eventId), filter);

        if (priceTypes == null || CollectionUtils.isEmpty(priceTypes.getData()))
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);

        List<PriceTypeDTO> priceTypesList = priceTypes.getData();

        List<Integer> eventPriceZoneIds = priceTypesList.stream()
                .map(PriceTypeDTO::getId)
                .map(Long::intValue)
                .toList();

        if (priceZoneRestrictionDTO.getRestrictedPriceZoneIds().stream().anyMatch(zone -> !eventPriceZoneIds.contains(zone))) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
        }
    }

    public CustomerTypes validateCustomerTypesRestriction(
        UpdateRateRestrictionsDTO restrictionsDTO, Integer entityId) {
        if (BooleanUtils.isTrue(restrictionsDTO.getCustomerTypeRestrictionEnabled())) {
            CustomerTypes customerTypesResult = entitiesRepository.getCustomerTypes(entityId, null);
            validateCustomerTypeRestriction(restrictionsDTO.getCustomerTypeRestriction(), customerTypesResult);
            return customerTypesResult;
        }
        return null;
    }

    public void validateChannelRestrictions(List<Integer> channelIds, Integer eventId){
        if (CollectionUtils.isEmpty(channelIds)){
            return;
        }
        List<Long> longChannelIds = channelIds.stream().map(Integer::longValue).toList();

        EventChannelSearchFilter filter = new EventChannelSearchFilter();
        filter.setSubtype(List.of(ChannelSubtype.PORTAL_WEB, ChannelSubtype.PORTAL_B2B));
        filter.setId(longChannelIds);

        List<EventChannelRecord> channelEvents = channelEventDao.findChannelEvents(eventId.longValue(), filter);

        if(channelEvents.isEmpty() || channelEvents.size() != channelIds.size()){
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
        }

    }

    private void validateCustomerTypeRestriction(List<Long> customerTypeRestriction, CustomerTypes customerTypes) {
        if (customerTypeRestriction == null || customerTypeRestriction.isEmpty()) return;

        if (customerTypes == null || CollectionUtils.isEmpty(customerTypes.getData()))
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);

        Set<Long> validCustomerTypeIds = customerTypes.getData().stream()
                .map(CustomerType::getId)
                .collect(Collectors.toSet());

        if (customerTypeRestriction.stream().anyMatch(customerTypeId -> !validCustomerTypeIds.contains(customerTypeId))) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
        }
    }
}
