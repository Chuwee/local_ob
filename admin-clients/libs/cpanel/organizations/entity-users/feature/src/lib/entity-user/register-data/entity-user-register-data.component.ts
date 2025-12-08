
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'ob-entity-user-register-data',
    templateUrl: './entity-user-register-data.component.html',
    styleUrl: './entity-user-register-data.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe,
        RouterOutlet,
        MaterialModule,
        TranslatePipe,
        LastPathGuardListenerDirective,
        FlexLayoutModule,
        RouterLink
    ]
})
export class EntityUserRegisterDataComponent {
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);

    readonly deepPath$ = getDeepPath$(this.#router, this.#route);
}
