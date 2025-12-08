import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatChipsModule } from '@angular/material/chips';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { ChipLabelPipe } from './label/chip-label.pipe';

export interface Chip {
    key?: string;
    label: string;
    value?: unknown;
    valueText?: string;
}

@Component({
    imports: [
        TranslatePipe, FlexLayoutModule, EllipsifyDirective, ChipLabelPipe, AsyncPipe, MatChipsModule,
        MatTooltip, MatIcon, MatDivider
    ],
    selector: 'app-chips',
    templateUrl: './chips.component.html',
    styleUrls: ['./chips.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChipsComponent {

    @Input() chips$: Observable<Chip[]>;
    @Input() labelText: string;
    @Input() removeText: string;
    @Input() showDivider = false;
    @Input() isRemoveAllShown = true;
    @Input() overrideClasses = '';
    @Input() isRemoveShow = true;

    @Output() readonly removeEmitter = new EventEmitter<Chip>();
    @Output() readonly removeAllEmitter = new EventEmitter<void>();

    trackBy = (index: number, chip: Chip): string => chip.key;

    removeChip(chip: Chip): void {
        this.removeEmitter.emit(chip);
    }

    removeChips(): void {
        this.removeAllEmitter.emit();
    }
}
