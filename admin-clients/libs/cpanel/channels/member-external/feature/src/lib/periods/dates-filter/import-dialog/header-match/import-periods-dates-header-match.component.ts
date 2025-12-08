import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CsvModule } from '@admin-clients/shared/common/feature/csv';
import { MatDialogContent } from '@angular/material/dialog';

@Component({
    imports: [
        CsvModule,
        MatDialogContent,
        ReactiveFormsModule
    ],
    selector: 'app-import-periods-dates-header-match',
    templateUrl: './import-periods-dates-header-match.component.html',
    styleUrls: ['./import-periods-dates-header-match.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImportPeriodsDatesHeaderMatchComponent {

    @Output()
    isLoading = new EventEmitter<boolean>();

    @Input()
    headerMatchFormGroup: FormGroup;

    @Input()
    headerMatchControlName: string;
}
