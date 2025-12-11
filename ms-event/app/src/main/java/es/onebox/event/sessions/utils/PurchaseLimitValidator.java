package es.onebox.event.sessions.utils;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.datasources.ms.entity.dto.ExternalLoginConfig;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.sessions.dto.UpdateSaleConstraintDTO;

public class PurchaseLimitValidator {

	private PurchaseLimitValidator() {
	}

	public static void validateConfig(ExternalLoginConfig externalLoginConfig, UpdateSaleConstraintDTO request) {

		if (Boolean.FALSE.equals(request.getCustomersLimitsEnabled()) || request.getCustomersLimits() == null) {
			return;
		}

		int maxAllowed = externalLoginConfig.purchaseLimit().customer().maxAllowed();
		int requestedMax = request.getCustomersLimits().getMax();

		if (requestedMax > maxAllowed) {
			throw new OneboxRestException(MsEventSessionErrorCode.SESSION_LIMIT_NOT_ALLOWED);
		}
	}
}
