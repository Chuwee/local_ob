import { BiService } from '@admin-clients/cpanel/bi/data-access';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-bi-user-details',
    templateUrl: './bi-user-details.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MatProgressSpinner, GoBackComponent, TranslatePipe, NavTabsMenuComponent, RouterOutlet]
})
export class BiUserDetailsComponent {
    readonly #biSrv = inject(BiService);
    readonly #route = inject(ActivatedRoute);

    readonly #id = this.#route.snapshot.params['userId'];
    readonly $user = toSignal(this.#biSrv.userDetails.get$());
    readonly $loading = toSignal(this.#biSrv.userDetails.loading$());

    constructor() {
        this.#biSrv.userDetails.load(this.#id);
    }
}
