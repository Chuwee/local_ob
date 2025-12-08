import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

@Component({
    selector: 'app-import-collective-codes-header-match',
    templateUrl: './import-collective-codes-header-match.component.html',
    styleUrls: ['./import-collective-codes-header-match.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ImportCollectiveCodesHeaderMatchComponent {

    @Output()
    isLoading = new EventEmitter<boolean>();

    @Input()
    headerMatchFormGroup: UntypedFormGroup;

    @Input()
    headerMatchControlName: string;
}
