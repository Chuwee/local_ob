import { FORM_CONTROL_ERRORS } from '@OneboxTM/feature-form-control-errors';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge, noDuplicateValuesValidatorFn } from '@admin-clients/shared/utility/utils';
import { VenueTemplateQuota, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatList, MatListItem } from '@angular/material/list';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, take } from 'rxjs/operators';

@Component({
    selector: 'app-activity-venue-template-quotas',
    templateUrl: './activity-venue-template-quotas.component.html',
    styleUrls: ['./activity-venue-template-quotas.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, TranslatePipe, ReactiveFormsModule, MatFormField, MatInput, MatError,
        MatIcon, MatList, MatButton, MatIconButton, FlexLayoutModule, MatTooltip, MatList,
        MatListItem, MatLabel
    ]
})
export class ActivityVenueTemplateQuotasComponent implements OnInit {
    readonly #formErrors = inject(FORM_CONTROL_ERRORS);
    readonly #onDestroy = inject(DestroyRef);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    #templateId: number;
    newQuotaForm: UntypedFormControl;
    quotasForm: UntypedFormArray;
    quotas$: Observable<VenueTemplateQuota[]>;
    requestsInProgress$: Observable<boolean>;

    @Output() dataChanged = new EventEmitter<void>();
    @Input() isReadOnly: boolean = false;

    ngOnInit(): void {
        this.quotas$ = this.#venueTemplatesSrv.getVenueTemplateQuotas$();
        this.#venueTemplatesSrv.venueTpl.get$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#onDestroy))
            .subscribe(venueTemplate => {
                this.#templateId = venueTemplate.id;
                this.#venueTemplatesSrv.loadVenueTemplateQuotas(venueTemplate.id);
            });
        this.requestsInProgress$ = booleanOrMerge([
            this.#venueTemplatesSrv.isVenueTemplateQuotasLoading$(),
            this.#venueTemplatesSrv.isVenueTemplateQuotaSaving$()
        ]);
        this.initForm();
    }

    addNewQuota(): void {
        if (this.newQuotaForm.valid && this.newQuotaForm.value) {
            this.#venueTemplatesSrv.addVenueTemplateQuota(
                this.#templateId,
                {
                    name: this.newQuotaForm.value,
                    code: this.newQuotaForm.value
                })
                .subscribe(() => {
                    this.#ephemeralMessageService.showSuccess({
                        msgKey: 'VENUE_TPLS.CREATE_QUOTA_SUCCESS',
                        msgParams: { name: this.newQuotaForm.value }
                    });
                    this.#venueTemplatesSrv.loadVenueTemplateQuotas(this.#templateId);
                    this.dataChanged.emit();
                    this.newQuotaForm.patchValue('');
                    this.newQuotaForm.markAsPristine();
                });
        }
    }

    deleteQuota(quota: VenueTemplateQuota): void {
        this.#msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.WARNING',
            message: 'VENUE_TPLS.DELETE_QUOTA_WARNING',
            messageParams: { name: quota.name }
        })
            .subscribe(success => {
                if (success) {
                    this.#venueTemplatesSrv.deleteVenueTemplateQuota(this.#templateId, String(quota.id))
                        .subscribe(() => {
                            this.#ephemeralMessageService.showSuccess({
                                msgKey: 'VENUE_TPLS.DELETE_QUOTA_SUCCESS',
                                msgParams: { name: quota.name }
                            });
                            this.#venueTemplatesSrv.loadVenueTemplateQuotas(this.#templateId);
                            this.dataChanged.emit();
                        });
                }
            });

    }

    enable(): void {
        this.newQuotaForm.enable();
        this.quotasForm.enable();
    }

    disable(): void {
        this.newQuotaForm.disable();
        this.quotasForm.disable();
    }

    getArrayFormControl(formArray: UntypedFormArray, index: number): UntypedFormControl {
        return formArray.get(index + '.name') as UntypedFormControl;
    }

    private initForm(): void {
        this.quotasForm = this.#fb.array([]);
        this.newQuotaForm =
            this.#fb.control('', control => noDuplicateValuesValidatorFn(control, this.quotasForm.controls.map(c => c.get('name').value)));
        this.quotasForm.valueChanges.pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(() => {
                const editedControl = this.quotasForm.controls.find(control => control.dirty);
                if (editedControl) {
                    editedControl.markAsPristine();
                    if (this.quotasForm.valid) {
                        this.#venueTemplatesSrv.updateVenueTemplateQuota(this.#templateId,
                            {
                                id: editedControl.value.id,
                                name: editedControl.value.name
                            })
                            .subscribe(() => {
                                this.#ephemeralMessageService.showSaveSuccess();
                                this.#venueTemplatesSrv.loadVenueTemplateQuotas(this.#templateId);
                            });
                    } else {
                        this.#msgDialogService.showAlert({
                            size: DialogSize.SMALL,
                            title: 'TITLES.ERROR_DIALOG',
                            message: this.#formErrors.getErrorMessage(editedControl.get('name').errors),
                            messageParams: this.#formErrors.getErrorParameters(editedControl.get('name').errors)
                        });
                        this.quotas$.pipe(take(1)).subscribe(quotas => {
                            const controlQuota = quotas.find(quota => quota.id === editedControl.value.id);
                            editedControl.get('name').patchValue(controlQuota.name);
                        });
                    }
                }
            });
        this.quotas$.pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(quotas => {
            this.quotasForm.clear();
            quotas.forEach(quota => {
                const nameCtrl = this.#fb.group({
                    id: quota.id,
                    name: this.#fb.control(quota.name, {
                        updateOn: 'blur',
                        validators: [
                            Validators.required,
                            control => noDuplicateValuesValidatorFn(
                                control, this.quotasForm.controls.map(c => c.get('name')).filter(c => c !== control).map(c => c.value)
                            )
                        ]
                    })
                });
                this.quotasForm.push(nameCtrl);
            });
        });
    }
}
