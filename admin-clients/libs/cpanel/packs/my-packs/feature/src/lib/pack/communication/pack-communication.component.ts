import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { entitiesProviders, EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatDividerModule } from '@angular/material/divider';
import { ActivatedRoute, Router, RouterModule, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-pack-communication',
    templateUrl: './pack-communication.component.html',
    styleUrls: ['./pack-communication.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatButtonToggleModule, RouterModule, RouterOutlet, LastPathGuardListenerDirective, TranslatePipe, AsyncPipe, MatDividerModule
    ],
    providers: [entitiesProviders]
})
export class PackCommunicationComponent implements OnInit {
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #entitySrv = inject(EntitiesService);
    readonly #packsSrv = inject(PacksService);

    deepPath$ = getDeepPath$(this.#router, this.#route);
    readonly $pack = toSignal(this.#packsSrv.pack.get$());

    ngOnInit(): void {
        this.#entitySrv.loadEntity(this.$pack().entity.id);
    }
}
