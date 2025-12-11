package es.onebox.event.sessions.utils;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.sessions.domain.sessionconfig.DynamicPrice;
import es.onebox.event.sessions.dto.ConditionType;
import es.onebox.event.sessions.dto.DynamicPriceDTO;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class DynamicPriceValidator {

    public static void validateDynamicPrice(DynamicPriceDTO request,
                                            ZonedDateTime creationDate,
                                            ZonedDateTimeWithRelative saleStartDate) {

        validateRequiredFields(request);
        validateConditionTypeRules(request);
        validateDateRules(request, creationDate, saleStartDate);
    }

    private static void validateRequiredFields(DynamicPriceDTO request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new OneboxRestException(MsEventErrorCode.MISSING_NAME);
        }

        if (request.getConditionTypes() == null) {
            throw new OneboxRestException(MsEventErrorCode.MISSING_CONDITION_TYPE);
        }

        if (request.getOrder() == null) {
            throw new OneboxRestException(MsEventErrorCode.MISSING_ORDER);
        }

        if (request.getDynamicRatesPriceDTO() == null || request.getDynamicRatesPriceDTO().isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.MISSING_RATES);
        }
    }

    private static void validateConditionTypeRules(DynamicPriceDTO request) {
        if (request.getConditionTypes() == null || request.getConditionTypes().isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.MISSING_CONDITION_TYPE);
        }

        if (request.getConditionTypes().contains(ConditionType.DATE) && request.getValidDate() == null) {
            throw new OneboxRestException(MsEventErrorCode.MISSING_DATE);
        }

        if (request.getConditionTypes().contains(ConditionType.CAPACITY) && request.getCapacity() == null) {
            throw new OneboxRestException(MsEventErrorCode.MISSING_CAPACITY);
        }
    }

    private static void validateDateRules(DynamicPriceDTO request,
                                          ZonedDateTime creationDate,
                                          ZonedDateTimeWithRelative saleStartDate) {
        if (request.getValidDate() != null) {
            if (request.getValidDate().isBefore(creationDate)) {
                throw new OneboxRestException(MsEventErrorCode.DATE_BEFORE_CREATION);
            }

            if (request.getValidDate().isBefore(saleStartDate.absolute())) {
                throw new OneboxRestException(MsEventErrorCode.DATE_BEFORE_SALE_START);
            }
        }
    }

    public static void validateCapacitySequence(List<DynamicPriceDTO> prices) {
        List<DynamicPriceDTO> sortedPrices = prices.stream()
                .sorted(Comparator.comparingInt(DynamicPriceDTO::getOrder))
                .toList();
        Integer previousCapacity = null;
        boolean previousWasOnlyCapacity = false;
        for (DynamicPriceDTO price : sortedPrices) {
            if(price.getConditionTypes()== null){
                price.setConditionTypes(new HashSet<>());
            }

            boolean hasCapacityCondition = price.getConditionTypes().contains(ConditionType.CAPACITY);
            boolean hasOtherConditions = price.getConditionTypes().stream().anyMatch(type -> type != ConditionType.CAPACITY);
            boolean onlyCapacityCondition = price.getConditionTypes().size() == 1 && hasCapacityCondition;

            if (hasCapacityCondition && previousCapacity != null) {
                if (previousWasOnlyCapacity && hasOtherConditions && price.getCapacity() < previousCapacity) {
                    throw new OneboxRestException(MsEventErrorCode.CAPACITY_LESS_THAN_PREVIOUS);
                }
                else if (onlyCapacityCondition && previousWasOnlyCapacity && price.getCapacity() <= previousCapacity) {
                    throw new OneboxRestException(MsEventErrorCode.CAPACITY_LESS_THAN_PREVIOUS);
                }
            }

            if (hasCapacityCondition) {
                previousCapacity = price.getCapacity();
                previousWasOnlyCapacity = onlyCapacityCondition;
            }
        }
    }

    public static void validateDateSequence(List<DynamicPriceDTO> prices) {
        List<DynamicPriceDTO> sortedPrices = prices.stream()
                .sorted(Comparator.comparingInt(DynamicPriceDTO::getOrder))
                .toList();

        ZonedDateTime previousDate = null;
        boolean previousHasMultipleConditions = false;

        for (DynamicPriceDTO price : sortedPrices) {
            if(price.getConditionTypes() == null) {
                price.setConditionTypes(new HashSet<>());
            }

            boolean hasDateCondition = price.getConditionTypes().contains(ConditionType.DATE);
            if (!hasDateCondition || previousDate == null) {
                if (hasDateCondition) {
                    previousDate = price.getValidDate();
                    previousHasMultipleConditions = price.getConditionTypes().size() > 1;
                }
                continue;
            }

            boolean currentHasOnlyDate = price.getConditionTypes().size() == 1;
            boolean allowPreviousDate = currentHasOnlyDate && previousHasMultipleConditions;
            if (!allowPreviousDate && price.getValidDate().isBefore(previousDate)) {
                throw new OneboxRestException(MsEventErrorCode.DATE_LESS_THAN_PREVIOUS);
            }

            previousDate = price.getValidDate();
            previousHasMultipleConditions = price.getConditionTypes().size() > 1;
        }
    }

    public static void validateReordering(List<DynamicPrice> dynamicPrices) {
        if (dynamicPrices.isEmpty()) {
            return;
        }

        List<Integer> orders = dynamicPrices.stream()
                .map(DynamicPrice::getOrder)
                .sorted()
                .toList();

        if (orders.get(0) != 0) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_ORDER_SEQUENCE);
        }

        for (int i = 1; i < orders.size(); i++) {
            if (orders.get(i) != orders.get(i - 1) + 1) {
                throw new OneboxRestException(MsEventErrorCode.INVALID_ORDER_SEQUENCE);
            }
        }
    }
}
