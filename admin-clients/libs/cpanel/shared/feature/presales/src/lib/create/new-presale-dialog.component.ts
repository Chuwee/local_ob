import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { CollectiveValidationMethod, CollectivesService } from '@admin-clients/cpanel/collectives/data-access';
import { Presale, PresalePost, PRESALES_SERVICE, ValidatorTypes } from '@admin-clients/cpanel/shared/data-access';
import { ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { notEmpty } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, ElementRef, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Validators, FormsModule, ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatSelect, MatOption } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-new-presale-dialog',
    templateUrl: './new-presale-dialog.component.html',
    styleUrls: ['./new-presale-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormsModule, ReactiveFormsModule, SelectSearchComponent, FormControlErrorsComponent,
        TranslatePipe, AsyncPipe, PrefixPipe, MatIcon, MatFormField, MatSelect, MatOption, MatButton,
        MatDialogTitle, MatDialogContent, MatDialogActions, MatLabel, MatRadioButton, MatRadioGroup,
        MatDivider, MatProgressSpinner, MatIconButton, MatInput
    ]
})
export class NewPresaleDialogComponent implements OnInit, OnDestroy {
    readonly #dialogRef = inject(MatDialogRef<NewPresaleDialogComponent, Presale>);
    readonly #collectivesSrv = inject(CollectivesService);
    readonly #presalesSrv = inject(PRESALES_SERVICE);
    readonly #fb = inject(FormBuilder);
    readonly #elemRef = inject(ElementRef);
    readonly #destroyRef = inject(DestroyRef);
    readonly #data = inject<{
        entityId: number;
        onlyCustomersValidation: boolean;
        externalInventoryProvider: ExternalInventoryProviders;
    }>(MAT_DIALOG_DATA);

    readonly onlyCustomersValidation = this.#data.onlyCustomersValidation ?? false;
    readonly validatorTypes = ValidatorTypes;

    readonly form = this.#fb.group({
        type: [null as ValidatorTypes, Validators.required],
        collective: [{ value: null, disabled: true }, [Validators.required]],
        presale_name: [{ value: null, disabled: true }, [Validators.required, Validators.maxLength(50), notEmpty()]],
        inventory_provider: ['internal' as 'internal' | ExternalInventoryProviders, [Validators.required]],
        presale_external_id: [{ value: null, disabled: true }, [Validators.required]]
    });

    readonly collectives$ = this.#collectivesSrv.getCollectivesListData$();
    readonly isLoading$ = this.#collectivesSrv.isCollectiveListLoading$();
    readonly externalProvider = this.#data.externalInventoryProvider;
    readonly externalPresales$ = this.#presalesSrv.getExternalPresales$();
    readonly loadingExtPresales$ = this.#presalesSrv.externalPresalesLoading$();

    selectedInventoryProvider: 'internal' | ExternalInventoryProviders = 'internal';

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.#collectivesSrv.fetchCollectives({
            entity_id: this.#data.entityId,
            validation_method: [
                CollectiveValidationMethod.promotionalCode,
                CollectiveValidationMethod.user,
                CollectiveValidationMethod.userCodePassword,
                CollectiveValidationMethod.userPassword
            ]
        });

        this.form.controls.type.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(value => {
                if (value === this.validatorTypes.collective) {
                    this.form.controls.collective.reset();
                    this.form.controls.collective.enable();
                    this.form.controls.presale_name.reset();
                    this.form.controls.presale_name.enable();
                } else if (value === this.validatorTypes.customers) {
                    this.form.controls.collective.disable();
                    this.form.controls.presale_name.reset();
                    this.form.controls.presale_name.enable();
                }
            });

        this.form.controls.inventory_provider.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(value => {
                if (value === 'internal') {
                    this.form.controls.presale_external_id.disable();
                    this.form.controls.presale_external_id.reset();
                    this.form.controls.type.reset();
                    this.selectedInventoryProvider = null;
                } else {
                    this.#presalesSrv.loadExternalPresales();
                    this.selectedInventoryProvider = value;
                    this.form.controls.presale_external_id.enable();
                    this.form.controls.presale_external_id.reset();
                    this.form.controls.type.setValue(this.validatorTypes.customers);
                }
            });

        if (this.onlyCustomersValidation) this.form.controls.type.setValue(this.validatorTypes.customers);
    }

    ngOnDestroy(): void {
        this.#presalesSrv.clearExternalPresales();
    }

    createPresale(): void {
        if (this.form.valid) {
            const presale: PresalePost = {
                validator_type: this.form.value.type,
                validator_id: this.form.value.collective,
                name: this.form.value.presale_name
            };
            if (this.form.value.inventory_provider !== 'internal') {
                presale.additional_config = {
                    inventory_provider: this.form.value.inventory_provider,
                    external_presale_id: this.form.value.presale_external_id,
                    entity_id: this.#data.entityId
                };
            }
            this.#presalesSrv.create(presale).subscribe(presale => this.close(presale));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
        }
    }

    close(presale: Presale = null): void {
        this.#dialogRef.close(presale);
    }

}
