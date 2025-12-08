import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import {
    EventChannelsApi, EventChannelsService, EventChannelsState
} from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { ContentLinkStatus, ContentLinkType } from '@admin-clients/cpanel/shared/data-access';
import { LinkListComponent } from '@admin-clients/cpanel/shared/ui/components';
import { LanguageBarComponent, EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatIconModule } from '@angular/material/icon';
import { PageEvent } from '@angular/material/paginator';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, filter, map, shareReplay, tap } from 'rxjs';

@Component({
    selector: 'app-product-channel-preview',
    imports: [
        FormContainerComponent, LanguageBarComponent, AsyncPipe, LinkListComponent, EmptyStateComponent, TranslatePipe,
        MatProgressSpinner, MatIconModule
    ],
    providers: [EventChannelsState, EventChannelsService, EventChannelsApi],
    templateUrl: './product-channel-preview.component.html',
    styleUrls: ['./product-channel-preview.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductChannelPreviewComponent implements OnInit {
    protected readonly eventChannelSessionsContentLinkType = ContentLinkType;

    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #productsSrv = inject(ProductsService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #language = new BehaviorSubject<string>(null);

    readonly pageSize = 10;

    readonly publishedSessionLinks$ = this.#productsSrv.productChannelPublishedSessionLinks.getData$();
    readonly publishedSessionLinksMetadata$ = this.#productsSrv.productChannelPublishedSessionLinks.getMetadata$();
    readonly publishedSessionLinksLoading$ = this.#productsSrv.productChannelPublishedSessionLinks.inProgress$();
    readonly unpublishedSessionLinks$ = this.#productsSrv.productChannelUnpublishedSessionLinks.getData$();
    readonly unpublishedSessionLinksMetadata$ = this.#productsSrv.productChannelUnpublishedSessionLinks.getMetadata$();
    readonly unpublishedSessionLinksLoading$ = this.#productsSrv.productChannelUnpublishedSessionLinks.inProgress$();

    readonly $channelId = toSignal(this.#productsSrv.product.channel.get$()
        .pipe(filter(Boolean), map(productChannel => productChannel.channel.id)));

    readonly $languageList = toSignal(this.#productsSrv.product.channel.get$().pipe(
        filter(Boolean),
        map(channel => channel.languages?.selected || []),
        tap(languages => {
            this.#language.next(languages[0]);
        }),
        shareReplay(1)
    ));

    readonly language$ = this.#language.asObservable();

    isLoading$ = booleanOrMerge([
        this.#productsSrv.product.inProgress$(),
        this.#productsSrv.productChannelPublishedSessionLinks.inProgress$(),
        this.#productsSrv.productChannelUnpublishedSessionLinks.inProgress$(),
        this.#productsSrv.product.channel.inProgress$()
    ]);

    ngOnInit(): void {
        combineLatest([
            this.#productsSrv.product.get$(),
            this.#productsSrv.product.channel.get$(),
            this.language$
        ]).pipe(
            takeUntilDestroyed(this.#destroyRef),
            filter(resp => resp.every(Boolean)),
            tap(([product, productChannel, language]) => {
                if (product.product_id && productChannel.channel.id && language) {
                    this.#productsSrv.productChannelPublishedSessionLinks.clear();
                    this.#productsSrv.productChannelUnpublishedSessionLinks.clear();
                    this.loadSessionsData({ pageIndex: 0 }, this.eventChannelSessionsContentLinkType.published);
                    this.loadSessionsData({ pageIndex: 0 }, this.eventChannelSessionsContentLinkType.unpublished);
                }
            })).subscribe();
    }

    changeLanguage(newLanguage: string): void {
        this.#language.next(newLanguage);
        this.loadSessionsData({ pageIndex: 0 }, ContentLinkType.published);
        this.loadSessionsData({ pageIndex: 0 }, ContentLinkType.unpublished);
    }

    get productIdPath(): number | undefined {
        const allRouteParams = Object.assign({}, ...this.#activatedRoute.snapshot.pathFromRoot.map(path => path.params));
        return parseInt(allRouteParams.productId);
    }

    loadSessionsData(pageOptions: Partial<PageEvent>, type: ContentLinkType): void {
        const request = {
            productId: this.productIdPath,
            channelId: this.$channelId(),
            language: this.#language.value,
            limit: this.pageSize,
            sort: 'start_date:asc',
            offset: this.pageSize * pageOptions.pageIndex,
            session_status: [ContentLinkStatus.preview]
        };
        if (type === ContentLinkType.published) {
            request.session_status = [ContentLinkStatus.ready, ContentLinkStatus.scheduled];
            this.#productsSrv.productChannelPublishedSessionLinks.load(request);
        } else {
            this.#productsSrv.productChannelUnpublishedSessionLinks.load(request);
        }
    }

}
