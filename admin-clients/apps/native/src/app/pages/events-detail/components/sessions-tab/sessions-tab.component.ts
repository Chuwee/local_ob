import { Event } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, GetSessionsRequest, Session, SessionStatus, SessionType, eventSessionsProviders
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { ChangeDetectionStrategy, Component, Input, OnInit, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { InfiniteScrollCustomEvent, ModalController, RefresherCustomEvent } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment';
import { filter } from 'rxjs';
import { SelectedTime } from '../../../../core/components/calendar-selector/models/calendar-selector.model';
import { FiltersComponent } from '../../../../modules/filters/filters.component';
import { sessionsFilter } from './models/filter-list';

@Component({
    selector: 'events-sessions-tab',
    templateUrl: './sessions-tab.component.html',
    styleUrls: ['./sessions-tab.component.scss'],
    providers: [eventSessionsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionsTabComponent implements OnInit {
    readonly #eventSessionsSrv = inject(EventSessionsService);
    readonly #modalCtrl = inject(ModalController);
    readonly #translateService = inject(TranslateService);
    readonly #router = inject(Router);
    #filterSessions: GetSessionsRequest = {
        q: '',
        status: [
            SessionStatus.ready,
            SessionStatus.scheduled,
            SessionStatus.preview
        ],
        type: [SessionType.session],
        offset: 0,
        limit: 10
    };

    @Input() readonly event: Event;
    readonly $isLoading = toSignal(this.#eventSessionsSrv.sessionList.inProgress$());
    readonly $totalSessionEvents = signal(0);

    sessionList$ = this.#eventSessionsSrv.sessionList.get$()
        .pipe(filter(Boolean)).subscribe({
            next: response => {
                if (this.#filterSessions.offset === 0) {
                    this.foundSessions = response.data;
                } else {
                    this.foundSessions = [
                        ...this.foundSessions,
                        ...response.data
                    ];
                }
                if (this.currentEvent) {
                    this.currentEvent.target.complete();
                }
                this.$totalSessionEvents.set(response.metadata.total);
            },
            error: () => {
                this.isError = true;
            }
        });

    calendarModalIsOpen = false;
    isError = false;
    foundSessions: Session[] = [];
    filterSessionCalendar: SelectedTime = {
        timeFrom: null,
        timeTo: null
    };

    listOfFilterBubbles = [];
    ionInfinite = false;
    currentEvent: InfiniteScrollCustomEvent;

    ngOnInit(): void {
        this.loadSessions();
    }

    loadSessions = (): void => {
        this.loadListOfFilters();
        this.#eventSessionsSrv.sessionList.load(this.event.id, this.#filterSessions);
    };

    handleRefresh = (event: RefresherCustomEvent): void => {
        this.#filterSessions.offset = 0;
        setTimeout(() => {
            this.loadSessions();
            event.target.complete().then();
        }, 1000);
    };

    onIonInfinite(event: InfiniteScrollCustomEvent): void {
        this.ionInfinite = true;
        this.currentEvent = event;

        if (this.#filterSessions.offset <= this.$totalSessionEvents()) {
            this.#filterSessions.offset += this.#filterSessions.limit;
            this.loadSessions();
        } else {
            event.target.complete();
        }
    }

    goToCalendar(): void {
        this.calendarModalIsOpen = true;
    }

    closeToCalendar(): void {
        this.calendarModalIsOpen = false;
    }

    updateFiltersCalendar = (newTimes: SelectedTime): void => {
        if (newTimes.timeFrom && newTimes.timeTo) {
            this.filterSessionCalendar = newTimes;
            this.#filterSessions.initStartDate = moment(
                newTimes.timeFrom
            ).toJSON();
            this.#filterSessions.initEndDate = moment(newTimes.timeTo).toJSON();
            this.loadSessions();
        }
    };

    async goToSessionFilters(): Promise<void> {
        const filters = [...sessionsFilter];
        // Iniciando los filters de template con los que tiene el evento, los hago desde aqui porque no hay endpoint
        // para obtener los templates de un evento y ya en este punto tengo cargado el evento
        if (this.event.venue_templates) {
            filters.find(filter => filter.key === 'venueTplId').filterOptions =
                this.event.venue_templates.map(template => ({
                    label: template.name,
                    value: template.id,
                    isSelected: template.id === this.#filterSessions?.venueTplId
                }));
        }

        const modal = await this.#modalCtrl.create({
            component: FiltersComponent,
            componentProps: {
                target: 'sessions',
                listOfFilters: filters,
                appliedParams: {
                    ...this.#filterSessions
                }
            }
        });
        await modal.present();
        const { data, role } = await modal.onWillDismiss();

        if (role === 'confirm') {
            if (Object.keys(data.filters).length === 0) {
                // significa que se han limpiado los filtros
                this.#filterSessions.venueTplId = null;
                this.#filterSessions.status = [];
                this.#filterSessions.hourRanges = [];
            } else {
                this.#filterSessions = {
                    ...this.#filterSessions,
                    ...data.filters
                };
            }
            this.loadSessions();
        }
    }

    removeBubbleFilter = (key: string): void => {
        if (key === 'session_date') {
            this.#filterSessions.initStartDate = null;
            this.#filterSessions.initEndDate = null;
            this.filterSessionCalendar.timeTo = null;
            this.filterSessionCalendar.timeFrom = null;
        } else if (key.includes('status')) {
            const [, removedStatus] = key.split('status_');
            this.#filterSessions.status = this.#filterSessions.status
                .map(appliedStatus => {
                    if (appliedStatus === removedStatus) {
                        return undefined;
                    }
                    return appliedStatus;
                })
                .filter(a => a);
        } else {
            this.#filterSessions[key] = null;
        }

        this.#filterSessions.offset = 0;
        this.loadSessions();
    };

    goToSessionDetail = (session: Session): void => {
        this.#router.navigate(['session-detail'], {
            queryParams: {
                eventId: this.event.id,
                id: session.id
            }
        });
    };

    private loadListOfFilters(): void {
        this.listOfFilterBubbles = [];
        const appliedSearchFilters = this.#filterSessions;

        if (appliedSearchFilters.initStartDate && appliedSearchFilters.initEndDate) {
            const filterBubble = {
                name: 'session_date',
                label: `${moment(appliedSearchFilters.initStartDate).format(
                    'DD/MM/YYYY - HH:mm'
                )} - ${moment(appliedSearchFilters.initEndDate).format(
                    'DD/MM/YYYY - HH:mm'
                )}`
            };
            this.listOfFilterBubbles.push(filterBubble);
        }

        if (appliedSearchFilters.venueTplId) {
            const filterBubble = {
                name: 'venueTplId',
                label: this.#translateService.instant(
                    'FILTERS.BUBBLES.VENUETPLID',
                    {
                        value: this.event.venue_templates.find(
                            template =>
                                template.id === appliedSearchFilters.venueTplId
                        ).name
                    }
                ),
                value: appliedSearchFilters.venueTplId
            };

            this.listOfFilterBubbles.push(filterBubble);
        }

        if (appliedSearchFilters.status) {
            appliedSearchFilters.status.forEach(singleStatus => {
                const filterBubble = {
                    name: 'status_' + singleStatus,
                    label: this.#translateService.instant(
                        'FILTERS.BUBBLES.STATE',
                        {
                            value: this.#translateService.instant(
                                'FILTERS.SESSION_STATUS.OPTIONS.' + singleStatus
                            )
                        }
                    ),
                    value: singleStatus
                };

                this.listOfFilterBubbles.push(filterBubble);
            });
        }

        for (const key in appliedSearchFilters) {
            const omittedKeys = [
                'q',
                'limit',
                'offset',
                'initStartDate',
                'initEndDate',
                'status',
                'sort',
                'type',
                'venueTplId'
            ];

            if (
                !omittedKeys.includes(key) && Object.prototype.hasOwnProperty.call(appliedSearchFilters, key) && appliedSearchFilters[key]
            ) {
                let textOfValues: string;

                if (Array.isArray(appliedSearchFilters[key])) {
                    textOfValues = this.#translateService.instant(
                        'FILTERS.SELECTED-FILTERS',
                        { number: appliedSearchFilters[key].length }
                    );
                } else {
                    textOfValues = appliedSearchFilters[key];
                }

                const filterBubble = {
                    name: key,
                    label: this.#translateService.instant(
                        'FILTERS.BUBBLES.' + key.toUpperCase(),
                        { value: textOfValues }
                    )
                };
                this.listOfFilterBubbles.push(filterBubble);
            }
        }
    }
}
