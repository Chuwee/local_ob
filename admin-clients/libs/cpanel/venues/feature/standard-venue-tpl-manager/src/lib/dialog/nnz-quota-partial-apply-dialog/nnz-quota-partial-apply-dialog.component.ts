import { DialogSize, HelpButtonComponent, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { QuotaCounter } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgStyle } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatList, MatListItem } from '@angular/material/list';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatOption, MatSelect, MatSelectTrigger } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { map, startWith, take, takeUntil } from 'rxjs/operators';
import { VenueTemplateLabel } from '../../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../../models/label-group/venue-template-label-group-type.enum';
import { NnzPartialApplyData } from '../../models/nnz-partial-apply-data.model';
import { StandardVenueTemplateBaseService } from '../../services/standard-venue-template-base.service';
import { StandardVenueTemplatePartialChangesService } from '../../services/standard-venue-template-partial-changes.service';
import { VmQuotaCounter, VmQuotaCounterSource, VmQuotaCounterTarget } from './vm-quota-counter.model';

enum FormControlNames {
    quotaCounterSource = 'quotaCounterSource',
    count = 'count',
    allTickets = 'allTickets'
}

@Component({
    imports: [
        TranslatePipe, ReactiveFormsModule, SelectSearchComponent, SharedUtilityDirectivesModule, LocalNumberPipe,
        HelpButtonComponent, MatDialogTitle, MatIconButton, MatIcon, MatDialogContent, AsyncPipe, MatDivider, MatFormField,
        MatLabel, MatSelect, MatSelectTrigger, MatOption, MatTooltip, MatRadioButton, MatRadioGroup, MatInput, MatButton,
        NgStyle, MatListItem, MatList, MatDialogActions
    ],
    selector: 'app-nnz-quota-partial-apply-dialog',
    templateUrl: './nnz-quota-partial-apply-dialog.component.html',
    styleUrls: ['./nnz-quota-partial-apply-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NnzQuotaPartialApplyDialogComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _initialFormValues = {
        [FormControlNames.quotaCounterSource]: null,
        [FormControlNames.allTickets]: true,
        [FormControlNames.count]: 1
    } as const;

    private readonly _quotaCounterTargetBS = new BehaviorSubject<VmQuotaCounterTarget>(null);
    private readonly _quotaCounterSourcesBS = new BehaviorSubject<VmQuotaCounterSource[]>(null);
    private readonly _modifiedQuotaCounterSourcesBS = new BehaviorSubject<VmQuotaCounter[]>(null);
    private _quotaCounterTarget: VmQuotaCounterTarget;
    private _quotaCounterSources = new Map<string, VmQuotaCounterSource>();
    private _modifiedQuotaCounterSources = new Map<string, VmQuotaCounter>();
    private _maxValue: number;

    form: UntypedFormGroup;
    formControlNames = FormControlNames;
    nnz$: Observable<string>;
    nnzCapacity$: Observable<number>;
    sector$: Observable<string>;
    quotaCounterTarget$: Observable<VmQuotaCounterTarget>;
    quotaCounterSources$: Observable<VmQuotaCounterSource[]>;
    modifiedQuotaCounterSources$: Observable<VmQuotaCounter[]>;
    isSaveDisabled$: Observable<boolean>;
    isAssignDisabled$: Observable<boolean>;
    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    get countControl(): UntypedFormControl {
        return this.form.get(FormControlNames.count) as UntypedFormControl;
    }

    get allTicketsControl(): UntypedFormControl {
        return this.form.get(FormControlNames.allTickets) as UntypedFormControl;
    }

    get quotaCounterSourceControl(): UntypedFormControl {
        return this.form.get(FormControlNames.quotaCounterSource) as UntypedFormControl;
    }

    constructor(
        private _breakpointObserver: BreakpointObserver,
        private _dialogRef: MatDialogRef<NnzQuotaPartialApplyDialogComponent>,
        @Inject(MAT_DIALOG_DATA) private _data: NnzPartialApplyData,
        private _fb: UntypedFormBuilder,
        private _standardVenueTemplateSrv: StandardVenueTemplateBaseService,
        private _standardVenueTemplatePartialChangesSrv: StandardVenueTemplatePartialChangesService
    ) {
        this._dialogRef.addPanelClass([DialogSize.LARGE, 'no-horizontal-padding']);
    }

    ngOnInit(): void {
        this.initForm();
        this.setNnz();
        this.setSector();
        this.setQuotaCounterTarget();
        this.setQuotaCounterSources();
        this.setModifiedQuotaCounterSources();
        this.setInteractionConditions();
        this.quotaCounterSourceControlChangeHandler();
        this.allTicketsControlChangeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cancel(): void {
        this._dialogRef.close();
    }

    save(): void {
        this._standardVenueTemplatePartialChangesSrv.quotaPartialApply(
            this._quotaCounterTarget,
            Array.from(this._modifiedQuotaCounterSources.values()),
            this._data.nnz
        );
        this._dialogRef.close(true);
    }

    assign(): void {
        if (!this.form.valid) {
            this.form.markAllAsTouched();
        } else {
            const quotaCounterSource: VmQuotaCounterSource = this.quotaCounterSourceControl.value;
            this.addModifiedQuotaCounterSource(quotaCounterSource);
            this.disableQuotaCounterSource(quotaCounterSource.label.id);
            this.incrementQuotaCounterTarget(quotaCounterSource.label.id);
            this.quotaCounterSourceControl.reset(this._initialFormValues.quotaCounterSource);
        }
    }

    delete(modifiedQuotaCounterSource: VmQuotaCounter): void {
        this.decrementQuotaCounterTarget(modifiedQuotaCounterSource);
        this.deleteModifiedQuotaCounterSource(modifiedQuotaCounterSource);
        this.enableQuotaCounterSource(modifiedQuotaCounterSource.label.id);
    }

    private initForm(): void {
        this._maxValue = this.getMaxValue();
        this.form = this._fb.group({
            [FormControlNames.quotaCounterSource]: this._initialFormValues.quotaCounterSource,
            [FormControlNames.allTickets]: [{ value: this._initialFormValues.allTickets, disabled: true }, Validators.required],
            [FormControlNames.count]: [{ value: this._initialFormValues.count, disabled: true }, [
                Validators.required,
                Validators.min(1),
                (control: AbstractControl) =>
                    Validators.max(this._maxValue || 1)(control)
            ]]
        });
    }

    private setNnz(): void {
        this.nnz$ = of(this._data.nnz.name);
        this.nnzCapacity$ = of(this._data.nnz.capacity);
    }

    private setSector(): void {
        this.sector$ = of(this._data.nnz.sectorName);
    }

    private setQuotaCounterTarget(): void {
        this.quotaCounterTarget$ = this._quotaCounterTargetBS.asObservable();
        const quotaCounterTarget = this._data.nnz.quotaCounters
            .find(quotaCounter => this._data.label.id === quotaCounter.quota.toString());

        this._quotaCounterTarget = {
            previousCount: quotaCounterTarget ? quotaCounterTarget.count : 0,
            count: 0,
            label: this._data.label
        };
        this._quotaCounterTargetBS.next(this._quotaCounterTarget);
    }

    private setQuotaCounterSources(): void {
        this.quotaCounterSources$ = this._quotaCounterSourcesBS.asObservable();
        const quotaLabels = this.getQuotaLabels();
        this._quotaCounterSources = this.getQuotaCounterSources(quotaLabels);
        this._quotaCounterSourcesBS.next(Array.from(this._quotaCounterSources.values()));
    }

    private getQuotaLabels(): VenueTemplateLabel[] {
        let quotaLabels: VenueTemplateLabel[];
        this._standardVenueTemplateSrv.getLabelGroups$()
            .pipe(take(1))
            .subscribe(labelGroups => quotaLabels = labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.quota).labels);
        return quotaLabels;
    }

    private getQuotaCounterSources(quotaLabels: VenueTemplateLabel[]): Map<string, VmQuotaCounterSource> {
        const quotaCounterSourcesMap = this._data.nnz.quotaCounters
            .filter(quotaCounter =>
                this._data.label.id !== quotaCounter.quota.toString()
                && quotaCounter.count > 0
                && quotaCounter.available > 0
            )
            .map(quotaCounter => this.getQuotaCounterSource(quotaCounter, quotaLabels));
        return new Map<string, VmQuotaCounterSource>(quotaCounterSourcesMap);
    }

    private getQuotaCounterSource(quotaCounter: QuotaCounter, quotaLabels: VenueTemplateLabel[]): [string, VmQuotaCounterSource] {
        const quotaLabelCounter = quotaLabels.find(quotaLabel => quotaLabel.id === quotaCounter.quota.toString());
        return [quotaLabelCounter.id.toString(), {
            label: quotaLabelCounter,
            count: quotaCounter.count,
            available: quotaCounter.available,
            disabled: false
        }];
    }

    private setModifiedQuotaCounterSources(): void {
        this.modifiedQuotaCounterSources$ = this._modifiedQuotaCounterSourcesBS.asObservable();
    }

    private setInteractionConditions(): void {
        this.isSaveDisabled$ = this._modifiedQuotaCounterSourcesBS
            .pipe(map(modifiedQuotaCounterSources => !modifiedQuotaCounterSources?.length));

        this.isAssignDisabled$ = this.quotaCounterSourceControl.valueChanges
            .pipe(
                startWith(null),
                map(quotaCounterSource => !quotaCounterSource)
            );
    }

    private quotaCounterSourceControlChangeHandler(): void {
        this.quotaCounterSourceControl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(quotaCounterSource => {
                this._maxValue = this.getMaxValue(quotaCounterSource);
                this.enableAllTicketsControl(quotaCounterSource);
                this.allTicketsControl.reset(this._initialFormValues.allTickets);
            });
    }

    private getMaxValue(quotaCounterSource: VmQuotaCounterSource = null): number {
        let maxValue: number;
        if (quotaCounterSource) {
            maxValue = quotaCounterSource.available;
        } else {
            maxValue = this._initialFormValues.count;
        }
        return maxValue;
    }

    private enableAllTicketsControl(quotaCounterSource: VmQuotaCounterSource): void {
        if (quotaCounterSource) {
            this.allTicketsControl.enable({ emitEvent: false });
        } else {
            this.allTicketsControl.disable({ emitEvent: false });
        }
    }

    private allTicketsControlChangeHandler(): void {
        this.allTicketsControl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(allTickets => {
                if (allTickets) {
                    this.countControl.reset(this._maxValue);
                    this.countControl.disable({ emitEvent: false });
                } else {
                    this.countControl.reset(this._initialFormValues.count);
                    this.countControl.enable({ emitEvent: false });
                }
            });
    }

    private addModifiedQuotaCounterSource(quotaCounterSource: VmQuotaCounter): void {
        const modifiedQuotaCounterSource: VmQuotaCounter = {
            label: quotaCounterSource.label,
            count: this.countControl.value
        };
        this._modifiedQuotaCounterSources.set(modifiedQuotaCounterSource.label.id, modifiedQuotaCounterSource);
        this._modifiedQuotaCounterSourcesBS.next(Array.from(this._modifiedQuotaCounterSources.values()).reverse());
    }

    private enableQuotaCounterSource(quotaCounterSourceId: string): void {
        let quotaCounterSource = this._quotaCounterSources.get(quotaCounterSourceId);
        quotaCounterSource = {
            ...quotaCounterSource,
            disabled: false
        };
        this._quotaCounterSources.set(quotaCounterSourceId, quotaCounterSource);
        this._quotaCounterSourcesBS.next(Array.from(this._quotaCounterSources.values()));
    }

    private incrementQuotaCounterTarget(modifiedQuotaCounterSourceId: string): void {
        this._quotaCounterTarget = {
            ...this._quotaCounterTarget,
            count: this._quotaCounterTarget.count + this._modifiedQuotaCounterSources.get(modifiedQuotaCounterSourceId).count
        };
        this._quotaCounterTargetBS.next(this._quotaCounterTarget);
    }

    private decrementQuotaCounterTarget(modifiedQuotaCounterSource: VmQuotaCounter): void {
        this._quotaCounterTarget = {
            ...this._quotaCounterTarget,
            count: this._quotaCounterTarget.count - modifiedQuotaCounterSource.count
        };
        this._quotaCounterTargetBS.next(this._quotaCounterTarget);
    }

    private deleteModifiedQuotaCounterSource(modifiedQuotaCounterSource: VmQuotaCounter): void {
        this._modifiedQuotaCounterSources.delete(modifiedQuotaCounterSource.label.id);
        this._modifiedQuotaCounterSourcesBS.next(Array.from(this._modifiedQuotaCounterSources.values()).reverse());
    }

    private disableQuotaCounterSource(quotaCounterSourceId: string): void {
        let quotaCounterSource = this._quotaCounterSources.get(quotaCounterSourceId);
        quotaCounterSource = {
            ...quotaCounterSource,
            disabled: true
        };
        this._quotaCounterSources.set(quotaCounterSourceId, quotaCounterSource);
        this._quotaCounterSourcesBS.next(Array.from(this._quotaCounterSources.values()));
    }
}
