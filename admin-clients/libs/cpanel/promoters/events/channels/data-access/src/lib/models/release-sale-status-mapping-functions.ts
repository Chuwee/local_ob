import { EventChannelReleaseStatus } from './event-channel-release-status.enum';
import { EventChannelRequestStatus } from './event-channel-request-status.enum';
import { EventChannelSaleStatus } from './event-channel-sale-status.enum';
import { EventChannelStatusIndicators } from './event-channel-status-indicators.enum';

export function getSaleStatusIndicator(
    requestStatus: EventChannelRequestStatus,
    eventChannelSaleStatus: EventChannelSaleStatus
): EventChannelStatusIndicators {
    let result: EventChannelStatusIndicators;

    if (requestStatus === EventChannelRequestStatus.rejected) {
        result = EventChannelStatusIndicators.rejected;
    } else if (requestStatus === EventChannelRequestStatus.pendingRequest) {
        result = eventChannelSaleStatus === EventChannelSaleStatus.pendingRelationship
            ? EventChannelStatusIndicators.neutral
            : EventChannelStatusIndicators.pending;
    } else if (requestStatus === EventChannelRequestStatus.pending) {
        result = EventChannelStatusIndicators.pending;
    } else {
        switch (eventChannelSaleStatus) {
            case EventChannelSaleStatus.salePending:
            case EventChannelSaleStatus.pendingRelationship:
                result = EventChannelStatusIndicators.pending;
                break;
            case EventChannelSaleStatus.saleCancelled:
            case EventChannelSaleStatus.releaseCancelled:
                result = EventChannelStatusIndicators.cancelled;
                break;
            case EventChannelSaleStatus.rejected:
                result = EventChannelStatusIndicators.rejected;
                break;
            case EventChannelSaleStatus.sale:
            case EventChannelSaleStatus.saleOnlySecondaryMarket:
                result = EventChannelStatusIndicators.success;
                break;
            case EventChannelSaleStatus.releaseFinished:
                result = EventChannelStatusIndicators.finished;
                break;
            case EventChannelSaleStatus.planned:
            case EventChannelSaleStatus.inProgramming:
            case EventChannelSaleStatus.cancelled:
            case EventChannelSaleStatus.notAccomplished:
            default:
                result = EventChannelStatusIndicators.neutral;
        }
    }

    return result;
}

export function getReleaseStatusIndicator(
    requestStatus: EventChannelRequestStatus,
    eventChannelReleaseStatus: EventChannelReleaseStatus
): EventChannelStatusIndicators {
    let result: EventChannelStatusIndicators;

    if (requestStatus === EventChannelRequestStatus.rejected) {
        result = EventChannelStatusIndicators.rejected;
    } else if (requestStatus === EventChannelRequestStatus.pendingRequest) {
        result = eventChannelReleaseStatus === EventChannelReleaseStatus.pendingRelationship
            ? EventChannelStatusIndicators.neutral
            : EventChannelStatusIndicators.pending;
    } else if (requestStatus === EventChannelRequestStatus.pending) {
        result = EventChannelStatusIndicators.pending;
    } else {
        switch (eventChannelReleaseStatus) {
            case EventChannelReleaseStatus.releasePending:
            case EventChannelReleaseStatus.pendingRelationship:
                result = EventChannelStatusIndicators.pending;
                break;
            case EventChannelReleaseStatus.releaseCancelled:
                result = EventChannelStatusIndicators.cancelled;
                break;
            case EventChannelReleaseStatus.rejected:
                result = EventChannelStatusIndicators.rejected;
                break;
            case EventChannelReleaseStatus.released:
                result = EventChannelStatusIndicators.success;
                break;
            case EventChannelReleaseStatus.releaseFinished:
                result = EventChannelStatusIndicators.finished;
                break;
            case EventChannelReleaseStatus.planned:
            case EventChannelReleaseStatus.inProgramming:
            case EventChannelReleaseStatus.cancelled:
            case EventChannelReleaseStatus.notAccomplished:
            default:
                result = EventChannelStatusIndicators.neutral;
        }
    }

    return result;
}
