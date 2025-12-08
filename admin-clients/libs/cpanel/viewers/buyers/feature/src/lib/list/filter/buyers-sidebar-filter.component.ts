/* eslint-disable @typescript-eslint/dot-notation */
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EventPromotionsService } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import {
    BuyersService, BuyersFilterContentData, BuyerGender, BuyerType, BuyersFilterField, BuyersQueryWrapper, BuyersQuery
} from '@admin-clients/cpanel-viewers-buyers-data-access';
import { CountriesService, Country, Region, RegionsService } from '@admin-clients/shared/common/data-access';
import {
    FilterItem, FilterItemBuilder, FilterItemValue, FilterWrapped, ListFiltersService
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge, mergeObjects } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Inject, input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import { Params } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { combineLatest, Observable, of, Subject } from 'rxjs';
import { filter, first, map, pairwise, shareReplay, startWith, takeUntil } from 'rxjs/operators';
import { buyerFilterElements as bfe, OrderItemValueType, TicketDataType } from '../buyers-filter-elements';
import { BuyersQueryListComponent } from '../query-list/buyers-query-list.component';

@Component({
    selector: 'app-buyers-sidebar-filter',
    templateUrl: './buyers-sidebar-filter.component.html',
    styleUrls: ['./buyers-sidebar-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class BuyersSidebarFilterComponent extends FilterWrapped implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    @ViewChild(BuyersQueryListComponent) private readonly _queryListComponent: BuyersQueryListComponent;
    private readonly _formStruct = {
        [bfe.name.param]: {
            isActive: false,
            value: null as string
        },
        [bfe.surname.param]: {
            isActive: false,
            value: null as string
        },
        [bfe.age.param]: {
            isActive: false,
            [bfe.age.from.param]: { isActive: false, value: null as number },
            [bfe.age.to.param]: { isActive: false, value: null as number }
        },
        [bfe.gender.param]: { isActive: false, value: null as BuyerGender },
        [bfe.country.param]: { isActive: false, value: null as string },
        [bfe.phone.param]: { isActive: false, value: null as string },
        [bfe.email.param]: { isActive: false, value: null as string },
        [bfe.countrySubdivision.param]: { isActive: false, value: null as number[] },
        [bfe.allowComercialMailing.param]: { isActive: false, value: null as boolean },
        [bfe.type.param]: { isActive: false, value: null as BuyerType },
        [bfe.subscriptionLists.param]: { isActive: false, value: [] as number[] },
        [bfe.collectives.param]: { isActive: false, value: [] as number[] },
        [bfe.channels.param]: { isActive: false, value: [] as number[] },
        [bfe.orderDate.param]: {
            isActive: false,
            [bfe.orderDate.from.param]: { isActive: false, value: null as string },
            [bfe.orderDate.to.param]: { isActive: false, value: null as string }
        },
        [bfe.firstOrderDate.param]: {
            isActive: false,
            [bfe.firstOrderDate.from.param]: { isActive: false, value: null as string },
            [bfe.firstOrderDate.to.param]: { isActive: false, value: null as string }
        },
        [bfe.withoutOrdersDate.param]: {
            isActive: false,
            [bfe.withoutOrdersDate.from.param]: { isActive: false, value: null as string },
            [bfe.withoutOrdersDate.to.param]: { isActive: false, value: null as string }
        },
        [bfe.ordersPurchased.param]: {
            isActive: false,
            [bfe.ordersPurchased.from.param]: { isActive: false, value: null as number },
            [bfe.ordersPurchased.to.param]: { isActive: false, value: null as number }
        },
        [bfe.itemsPurchased.param]: {
            isActive: false,
            [bfe.itemsPurchased.from.param]: { isActive: false, value: null as number },
            [bfe.itemsPurchased.to.param]: { isActive: false, value: null as number }
        },
        [bfe.itemsRefunded.param]: {
            isActive: false,
            [bfe.itemsRefunded.from.param]: { isActive: false, value: null as number },
            [bfe.itemsRefunded.to.param]: { isActive: false, value: null as number }
        },
        [bfe.presaleDays.param]: {
            isActive: false,
            [bfe.presaleDays.from.param]: { isActive: false, value: null as number },
            [bfe.presaleDays.to.param]: { isActive: false, value: null as number }
        },
        [bfe.ordersPurchasedPrice.param]: {
            isActive: false,
            [bfe.ordersPurchasedPrice.from.param]: { isActive: false, value: null as number },
            [bfe.ordersPurchasedPrice.to.param]: { isActive: false, value: null as number }
        },
        [bfe.ordersRefundedPrice.param]: {
            isActive: false,
            [bfe.ordersRefundedPrice.from.param]: { isActive: false, value: null as number },
            [bfe.ordersRefundedPrice.to.param]: { isActive: false, value: null as number }
        },

        [bfe.ticketData.param]: {
            isActive: false,
            value: null as TicketDataType,
            [bfe.ticketData.events.param]: { isActive: false, value: null as number | number[] },
            [bfe.ticketData.sessions.param]: { isActive: false, value: [] as number[] },
            [bfe.ticketData.promotions.param]: { isActive: false, value: [] as number[] },
            [bfe.ticketData.sessionDatesFrom.param]: { isActive: false, value: null as string },
            [bfe.ticketData.sessionDatesTo.param]: { isActive: false, value: null as string }
        },

        [bfe.orderItemAvgPrice.param]: {
            isActive: false,
            [bfe.orderItemAvgPrice.from.param]: { isActive: false, value: null as number },
            [bfe.orderItemAvgPrice.to.param]: { isActive: false, value: null as number }
        },

        [bfe.orderItemValue.param]: {
            isActive: false,
            value: null as OrderItemValueType,
            [bfe.orderItemValue.basePriceFrom.param]: { isActive: false, value: null as number },
            [bfe.orderItemValue.basePriceTo.param]: { isActive: false, value: null as number },
            [bfe.orderItemValue.finalPriceFrom.param]: { isActive: false, value: null as number },
            [bfe.orderItemValue.finalPriceTo.param]: { isActive: false, value: null as number },
            [bfe.orderItemValue.invitations.param]: { isActive: false, value: null as boolean }
        }
    };

    private _entityId: number;
    readonly isActiveParam = 'isActive';
    readonly valueParam = 'value';
    readonly buyerType = BuyerType;
    readonly dateFormat = moment.localeData().longDateFormat(this._formats.display.dateInput).toLowerCase();
    readonly bfe = bfe;
    readonly dateTimeFormats = DateTimeFormats;
    userCurrency: string;
    currentQuery$: Observable<BuyersQueryWrapper>;
    form: UntypedFormGroup;
    genders = [BuyerGender.male, BuyerGender.female, BuyerGender.notEspecified];
    subscriptionLists$: Observable<IdName[]>;
    promotionsList$: Observable<IdName[]>;
    channelsList$: Observable<IdName[]>;
    collectivesList$: Observable<IdName[]>;
    eventsList$: Observable<IdName[]>;
    sessionsList$: Observable<BuyersFilterContentData[]>;
    countries$: Observable<Country[]>;
    countrySubdivisions$: Observable<Region[]>;

    readonly $isEntityIdNeeded = input<boolean | undefined>(false, { alias: 'isEntityIdNeeded' });

    constructor(
        private _translateSrv: TranslateService,
        private _fb: UntypedFormBuilder,
        @Inject(MAT_DATE_FORMATS) private _formats: MatDateFormats,
        private _authService: AuthenticationService,
        private _buyersSrv: BuyersService,
        private _countriesSrv: CountriesService,
        private _regionsSrv: RegionsService,
        private _eventPromotionsSrv: EventPromotionsService,
        private _filterSrv: ListFiltersService
    ) {
        super();
    }

    ngOnInit(): void {
        this.isLoading$ = booleanOrMerge([
            this._countriesSrv.isCountriesLoading$(),
            this._regionsSrv.isRegionsLoading$(),
            this._buyersSrv.isFilterSubscriptionListsLoading$(),
            this._buyersSrv.isFilterChannelsLoading$(),
            this._buyersSrv.isFilterCollectivesLoading$(),
            this._buyersSrv.isFilterEventsLoading$(),
            this._buyersSrv.isFilterSessionsLoading$(),
            this._buyersSrv.isQueriesLoading$(),
            this._buyersSrv.isQueryLoading$(),
            this._buyersSrv.isQuerySaving$(),
            this._buyersSrv.isDeletingQuery()
        ]);
        this._authService.getLoggedUser$()
            .pipe(first(user => user !== null))
            .subscribe(user => this.userCurrency = user.currency);
        this.promotionsList$ = this._eventPromotionsSrv.promotionsList.getData$();
        this.countries$ = this._countriesSrv.getCountries$();
        this.subscriptionLists$ = this._buyersSrv.getFilterSubscriptionLists$();
        this.channelsList$ = this._buyersSrv.getFilterChannels$();
        this.collectivesList$ = this._buyersSrv.getFilterCollectives$();
        this.eventsList$ = this._buyersSrv.getFilterEvents$();
        this.sessionsList$ = this._buyersSrv.getFilterSessions()
            .pipe(
                filter(sessions => !!sessions),
                map(sessions => sessions.sort((a, b) => (a.start_date > b.start_date) ? 1 : ((b.start_date > a.start_date) ? -1 : 0)))
            );
        this.currentQuery$ = this._buyersSrv.getQuery$().pipe(shareReplay(1));
        this._buyersSrv.loadQueries();
        this._regionsSrv.loadRegions();
        this.initForm();
        this._filterSrv.addListenerBeforeUseFilterValuesModified(filterItems => {
            const newFilterValue = filterItems?.find(fi => fi.key === 'ENTITY')?.values[0]?.value;
            if (newFilterValue && newFilterValue !== this._entityId) {
                this._entityId = newFilterValue;
                this.form.reset();
                this._buyersSrv.setBlankQuery();
            }
        });

        this._buyersSrv.getQuery$()
            .pipe(
                filter(queryWrapper => !!queryWrapper?.query),
                takeUntil(this._onDestroy)
            )
            .subscribe(queryWrapper => this.loadQuery(queryWrapper.query));
    }

    override ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    removeFilter(): void {
        // no chips > not required
    }

    resetFilters(): void {
        this.form.reset();
        this._buyersSrv.setBlankQuery();
    }

    getFilters(): FilterItem[] {
        const filters = this.getItemFilters();
        return filters;
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        this.applyFiltersByUrlParams(params);
        return of(this.getFilters());
    }

    saveQuery(): void {
        this.applyFilters();
        setTimeout(() => this._queryListComponent.saveQuery());
    }

    // FORM

    private initForm(): void {
        //form definition
        this.form = this._fb.group({});
        Object.keys(this._formStruct)
            .forEach(field => this.addFormField(field, this._formStruct[field]));
        // initial value set
        this.form.setValue(mergeObjects(this.form.value, this._formStruct));
        // form change handlers
        this.setFieldActivators();
        this.setCollectionLoaders();
        this.setCountrySubdivisionBehaviours();
        this.setTicketDataBehaviours();
        this.setEventFieldBehaviours();
        this.setOrderItemValueBehaviours();
    }

    private addFormField(field: string, fieldContent: unknown, parent: UntypedFormGroup = null): void {
        parent = parent || this.form;
        const group = this._fb.group({});
        parent.addControl(field, group);
        Object.keys(fieldContent)
            .forEach(fieldName => {
                if (fieldName === this.isActiveParam || fieldName === this.valueParam) {
                    group.addControl(fieldName, this._fb.control(fieldContent[fieldName]));
                } else {
                    this.addFormField(fieldName, fieldContent[fieldName], group);
                }
            });
    }

    private setFieldActivators(): void {
        // simple fields, each one is a group with isActive and value fields only
        [
            bfe.name, bfe.surname, bfe.gender, bfe.type, bfe.allowComercialMailing, bfe.country, bfe.phone, bfe.email,
            bfe.countrySubdivision, bfe.subscriptionLists, bfe.collectives, bfe.channels, bfe.ticketData, bfe.orderItemValue
        ]
            .forEach(fieldToActivate => this.setFieldActivator(fieldToActivate));
        // grouped fields, each one is a group that contains isActive and value (optional) properties, and other subgroups
        // with the same structure, isActive and value
        [
            bfe.age, bfe.orderDate, bfe.firstOrderDate, bfe.withoutOrdersDate, bfe.ordersPurchased, bfe.itemsPurchased, bfe.itemsRefunded,
            bfe.presaleDays, bfe.ordersPurchasedPrice, bfe.ordersRefundedPrice, bfe.orderItemAvgPrice
        ]
            .forEach(activatorField => this.setFormGroupActivator(activatorField));
    }

    private setFieldActivator(...fields: { param: string }[]): void {
        const activatorParam = fields[0].param;
        const paramsToActivate = fields.map(field => field.param);
        const activatorGroup = this.form.get(activatorParam);
        activatorGroup.get(this.isActiveParam).valueChanges
            .pipe(
                startWith(null as boolean),
                pairwise(),
                takeUntil(this._onDestroy)
            )
            .subscribe(([prevActive, isActived]) => {
                if (prevActive !== isActived) {
                    paramsToActivate.forEach(paramToActivate => {
                        const groupToActivate = this.form.get(paramToActivate);
                        if (isActived) {
                            groupToActivate.enable({ emitEvent: false });
                        } else {
                            groupToActivate.get(this.valueParam)?.setValue(null);
                            groupToActivate.disable({ emitEvent: false });
                            groupToActivate.get(this.isActiveParam).enable({ emitEvent: false });
                        }
                        if (activatorGroup !== groupToActivate) {
                            groupToActivate.get(this.isActiveParam).setValue(isActived);
                        }
                    });
                }
            });
    }

    private setFormGroupActivator(parentGroupDef: { param: string }): void {
        const group = this.form.get(parentGroupDef.param) as UntypedFormGroup;
        group.get(this.isActiveParam).valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(isActive => {
                Object.keys(this.form.get(parentGroupDef.param).value)
                    .forEach(groupValueKey => {
                        if (group.get(groupValueKey) instanceof UntypedFormGroup) {
                            group.get(groupValueKey).setValue({ isActive, value: null });
                        }
                    });
            });
    }

    private setCollectionLoaders(): void {
        this.setCollectionLoader([bfe.country.param], () => this._countriesSrv.loadCountries());
        this.setCollectionLoader(
            [bfe.subscriptionLists.param],
            () => (!this.$isEntityIdNeeded() || (this.$isEntityIdNeeded() && Number(this._entityId))) &&
                this._buyersSrv.loadFilterCollection(this._entityId, BuyersFilterField.subscriptionLists)
        );
        this.setCollectionLoader(
            [bfe.channels.param],
            () => (!this.$isEntityIdNeeded() || (this.$isEntityIdNeeded() && Number(this._entityId))) &&
                this._buyersSrv.loadFilterCollection(this._entityId, BuyersFilterField.channels)
        );
        this.setCollectionLoader(
            [bfe.collectives.param],
            () => (!this.$isEntityIdNeeded() || (this.$isEntityIdNeeded() && Number(this._entityId))) &&
                this._buyersSrv.loadFilterCollection(this._entityId, BuyersFilterField.collectives)
        );
        this.setCollectionLoader(
            [bfe.ticketData.param, bfe.ticketData.events.param],
            () => {
                (!this.$isEntityIdNeeded() || (this.$isEntityIdNeeded() && Number(this._entityId))) &&
                this._buyersSrv.loadFilterCollection(this._entityId, BuyersFilterField.events);
            }
        );
        this.setCollectionLoader(
            [bfe.ticketData.param, bfe.ticketData.events.param],
            parentId => {
                (!this.$isEntityIdNeeded() || (this.$isEntityIdNeeded() && Number(this._entityId))) &&
                this._buyersSrv.loadFilterCollection(this._entityId, BuyersFilterField.sessions, parentId);
            },
            [bfe.ticketData.param],
            TicketDataType.eventAndSessions
        );
        this.setCollectionLoader(
            [bfe.ticketData.param, bfe.ticketData.events.param],
            parentId => this._eventPromotionsSrv.promotionsList.load(parentId, { limit: 999, offset: 0, sort: 'name:asc' }),
            [bfe.ticketData.param],
            TicketDataType.eventAndPromos
        );
    }

    private setCollectionLoader(
        ctrlPath: string[],
        loaderFn: (parentId?: number) => void,
        optionsCtrlPath: string[] = null,
        loaderOption: string = null
    ): void {
        if (optionsCtrlPath && loaderOption) {
            const optionsCtrl = optionsCtrlPath && this.form.get([...optionsCtrlPath, this.valueParam]) || null;
            this.form.get([...ctrlPath, this.valueParam]).valueChanges
                .pipe(
                    filter(value => optionsCtrl.value === loaderOption && !!value),
                    takeUntil(this._onDestroy)
                )
                .subscribe(value => loaderFn(value));
        } else {
            this.form.get([...ctrlPath, this.isActiveParam]).valueChanges
                .pipe(
                    startWith(null as boolean),
                    pairwise(),
                    filter(([prevActive, isActived]) => isActived && !prevActive),
                    takeUntil(this._onDestroy)
                )
                .subscribe(_ => loaderFn());
        }
    }

    private setTicketDataBehaviours(): void {
        this.form.get([bfe.ticketData.param, this.valueParam]).valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => {
                // resets childs values
                [
                    bfe.ticketData.events, bfe.ticketData.sessions, bfe.ticketData.promotions,
                    bfe.ticketData.sessionDatesFrom, bfe.ticketData.sessionDatesTo
                ]
                    .forEach(field => this.form.get([bfe.ticketData.param, field.param]).setValue({ isActive: false, value: null }));
                // value generator
                const newValue = (): { isActive: boolean; value: unknown } => ({ isActive: true, value: null });
                // activate the current group selected option
                switch (value) {
                    case bfe.ticketData.opts.event:
                        this.form.get([bfe.ticketData.param, bfe.ticketData.events.param]).setValue(newValue());
                        break;
                    case bfe.ticketData.opts.eventAndSessions:
                        this.form.get([bfe.ticketData.param, bfe.ticketData.events.param]).setValue(newValue());
                        this.form.get([bfe.ticketData.param, bfe.ticketData.sessions.param]).setValue(newValue());
                        break;
                    case bfe.ticketData.opts.eventAndPromos:
                        this.form.get([bfe.ticketData.param, bfe.ticketData.events.param]).setValue(newValue());
                        this.form.get([bfe.ticketData.param, bfe.ticketData.promotions.param]).setValue(newValue());
                        break;
                    case bfe.ticketData.opts.sessionDates:
                        this.form.get([bfe.ticketData.param, bfe.ticketData.sessionDatesFrom.param]).setValue(newValue());
                        this.form.get([bfe.ticketData.param, bfe.ticketData.sessionDatesTo.param]).setValue(newValue());
                        break;
                }
            });
    }

    private setEventFieldBehaviours(): void {
        // session and promotion controls handler, they appear with event interaction
        const eventsFieldPath = [bfe.ticketData.param, bfe.ticketData.events.param];
        const sessionsFieldPath = [bfe.ticketData.param, bfe.ticketData.sessions.param];
        const promotionsFieldPath = [bfe.ticketData.param, bfe.ticketData.promotions.param];
        this.form.get(eventsFieldPath).valueChanges
            .pipe<{ isActive: boolean; value: number[] }>(takeUntil(this._onDestroy))
            .subscribe(eventsValue => {
                const isEventAndSessionType = this.form.get([bfe.ticketData.param]).value === TicketDataType.eventAndSessions;
                if (isEventAndSessionType) {
                    this.activateEventSubfield(eventsValue, sessionsFieldPath, () => this._buyersSrv.clearFilterSessions());
                }
                const isEventAndPromoType = this.form.get([bfe.ticketData.param]).value === TicketDataType.eventAndPromos;
                if (isEventAndPromoType) {
                    this.activateEventSubfield(eventsValue, promotionsFieldPath, () => this._eventPromotionsSrv.promotionsList.clear());
                }
            });
    }

    private activateEventSubfield(
        eventProps: { isActive: boolean; value: number[] }, subCtrlPath: string | string[], disableCallback: () => void
    ): void {
        const subControl = this.form.get(subCtrlPath);
        if (eventProps.isActive && eventProps.value?.length === 1) {
            subControl.enable();
        } else {
            subControl.disable();
            subControl.setValue({ isActive: false, value: [] });
            disableCallback();
        }
    }

    private setCountrySubdivisionBehaviours(): void {
        // country subdivision, country conditionates country subdivisions source list, and an empty source list disables the control
        this.form.get(bfe.country.param).valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => this.form.get((bfe.countrySubdivision.param)).setValue({ isActive: false, value: [] }));
        this.countrySubdivisions$ = combineLatest([
            this.form.get([bfe.country.param, this.valueParam]).valueChanges,
            this._regionsSrv.getRegions$()
        ])
            .pipe(
                map(([selectedCountry, countrySubdivisions]) => {
                    if (selectedCountry?.length && countrySubdivisions?.length) {
                        return countrySubdivisions.filter(region => region.code.startsWith(selectedCountry));
                    } else {
                        return [];
                    }
                })
            );
        this.countrySubdivisions$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(countrySubdivisions => {
                const countrySubdivisionControl = this.form.get((bfe.countrySubdivision.param));
                if (countrySubdivisions?.length) {
                    countrySubdivisionControl.enable();
                } else {
                    countrySubdivisionControl.disable();
                }
            });
    }

    private setOrderItemValueBehaviours(): void {
        this.form.get([bfe.orderItemValue.param, this.valueParam]).valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => {
                // resets childs values
                [
                    bfe.orderItemValue.basePriceFrom, bfe.orderItemValue.basePriceTo,
                    bfe.orderItemValue.finalPriceFrom, bfe.orderItemValue.finalPriceTo,
                    bfe.orderItemValue.invitations
                ]
                    .forEach(field => this.form.get([bfe.orderItemValue.param, field.param]).setValue({ isActive: false, value: null }));
                // value generator
                const newValue = (): { isActive: boolean; value: unknown } => ({ isActive: true, value: null });
                // activate the current group selected option
                switch (value) {
                    case bfe.orderItemValue.opts.ticket:
                        this.form.get([bfe.orderItemValue.param, bfe.orderItemValue.basePriceFrom.param]).setValue(newValue());
                        this.form.get([bfe.orderItemValue.param, bfe.orderItemValue.basePriceTo.param]).setValue(newValue());
                        break;
                    case bfe.orderItemValue.opts.order:
                        this.form.get([bfe.orderItemValue.param, bfe.orderItemValue.finalPriceFrom.param]).setValue(newValue());
                        this.form.get([bfe.orderItemValue.param, bfe.orderItemValue.finalPriceTo.param]).setValue(newValue());
                        break;
                    case bfe.orderItemValue.opts.invitations:
                        this.form.get([bfe.orderItemValue.param, bfe.orderItemValue.invitations.param]).setValue(newValue());
                        this.form.get([bfe.orderItemValue.param, bfe.orderItemValue.invitations.param, this.valueParam])
                            .setValue(true);
                        break;
                }
            });
    }

    // GET ITEM FILTERS

    private getItemFilters(): FilterItem[] {
        return [
            // personal data
            this.getItemFilter(bfe.name, this.getFilterStringFormValue(bfe.name)),
            this.getItemFilter(bfe.surname, this.getFilterStringFormValue(bfe.surname)),
            this.getItemFilter(bfe.age.from, this.getFilterNumberFormValue(bfe.age.from, bfe.age)),
            this.getItemFilter(bfe.age.to, this.getFilterNumberFormValue(bfe.age.to, bfe.age)),
            this.getItemFilter(bfe.gender, this.getFilterStringFormValue(bfe.gender)),
            this.getItemFilter(bfe.country, this.getFilterStringFormValue(bfe.country)),
            this.getItemFilter(bfe.phone, this.getFilterStringFormValue(bfe.phone)),
            this.getItemFilter(bfe.email, this.getFilterStringFormValue(bfe.email)),
            this.getItemFilter(bfe.countrySubdivision, this.getFilterArrayFormValue(bfe.countrySubdivision)),
            // communication data
            this.getItemFilter(bfe.type, this.getFilterStringFormValue(bfe.type)),
            this.getItemFilter(bfe.allowComercialMailing, this.getFilterStringFormValue(bfe.allowComercialMailing)),
            this.getItemFilter(bfe.subscriptionLists, this.getFilterArrayFormValue(bfe.subscriptionLists)),
            this.getItemFilter(bfe.collectives, this.getFilterArrayFormValue(bfe.collectives)),
            this.getItemFilter(bfe.channels, this.getFilterArrayFormValue(bfe.channels)),
            // business data
            this.getItemFilter(bfe.orderDate.from, this.getFilterDateFormValue(bfe.orderDate.from, bfe.orderDate)),
            this.getItemFilter(bfe.orderDate.to, this.getFilterDateFormValue(bfe.orderDate.to, bfe.orderDate, false)),
            this.getItemFilter(bfe.firstOrderDate.from, this.getFilterDateFormValue(bfe.firstOrderDate.from, bfe.firstOrderDate)),
            this.getItemFilter(bfe.firstOrderDate.to, this.getFilterDateFormValue(bfe.firstOrderDate.to, bfe.firstOrderDate, false)),
            this.getItemFilter(bfe.withoutOrdersDate.from, this.getFilterDateFormValue(bfe.withoutOrdersDate.from, bfe.withoutOrdersDate)),
            this.getItemFilter(
                bfe.withoutOrdersDate.to, this.getFilterDateFormValue(bfe.withoutOrdersDate.to, bfe.withoutOrdersDate, false)),
            this.getItemFilter(bfe.ordersPurchased.from, this.getFilterNumberFormValue(bfe.ordersPurchased.from, bfe.ordersPurchased)),
            this.getItemFilter(bfe.ordersPurchased.to, this.getFilterNumberFormValue(bfe.ordersPurchased.to, bfe.ordersPurchased)),
            this.getItemFilter(bfe.itemsPurchased.from, this.getFilterNumberFormValue(bfe.itemsPurchased.from, bfe.itemsPurchased)),
            this.getItemFilter(bfe.itemsPurchased.to, this.getFilterNumberFormValue(bfe.itemsPurchased.to, bfe.itemsPurchased)),
            this.getItemFilter(bfe.itemsRefunded.from, this.getFilterNumberFormValue(bfe.itemsRefunded.from, bfe.itemsRefunded)),
            this.getItemFilter(bfe.itemsRefunded.to, this.getFilterNumberFormValue(bfe.itemsRefunded.to, bfe.itemsRefunded)),
            this.getItemFilter(bfe.presaleDays.from, this.getFilterNumberFormValue(bfe.presaleDays.from, bfe.presaleDays)),
            this.getItemFilter(bfe.presaleDays.to, this.getFilterNumberFormValue(bfe.presaleDays.to, bfe.presaleDays)),
            this.getItemFilter(
                bfe.ordersPurchasedPrice.from, this.getFilterNumberFormValue(bfe.ordersPurchasedPrice.from, bfe.ordersPurchasedPrice)),
            this.getItemFilter(
                bfe.ordersPurchasedPrice.to, this.getFilterNumberFormValue(bfe.ordersPurchasedPrice.to, bfe.ordersPurchasedPrice)),
            this.getItemFilter(
                bfe.ordersRefundedPrice.from, this.getFilterNumberFormValue(bfe.ordersRefundedPrice.from, bfe.ordersRefundedPrice)),
            this.getItemFilter(
                bfe.ordersRefundedPrice.to, this.getFilterNumberFormValue(bfe.ordersRefundedPrice.to, bfe.ordersRefundedPrice)),
            // products data
            this.getItemFilter(bfe.ticketData.events,
                this.form.get([bfe.ticketData.param, this.valueParam]).value === TicketDataType.event ?
                    this.getFilterArrayFormValue(bfe.ticketData.events, bfe.ticketData) :
                    this.getFilterNumberFormValue(bfe.ticketData.events, bfe.ticketData)
            ),
            this.getItemFilter(bfe.ticketData.sessions, this.getFilterArrayFormValue(bfe.ticketData.sessions, bfe.ticketData)),
            this.getItemFilter(bfe.ticketData.promotions, this.getFilterArrayFormValue(bfe.ticketData.promotions, bfe.ticketData)),
            this.getItemFilter(
                bfe.ticketData.sessionDatesFrom,
                this.getFilterDateFormValue(bfe.ticketData.sessionDatesFrom, bfe.ticketData)
            ),
            this.getItemFilter(
                bfe.ticketData.sessionDatesTo,
                this.getFilterDateFormValue(bfe.ticketData.sessionDatesTo, bfe.ticketData, false)
            ),
            this.getItemFilter(
                bfe.orderItemAvgPrice.from, this.getFilterNumberFormValue(bfe.orderItemAvgPrice.from, bfe.orderItemAvgPrice)),
            this.getItemFilter(bfe.orderItemAvgPrice.to, this.getFilterNumberFormValue(bfe.orderItemAvgPrice.to, bfe.orderItemAvgPrice)),
            this.getItemFilter(bfe.orderItemValue.basePriceFrom,
                this.getFilterNumberFormValue(bfe.orderItemValue.basePriceFrom, bfe.orderItemValue)),
            this.getItemFilter(bfe.orderItemValue.basePriceTo,
                this.getFilterNumberFormValue(bfe.orderItemValue.basePriceTo, bfe.orderItemValue)),
            this.getItemFilter(bfe.orderItemValue.finalPriceFrom,
                this.getFilterNumberFormValue(bfe.orderItemValue.finalPriceFrom, bfe.orderItemValue)),
            this.getItemFilter(bfe.orderItemValue.finalPriceTo,
                this.getFilterNumberFormValue(bfe.orderItemValue.finalPriceTo, bfe.orderItemValue)),
            this.getItemFilter(bfe.orderItemValue.invitations,
                this.getFilterBooleanFormValue(bfe.orderItemValue.invitations, bfe.orderItemValue)),
            this.getFilterEntity()
        ]
            .filter(element => !!element);
    }

    private getFilterEntity(): FilterItem {
        const filterItem = new FilterItem('ENTITY', null);
        if (this._entityId) {
            filterItem.values = [new FilterItemValue(this._entityId, null)];
            filterItem.urlQueryParams['entityId'] = this._entityId.toString();
            return filterItem;
        }
        return null;
    }

    private getItemFilter({ key, param }: { key: string; param: string }, value: string | number | number[] | boolean): FilterItem {
        if (value != null) {
            if (Array.isArray(value)) {
                if (value.length) {
                    return {
                        key,
                        urlQueryParams: {
                            [param]: value.join()
                        },
                        values: value.map(i => ({ value: i, text: String(i) } as FilterItemValue))
                    } as FilterItem;
                }
            } else {
                return new FilterItemBuilder(this._translateSrv)
                    .key(key)
                    .labelKey('.')
                    .queryParam(param)
                    .value({ id: String(value), name: String(value) })
                    .build();
            }
        }
        return null;
    }

    private getFilterStringFormValue(fieldDef: { param: string }, parentFormDef: { param: string } = null): string {
        const formGroup = this.getFormFieldGroup(fieldDef, parentFormDef);
        return (
            formGroup?.get(this.isActiveParam)?.value
            && formGroup?.get(this.valueParam)?.enabled
            && formGroup?.get(this.valueParam)?.value != null
            && String(formGroup.get(this.valueParam)?.value)
        ) || null;
    }

    private getFilterNumberFormValue(fieldDef: { param: string }, parentFormDef: { param: string } = null): number {
        const formGroup = this.getFormFieldGroup(fieldDef, parentFormDef);
        const formCtrl = formGroup?.get(this.valueParam);
        if (!formCtrl?.enabled || typeof formCtrl?.value !== 'number') {
            return null;
        } else {
            return Number(formCtrl?.value);
        }
    }

    private getFilterDateFormValue(
        fieldDef: { param: string }, parentFormDef: { param: string } = null, isFromValue = true
    ): string {
        const formGroup = this.getFormFieldGroup(fieldDef, parentFormDef);
        if (formGroup?.get(this.isActiveParam).value && formGroup?.get(this.valueParam).value != null) {
            const result = moment(formGroup.get(this.valueParam).value).format('YYYY-MM-DD');
            if (isFromValue) {
                return result + 'T00:00:00Z';
            } else {
                return result + 'T23:59:59Z';
            }
        } else {
            return null;
        }
    }

    private getFilterBooleanFormValue(fieldDef: { param: string }, parentFormDef: { param: string } = null): boolean {
        const formGroup = this.getFormFieldGroup(fieldDef, parentFormDef);
        return formGroup?.get(this.isActiveParam).value
            && formGroup?.get(this.valueParam)?.enabled
            && formGroup.get(this.valueParam)?.value !== null
            && Boolean(formGroup.get(this.valueParam)?.value) || null;
    }

    private getFilterArrayFormValue(fieldDef: { param: string }, parentFormDef: { param: string } = null): number[] {
        const formGroup = this.getFormFieldGroup(fieldDef, parentFormDef);
        if (formGroup?.get(this.isActiveParam).value && formGroup?.get(this.valueParam)?.enabled) {
            const value: number[] = formGroup.get(this.valueParam)?.value as number[];
            if (value?.length) {
                return value;
            }
        }
        return [];
    }

    private getFormFieldGroup({ param }: { param: string }, parentFormDef: { param: string } = null): UntypedFormGroup {
        if (parentFormDef) {
            const parentControl = this.form.get(parentFormDef.param) as UntypedFormGroup;
            if (parentControl.value.isActive) {
                return this.form.get([parentFormDef.param, param]) as UntypedFormGroup;
            } else {
                return null;
            }
        } else {
            return this.form.get(param) as UntypedFormGroup;
        }
    }

    // APPLY FILTERS BY URL PARAMS

    private applyFiltersByUrlParams(urlParams: Params): void {
        this._entityId = urlParams['entityId'] || this._entityId;
        const formValues = mergeObjects({}, this._formStruct);
        // Date fields (specific value mapper)
        [
            [bfe.orderDate.from, bfe.orderDate],
            [bfe.orderDate.to, bfe.orderDate],
            [bfe.firstOrderDate.from, bfe.firstOrderDate],
            [bfe.firstOrderDate.to, bfe.firstOrderDate],
            [bfe.withoutOrdersDate.from, bfe.withoutOrdersDate],
            [bfe.withoutOrdersDate.to, bfe.withoutOrdersDate],
            [bfe.ticketData.sessionDatesFrom, bfe.ticketData, bfe.ticketData.opts.sessionDates],
            [bfe.ticketData.sessionDatesTo, bfe.ticketData, bfe.ticketData.opts.sessionDates]
        ]
            .forEach(([fieldToSet, parentField, parentFieldValue]) => this.setFormValueByParam(
                urlParams,
                fieldToSet as { param: string },
                formValues,
                value => value && moment(value, 'YYYY-MM-DDTHH:mm:ss').format('YYYY-MM-DD') || null,
                parentField as { param: string },
                parentFieldValue as string
            ));
        // number fields (specific value mapper)
        [
            [bfe.age.from, bfe.age],
            [bfe.age.to, bfe.age],
            [bfe.ordersPurchased.from, bfe.ordersPurchased],
            [bfe.ordersPurchased.to, bfe.ordersPurchased],
            [bfe.itemsPurchased.from, bfe.itemsPurchased],
            [bfe.itemsPurchased.to, bfe.itemsPurchased],
            [bfe.itemsRefunded.from, bfe.itemsRefunded],
            [bfe.itemsRefunded.to, bfe.itemsRefunded],
            [bfe.presaleDays.from, bfe.presaleDays],
            [bfe.presaleDays.to, bfe.presaleDays],
            [bfe.ordersPurchasedPrice.from, bfe.ordersPurchasedPrice],
            [bfe.ordersPurchasedPrice.to, bfe.ordersPurchasedPrice],
            [bfe.ordersRefundedPrice.from, bfe.ordersRefundedPrice],
            [bfe.ordersRefundedPrice.to, bfe.ordersRefundedPrice],
            [bfe.orderItemAvgPrice.from, bfe.orderItemAvgPrice],
            [bfe.orderItemAvgPrice.to, bfe.orderItemAvgPrice],
            ...(urlParams[bfe.ticketData.sessions.param] || urlParams[bfe.ticketData.promotions.param] ?
                [[bfe.ticketData.events, bfe.ticketData, bfe.ticketData.opts.event]] : []),
            [bfe.orderItemValue.basePriceFrom, bfe.orderItemValue, bfe.orderItemValue.opts.ticket],
            [bfe.orderItemValue.basePriceTo, bfe.orderItemValue, bfe.orderItemValue.opts.ticket],
            [bfe.orderItemValue.finalPriceFrom, bfe.orderItemValue, bfe.orderItemValue.opts.order],
            [bfe.orderItemValue.finalPriceTo, bfe.orderItemValue, bfe.orderItemValue.opts.order]
        ]
            .forEach(([fieldToSet, parentField, parentFieldValue]) =>
                this.setFormValueByParam(
                    urlParams,
                    fieldToSet as { param: string },
                    formValues,
                    v => isNaN(Number(v)) ? null : Number(v),
                    parentField as { param: string },
                    parentFieldValue as string
                ));
        // array number fields (specific value mapper)
        [
            [bfe.subscriptionLists],
            [bfe.collectives],
            [bfe.channels],
            ...(!urlParams[bfe.ticketData.sessions.param] && !urlParams[bfe.ticketData.promotions.param] ?
                [[bfe.ticketData.events, bfe.ticketData, bfe.ticketData.opts.event]] : []),
            [bfe.ticketData.sessions, bfe.ticketData, bfe.ticketData.opts.eventAndSessions],
            [bfe.ticketData.promotions, bfe.ticketData, bfe.ticketData.opts.eventAndPromos]
        ]
            .forEach(([fieldToSet, parentField, parentFieldValue]) => this.setFormArrayValueByParam(
                urlParams,
                fieldToSet as { param: string },
                formValues,
                v => Number(v),
                parentField as { param: string },
                parentFieldValue as string
            ));
        // various type fields
        this.setFormValueByParam(urlParams, bfe.name, formValues, v => v && String(v) || null);
        this.setFormValueByParam(urlParams, bfe.surname, formValues, v => v && String(v) || null);
        this.setFormValueByParam(urlParams, bfe.gender, formValues, v => v as BuyerGender);
        this.setFormValueByParam(urlParams, bfe.type, formValues, v => v as BuyerType);
        this.setFormValueByParam(urlParams, bfe.allowComercialMailing, formValues, v => v == null ? null : v === 'true');
        this.setFormValueByParam(urlParams, bfe.orderItemValue.invitations, formValues, v => v === 'true' || null,
            bfe.orderItemValue, bfe.orderItemValue.opts.invitations);
        this.setFormValueByParam(urlParams, bfe.country, formValues, v => !!v && String(v) || null);
        this.setFormArrayValueByParam(urlParams, bfe.countrySubdivision, formValues, v => String(v));
        this.setFormValueByParam(urlParams, bfe.phone, formValues, v => v && String(v) || null);
        this.setFormValueByParam(urlParams, bfe.email, formValues, v => v && String(v) || null);
        // final operations
        this.form.setValue(formValues);
    }

    private setFormValueByParam<T>(
        urlParams: Params,
        { param: paramName }: { param: string },
        formValues: { [key: string]: unknown },
        paramValueMapper: (v: unknown) => T,
        parentGroupDef: { param: string } = null,
        parentGroupValue: string = null
    ): void {
        const paramValue = paramValueMapper(urlParams[paramName]) ?? null;
        formValues = this.procesParentGroupValue(paramValue, formValues, parentGroupDef, parentGroupValue);
        formValues[paramName] = {
            [this.isActiveParam]: formValues[paramName][this.isActiveParam] || paramValue != null,
            [this.valueParam]: paramValue
        };

    }

    private procesParentGroupValue<T>(
        paramValue: T,
        formValues: { [key: string]: unknown },
        parentGroupDef: { param: string } = null,
        parentGroupValue: string = null
    ): { [key: string]: unknown } {
        if (parentGroupDef) {
            if (paramValue != null) {
                Object.keys(formValues[parentGroupDef.param])
                    .filter(groupKey => groupKey !== this.isActiveParam && groupKey !== this.valueParam)
                    .forEach(groupKey => formValues[parentGroupDef.param][groupKey][this.isActiveParam] = true);
            }
            formValues = formValues[parentGroupDef.param] as { [key: string]: unknown };
            if (paramValue != null) {
                formValues['isActive'] = true;
                if (parentGroupValue !== null) {
                    formValues['value'] = parentGroupValue;
                }
            }
        }
        return formValues;
    }

    private setFormArrayValueByParam<T>(
        urlParams: Params,
        { param: paramName }: { param: string },
        formValues: { [key: string]: unknown },
        paramValueMapper: (v: unknown) => T,
        parentGroupDef: { param: string } = null,
        parentGroupValue: string = null
    ): void {
        formValues = this.procesParentGroupValue(urlParams[paramName], formValues, parentGroupDef, parentGroupValue);
        const paramValue = (urlParams[paramName] as string)?.split(',').filter(v => !!v).map(v => paramValueMapper(v)) || [];
        formValues[paramName] = { isActive: !!(paramValue?.length), value: paramValue };
    }

    // query load

    private loadQuery(query: BuyersQuery): void {
        if (query.entity_id) {
            this._entityId = query.entity_id;
        }
        const formValues = mergeObjects({}, this._formStruct);
        this.setValue(query.name, formValues, [bfe.name]);
        this.setValue(query.surname, formValues, [bfe.surname]);
        this.setValue(query.age?.from, formValues, [bfe.age, bfe.age.from]);
        this.setValue(query.age?.to, formValues, [bfe.age, bfe.age.to]);
        this.setValue(query.gender, formValues, [bfe.gender]);
        if (query.country_subdivision?.length) {
            this.setValue(query.country_subdivision, formValues, [bfe.countrySubdivision]);
            this.setValue(query.country_subdivision[0].substr(0, 2), formValues, [bfe.country]);
        } else {
            this.setValue(query.country, formValues, [bfe.country]);
        }
        this.setValue(query.phone, formValues, [bfe.phone]);
        this.setValue(query.email, formValues, [bfe.email]);
        this.setValue(query.allow_commercial_mailing, formValues, [bfe.allowComercialMailing]);
        this.setValue(query.type, formValues, [bfe.type]);
        this.setValue(query.subscription_list_id, formValues, [bfe.subscriptionLists]);
        this.setValue(query.collective_id, formValues, [bfe.collectives]);
        this.setValue(query.channel_id, formValues, [bfe.channels]);
        this.setValue(query.order?.dates?.purchase?.from, formValues, [bfe.orderDate, bfe.orderDate.from]);
        this.setValue(query.order?.dates?.purchase?.to, formValues, [bfe.orderDate, bfe.orderDate.to]);
        this.setValue(query.order?.dates?.first_purchase?.from, formValues, [bfe.firstOrderDate, bfe.firstOrderDate.from]);
        this.setValue(query.order?.dates?.first_purchase?.to, formValues, [bfe.firstOrderDate, bfe.firstOrderDate.to]);
        this.setValue(query.order?.dates?.without_transactions?.from, formValues, [bfe.withoutOrdersDate, bfe.withoutOrdersDate.from]);
        this.setValue(query.order?.dates?.without_transactions?.to, formValues, [bfe.withoutOrdersDate, bfe.withoutOrdersDate.to]);
        this.setValue(query.order?.transactions?.orders_purchased?.from, formValues, [bfe.ordersPurchased, bfe.ordersPurchased.from]);
        this.setValue(query.order?.transactions?.orders_purchased?.to, formValues, [bfe.ordersPurchased, bfe.ordersPurchased.to]);
        this.setValue(query.order?.transactions?.order_items_purchased?.from, formValues, [bfe.itemsPurchased, bfe.itemsPurchased.from]);
        this.setValue(query.order?.transactions?.order_items_purchased?.to, formValues, [bfe.itemsPurchased, bfe.itemsPurchased.to]);
        this.setValue(query.order?.transactions?.order_items_refunded?.from, formValues, [bfe.itemsRefunded, bfe.itemsRefunded.from]);
        this.setValue(query.order?.transactions?.order_items_refunded?.to, formValues, [bfe.itemsRefunded, bfe.itemsRefunded.to]);
        this.setValue(query.order?.dates?.presale_days?.from, formValues, [bfe.presaleDays, bfe.presaleDays.from]);
        this.setValue(query.order?.dates?.presale_days?.to, formValues, [bfe.presaleDays, bfe.presaleDays.to]);
        this.setValue(query.order?.prices?.orders_purchased?.from, formValues, [bfe.ordersPurchasedPrice, bfe.ordersPurchasedPrice.from]);
        this.setValue(query.order?.prices?.orders_purchased?.to, formValues, [bfe.ordersPurchasedPrice, bfe.ordersPurchasedPrice.to]);
        this.setValue(query.order?.prices?.orders_refunded?.from, formValues, [bfe.ordersRefundedPrice, bfe.ordersRefundedPrice.from]);
        this.setValue(query.order?.prices?.orders_refunded?.to, formValues, [bfe.ordersRefundedPrice, bfe.ordersRefundedPrice.to]);
        this.setValue(query.order?.prices?.order_items_avg?.from, formValues, [bfe.orderItemAvgPrice, bfe.orderItemAvgPrice.from]);
        this.setValue(query.order?.prices?.order_items_avg?.to, formValues, [bfe.orderItemAvgPrice, bfe.orderItemAvgPrice.to]);
        this.setValue(query.order?.prices?.order_items_base_price?.from, formValues,
            [bfe.orderItemValue, bfe.orderItemValue.basePriceFrom]);
        this.setValue(query.order?.prices?.order_items_base_price?.to, formValues,
            [bfe.orderItemValue, bfe.orderItemValue.basePriceTo]);
        this.setValue(query.order?.prices?.order_items_final_price?.from, formValues,
            [bfe.orderItemValue, bfe.orderItemValue.finalPriceFrom]);
        this.setValue(query.order?.prices?.order_items_final_price?.to, formValues, [bfe.orderItemValue, bfe.orderItemValue.finalPriceTo]);
        this.setValue(query.order?.prices?.invitations, formValues, [bfe.orderItemValue, bfe.orderItemValue.invitations]);
        this.setValue(query.session_id, formValues, [bfe.ticketData, bfe.ticketData.sessions]);
        this.setValue(query.event_promotion_id, formValues, [bfe.ticketData, bfe.ticketData.promotions]);
        this.setValue(query.session_dates?.from, formValues, [bfe.ticketData, bfe.ticketData.sessionDatesFrom]);
        this.setValue(query.session_dates?.to, formValues, [bfe.ticketData, bfe.ticketData.sessionDatesTo]);
        // ñapa starts... :
        if (query.session_id !== undefined) {
            this.setValue(bfe.ticketData.opts.eventAndSessions, formValues, [bfe.ticketData]);
            this.setValue(query.event_id[0], formValues, [bfe.ticketData, bfe.ticketData.events]);
        } else if (query.event_promotion_id !== undefined) {
            this.setValue(bfe.ticketData.opts.eventAndPromos, formValues, [bfe.ticketData]);
            this.setValue(query.event_id[0], formValues, [bfe.ticketData, bfe.ticketData.events]);
        } else if (query.session_dates?.from !== undefined || query.session_dates?.to !== undefined) {
            this.setValue(bfe.ticketData.opts.sessionDates, formValues, [bfe.ticketData]);
        } else if (query.event_id !== undefined) {
            this.setValue(bfe.ticketData.opts.event, formValues, [bfe.ticketData]);
            this.setValue(query.event_id, formValues, [bfe.ticketData, bfe.ticketData.events]);
        }
        if (query.order?.prices?.order_items_base_price !== undefined) {
            this.setValue(bfe.orderItemValue.opts.ticket, formValues, [bfe.orderItemValue]);
        } else if (query.order?.prices?.order_items_final_price !== undefined) {
            this.setValue(bfe.orderItemValue.opts.order, formValues, [bfe.orderItemValue]);
        } else if (query.order?.prices?.invitations !== undefined) {
            this.setValue(bfe.orderItemValue.opts.invitations, formValues, [bfe.orderItemValue]);
        }
        this.form.setValue(formValues);
        this.applyFilters();
    }

    private setValue(valueToSet: unknown, value: unknown, path: { param: string }[]): void {
        if (valueToSet != null && (!Array.isArray(valueToSet) || !!valueToSet.length)) {
            let objToSetValue: { value?: unknown } = value;
            path.forEach(fieldDef => {
                mergeObjects(objToSetValue,
                    {
                        [fieldDef.param]: {
                            isActive: true
                        }
                    }
                );
                objToSetValue = objToSetValue[fieldDef.param] as { value: unknown };
            });
            objToSetValue.value = valueToSet;
        }
    }
}
