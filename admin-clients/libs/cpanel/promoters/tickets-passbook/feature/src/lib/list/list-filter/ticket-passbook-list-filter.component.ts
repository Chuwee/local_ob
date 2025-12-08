import { TicketPassbookType } from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { Entity } from '@admin-clients/shared/common/data-access';
import { FilterItemBuilder, FilterWrapped, FilterItem, FilterItemValue } from '@admin-clients/shared/common/ui/components';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, Input, OnInit } from '@angular/core';
import { UntypedFormGroup, UntypedFormBuilder } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { Params } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { forkJoin, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    selector: 'app-ticket-passbook-list-filter',
    templateUrl: './ticket-passbook-list-filter.component.html',
    styleUrls: ['./ticket-passbook-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TicketPassbookListFilterComponent extends FilterWrapped implements OnInit {
    private readonly _formats = inject(MAT_DATE_FORMATS);
    private readonly _formStructure = {
        entity: null,
        startDate: null,
        endDate: null,
        type: null
    };

    readonly dateFormat = moment.localeData().longDateFormat(this._formats.display.dateInput).toLowerCase();
    filtersForm: UntypedFormGroup;
    @Input() canSelectEntity$: Observable<boolean>;
    @Input() entities$: Observable<Entity[]>;
    @Input() isAvetEntityDigitalSeasonTicket$: Observable<boolean>;
    types = Object.values(TicketPassbookType)
        .map(type => ({ id: type, name: `TICKET_PASSBOOK.TYPES.${type}.TITLE` }));

    constructor(
        private _fb: UntypedFormBuilder,
        private _translate: TranslateService
    ) {
        super();
    }

    ngOnInit(): void {
        // Init reactive form:
        this.filtersForm = this._fb.group(Object.assign({}, this._formStructure));
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterStartDate(),
            this.getFilterEndDate(),
            this.getFilterType()
        ];
    }

    removeFilter(key: string): void {
        if (key === 'ENTITY') {
            this.filtersForm.get('entity').reset();
        } else if (key === 'START_DATE') {
            this.filtersForm.get('startDate').reset();
        } else if (key === 'END_DATE') {
            this.filtersForm.get('endDate').reset();
        } else if (key === 'TYPE') {
            this.filtersForm.get('type').reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this._formStructure);

        if (params['startDate']) {
            formFields.startDate = moment(params['startDate']).tz(moment().tz()).format();
        }
        if (params['endDate']) {
            formFields.endDate = moment(params['endDate']).tz(moment().tz()).format();
        }
        if (params['type']) {
            formFields.type = this.types.find(typeObj => typeObj.id === params['type']);
        }
        const asyncFields = [
            applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.entities$, 'id')
        ];
        return forkJoin(asyncFields).pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    private getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('ENTITY')
            .labelKey('TICKET_PASSBOOK.ENTITY')
            .queryParam('entity')
            .value(this.filtersForm.value.entity)
            .build();
    }

    private getFilterStartDate(): FilterItem {
        const filterItem = new FilterItem('START_DATE', this._translate.instant('TICKET_PASSBOOK.DATE_FROM'));
        const value = this.filtersForm.value.startDate;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this._formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['startDate'] = valueId;
        }
        return filterItem;
    }

    private getFilterEndDate(): FilterItem {
        const filterItem = new FilterItem('END_DATE', this._translate.instant('TICKET_PASSBOOK.DATE_TO'));
        const value = this.filtersForm.value.endDate;
        if (value) {
            const valueId = moment(value).endOf('day').utc().format();
            const valueFormatted = moment(value).format(this._formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['endDate'] = valueId;
        }
        return filterItem;
    }

    private getFilterType(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('TYPE')
            .labelKey('TICKET_PASSBOOK.TYPES.TITLE')
            .queryParam('type')
            .value(this.filtersForm.value.type)
            .translateValue()
            .build();
    }
}
