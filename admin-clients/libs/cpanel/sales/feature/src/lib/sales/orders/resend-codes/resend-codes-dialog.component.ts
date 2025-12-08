import {
    OrdersService, OrdersState, ExternalPermissionsResendMethod, ExternalPermissionsResendRequest, ExternalPermissionsResendSource
} from '@admin-clients/cpanel-sales-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { FilterOption } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import moment from 'moment-timezone';
import { map, Observable, Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'app-resend-codes',
    templateUrl: './resend-codes-dialog.component.html',
    styleUrls: ['./resend-codes-dialog.component.scss'],
    providers: [
        OrdersService,
        OrdersState
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ResendCodesDialogComponent implements OnInit, OnDestroy {
    private readonly PAGE_LIMIT = 100;
    private _onDestroy = new Subject<void>();

    form: UntypedFormGroup;
    events$: Observable<FilterOption[]>;
    moreEventsAvailable$: Observable<boolean>;
    sessions$: Observable<FilterOption[]>;
    moreSessionsAvailable$: Observable<boolean>;
    isResending$: Observable<boolean>;

    readonly externalPermissionsResendSources = ExternalPermissionsResendSource;
    readonly externalPermissionsResendMethods = ExternalPermissionsResendMethod;
    readonly externalPermissionsResendMethodsList = Object.values(ExternalPermissionsResendMethod);

    constructor(
        private _fb: UntypedFormBuilder,
        private _ordersService: OrdersService,
        private _dialogRef: MatDialogRef<ResendCodesDialogComponent>
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.initForm();
        this.initFormChangesHandlers();
        this.updateFormValues();
        this.events$ = this._ordersService.getFilterEventListData$();
        this.moreEventsAvailable$ = this._ordersService.getFilterEventListMetadata$().pipe(map(md => !!md?.next_cursor));
        this.sessions$ = this._ordersService.getFilterSessionListData$();
        this.moreSessionsAvailable$ = this._ordersService.getFilterSessionListMetadata$().pipe(map(md => !!md?.next_cursor));
        this.isResending$ = this._ordersService.isResendExternalPermissionsInProgress$();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    loadEvents(q: string, next = false): void {
        this._ordersService.loadFilterEventList({ q, limit: this.PAGE_LIMIT }, next);
    }

    loadSessions(q: string, next = false): void {
        const eventSelected = this.form.get('event').value;
        if (eventSelected?.id) {
            this._ordersService.loadFilterSessionList({ q, limit: this.PAGE_LIMIT, event_id: eventSelected.id }, next);
        }
    }

    resendCodes(): void {
        if (this.form.valid) {
            const formValues = this.form.value;
            const externalPermissionsResendRequest: ExternalPermissionsResendRequest = {
                resend_method: formValues.resendMethod
            };

            switch (formValues.resendSourceSelector) {
                case ExternalPermissionsResendSource.fromEventAndSession:
                    externalPermissionsResendRequest.event_id = formValues.event.id;
                    if (formValues.session) {
                        externalPermissionsResendRequest.session_id = formValues.session.id;
                    }
                    break;
                case ExternalPermissionsResendSource.fromDateRange:
                    externalPermissionsResendRequest.start_date = formValues.startDate;
                    externalPermissionsResendRequest.end_date = formValues.endDate;
                    break;
                default:
                    externalPermissionsResendRequest.order_code = formValues.orderCode;
            }

            this._ordersService.resendExternalPermissions(externalPermissionsResendRequest).subscribe(() => this.close('Success'));
        } else {
            this.form.markAsDirty();
            this.form.markAllAsTouched();
        }
    }

    close(response: string = null): void {
        this._dialogRef.close(response);
    }

    private initForm(): void {
        this.form = this._fb.group({
            resendSourceSelector: [null, Validators.required],
            event: [{ value: null, disabled: true }, Validators.required],
            session: [{ value: null, disabled: true }],
            startDate: [{ value: null, disabled: true }, Validators.required],
            endDate: [{ value: null, disabled: true }, Validators.required],
            orderCode: [{ value: null, disabled: true }, [Validators.required, Validators.minLength(12), Validators.maxLength(12)]],
            resendMethod: [null, Validators.required]
        });
    }

    private initFormChangesHandlers(): void {
        this.form.get('resendSourceSelector').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(resendSource => {
                if (resendSource === ExternalPermissionsResendSource.fromEventAndSession) {
                    this.form.get('event').enable();
                    this.form.get('startDate').disable();
                    this.form.get('endDate').disable();
                    this.form.get('orderCode').disable();
                } else if (resendSource === ExternalPermissionsResendSource.fromDateRange) {
                    this.form.get('event').disable();
                    this.form.get('session').disable();
                    this.form.get('startDate').enable();
                    this.form.get('orderCode').disable();
                } else {
                    this.form.get('event').disable();
                    this.form.get('session').disable();
                    this.form.get('startDate').disable();
                    this.form.get('endDate').disable();
                    this.form.get('orderCode').enable();
                }
            });
        this.form.get('event').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => {
                if (value) {
                    this.form.get('session').enable();
                    this.form.get('session').setValue(null, { emitEvent: false });
                    this.loadSessions(undefined);
                } else {
                    this.form.get('session').disable();
                }
            });
        this.form.get('startDate').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => {
                if (value) {
                    this.form.get('endDate').enable();
                    if (!this.form.get('endDate').value) {
                        this.form.get('endDate').setValue(
                            moment(value).hour(23).minutes(59).format(),
                            { emitEvent: false }
                        );
                    }
                } else {
                    this.form.get('endDate').disable();
                }
            });
    }

    private updateFormValues(): void {
        this.form.patchValue({
            resendSourceSelector: ExternalPermissionsResendSource.fromEventAndSession,
            resendMethod: ExternalPermissionsResendMethod.insert
        });
        this.form.markAsPristine();
    }

}
