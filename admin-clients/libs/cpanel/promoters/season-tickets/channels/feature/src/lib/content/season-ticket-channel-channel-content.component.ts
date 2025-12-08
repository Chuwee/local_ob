import { channelWebTypes } from '@admin-clients/cpanel/channels/data-access';
import {
    SeasonTicketChannelSessionLink, SeasonTicketChannelsService, SeasonTicketChannel
} from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import {
    CopyTextComponent,
    EmptyStateComponent,
    LanguageBarComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, takeUntil, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent,
        LanguageBarComponent,
        FlexLayoutModule,
        CopyTextComponent,
        TranslatePipe,
        MaterialModule,
        EmptyStateComponent,
        AsyncPipe
    ],
    selector: 'app-season-ticket-channel-channel-content',
    templateUrl: './season-ticket-channel-channel-content.component.html',
    styleUrls: ['./season-ticket-channel-channel-content.component.scss']
})
export class SeasonTicketChannelChannelContentComponent implements OnInit, OnDestroy {
    private _language = new BehaviorSubject<string>(null);
    private _onDestroy = new Subject<void>();

    seasonTicketChannel: SeasonTicketChannel;
    isLoading$: Observable<boolean>;
    ticketLoading$: Observable<boolean>;
    requestAccepted$: Observable<boolean>;
    languageList$: Observable<string[]>;
    language$ = this._language.asObservable();
    seasonTicketChannelSessionLink: SeasonTicketChannelSessionLink;
    shouldShowLinks: boolean;
    shouldEnableLink: boolean;

    constructor(
        private _seasonTicketChannelsService: SeasonTicketChannelsService
    ) { }

    ngOnInit(): void {
        this.seasonTicketChannelSessionLink = {} as SeasonTicketChannelSessionLink;

        this.isLoading$ = booleanOrMerge([
            this._seasonTicketChannelsService.isSeasonTicketChannelInProgress$(),
            this._seasonTicketChannelsService.isSeasonTicketChannelLinkLoading$()
        ]);
        this.ticketLoading$ = this._seasonTicketChannelsService.isTicketPdfPreviewDownloading$();
        this.requestAccepted$ = this._seasonTicketChannelsService.isSeasonTicketChannelRequestAccepted$();

        this._seasonTicketChannelsService.getSeasonTicketChannel$()
            .pipe(
                withLatestFrom(this._seasonTicketChannelsService.getSeasonTicketChannel$()),
                filter(data => data.every(item => !!item)),
                takeUntil(this._onDestroy))
            .subscribe(([seasonTicketChannel]) => {
                this.seasonTicketChannel = seasonTicketChannel;
                this.shouldShowLinks = channelWebTypes.includes(seasonTicketChannel.channel.type);
                this._seasonTicketChannelsService.clearSeasonTicketChannelLink();
                if (this.shouldShowLinks) {
                    const seasonTicketId = seasonTicketChannel.season_ticket.id;
                    const channelId = seasonTicketChannel.channel.id;
                    this._seasonTicketChannelsService.loadSeasonTicketChannelLink(seasonTicketId, channelId);

                }
            });
        this.languageList$ = this._seasonTicketChannelsService.getSeasonTicketChannelLanguages$()
            .pipe(
                tap(languages => {
                    if (!languages?.default && languages?.selected.length) {
                        languages.default = languages.selected[0];
                    }
                    this._language.next(languages.default);
                }),
                map(languages => languages.selected)
            );

        this.refreshContent();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._seasonTicketChannelsService.clearSeasonTicketChannelLink();
    }

    changeLanguage(newLanguage: string): void {
        this._language.next(newLanguage);
    }

    openTicketPreview(): void {
        this._seasonTicketChannelsService.downloadTicketPdfPreview$(
            this.seasonTicketChannel.season_ticket.id, this.seasonTicketChannel.channel.id, this._language.getValue()
        ).subscribe(res => window.open(res?.url, '_blank'));
    }

    private refreshContent(): void {
        combineLatest([
            this.language$,
            this._seasonTicketChannelsService.getSeasonTicketChannelLink$()
        ]).pipe(
            takeUntil(this._onDestroy),
            filter(data => data.every(item => !!item))
        ).subscribe(([language, seasonTicketChannelLink]) => {
            this.seasonTicketChannelSessionLink = {} as SeasonTicketChannelSessionLink;
            this.shouldEnableLink = seasonTicketChannelLink?.enabled;
            seasonTicketChannelLink?.links?.forEach(comLink => {
                if (comLink.language === language) {
                    this.seasonTicketChannelSessionLink = comLink;
                }
            });
        });
    }
}
