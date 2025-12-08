import { CsvModule } from '@admin-clients/shared/common/feature/csv';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';

@Component({
    selector: 'app-import-barcodes-header-match',
    templateUrl: './import-barcodes-header-match.component.html',
    styleUrls: ['./import-barcodes-header-match.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, ReactiveFormsModule, CsvModule
    ]
})
export class ImportBarcodesHeaderMatchComponent {

    @Output()
    isLoading = new EventEmitter<boolean>();

    @Input()
    headerMatchFormGroup: UntypedFormGroup;

    @Input()
    headerMatchControlName: string;
}
