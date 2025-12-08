import { ObFile } from '@admin-clients/shared/data-access/models';
import {
    GateUpdateType, NotNumberedZone, occupiedStatus, Seat, SeatLinkable, SeatLinked, SeatStatus, Sector, VenueTemplateItemType, VmItemsMap,
    VmItemsSet
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { Injectable } from '@angular/core';
import { combineLatest, map, Observable, take } from 'rxjs';
import { ApplyLabelResult } from '../models/label-group/apply-label-result';
import { VenueTemplateLabel, VenueTemplateLabelGroup } from '../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../models/label-group/venue-template-label-group-type.enum';
import { VenueTemplateEditorType } from '../models/venue-template-editor-type.model';
import { StandardVenueTemplateState } from '../state/standard-venue-template.state';
import {
    getAssignLabelToNNZFunction, getAssignLabelToSeatFunction, getNNZIdentifierFunction, getSeatIdentifierFunction
} from '../utils/venue-item-managing-functions.utils';
import { setSeatsRecord } from '../utils/venue-item-record-mgr.utils';

@Injectable()
export class StandardVenueTemplateChangesService {

    static readonly emptyBlockingReason = '-1';

    constructor(private _venueTplSrv: VenueTemplatesService, private _standardVenueTemplateState: StandardVenueTemplateState) { }

    getModifiedItems$(): Observable<VmItemsSet> {
        return this._standardVenueTemplateState.getModifiedItems$();
    }

    clearModifiedItems(): void {
        this._standardVenueTemplateState.setModifiedItems();
    }

    // image

    setTemplateImage(value: ObFile | string): void {
        this._standardVenueTemplateState.setTemplateImage(value);
    }

    getTemplateImage$(): Observable<ObFile | string> {
        return this._standardVenueTemplateState.getTemplateImage$();
    }

    setModifiedItem(item: Sector | NotNumberedZone, set = true): void {
        this._standardVenueTemplateState.getModifiedItems$().pipe(take(1)).subscribe(modifiedItems => {
            const sectors = modifiedItems.sectors;
            const zones = modifiedItems.nnzs;
            let colToUpdate: Set<number>;
            if (item.itemType === VenueTemplateItemType.sector) {
                colToUpdate = sectors;
            } else if (item.itemType === VenueTemplateItemType.notNumberedZone) {
                colToUpdate = zones;
            } else {
                console.warn('setting not controlled item to modified items: ', item);
                return;
            }
            if (set) {
                colToUpdate.add(item.id);
            } else {
                colToUpdate.delete(item.id);
            }
            this._standardVenueTemplateState.setModifiedItems(modifiedItems);
        });
    }

    applyLabelToSelection(
        label: VenueTemplateLabel,
        editorType?: VenueTemplateEditorType,
        gateUpdateType?: GateUpdateType
    ): Observable<ApplyLabelResult> {
        return combineLatest([
            this._standardVenueTemplateState.getLabelGroups$(),
            this._standardVenueTemplateState.getVenueItems$(),
            this._standardVenueTemplateState.getSelectedVenueItems$(),
            this._standardVenueTemplateState.getModifiedItems$()
        ])
            .pipe(
                take(1),
                map(([labelGroups, venueItems, selectedItems, modifiedItems]) => {
                    let result: ApplyLabelResult;
                    if (selectedItems?.seats?.size || selectedItems?.nnzs?.size) {
                        const newModifiedItems: VmItemsMap = {
                            seats: this.getNewModifiedItems(venueItems.seats, modifiedItems.seats),
                            nnzs: this.getNewModifiedItems(venueItems.nnzs, modifiedItems.nnzs)
                        };
                        // every check could return a result or null, the first result skips the next check or apply
                        if (!result) {
                            result = this.partialApplyChecks(label, labelGroups, venueItems, selectedItems, newModifiedItems);
                        }
                        if (!result) {
                            result = this.seasonTicketLinkableCheck(label, labelGroups, venueItems, selectedItems, newModifiedItems);
                        }
                        if (!result) {
                            result = this.sessionPackLinkCheck(label, labelGroups, venueItems, selectedItems, newModifiedItems);
                        }
                        if (!result) {
                            result = this.simpleApplyLabel(
                                label, labelGroups, venueItems, selectedItems, newModifiedItems, editorType, gateUpdateType);
                        }
                        if (gateUpdateType) {
                            Array.from(newModifiedItems.seats.values())
                                .forEach(seat => seat.gateUpdateType = occupiedStatus.includes(seat.status) ? gateUpdateType : undefined);
                            Array.from(newModifiedItems.nnzs.values())
                                .forEach(nnz => nnz.gateUpdateType = nnz.statusCounters
                                    .some(counter => occupiedStatus.includes(counter.status)) ? gateUpdateType : undefined);
                        }
                        if ((newModifiedItems.seats.size > 0 || modifiedItems?.seats?.size) ||
                            (newModifiedItems.nnzs.size > 0 || modifiedItems?.nnzs?.size)) {
                            this._standardVenueTemplateState.setModifiedItems({
                                seats: new Set<number>(newModifiedItems.seats.keys()),
                                nnzs: new Set<number>(newModifiedItems.nnzs.keys()),
                                sectors: modifiedItems.sectors
                            });
                        }
                    } else {
                        result = ApplyLabelResult.noSelection;
                    }
                    return result;
                }));
    }

    replaceLabelByDefaultLabel(labelGroup: VenueTemplateLabelGroup, deletedLabel: VenueTemplateLabel): void {
        combineLatest([
            this._standardVenueTemplateState.getLabelGroups$(),
            this._standardVenueTemplateState.getVenueItems$()
        ])
            .pipe(take(1))
            .subscribe(([labelGroups, venueItems]) => {
                const defaultLabel = labelGroup.labels.find(groupLabel => groupLabel.default);
                this.internalReplaceByDefaultLabelOnSeats(deletedLabel, defaultLabel, labelGroup, labelGroups, venueItems.seats);
                this.internalReplaceByDefaultLabelOnNNZs(deletedLabel, defaultLabel, labelGroup, labelGroups, venueItems.nnzs);
            });
    }

    removeCustomTagValues(groupType: VenueTemplateLabelGroupType): void {
        combineLatest([
            this._standardVenueTemplateState.getVenueItems$(),
            this._venueTplSrv.secondCustomTagGroupLabels.get$()
        ])
            .pipe(take(1))
            .subscribe(([items, secondCustomTagTroup]) => {
                // If it deletes first custom group having a second one,
                // the second comes the new first, and values goes from the second property to the first.
                if (groupType === VenueTemplateLabelGroupType.firstCustomLabelGroup && secondCustomTagTroup) {
                    items.seats.forEach(seat => {
                        seat.firstCustomTag = seat.secondCustomTag;
                        seat.secondCustomTag = -1;
                    });
                    items.nnzs.forEach(nnz => {
                        nnz.firstCustomTag = nnz.secondCustomTag;
                        nnz.secondCustomTag = -1;
                    });
                } else {
                    const seatSetter = getAssignLabelToSeatFunction(groupType);
                    const nnzSetter = getAssignLabelToNNZFunction(groupType);
                    items.seats.forEach(seat => seatSetter(seat, '-1'));
                    items.nnzs.forEach(nnz => nnzSetter(nnz, '-1'));
                }
            });
    }

    private getNewModifiedItems<T = Seat | NotNumberedZone>(venueItems: Map<number, T>, modifiedItems: Set<number>): Map<number, T> {
        const result = new Map<number, T>();
        Array.from(modifiedItems || []).forEach(id => result.set(id, venueItems.get(id)));
        return result;
    }

    private internalReplaceByDefaultLabelOnSeats(
        deletedLabel: VenueTemplateLabel,
        defaultLabel: VenueTemplateLabel,
        labelGroup: VenueTemplateLabelGroup,
        labelGroups: VenueTemplateLabelGroup[],
        seats: Map<number, Seat>
    ): void {
        const getterFunction = getSeatIdentifierFunction(labelGroup.id);
        const itemToModify: Seat[] = [];
        seats.forEach(seat => {
            if (!deletedLabel || getterFunction(seat) === deletedLabel.id) {
                itemToModify.push(seat);
            }
        });

        this.applyLabelToSeats(
            defaultLabel,
            labelGroup,
            labelGroups,
            itemToModify
        );
    }

    private internalReplaceByDefaultLabelOnNNZs(
        deletedLabel: VenueTemplateLabel,
        defaultLabel: VenueTemplateLabel,
        labelGroup: VenueTemplateLabelGroup,
        labelGroups: VenueTemplateLabelGroup[],
        nnzs: Map<number, NotNumberedZone>
    ): void {
        const getterFunction = getNNZIdentifierFunction(labelGroup.id);
        const itemToModify: NotNumberedZone[] = [];
        if (!deletedLabel) {
            nnzs.forEach(nnz => itemToModify.push(nnz));
        } else {
            nnzs.forEach(nnz => {
                if (getterFunction(nnz) === deletedLabel?.id) {
                    itemToModify.push(nnz);
                }
            });
        }
        this.applyLabelToNNZs(
            defaultLabel,
            labelGroup,
            labelGroups,
            itemToModify
        );
    }

    private partialApplyChecks(
        label: VenueTemplateLabel, labelGroups: VenueTemplateLabelGroup[],
        venueItems: VmItemsMap, selectedItems: VmItemsSet,
        modifiedItems: VmItemsMap
    ): ApplyLabelResult {
        const labelGroup = labelGroups.find(lg => lg.id === label.labelGroupId);
        const hasSeatsSelected = selectedItems.seats?.size > 0;
        const hasNNZsSelected = selectedItems.nnzs?.size > 0;
        const hasOneNNZSelected = selectedItems.nnzs?.size === 1;
        if (labelGroup.nnzPartialApply) { // partial apply required check
            if (!hasSeatsSelected && !hasNNZsSelected) {
                return ApplyLabelResult.noChanges;
            } else if (hasSeatsSelected && !hasNNZsSelected) {
                let modifiableSeats = this.getModifiableSeats(venueItems.seats, selectedItems.seats, labelGroups, labelGroup);
                modifiableSeats = setSeatsRecord(modifiableSeats, modifiedItems.seats, label, labelGroup);
                this.applyLabelToModifiableSeats(labelGroups, label, modifiableSeats, modifiedItems.seats);
                if (modifiableSeats.length === 0) {
                    return ApplyLabelResult.cannotApply;
                } else if (modifiableSeats.length === (selectedItems.seats || new Set()).size) {
                    return ApplyLabelResult.ok;
                } else {
                    return ApplyLabelResult.partialApply;
                }
            } else if (hasOneNNZSelected && !hasSeatsSelected) {
                // checks that the label to apply is not fully applied at all
                const selectedNNZ = venueItems.nnzs.get(Array.from(selectedItems.nnzs)[0]);
                if (labelGroup.id === VenueTemplateLabelGroupType.state) {
                    if (selectedNNZ.statusCounters.find(sc =>
                        sc.status === label.id && sc.count === selectedNNZ.capacity)) {
                        return ApplyLabelResult.noChanges;
                    }
                    return ApplyLabelResult.nnzStatusOrBRPartialApply;
                } else if (labelGroup.id === VenueTemplateLabelGroupType.blockingReason) {
                    if (selectedNNZ.blockingReasonCounters.find(brc =>
                        brc.blocking_reason.toString() === label.id && brc.count === selectedNNZ.capacity)) {
                        return ApplyLabelResult.noChanges;
                    }
                    return ApplyLabelResult.nnzStatusOrBRPartialApply;
                } else if (labelGroup.id === VenueTemplateLabelGroupType.quota) {
                    return ApplyLabelResult.nnzQuotaPartialApply;
                }
            } else {
                return ApplyLabelResult.invalidNnzSelection;
            }
        }
        return null;
    }

    private seasonTicketLinkableCheck(
        label: VenueTemplateLabel, labelGroups: VenueTemplateLabelGroup[],
        venueItems: VmItemsMap, selectedItems: VmItemsSet,
        modifiedItems: VmItemsMap
    ): ApplyLabelResult {
        const labelGroup = labelGroups.find(lg => lg.id === label.labelGroupId);
        const hasSeatsSelected = selectedItems.seats?.size > 0;
        const hasNNZsSelected = selectedItems.nnzs?.size > 0;
        const hasOneNNZSelected = selectedItems.nnzs?.size === 1;
        if (labelGroup.isLinkableGroup && labelGroup.id === VenueTemplateLabelGroupType.seasonTicketLinkable) {
            if (!hasSeatsSelected && !hasNNZsSelected) {
                return ApplyLabelResult.noChanges;
            } else if (hasSeatsSelected && !hasNNZsSelected) {
                const seatLinkable = Object.values<string>(SeatLinkable);
                if (seatLinkable.includes(label.id)) {
                    return null;
                }
            } else if (hasOneNNZSelected && !hasSeatsSelected) {
                if (modifiedItems.seats.size || modifiedItems.nnzs.size) {
                    return ApplyLabelResult.cannotApplyWithPendingChanges;
                }

                const selectedNNZ = venueItems.nnzs.get(Array.from(selectedItems.nnzs)[0]);
                if (label.id === SeatLinkable.linkable) {
                    if (selectedNNZ.linkableSeats === selectedNNZ.capacity) {
                        return ApplyLabelResult.noChanges;
                    }
                } else if (label.id === SeatLinkable.notLinkable) {
                    if (selectedNNZ.linkableSeats === 0) {
                        return ApplyLabelResult.noChanges;
                    }
                }
                return ApplyLabelResult.seasonTicketLinkableApply;
            } else {
                return ApplyLabelResult.invalidNnzSelection;
            }
        }

        return null;
    }

    private sessionPackLinkCheck(
        label: VenueTemplateLabel, labelGroups: VenueTemplateLabelGroup[],
        venueItems: VmItemsMap, selectedItems: VmItemsSet,
        modifiedItems: VmItemsMap
    ): ApplyLabelResult {
        const labelGroup = labelGroups.find(lg => lg.id === label.labelGroupId);
        const hasSeatsSelected = selectedItems.seats?.size > 0;
        const hasNNZsSelected = selectedItems.nnzs?.size > 0;
        const hasOneNNZSelected = selectedItems.nnzs?.size === 1;
        if (labelGroup.isLinkableGroup && labelGroup.id === VenueTemplateLabelGroupType.sessionPackLink) {
            if (!hasSeatsSelected && !hasNNZsSelected) {
                return ApplyLabelResult.noChanges;
            } else if (modifiedItems.seats.size || modifiedItems.nnzs.size) {
                return ApplyLabelResult.cannotApplyWithPendingChanges;
            } else if (hasSeatsSelected && !hasNNZsSelected) {
                let modifiableSeats = this.getModifiableSeats(venueItems.seats, selectedItems.seats, labelGroups, labelGroup);
                if (label.id === SeatLinked.linked) {
                    modifiableSeats = modifiableSeats.filter(seat => seat.linked === SeatLinked.unlinked);
                } else {
                    modifiableSeats = modifiableSeats.filter(seat => seat.linked === SeatLinked.linked);
                }
                if (modifiableSeats.length === 0) {
                    return ApplyLabelResult.cannotApply;
                } else {
                    return ApplyLabelResult.sessionPackLinkApply;
                }
            } else if (hasOneNNZSelected && !hasSeatsSelected) {
                // checks that the label to apply is not fully applied
                const selectedNNZ = venueItems.nnzs.get(Array.from(selectedItems.nnzs)[0]);
                if (label.id === SeatLinked.linked) {
                    if (!selectedNNZ.statusCounters.find(sc => sc.linked === SeatLinked.unlinked)) {
                        return ApplyLabelResult.noChanges;
                    }
                } else { //label.id === SeatLinked.unlinked
                    if (selectedNNZ.statusCounters
                        .filter(sc => sc.linked === SeatLinked.unlinked
                            || sc.status === SeatStatus.sold
                            || sc.status === SeatStatus.emitted
                            || sc.status === SeatStatus.gift
                        )
                        .map(sc => sc.count)
                        .reduce((prevVal, currentVal) => prevVal + currentVal, 0) === selectedNNZ.capacity) {
                        return ApplyLabelResult.noChanges;
                    }
                }
                return ApplyLabelResult.sessionPackLinkApply;
            } else {
                return ApplyLabelResult.invalidNnzSelection;
            }
        }
        return null;
    }

    private simpleApplyLabel(
        label: VenueTemplateLabel, labelGroups: VenueTemplateLabelGroup[],
        venueItems: VmItemsMap, selectedItems: VmItemsSet,
        modifiedItems: VmItemsMap, editorType: VenueTemplateEditorType, gateSpreadStrategy: GateUpdateType
    ): ApplyLabelResult {
        const labelGroup = labelGroups.find(lg => lg.id === label.labelGroupId);
        const hasSeatsSelected = selectedItems.seats?.size > 0;
        const hasNNZsSelected = selectedItems.nnzs?.size > 0;
        if (!hasSeatsSelected && !hasNNZsSelected) {
            return ApplyLabelResult.noChanges;
        } else {
            let modifiableSeats = this.getModifiableSeats(venueItems.seats, selectedItems.seats, labelGroups, labelGroup);
            modifiableSeats = setSeatsRecord(modifiableSeats, modifiedItems.seats, label, labelGroup);
            const modifiableNNZs = this.getModifiableNNZs(venueItems.nnzs, selectedItems.nnzs, labelGroups, labelGroup);
            if (labelGroup.id === VenueTemplateLabelGroupType.gate && [
                VenueTemplateEditorType.sessionTemplate,
                VenueTemplateEditorType.sessionPackTemplate,
                VenueTemplateEditorType.seasonTicketTemplate
            ].includes(editorType)
                && !gateSpreadStrategy && ((modifiableSeats?.some(seat => !seat.gateUpdateType && occupiedStatus.includes(seat.status)))
                    || modifiableNNZs?.some(nnz => !nnz.gateUpdateType && nnz.statusCounters.some(c => occupiedStatus.includes(c.status))))
            ) {
                return ApplyLabelResult.gateSpreadStrategyReq;
            }
            this.applyLabelToModifiableSeats(labelGroups, label, modifiableSeats, modifiedItems.seats);
            this.applyLabelToModifiableNNZs(labelGroups, label, modifiableNNZs, modifiedItems.nnzs);
            const modifiedElementsLength = modifiableSeats.length + modifiableNNZs.length;
            const selectedElementsLength = (selectedItems.seats || new Set()).size + (selectedItems.nnzs || new Set()).size;
            if (modifiedElementsLength === selectedElementsLength) {
                return ApplyLabelResult.ok;
            } else if (modifiedElementsLength === 0) {
                return ApplyLabelResult.cannotApply;
            } else { // modifiedElementsLength !== selectedElementsLength
                return ApplyLabelResult.partialApply;
            }
        }
    }

    private getModifiableSeats(
        seats: Map<number, Seat>,
        selectedSeatIds: Set<number>,
        labelGroups: VenueTemplateLabelGroup[],
        selectedLabelGroup: VenueTemplateLabelGroup
    ): Seat[] {
        if (selectedSeatIds?.size) {
            const editableSeatStatus = this.getEditableSeatStatus(labelGroups, selectedLabelGroup);
            return Array.from(selectedSeatIds)
                .map(seatId => seats.get(seatId))
                .filter(seat => editableSeatStatus.has(seat.status) || seat.status === null);
        } else {
            return [];
        }
    }

    private getEditableSeatStatus(
        labelGroups: VenueTemplateLabelGroup[],
        selectedLabelGroup: VenueTemplateLabelGroup
    ): Set<SeatStatus> {
        return new Set<SeatStatus>(
            labelGroups
                .find(labelGroup => labelGroup.id === VenueTemplateLabelGroupType.state)
                .labels
                ?.filter(label => label.editableStatus
                    || label.notModifiableGroups?.every(labelGroupType => labelGroupType !== selectedLabelGroup.id)
                )
                .map(label => label.id as SeatStatus)
        );
    }

    private applyLabelToModifiableSeats(
        labelGroups: VenueTemplateLabelGroup[],
        label: VenueTemplateLabel,
        seats: Seat[],
        modifiedSeats: Map<number, Seat>
    ): void {
        if (seats?.length) {
            const selectedLabelGroup = labelGroups.find(labelGroup => labelGroup.id === label.labelGroupId);
            if (selectedLabelGroup.id === VenueTemplateLabelGroupType.blockingReason) {
                // if it is assigning a blocking reason, first assigns the blocked state to seats
                const stateLabelGroup = labelGroups.find(labelGroup => labelGroup.id === VenueTemplateLabelGroupType.state);
                const promotorLockedLabel = stateLabelGroup.labels.find(stateLabel => stateLabel.id === SeatStatus.promotorLocked);
                // venue venue-templates doesn't manage blocking reasons
                if (promotorLockedLabel) {
                    this.applyLabelToSeats(promotorLockedLabel, stateLabelGroup, labelGroups, seats, modifiedSeats);
                }
            } else if (selectedLabelGroup.id === VenueTemplateLabelGroupType.state) {
                // if it is assigning state label, first unasigns the blocking reason to let it empty.
                const blockingReasonLabelGroup
                    = labelGroups.find(labelGroup => labelGroup.id === VenueTemplateLabelGroupType.blockingReason);
                // venue venue-templates doesn't manage blocking reasons
                if (blockingReasonLabelGroup) {
                    const emptyBlockingReasonLabel = {
                        id: StandardVenueTemplateChangesService.emptyBlockingReason,
                        labelGroupId: VenueTemplateLabelGroupType.blockingReason,
                        count: 0
                    };
                    this.applyLabelToSeats(emptyBlockingReasonLabel, blockingReasonLabelGroup, labelGroups, seats, modifiedSeats);
                }
            }
            this.applyLabelToSeats(
                label,
                selectedLabelGroup,
                labelGroups,
                seats,
                modifiedSeats);
            this.deleteModifiedSeatsWithoutHistory(modifiedSeats);
            seats?.forEach(seat => seat.notAssignableReason = null);
        }
    }

    private applyLabelToSeats(
        label: VenueTemplateLabel,
        selectedLabelGroup: VenueTemplateLabelGroup,
        labelGroups: VenueTemplateLabelGroup[],
        seats: Seat[],
        modifiedSeats?: Map<number, Seat>
    ): void {
        const getterFunction = getSeatIdentifierFunction(selectedLabelGroup.id);
        const assignFunction = getAssignLabelToSeatFunction(selectedLabelGroup.id);
        const labelGroupLabelMap = this.getLabelGroupLabelMap(label, selectedLabelGroup, labelGroups);
        seats.forEach(item => {
            const currentValue = getterFunction(item);
            if (currentValue !== label.id) {
                if (labelGroupLabelMap.has(currentValue)) {
                    labelGroupLabelMap.get(currentValue).count--;
                }
                assignFunction(item, label.id);
                label.count++;
                modifiedSeats?.set(item.id, item);
            }
        });
    }

    private deleteModifiedSeatsWithoutHistory(modifiedSeats: Map<number, Seat>): void {
        if (modifiedSeats) {
            modifiedSeats.forEach(modifiedSeat => {
                if (!modifiedSeat.seatRecord) {
                    modifiedSeats.delete(modifiedSeat.id);
                }
            });
        }
    }

    private getLabelGroupLabelMap(
        label: VenueTemplateLabel,
        selectedLabelGroup: VenueTemplateLabelGroup,
        labelGroups: VenueTemplateLabelGroup[]
    ): Map<string, VenueTemplateLabel> {
        const labelGroupLabelMap = new Map<string, VenueTemplateLabel>();
        labelGroups.find(labelGroup => labelGroup.id === selectedLabelGroup.id)
            .labels.forEach(lbl => labelGroupLabelMap.set(lbl.id, lbl));
        if (label?.id === StandardVenueTemplateChangesService.emptyBlockingReason) {
            labelGroupLabelMap.set(StandardVenueTemplateChangesService.emptyBlockingReason, label);
        } else if (selectedLabelGroup.id === VenueTemplateLabelGroupType.blockingReason) {
            labelGroupLabelMap.set(
                StandardVenueTemplateChangesService.emptyBlockingReason,
                {
                    id: StandardVenueTemplateChangesService.emptyBlockingReason,
                    count: 0
                } as VenueTemplateLabel
            );
        }
        return labelGroupLabelMap;
    }

    private applyLabelToModifiableNNZs(
        labelGroups: VenueTemplateLabelGroup[],
        label: VenueTemplateLabel,
        nnzs: NotNumberedZone[],
        modifiedNNZs: Map<number, NotNumberedZone>
    ): void {
        if (nnzs?.length) {
            const selectedLabelGroup = labelGroups.find(labelGroup => labelGroup.id === label.labelGroupId);
            // must check if the assignment have to set locked state or remove blocking reason
            if (selectedLabelGroup.id === VenueTemplateLabelGroupType.blockingReason) {
                const stateLabelGroup
                    = labelGroups.find(labelGroup => labelGroup.id === VenueTemplateLabelGroupType.state);
                const blockedStateLabel = stateLabelGroup.labels.find(stateLabel => stateLabel.id === SeatStatus.promotorLocked);
                // venue venue-templates hasnt locked status
                if (blockedStateLabel) {
                    this.applyLabelToNNZs(
                        blockedStateLabel,
                        stateLabelGroup,
                        labelGroups,
                        nnzs,
                        modifiedNNZs
                    );
                }
            } else if (selectedLabelGroup.id === VenueTemplateLabelGroupType.state) {
                const blockingReasonLabelGrpoup
                    = labelGroups.find(labelGroup => labelGroup.id === VenueTemplateLabelGroupType.blockingReason);
                // venue venue-templates hasnt blocking reasons
                if (blockingReasonLabelGrpoup) {
                    this.applyLabelToNNZs(
                        { id: StandardVenueTemplateChangesService.emptyBlockingReason, count: 0 } as VenueTemplateLabel,
                        blockingReasonLabelGrpoup,
                        labelGroups,
                        nnzs,
                        modifiedNNZs
                    );
                }
            }
            this.applyLabelToNNZs(
                label,
                selectedLabelGroup,
                labelGroups,
                nnzs,
                modifiedNNZs);
        }
    }

    private applyLabelToNNZs(
        label: VenueTemplateLabel,
        selectedLabelGroup: VenueTemplateLabelGroup,
        labelGroups: VenueTemplateLabelGroup[],
        nNZs: NotNumberedZone[],
        modifiedNNZs?: Map<number, NotNumberedZone>
    ): void {
        const getterFunction = getNNZIdentifierFunction(selectedLabelGroup.id);
        const assignFunction = getAssignLabelToNNZFunction(selectedLabelGroup.id);
        const labelGroupLabelMap = this.getLabelGroupLabelMap(label, selectedLabelGroup, labelGroups);
        nNZs.forEach(nnz => {
            const currentValue = getterFunction(nnz);
            if (currentValue !== label.id) {
                const nnzEditableCapacity
                    = nnz.capacity - (nnz.statusCounters.find(sc => sc.status === SeatStatus.seasonLocked)?.count || 0);
                if (labelGroupLabelMap.has(currentValue)) {
                    labelGroupLabelMap.get(currentValue).count -= nnzEditableCapacity;
                }
                assignFunction(nnz, label.id !== StandardVenueTemplateChangesService.emptyBlockingReason && label.id);
                label.count += nnzEditableCapacity;
                modifiedNNZs?.set(nnz.id, nnz);
            }
        });
    }

    private getModifiableNNZs(
        nnzs: Map<number, NotNumberedZone>,
        selectedNNZIds: Set<number>,
        labelGroups: VenueTemplateLabelGroup[],
        selectedLabelGroup: VenueTemplateLabelGroup
    ): NotNumberedZone[] {
        if (selectedNNZIds?.size) {
            const editableStatus = this.getEditableSeatStatus(labelGroups, selectedLabelGroup);
            return Array.from(selectedNNZIds)
                .map(nnzId => nnzs.get(nnzId))
                .filter(nnz => !nnz.statusCounters?.length || nnz.statusCounters.some(counter => editableStatus.has(counter.status)));
        } else {
            return [];
        }
    }
}
