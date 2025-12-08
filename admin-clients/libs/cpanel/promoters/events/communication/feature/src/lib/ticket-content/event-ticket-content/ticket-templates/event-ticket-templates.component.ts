import {
    EventCommunicationService, EventTicketTemplate, EventTicketTemplateFields, EventTicketTemplateType, TicketContentFormat
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    GetTicketTemplatesRequest, TicketTemplate, TicketTemplateFormat, TicketTemplatesService
} from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { TicketPassbook, TicketsPassbookService, TicketPassbookType } from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { EntitiesBaseService, TicketType } from '@admin-clients/shared/common/data-access';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, take, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-event-ticket-templates',
    templateUrl: './event-ticket-templates.component.html',
    styleUrls: ['./event-ticket-templates.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventTicketTemplatesComponent
    implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();

    private _eventId: number;
    private _entityId: number;
    private _templates$: Observable<void>;
    private _templateFields: EventTicketTemplateFields[];
    private _selectedLanguage: string;
    private readonly _entitiesSrv = inject(EntitiesBaseService);

    allowGroups: boolean;
    templatesForm: UntypedFormGroup;

    templatesPdf: TicketTemplate[] = [];
    templatesPrinter: TicketTemplate[] = [];
    templatesPassbook: TicketPassbook[] = [];

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;
    @Input() passbookEnabled: boolean;
    @Input() seasonPackEnabled: boolean;
    @Input() ticketType = TicketType.general;

    constructor(
        private _fb: UntypedFormBuilder,
        private _eventService: EventsService,
        private _eventCommunicationService: EventCommunicationService,
        private _ticketTemplatesService: TicketTemplatesService,
        private _ticketsPassbookService: TicketsPassbookService
    ) { }

    ngOnInit(): void {
        this.prepareFields();
        this.initForms();
        this.loadContents();
        this.language$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(lang => (this._selectedLanguage = lang));
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cancel(): void {
        this.templatesForm.markAsPristine();
        this._eventCommunicationService.loadEventTicketTemplates(this._eventId);
        this._ticketTemplatesService.loadTicketTemplates(
            { entity_id: this._entityId, limit: 999, sort: 'name:asc' } as GetTicketTemplatesRequest);
        if (this.passbookEnabled) {
            this._ticketsPassbookService.loadTicketPassbookList({
                limit: 999, offset: 0, entity_id: this._entityId, type: TicketPassbookType.order
            });
        }
    }

    save(
        getTemplateFields: (contentForm: UntypedFormGroup, templateFields: EventTicketTemplateFields[]) => EventTicketTemplate[]
    ): Observable<void | void[]>[] {
        const templatesToSave = getTemplateFields(this.templatesForm, this._templateFields);
        if (templatesToSave.length > 0) {
            return [
                this._eventCommunicationService.saveEventTicketTemplates(this._eventId, templatesToSave)
                    .pipe(
                        tap(() => this.cancel()) // reloads data from backend
                    )
            ];
        }

        return [];
    }

    private prepareFields(): void {
        this._templateFields = [
            {
                format: TicketContentFormat.pdf,
                formField: 'singlePdfTemplate',
                type: this.getSingleTemplateType()
            },
            {
                format: TicketContentFormat.printer,
                formField: 'singlePrinterTemplate',
                type: this.getSingleTemplateType()
            },
            {
                format: TicketContentFormat.pdf,
                formField: 'groupPdfTemplate',
                type: this.getGroupTemplateType()
            },
            {
                format: TicketContentFormat.printer,
                formField: 'groupPrinterTemplate',
                type: this.getGroupTemplateType()
            }
        ];

        if (this.passbookEnabled) {
            this._templateFields.push({
                format: TicketContentFormat.passbook,
                formField: 'singlePassbookTemplate',
                type: this.getSingleTemplateType()
            });
            this._templateFields.push({
                format: TicketContentFormat.passbook,
                formField: 'groupPassbookTemplate',
                type: this.getGroupTemplateType()
            });
            if (this.seasonPackEnabled) {
                this._templateFields.push({
                    format: TicketContentFormat.passbook,
                    formField: 'passbookSeasonPackTemplate',
                    type: EventTicketTemplateType.seasonPack
                });
            }
        }
    }

    private initForms(): void {
        const templateFields = {};

        this._templateFields.forEach(templateField => {
            templateFields[templateField.formField] = [{ value: null, disabled: true }];
        });

        this.templatesForm = this._fb.group(templateFields);
        this.form.addControl('templatesForm', this.templatesForm);
    }

    private loadContents(): void {
        this._eventService.event.get$()
            .pipe(
                take(1),
                tap(event => {
                    this._eventId = event.id;
                    this._entityId = event.entity.id;
                    this.allowGroups = event.settings.groups?.allowed;
                    this._eventCommunicationService.loadEventTicketTemplates(event.id);
                    this._ticketTemplatesService.loadTicketTemplates({
                        entity_id: this._entityId,
                        limit: 999,
                        sort: 'name:asc'
                    } as GetTicketTemplatesRequest);
                    if (this.passbookEnabled) {
                        this._ticketsPassbookService.loadTicketPassbookList({
                            limit: 999,
                            offset: 0,
                            entity_id: this._entityId,
                            type: TicketPassbookType.order
                        });
                    }
                })
            ).subscribe();

        this._templates$ = combineLatest([
            this._eventCommunicationService.getEventTicketTemplates$(),
            this._ticketTemplatesService.getTicketTemplates$(),
            this._ticketsPassbookService.getTicketPassbookListData$(),
            this.language$,
            this._entitiesSrv.getEntity$()
        ]).pipe(
            takeUntil(this._onDestroy),
            filter(([templates, templatesList]) => !!templates && !!templatesList),
            tap(([templates, templatesList, templatesPassbookList, _, entity]) => {

                this.templatesPdf = templatesList.filter(template => template.design.format === TicketTemplateFormat.pdf);
                if (this.templatesPdf.length) {
                    this.templatesForm.get('singlePdfTemplate').enable();
                    this.templatesForm.get('groupPdfTemplate').enable();
                }
                if (entity?.settings?.allow_hard_ticket_pdf) {
                    this.templatesPrinter = templatesList.filter(template =>
                        template.design.format === TicketTemplateFormat.printer ||
                        template.design.format === TicketTemplateFormat.hardTicketPdf
                    );
                } else {
                    this.templatesPrinter = templatesList.filter(template => template.design.format === TicketTemplateFormat.printer);
                }
                if (this.templatesPrinter.length) {
                    this.templatesForm.get('singlePrinterTemplate').enable();
                    this.templatesForm.get('groupPrinterTemplate').enable();
                }
                if (this.passbookEnabled && templatesPassbookList) {
                    this.templatesPassbook = templatesPassbookList;
                    if (this.templatesPassbook.length) {
                        this.templatesForm.get('singlePassbookTemplate').enable();
                        this.templatesForm.get('groupPassbookTemplate').enable();
                        if (this.seasonPackEnabled) {
                            this.templatesForm.get('passbookSeasonPackTemplate').enable();
                        }
                    }
                }

                this._templateFields.forEach(templateField => {
                    const field = this.templatesForm.get(templateField.formField);
                    field.reset();
                    for (const template of templates) {
                        if ((template.format === templateField.format ||
                            template.format === TicketContentFormat.hardTicketPdf && templateField.format === TicketContentFormat.printer)
                            && template.type === templateField.type
                        ) {
                            if (template.format === TicketContentFormat.passbook) {
                                field.setValue(template.id);
                            } else {
                                field.setValue(+template.id);
                            }
                        }
                    }
                });
            }),
            map(() => null)
        );
    }

    private getSingleTemplateType(): EventTicketTemplateType {
        return this.ticketType === TicketType.general
            ? EventTicketTemplateType.single
            : EventTicketTemplateType.singleInvitation;
    }

    private getGroupTemplateType(): EventTicketTemplateType {
        return this.ticketType === TicketType.general
            ? EventTicketTemplateType.group
            : EventTicketTemplateType.groupInvitation;
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this._templates$,
            this.form.valueChanges
        ])
            .pipe(
                filter(([language]) => !!language),
                takeUntil(this._onDestroy)
            )
            .subscribe();
    }
}
