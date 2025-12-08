import { BuyersQueryDef } from '@admin-clients/cpanel-viewers-buyers-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BuyersQueryDialogData } from './model/buyers-query-dialog-data.model';

@Component({
    selector: 'app-buyers-query-dialog',
    templateUrl: './buyers-query-dialog.component.html',
    styleUrls: ['./buyers-query-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class BuyersQueryDialogComponent implements OnInit {

    form: UntypedFormGroup;
    title: string;

    constructor(
        private _dialogRef: MatDialogRef<BuyersQueryDialogComponent>,
        @Inject(MAT_DIALOG_DATA) private _data: BuyersQueryDialogData,
        private _fb: UntypedFormBuilder
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this.title = _data.title;
    }

    ngOnInit(): void {
        this.form = this._fb.group({
            name: [this._data.queryDef?.query_name || null, Validators.required],
            description: [this._data.queryDef?.query_description || null, Validators.required]
        });
    }

    save(): void {
        if (this.isValid()) {
            const v = this.form.value;
            this.close({
                query_name: v.name,
                query_description: v.description
            } as BuyersQueryDef);
        }
    }

    close(query: BuyersQueryDef = null): void {
        this._dialogRef.close(query);
    }

    private isValid(): boolean {
        if (this.form.valid) {
            return true;
        } else {
            this.form.markAllAsTouched();
            return false;
        }
    }
}
