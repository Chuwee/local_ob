import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, OnInit, signal } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';
import { BaseFormContainerComponent } from '../base-form-container.component';

@Component({
    selector: 'app-form-container',
    imports: [MatButton, MatDivider, CommonModule, FlexLayoutModule, TranslatePipe],
    templateUrl: './form-container.component.html',
    styleUrls: ['./form-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class FormContainerComponent extends BaseFormContainerComponent implements OnInit {
    private readonly MAIN_WIDTH = 1280; // Coupled with the css form-container
    private readonly MENU_WIDTH = 1052; // Coupled with the css form-container
    private readonly SIDEBAR_WIDTH = 200; // Coupled with the css form-container

    readonly $maxWidth = signal(this.MENU_WIDTH + 'px');
    readonly $sidebarWidth = signal(this.SIDEBAR_WIDTH + 'px');
    readonly $layoutAlign = signal('center');

    readonly $layout = input<'main' | 'menu' | 'list' | ''>('list', { alias: 'layout' });
    readonly $cancelButtonText = input('FORMS.ACTIONS.CANCEL', { alias: 'cancelButtonText' });
    readonly $saveButtonText = input('FORMS.ACTIONS.UPDATE', { alias: 'saveButtonText' });
    readonly $sidebar = input(false, { alias: 'sidebar', transform: val => coerceBooleanProperty(val) });

    constructor() {
        super();
    }

    ngOnInit(): void {
        if (this.$layout() === 'list') {
            this.setLayoutParameters(this.MENU_WIDTH, 'center');
        } else if (this.$layout() === 'main') {
            this.setLayoutParameters(this.MAIN_WIDTH, 'center');
        } else if (this.$layout() === 'menu') {
            this.setLayoutParameters(this.MENU_WIDTH, 'start');
        }
    }

    private setLayoutParameters(width: number, layoutAlign: string): void {
        this.$maxWidth.set((width - (this.$sidebar() ? this.SIDEBAR_WIDTH : 0)) + 'px');
        this.$layoutAlign.set(layoutAlign);
    }
}
