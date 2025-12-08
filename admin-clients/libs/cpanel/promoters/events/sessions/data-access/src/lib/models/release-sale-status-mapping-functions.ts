import { SessionGenerationStatus } from './session-generation-status.enum';
import { SessionReleaseStatus } from './session-release-status.enum';
import { SessionSaleStatus } from './session-sale-status.enum';
import { SessionStatusIndicators } from './session-status-indicators.enum';
import { SessionStatus } from './session-status.enum';
import { Session } from './session.model';

export function getSaleStatusIndicator(session: Session): SessionStatusIndicators {
    let result: SessionStatusIndicators;
    const sessionStatus = session.status;
    const sessionSaleStatus = session.sale;
    const sessionGenerationStatus = session.generation_status;
    const publicationCancelledReason = session.publication_cancelled_reason;
    const releaseEnabled = session.release_enabled;

    if (sessionGenerationStatus === SessionGenerationStatus.error &&
        (sessionStatus === SessionStatus.scheduled || sessionStatus === SessionStatus.ready)) {
        result = SessionStatusIndicators.finished;
    } else if (sessionStatus === SessionStatus.preview && sessionSaleStatus === SessionSaleStatus.sale) {
        result = SessionStatusIndicators.preview;
    } else if (publicationCancelledReason && !releaseEnabled) {
        result = SessionStatusIndicators.disabled;
    } else {
        switch (sessionSaleStatus) {
            case SessionSaleStatus.salePending:
            case SessionSaleStatus.pendingSaleCancelled:
                result = SessionStatusIndicators.pending;
                break;
            case SessionSaleStatus.saleCancelled:
                result = SessionStatusIndicators.cancelled;
                break;
            case SessionSaleStatus.sale:
                result = SessionStatusIndicators.success;
                break;
            case SessionSaleStatus.saleFinished:
                result = SessionStatusIndicators.finished;
                break;
            case SessionSaleStatus.planned:
            case SessionSaleStatus.inProgramming:
            case SessionSaleStatus.cancelled:
            case SessionSaleStatus.notAccomplished:
            default:
                result = SessionStatusIndicators.neutral;
        }
    }

    return result;
}

export function getReleaseStatusIndicator(session: Session): SessionStatusIndicators {
    let result: SessionStatusIndicators;

    if (session) {
        const sessionStatus = session.status;
        const sessionReleaseStatus = session.release;
        const sessionGenerationStatus = session.generation_status;
        const publicationCancelledReason = session.publication_cancelled_reason;
        const releaseEnabled = session.release_enabled;

        if (sessionGenerationStatus === SessionGenerationStatus.error &&
            (sessionStatus === SessionStatus.scheduled || sessionStatus === SessionStatus.ready)) {
            result = SessionStatusIndicators.finished;
        } else if (sessionStatus === SessionStatus.preview && sessionReleaseStatus === SessionReleaseStatus.released) {
            result = SessionStatusIndicators.preview;
        } else if (publicationCancelledReason && !releaseEnabled) {
            result = SessionStatusIndicators.disabled;
        } else {
            switch (sessionReleaseStatus) {
                case SessionReleaseStatus.releasePending:
                    result = SessionStatusIndicators.pending;
                    break;
                case SessionReleaseStatus.releaseCancelled:
                    result = SessionStatusIndicators.cancelled;
                    break;
                case SessionReleaseStatus.released:
                    result = SessionStatusIndicators.success;
                    break;
                case SessionReleaseStatus.releaseFinished:
                    result = SessionStatusIndicators.finished;
                    break;
                case SessionReleaseStatus.planned:
                case SessionReleaseStatus.inProgramming:
                case SessionReleaseStatus.cancelled:
                case SessionReleaseStatus.notAccomplished:
                default:
                    result = SessionStatusIndicators.neutral;
            }
        }
    }

    return result;
}
