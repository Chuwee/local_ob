import {
    NotNumberedZone, Seat, SeatLinkable, SeatLinked, SeatStatus, Sector, StdVenueTplService, VENUE_MAP_SERVICE, VenueMap
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { DestroyRef, inject, Injectable, OnDestroy } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { combineLatest, Observable, Subject } from 'rxjs';
import { take } from 'rxjs/operators';
import { VenueTemplateLabel, VenueTemplateLabelGroup } from '../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../models/label-group/venue-template-label-group-type.enum';
import { RelocationStatus } from '../models/rellocation-status.model';
import { VenueTemplateAction } from '../models/venue-template-action.model';
import { VenueTemplateEditorState } from '../models/venue-template-editor-state.enum';
import { VenueTemplateEditorView } from '../models/venue-template-editor-view.enum';
import { StandardVenueTemplateState } from '../state/standard-venue-template.state';

@Injectable()
export class StandardVenueTemplateBaseService implements OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #standardVenueTemplateState = inject(StandardVenueTemplateState);
    readonly #stdVenueTplSrv = inject(StdVenueTplService);
    readonly #venueMapSrv = inject(VENUE_MAP_SERVICE, { optional: true }) || this.#stdVenueTplSrv;

    readonly #action = new Subject<VenueTemplateAction>();
    //refresh view counters
    readonly #refreshViewCountersTrigger = new Subject<void>();

    #includeUnlinkedEl: boolean = null;

    readonly action = this.#action.asObservable();

    readonly relocation = Object.freeze({
        getStatus$: () => this.#standardVenueTemplateState.relocationStatus.getValue$(),
        updateStatus: (value: Partial<RelocationStatus>) =>
            this.#standardVenueTemplateState.relocationStatus.getValue$().pipe(take(1))
                .subscribe(prevValue => this.#standardVenueTemplateState.relocationStatus.setValue({ ...prevValue, ...value }))
    });

    constructor() {
        // this._venueMapSrv ??= this._stdVenueTplSrv;
        this.#venueMapSrv.getVenueMap$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(venueMap => this.indexVenueMap(venueMap));
    }

    ngOnDestroy(): void {
        this.clearVenueMapItems();
    }

    updateVenueMapElements(): void {
        combineLatest([
            this.#standardVenueTemplateState.getLabelGroups$(),
            this.#venueMapSrv.getVenueMap$()
        ])
            .pipe(take(1))
            .subscribe(([labelGroups, venueMap]) => {
                this.setLabelsCounter(labelGroups, venueMap);
                this.indexVenueMap(venueMap);
            });
    }

    clearVenueMapItems(): void {
        this.#standardVenueTemplateState.setVenueItems({ seats: null, nnzs: null });
        this.#standardVenueTemplateState.setTemplateImage(null);
        this.#standardVenueTemplateState.setFilteredLabels(null);
        this.#standardVenueTemplateState.setFilteredVenueItems({ seats: null, nnzs: null });
        this.#standardVenueTemplateState.getSelectedVenueItems$()
            .pipe(take(1))
            .subscribe(selection => {
                selection.nnzs.clear();
                selection.seats.clear();
            });
        this.#standardVenueTemplateState.enqueueSelection({
            select: false,
            items: null
        });
        this.#standardVenueTemplateState.setModifiedItems(null);
        this.#venueMapSrv.clearVenueMap();
    }

    getVenueItems$(): Observable<{ seats: Map<number, Seat>; nnzs: Map<number, NotNumberedZone> }> {
        return this.#standardVenueTemplateState.getVenueItems$();
    }

    // label groups

    getLabelGroups$(): Observable<VenueTemplateLabelGroup[]> {
        return this.#standardVenueTemplateState.getLabelGroups$();
    }

    setLabelGroups(labelGroups: VenueTemplateLabelGroup[]): void {
        this.#standardVenueTemplateState.setLabelGroups(labelGroups);
    }

    getSelectedLabelGroup$(): Observable<VenueTemplateLabelGroup> {
        return this.#standardVenueTemplateState.getSelectedLabelGroup$();
    }

    setSelectedLabelGroup(labelGroup: VenueTemplateLabelGroup): void {
        this.#standardVenueTemplateState.setSelectedLabelGroup(labelGroup);
    }

    // visor view

    getCurrentView$(): Observable<VenueTemplateEditorView> {
        return this.#standardVenueTemplateState.getCurrentView$();
    }

    setCurrentView(view: VenueTemplateEditorView): void {
        this.#standardVenueTemplateState.setCurrentView(view);
    }

    // visor state, main or capacity increase (not graphic)

    getCurrentState$(): Observable<VenueTemplateEditorState> {
        return this.#standardVenueTemplateState.getCurrentState$();
    }

    setCurrentState$(newState: VenueTemplateEditorState): void {
        this.#standardVenueTemplateState.setCurrentState(newState);
    }

    setLabelsCounter(labelGroups: VenueTemplateLabelGroup[], venueMap: VenueMap, includeUnlinkedEl: boolean = null): LabelErrors {
        includeUnlinkedEl ??= this.#includeUnlinkedEl;
        this.#includeUnlinkedEl = includeUnlinkedEl;
        const errors = this.countVenueMapLabels(includeUnlinkedEl, labelGroups, venueMap);
        this.#refreshViewCountersTrigger.next();
        return errors;
    }

    getRefreshViewCountersTrigger$(): Observable<void> {
        return this.#refreshViewCountersTrigger.asObservable()
            .pipe(takeUntilDestroyed(this.#destroyRef));
    }

    // ACTIONS

    emitAction(action: VenueTemplateAction): void {
        this.#action.next(action);
    }

    // INITIALIZATION

    private indexVenueMap(venueMap: VenueMap): void {
        if (venueMap) {
            const seats = new Map<number, Seat>();
            const nnzs = new Map<number, NotNumberedZone>();
            venueMap.sectors?.forEach(sector => {
                sector.rows?.forEach(row => row.seats?.forEach(seat => seats.set(seat.id, seat)));
                sector.notNumberedZones?.forEach(nnz => nnzs.set(nnz.id, nnz));
            });
            this.#standardVenueTemplateState.setVenueItems({ seats, nnzs });
        }
    }

    private countVenueMapLabels(includeUnlinkedEl: boolean, labelGroups: VenueTemplateLabelGroup[], venueMap: VenueMap): LabelErrors {
        const labelErrors = this.initLabelErrors();

        // init counters
        labelGroups.forEach(lg => lg.labels.forEach(l => l.count = 0));

        // label maps/index to increase access and searches
        const labelIndexer: VenueTemplateLabelIndexer = {
            stateLabels: this.getLabelGroupLabelsMap(labelGroups, VenueTemplateLabelGroupType.state),
            brLabels: this.getLabelGroupLabelsMap(labelGroups, VenueTemplateLabelGroupType.blockingReason),
            priceTypesLabels: this.getLabelGroupLabelsMap(labelGroups, VenueTemplateLabelGroupType.priceType),
            quotaLabels: this.getLabelGroupLabelsMap(labelGroups, VenueTemplateLabelGroupType.quota),
            visibilityLabels: this.getLabelGroupLabelsMap(labelGroups, VenueTemplateLabelGroupType.visibility),
            accessibilityLabels: this.getLabelGroupLabelsMap(labelGroups, VenueTemplateLabelGroupType.accessibility),
            gateLabels: this.getLabelGroupLabelsMap(labelGroups, VenueTemplateLabelGroupType.gate),
            seasonTicketLinkableLabels: this.getLabelGroupLabelsMap(labelGroups, VenueTemplateLabelGroupType.seasonTicketLinkable),
            sessionPackLabels: this.getLabelGroupLabelsMap(labelGroups, VenueTemplateLabelGroupType.sessionPacks),
            sessionPackLinkLabels: this.getLabelGroupLabelsMap(labelGroups, VenueTemplateLabelGroupType.sessionPackLink),
            firstCustomTagGroup: this.getLabelGroupLabelsMap(labelGroups, VenueTemplateLabelGroupType.firstCustomLabelGroup),
            secondCustomTagGroup: this.getLabelGroupLabelsMap(labelGroups, VenueTemplateLabelGroupType.secondCustomLabelGroup)
        };

        this.countSeatsLabels(venueMap.sectors, includeUnlinkedEl, labelIndexer, labelErrors);
        this.countNnzLabels(venueMap.sectors, includeUnlinkedEl, labelIndexer, labelErrors);

        return labelErrors;
    }

    private countSeatsLabels(
        sectors: Sector[], includeUnlinkedEl: boolean, labelIndexer: VenueTemplateLabelIndexer, labelErrors: LabelErrors
    ): void {
        sectors.forEach(sector => {
            const increaseCounter = (
                labelMap: Map<string, VenueTemplateLabel>, seatId: number, labelId: string, group: VenueTemplateLabelGroupType
            ): void => {
                if (labelMap?.has(labelId)) {
                    labelMap.get(labelId).count++;
                } else if (labelId !== '-1') {
                    this.registerLabelError(labelErrors, group, seatId, labelId);
                }
            };

            sector.rows?.forEach(row =>
                row.seats?.forEach(seat => {
                    increaseCounter(labelIndexer.stateLabels, seat.id, seat.status, VenueTemplateLabelGroupType.state);
                    increaseCounter(labelIndexer.sessionPackLinkLabels, seat.id, seat.linked, VenueTemplateLabelGroupType.sessionPackLink);
                    increaseCounter(
                        labelIndexer.seasonTicketLinkableLabels,
                        seat.id,
                        seat.linkable,
                        VenueTemplateLabelGroupType.seasonTicketLinkable
                    );

                    if (includeUnlinkedEl || seat.linked === SeatLinked.linked) {
                        if (seat.status === SeatStatus.promotorLocked) {
                            increaseCounter(
                                labelIndexer.brLabels,
                                seat.id,
                                String(seat.blockingReason),
                                VenueTemplateLabelGroupType.blockingReason
                            );
                        }
                        increaseCounter(
                            labelIndexer.priceTypesLabels,
                            seat.id,
                            String(seat.priceType),
                            VenueTemplateLabelGroupType.priceType
                        );
                        increaseCounter(labelIndexer.quotaLabels, seat.id, String(seat.quota), VenueTemplateLabelGroupType.quota);
                        increaseCounter(labelIndexer.visibilityLabels, seat.id, seat.visibility, VenueTemplateLabelGroupType.visibility);
                        increaseCounter(
                            labelIndexer.accessibilityLabels,
                            seat.id,
                            seat.accessibility,
                            VenueTemplateLabelGroupType.accessibility
                        );
                        increaseCounter(labelIndexer.gateLabels, seat.id, String(seat.gate), VenueTemplateLabelGroupType.gate);
                        increaseCounter(
                            labelIndexer.sessionPackLabels,
                            seat.id,
                            String(seat.sessionPack),
                            VenueTemplateLabelGroupType.sessionPacks
                        );

                        increaseCounter(
                            labelIndexer.firstCustomTagGroup,
                            seat.id,
                            String(seat.firstCustomTag),
                            VenueTemplateLabelGroupType.firstCustomLabelGroup
                        );
                        increaseCounter(
                            labelIndexer.secondCustomTagGroup,
                            seat.id,
                            String(seat.secondCustomTag),
                            VenueTemplateLabelGroupType.secondCustomLabelGroup
                        );

                    }
                })
            );
        });
    }

    private countNnzLabels(
        sectors: Sector[], includeUnlinkedEl: boolean, labelIndexer: VenueTemplateLabelIndexer, labelErrors: LabelErrors
    ): void {
        sectors.forEach(sector => {
            const increaseCounter = (
                labelMap: Map<string, VenueTemplateLabel>, nnzId: number,
                labelId: string, group: VenueTemplateLabelGroupType, count: number = 1
            ): void => {
                if (labelMap?.has(labelId)) {
                    labelMap.get(labelId).count += count;
                } else if (labelId !== '-1') {
                    this.registerLabelError(labelErrors, group, nnzId, labelId);
                }
            };

            sector.notNumberedZones?.forEach(nnz => {
                nnz.statusCounters?.forEach(sc => increaseCounter(
                    labelIndexer.sessionPackLinkLabels,
                    nnz.id,
                    sc.linked,
                    VenueTemplateLabelGroupType.sessionPackLink,
                    sc.count
                ));
                nnz.statusCounters?.forEach(sc => increaseCounter(
                    labelIndexer.stateLabels,
                    nnz.id,
                    sc.status,
                    VenueTemplateLabelGroupType.state,
                    sc.count
                ));
                nnz.sessionPackCounters?.forEach(spc => increaseCounter(
                    labelIndexer.sessionPackLabels,
                    nnz.id,
                    String(spc.sessionPack),
                    VenueTemplateLabelGroupType.sessionPacks,
                    spc.count
                ));
                nnz.blockingReasonCounters?.forEach(brc => increaseCounter(
                    labelIndexer.brLabels,
                    nnz.id,
                    String(brc.blocking_reason),
                    VenueTemplateLabelGroupType.blockingReason,
                    brc.count
                ));
                nnz.quotaCounters?.forEach(qc => increaseCounter(
                    labelIndexer.quotaLabels,
                    nnz.id,
                    String(qc.quota),
                    VenueTemplateLabelGroupType.quota,
                    qc.count
                ));

                increaseCounter(
                    labelIndexer.seasonTicketLinkableLabels,
                    nnz.id,
                    SeatLinkable.linkable,
                    VenueTemplateLabelGroupType.seasonTicketLinkable,
                    nnz.linkableSeats
                );
                increaseCounter(
                    labelIndexer.seasonTicketLinkableLabels,
                    nnz.id,
                    SeatLinkable.notLinkable,
                    VenueTemplateLabelGroupType.seasonTicketLinkable,
                    nnz.capacity - nnz.linkableSeats
                );

                const capacityToWork = includeUnlinkedEl
                    ? nnz.capacity
                    : nnz.capacity - (nnz.statusCounters.find(sc => sc.linked === SeatLinked.unlinked)?.count || 0);

                increaseCounter(labelIndexer.priceTypesLabels,
                    nnz.id,
                    String(nnz.priceType),
                    VenueTemplateLabelGroupType.priceType,
                    capacityToWork
                );
                increaseCounter(labelIndexer.visibilityLabels,
                    nnz.id,
                    nnz.visibility,
                    VenueTemplateLabelGroupType.visibility,
                    capacityToWork
                );
                increaseCounter(labelIndexer.accessibilityLabels,
                    nnz.id,
                    nnz.accessibility,
                    VenueTemplateLabelGroupType.accessibility,
                    capacityToWork
                );
                increaseCounter(labelIndexer.gateLabels,
                    nnz.id,
                    String(nnz.gate),
                    VenueTemplateLabelGroupType.gate,
                    capacityToWork
                );
                increaseCounter(labelIndexer.firstCustomTagGroup,
                    nnz.id,
                    String(nnz.firstCustomTag),
                    VenueTemplateLabelGroupType.firstCustomLabelGroup,
                    capacityToWork
                );
                increaseCounter(labelIndexer.secondCustomTagGroup,
                    nnz.id,
                    String(nnz.secondCustomTag),
                    VenueTemplateLabelGroupType.secondCustomLabelGroup,
                    capacityToWork
                );
            });
        });
    }

    private getLabelGroupLabelsMap(labelGroups: VenueTemplateLabelGroup[], labelGroupType: string): Map<string, VenueTemplateLabel> {
        const labelMap = new Map<string, VenueTemplateLabel>();
        labelGroups
            ?.find(labelGroup => labelGroup.id === labelGroupType)
            ?.labels.forEach(label => labelMap.set(label.id, label));
        return labelMap;
    }

    private initLabelErrors(): LabelErrors {
        return {
            [VenueTemplateLabelGroupType.state]: new Map<string, number[]>(),
            [VenueTemplateLabelGroupType.blockingReason]: new Map<string, number[]>(),
            [VenueTemplateLabelGroupType.priceType]: new Map<string, number[]>(),
            [VenueTemplateLabelGroupType.quota]: new Map<string, number[]>(),
            [VenueTemplateLabelGroupType.visibility]: new Map<string, number[]>(),
            [VenueTemplateLabelGroupType.accessibility]: new Map<string, number[]>(),
            [VenueTemplateLabelGroupType.gate]: new Map<string, number[]>(),
            [VenueTemplateLabelGroupType.seasonTicketLinkable]: new Map<string, number[]>(),
            [VenueTemplateLabelGroupType.sessionPacks]: new Map<string, number[]>(),
            [VenueTemplateLabelGroupType.sessionPackLink]: new Map<string, number[]>(),
            [VenueTemplateLabelGroupType.firstCustomLabelGroup]: new Map<string, number[]>(),
            [VenueTemplateLabelGroupType.secondCustomLabelGroup]: new Map<string, number[]>(),
            [VenueTemplateLabelGroupType.seasonSessions]: new Map<string, number[]>(),
            [VenueTemplateLabelGroupType.sessionSeasons]: new Map<string, number[]>()
        };
    }

    private registerLabelError(labelErrors: LabelErrors, group: VenueTemplateLabelGroupType, seatId: number, labelId: string): void {
        if (labelErrors[group].has(labelId)) {
            labelErrors[group].get(labelId).push(seatId);
        } else {
            labelErrors[group].set(labelId, [seatId]);
        }
    }
}

export type LabelErrors = {
    [K in VenueTemplateLabelGroupType]: Map<string, number[]>;
};

interface VenueTemplateLabelIndexer {
    stateLabels: Map<string, VenueTemplateLabel>;
    sessionPackLinkLabels: Map<string, VenueTemplateLabel>;
    brLabels: Map<string, VenueTemplateLabel>;
    priceTypesLabels: Map<string, VenueTemplateLabel>;
    quotaLabels: Map<string, VenueTemplateLabel>;
    visibilityLabels: Map<string, VenueTemplateLabel>;
    accessibilityLabels: Map<string, VenueTemplateLabel>;
    gateLabels: Map<string, VenueTemplateLabel>;
    seasonTicketLinkableLabels: Map<string, VenueTemplateLabel>;
    sessionPackLabels: Map<string, VenueTemplateLabel>;
    firstCustomTagGroup: Map<string, VenueTemplateLabel>;
    secondCustomTagGroup: Map<string, VenueTemplateLabel>;
}
