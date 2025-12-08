import { SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import {
    SeasonTicketChannelContentImageType, SeasonTicketChannelContentText,
    SeasonTicketChannelContentTextType, SeasonTicketCommunicationService, provideSeasonTicketCommunicationService
} from '@admin-clients/cpanel-promoters-season-tickets-communication-data-access';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, first, map, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-sale-request-season-ticket-channel-contents',
    templateUrl: './sale-request-season-ticket-channel-contents.component.html',
    providers: [provideSeasonTicketCommunicationService()],
    styleUrls: ['./sale-request-season-ticket-channel-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestSeasonTicketChannelContentsComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _selectedLanguage: string;
    private _eventId: number;

    @Input() language$: Observable<string>;

    texts$: Observable<Record<SeasonTicketChannelContentTextType, SeasonTicketChannelContentText>>;
    imageUrl$: Observable<string>;
    seasonTicketChannelContentTextType = SeasonTicketChannelContentTextType;

    constructor(
        private _saleRequestsService: SalesRequestsService,
        private _seasonTicketComService: SeasonTicketCommunicationService
    ) { }

    ngOnInit(): void {
        this.language$.pipe(takeUntil(this._onDestroy)).subscribe(lang => this._selectedLanguage = lang);
        this.loadContent();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._seasonTicketComService.clearSeasonTicketChannelContentTexts();
        this._seasonTicketComService.clearSeasonTicketChannelContentImages();
    }

    private loadContent(): void {
        this._saleRequestsService.getSaleRequest$()
            .pipe(
                first(value => !!value)
            ).subscribe(saleRequest => {
                this._eventId = saleRequest.event.id;
                this._seasonTicketComService.loadSeasonTicketChannelContentTexts(this._eventId);
                this._seasonTicketComService.loadSeasonTicketChannelContentImages(this._eventId);
            });

        this.texts$ = combineLatest([
            this._seasonTicketComService.getSeasonTicketChannelContentTexts(),
            this.language$
        ]).pipe(
            filter(data => data.every(item => !!item)),
            map(([channelText, language]) => {
                const communicationText = {} as Record<SeasonTicketChannelContentTextType, SeasonTicketChannelContentText>;
                channelText?.filter(text => text.language === language)
                    .forEach(comText => {
                        communicationText[comText.type] = comText;
                    });
                return communicationText;
            })
        );

        this.imageUrl$ = combineLatest([
            this._seasonTicketComService.getSeasonTicketChannelContentImages$(),
            this.language$
        ]).pipe(
            filter(data => data.every(item => !!item)),
            map(([channelImages, language]) => {
                let communicationImageUrl = '';
                channelImages?.filter(img => img.language === language)
                    .forEach(comImg => {
                        if (comImg.type === SeasonTicketChannelContentImageType.landscape && comImg.position <= 1) {
                            communicationImageUrl = comImg.image_url;
                        }
                    });
                return communicationImageUrl;
            })
        );
    }
}

