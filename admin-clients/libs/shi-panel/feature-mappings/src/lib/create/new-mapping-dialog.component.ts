import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SuppliersApi, SuppliersService, SuppliersState } from '@admin-clients/shi-panel/data-access-grant-suppliers';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, ChangeDetectionStrategy, inject, ElementRef, OnInit, DestroyRef, Inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, shareReplay, switchMap, tap } from 'rxjs';
import { MappingsService } from '../mappings.service';
import { PostMapping } from '../models/post-mapping.model';

@Component({
    imports: [CommonModule, MaterialModule, TranslatePipe, FormControlErrorsComponent, ReactiveFormsModule, FlexLayoutModule],
    providers: [SuppliersService, SuppliersApi, SuppliersState],
    selector: 'app-new-mapping-dialog',
    styleUrls: ['./new-mapping-dialog.component.scss'],
    templateUrl: './new-mapping-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewMappingDialogComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #mappingsService = inject(MappingsService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #elemRef = inject(ElementRef);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #suppliersService = inject(SuppliersService);

    readonly suppliers$ = this.#suppliersService.suppliers.getSuppliers$().pipe(
        filter(Boolean),
        map(suppliersList => suppliersList.map(supplier => ({ id: supplier.name, name: `MAPPINGS.SUPPLIER_OPTS.${supplier.name}` }))),
        shareReplay(1)
    );

    readonly form = this.#fb.group({
        shi_id: [null, Validators.required],
        supplier: [null, Validators.required],
        supplier_id: [null, Validators.required],
        favorite: [false]
    });

    readonly favoriteAvailable$ = this.form.controls['supplier'].valueChanges.pipe(
        filter(Boolean),
        switchMap(supplier => this.#mappingsService.list.getFavorites(supplier)),
        map(response => response.available),
        tap(available => {
            if (available) {
                this.form.controls['favorite'].enable();
            } else {
                this.form.controls['favorite'].disable();
            }
        })
    );

    readonly loadingData$ = this.#mappingsService.list.loading$();

    constructor(
        private _dialogRef: MatDialogRef<NewMappingDialogComponent>,
        @Inject(MAT_DIALOG_DATA) private _data: { favoriteAvaialble: boolean }
    ) {
        this._dialogRef.addPanelClass(DialogSize.SMALL);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.#suppliersService.suppliers.load();
        this.form.controls['favorite'].disable();
        this.form.controls['supplier'].valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(() => {
            this.form.controls['favorite'].reset();
        });
    }

    createMapping(): void {
        if (this.form.valid) {
            const mapping: PostMapping = {
                shi_id: this.form.value.shi_id,
                supplier: this.form.value.supplier,
                supplier_id: this.form.value.supplier_id,
                favorite: this.form.value.favorite
            };
            this.#mappingsService.list.save(mapping)
                .subscribe({
                    next: mappingId => {
                        this.close(mappingId);
                    },
                    error: (error: HttpErrorResponse) => {
                        if (error.status === 409) {
                            this.#msgDialogService.showAlert({
                                size: DialogSize.SMALL,
                                title: 'TITLES.MAPPINGS.ERROR_DIALOG.' + error.error.code,
                                message: 'MAPPINGS.ERROR_DIALOG.' + error.error.code
                            });
                        }
                    }
                });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
        }
    }

    close(mappingId: string = null): void {
        this._dialogRef.close(mappingId);
    }
}
