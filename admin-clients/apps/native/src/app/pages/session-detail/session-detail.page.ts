import { eventSessionsProviders, EventSessionsService, PutSession, Session, SessionStatus } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { GetTicketsRequest, TicketDetail, ticketsBaseProviders, TicketsBaseService } from '@admin-clients/shared/common/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { PriceTypeAvailability } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnDestroy, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertController, InfiniteScrollCustomEvent, ToastController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { combineLatest, filter, Subject, takeUntil } from 'rxjs';
import { PickerDataItem } from '../../core/components/picker/models/pickerData';
import { PriceAvailabilityModel } from './models/price-availability.model';

@Component({
    selector: 'session-detail-page',
    templateUrl: './session-detail.page.html',
    styleUrls: ['./session-detail.page.scss'],
    providers: [eventSessionsProviders, ticketsBaseProviders],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionDetailPage implements OnDestroy {
    readonly #translate = inject(TranslateService);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #eventSessionService = inject(EventSessionsService);
    readonly #ticketsBaseService = inject(TicketsBaseService);
    readonly #changeDetectorRef = inject(ChangeDetectorRef);
    readonly #alertController = inject(AlertController);
    readonly #toastController = inject(ToastController);
    readonly #router = inject(Router);

    readonly #defaultFilters = {
        limit: 10,
        offset: 0
    };

    readonly #onDestroy = new Subject<void>();

    #id: number;
    #eventId: number;
    #priceSelect: string;
    #priceMapOptions = new Map<string, PriceAvailabilityModel>();

    readonly $totalResultsCounter = signal(0);
    readonly $isError = toSignal(this.#eventSessionService.session.error$());
    readonly $isLoading = toSignal(booleanOrMerge([
        this.#eventSessionService.session.loading$(),
        this.#eventSessionService.isSessionPriceTypesLoading$()
    ]));

    readonly params$ = this.#activatedRoute.queryParams.pipe(
        takeUntil(this.#onDestroy)
    ).subscribe(params => {
        this.#id = params['id'];
        this.#eventId = params['eventId'];

        this.#eventSessionService.session.load(this.#eventId, this.#id);
        this.#eventSessionService.loadPriceTypeAvailability(this.#eventId, this.#id);
    });

    readonly loadData$ = combineLatest([
        this.#eventSessionService.session.get$(),
        this.#eventSessionService.getPriceTypeAvailability$()
    ]).pipe(
        filter(([session, priceTypeAvailability]) => !!session && !!priceTypeAvailability),
        takeUntil(this.#onDestroy)
    ).subscribe(([session, priceTypeAvailability]) => {
        this.session = session;

        this.pricesOptions = priceTypeAvailability.map((priceType: PriceTypeAvailability, key) => {
            // TODO: Revisar porque llegan propiedas undefined cuando no esta reflejado en el modelo
            // Saber como puedo asociar la zona de precio con este objeto ya que no veo un identificados para asociar
            this.#priceMapOptions.set(key.toString(), priceType.availability);
            return {
                label: key.toString(),
                value: key.toString()
            };
        });
        // Añadimos la opción total
        this.pricesOptions.unshift({
            label: this.#translate.instant(`SESSION_DETAIL.CHECK_OCCUPATION.OPTION_ALL`),
            value: 'total'
        });
        this.#priceSelect = 'total';
        this.#changeDetectorRef.detectChanges();
    });

    readonly ticketList$ = this.#ticketsBaseService.ticketList.getData$().pipe(
        filter(Boolean),
        takeUntil(this.#onDestroy)
    ).subscribe(data => {
        if (this.filters.appliedSearchFilters.offset === 0) {
            this.filters.searchedResults = data;
        } else {
            this.filters.searchedResults = [
                ...this.filters.searchedResults,
                ...data
            ];
        }
        if (this.currentEvent) {
            this.currentEvent.target.complete();
        }
        this.#changeDetectorRef.detectChanges();
    });

    readonly ticketMetaData = this.#ticketsBaseService.ticketList.getMetaData$().pipe(filter(Boolean)).subscribe(
        metadata => {
            this.$totalResultsCounter.set(metadata.total);
            this.filters.appliedSearchFilters.offset = metadata.offset;
        });

    readonly dateTimeFormats = DateTimeFormats;
    readonly statusOptions: PickerDataItem[] = Object.keys(SessionStatus).map(key => ({
        label: this.#translate.instant(`SESSION_DETAIL.STATUS_BLOCK.STATUS_SESSION.${SessionStatus[key].toUpperCase()}`),
        value: SessionStatus[key as keyof typeof SessionStatus]
    }));

    readonly filters = {
        isSearching: false,
        searchedResults: [],
        appliedSearchFilters: {
            ...this.#defaultFilters
        } as GetTicketsRequest
    };

    session: Session;
    pricesOptions: PickerDataItem[] = [];
    inputValue: string;
    currentEvent: InfiniteScrollCustomEvent;

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    async changeStatusSession(key: 'sale' | 'release' | 'booking'): Promise<void> {
        this.session.settings[key].enable = !this.session.settings[key].enable;

        const title = this.#translate.instant(`SESSION_DETAIL.DATES.ALERT.TITLE`);
        const text = this.#translate.instant(`SESSION_DETAIL.DATES.ALERT.TEXT`);
        const cancelText = this.#translate.instant(`SESSION_DETAIL.DATES.ALERT.CANCEL`);
        const saveText = this.#translate.instant(`SESSION_DETAIL.DATES.ALERT.SAVE`);
        const alert = await this.#alertController.create({
            header: title,
            message: text,
            cssClass: 'ob-alert alert-warning',
            // eslint-disable-next-line @typescript-eslint/naming-convention
            htmlAttributes: { 'data-override-styles': '' },
            buttons: [
                {
                    text: cancelText,
                    role: 'cancel',
                    cssClass: 'ob-btn ghost size--small',
                    // eslint-disable-next-line @typescript-eslint/naming-convention
                    htmlAttributes: { 'data-override-styles': '' },
                    handler: () => {
                        this.session.settings[key].enable = !this.session.settings[key].enable;
                        this.#changeDetectorRef.detectChanges();
                    }
                },
                {
                    text: saveText,
                    role: 'confirm',
                    cssClass: 'ob-btn primary size--small',
                    // eslint-disable-next-line @typescript-eslint/naming-convention
                    htmlAttributes: { 'data-override-styles': '' },
                    handler: () => {
                        this.updateSessionStatus({
                            settings: {
                                [key]: { enable: this.session.settings[key].enable }
                            }
                        });
                    }
                }
            ]
        });

        await alert.present();
    }

    reTry(): void {
        this.#eventSessionService.session.load(this.#eventId, this.#id);
        this.#eventSessionService.loadPriceTypeAvailability(this.#eventId, this.#id);
    }

    toggleActivatedClass(ev: Event): void {
        const element = ev.currentTarget as HTMLElement;
        const allAccordionTitles = document.querySelectorAll(
            '.session-detail-page__accordion-title'
        );

        allAccordionTitles.forEach(accordion => {
            if (accordion.id === element.id) {
                element.classList.toggle('activated');
            } else {
                accordion.classList.remove('activated');
            }
        });
    }

    async changeStatus(status: SessionStatus): Promise<void> {
        const previousStatus: SessionStatus = this.session.status;
        this.session.status = status;
        if (status !== previousStatus) {
            const title = this.#translate.instant(`SESSION_DETAIL.STATUS_BLOCK.ALERT.TITLE`);
            const text = this.#translate.instant(`SESSION_DETAIL.STATUS_BLOCK.ALERT.TEXT`);
            const cancelText = this.#translate.instant(`SESSION_DETAIL.STATUS_BLOCK.ALERT.CANCEL`);
            const saveText = this.#translate.instant(`SESSION_DETAIL.STATUS_BLOCK.ALERT.SAVE`);
            const alert = await this.#alertController.create({
                header: title,
                message: text,
                cssClass: 'ob-alert alert-warning',
                // eslint-disable-next-line @typescript-eslint/naming-convention
                htmlAttributes: { 'data-override-styles': '' },
                buttons: [
                    {
                        text: cancelText,
                        role: 'cancel',
                        cssClass: 'ob-btn ghost size--small',
                        // eslint-disable-next-line @typescript-eslint/naming-convention
                        htmlAttributes: { 'data-override-styles': '' },
                        handler: () => {
                            this.session.status = previousStatus;
                            this.#changeDetectorRef.detectChanges();
                        }
                    },
                    {
                        text: saveText,
                        role: 'confirm',
                        cssClass: 'ob-btn primary size--small',
                        // eslint-disable-next-line @typescript-eslint/naming-convention
                        htmlAttributes: { 'data-override-styles': '' },
                        handler: () => {
                            this.updateSessionStatus({ status: this.session.status });
                        }
                    }
                ]
            });

            await alert.present();
        }
    }

    updateSessionStatus(update: PutSession): void {
        //TODO: Control when this has an error
        this.#eventSessionService.updateSession(this.#eventId, this.#id, update).subscribe(() => {
            this.showToast('success').then();
        });
    }

    onEnter(): void {
        this.filters.isSearching = true;
        this.filters.appliedSearchFilters.offset = 0;
        this.loadEntries(this.inputValue);
    }

    onClear(): void {
        this.filters.searchedResults = [];
    }

    onIonInfinite(event: InfiniteScrollCustomEvent): void {
        this.currentEvent = event;
        if (this.filters.appliedSearchFilters.offset <= this.$totalResultsCounter()) {
            this.filters.appliedSearchFilters.offset += this.filters.appliedSearchFilters.limit;
            this.loadEntries();
        } else {
            event.target.complete();
        }

    }

    gotToDetailTicket(ticket: TicketDetail): void {
        this.#router.navigate(['ticket-detail'], {
            queryParams: {
                order_code: ticket.order.code,
                id: ticket.id
            }
        });
    }

    get getPriceAvailable(): PriceAvailabilityModel | null {
        return this.#priceMapOptions.get(this.#priceSelect) ?? null;
    }

    selectPriceType(priceType: string): void {
        this.#priceSelect = priceType;
    }

    private loadEntries(searchValue?: string): void {
        this.filters.appliedSearchFilters.session_id = [this.#id];
        this.filters.appliedSearchFilters.event_id = [this.#eventId];
        this.filters.appliedSearchFilters.q = searchValue;

        this.#ticketsBaseService.ticketList.load(this.filters.appliedSearchFilters);
    }

    private async showToast(type: 'error' | 'success'): Promise<void> {
        const toast = await this.#toastController.create({
            message: this.#translate.instant(
                `SESSION_DETAIL.TOAST.${type.toUpperCase()}`
            ),
            duration: 2500,
            position: 'top',
            icon: `./assets/media/icons/${type}_circle.svg`,
            cssClass: `ob-toast ob-toast--${type}`
        });

        await toast.present();
    }
}
