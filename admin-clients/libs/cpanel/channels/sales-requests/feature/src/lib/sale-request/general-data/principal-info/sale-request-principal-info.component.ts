import { Metadata } from '@OneboxTM/utils-state';
import {
    EventCommunicationService, EventChannelContentImageType, EventChannelContentImage

} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { SaleRequest, SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import {
    SeasonTicketChannelContentImage, SeasonTicketChannelContentImageType, SeasonTicketCommunicationService,
    provideSeasonTicketCommunicationService
} from '@admin-clients/cpanel-promoters-season-tickets-communication-data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { MatExpansionPanel } from '@angular/material/expansion';
import { Observable, Subject } from 'rxjs';
import { filter, first, map } from 'rxjs/operators';

@Component({
    selector: 'app-sale-request-principal-info',
    templateUrl: './sale-request-principal-info.component.html',
    styleUrls: ['./sale-request-principal-info.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [provideSeasonTicketCommunicationService()],
    standalone: false
})
export class SaleRequestPrincipalInfoComponent
    implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();
    private _salesRequest: SaleRequest;

    isLoading$: Observable<boolean>;
    saleRequest$: Observable<SaleRequest>;
    salesRequestsSessionsMetadata$: Observable<Metadata>;
    imageUrl$: Observable<string>;

    @ViewChildren(MatExpansionPanel)
    matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    constructor(
        private _salesRequestsService: SalesRequestsService,
        private _eventCommunicationService: EventCommunicationService,
        private _seasonTicketCommunicationService: SeasonTicketCommunicationService
    ) { }

    ngOnInit(): void {
        this.saleRequest$ = this._salesRequestsService
            .getSaleRequest$()
            .pipe(first(value => value !== null));
        this.salesRequestsSessionsMetadata$ =
            this._salesRequestsService.getSaleRequestSessionsMetadata$();
        this.isLoading$ = booleanOrMerge([
            this._salesRequestsService.isSaleRequestLoading$(),
            this._eventCommunicationService.isEventChannelContentImagesLoading$(),
            this._seasonTicketCommunicationService.isSeasonTicketChannelContentImagesLoading$()
        ]);
    }

    ngAfterViewInit(): void {
        this.saleRequest$.subscribe(saleRequest => {
            this._salesRequest = saleRequest;
            if (saleRequest.event.event_type === EventType.seasonTicket) {
                this._seasonTicketCommunicationService.loadSeasonTicketChannelContentImages(
                    saleRequest.event.id,
                    saleRequest.languages?.default,
                    SeasonTicketChannelContentImageType.main
                );
            } else {
                this._eventCommunicationService.loadEventChannelContentImages(
                    saleRequest.event.id,
                    saleRequest.languages?.default,
                    EventChannelContentImageType.main
                );
            }
        });
        this.loadEventImage();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._salesRequestsService.clearSaleRequestSessions();
    }

    private loadEventImage(): void {
        let channelContentImagesObservable: Observable<SeasonTicketChannelContentImage[] | EventChannelContentImage[]>;
        if (this._salesRequest.event.event_type === EventType.seasonTicket) {
            channelContentImagesObservable = this._seasonTicketCommunicationService.getSeasonTicketChannelContentImages$();
        } else {
            channelContentImagesObservable = this._eventCommunicationService.getEventChannelContentImages$();
        }
        this.imageUrl$ = channelContentImagesObservable.pipe(
            filter(value => value !== null),
            map(contents => {
                if (contents?.length) { return contents[0].image_url; }
                return null;
            })
        );
    }
}
