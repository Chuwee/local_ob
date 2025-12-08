import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketRenewalMappingStatus, SeasonTicketRenewalsService, SeasonTicketRenewalStatus
} from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import {
    FilterItemBuilder, FilterWrapped, FilterItem, FilterItemValue, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { compareWithIdOrCode } from '@admin-clients/shared/data-access/models';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { MAT_DATE_FORMATS, MatOption } from '@angular/material/core';
import { MatFormField } from '@angular/material/form-field';
import { MatSelect } from '@angular/material/select';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { Observable } from 'rxjs';
import { filter, first, map } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-renewals-list-filter',
    templateUrl: './season-ticket-renewals-list-filter.component.html',
    styleUrls: ['./season-ticket-renewals-list-filter.component.scss'],
    imports: [
        ReactiveFormsModule, TranslatePipe, MatFormField, MatSelect, MatOption, SelectSearchComponent, AsyncPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SeasonTicketRenewalsListFilterComponent extends FilterWrapped implements OnInit {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #translate = inject(TranslateService);
    readonly #formats = inject(MAT_DATE_FORMATS);
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #seasonTicketRenewalsSrv = inject(SeasonTicketRenewalsService);

    readonly #formStructure = {
        entity: null,
        mapping_status: null,
        renewal_status: null,
        auto_renewal: null,
        startDate: null,
        endDate: null
    };

    readonly filtersForm = this.#fb.group(Object.assign({}, this.#formStructure));

    readonly renewalsEntities$ = this.#seasonTicketRenewalsSrv.renewalsEntities.getData$()
        .pipe(
            first(Boolean),
            map(resp => resp.data)
        );

    readonly mappingStatusList = Object.values(SeasonTicketRenewalMappingStatus)
        .map(mappingStatus => ({ id: mappingStatus, name: `SEASON_TICKET.RENEWALS.LIST.MAPPING_STATUS_OPTS.${mappingStatus}` }));

    readonly $renewalStatusList = signal(Object.values(SeasonTicketRenewalStatus)
        .map(mappingStatus => ({ id: mappingStatus as string, name: `SEASON_TICKET.RENEWALS.LIST.RENEWAL_STATUS_OPTS.${mappingStatus}` })));

    readonly renewalTypeList = [
        { name: 'SEASON_TICKETS.RENEWALS.LIST.AUTOMATIC_TYPE', value: true },
        { name: 'SEASON_TICKET.RENEWALS.LIST.MANUAL_TYPE', value: false }
    ];

    readonly dateFormat = moment.localeData().longDateFormat(this.#formats.display.dateInput).toLowerCase();

    readonly compareWith = compareWithIdOrCode;

    ngOnInit(): void {
        this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(first())
            .subscribe(st => this.#seasonTicketRenewalsSrv.renewalsEntities.load(st.id));

        this.#seasonTicketRenewalsSrv.renewalsList.getData$()
            .pipe(filter(Boolean))
            .subscribe(renewals => {
                const renewalSubstatus = renewals.find(renewal => renewal.renewal_substatus)?.renewal_substatus;
                if (renewalSubstatus && !this.$renewalStatusList().some(s => s.id === renewalSubstatus)) {
                    this.$renewalStatusList.set([...this.$renewalStatusList().filter(s => s.id !== renewalSubstatus),
                    {
                        id: renewalSubstatus,
                        name: `SEASON_TICKETS.RENEWALS.LIST.RENEWAL_SUBSTATUS_OPTS.${renewalSubstatus}`
                    }]);
                }
            });
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this.#formStructure);

        if (params['mapping_status']) {
            formFields.mapping_status = this.mappingStatusList.find(mappingStatusObj => mappingStatusObj.id === params['mapping_status']);
        }
        if (params['renewal_status']) {
            formFields.renewal_status = this.$renewalStatusList().find(v => v.id === params['renewal_status']) || null;
        }
        if (params['renewal_substatus']) {
            const renewalSubstatus = {
                id: params['renewal_substatus'],
                name: `SEASON_TICKET.RENEWALS.LIST.RENEWAL_SUBSTATUS_OPTS.${params['renewal_substatus']}`
            };

            if (!this.$renewalStatusList().some(s => s.id === params['renewal_substatus'])) {
                this.$renewalStatusList.set([...this.$renewalStatusList().filter(s => s.id !== params['renewal_substatus']), renewalSubstatus]);
            }

            formFields.renewal_status = this.$renewalStatusList().find(v => v.id === params['renewal_substatus']) || null;
        }
        if (params['auto_renewal']) {
            formFields.auto_renewal = this.renewalTypeList.find(v => v.value.toString() === params['auto_renewal']);
        }
        if (params['startDate']) {
            formFields.startDate = moment(params['startDate']).tz(moment().tz()).format();
        }
        if (params['endDate']) {
            formFields.endDate = moment(params['endDate']).tz(moment().tz()).format();
        }
        return applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.renewalsEntities$, 'id').pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterMappingStatus(),
            this.getFilterRenewalStatus(),
            this.getFilterRenewalType(),
            this.getFilterStartDate(),
            this.getFilterEndDate()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'MAPPING_STATUS') {
            this.filtersForm.get('mapping_status').reset();
        } else if (key === 'RENEWAL_STATUS') {
            this.filtersForm.get('renewal_status').reset();
        } else if (key === 'RENEWAL_SUBSTATUS') {
            this.filtersForm.get('renewal_status').reset();
        } else if (key === 'AUTO_RENEWAL') {
            this.filtersForm.get('auto_renewal').reset();
        } else if (key === 'START_DATE') {
            this.filtersForm.get('startDate').reset();
        } else if (key === 'END_DATE') {
            this.filtersForm.get('endDate').reset();
        } else if (key === 'ENTITY') {
            this.filtersForm.get('entity').reset();
        }
    }

    private getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('ENTITY')
            .labelKey('SEASON_TICKET.RENEWALS.LIST.ENTITY')
            .queryParam('entity')
            .value(this.filtersForm.value.entity)
            .build();
    }

    private getFilterMappingStatus(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('MAPPING_STATUS')
            .labelKey('SEASON_TICKET.RENEWALS.LIST.MAPPING_STATUS')
            .queryParam('mapping_status')
            .value(this.filtersForm.value.mapping_status)
            .translateValue()
            .build();
    }

    private getFilterRenewalStatus(): FilterItem {
        const renewalStatus = this.filtersForm.value.renewal_status;
        const isAuto = renewalStatus?.name?.includes('SUBSTATUS');

        return new FilterItemBuilder(this.#translate)
            .key(isAuto ? 'RENEWAL_SUBSTATUS' : 'RENEWAL_STATUS')
            .labelKey('SEASON_TICKET.RENEWALS.LIST.RENEWAL_STATUS')
            .queryParam(isAuto ? 'renewal_substatus' : 'renewal_status')
            .value(renewalStatus)
            .translateValue()
            .build();
    }

    private getFilterRenewalType(): FilterItem {
        const filterItem = new FilterItem('AUTO_RENEWAL', this.#translate.instant('SEASON_TICKET.RENEWALS.LIST.TYPE'));
        const value = this.filtersForm.value.auto_renewal;
        if (value) {
            filterItem.values = [new FilterItemValue(value.value, value.name)];
            filterItem.urlQueryParams['auto_renewal'] = value.value;
        }
        return filterItem;
    }

    private getFilterStartDate(): FilterItem {
        const filterItem = new FilterItem('START_DATE', this.#translate.instant('SEASON_TICKET.SESSION_DATE_FROM'));
        const value = this.filtersForm.value.startDate;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this.#formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['startDate'] = valueId;
        }
        return filterItem;
    }

    private getFilterEndDate(): FilterItem {
        const filterItem = new FilterItem('END_DATE', this.#translate.instant('SEASON_TICKET.SESSION_DATE_TO'));
        const value = this.filtersForm.value.endDate;
        if (value) {
            const valueId = moment(value).endOf('day').utc().format();
            const valueFormatted = moment(value).format(this.#formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['endDate'] = valueId;
        }
        return filterItem;
    }
}
