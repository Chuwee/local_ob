package es.onebox.event.datasources.ms.ticket.enums;

public enum SessionCompatibilityValidationReason {
    ORIGIN_SESSION_NOT_LINKABLE_SEATS,
    ONE_SECTOR_MIN_NOT_FOUND_IN_TARGET,
    ONE_LINKABLE_SEAT_NOT_FOUND_IN_TARGET,
    AT_LEAST_ONE_SEAT_IS_NOT_FOUND_IN_TARGET_SESSION,
    ONE_NNZ_MIN_NOT_FOUND_IN_TARGET
}
