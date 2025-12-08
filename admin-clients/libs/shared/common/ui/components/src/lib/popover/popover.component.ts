import { NgTemplateOutlet, UpperCasePipe } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    Output,
    TemplateRef,
    ViewChild
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { SatPopoverComponent, SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import { PopoverDirective } from './popover.directive';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatButtonToggleModule,
        MatIconModule,
        MatCardModule,
        MatTooltipModule,
        UpperCasePipe,
        NgTemplateOutlet,
        SatPopoverModule,
        TranslatePipe,
        FlexLayoutModule,
        PopoverDirective,
        MatButtonModule
    ],
    selector: 'app-popover',
    styleUrls: ['./popover.component.scss'],
    templateUrl: './popover.component.html'
})
export class PopoverComponent {
    @ViewChild('popover') popover: SatPopoverComponent;

    @Input() contentNoPadding: boolean;
    @Input() yOffset?: string;
    @Output() opened: EventEmitter<boolean> = new EventEmitter();

    @Input() isTextButtonRange = false;
    @Input() buttonText: string;
    @Input() buttonIconStart: string;
    @Input() buttonIconEnd = 'filter_list';
    @Input() textButtonTemplateRef: TemplateRef<unknown>;
    @Output() changesEmitter = new EventEmitter<void>();

    @Input() removeButtonText: string;
    @Input() removeButtonTooltipText: string;
    @Input() removeButton: boolean;
    @Input() hideRemoveBtn = false;
    @Input() isOnRemoveBtnClickEnabled = true;
    @Output() removeEmitter = new EventEmitter<void>();

    @Input() disableRipple = false;

    setOpened(isOpened: boolean): void {
        this.opened.emit(isOpened);
    }

    openPopover(): void {
        this.popover.open();
    }

    onCloseBtnClick(): void {
        this.popover.close();
    }

    onApplyBtnClick(): void {
        this.popover.close();
        this.changesEmitter.emit();
    }

    onRemoveBtnClick(): void {
        if (this.isOnRemoveBtnClickEnabled) {
            this.popover.close();
            this.removeEmitter.emit();
        }
    }
}
