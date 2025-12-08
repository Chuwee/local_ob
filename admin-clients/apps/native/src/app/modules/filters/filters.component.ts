import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { GetEventsRequest, EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { SessionStatus } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import { VenuesFilterFields, VenuesService, venuesProviders } from '@admin-clients/cpanel/venues/data-access';
import { GetOrdersRequest, OrdersService } from '@admin-clients/cpanel-sales-data-access';
import {
    CountriesService, EventType, GetTicketsRequest, OrderType, ticketsBaseProviders, TicketsBaseService, TicketState, TicketType
} from '@admin-clients/shared/common/data-access';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { CheckboxCustomEvent, ModalController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment';
import { Subject, filter, map, takeUntil } from 'rxjs';
import { SelectedTime } from '../../core/components/calendar-selector/models/calendar-selector.model';
import { Filter, FilterOption } from './models/filters.model';

@Component({
    selector: 'filters',
    templateUrl: './filters.component.html',
    styleUrls: ['./filters.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ticketsBaseProviders, venuesProviders],
    standalone: false
})
export class FiltersComponent implements OnInit, OnDestroy {
    readonly #onDestroy = new Subject<void>();
    readonly #modalCtrl = inject(ModalController);
    readonly #translate = inject(TranslateService);
    readonly #producerSrv = inject(ProducersService);
    readonly #venuesSrv = inject(VenuesService);
    readonly #ticketsBaseSrv = inject(TicketsBaseService);
    readonly #changeDetector = inject(ChangeDetectorRef);
    readonly #ordersSrv = inject(OrdersService);
    readonly #countriesSrv = inject(CountriesService);
    readonly #auth = inject(AuthenticationService);
    readonly #i18nSrv = inject(I18nService);
    @Input() readonly appliedParams: GetTicketsRequest & GetOrdersRequest & GetEventsRequest;
    @Input() readonly target: string;
    @Input() set listOfFilters(listOfFilters: Filter[]) {
        this.$listOfFilters.set([...listOfFilters]);
    }

    $listOfFilters = signal<Filter[]>([]);
    readonly ranges: SelectedTime = { timeFrom: moment().startOf('day').valueOf(), timeTo: moment().endOf('day').valueOf() };
    modalKey: string;
    modalType: string;
    modalTitle: string;
    modalPlaceHolder: string;
    modalIsOpen = false;
    filterOptions: FilterOption[];
    filterValuesAlreadySelected: (string | number | boolean)[] = [];
    filterIsEmpty: boolean;

    readonly $producersListData = toSignal(this.#producerSrv.getProducersListData$().pipe(filter(Boolean),
        map(response => {
            const copyListFilters = [...this.$listOfFilters()];
            copyListFilters.find(
                filter => filter.key === 'producerId'
            ).filterOptions = response.map(producer => {
                const selectedProducerIds = this.appliedParams.producerId;
                return {
                    label: producer.name,
                    value: producer.id,
                    isSelected: !!selectedProducerIds && (selectedProducerIds === producer.id)
                };
            });
            this.$listOfFilters.set([...copyListFilters]);
        })
    ));

    readonly $countriesListData = toSignal(this.#countriesSrv.getCountries$().pipe(filter(Boolean),
        map(response => {
            const copyListFilters = [...this.$listOfFilters()];
            copyListFilters.find(
                filter => filter.key === 'country'
            ).filterOptions = response.map(country => {
                const selectedCountry = this.appliedParams.country;
                return {
                    label: country.name,
                    value: country.code,
                    isSelected: !!selectedCountry && (selectedCountry === country.code)
                };
            });
            this.$listOfFilters.set([...copyListFilters]);
        })
    ));

    readonly $venuesListData = toSignal(this.#venuesSrv.venuesList.getData$().pipe(filter(Boolean),
        map(response => {
            const selectedVenueId = this.appliedParams.venueId;
            const copyListFilters = [...this.$listOfFilters()];
            copyListFilters.find(
                filter => filter.key === 'venueId'
            ).filterOptions = response.map(city => ({
                label: city.name,
                value: city.id,
                isSelected: !!selectedVenueId && selectedVenueId === city.id
            }));
            this.$listOfFilters.set([...copyListFilters]);
        }))
    );

    readonly $venueCitiesListData = toSignal(this.#venuesSrv.getVenueCitiesListData$().pipe(filter(Boolean),
        map(response => {
            const copyListFilters = [...this.$listOfFilters()];
            copyListFilters.find(
                filter => filter.key === 'city'
            ).filterOptions = response.map(city => {
                const selectedCity = this.appliedParams.city;
                return {
                    label: city.name,
                    value: city.name,
                    isSelected: !!selectedCity && selectedCity === city.name
                };
            });
            this.$listOfFilters.set([...copyListFilters]);
        })
    ));

    readonly $currenciesListData = toSignal(
        this.#auth.getLoggedUser$().pipe(filter(Boolean), map(AuthenticationService.operatorCurrencies)));

    readonly $selectedFilters = effect(() => {
        if (this.$listOfFilters().find(filter => filter.key === 'currency')) {
            if (this.$currenciesListData() && this.$currenciesListData().length > 1) {
                this.$listOfFilters().find(
                    filter => filter.key === 'currency'
                ).filterOptions = this.$currenciesListData().map(currency => {
                    const selectedCurrency = this.appliedParams.currency ?? this.appliedParams.currency_code;
                    return {
                        label: this.#i18nSrv.getCurrencyPartialTranslation(currency.code),
                        value: currency.code,
                        isSelected: !!selectedCurrency && selectedCurrency === currency.code
                    };
                });
            }
        }
    });

    ngOnInit(): void {
        this.loadFilterOptions();
        this.initFilters();
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    openModal(): void {
        this.modalIsOpen = true;
    }

    closeModal(): void {
        this.modalIsOpen = false;
    }

    openFilter(filter: Filter): void {
        this.modalType = filter.filterType;
        this.modalTitle = filter.filterTitle;
        this.modalPlaceHolder = filter.filterplaceHolder;
        this.filterOptions = filter.filterOptions;
        this.modalKey = filter.key;

        if (filter.filterType !== 'hour_range') {
            const filterOptionsAlreadySelected = filter.filterOptions
                .filter(filterOption => filterOption.isSelected)
                .map(filterOption => filterOption.value);
            if (filterOptionsAlreadySelected.length) {
                this.filterValuesAlreadySelected = filterOptionsAlreadySelected;
            }
        }

        this.openModal();
    }

    onSave(selectedOptions: (string | number)[], key: string): void {
        const filter = this.$listOfFilters().find(filter => filter.key === key);
        filter.filterOptions = filter.filterOptions.map(filterOption => ({
            ...filterOption,
            isSelected: selectedOptions.includes(filterOption.value as string | number)
        }));
        this.checkFilterSelected();
        this.closeModal();
    }

    onClickFilterButton(selectedButtonValue: string | number | boolean, key: string): void {
        const filter = this.$listOfFilters().find(filter => filter.key === key);
        filter.filterOptions = filter.filterOptions.map(filterOption => {
            if (filterOption.value === selectedButtonValue) {
                return {
                    ...filterOption,
                    isSelected: !filterOption.isSelected
                };
            }
            return filterOption;
        });
    }

    onSaveCheckbox(event: CheckboxCustomEvent, key: string): void {
        const filter = this.$listOfFilters().find(filter => filter.key === key);
        filter.filterOptions = [
            { value: event.detail.checked, isSelected: event.detail.checked }
        ];
    }

    removeFilters(): void {
        this.$listOfFilters().forEach(filter => {
            filter.filterOptions.forEach(option => {
                option.isSelected = false;
            });
        });
        this.checkFilterSelected();
    }

    checkFilterSelected(): void {
        this.filterIsEmpty = true;
        this.$listOfFilters().forEach(filter => {
            filter.filterOptions.forEach(option => {
                if (option.isSelected) {
                    this.filterIsEmpty = false;
                }
            });
        });
    }

    removeFilterItem(selectedOption: string | number | boolean, key: string): void {
        const filter = this.$listOfFilters().find(filter => filter.key === key);
        filter.filterOptions = filter.filterOptions.filter(filterOption => filterOption.value !== selectedOption);
        this.checkFilterSelected();
    }

    loadTextToDisplay(filter: Filter): string {
        if (!filter.filterOptions.length) {
            return '';
        }

        const numberOfSelections = filter.filterOptions.filter(option => option.isSelected).length;

        if (numberOfSelections === 0) {
            return this.#translate.instant('FILTERS.ALL-FILTERS');
        }

        return numberOfSelections === 1 ? filter.filterOptions.find(option => option.isSelected).label
            : this.#translate.instant('FILTERS.SELECTED-FILTERS', { number: numberOfSelections });
    }

    filterDisabled(filter: Filter): boolean {
        if (!filter.filterOptions.length) {
            return true;
        }
        const numberOfSelections = filter.filterOptions.filter(option => option.isSelected).length;

        if (numberOfSelections === 0) {
            return true;
        }
        return false;
    }

    applyFilters(): void {
        const appliedFiltersAsRequestParams = {};
        const labelsForEachAppliedFilter = {};
        this.$listOfFilters()
            .filter(filter => filter.target === this.target)
            .forEach(filter => {
                const selectedOptions = filter.filterOptions.filter(option => option.isSelected)
                    .map(selectedOption => selectedOption.value);

                const labelOption = filter.filterOptions.filter(option => option.isSelected)
                    .map(selectedOption => selectedOption.label);

                if (!selectedOptions.length) {
                    return;
                }

                appliedFiltersAsRequestParams[filter.key] = filter.isMultiple ? selectedOptions : selectedOptions[0];
                labelsForEachAppliedFilter[filter.key] = !filter.isMultiple ? labelOption : undefined;
            });

        this.#modalCtrl.dismiss({
            filters: appliedFiltersAsRequestParams,
            labels: labelsForEachAppliedFilter
        }, 'confirm').then();
    }

    addTimeToRangeHour(time: SelectedTime): void {
        this.$listOfFilters().find(filter => filter.key === this.modalKey && filter.filterType === 'hour_range').filterOptions.push({
            value: `${moment(time.timeFrom).format('HH:mmZ')}::${moment(time.timeTo).format('HH:mmZ')}`,
            label: `${moment(time.timeFrom).format('HH:mm')} - ${moment(time.timeTo).format('HH:mm')}`,
            isSelected: true
        });
        this.closeModal();
    }

    private loadFilterOptions(): void {
        if (this.target === 'events') {
            // Producer endpoints
            this.#producerSrv.loadProducersList(999, 0, 'name:asc', '', null, null, null);

            //Countries endpoints
            this.#countriesSrv.loadCountries();

            // Venue endpoints
            this.#venuesSrv.venuesList.load({ limit: 999, fields: [VenuesFilterFields.name] });

            // City endpoints
            this.#venuesSrv.loadVenueCitiesList({ limit: 999, sort: 'name:asc' });
        }

        if (this.target === 'tickets' || this.target === 'transactions') {
            this.#ticketsBaseSrv.ticketList.load({});
        }

        if (this.target === 'transactions') {
            this.#ordersSrv.loadOrdersList({});
        }
    }

    private initFilters(): void {
        if (this.target === 'events') {
            // Event type endpoints
            this.$listOfFilters().find(
                filter => filter.key === 'type'
            ).filterOptions = Object.keys(EventType).map(key => {
                if (key === 'seasonTicket') {
                    return undefined;
                }
                const selectedType = this.appliedParams.type;
                return {
                    label: this.#translate.instant(`FILTERS.EVENT_TYPE.OPTIONS.${key.toUpperCase()}`),
                    value: EventType[key as keyof typeof EventType],
                    isSelected: !!selectedType && selectedType === EventType[key]
                };
            }).filter(a => a);

            // Status endpoints
            this.$listOfFilters().find(
                filter => filter.key === 'status'
            ).filterOptions = Object.keys(EventStatus).map(key => {
                const selectedStatus = this.appliedParams.status;
                return {
                    label: this.#translate.instant(`FILTERS.STATUS.OPTIONS.${EventStatus[key].toUpperCase()}`),
                    value: EventStatus[key as keyof typeof EventStatus],
                    isSelected: selectedStatus?.includes(EventStatus[key])
                };
            });

            const isIncludingArhived = this.appliedParams.includeArchived;

            this.$listOfFilters().find(
                filter => filter.key === 'includeArchived'
            ).filterOptions = [
                    {
                        value: !!isIncludingArhived,
                        isSelected: !!isIncludingArhived
                    }
                ];

            Object.keys(EventStatus).map(key => {
                const selectedStatus = this.appliedParams.status;
                return {
                    label: this.#translate.instant(`FILTERS.STATUS.OPTIONS.${EventStatus[key].toUpperCase()}`),
                    value: EventStatus[key as keyof typeof EventStatus],
                    isSelected: selectedStatus?.includes(EventStatus[key])
                };
            });
        }

        if (this.target === 'tickets' || this.target === 'transactions') {
            this.#ticketsBaseSrv.ticketList.getData$().pipe(
                filter(Boolean),
                takeUntil(this.#onDestroy)
            ).subscribe((response: any) => {
                const channelHash = {};
                const channelFilter = this.$listOfFilters().find(
                    filter => filter.key === 'channel_id'
                );
                channelFilter.filterOptions = response
                    .map(ticket => {
                        const selectedChannels = this.appliedParams.channel_id;
                        return {
                            label: ticket.channel.name,
                            value: ticket.channel.id,
                            isSelected: selectedChannels?.includes(ticket.channel.id)
                        };
                    })
                    .filter(current => {
                        const exists = !channelHash[current.value];
                        channelHash[current.value] = true;
                        return exists;
                    });

                const organizerHash = {};
                this.$listOfFilters().find(
                    filter => filter.key === 'event_entity_id'
                ).filterOptions = response
                    .map(ticket => {
                        const selectedOrganizers = this.appliedParams.event_entity_id;

                        return ({
                            label: ticket.ticket.allocation.event.entity.name,
                            value: ticket.ticket.allocation.event.entity.id,
                            isSelected: selectedOrganizers?.includes(ticket.ticket.allocation.event.entity.id)
                        });
                    })
                    .filter(current => {
                        const exists = !organizerHash[current.value];
                        organizerHash[current.value] = true;
                        return exists;
                    });

                const eventHash = {};
                const eventsFilter = this.$listOfFilters().find(
                    filter => filter.key === 'event_id'
                );
                eventsFilter.filterOptions = response
                    .map(ticket => {
                        const selectedEvents = this.appliedParams.event_id;

                        return ({
                            label: ticket.ticket.allocation.event.name,
                            value: ticket.ticket.allocation.event.id,
                            isSelected: selectedEvents?.includes(ticket.ticket.allocation.event.id)
                        });
                    })
                    .filter(current => {
                        const exists = !eventHash[current.value];
                        eventHash[current.value] = true;
                        return exists;
                    });

                const sessionHash = {};
                const sessionsFilter = this.$listOfFilters().find(
                    filter => filter.key === 'session_id'
                );
                sessionsFilter.filterOptions = response
                    .map(ticket => {
                        const selectedSessions = this.appliedParams.session_id;
                        return ({
                            label: ticket.ticket.allocation.session.name,
                            value: ticket.ticket.allocation.session.id,
                            isSelected: selectedSessions?.includes(ticket.ticket.allocation.session.id)
                        });
                    })
                    .filter(current => {
                        const exists = !sessionHash[current.value];
                        sessionHash[current.value] = true;
                        return exists;
                    });

                this.#changeDetector.detectChanges();
            });
        }

        if (this.target === 'tickets') {
            this.#ticketsBaseSrv.ticketList.getData$().pipe(
                filter(Boolean),
                takeUntil(this.#onDestroy)
            ).subscribe((response: any) => {
                const channelManagerHash = {};
                const channelEntityFilter = this.$listOfFilters().find(
                    filter => filter.key === 'channel_entity_id'
                );
                channelEntityFilter.filterOptions = response
                    .map(ticket => {
                        const selectedChannelEntities = this.appliedParams.channel_entity_id;
                        return {
                            label: ticket.channel.entity.name,
                            value: ticket.channel.entity.id,
                            isSelected: selectedChannelEntities?.includes(ticket.channel.entity.id)
                        };
                    })
                    .filter(current => {
                        const exists = !channelManagerHash[current.value];
                        channelManagerHash[current.value] = true;
                        return exists;
                    });

                const stateFilter = this.$listOfFilters().find(
                    filter => filter.key === 'state'
                );
                stateFilter.filterOptions = Object.keys(TicketState).map(
                    state => ({
                        label: this.#translate.instant('FILTERS.TICKET-STATE.' + TicketState[state]),
                        value: TicketState[state],
                        isSelected: !!Object.keys(this.appliedParams).find(
                            appliedParam => (this.appliedParams[appliedParam] === TicketState[state])
                        )
                    })
                );

                const printFilter = this.$listOfFilters().find(
                    filter => filter.key === 'print'
                );
                const selectedPrint = this.appliedParams.print;
                printFilter.filterOptions = [
                    {
                        label: this.#translate.instant('FILTERS.PRINTED'),
                        value: 'PRINTED',
                        isSelected: selectedPrint === 'PRINTED'
                    },
                    {
                        label: this.#translate.instant('FILTERS.NOT_PRINTED'),
                        value: 'NOT_PRINTED',
                        isSelected: selectedPrint === 'NOT_PRINTED'
                    }
                ];

                const validationFilter = this.$listOfFilters().find(
                    filter => filter.key === 'validation'
                );
                const selectedValidation = this.appliedParams.validation;

                validationFilter.filterOptions = [
                    {
                        label: this.#translate.instant('FILTERS.VALIDATED'),
                        value: 'VALIDATED',
                        isSelected: selectedValidation === 'VALIDATED'
                    },
                    {
                        label: this.#translate.instant('FILTERS.NOT_VALIDATED'),
                        value: 'NOT_VALIDATED',
                        isSelected: selectedValidation === 'NOT_VALIDATED'
                    }
                ];

                const invitationFilter = this.$listOfFilters().find(
                    filter => filter.key === 'ticket_type'
                );

                invitationFilter.filterOptions = Object.values(TicketType).map(
                    validationType => {
                        const selectedInvitations = this.appliedParams.ticket_type;

                        return ({
                            label: this.#translate.instant('FILTERS.' + validationType),
                            value: validationType,
                            isSelected: selectedInvitations?.includes(validationType)
                        });
                    }
                );

                const sectorHash = {};
                const sectorFilter = this.$listOfFilters().find(
                    filter => filter.key === 'sector_id'
                );
                const selectedSectors = this.appliedParams.sector_id;
                sectorFilter.filterOptions = response
                    .map(ticket => ticket.ticket.allocation.sector
                        ? {
                            label: ticket.ticket.allocation.sector?.name,
                            value: ticket.ticket.allocation.sector?.id,
                            isSelected: selectedSectors?.includes(ticket.ticket.allocation.sector?.id)
                        }
                        : null)
                    .filter(a => a)
                    .filter(current => {
                        const exists = !sectorHash[current.value];
                        sectorHash[current.value] = true;
                        return exists;
                    });

                const priceZoneHash = {};
                const priceZoneFilter = this.$listOfFilters().find(
                    filter => filter.key === 'price_type_id'
                );
                const selectedPriceZones = this.appliedParams.price_type_id;

                priceZoneFilter.filterOptions = response
                    .map(ticket => ({
                        label: ticket.ticket.allocation.price_type.name,
                        value: ticket.ticket.allocation.price_type.id,
                        isSelected: selectedPriceZones?.includes(ticket.ticket.allocation.price_type.id)
                    }))
                    .filter(current => {
                        const exists = !priceZoneHash[current.value];
                        priceZoneHash[current.value] = true;
                        return exists;
                    });

                this.#changeDetector.detectChanges();
            });
        }

        if (this.target === 'transactions') {
            this.#ordersSrv.getOrdersListData$().pipe(
                filter(Boolean),
                takeUntil(this.#onDestroy)
            ).subscribe(response => {
                const selectedTypes = this.appliedParams.type;
                this.$listOfFilters().find(
                    filter => filter.key === 'type'
                ).filterOptions = Object.keys(OrderType).map(key => ({
                    label: this.#translate.instant(`FILTERS.TRANSACTION-TYPE.${OrderType[key]}`),
                    value: OrderType[key],
                    isSelected: selectedTypes?.includes(OrderType[key])
                }));

                const transactionCodeHash = {};
                const selectedCodes = this.appliedParams.code;
                this.$listOfFilters().find(
                    filter => filter.key === 'code'
                ).filterOptions = response
                    .map(transaction => ({
                        label: transaction.code,
                        value: transaction.code,
                        isSelected: selectedCodes?.includes(transaction.code)
                    }))
                    .filter(current => {
                        const exists = !transactionCodeHash[current.value];
                        transactionCodeHash[current.value] = true;
                        return current.value && exists;
                    });
                this.#changeDetector.detectChanges();
            });
        }

        if (this.target === 'sessions') {
            // Status endpoints
            this.$listOfFilters().find(
                filter => filter.key === 'status'
            ).filterOptions = Object.keys(SessionStatus).map(key => {
                const selectedStatus = this.appliedParams.status;

                return {
                    label: this.#translate.instant(`FILTERS.SESSION_STATUS.OPTIONS.${SessionStatus[key].toUpperCase()}`),
                    value: SessionStatus[key as keyof typeof SessionStatus],
                    isSelected: selectedStatus?.includes(SessionStatus[key])
                };
            });
        }
    }
}
