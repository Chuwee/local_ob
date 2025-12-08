import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { PickerDataItem } from './models/pickerData';

@Component({
    selector: 'picker',
    imports: [CommonModule, IonicModule, TranslatePipe],
    templateUrl: './picker.component.html',
    styleUrls: ['./picker.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PickerComponent implements OnInit {

    @Input() readonly items: PickerDataItem[];
    @Input() readonly selectedItems: string[] | number[];
    @Input() readonly buttonLabel?: string = 'BUTTONS.SAVE';
    @Input() readonly description?: boolean;
    @Output() readonly pickEmitter: EventEmitter<(string | number)[]> = new EventEmitter<(string | number)[]>();
    selectedValue: string | number;
    ngOnInit(): void {
        if (this.items && this.items.length > 0) {
            this.selectedValue = this.items[0].value;
        }
    }

    pick(): void {
        this.pickEmitter.emit([this.selectedValue]);
    }

    onIonChange(event: CustomEvent): void {
        this.selectedValue = event.detail.value;
    }
}
