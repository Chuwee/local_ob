import { GetWebhooksRequest } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { Webhook, WebhookScope, WebhookService } from '@admin-clients/cpanel/shared/feature/webhook';
import {
    ChipsComponent, ChipsFilterDirective, ContextNotificationComponent, DialogSize, EphemeralMessageService, FilterItem,
    ListFilteredComponent, ListFiltersService, MessageDialogService, MessageType, ObMatDialogConfig, PaginatorComponent,
    PopoverComponent, PopoverFilterDirective, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { NgClass } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, viewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSort, MatSortModule, SortDirection } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs';
import { NewWebhookComponent } from '../create/new-webhook.component';
import { WebhooksFilterComponent } from './filter/webhooks-filter.component';

@Component({
    selector: 'app-webhooks-list',
    imports: [
        TranslatePipe, MatIconModule, MatButtonModule, FlexLayoutModule, PopoverComponent,
        PaginatorComponent, MatDividerModule, MatProgressSpinnerModule, MatTableModule,
        MatSortModule, RouterModule, NgClass, WebhooksFilterComponent, PopoverFilterDirective,
        ChipsComponent, ChipsFilterDirective, ContextNotificationComponent
    ],
    providers: [ListFiltersService],
    templateUrl: './webhooks-list.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class WebhooksListComponent extends ListFilteredComponent implements AfterViewInit {
    readonly $paginator = viewChild<PaginatorComponent>(PaginatorComponent);
    readonly $sort = viewChild<MatSort>(MatSort);
    readonly $webhooksFilter = viewChild<WebhooksFilterComponent>(WebhooksFilterComponent);

    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    readonly #webhookSrv = inject(WebhookService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #dialog = inject(MatDialog);
    readonly #destroyRef = inject(DestroyRef);
    readonly $isHandsetOrTablet = toSignal(inject(BreakpointObserver)
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches),
            takeUntilDestroyed(this.#destroyRef)
        ));

    readonly $webhooks = toSignal(this.#webhookSrv.webhooks.get$());
    readonly $webhooksMetadata = toSignal(this.#webhookSrv.webhooks.getMetadata$());
    readonly $isLoading = toSignal(this.#webhookSrv.webhooks.inProgress$());

    #request: GetWebhooksRequest;
    #sortFilterComponent: SortFilterComponent;

    readonly webhooksPageSize = 20;
    readonly initSortCol = 'internal_name';
    readonly initSortDir: SortDirection = 'asc';
    readonly displayedColumns = ['internal_name', 'operator', 'entity', 'status', 'actions'];

    constructor() {
        super();
    }

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this.$sort());
        this.initListFilteredComponent([
            this.$paginator(),
            this.$webhooksFilter(),
            this.#sortFilterComponent
        ]);
    }

    loadData(filters: FilterItem[]): void {
        this.#request = new GetWebhooksRequest();
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this.#request.sort = values[0].value;
                        break;
                    case 'PAGINATION':
                        this.#request.limit = values[0].value.limit;
                        this.#request.offset = values[0].value.offset;
                        break;
                    case 'ENTITY':
                        this.#request.entityId = values[0].value;
                        break;
                    case 'OPERATOR':
                        this.#request.operatorId = values[0].value;
                        break;
                    case 'STATUS':
                        this.#request.status = values.map(val => val.value);
                        break;
                }
            }
        });
        this.#loadWebhooks();
    }

    openNewWebhookDialog(): void {
        const dialog = this.#dialog.open(NewWebhookComponent, new ObMatDialogConfig());
        dialog.afterClosed().subscribe(webhookId => {
            if (webhookId) {
                this.#ephemeralMsgSrv.show({
                    type: MessageType.success,
                    msgKey: 'WEBHOOKS.NEW.SUCCESS'
                });
                this.#router.navigate([webhookId, 'general-data'], { relativeTo: this.#route });
            }
        });
    }

    openDeleteWebhookDialog(webhook: Webhook): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'WEBHOOKS.DELETE.TITLE',
            message: 'WEBHOOKS.DELETE.MESSAGE',
            messageParams: { eventName: webhook.internal_name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe((success: boolean) => {
                if (success) {
                    this.#webhookSrv.webhook.delete$(webhook.id).subscribe(() => {
                        this.#ephemeralMsgSrv.show({
                            type: MessageType.success,
                            msgKey: 'WEBHOOKS.DELETE.SUCCESS'
                        });
                        this.#loadWebhooks();
                    });
                }
            });
    }

    #loadWebhooks(): void {
        this.#webhookSrv.webhooks.load({
            scope: [WebhookScope.sysAdmin, WebhookScope.operator],
            operator_id: this.#request?.operatorId,
            entity_id: this.#request?.entityId,
            status: this.#request?.status,
            limit: this.#request?.limit || this.webhooksPageSize,
            offset: this.#request?.offset || 0,
            sort: this.#request?.sort || `${this.initSortCol}:${this.initSortDir}`
        } as GetWebhooksRequest);
    }
}
