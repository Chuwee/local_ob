import { CsvModule } from '@admin-clients/shared/common/feature/csv';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';

@Component({
    selector: 'app-import-automatic-sale-header-match',
    templateUrl: './import-automatic-sale-header-match.component.html',
    styleUrls: ['./import-automatic-sale-header-match.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [CsvModule, ReactiveFormsModule, MaterialModule]
})
export class ImportAutomaticSaleHeaderMatchComponent {
    @Output()
    isLoading = new EventEmitter<boolean>();

    @Input()
    headerMatchFormGroup: UntypedFormGroup;

    @Input()
    headerMatchControlName: string;
}
