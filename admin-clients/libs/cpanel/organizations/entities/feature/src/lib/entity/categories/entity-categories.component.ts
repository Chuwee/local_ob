import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Entity } from '@admin-clients/shared/common/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { ActivatedRoute, Router, RouterModule, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';

@Component({
    selector: 'app-entity-categories',
    imports: [
        RouterModule, TranslatePipe, AsyncPipe, MatButtonToggleModule, RouterOutlet,
        LastPathGuardListenerDirective
    ],
    templateUrl: './entity-categories.component.html',
    styleUrls: ['./entity-categories.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityCategoriesComponent implements OnInit {
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #entitiesSrv = inject(EntitiesService);

    deepPath$ = getDeepPath$(this.#router, this.#route);
    entity$: Observable<Entity>;

    ngOnInit(): void {
        this.model();
    }

    private model(): void {
        this.entity$ = this.#entitiesSrv.getEntity$().pipe(filter(entity => !!entity));
    }
}
