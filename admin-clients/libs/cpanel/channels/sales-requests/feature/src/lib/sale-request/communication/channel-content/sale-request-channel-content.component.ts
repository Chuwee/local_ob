import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelType, channelWebTypes } from '@admin-clients/cpanel/channels/data-access';
import { EventCommunicationService } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { ContentLinkRequest } from '@admin-clients/cpanel/shared/data-access';
import { SaleRequest, SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import {
    SeasonTicketCommunicationService, provideSeasonTicketCommunicationService
} from '@admin-clients/cpanel-promoters-season-tickets-communication-data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import { MessageDialogService, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { BehaviorSubject, combineLatest, forkJoin, Observable, of, Subject, throwError } from 'rxjs';
import { filter, map, takeUntil, tap } from 'rxjs/operators';
import { SaleRequestAdditionalBannerComponent } from './additional-banner/sale-request-additional-banner.component';
import { SaleRequestEventChannelContentsComponent } from './event-channel-contents/sale-request-event-channel-contents.component';
import { SaleRequestPurchaseContentsComponent } from './purchase-contents/sale-request-purchase-contents.component';
import {
    SaleRequestSeasonTicketChannelContentsComponent
} from './season-ticket-channel-contents/sale-request-season-ticket-channel-contents.component';

@Component({
    selector: 'app-sale-request-channel-content',
    templateUrl: './sale-request-channel-content.component.html',
    styleUrls: ['./sale-request-channel-content.component.scss'],
    providers: [provideSeasonTicketCommunicationService()],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestChannelContentComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _language = new BehaviorSubject<string>(null);

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild(SaleRequestPurchaseContentsComponent)
    private _purchaseContentsComponent: SaleRequestPurchaseContentsComponent;

    @ViewChild(SaleRequestEventChannelContentsComponent)
    private _eventChannelContentsComponent: SaleRequestEventChannelContentsComponent;

    @ViewChild(SaleRequestSeasonTicketChannelContentsComponent)
    private _seasonTicketChannelContentsComponent: SaleRequestEventChannelContentsComponent;

    @ViewChild(SaleRequestAdditionalBannerComponent)
    private _additionalBannerComponent: SaleRequestAdditionalBannerComponent;

    saleRequest: SaleRequest;
    form: UntypedFormGroup;
    isLoadingOrSaving$: Observable<boolean>;
    languageList$: Observable<string[]>;
    selectedLanguage$ = this._language.asObservable();
    links$: Observable<ContentLinkRequest>;
    showLinks: boolean;
    showPurchaseContent: boolean;
    isSeasonTicket: boolean;

    constructor(
        private _fb: UntypedFormBuilder,
        private _salesRequestsService: SalesRequestsService,
        private _eventComService: EventCommunicationService,
        private _ephemeralMessage: EphemeralMessageService,
        private _messageDialogService: MessageDialogService,
        private _seasonTicketComService: SeasonTicketCommunicationService
    ) { }

    ngOnInit(): void {
        this.form = this._fb.group({});

        this.isLoadingOrSaving$ = booleanOrMerge([
            this._salesRequestsService.isSaleRequestLoading$(),
            this._eventComService.isEventChannelContentTextsLoading$(),
            this._eventComService.isEventChannelContentImagesLoading$(),
            this._seasonTicketComService.isSeasonTicketChannelContentTextsLoading$(),
            this._seasonTicketComService.isSeasonTicketChannelContentImagesLoading$(),
            this._salesRequestsService.isSaleRequestChannelContentLinksLoading$(),
            this._salesRequestsService.isSaleRequestPurchaseContentImagesLoading$(),
            this._salesRequestsService.isSaleRequestPurchaseContentImagesSaving$(),
            this._salesRequestsService.isSaleRequestPurchaseContentImagesRemoving$(),
            this._salesRequestsService.isSaleRequestPurchaseContentTextsLoading$(),
            this._salesRequestsService.isSaleRequestPurchaseContentTextsSaving$(),
            this._salesRequestsService.isSaleRequestAdditionalBannerTextsLoading$(),
            this._salesRequestsService.isSaleRequestAdditionalBannerTextsSaving$()
        ]);

        this._salesRequestsService
            .getSaleRequest$()
            .pipe(
                filter(saleRequest => !!saleRequest),
                takeUntil(this._onDestroy)
            )
            .subscribe(saleRequest => {
                this.saleRequest = saleRequest;
                this.isSeasonTicket =
                    saleRequest.event.event_type === EventType.seasonTicket;
                this.showLinks = channelWebTypes.includes(
                    saleRequest.channel.type
                );
                this.showPurchaseContent =
                    saleRequest.channel.type !== ChannelType.external;
                this._salesRequestsService.loadSaleRequestChannelContentLinks(
                    saleRequest.id
                );
            });

        this.links$ = combineLatest([
            this._salesRequestsService.getSaleRequestChannelContentLinks$(),
            this.selectedLanguage$
        ]).pipe(
            filter(data => data.every(item => !!item)),
            map(([links, language]) =>
                links.find(links => links.language === language)
            )
        );

        this.languageList$ = this._salesRequestsService.getSaleRequest$().pipe(
            map(saleRequest => saleRequest.languages),
            tap(languages => {
                if (!languages?.default && languages?.selected.length) {
                    languages.default = languages.selected[0];
                }
                this._language.next(languages?.default);
            }),
            map(languages => languages?.selected)
        );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._salesRequestsService.clearSaleRequestChannelContentLinks();
    }

    canChangeLanguage: () => Observable<boolean> = () =>
        this.validateIfCanChangeLanguage();

    cancel(): void {
        this._purchaseContentsComponent.cancel();
        this._additionalBannerComponent.cancel();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.form.markAsPristine();
        });
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            if (this._purchaseContentsComponent) {
                obs$.push(...this._purchaseContentsComponent.save());
            }
            if (this._additionalBannerComponent) {
                obs$.push(...this._additionalBannerComponent.save());
            }
            return forkJoin(obs$).pipe(
                tap(() => {
                    this._ephemeralMessage.showSaveSuccess();
                })
            );
        } else {
            FormControlHandler.markAllControlsAsTouched(this.form);
            scrollIntoFirstInvalidFieldOrErrorMsg(
                document,
                this._matExpansionPanelQueryList
            );
            return throwError(() => 'invalid form');
        }
    }

    changeLanguage(newLanguage: string): void {
        this._language.next(newLanguage);
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this._messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
