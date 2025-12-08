import { SeasonChannelRequestStatus } from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { SeasonChannelReleaseStatus } from './season-channel-release-status.type';
import { SeasonChannelSaleStatus } from './season-channel-sale-status.enum';
import { SeasonChannelStatusIndicators } from './season-channel-status-indicators.enum';

export function getSaleStatusIndicator(
    requestStatus: SeasonChannelRequestStatus | string,
    seasonChannelSaleStatus: SeasonChannelSaleStatus | string
): SeasonChannelStatusIndicators {
    let result: SeasonChannelStatusIndicators;

    if (requestStatus === 'REJECTED') {
        result = SeasonChannelStatusIndicators.rejected;
    } else if (requestStatus === 'PENDING_REQUEST' || requestStatus === 'PENDING') {
        result = SeasonChannelStatusIndicators.pending;
    } else {
        switch (seasonChannelSaleStatus) {
            case SeasonChannelSaleStatus.salePending:
            case SeasonChannelSaleStatus.pendingRelationship:
                result = SeasonChannelStatusIndicators.pending;
                break;
            case SeasonChannelSaleStatus.saleCancelled:
            case SeasonChannelSaleStatus.releaseCancelled:
                result = SeasonChannelStatusIndicators.cancelled;
                break;
            case SeasonChannelSaleStatus.rejected:
                result = SeasonChannelStatusIndicators.rejected;
                break;
            case SeasonChannelSaleStatus.sale:
                result = SeasonChannelStatusIndicators.success;
                break;
            case SeasonChannelSaleStatus.releaseFinished:
                result = SeasonChannelStatusIndicators.finished;
                break;
            case SeasonChannelSaleStatus.planned:
            case SeasonChannelSaleStatus.inProgramming:
            case SeasonChannelSaleStatus.cancelled:
            case SeasonChannelSaleStatus.notAccomplished:
            default:
                result = SeasonChannelStatusIndicators.neutral;
        }
    }

    return result;
}

export function getReleaseStatusIndicator(
    requestStatus: SeasonChannelRequestStatus | string,
    seasonChannelReleaseStatus: SeasonChannelReleaseStatus | string
): SeasonChannelStatusIndicators {
    let result: SeasonChannelStatusIndicators;

    if (requestStatus === 'REJECTED') {
        result = SeasonChannelStatusIndicators.rejected;
    } else if (requestStatus === 'PENDING_REQUEST' || requestStatus === 'PENDING') {
        result = SeasonChannelStatusIndicators.pending;
    } else {
        switch (seasonChannelReleaseStatus) {
            case 'RELEASE_PENDING':
            case 'PENDING_RELATIONSHIP':
                result = SeasonChannelStatusIndicators.pending;
                break;
            case 'RELEASE_CANCELLED':
                result = SeasonChannelStatusIndicators.cancelled;
                break;
            case 'REJECTED':
                result = SeasonChannelStatusIndicators.rejected;
                break;
            case 'RELEASED':
                result = SeasonChannelStatusIndicators.success;
                break;
            case 'RELEASE_FINISHED':
                result = SeasonChannelStatusIndicators.finished;
                break;
            case 'PLANNED':
            case 'IN_PROGRAMMING':
            case 'CANCELLED':
            case 'NOT_ACCOMPLISHED':
            default:
                result = SeasonChannelStatusIndicators.neutral;
        }
    }

    return result;
}
