import { PackChannelRequestStatus } from './pack-channel-request-status.enum';
import { PackChannelStatusIndicators } from './pack-channel-status-indicators.enum';

export function getPackSaleStatusIndicator(
    requestStatus: PackChannelRequestStatus
): PackChannelStatusIndicators {
    let result: PackChannelStatusIndicators;

    if (requestStatus === PackChannelRequestStatus.rejected) {
        result = PackChannelStatusIndicators.rejected;
    } else if (requestStatus === PackChannelRequestStatus.pendingRequest) {
        result = PackChannelStatusIndicators.pending;
    } else if (requestStatus === PackChannelRequestStatus.pending) {
        result = PackChannelStatusIndicators.pending;
    } else if (requestStatus === PackChannelRequestStatus.accepted) {
        result = PackChannelStatusIndicators.success;
    } else {
        result = PackChannelStatusIndicators.neutral;
    }

    return result;
}