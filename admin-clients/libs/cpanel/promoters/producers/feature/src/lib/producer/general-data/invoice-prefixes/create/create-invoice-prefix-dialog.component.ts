import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { PostProducerInvoicePrefix, ProducerInvoicePrefix } from '@admin-clients/cpanel/promoters/producers/data-access';
import { DialogSize, HelpButtonComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import {
    noDuplicateValuesValidatorStatic
} from '@admin-clients/shared/utility/utils';
import { Component, OnInit, ChangeDetectionStrategy, Inject, OnDestroy, AfterViewInit, ViewChild } from '@angular/core';
import { FlexModule } from '@angular/flex-layout/flex';
import { UntypedFormBuilder, UntypedFormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';

@Component({
    selector: 'app-create-invoice-prefix-dialog',
    templateUrl: './create-invoice-prefix-dialog.component.html',
    styleUrls: ['./create-invoice-prefix-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexModule, ReactiveFormsModule, MaterialModule,
        FormControlErrorsComponent, HelpButtonComponent, TranslatePipe
    ]
})
export class CreateInvoicePrefixDialogComponent implements OnInit, OnDestroy, AfterViewInit {
    @ViewChild(MatInput) private _input: MatInput;
    private _onDestroy = new Subject<void>();

    form: UntypedFormGroup;

    constructor(
        private _dialogRef: MatDialogRef<CreateInvoicePrefixDialogComponent>,
        private _formBuilder: UntypedFormBuilder,
        @Inject(MAT_DIALOG_DATA) private _data: { prefixes: ProducerInvoicePrefix[] }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.form = this._formBuilder.group({
            prefix: [
                null,
                {
                    validators: [
                        Validators.required,
                        Validators.minLength(1),
                        Validators.maxLength(10),
                        Validators.pattern('^[A-Z0-9/_#-]+$'),
                        noDuplicateValuesValidatorStatic(
                            this._data.prefixes?.map(producerInvoicePrefix => producerInvoicePrefix.prefix) || []
                        )
                    ]
                }]
        });
    }

    ngAfterViewInit(): void {
        setTimeout(() => this._input.focus(), 100); // focus first input improves UX
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(result = null): void {
        this._dialogRef.close(result);
    }

    save(): void {
        if (this.form.valid) {
            this.close(this.form.value as PostProducerInvoicePrefix);
        } else {
            this.form.markAllAsTouched();
            this.form.patchValue(this.form.value);
        }
    }

}
