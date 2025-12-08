import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, ChangeDetectionStrategy, inject, input, computed, output } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatIconAnchor, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatToolbar } from '@angular/material/toolbar';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs';
import { User } from '@admin-clients/cpanel/core/data-access';
import { VersionManagementComponent } from '@admin-clients/shared/core/features';
import { PopoverUserComponent } from '../popover-user/popover-user.component';

@Component({
    imports: [
        PopoverUserComponent, TranslatePipe, MatIcon, MatIconButton,
        MatTooltip, MatToolbar, VersionManagementComponent, MatIconAnchor
    ],
    selector: 'app-topbar',
    templateUrl: './topbar.component.html',
    styleUrls: ['./topbar.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TopbarComponent {
    private readonly _env = inject(ENVIRONMENT_TOKEN);

    readonly sidenavToggled = output<boolean>();

    readonly $user = input<User>(null, { alias: 'user' });

    readonly $knowledgeBaseLink = computed(() => {
        switch (this.$user()?.language) {
            case 'es-ES':
            case 'es-CR':
            case 'ca-ES':
                return 'https://support.oneboxtm.com/hc/es';
            default:
                return 'https://support.oneboxtm.com/hc/en-us';
        }
    });

    readonly $isHandset = toSignal(inject(BreakpointObserver).observe(Breakpoints.Handset).pipe(map(result => result.matches)));

    readonly isLowEnv = this._env.env !== 'pro';

    sidenavToggle(event: boolean): void {
        this.sidenavToggled.emit(event);
    }

}
