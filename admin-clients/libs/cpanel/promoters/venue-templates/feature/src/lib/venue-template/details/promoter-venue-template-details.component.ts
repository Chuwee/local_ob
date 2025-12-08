import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { RoutingState } from '@admin-clients/shared/utility/state';
import { getDeepPath } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService, VenueTemplateType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { withLatestFrom } from 'rxjs';
import { debounceTime, filter, first, map, startWith } from 'rxjs/operators';

@Component({
    selector: 'app-promoter-venue-template-details',
    templateUrl: './promoter-venue-template-details.component.html',
    styleUrls: ['./promoter-venue-template-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [GoBackComponent, NavTabsMenuComponent, FlexLayoutModule, RouterOutlet, AsyncPipe]
})
export class PromoterVenueTemplateDetailsComponent implements OnInit {
    readonly #onDestroy = inject(DestroyRef);
    readonly #routingState = inject(RoutingState);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #venueTemplatesService = inject(VenueTemplatesService);
    readonly #auth = inject(AuthenticationService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly standardTemplateTypes = [VenueTemplateType.normal, VenueTemplateType.avet];

    // important debounce to let the route change on malformed urls, like use and standard id in activity route
    readonly venueTemplate$ = this.#venueTemplatesService.venueTpl.get$().pipe(debounceTime(1));
    readonly isOperator$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]).pipe(first());

    readonly isEventOrVenueManager$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.EVN_MGR, UserRoles.REC_MGR]).pipe(first());
    readonly isV4$ = this.#entitiesSrv.getEntity$().pipe(filter(Boolean), map(entity => !!entity.settings.enable_v4_configs));

    ngOnInit(): void {
        this.#routingState.removeLastUrlsWith('/promoter-venue-templates/');
        this.#router.events.pipe()
            .pipe(
                filter(event => event instanceof NavigationEnd),
                startWith(null),
                withLatestFrom(this.#venueTemplatesService.venueTpl.get$()),
                map(([_, tpl]) => tpl),
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(tpl => {
                //TODO millorar quan elements info suporti activitats
                let targetPath = 'activity';
                if (this.standardTemplateTypes.includes(tpl.type)) {
                    targetPath = 'standard';
                    if (getDeepPath(this.#route) === 'elements-info') {
                        targetPath = 'elements-info';
                    }
                }
                if (getDeepPath(this.#route) !== targetPath) {
                    this.#router.navigate(['promoter-venue-templates', tpl.id, targetPath]);
                }
            });
    }
}
