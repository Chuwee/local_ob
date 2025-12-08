import { CsvModule } from '@admin-clients/shared/common/feature/csv';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { MatDialogContent } from '@angular/material/dialog';

@Component({
    selector: 'app-import-literals-header-match',
    imports: [
        MatDialogContent, CsvModule, ReactiveFormsModule
    ],
    templateUrl: './import-literals-header-match.component.html',
    styleUrls: ['./import-literals-header-match.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImportLiteralsHeaderMatchComponent {

    @Output()
    isLoading = new EventEmitter<boolean>();

    @Input()
    headerMatchFormGroup: UntypedFormGroup;

    @Input()
    headerMatchControlName: string;
}
