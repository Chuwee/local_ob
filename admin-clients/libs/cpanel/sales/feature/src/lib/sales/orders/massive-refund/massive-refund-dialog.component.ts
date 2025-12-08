import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import {
    MassiveRefundSelectionType, PostMassiveRefundOrdersRequest, PostMassiveRefundOrdersSummaryRequest, RefundType, OrdersService
} from '@admin-clients/cpanel-sales-data-access';
import { CsvErrorEnum, csvValidator, CsvFile } from '@admin-clients/shared/common/feature/csv';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge, requiredFieldsInOneControl } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import { BehaviorSubject, combineLatest, distinctUntilChanged, map, Observable, startWith, Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'app-massive-refund',
    templateUrl: './massive-refund-dialog.component.html',
    styleUrls: ['./massive-refund-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MassiveRefundDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _currentStepBS = new BehaviorSubject<number>(0);
    private _refundDetailsLoadingBS = new BehaviorSubject<boolean>(false);
    private _refundTypeLoadingBS = new BehaviorSubject<boolean>(false);
    private _isCsvParserLoadingBS = new BehaviorSubject<boolean>(false);
    private _file: File;

    @ViewChild(WizardBarComponent, { static: true })
    private _wizardBar: WizardBarComponent;

    steps: { title: string; form: AbstractControl }[];
    currentStep$: Observable<number>;
    nextText$: Observable<string>;
    isLoading$: Observable<boolean>;
    isPreviousDisabled$: Observable<boolean>;
    isNextDisabled$: Observable<boolean>;

    form: UntypedFormGroup;
    refundDetailsForm: UntypedFormGroup;
    refundTypeForm: UntypedFormGroup;

    constructor(
        private _fb: UntypedFormBuilder,
        private _ordersService: OrdersService,
        private _translate: TranslateService,
        private _msgDialogSrv: MessageDialogService,
        private _csvParser: Papa,
        private _dialogRef: MatDialogRef<MassiveRefundDialogComponent, PostMassiveRefundOrdersRequest>
    ) {
        this._dialogRef.addPanelClass(DialogSize.LARGE);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.initForm();
        this.setLoading();
        this.setSteps();
        this.initFormChangesHandlers();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(request: PostMassiveRefundOrdersRequest = null): void {
        this._dialogRef.close(request);
    }

    goToStep(step: number): void {
        this.setStep(step);
    }

    nextStep(): void {
        if (this._currentStepBS.value === this.steps.length - 1) {
            this.refund();
        } else {
            this.loadSummary();
            this.setStep(this._currentStepBS.value + 1);
        }
    }

    previousStep(): void {
        this._ordersService.clearMassiveRefundSummary();
        this.setStep(this._currentStepBS.value - 1);
    }

    mapToStepsTitles(steps: { title: string; form: AbstractControl }[]): string[] {
        return steps.map(step => step.title);
    }

    refundDetailsLoadingHandler(isLoading: boolean): void {
        this._refundDetailsLoadingBS.next(isLoading);
    }

    refundTypeLoadingHandler(isLoading: boolean): void {
        this._refundTypeLoadingBS.next(isLoading);
    }

    private initForm(): void {
        const csvFileSelector: CsvFile = { file: null, processedFile: null };

        this.refundDetailsForm = this._fb.group({
            entity: [null, Validators.required],
            event: [{ value: null, disabled: true }, Validators.required],
            session: [{ value: null, disabled: true }, Validators.required],
            channel: { value: null, disabled: true },
            selectionType: [{ value: null, disabled: true }, Validators.required],
            barcodesListSelectionForm: this._fb.group({
                barcodesCsvSelection: [
                    csvFileSelector,
                    [
                        requiredFieldsInOneControl(Object.keys(csvFileSelector)),
                        csvValidator(CsvErrorEnum.csvProcessorFileError)
                    ]
                ]
            })
        });

        this.refundTypeForm = this._fb.group({
            type: [RefundType.refund, Validators.required],
            surcharges: { value: false, disabled: true },
            insurance: { value: false, disabled: true },
            delivery: { value: false, disabled: true }
        });

        this.form = this._fb.group({
            refundDetailsForm: this.refundDetailsForm,
            refundTypeForm: this.refundTypeForm
        });
    }

    private initFormChangesHandlers(): void {
        this.refundDetailsForm.get('entity').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(entity => {
                this._ordersService.clearFilterEventList();
                this._ordersService.clearFilterSessionList();
                this._ordersService.clearFilterChannelList();
                this.refundDetailsForm.get('event').reset();
                this.refundDetailsForm.get('session').reset();
                this.refundDetailsForm.get('channel').reset();
                if (entity) {
                    this.refundDetailsForm.get('event').enable();
                } else {
                    this.refundDetailsForm.get('event').disable();
                }
            });

        this.refundDetailsForm.get('event').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(event => {
                this._ordersService.clearFilterSessionList();
                this._ordersService.clearFilterChannelList();
                this.refundDetailsForm.get('session').reset();
                this.refundDetailsForm.get('channel').reset();
                if (event) {
                    this.refundDetailsForm.get('session').enable();
                } else {
                    this.refundDetailsForm.get('session').disable();
                }
            });

        this.refundDetailsForm.get('session').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(session => {
                this._ordersService.clearFilterChannelList();
                this.refundDetailsForm.get('channel').reset();
                this.refundDetailsForm.get('selectionType').reset();
                if (session) {
                    this.refundDetailsForm.get('channel').enable();
                    this.refundDetailsForm.get('selectionType').enable();
                } else {
                    this.refundDetailsForm.get('channel').disable();
                    this.refundDetailsForm.get('selectionType').disable();
                }
            });

        this.refundDetailsForm.get('selectionType').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(selectionType => {
                if (selectionType === MassiveRefundSelectionType.ordersList) {
                    this.refundDetailsForm.get('channel').reset();
                    this.refundDetailsForm.get('channel').disable();
                    this.refundDetailsForm.get('barcodesListSelectionForm').enable();
                } else if (selectionType === MassiveRefundSelectionType.session) {
                    this.refundDetailsForm.get('channel').enable();
                    this.refundDetailsForm.get('barcodesListSelectionForm').disable();
                }
            });

        this.refundDetailsForm.get('barcodesListSelectionForm.barcodesCsvSelection').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((csvFileProcessed: CsvFile) => {
                const { file } = csvFileProcessed;
                this._file = file;
            });

    }

    private setLoading(): void {
        this.isLoading$ = booleanOrMerge([
            this._refundDetailsLoadingBS.asObservable(),
            this._refundTypeLoadingBS.asObservable(),
            this._isCsvParserLoadingBS.asObservable(),
            this._ordersService.isMassiveRefundSummaryLoading$(),
            this._ordersService.isMassiveRefundLoading$()
        ]);
    }

    private setSteps(): void {
        this.steps = [
            {
                title: 'ORDERS.MASSIVE_REFUND.DIALOG.DETAILS.TITLE',
                form: this.refundDetailsForm
            },
            {
                title: 'ORDERS.MASSIVE_REFUND.DIALOG.TYPE.TITLE',
                form: this.refundTypeForm
            }
        ];

        this.currentStep$ = this._currentStepBS.asObservable();

        this.nextText$ = this._currentStepBS.asObservable()
            .pipe(
                map(currentStep => {
                    if (currentStep === this.steps.length - 1) {
                        return this._translate.instant('ACTIONS.REFUND.CONFIRM');
                    } else {
                        return this._translate.instant('FORMS.ACTIONS.NEXT');
                    }
                }),
                distinctUntilChanged()
            );

        this.isPreviousDisabled$ = combineLatest([
            this._currentStepBS.asObservable(),
            this.isLoading$
        ]).pipe(
            map(([currentStep, isLoading]) => currentStep === 0 || isLoading),
            distinctUntilChanged()
        );

        this.isNextDisabled$ = combineLatest([
            this._currentStepBS.asObservable(),
            this.isLoading$,
            this.form.statusChanges.pipe(startWith(null as unknown))
        ]).pipe(
            map(([currentStep, isLoading]) => isLoading || this.steps[currentStep].form.invalid),
            distinctUntilChanged()
        );
    }

    private setStep(step: number): void {
        this._wizardBar.setActiveStep(step);
        this._currentStepBS.next(step);
    }

    private loadSummary(): void {
        const data = this.form.get('refundDetailsForm').value;
        const request: PostMassiveRefundOrdersSummaryRequest = {
            event_entity_id: data.entity.id,
            event_id: data.event.id,
            session_id: [data.session.id]
        };
        if (data.channel) {
            request.channel_id = [data.channel.id];
        }
        if (data.selectionType === MassiveRefundSelectionType.ordersList) {
            this.parseFile(idsToRefund => {
                request.order_codes = idsToRefund;
                this._ordersService.loadMassiveRefundSummary(request);
            });
        } else {
            this._ordersService.loadMassiveRefundSummary(request);
        }
    }

    private refund(): void {

        const { entity, event, session, channel, selectionType } = this.refundDetailsForm.value;
        const { type, surcharges, delivery, insurance } = this.refundTypeForm.value;

        const request: PostMassiveRefundOrdersRequest = {
            event_entity_id: entity.id,
            event_id: event.id,
            session_id: [session.id],
            channel_id: channel ? [channel.id] : null,
            refund_type: type,
            include_surcharges: surcharges || false,
            include_delivery: delivery || false,
            include_insurance: insurance || false
        };

        if (selectionType === MassiveRefundSelectionType.ordersList) {
            this._isCsvParserLoadingBS.next(true);
            this.parseFile(idsToRefund => {
                request.order_codes = idsToRefund;
                this.close(request);
            });
        } else {
            this.close(request);
        }
    }

    private parseFile(
        cb: (idsToRefund: string[]) => void
    ): void {
        const idsToRefund: string[] = [];
        let hasErrors = false;
        this._file.text().then(res => {
            this._csvParser.parse(res, {
                delimiter: ';', skipEmptyLines: true, header: true, worker: true, dynamicTyping: true,
                step: (({ data, errors }) => {
                    if (!hasErrors && errors?.length) {
                        hasErrors = true;
                    }
                    const key = Object.keys(data)[0];
                    idsToRefund.push(data[key]);
                }),
                complete: (() => {
                    if (hasErrors) {
                        this.showParseError('CSV.IMPORT.PROCESS_ERROR');
                    } else if (idsToRefund.length > 200) {
                        this.showParseError('ORDERS.MASSIVE_REFUND.DIALOG.CODES_LIMIT_EXCEEDED');
                    } else {
                        cb(idsToRefund);
                        this._isCsvParserLoadingBS.next(false);
                    }
                })
            });
        });
    }

    private showParseError(message: string): void {
        this._isCsvParserLoadingBS.next(false);
        this._msgDialogSrv.showAlert({
            size: DialogSize.SMALL,
            title: 'TITLES.ERROR_DIALOG',
            message
        });
    }
}
