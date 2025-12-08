import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

@Component({
    selector: 'app-import-voucher-codes-header-match',
    templateUrl: './import-voucher-codes-header-match.component.html',
    styleUrls: ['./import-voucher-codes-header-match.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ImportVoucherCodesHeaderMatchComponent {

    @Output()
    isLoading = new EventEmitter<boolean>();

    @Input()
    headerMatchFormGroup: UntypedFormGroup;

    @Input()
    headerMatchControlName: string;
}
