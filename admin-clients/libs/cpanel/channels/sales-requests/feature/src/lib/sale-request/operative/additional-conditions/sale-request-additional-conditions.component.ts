import { AdditionalCondition } from '@admin-clients/cpanel/channels/data-access';
import { SaleRequest, SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { MessageDialogService, DialogSize, ObMatDialogConfig, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { TrackingService } from '@admin-clients/shared/data-access/trackers';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { filter, first, map, Observable, shareReplay, startWith, Subject, switchMap, takeUntil, tap, throwError } from 'rxjs';
import {
    SaleRequestAdditionalConditionsDialogComponent
} from './additional-conditions-dialog/sale-request-additional-conditions-dialog.component';

@Component({
    selector: 'app-sale-request-additional-conditions',
    templateUrl: './sale-request-additional-conditions.component.html',
    styleUrls: ['./sale-request-additional-conditions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestAdditionalConditionsComponent implements OnInit, OnDestroy, WritingComponent {
    private readonly _onDestroy = new Subject<void>();
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _matDialog = inject(MatDialog);
    private readonly _salesRequestsService = inject(SalesRequestsService);
    private readonly _ephemeralMessageService = inject(EphemeralMessageService);
    private readonly _msgDialogService = inject(MessageDialogService);
    private readonly _trackingService = inject(TrackingService);
    private readonly _breakpointObserver = inject(BreakpointObserver);

    private get _additionalConditionsFormArray(): UntypedFormArray {
        return this.form.get('additionalConditions') as UntypedFormArray;
    }

    readonly MAX_ADDITIONAL_CONDITIONS = 6;
    form: UntypedFormGroup;
    additionalConditionsColumns = ['condition-enabled', 'name', 'condition-required', 'actions'];
    additionalConditions$: Observable<AdditionalCondition[]>;
    loadingOrSaving$: Observable<boolean>;
    saleRequest: SaleRequest;

    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches)
        );

    ngOnInit(): void {
        // used to show spinner and disable save & cancel button
        this.loadingOrSaving$ = booleanOrMerge([
            this._salesRequestsService.isSaleRequestAdditionalConditionsLoading$(),
            this._salesRequestsService.isSaleRequestAdditionalConditionsSaving$()
        ]);

        this.initForm();

        //load saleRequest delivery conditions
        this._salesRequestsService.getSaleRequest$()
            .pipe(first(saleRequest => !!saleRequest))
            .subscribe(saleRequest => {
                this._salesRequestsService.loadSaleRequestAdditionalConditions(saleRequest.id);
                this.saleRequest = saleRequest;
            });

        this.additionalConditions$ = this._salesRequestsService.getSaleRequestAdditionalConditions$()
            .pipe(
                filter(additionalCond => !!additionalCond),
                tap(additionalCond =>
                    this.updateAdditionalConditionsData(additionalCond)
                ),
                startWith(null as AdditionalCondition[]),
                shareReplay(1));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._salesRequestsService.clearSaleRequestAdditionalConditions();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.loadAdditionalConditions(this.saleRequest.id);
        });
    }

    save$(): Observable<void> {
        if (this.form.valid && this.form.dirty) {
            return this.additionalConditions$.pipe(
                first(Boolean),
                switchMap(additionalCond => {
                    if (this._additionalConditionsFormArray.dirty) {
                        const additionalConditionsData = additionalCond
                            .map((condition, index) => ({
                                id: condition.id,
                                mandatory: this._additionalConditionsFormArray.at(index)
                                    .value.conditionRequired || false,
                                enabled: this._additionalConditionsFormArray.at(index)
                                    .value.conditionEnabled
                            }));

                        return this._salesRequestsService.updateSaleRequestAdditionalConditions(
                            this.saleRequest.id,
                            additionalConditionsData
                        ).pipe(
                            tap(() => {
                                this._ephemeralMessageService.showSuccess({
                                    msgKey: 'SALE_REQUEST.UPDATE_SUCCESS',
                                    msgParams: { saleRequestName: `${this.saleRequest.event.name}-${this.saleRequest.channel.name}` }
                                });
                            }));
                    }
                    return throwError(() => 'not saved!');
                }));

        }
        return throwError(() => 'not saved!');
    }

    cancel(): void {
        this.loadAdditionalConditions(this.saleRequest.id);
    }

    openAdditionalConditionDialog(saleReqId: number, condition?: AdditionalCondition): void {
        this._matDialog.open(SaleRequestAdditionalConditionsDialogComponent, new ObMatDialogConfig(condition))
            .beforeClosed()
            .subscribe(isSaved => {
                if (isSaved) {
                    this.loadAdditionalConditions(saleReqId);

                    //Add google analytics tracking to save new condition event
                    this._trackingService.sendEventTrack('Additional condition Created', 'Sales request', null, this.saleRequest.id);
                }
            });
    }

    openDeleteConditionDialog(saleReqId: number, condition: AdditionalCondition): void {
        this._msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'CHANNELS.OPTIONS.DELETE_CONDITION_TITLE',
            message: 'CHANNELS.OPTIONS.DELETE_CONDITION_MSG',
            messageParams: { conditionName: condition.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(isConfirmed => {
                if (isConfirmed) {
                    this._salesRequestsService.deleteAdditionalCondition(saleReqId, condition.id)
                        .subscribe(() => {
                            this._ephemeralMessageService.showSuccess({
                                msgKey: 'CHANNELS.OPTIONS.DELETE_CONDITION_SUCCESS',
                                msgParams: { conditionName: condition.name }
                            });
                            this.loadAdditionalConditions(saleReqId);
                        });
                }
            });
    }

    private initForm(): void {
        this.form = this._fb.group({
            additionalConditions: this._fb.array([])
        });
    }

    private updateAdditionalConditionsData(additionalConditions: AdditionalCondition[]): void {
        this._additionalConditionsFormArray.clear();
        additionalConditions
            ?.sort((a, b) => a.position - b.position)
            .forEach(cond => {
                this._additionalConditionsFormArray.push(this.mapAdditionalConditionsToFormRow(cond));
            });

        this._additionalConditionsFormArray.markAsPristine();
    }

    private loadAdditionalConditions(saleRequestId: number): void {
        this._salesRequestsService.loadSaleRequestAdditionalConditions(saleRequestId);
    }

    private mapAdditionalConditionsToFormRow(field: AdditionalCondition): UntypedFormGroup {
        const rowSchema: { [key: string]: { value: boolean; disabled: boolean } } = {
            conditionEnabled: {
                value: field.enabled,
                disabled: false
            },
            conditionRequired: {
                value: field.mandatory,
                disabled: !field.enabled
            }
        };

        const row = this._fb.group({
            ...rowSchema
        });

        row.get('conditionEnabled').valueChanges.pipe(takeUntil(this._onDestroy))
            .subscribe(value => {
                const requiredField = row.get('conditionRequired');
                if (!value) {
                    requiredField.setValue(false);
                    requiredField.disable();
                } else {
                    requiredField.enable();
                }
            });
        return row;
    }
}
