package es.onebox.internal.automaticrenewals.renewals.dto;

import es.onebox.internal.automaticrenewals.renewals.enums.AutomaticRenewalsStatus;

public record UpdateAutomaticRenewalsExecutionDTO(AutomaticRenewalsStatus status) { }
