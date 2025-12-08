import { ChangeDetectionStrategy, Component, HostAttributeToken, inject, Input } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

@Component({
    selector: 'app-csv-selector',
    templateUrl: './csv-selector.component.html',
    styleUrls: ['./csv-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CsvSelectorComponent {
    readonly class: string = inject(new HostAttributeToken('class'), { optional: true });

    @Input()
    csvFormGroup: UntypedFormGroup;

    @Input()
    csvControlName: string;

    @Input()
    importInfoText = 'CSV.IMPORT.INFO';

    @Input()
    exampleCsv: unknown;

    @Input()
    showInfo = true;
}
