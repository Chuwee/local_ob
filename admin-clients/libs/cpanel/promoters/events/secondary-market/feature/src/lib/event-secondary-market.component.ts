import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'ob-event-secondary-market',
    imports: [MatButtonToggleModule, TranslatePipe, RouterOutlet, RouterLink,
        LastPathGuardListenerDirective],
    templateUrl: './event-secondary-market.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventSecondaryMarketComponent {

    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    readonly $deepPath = toSignal(getDeepPath$(this.#router, this.#route));
}
