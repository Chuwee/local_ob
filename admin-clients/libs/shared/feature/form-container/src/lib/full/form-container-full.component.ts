import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';
import { BaseFormContainerComponent } from '../base-form-container.component';

@Component({
    imports: [TranslatePipe, FlexLayoutModule, MatButton],
    selector: 'app-form-container-full',
    templateUrl: './form-container-full.component.html',
    styleUrls: ['./form-container-full.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class FormContainerFullComponent extends BaseFormContainerComponent {
    readonly $sidebarWidth = input('0', { alias: 'sidebarWidth' });

    constructor() {
        super();
    }
}
