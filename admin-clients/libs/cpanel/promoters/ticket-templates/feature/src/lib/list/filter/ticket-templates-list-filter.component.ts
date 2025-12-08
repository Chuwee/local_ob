/* eslint-disable @typescript-eslint/dot-notation */
import {
    TicketTemplateDesign, TicketTemplateFormat,
    TicketTemplatesService
} from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { EntitiesBaseService, Entity, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { FilterItemBuilder, FilterWrapped, FilterItem, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { NgIf, NgFor, AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, Input, OnInit } from '@angular/core';
import { FlexModule } from '@angular/flex-layout/flex';
import { UntypedFormBuilder, UntypedFormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDateFormats, MAT_DATE_FORMATS } from '@angular/material/core';
import { Params } from '@angular/router';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { forkJoin, Observable, of } from 'rxjs';
import { filter, map, shareReplay, switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-ticket-templates-list-filter',
    templateUrl: './ticket-templates-list-filter.component.html',
    styleUrls: ['./ticket-templates-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, FlexModule, NgIf,
        SelectSearchComponent, NgFor, EllipsifyDirective,
        MaterialModule, AsyncPipe, TranslatePipe
    ]
})
export class TicketTemplatesListFilterComponent extends FilterWrapped implements OnInit {
    private readonly _formStructure = {
        entity: null,
        format: null,
        venue: null,
        design: null,
        printer: null,
        paper_type: null
    };

    readonly dateFormat = moment.localeData().longDateFormat(this.formats.display.dateInput).toLowerCase();
    entities$: Observable<Entity[]>;
    designs$: Observable<TicketTemplateDesign[]>;
    printers$: Observable<{ id: string; name: string }[]>;
    paperTypes$: Observable<{ id: string; name: string }[]>;
    ticketTemplateformats = [TicketTemplateFormat.pdf, TicketTemplateFormat.printer]
        .map(format => ({ id: format, name: `TICKET_TEMPLATE.FORMAT_OPTS.${format}` }));

    filtersForm: UntypedFormGroup;
    @Input() canSelectEntity$: Observable<boolean>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _entitiesService: EntitiesBaseService,
        private _ticketTemplatesService: TicketTemplatesService,
        private _translate: TranslateService,
        @Inject(MAT_DATE_FORMATS) private readonly formats: MatDateFormats) {
        super();
    }

    ngOnInit(): void {
        // Init reactive form:
        this.filtersForm = this._fb.group(Object.assign({}, this._formStructure));
        // Make HTTP calls:
        this._ticketTemplatesService.loadDesigns();
        this._ticketTemplatesService.loadPrinters();
        this._ticketTemplatesService.loadPaperTypes();
        // Map data observables to component:
        this.entities$ = this.canSelectEntity$.pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    this._entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name]
                    });
                    return this._entitiesService.entityList.getData$();
                }
                return of([]);
            }),
            shareReplay(1));

        this.designs$ = this._ticketTemplatesService.getDesignsList$();
        this.printers$ = this._ticketTemplatesService.getPrintersList$()
            .pipe(
                filter(printers => !!printers),
                map(printers => printers.map(printer => ({ id: printer, name: printer }))),
                shareReplay(1)
            );
        this.paperTypes$ = this._ticketTemplatesService.getPaperTypesList$()
            .pipe(
                filter(papertypes => !!papertypes),
                map(papertypes => papertypes.map(type => ({ id: type, name: type }))),
                shareReplay(1)
            );
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterFormat(),
            this.getFilterDesign(),
            this.getFilterPaperType(),
            this.getFilterPrinter()
        ];
    }

    removeFilter(key: string, _: unknown): void {
        if (this.filtersForm.get([key.toLowerCase()])) {
            this.filtersForm.get([key.toLowerCase()]).reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this._formStructure);

        if (params['format']) {
            formFields.format = this.ticketTemplateformats.find(elem => elem.id === params['format']);
        }
        const asyncFields = [
            applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.entities$, 'id'),
            applyAsyncFieldValue$(formFields, 'design', params['design'], this.designs$, 'id'),
            applyAsyncFieldValue$(formFields, 'printer', params['printer'], this.printers$, 'id'),
            applyAsyncFieldValue$(formFields, 'paper_type', params['paper_type'], this.paperTypes$, 'id')
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
            .labelKey('FORMS.LABELS.ENTITY')
            .queryParam('entity')
            .value(this.filtersForm.value.entity)
            .build();
    }

    private getFilterPrinter(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('PRINTER')
            .labelKey('TICKET_TEMPLATE.PRINTER')
            .queryParam('printer')
            .value(this.filtersForm.value.printer)
            .build();
    }

    private getFilterDesign(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('DESIGN')
            .labelKey('TICKET_TEMPLATE.DESIGN')
            .queryParam('design')
            .value(this.filtersForm.value.design)
            .build();
    }

    private getFilterPaperType(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('PAPER_TYPE')
            .labelKey('TICKET_TEMPLATE.PAPER_TYPE')
            .queryParam('paper_type')
            .value(this.filtersForm.value.paper_type)
            .build();
    }

    private getFilterFormat(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('FORMAT')
            .labelKey('TICKET_TEMPLATE.FORMAT')
            .queryParam('format')
            .value(this.filtersForm.value.format)
            .translateValue()
            .build();
    }

}
