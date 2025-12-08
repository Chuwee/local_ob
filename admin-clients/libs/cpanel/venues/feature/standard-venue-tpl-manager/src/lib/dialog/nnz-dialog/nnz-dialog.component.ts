import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { diffThanValidator } from '@admin-clients/shared/utility/utils';
import {
    Sector, StdVenueTplService, VENUE_MAP_SERVICE, VenueMapService, VenueTemplateItemType
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplateFieldsRestrictions, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnInit, Optional, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { distinctUntilChanged, map, startWith, take } from 'rxjs/operators';
import { VenueTemplateLabel } from '../../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../../models/label-group/venue-template-label-group-type.enum';
import { VenueTemplateZoneDialogData } from '../../models/venue-template-zone-dialog-data.model';
import { ZoneActionType } from '../../models/venue-tpl-tree-dialog-type.enum';
import { StandardVenueTemplateBaseService } from '../../services/standard-venue-template-base.service';
import { VenueTemplateTreeService } from '../../tree-view/venue-template-tree.service';
import { FormControlNames, FormGroupNames } from './form-names.enum';
import { QuotaComponent } from './quota/quota.component';
import { ZoneComponent } from './zone/zone.component';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        WizardBarComponent,
        ZoneComponent,
        QuotaComponent
    ],
    selector: 'app-zone-dialog',
    templateUrl: './nnz-dialog.component.html',
    styleUrls: ['./nnz-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NnzDialogComponent implements OnInit {
    private readonly _currentStepBS = new BehaviorSubject<number>(0);
    private _windowCommitLabelKey: string;
    @ViewChild(WizardBarComponent) private readonly _wizardBar: WizardBarComponent;
    form: UntypedFormGroup;
    formControlNames = FormControlNames;
    formGroupNames = FormGroupNames;
    isLoading$: Observable<boolean>;
    sectors$: Observable<Sector[]>;
    windowTitleKey: string;
    showCapacityField: boolean;
    showCapacityIncreaseInfo: boolean;
    showSectorSelector: boolean;
    showZoneName: boolean;
    showQuotaSelector: boolean;
    action: ZoneActionType;
    steps: { title: string; form: AbstractControl }[];
    currentStep$: Observable<number>;
    isPreviousDisabled$: Observable<boolean>;
    isNextDisabled$: Observable<boolean>;
    nextText$: Observable<string>;

    get nnzFormGroup(): UntypedFormGroup {
        return this.form.get(this.formGroupNames.nnz) as UntypedFormGroup;
    }

    get capacityControl(): UntypedFormControl {
        return this.nnzFormGroup.get(this.formControlNames.capacity) as UntypedFormControl;
    }

    get sectorControl(): UntypedFormControl {
        return this.nnzFormGroup.get(this.formControlNames.sector) as UntypedFormControl;
    }

    get nameControl(): UntypedFormControl {
        return this.nnzFormGroup.get(this.formControlNames.name) as UntypedFormControl;
    }

    get quotaFormGroup(): UntypedFormGroup {
        return this.form.get(this.formGroupNames.quota) as UntypedFormGroup;
    }

    get quotaControl(): UntypedFormGroup {
        return this.quotaFormGroup.get(this.formControlNames.quota) as UntypedFormGroup;
    }

    get capacityDeltaControl(): UntypedFormGroup {
        return this.quotaFormGroup.get(this.formControlNames.capacityDelta) as UntypedFormGroup;
    }

    constructor(
        private _dialogRef: MatDialogRef<NnzDialogComponent>,
        private _venueTemplateTreeSrv: VenueTemplateTreeService,
        private _venueTemplatesSrv: VenueTemplatesService,
        private _stdVenueTplSrv: StdVenueTplService,
        @Inject(VENUE_MAP_SERVICE) @Optional() private _venueMapSrv: VenueMapService,
        private _standardVenueTemplateSrv: StandardVenueTemplateBaseService,
        private _fb: UntypedFormBuilder,
        private _translate: TranslateService,
        @Inject(MAT_DIALOG_DATA) private _data: VenueTemplateZoneDialogData) {
        this._venueMapSrv ??= this._stdVenueTplSrv;
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.initDialog();
        this.initForm();
        this.initSteps();
        this.setLoading();
        this.setSteps();
        this.setSectors();
    }

    close(zoneId: number = null): void {
        this._dialogRef.close(zoneId);
    }

    goToStep(step: number): void {
        this.setStep(step);
    }

    nextStep(): void {
        if (this._currentStepBS.value === this.steps.length - 1) {
            this.commitForm();
        } else {
            this.setStep(this._currentStepBS.value + 1);
        }
    }

    previousStep(): void {
        this.setStep(this._currentStepBS.value - 1);
    }

    mapToStepsTitles(steps: { title: string }[]): string[] {
        return steps.map(step => step.title);
    }

    private setLoading(): void {
        this.isLoading$ = this._stdVenueTplSrv.isNnzSaving$();
    }

    private setSectors(): void {
        if (this.showSectorSelector) {
            this.sectors$ = this._venueMapSrv.getVenueMap$()
                .pipe(map(venueMap => venueMap.sectors));
        }
    }

    private initDialog(): void {
        this.action = this._data.action;
        this.showCapacityField = this._data.capacityEditable
            && this._data.action !== ZoneActionType.clone
            && this._data.action !== ZoneActionType.editName
            && !this._data.venueTemplate.graphic;
        switch (this._data.action) {
            case ZoneActionType.create:
            case ZoneActionType.increaseCreate:
                this.windowTitleKey = 'VENUE_TPL_MGR.ACTIONS.ADD_NNZ DONE';
                this._windowCommitLabelKey = 'FORMS.ACTIONS.CREATE';
                this.showSectorSelector = true;
                this.showZoneName = true;
                break;
            case ZoneActionType.clone:
                this.windowTitleKey = 'VENUE_TPL_MGR.ACTIONS.CLONE_NNZ DONE';
                this._windowCommitLabelKey = 'FORMS.ACTIONS.CLONE';
                this.showSectorSelector = false;
                this.showZoneName = true;
                break;
            case ZoneActionType.editName:
                this.windowTitleKey = 'VENUE_TPL_MGR.ACTIONS.EDIT_NNZ_NAME';
                this._windowCommitLabelKey = 'FORMS.ACTIONS.SAVE';
                this.showSectorSelector = false;
                this.showZoneName = true;
                break;
            case ZoneActionType.editCapacity:
                this.windowTitleKey = 'VENUE_TPL_MGR.ACTIONS.EDIT_NNZ_CAPACITY';
                this._windowCommitLabelKey = 'FORMS.ACTIONS.SAVE';
                this.showSectorSelector = false;
                this.showQuotaSelector = true;
                this.showZoneName = false;
                break;
            case ZoneActionType.increaseEditCapacity:
                this.showCapacityIncreaseInfo = true;
                this.windowTitleKey = 'VENUE_TPL_MGR.ACTIONS.EDIT_NNZ_CAPACITY';
                this._windowCommitLabelKey = 'FORMS.ACTIONS.SAVE';
                this.showSectorSelector = false;
                this.showQuotaSelector = true;
                this.showZoneName = false;
                break;
        }
    }

    private initForm(): void {
        const form = this._fb.group({});
        this.setFormWithNnzFormGroup(form);
        this.setFormWithQuotaFormGroup(form);
        this.form = form;
    }

    private setFormWithNnzFormGroup(form: UntypedFormGroup): void {
        let minCapacity = 0;
        if (this._data.action === ZoneActionType.increaseEditCapacity) {
            minCapacity = Number.isInteger(this._data.zone?.record?.initialState?.capacity) ?
                this._data.zone?.record?.initialState?.capacity
                : this._data.zone?.capacity;
        }
        const nnzFormGroup = this._fb.group({
            [FormControlNames.sector]: [{ value: this._data.zone?.sector, disabled: false }, Validators.required],
            [FormControlNames.name]: [{ value: this._data.zone?.name, disabled: false },
            [Validators.required, Validators.maxLength(VenueTemplateFieldsRestrictions.zoneNameLength)]],
            [FormControlNames.capacity]: [{
                value: this._data.zone?.capacity,
                disabled: false
            }, [
                Validators.required,
                Validators.min(minCapacity)
            ]]
        });
        form.addControl(FormGroupNames.nnz, nnzFormGroup);
    }

    private setFormWithQuotaFormGroup(form: UntypedFormGroup): void {
        if (this.showQuotaSelector) {
            const quotaFormGroup = this._fb.group({
                [FormControlNames.quota]: [null, Validators.required],
                [FormControlNames.capacityDelta]: [null, Validators.required]
            });
            form.addControl(FormGroupNames.quota, quotaFormGroup);
            const initialCapacity = Number.isInteger(this._data.zone?.record?.initialState?.capacity) ?
                this._data.zone?.record?.initialState?.capacity :
                this._data.zone?.capacity;
            const capacityControl = form.get(FormGroupNames.nnz).get(FormControlNames.capacity);
            capacityControl.setValidators(Validators.compose([
                capacityControl.validator,
                diffThanValidator(initialCapacity)
            ]));
            capacityControl.updateValueAndValidity();
        }
    }

    private initSteps(): void {
        if (this.showQuotaSelector) {
            this.steps = [
                {
                    title: 'VENUE_TPL_MGR.TITLES.NNZ_CAPACITY_INCREASE_STEP',
                    form: this.nnzFormGroup
                },
                {
                    title: 'VENUE_TPL_MGR.TITLES.NNZ_QUOTA_STEP_ASSIGN_STEP',
                    form: this.quotaFormGroup
                }
            ];
        } else {
            this.steps = [
                {
                    title: '',
                    form: this.nnzFormGroup
                }
            ];
        }
    }

    private setSteps(): void {
        this.currentStep$ = this._currentStepBS.asObservable();
        this.nextText$ = this._currentStepBS.asObservable()
            .pipe(
                map(currentStep => {
                    if (currentStep === this.steps.length - 1) {
                        return this._translate.instant(this._windowCommitLabelKey);
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

    private commitForm(): void {
        if (this.form.valid) {
            switch (this.action) {
                case ZoneActionType.create:
                    this._stdVenueTplSrv.createZone(
                        this._data.venueTemplate.id,
                        {
                            itemType: VenueTemplateItemType.notNumberedZone,
                            name: this.nameControl.value,
                            capacity: this.capacityControl.value,
                            sector: this.sectorControl.value
                        })
                        .subscribe(({ id }) => this.close(id));
                    break;
                case ZoneActionType.clone:
                    this._stdVenueTplSrv.cloneZone(
                        this._data.venueTemplate.id, this._data.zone.id, this.sectorControl.value, this.nameControl.value
                    )
                        .subscribe(({ id }) => this.close(id));
                    break;
                case ZoneActionType.increaseCreate:
                    this._venueTemplateTreeSrv.createNnzCapacityIncrease(
                        this.sectorControl.value,
                        this.nameControl.value,
                        this.capacityControl.value,
                        this.getDefaultQuotaLabel()
                    ).subscribe(zoneId => this.close(zoneId));
                    break;
                case ZoneActionType.editName:
                    this._data.zone.name = this.nameControl.value;
                    this._stdVenueTplSrv.updateZone(
                        this._data.venueTemplate.id,
                        {
                            itemType: VenueTemplateItemType.notNumberedZone,
                            id: this._data.zone.id,
                            name: this._data.zone.name,
                            capacity: undefined
                        })
                        .subscribe(() => this.close(this._data.zone.id));
                    break;
                case ZoneActionType.editCapacity:
                    this._stdVenueTplSrv.updateZone(
                        this._data.venueTemplate.id,
                        {
                            itemType: VenueTemplateItemType.notNumberedZone,
                            id: this._data.zone.id,
                            name: this.showZoneName ? this.nameControl.value : undefined,
                            capacity: this.showCapacityField ? this.capacityControl.value : undefined,
                            quotaCounters: this.showQuotaSelector ? [{
                                quota: +this.quotaControl.value.label.id,
                                count: this.capacityDeltaControl.value,
                                itemType: VenueTemplateItemType.notNumberedZoneQuotaCounters
                            }] : undefined
                        })
                        .subscribe(() => this.close(this._data.zone.id));
                    break;
                case ZoneActionType.increaseEditCapacity:
                    this._venueTemplateTreeSrv.updateNnzCapacityIncrease(
                        this._data.zone,
                        this.nameControl.value,
                        this.capacityControl.value,
                        {
                            quota: +this.quotaControl.value.label.id,
                            countDelta: this.capacityDeltaControl.value
                        }
                    );
                    this.close(this._data.zone.id);
                    break;
            }
        }
    }

    private setStep(step: number): void {
        this._wizardBar.setActiveStep(step);
        this._currentStepBS.next(step);
    }

    private getDefaultQuotaLabel(): VenueTemplateLabel {
        let quotaLabels: VenueTemplateLabel[];
        this._standardVenueTemplateSrv.getLabelGroups$()
            .pipe(take(1))
            .subscribe(labelGroups => quotaLabels = labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.quota).labels);
        return quotaLabels.find(quotaLabel => quotaLabel.default);
    }
}
