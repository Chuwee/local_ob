import { AuthenticationService, User } from '@admin-clients/cpanel/core/data-access';
import {
    EventsService, GetEventsRequest, EventStatus, Event, eventsProviders
} from '@admin-clients/cpanel/promoters/events/data-access';
import { ChangeDetectionStrategy, Component, ViewChild, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { InfiniteScrollCustomEvent, ModalController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment';
import { filter, map } from 'rxjs';
import { CalendarSelectorComponent } from '../../core/components/calendar-selector/calendar-selector.component';
import { SelectedTime } from '../../core/components/calendar-selector/models/calendar-selector.model';
import { getInitials } from '../../helpers/string.utils';
import { AuthService } from '../../modules/auth/services/auth.service';
import { FiltersComponent } from '../../modules/filters/filters.component';
import { eventFilters } from './data/filter-list';

@Component({
    selector: 'events-page',
    templateUrl: 'events.page.html',
    styleUrls: ['events.page.scss'],
    providers: [eventsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventsPage {
    readonly #modalCtrl = inject(ModalController);
    readonly #authService = inject(AuthService);
    readonly #eventsSrv = inject(EventsService);
    readonly #translateService = inject(TranslateService);
    readonly #router = inject(Router);
    readonly #defaultFilters = {
        limit: 10,
        offset: 0
    };

    #labelsForEachAppliedFilter = null;
    #appliedSearchFilters: GetEventsRequest;

    @ViewChild('calendarSelector') readonly calendarSelector: CalendarSelectorComponent;

    readonly $isLoading = toSignal(this.#eventsSrv.eventsList.loading$());
    readonly $initials = toSignal(this.#authService.getLoggedUser$().pipe(filter(Boolean),
        map((user: User) => getInitials(user.name, user.last_name))));

    readonly $currencies = toSignal(this.#authService.getLoggedUser$().pipe(
        filter(Boolean),
        map(AuthenticationService.operatorCurrencies)));

    readonly $totalResultsCounter = toSignal(this.#eventsSrv.eventsList.getMetadata$().pipe(filter(Boolean),
        map(metadata => metadata.total)));

    readonly $listOfFilterBubbles = signal<{ name: string; label: string }[]>([]);

    readonly $events = toSignal(this.#eventsSrv.eventsList.getData$().pipe(filter(Boolean),
        map(response => {
            if (this.currentEvent) {
                this.currentEvent.target.complete();
            }
            if (this.#appliedSearchFilters.offset === 0) {
                return response;
            } else {
                return [...this.$events(), ...response];
            }
        })));

    readonly user$ = this.#authService.getLoggedUser$().pipe(filter(Boolean)).subscribe(() => {
        // Reset applied filters every time a user change occurs
        this.#appliedSearchFilters = {
            ...this.#defaultFilters,
            sort: 'start_date:desc',
            status: [EventStatus.planned, EventStatus.ready, EventStatus.inProgramming]
        };
        this.loadData();
    });

    calendarModalIsOpen = false;
    filterCalendar: SelectedTime = {
        timeFrom: null,
        timeTo: null
    };

    ionInfinite = false;
    inputValue = null;
    currentEvent: InfiniteScrollCustomEvent;

    handleRefresh = (event): void => {
        this.#appliedSearchFilters.offset = 0;
        this.loadData();
        setTimeout(() => {
            event.target.complete();
        }, 1000);
    };

    onIonInfinite(event: InfiniteScrollCustomEvent): void {
        this.ionInfinite = true;
        this.currentEvent = event;
        if (this.#appliedSearchFilters.offset <= this.$totalResultsCounter()) {
            this.#appliedSearchFilters.offset += 10;
            this.loadData();
        } else {
            event.target.complete();
        }
    }

    onSearch(searchInput: CustomEvent): void {
        this.#appliedSearchFilters.offset = 0;
        this.#appliedSearchFilters.q = searchInput.detail.value;
        this.loadData();
    }

    onClear(): void {
        this.inputValue = null;
        this.#appliedSearchFilters.q = null;
        this.loadData();
    }

    async goToFilters(): Promise<void> {
        const listEventFilter = [...eventFilters];
        // The currency filter only appears if it is Multicurrency with more than one currency
        if (this.$currencies() && this.$currencies().length > 1) {
            listEventFilter.unshift({
                target: 'events',
                key: 'currency',
                filterName: 'CURRENCY.CURRENCY',
                filterType: 'picker',
                filterTitle: 'CURRENCY.SELECT-CURRENCY',
                filterplaceHolder: 'CURRENCY.FIND-CURRENCY-PLACEHOLDER',
                filterOptions: [],
                value: [],
                isMultiple: false
            });
        }

        const modal = await this.#modalCtrl.create({
            component: FiltersComponent,
            componentProps: {
                target: 'events',
                listOfFilters: listEventFilter,
                appliedParams: this.#appliedSearchFilters
            }
        });
        await modal.present();
        const { data, role } = await modal.onWillDismiss();

        if (role === 'confirm') {
            this.#appliedSearchFilters = {
                ...this.#defaultFilters,
                startDate: this.#appliedSearchFilters.startDate,
                endDate: this.#appliedSearchFilters.endDate,
                ...data.filters
            };

            this.#labelsForEachAppliedFilter = data.labels;
            this.loadData();
        }
    }

    goToCalendar(): void {
        this.calendarModalIsOpen = true;
    }

    closeToCalendar(): void {
        this.calendarModalIsOpen = false;
    }

    goToProfile(): void {
        this.#router.navigate(['/profile']);
    }

    goToEventDetail(event: Event): void {
        this.#router.navigate(['event-detail', event.id]);
    }

    removeBubbleFilter = (key: string): void => {
        if (key === 'date') {
            this.#appliedSearchFilters.startDate = null;
            this.#appliedSearchFilters.endDate = null;
        } else if (key.includes('status')) {
            const [, removedStatus] = key.split('status_');
            this.#appliedSearchFilters.status =
                this.#appliedSearchFilters.status
                    .map(appliedStatus => {
                        if (appliedStatus === removedStatus) {
                            return undefined;
                        }
                        return appliedStatus;
                    }).filter(a => a);
        } else {
            this.#appliedSearchFilters[key] = null;
        }

        this.#appliedSearchFilters.offset = 0;
        this.loadData();
    };

    updateFiltersCalendar = (newTimes: SelectedTime): void => {
        if (newTimes.timeFrom && newTimes.timeTo) {
            this.filterCalendar = newTimes;
            this.#appliedSearchFilters.startDate = moment(newTimes.timeFrom).toJSON();
            this.#appliedSearchFilters.endDate = moment(newTimes.timeTo).toJSON();
            this.loadData();
        }
    };

    private loadData(): void {
        this.loadListOfFilters();
        this.#eventsSrv.eventsList.load(this.#appliedSearchFilters);
    }

    private loadListOfFilters(): void {
        this.$listOfFilterBubbles.set([]);
        const appliedSearchFilters = this.#appliedSearchFilters;
        if (appliedSearchFilters.startDate && appliedSearchFilters.endDate) {
            const filterBubble = {
                name: 'date',
                label: `${moment(appliedSearchFilters.startDate).format('DD/MM/YYYY - HH:mm')}
                - ${moment(appliedSearchFilters.endDate).format('DD/MM/YYYY - HH:mm')}`
            };
            this.$listOfFilterBubbles.set([...this.$listOfFilterBubbles(), filterBubble]);
        }
        if (appliedSearchFilters.currency) {
            const filterBubble = {
                name: 'currency',
                label: this.#translateService.instant('FILTERS.BUBBLES.CURRENCY', { value: this.#labelsForEachAppliedFilter.currency }),
                value: appliedSearchFilters.currency
            };
            this.$listOfFilterBubbles.set([...this.$listOfFilterBubbles(), filterBubble]);
        }

        if (appliedSearchFilters.producerId) {
            const filterBubble = {
                name: 'producerId',
                label: this.#translateService.instant('FILTERS.BUBBLES.PRODUCERID', { value: this.#labelsForEachAppliedFilter.producerId }),
                value: appliedSearchFilters.producerId
            };
            this.$listOfFilterBubbles.set([...this.$listOfFilterBubbles(), filterBubble]);
        }

        if (appliedSearchFilters.country) {
            const filterBubble = {
                name: 'country',
                label: this.#translateService.instant('FILTERS.BUBBLES.COUNTRY', { value: this.#labelsForEachAppliedFilter.country }),
                value: appliedSearchFilters.country
            };
            this.$listOfFilterBubbles.set([...this.$listOfFilterBubbles(), filterBubble]);
        }

        if (appliedSearchFilters.status) {
            appliedSearchFilters.status.forEach(singleStatus => {
                const filterBubble = {
                    name: 'status_' + singleStatus,
                    label: this.#translateService.instant(
                        'FILTERS.BUBBLES.STATE', { value: this.#translateService.instant('FILTERS.STATUS.OPTIONS.' + singleStatus) }
                    ),
                    value: singleStatus
                };
                this.$listOfFilterBubbles.set([...this.$listOfFilterBubbles(), filterBubble]);
            });
        }

        if (appliedSearchFilters.venueId) {
            const filterBubble = {
                name: 'venueId',
                label: this.#translateService.instant('FILTERS.BUBBLES.VENUEID', { value: this.#labelsForEachAppliedFilter.venueId }),
                value: appliedSearchFilters.venueId
            };
            this.$listOfFilterBubbles.set([...this.$listOfFilterBubbles(), filterBubble]);
        }

        if (appliedSearchFilters.city) {
            const filterBubble = {
                name: 'city',
                label: this.#translateService.instant('FILTERS.BUBBLES.CITY', { value: this.#labelsForEachAppliedFilter.city }),
                value: appliedSearchFilters.city
            };

            this.$listOfFilterBubbles.set([...this.$listOfFilterBubbles(), filterBubble]);
        }

        if (appliedSearchFilters.type) {
            const filterBubble = {
                name: 'type',
                label: this.#translateService.instant('FILTERS.BUBBLES.TYPE', { value: this.#labelsForEachAppliedFilter.type }),
                value: appliedSearchFilters.type
            };

            this.$listOfFilterBubbles.set([...this.$listOfFilterBubbles(), filterBubble]);
        }

        for (const key in appliedSearchFilters) {
            const omittedKeys = [
                'q',
                'limit',
                'offset',
                'startDate',
                'endDate',
                'status',
                'sort',
                'producerId',
                'country',
                'venueId',
                'city',
                'type',
                'currency'
            ];

            if (!omittedKeys.includes(key) && Object.prototype.hasOwnProperty.call(appliedSearchFilters, key)
                && appliedSearchFilters[key]) {
                let textOfValues: string;

                if (Array.isArray(appliedSearchFilters[key])) {
                    textOfValues = this.#translateService.instant('FILTERS.SELECTED-FILTERS', { number: appliedSearchFilters[key].length });
                } else {
                    textOfValues = appliedSearchFilters[key];
                }

                const filterBubble = {
                    name: key,
                    label: this.#translateService.instant('FILTERS.BUBBLES.' + key.toUpperCase(), { value: textOfValues })
                };

                this.$listOfFilterBubbles.set([...this.$listOfFilterBubbles(), filterBubble]);
            }
        }
    }
}
