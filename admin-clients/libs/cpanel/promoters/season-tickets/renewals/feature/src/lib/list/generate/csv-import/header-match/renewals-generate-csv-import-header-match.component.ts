import { CsvModule } from '@admin-clients/shared/common/feature/csv';
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDialogContent } from '@angular/material/dialog';

@Component({
    selector: 'app-renewals-generate-csv-import-header-match',
    imports: [ReactiveFormsModule, CsvModule, MatDialogContent],
    templateUrl: './renewals-generate-csv-import-header-match.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RenewalsGenerateCsvImportHeaderMatchComponent {

    readonly isLoading = output<boolean>();
    readonly $headerMatchFormGroup = input.required<FormGroup>({ alias: 'headerMatchFormGroup' });
    readonly $headerMatchControlName = input.required<string>({ alias: 'headerMatchControlName' });
}
