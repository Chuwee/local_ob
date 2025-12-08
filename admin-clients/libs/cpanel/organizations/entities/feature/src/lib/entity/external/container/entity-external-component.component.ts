import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs';
import { ExternalEntityService } from '../service/external.service';

@Component({
    selector: 'app-entity-external-component',
    imports: [FlexLayoutModule, RouterModule, MatButtonToggleModule, TranslatePipe, LastPathGuardListenerDirective, AsyncPipe],
    templateUrl: './entity-external-component.component.html',
    styleUrls: ['./entity-external-component.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityExternalComponent implements OnInit {
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #externalSrv = inject(ExternalEntityService);

    readonly deepPath$ = getDeepPath$(this.#router, this.#route);

    readonly isSmartBooking$ = this.#entitiesSrv.getEntity$().pipe(
        filter(Boolean),
        map(entity => entity.settings.external_integration?.custom_managements
            .filter(management => management.type === 'SMART_BOOKING_INTEGRATION')
            .length > 0
        )
    );

    readonly configuration$ = this.#externalSrv.configuration.get$().pipe(filter(Boolean));

    ngOnInit(): void {
        this.#externalSrv.configuration.clear();
        this.#externalSrv.clubCodes.clear();
    }

}
