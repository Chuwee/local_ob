import { CsvModule } from '@admin-clients/shared/common/feature/csv';
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';

@Component({
    selector: 'app-import-customer-header-match',
    imports: [ReactiveFormsModule, MatDialogModule, CsvModule],
    templateUrl: './import-customer-header-match.component.html',
    styleUrls: ['./import-customer-header-match.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImportCustomerHeaderMatchComponent {

    isLoading = output<boolean>();

    $headerMatchFormGroup = input.required<UntypedFormGroup>({ alias: 'headerMatchFormGroup' });
    $headerMatchControlName = input.required<string>({ alias: 'headerMatchControlName' });;
}
