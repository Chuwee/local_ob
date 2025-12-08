import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SuppliersApi, SuppliersService, SuppliersState } from '@admin-clients/shi-panel/data-access-grant-suppliers';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs';

@Component({
    imports: [
        CommonModule, MaterialModule, ReactiveFormsModule, FormControlErrorsComponent, TranslatePipe, FlexLayoutModule
    ],
    selector: 'app-supplier-selection-dialog',
    templateUrl: './supplier-selection-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [SuppliersService, SuppliersApi, SuppliersState]
})
export class SupplierSelectionDialogComponent {
    private readonly _suppliersService = inject(SuppliersService);

    supplierForm: FormGroup = this._fb.group({
        supplier: [null, Validators.required]
    });

    readonly suppliers$ = this._suppliersService.suppliers.getSuppliers$().pipe(
        filter(Boolean),
        map(suppliersList => suppliersList.map(supplier => ({ id: supplier.name, name: `MATCHINGS.SUPPLIER_OPTS.${supplier.name}` })))
    );

    constructor(
        private _dialogRef: MatDialogRef<SupplierSelectionDialogComponent, string>,
        private _fb: FormBuilder
    ) {
        this._dialogRef.addPanelClass(DialogSize.SMALL);
        this._dialogRef.disableClose = false;

        this._suppliersService.suppliers.load();
    }

    selectClick(): void {
        if (this.isValid()) {
            this.close(this.supplierForm.value.supplier.id);
        }
    }

    close(supplier: string = null): void {
        this._dialogRef.close(supplier);
    }

    private isValid(): boolean {
        if (this.supplierForm.valid) {
            return true;
        } else {
            this.supplierForm.markAllAsTouched();
            return false;
        }
    }
}
