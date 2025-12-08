import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import {
    NotNumberedZone, SeatLinked, SeatStatus, Sector, SESSION_PACK_SERVICE, SessionPackService, StdVenueTplService, VENUE_MAP_SERVICE,
    VenueMapService
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit, Optional } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, FormsModule, ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, Observable, Subject } from 'rxjs';
import { first, map, shareReplay, switchMap, take, takeUntil, tap, withLatestFrom } from 'rxjs/operators';
import { VenueTemplateLabel } from '../../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../../models/label-group/venue-template-label-group-type.enum';
import { StandardVenueTemplateBaseService } from '../../services/standard-venue-template-base.service';
import { SourceLabelCounter } from '../nnz-partial-apply-dialog/source-label-counter.model';
import { SessionPackLinkDialogData } from './session-pack-link-dialog.data';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        FormsModule,
        ReactiveFormsModule,
        SelectSearchComponent
    ],
    selector: 'app-session-pack-link-dialog',
    templateUrl: './session-pack-link-dialog.component.html',
    styleUrls: ['./session-pack-link-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SessionPackLinkDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _nnzId: number;
    isNNZ: boolean;
    form: UntypedFormGroup;
    isLinkAction: boolean;
    targetStatusLabels$: Observable<VenueTemplateLabel[]>;
    sourceStatusLabels$: Observable<SourceLabelCounter[]>;
    nnz$: Observable<NotNumberedZone>;
    sector$: Observable<Sector>;
    quotaLabels$: Observable<VenueTemplateLabel[]>;
    isSaving$: Observable<boolean>;

    constructor(
        private readonly _dialogRef: MatDialogRef<SessionPackLinkDialogComponent>,
        @Inject(MAT_DIALOG_DATA) private readonly _data: SessionPackLinkDialogData,
        private readonly _fb: UntypedFormBuilder,
        private readonly _translateSrv: TranslateService,
        private readonly _standardVenueTemplateBaseSrv: StandardVenueTemplateBaseService,
        private readonly _stdVenueTplSrv: StdVenueTplService,
        @Inject(VENUE_MAP_SERVICE) @Optional() private readonly _venueMapSrv: VenueMapService,
        @Inject(SESSION_PACK_SERVICE) private readonly _sessionPackService: SessionPackService
    ) {
        this._venueMapSrv ??= this._stdVenueTplSrv;
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.isSaving$ = this._sessionPackService.isLinkToSessionPackSaving$();
        this.isNNZ = this._data.items.nnzs.size > 0;
        this.isLinkAction = this._data.label.id === SeatLinked.linked;
        this.form = this._fb.group({
            targetStatus: [null, Validators.required],
            sourceStatus: [{ value: null, disabled: !this.isNNZ }, Validators.required],
            quota: [{ value: null, disabled: this.isNNZ }, Validators.required],
            allTickets: [{ value: true, disabled: !this.isNNZ }, Validators.required],
            count: [{ value: null, disabled: true }, [
                Validators.required,
                Validators.min(1),
                (control: AbstractControl) =>
                    Validators.max(this.form.get('sourceStatus')?.value?.count || 1)(control)
            ]]
        });

        this.targetStatusLabels$ = this._standardVenueTemplateBaseSrv.getLabelGroups$()
            .pipe(
                take(1),
                map(labelGroups => {
                    const freeStatusLabel = labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.state)
                        .labels
                        .find(label => label.id === SeatStatus.free);
                    if (this._data.unrestrictedPack && !this.isNNZ) {
                        if (this.isLinkAction) {
                            return [freeStatusLabel];
                        } else {
                            return [
                                { id: null, labelGroupId: undefined, literal: this._translateSrv.instant('EVENTS.SESSION.KEEP_STATUS') },
                                freeStatusLabel,
                                ...(labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.blockingReason)?.labels || [])
                            ];
                        }
                    } else {
                        return [
                            freeStatusLabel,
                            ...(labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.blockingReason)?.labels || [])
                        ];
                    }
                }),
                tap(labels => this.form.get('targetStatus').setValue(labels[0])),
                shareReplay(1)
            );

        if (this.isNNZ) {
            this._nnzId = Array.from(this._data.items.nnzs)[0];
            this.nnz$ = this._standardVenueTemplateBaseSrv.getVenueItems$()
                .pipe(
                    take(1),
                    map(venueItems => venueItems.nnzs.get(this._nnzId)),
                    shareReplay(1)
                );
            this.sector$ = this._venueMapSrv.getVenueMap$()
                .pipe(
                    first(venueMap => !!venueMap),
                    withLatestFrom(this.nnz$),
                    map(([venueMap, nnz]) => venueMap.sectors.find(sector => sector.id === nnz.sector)),
                    shareReplay(1)
                );

            this.form.get('sourceStatus').valueChanges
                .pipe(takeUntil(this._onDestroy))
                .subscribe(sourceStatus => {
                    const countControl = this.form.get('count');
                    countControl.setValue(sourceStatus ? Math.min(sourceStatus.count, countControl.value) : null);
                });
            this.form.get('allTickets').valueChanges
                .pipe(takeUntil(this._onDestroy))
                .subscribe(allTickets => {
                    const countControl = this.form.get('count');
                    if (allTickets) {
                        countControl.disable();
                    } else {
                        countControl.enable();
                    }
                });

            this.sourceStatusLabels$ = combineLatest([
                this._standardVenueTemplateBaseSrv.getLabelGroups$(),
                this.nnz$
            ])
                .pipe(
                    take(1),
                    map(([labelGroups, nnz]) => {
                        const seasonLockedCount = nnz.statusCounters.find(sc => sc.linked === SeatLinked.unlinked)?.count || 0;
                        const freeStatusLabel: SourceLabelCounter[] = labelGroups
                            .find(lg => lg.id === VenueTemplateLabelGroupType.state)
                            .labels
                            .filter(label => label.id === SeatStatus.free)
                            .map(label => ({
                                label,
                                count: this.isLinkAction ? seasonLockedCount :
                                    nnz.statusCounters.find(sc => sc.status === SeatStatus.free)?.count
                            }));
                        const brLabels: SourceLabelCounter[] = labelGroups
                            .find(lg => lg.id === VenueTemplateLabelGroupType.blockingReason)
                            ?.labels
                            .map(label => ({
                                label,
                                count: this.isLinkAction ? seasonLockedCount :
                                    nnz.blockingReasonCounters?.find(brc => String(brc.blocking_reason) === label.id)?.count
                            })) || [];
                        return freeStatusLabel.concat(brLabels)
                            .filter(slc => slc.count);
                    }),
                    tap(labels => this.form.get('sourceStatus').setValue(labels[0])),
                    shareReplay(1)
                );
        } else {
            this.quotaLabels$ = this._standardVenueTemplateBaseSrv.getLabelGroups$()
                .pipe(
                    take(1),
                    map(labelGroups =>
                        [
                            { id: null, labelGroupId: undefined, literal: this._translateSrv.instant('EVENTS.SESSION.KEEP_QUOTA') },
                            ...labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.quota).labels
                        ]),
                    tap(labels => this.form.get('quota').setValue(labels[0])),
                    shareReplay(1)
                );
        }
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    save(): void {
        if (this.form.valid) {
            this._standardVenueTemplateBaseSrv.getVenueItems$().pipe(first())
                .pipe(
                    switchMap(venueItems => {
                        const data = this.form.value;
                        const target = data.targetStatus.id;
                        if (this.isNNZ) {
                            const source = data.sourceStatus.label.id;
                            const count = data.count || data.sourceStatus.count;
                            if (this.isLinkAction) {
                                return this._sessionPackService.linkNnzToSessionPack(
                                    this._data.eventId, this._data.sessionId, this._nnzId, source, target, count
                                );
                            } else {
                                return this._sessionPackService.unlinkNnzToSessionPack(
                                    this._data.eventId, this._data.sessionId, this._nnzId, source, target, count
                                );
                            }
                        } else {
                            let seatIds = Array.from(this._data.items.seats);
                            const quota = data.quota.id;
                            if (this.isLinkAction) {
                                seatIds = seatIds.filter(seatId => venueItems.seats.get(seatId).linked === SeatLinked.unlinked);
                                return this._sessionPackService.linkSeatsToSessionPack(
                                    this._data.eventId, this._data.sessionId, seatIds, target, quota
                                );
                            } else {
                                seatIds = seatIds.filter(seatId => venueItems.seats.get(seatId).linked === SeatLinked.linked);
                                return this._sessionPackService.unlinkSeatsToSessionPack(
                                    this._data.eventId, this._data.sessionId, seatIds, target, quota
                                );
                            }
                        }
                    })
                )
                .subscribe(() => this.close(true));
        }
    }

    close(success = false): void {
        this._dialogRef.close(success);
    }
}
