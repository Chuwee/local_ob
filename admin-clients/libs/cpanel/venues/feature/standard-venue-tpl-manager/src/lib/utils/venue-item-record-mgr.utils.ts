import { Seat, SeatStatus } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplateLabel, VenueTemplateLabelGroup } from '../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../models/label-group/venue-template-label-group-type.enum';
import { getSeatIdentifierFunction } from './venue-item-managing-functions.utils';

export function setSeatsRecord(
    modifiableSeats: Seat[],
    modifiedSeats: Map<number, Seat>,
    label: VenueTemplateLabel,
    labelGroup: VenueTemplateLabelGroup
): Seat[] {
    return modifiableSeats.map(seat => {
        if (modifiedSeats?.has(seat.id)) {
            const getterFunction = getSeatIdentifierFunction(labelGroup.id);
            const initialSeatState = getterFunction(modifiedSeats.get(seat.id).seatRecord.initialSeat);
            setSeatRecord(seat, label, labelGroup, initialSeatState);
            return seat;
        } else {
            initSeatRecord(seat, labelGroup);
            return seat;
        }
    });
}

function setSeatRecord(
    seat: Seat,
    label: VenueTemplateLabel,
    labelGroup: VenueTemplateLabelGroup,
    initialSeatState: string
): void {
    deleteBlockAndStateSeatRecord(seat, labelGroup);
    if (initialSeatState === label.id) {
        deleteSeatRecord(seat, labelGroup);
    } else {
        addSeatRecord(seat, labelGroup);
    }
}

function deleteBlockAndStateSeatRecord(
    seat: Seat,
    labelGroup: VenueTemplateLabelGroup
): void {
    if (
        labelGroup.id === VenueTemplateLabelGroupType.blockingReason &&
        seat.seatRecord.labelGroupRecord?.has(VenueTemplateLabelGroupType.state) &&
        getSeatIdentifierFunction(VenueTemplateLabelGroupType.state)(seat) !== SeatStatus.promotorLocked
    ) {
        seat.seatRecord.labelGroupRecord.delete(VenueTemplateLabelGroupType.state);
    } else if (
        labelGroup.id === VenueTemplateLabelGroupType.state &&
        seat.seatRecord.labelGroupRecord?.has(VenueTemplateLabelGroupType.blockingReason)
    ) {
        seat.seatRecord.labelGroupRecord.delete(VenueTemplateLabelGroupType.blockingReason);
    }
}

function addSeatRecord(seat: Seat, labelGroup: VenueTemplateLabelGroup): void {
    if (labelGroup.id === VenueTemplateLabelGroupType.blockingReason) {
        seat.seatRecord.labelGroupRecord.add(VenueTemplateLabelGroupType.state);
    }
    seat.seatRecord.labelGroupRecord.add(labelGroup.id);
}

function deleteSeatRecord(
    seat: Seat,
    labelGroup: VenueTemplateLabelGroup): void {
    if (seat.seatRecord.labelGroupRecord?.has(labelGroup.id)) {
        if (labelGroup.id === VenueTemplateLabelGroupType.blockingReason) {
            seat.seatRecord.labelGroupRecord.delete(VenueTemplateLabelGroupType.state);
        }
        seat.seatRecord.labelGroupRecord.delete(labelGroup.id);
    }
    if (seat.seatRecord.labelGroupRecord.size === 0) {
        seat.seatRecord.labelGroupRecord = null;
        seat.seatRecord.initialSeat = null;
        seat.seatRecord = null;
    }
}

function initSeatRecord(seat: Seat, labelGroup: VenueTemplateLabelGroup): void {
    seat.seatRecord = {
        initialSeat: { ...seat },
        labelGroupRecord: new Set<VenueTemplateLabelGroupType>()
    };
    if (labelGroup.id === VenueTemplateLabelGroupType.blockingReason) {
        seat.seatRecord.labelGroupRecord.add(VenueTemplateLabelGroupType.state);
    }
    seat.seatRecord.labelGroupRecord.add(labelGroup.id);
}
