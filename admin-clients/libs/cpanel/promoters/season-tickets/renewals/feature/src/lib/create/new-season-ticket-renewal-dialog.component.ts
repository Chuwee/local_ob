import { SeasonTicket, SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketRenewalsService, NewSeasonTicketRenewalDialogState,
    RenewalCandidateTypeEnum, SeasonTicketRenewalCandidateSearch, SeasonTicketExternalRenewalCandidateSearch,
    SeasonTicketRenewalRateMapping, RenewalCandidateToImport, seasonTicketRenewalsProviders
} from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable, Subject } from 'rxjs';
import { distinctUntilChanged, filter, first, map, startWith, take, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-new-season-ticket-renewal-dialog',
    templateUrl: './new-season-ticket-renewal-dialog.component.html',
    styleUrls: ['./new-season-ticket-renewal-dialog.component.scss'],
    providers: [
        seasonTicketRenewalsProviders,
        NewSeasonTicketRenewalDialogState
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class NewSeasonTicketRenewalDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _currentStepBS = new BehaviorSubject<number>(0);
    private _candidateTypeBS = new BehaviorSubject<RenewalCandidateTypeEnum>(RenewalCandidateTypeEnum.none);
    private _candidateType: RenewalCandidateTypeEnum;
    private _internalCandidate: SeasonTicketRenewalCandidateSearch;
    private _externalCandidate: SeasonTicketExternalRenewalCandidateSearch;
    private _candidateLoadingBS = new BehaviorSubject<boolean>(false);
    private _ratesLoadingBS = new BehaviorSubject<boolean>(false);
    private _candidateFormGroupName = 'candidate';
    private _ratesFormGroupName = 'rates';
    private _seasonTicket: SeasonTicket;

    @ViewChild(WizardBarComponent, { static: true })
    private _wizardBar: WizardBarComponent;

    candidateTypeControlName = 'candidateType';
    internalCandidateControlName = 'internalCandidate';
    externalCandidateControlName = 'externalCandidate';
    internalRatesControlName = 'internalRates';
    externalRatesControlName = 'externalRates';
    hasPreviousRenewals: boolean;
    isLoading$: Observable<boolean>;
    form: UntypedFormGroup;
    currentStep$: Observable<number>;
    steps: { title: string; form: AbstractControl }[];
    isPreviousDisabled$: Observable<boolean>;
    isNextDisabled$: Observable<boolean>;
    nextText$: Observable<string>;

    get candidateFormGroup(): UntypedFormGroup {
        return this.form.get(this._candidateFormGroupName) as UntypedFormGroup;
    }

    get ratesFormGroup(): UntypedFormGroup {
        return this.form.get(this._ratesFormGroupName) as UntypedFormGroup;
    }

    constructor(
        private _dialogRef: MatDialogRef<NewSeasonTicketRenewalDialogComponent>,
        private _seasonTicketRenewalsSrv: SeasonTicketRenewalsService,
        private _seasonTicketSrv: SeasonTicketsService,
        private _fb: UntypedFormBuilder,
        private _translate: TranslateService,
        @Inject(MAT_DIALOG_DATA) private _data: { hasPreviousRenewals: boolean }
    ) {
        this._dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this.hasPreviousRenewals = _data?.hasPreviousRenewals;
    }

    ngOnInit(): void {
        this.initForm();
        this.setLoading();
        this.setSteps();
        this.setSeasonTicket();
        this.setSeasonTicketRates();
        this.candidateTypeChangeHandler();
        this.internalCandidateChangeHandler();
        this.externalCandidateChangeHandler();
        this.internalRatesChangeHandler();
        this.externalRatesChangeHandler();
    }

    ngOnDestroy(): void {
        this._seasonTicketSrv.clearSeasonTicketRates();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    candidateLoadingHandler(isLoading: boolean): void {
        this._candidateLoadingBS.next(isLoading);
    }

    ratesLoadingHandler(isLoading: boolean): void {
        this._ratesLoadingBS.next(isLoading);
    }

    goToStep(step: number): void {
        this._currentStepBS.next(step);
        this._wizardBar.setActiveStep(step);
    }

    nextStep(): void {
        if (this._currentStepBS.value === this.steps.length - 1) {
            this.importRenewals();
        } else {
            this._wizardBar.setActiveStep(this._currentStepBS.value + 1);
            this._currentStepBS.next(this._currentStepBS.value + 1);
        }
    }

    previousStep(): void {
        this._wizardBar.setActiveStep(this._currentStepBS.value - 1);
        this._currentStepBS.next(this._currentStepBS.value - 1);
    }

    close(): void {
        this._dialogRef.close();
    }

    mapToStepsTitles(steps: { title: string; form: AbstractControl }[]): string[] {
        return steps.map(step => step.title);
    }

    private initForm(): void {
        const candidateFormGroup = this._fb.group({
            [this.candidateTypeControlName]: [null, Validators.required],
            [this.internalCandidateControlName]: [null, Validators.required],
            [this.externalCandidateControlName]: [null, Validators.required]
        });

        const ratesFormGroup = this._fb.group({
            [this.internalRatesControlName]: this._fb.array([]),
            [this.externalRatesControlName]: this._fb.array([]),
            includeBalance: [null, Validators.required]
        });

        this.form = this._fb.group({
            [this._candidateFormGroupName]: candidateFormGroup,
            [this._ratesFormGroupName]: ratesFormGroup
        });
    }

    private setLoading(): void {
        this.isLoading$ = booleanOrMerge([
            this._candidateLoadingBS.asObservable(),
            this._ratesLoadingBS.asObservable()
        ]);
    }

    private setSteps(): void {
        this.steps = [
            {
                title: 'SEASON_TICKET.RENEWALS.DIALOG_RENEWAL_LIST.CANDIDATE_STEP',
                form: this.candidateFormGroup
            },
            {
                title: 'SEASON_TICKET.RENEWALS.DIALOG_RENEWAL_LIST.RATE_STEP',
                form: this.ratesFormGroup
            }
        ];

        this.currentStep$ = this._currentStepBS.asObservable();

        this.nextText$ = this.currentStep$
            .pipe(
                map(currentStep => {
                    if (currentStep === this.steps.length - 1) {
                        return this._translate.instant('FORMS.ACTIONS.IMPORT');
                    } else {
                        return this._translate.instant('FORMS.ACTIONS.NEXT');
                    }
                }),
                distinctUntilChanged()
            );

        this.isPreviousDisabled$ = combineLatest([
            this.currentStep$,
            this.isLoading$
        ]).pipe(
            map(([currentStep, isLoading]) => currentStep === 0 || isLoading),
            distinctUntilChanged()
        );

        this.isNextDisabled$ = combineLatest([
            this.currentStep$,
            this.isLoading$,
            this.form.valueChanges.pipe(startWith({ importedSeasonTicket: undefined, rates: [], includeBalance: null }))
        ]).pipe(
            map(([currentStep, isLoading]) => isLoading || this.steps[currentStep].form.invalid),
            distinctUntilChanged()
        );
    }

    private setSeasonTicket(): void {
        this._seasonTicketSrv.seasonTicket.get$()
            .pipe(take(1))
            .subscribe(seasonTicket => this._seasonTicket = seasonTicket);
    }

    private setSeasonTicketRates(): void {
        this._seasonTicketSrv.clearSeasonTicketRates();
        this._seasonTicketSrv.loadSeasonTicketRates(this._seasonTicket.id.toString());
    }

    private candidateTypeChangeHandler(): void {
        this.candidateFormGroup.get(this.candidateTypeControlName).valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((type: RenewalCandidateTypeEnum) => {
                this._candidateTypeBS.next(type);
                this._candidateType = type;
                this.setFormEnableStatus();
                this.loadCandidates();
            });
    }

    private setFormEnableStatus(): void {
        if (this._candidateType === RenewalCandidateTypeEnum.internal
            || this._candidateType === RenewalCandidateTypeEnum.internalAllEntities) {
            this.candidateFormGroup.get(this.externalCandidateControlName).disable({ emitEvent: false });
            this.ratesFormGroup.get(this.externalRatesControlName).disable({ emitEvent: false });
            this.candidateFormGroup.get(this.internalCandidateControlName).enable({ emitEvent: false });
            this.ratesFormGroup.get(this.internalRatesControlName).enable({ emitEvent: false });
        }
        if (this._candidateType === RenewalCandidateTypeEnum.external) {
            this.candidateFormGroup.get(this.internalCandidateControlName).disable({ emitEvent: false });
            this.ratesFormGroup.get(this.internalRatesControlName).disable({ emitEvent: false });
            this.candidateFormGroup.get(this.externalCandidateControlName).enable({ emitEvent: false });
            this.ratesFormGroup.get(this.externalRatesControlName).enable({ emitEvent: false });
        }
    }

    private loadCandidates(): void {
        if (this._candidateType === RenewalCandidateTypeEnum.internal
            || this._candidateType === RenewalCandidateTypeEnum.internalAllEntities) {
            this._seasonTicketRenewalsSrv.renewalCandidatesList.getData$()
                .pipe(first(value => !value))
                .subscribe(() => {
                    this._seasonTicketRenewalsSrv.renewalCandidatesList.load(this._seasonTicket.id);
                });
        } else if (this._candidateType === RenewalCandidateTypeEnum.external) {
            this._seasonTicketRenewalsSrv.externalRenewalCandidatesList.getData$()
                .pipe(first(value => !value))
                .subscribe(() => {
                    this._seasonTicketRenewalsSrv.externalRenewalCandidatesList.load(this._seasonTicket.entity.id);
                });
        }
    }

    private internalCandidateChangeHandler(): void {
        this.candidateFormGroup.get(this.internalCandidateControlName).valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([candidate]: SeasonTicketRenewalCandidateSearch[]) => {
                if (
                    !this._internalCandidate ||
                    (this._internalCandidate && this._internalCandidate.id !== candidate.id)
                ) {
                    this._internalCandidate = candidate;
                    this.loadRenewalRates();
                }
            });
    }

    private loadRenewalRates(): void {
        this._seasonTicketRenewalsSrv.renewalRates.clear();
        this._seasonTicketRenewalsSrv.renewalRates.load(this._internalCandidate.id);
    }

    private externalCandidateChangeHandler(): void {
        this.candidateFormGroup.get(this.externalCandidateControlName).valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([candidate]: SeasonTicketExternalRenewalCandidateSearch[]) => {
                if (
                    !this._externalCandidate ||
                    (this._externalCandidate && this._externalCandidate.id !== candidate.id)
                ) {
                    this._externalCandidate = candidate;
                    this.loadExternalRenewalRates();
                }
            });
    }

    private loadExternalRenewalRates(): void {
        this._seasonTicketRenewalsSrv.externalRenewalRates.clear();
        this._seasonTicketRenewalsSrv.externalRenewalRates.load(this._externalCandidate.id);
    }

    private internalRatesChangeHandler(): void {
        this._seasonTicketRenewalsSrv.renewalRates.get$()
            .pipe(
                filter(value => !!value),
                takeUntil(this._onDestroy)
            ).subscribe(rates => {
                const formArray = (this.ratesFormGroup.get(this.internalRatesControlName) as UntypedFormArray);
                formArray.clear();
                rates.forEach(() => formArray.push(this._fb.control('', Validators.required)));
            });
    }

    private externalRatesChangeHandler(): void {
        this._seasonTicketRenewalsSrv.externalRenewalRates.get$()
            .pipe(
                filter(value => !!value),
                takeUntil(this._onDestroy)
            ).subscribe(rates => {
                const formArray = (this.ratesFormGroup.get(this.externalRatesControlName) as UntypedFormArray);
                formArray.clear();
                rates.forEach(() => formArray.push(this._fb.control('', Validators.required)));
            });
    }

    private importRenewals(): void {
        if (this._candidateType === RenewalCandidateTypeEnum.internal ||
            this._candidateType === RenewalCandidateTypeEnum.internalAllEntities) {
            const renewalCandidateId = this._internalCandidate.id;
            const renewalRates: SeasonTicketRenewalRateMapping[] = this.ratesFormGroup.get(this.internalRatesControlName).value;
            const postRenewal: RenewalCandidateToImport = {
                renewalCandidateId,
                renewalRates,
                type: this._candidateType,
                includeBalance: this.ratesFormGroup.get('includeBalance').value
            };
            this._dialogRef.close(postRenewal);
        } else if (this._candidateType === RenewalCandidateTypeEnum.external) {
            const renewalCandidateId = this._externalCandidate.id;
            const renewalRates: SeasonTicketRenewalRateMapping[] = this.ratesFormGroup.get(this.externalRatesControlName).value;
            const postRenewal: RenewalCandidateToImport = {
                renewalCandidateId,
                renewalRates,
                type: RenewalCandidateTypeEnum.external,
                includeBalance: this.ratesFormGroup.get('includeBalance').value
            };
            this._dialogRef.close(postRenewal);
        }
    }
}
