import {
    EventChannelContentImageType,
    EventChannelContentText,
    EventChannelContentTextType,
    EventCommunicationService
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import {
    ChangeDetectionStrategy,
    Component,
    Input,
    OnDestroy,
    OnInit
} from '@angular/core';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-sale-request-event-channel-contents',
    templateUrl: './sale-request-event-channel-contents.component.html',
    styleUrls: ['./sale-request-event-channel-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestEventChannelContentsComponent
    implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _selectedLanguage: string;
    private _eventId: number;

    @Input() language$: Observable<string>;

    texts$: Observable<
        Record<EventChannelContentTextType, EventChannelContentText>
    >;

    imageUrl$: Observable<string>;
    eventChannelContentTextType = EventChannelContentTextType;

    constructor(
        private _saleRequestsService: SalesRequestsService,
        private _eventComService: EventCommunicationService
    ) { }

    ngOnInit(): void {
        this.language$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(lang => (this._selectedLanguage = lang));
        this.loadContent();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._eventComService.clearEventChannelContentImages();
        this._eventComService.clearEventChannelContentTexts();
    }

    private loadContent(): void {
        this._saleRequestsService
            .getSaleRequest$()
            .pipe(
                filter(value => !!value),
                takeUntil(this._onDestroy)
            )
            .subscribe(saleRequest => {
                this._eventId = saleRequest.event.id;
                this._eventComService.loadEventChannelContentTexts(
                    this._eventId
                );
                this._eventComService.loadEventChannelContentImages(
                    this._eventId
                );
            });

        this.texts$ = combineLatest([
            this._eventComService.getEventChannelContentTexts$(),
            this.language$
        ]).pipe(
            filter(data => data.every(item => !!item)),
            map(([channelText, language]) => {
                const communicationText = {} as Record<
                    EventChannelContentTextType,
                    EventChannelContentText
                >;
                channelText
                    ?.filter(text => text.language === language)
                    .forEach(comText => {
                        communicationText[comText.type] = comText;
                    });
                return communicationText;
            })
        );

        this.imageUrl$ = combineLatest([
            this._eventComService.getEventChannelContentImages$(),
            this.language$
        ]).pipe(
            filter(data => data.every(item => !!item)),
            map(([channelImages, language]) => {
                let communicationImageUrl = '';
                channelImages
                    ?.filter(img => img.language === language)
                    .forEach(comImg => {
                        if (
                            comImg.type ===
                            EventChannelContentImageType.landscape &&
                            comImg.position <= 1
                        ) {
                            communicationImageUrl = comImg.image_url;
                        }
                    });
                return communicationImageUrl;
            })
        );
    }
}
