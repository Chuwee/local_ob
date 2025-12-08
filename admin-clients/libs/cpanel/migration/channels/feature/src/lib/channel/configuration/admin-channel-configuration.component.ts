import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButtonToggle, MatButtonToggleGroup } from '@angular/material/button-toggle';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-admin-channel-configuration',
    templateUrl: './admin-channel-configuration.component.html',
    imports: [RouterOutlet, RouterLink, MatButtonToggleGroup, MatButtonToggle, TranslatePipe, LastPathGuardListenerDirective],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminChannelConfigurationComponent {
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    readonly $deepPath = toSignal(getDeepPath$(this.#router, this.#route));
}
