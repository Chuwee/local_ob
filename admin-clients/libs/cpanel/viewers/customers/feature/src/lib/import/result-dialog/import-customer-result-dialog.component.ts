import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-import-customer-result-dialog',
    imports: [TranslatePipe, MatDialogModule, FlexLayoutModule, MatIconModule, MatButtonModule,
        MatListModule
    ],
    templateUrl: './import-customer-result-dialog.component.html',
    styleUrls: ['./import-customer-result-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImportCustomerResultDialogComponent {

    readonly #dialogRef = inject(MatDialogRef<ImportCustomerResultDialogComponent>);
    readonly #data = inject<{ created: number; updated: number; errors: number; products: number }>(MAT_DIALOG_DATA);

    readonly created = this.#data.created;
    readonly updated = this.#data.updated;
    readonly errors = this.#data.errors;
    readonly products = this.#data.products;

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    close(): void {
        this.#dialogRef.close();
    }
}
