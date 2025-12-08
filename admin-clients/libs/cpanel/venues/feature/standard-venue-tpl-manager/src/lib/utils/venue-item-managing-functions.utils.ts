import {
    Accessibility, NotNumberedZone, Seat, SeatLinkable, SeatLinked, SeatStatus, VenueTemplateItemType, Visibility
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplateLabelGroupType } from '../models/label-group/venue-template-label-group-type.enum';

export function getSeatIdentifierFunction(labelGroupId: string): (seat: Seat) => string {
    switch (labelGroupId) {
        case VenueTemplateLabelGroupType.state:
            return seat => seat.status;
        case VenueTemplateLabelGroupType.blockingReason:
            return seat => String(seat.blockingReason);
        case VenueTemplateLabelGroupType.priceType:
            return seat => String(seat.priceType);
        case VenueTemplateLabelGroupType.quota:
            return seat => String(seat.quota);
        case VenueTemplateLabelGroupType.accessibility:
            return seat => seat.accessibility;
        case VenueTemplateLabelGroupType.visibility:
            return seat => seat.visibility;
        case VenueTemplateLabelGroupType.gate:
            return seat => String(seat.gate);
        case VenueTemplateLabelGroupType.seasonTicketLinkable:
            return seat => seat.linkable;
        case VenueTemplateLabelGroupType.sessionPacks:
            return seat => String(seat.sessionPack);
        case VenueTemplateLabelGroupType.sessionPackLink:
            return seat => seat.linked;
        case VenueTemplateLabelGroupType.firstCustomLabelGroup:
            return seat => String(seat.firstCustomTag);
        case VenueTemplateLabelGroupType.secondCustomLabelGroup:
            return seat => String(seat.secondCustomTag);
    }
}

export function getNNZIdentifierFunction(labelGroupId: string): (nnz: NotNumberedZone) => string {
    switch (labelGroupId) {
        case VenueTemplateLabelGroupType.state:
        case VenueTemplateLabelGroupType.seasonTicketLinkable:
            return nnz => nnz.statusCounters
                && nnz.statusCounters.length === 1
                && nnz.statusCounters[0].status;
        case VenueTemplateLabelGroupType.blockingReason:
            return nnz => nnz.blockingReasonCounters
                && nnz.blockingReasonCounters.length === 1
                && String(nnz.blockingReasonCounters[0].blocking_reason);
        case VenueTemplateLabelGroupType.priceType:
            return nnz => String(nnz.priceType);
        case VenueTemplateLabelGroupType.quota:
            return nnz => nnz.quotaCounters && nnz.quotaCounters.length === 1 && String(nnz.quotaCounters[0].quota);
        case VenueTemplateLabelGroupType.accessibility:
            return nnz => nnz.accessibility;
        case VenueTemplateLabelGroupType.visibility:
            return nnz => nnz.visibility;
        case VenueTemplateLabelGroupType.gate:
            return nnz => String(nnz.gate);
        case VenueTemplateLabelGroupType.sessionPacks:
            return nnz => nnz.sessionPackCounters?.length === 1
                && String(nnz.sessionPackCounters[0].sessionPack);
        case VenueTemplateLabelGroupType.sessionPackLink:
            return nnz => nnz.statusCounters?.some(sc => sc.linked === SeatLinked.linked) ? SeatLinked.linked : SeatLinked.unlinked;
        case VenueTemplateLabelGroupType.firstCustomLabelGroup:
            return nnz => String(nnz.firstCustomTag);
        case VenueTemplateLabelGroupType.secondCustomLabelGroup:
            return nnz => String(nnz.secondCustomTag);
    }
}

export function getAssignLabelToSeatFunction(groupType: VenueTemplateLabelGroupType): (seat: Seat, value: string) => void {
    switch (groupType) {
        case VenueTemplateLabelGroupType.state:
            return (seat: Seat, value: string) => seat.status = Object.values(SeatStatus).find(s => s === value);
        case VenueTemplateLabelGroupType.blockingReason:
            return (seat: Seat, value: string) => seat.blockingReason = Number(value);
        case VenueTemplateLabelGroupType.priceType:
            return (seat: Seat, value: string) => seat.priceType = Number(value);
        case VenueTemplateLabelGroupType.quota:
            return (seat: Seat, value: string) => seat.quota = Number(value);
        case VenueTemplateLabelGroupType.visibility:
            return (seat: Seat, value: string) => seat.visibility = Object.values(Visibility).find(s => s === value);
        case VenueTemplateLabelGroupType.accessibility:
            return (seat: Seat, value: string) => seat.accessibility = Object.values(Accessibility).find(s => s === value);
        case VenueTemplateLabelGroupType.gate:
            return (seat: Seat, value: string) => seat.gate = Number(value);
        case VenueTemplateLabelGroupType.seasonTicketLinkable:
            return (seat: Seat, value: string) => seat.linkable = Object.values(SeatLinkable).find(s => s === value);
        case VenueTemplateLabelGroupType.firstCustomLabelGroup:
            return (seat: Seat, value: string) => seat.firstCustomTag = Number(value);
        case VenueTemplateLabelGroupType.secondCustomLabelGroup:
            return (seat: Seat, value: string) => seat.secondCustomTag = Number(value);
    }
}

export function getAssignLabelToNNZFunction(groupType: VenueTemplateLabelGroupType): (nnz: NotNumberedZone, value: string) => void {
    switch (groupType) {
        case VenueTemplateLabelGroupType.state:
            return (nnz: NotNumberedZone, value: string) =>
                nnz.statusCounters = [
                    {
                        itemType: VenueTemplateItemType.notNumberedZoneStatusCounter,
                        status: Object.values(SeatStatus).find(s => s === value),
                        count: nnz.capacity,
                        linked: SeatLinked.unlinked
                    }];
        case VenueTemplateLabelGroupType.blockingReason:
            return (nnz: NotNumberedZone, value: string) => {
                if (value) {
                    nnz.blockingReasonCounters = [
                        {
                            itemType: VenueTemplateItemType.notNumberedZoneBlockingReasonCounter,
                            blocking_reason: Number(value),
                            count: nnz.capacity
                        }];
                } else {
                    nnz.blockingReasonCounters = null;
                }
            };
        case VenueTemplateLabelGroupType.priceType:
            return (nnz: NotNumberedZone, value: string) => nnz.priceType = Number(value);
        case VenueTemplateLabelGroupType.quota:
            return (nnz: NotNumberedZone, value: string) => {
                nnz.quotaCounters = [
                    {
                        itemType: VenueTemplateItemType.notNumberedZoneQuotaCounters,
                        quota: Number(value),
                        count: nnz.capacity,
                        available: nnz.capacity
                    }];
            };
        case VenueTemplateLabelGroupType.visibility:
            return (nnz: NotNumberedZone, value: string) => nnz.visibility = Object.values(Visibility).find(s => s === value);
        case VenueTemplateLabelGroupType.accessibility:
            return (nnz: NotNumberedZone, value: string) => nnz.accessibility = Object.values(Accessibility).find(s => s === value);
        case VenueTemplateLabelGroupType.gate:
            return (nnz: NotNumberedZone, value: string) => nnz.gate = Number(value);
        case VenueTemplateLabelGroupType.firstCustomLabelGroup: // revisar
            return (nnz: NotNumberedZone, value: string) => nnz.firstCustomTag = Number(value);
        case VenueTemplateLabelGroupType.secondCustomLabelGroup: // revisar
            return (nnz: NotNumberedZone, value: string) => nnz.secondCustomTag = Number(value);
        case VenueTemplateLabelGroupType.seasonTicketLinkable:
            // eslint-disable-next-line unused-imports/no-unused-vars
            return (nnz: NotNumberedZone, value: string) => {
                // do nothing
            };
    }
}
