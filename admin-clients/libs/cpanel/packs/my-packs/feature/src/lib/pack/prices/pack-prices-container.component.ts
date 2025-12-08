import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatDividerModule } from '@angular/material/divider';
import { ActivatedRoute, Router, RouterModule, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-pack-prices-container',
    templateUrl: './pack-prices-container.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatButtonToggleModule, RouterModule, RouterOutlet, LastPathGuardListenerDirective, TranslatePipe, AsyncPipe, MatDividerModule
    ]
})
export class PackPricesContainerComponent {
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);

    deepPath$ = getDeepPath$(this.#router, this.#route);
}
