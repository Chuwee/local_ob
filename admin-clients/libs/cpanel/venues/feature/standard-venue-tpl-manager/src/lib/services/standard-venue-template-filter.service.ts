import { NotNumberedZone, Seat, SeatLinkable, SeatStatus } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { Injectable } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { VenueTemplateLabel, VenueTemplateLabelGroup } from '../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../models/label-group/venue-template-label-group-type.enum';
import { StandardVenueTemplateState } from '../state/standard-venue-template.state';
import { getNNZIdentifierFunction, getSeatIdentifierFunction } from '../utils/venue-item-managing-functions.utils';

@Injectable()
export class StandardVenueTemplateFilterService {

    constructor(private _standardVenueTemplateState: StandardVenueTemplateState) { }

    getFilteredVenueItems$(): Observable<{ seats: Set<number>; nnzs: Set<number> }> {
        return this._standardVenueTemplateState.getFilteredVenueItems$();
    }

    isFiltering$(): Observable<boolean> {
        return this._standardVenueTemplateState.getFilteredLabels$().pipe(map(labels => !!labels?.length));
    }

    updateFilteredLabels(): void {
        combineLatest([
            this._standardVenueTemplateState.getLabelGroups$(),
            this._standardVenueTemplateState.getFilteredLabels$()
        ])
            .pipe(take(1))
            .subscribe(([labelGroups, prevFilteredLabels]) => {
                let newFilteredLabels: VenueTemplateLabel[] = null;
                if (prevFilteredLabels?.length) {
                    newFilteredLabels = prevFilteredLabels.map(prevFilteredLabel =>
                        labelGroups.find(labelGroup => labelGroup.id === prevFilteredLabel.labelGroupId)
                            ?.labels.find(label => label.id === prevFilteredLabel.id)
                    );
                    newFilteredLabels = newFilteredLabels.filter(label => !!label);
                    newFilteredLabels.forEach(label => label.filtering = true);
                }
                this._standardVenueTemplateState.setFilteredLabels(newFilteredLabels);
            });
    }

    filterLabel(selectedLabel: VenueTemplateLabel = null): void {
        combineLatest([
            this._standardVenueTemplateState.getLabelGroups$(),
            this._standardVenueTemplateState.getFilteredLabels$(),
            this._standardVenueTemplateState.getVenueItems$(),
            this._standardVenueTemplateState.getSelectedVenueItems$()
        ])
            .pipe(take(1))
            .subscribe(([labelGroups, filteredLabels, venueItems]) => {
                const newFilteredLabels = (!selectedLabel && (filteredLabels || []))
                    || this.getFilteredLabels(selectedLabel, labelGroups, filteredLabels);
                this._standardVenueTemplateState.setFilteredLabels(newFilteredLabels);
                this._standardVenueTemplateState.setFilteredVenueItems(
                    {
                        seats: this.getFilteredSeats(labelGroups, newFilteredLabels, venueItems.seats),
                        nnzs: this.getFilteredNotNumberedZones(labelGroups, newFilteredLabels, venueItems.nnzs)
                    });
            });
    }

    private getFilteredLabels(
        selectedLabel: VenueTemplateLabel, labelGroups: VenueTemplateLabelGroup[], labels: VenueTemplateLabel[]
    ): VenueTemplateLabel[] {
        const selectedLabelLabelGroup = labelGroups.find(labelGroup => labelGroup.id === selectedLabel.labelGroupId);
        const newLabels: VenueTemplateLabel[] = [];
        if (labels) {
            labels.forEach(label => {
                const labelLabelGroup = labelGroups.find(labelGroup => labelGroup.id === label.labelGroupId);
                if (labelLabelGroup !== selectedLabelLabelGroup) {
                    newLabels.push(label);
                } else {
                    label.filtering = false;
                    if (label.id === selectedLabel.id) {
                        selectedLabel = null;
                    }
                }
            });
        }
        if (selectedLabel !== null) {
            selectedLabel.filtering = true;
            newLabels.push(selectedLabel);
        }
        return newLabels;
    }

    private getFilteredSeats(
        labelGroups: VenueTemplateLabelGroup[], filteredLabels: VenueTemplateLabel[], seats: Map<number, Seat>
    ): Set<number> {
        const filteredSeats = new Set<number>();
        filteredLabels.forEach(filteredLabel => {
            const labelGroup = labelGroups.find(lg => lg.id === filteredLabel.labelGroupId);
            const tempSeatIdentifierFunction = getSeatIdentifierFunction(labelGroup.id);
            let seatIdentifierFunction: (seat: Seat) => string;
            if (labelGroup.id === VenueTemplateLabelGroupType.blockingReason) {
                const seatStatusIdentifierFunction = getSeatIdentifierFunction(VenueTemplateLabelGroupType.state);
                seatIdentifierFunction = (seat: Seat) => seatStatusIdentifierFunction(seat) === SeatStatus.promotorLocked
                    && tempSeatIdentifierFunction(seat) || null;
            } else {
                seatIdentifierFunction = tempSeatIdentifierFunction;
            }
            seats.forEach(seat => {
                if (!filteredSeats.has(seat.id) && seatIdentifierFunction(seat) !== filteredLabel.id) {
                    filteredSeats.add(seat.id);
                }
            });
        });
        return filteredSeats;
    }

    private getFilteredNotNumberedZones(
        labelGroups: VenueTemplateLabelGroup[], filteredLabels: VenueTemplateLabel[], nnzs: Map<number, NotNumberedZone>
    ): Set<number> {
        const filteredNNZs = new Set<number>();
        filteredLabels.forEach(filteredLabel => {
            const labelGroup = labelGroups.find(lg => lg.id === filteredLabel.labelGroupId);
            if (labelGroup) {
                if (labelGroup.id === VenueTemplateLabelGroupType.blockingReason) {
                    nnzs.forEach(nnz => {
                        if (!filteredNNZs.has(nnz.id)
                            && nnz.blockingReasonCounters
                            && !nnz.blockingReasonCounters.find(brc =>
                                brc.count > 0 && brc.blocking_reason === Number(filteredLabel.id))) {
                            filteredNNZs.add(nnz.id);
                        }
                    });
                } else if (labelGroup.id === VenueTemplateLabelGroupType.state) {
                    nnzs.forEach(nnz => {
                        if (!filteredNNZs.has(nnz.id)
                            && nnz.statusCounters
                            && !nnz.statusCounters.find(sc =>
                                sc.count > 0 && sc.status === filteredLabel.id)) {
                            filteredNNZs.add(nnz.id);
                        }
                    });
                } else if (labelGroup.id === VenueTemplateLabelGroupType.quota) {
                    nnzs.forEach(nnz => {
                        if (!filteredNNZs.has(nnz.id)
                            && nnz.quotaCounters
                            && !nnz.quotaCounters.find(sc =>
                                sc.count > 0 && sc.quota.toString() === filteredLabel.id)) {
                            filteredNNZs.add(nnz.id);
                        }
                    });
                } else if (labelGroup.id === VenueTemplateLabelGroupType.seasonTicketLinkable) {
                    this.setFilteredLinkableNNZ(filteredLabel, nnzs, filteredNNZs);
                } else {
                    const nnzIdentifierFunction = getNNZIdentifierFunction(labelGroup.id);
                    nnzs.forEach(nnz => {
                        if (!filteredNNZs.has(nnz.id)) {
                            if (nnzIdentifierFunction(nnz) !== filteredLabel.id) {
                                filteredNNZs.add(nnz.id);
                            }
                        }
                    });
                }
            }
        });
        return filteredNNZs;
    }

    private setFilteredLinkableNNZ(filteredLabel: VenueTemplateLabel, nnzs: Map<number, NotNumberedZone>, filteredNNZs: Set<number>): void {
        if (filteredLabel.id === SeatLinkable.linkable) {
            nnzs.forEach(nnz => {
                if (!filteredNNZs.has(nnz.id) && nnz.linkableSeats === 0) {
                    filteredNNZs.add(nnz.id);
                }
            });
        } else if (filteredLabel.id === SeatLinkable.notLinkable) {
            nnzs.forEach(nnz => {
                if (!filteredNNZs.has(nnz.id) && (nnz.capacity - nnz.linkableSeats) === 0) {
                    filteredNNZs.add(nnz.id);
                }
            });
        } else if (filteredLabel.id === SeatLinkable.notAssignable) {
            //notAssignable doesn't exist in nnz but in seats. It's an ephimeral state
            nnzs.forEach(nnz => filteredNNZs.add(nnz.id));
        }
    }
}
