import { WebhookService } from '@admin-clients/cpanel/shared/feature/webhook';
import { BreadcrumbsService, GoBackComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { combineLatest, filter, map, tap } from 'rxjs';

@Component({
    selector: 'app-webhooks-details',
    imports: [RouterModule, GoBackComponent, FlexLayoutModule],
    templateUrl: './webhooks-details.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class WebhooksDetailsComponent implements OnDestroy {
    readonly #breadcrumbsSrv = inject(BreadcrumbsService);
    readonly #webhookSrv = inject(WebhookService);
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly $routeParams = toSignal(this.#route.paramMap);
    readonly $routeData = toSignal(this.#route.data);

    readonly $webhook = toSignal(
        combineLatest(([this.#webhookSrv.webhook.get$(), this.#webhookSrv.webhook.error$()]))
            .pipe(
                filter(([webhook, error]) => webhook !== null || error !== null),
                tap(([webhook, error]) => {
                    if (error) {
                        this.#router.navigate(['/webhooks']);
                        return;
                    }

                    if (this.$routeData()['breadcrumb'] && webhook?.internal_name) {
                        this.#breadcrumbsSrv.addDynamicSegment(this.$routeData()['breadcrumb'], webhook.internal_name);
                    }
                }),
                map(([webhook]) => webhook)
            ));

    constructor() {
        const id = this.$routeParams().get('webhookId');
        if (id) this.#webhookSrv.webhook.load(id);
    }

    ngOnDestroy(): void {
        this.#webhookSrv.webhook.clear();
    }
}
