import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { Component, ChangeDetectionStrategy, Input, EventEmitter, inject } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatToolbar } from '@angular/material/toolbar';
import { TranslatePipe } from '@ngx-translate/core';
import { VersionManagementComponent } from '@admin-clients/shared/core/features';
import { PopoverUserComponent } from '../popover-user/popover-user.component';

@Component({
    imports: [PopoverUserComponent, MatToolbar, MatIcon, MatIconButton, TranslatePipe, VersionManagementComponent],
    selector: 'app-topbar',
    templateUrl: './topbar.component.html',
    styleUrls: ['./topbar.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TopbarComponent {
    private _env = inject(ENVIRONMENT_TOKEN);

    @Input() sidenavToggled = new EventEmitter<boolean>();

    isLowEnv = this._env?.env !== 'pro';

    sidenavToggle(event: boolean): void {
        this.sidenavToggled.emit(event);
    }

}
