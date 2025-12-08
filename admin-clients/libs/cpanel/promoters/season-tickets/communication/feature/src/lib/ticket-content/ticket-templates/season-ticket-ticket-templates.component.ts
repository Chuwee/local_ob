import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketTicketTemplateFields, SeasonTicketCommunicationService,
    SeasonTicketTicketTemplate, SeasonTicketTemplateType
} from '@admin-clients/cpanel-promoters-season-tickets-communication-data-access';
import {
    TicketTemplate, TicketTemplatesService,
    GetTicketTemplatesRequest, TicketTemplateFormat
} from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { TicketPassbook, TicketsPassbookService, TicketPassbookType } from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { EntitiesBaseService, TicketType } from '@admin-clients/shared/common/data-access';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, take, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-ticket-templates',
    templateUrl: './season-ticket-ticket-templates.component.html',
    styleUrls: ['./season-ticket-ticket-templates.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketTicketTemplatesComponent implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();

    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #seasonTicketService = inject(SeasonTicketsService);
    readonly #sessionTicketCommunicationService = inject(SeasonTicketCommunicationService);
    readonly #ticketTemplatesService = inject(TicketTemplatesService);
    readonly #ticketsPassbookService = inject(TicketsPassbookService);

    private _seasonTicketId: number;
    private _entityId: number;
    private _templates$: Observable<void>;
    private _templateFields: SeasonTicketTicketTemplateFields[];
    private _selectedLanguage: string;

    templatesForm: UntypedFormGroup;
    allowGroups: boolean;

    templatesPdf: TicketTemplate[] = [];
    templatesPrinter: TicketTemplate[] = [];
    templatesPassbook: TicketPassbook[] = [];

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;
    @Input() passbookEnabled: boolean;
    @Input() ticketType = TicketType.general;

    ngOnInit(): void {
        this.prepareFields();
        this.initForms();
        this.loadContents();
        this.language$.pipe(takeUntil(this._onDestroy)).subscribe(lang => this._selectedLanguage = lang);
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
        this.#sessionTicketCommunicationService.loadSeasonTicketTicketTemplates(this._seasonTicketId);
        this.#ticketTemplatesService.loadTicketTemplates(
            { entity_id: this._entityId, limit: 999, sort: 'name:asc' } as GetTicketTemplatesRequest);
        if (this.passbookEnabled) {
            this.#ticketsPassbookService.loadTicketPassbookList({
                limit: 999, offset: 0, entity_id: this._entityId, type: TicketPassbookType.order
            });
        }
    }

    save(getTemplateFields: (contentForm: UntypedFormGroup, templateFields: SeasonTicketTicketTemplateFields[]) =>
        SeasonTicketTicketTemplate[]
    ): Observable<void | void[]>[] {
        const templatesToSave = getTemplateFields(this.templatesForm, this._templateFields);

        if (templatesToSave.length > 0) {
            return [this.#sessionTicketCommunicationService.saveSeasonTicketTicketTemplates(this._seasonTicketId, templatesToSave).pipe(
                tap(() => this.cancel()) // reloads data from backend
            )];
        }

        return [];
    }

    private prepareFields(): void {
        this._templateFields = [{
            format: TicketTemplateFormat.pdf,
            formField: 'singlePdfTemplate',
            type: this.getSingleTemplateType()
        }, {
            format: TicketTemplateFormat.printer,
            formField: 'singlePrinterTemplate',
            type: this.getSingleTemplateType()
        }, {
            format: TicketTemplateFormat.pdf,
            formField: 'groupPdfTemplate',
            type: this.getGroupTemplateType()
        }, {
            format: TicketTemplateFormat.printer,
            formField: 'groupPrinterTemplate',
            type: this.getGroupTemplateType()
        }];
        if (this.passbookEnabled) {
            this._templateFields.push({
                format: TicketTemplateFormat.passbook,
                formField: 'seasonPackPassbookTemplate',
                type: SeasonTicketTemplateType.seasonPack
            });
        }
    }

    private initForms(): void {
        const templateFields = {};

        this._templateFields.forEach(templateField => {
            templateFields[templateField.formField] = [{ value: null, disabled: true }];
        });

        this.templatesForm = this.#fb.group(templateFields);
        this.form.addControl('templatesForm', this.templatesForm);
    }

    private loadContents(): void {
        this.#seasonTicketService.seasonTicket.get$()
            .pipe(
                take(1),
                tap(seasonTicket => {
                    this._seasonTicketId = seasonTicket.id;
                    this._entityId = seasonTicket.entity.id;
                    // this.allowGroups = event.settings.groups?.allowed;
                    this.#sessionTicketCommunicationService.loadSeasonTicketTicketTemplates(seasonTicket.id);
                    this.#ticketTemplatesService.loadTicketTemplates(
                        { entity_id: this._entityId, limit: 999, sort: 'name:asc' } as GetTicketTemplatesRequest);
                    if (this.passbookEnabled) {
                        this.#ticketsPassbookService.loadTicketPassbookList({
                            limit: 999, offset: 0, entity_id: this._entityId, type: TicketPassbookType.order
                        });
                    }
                })
            ).subscribe();

        this._templates$ = combineLatest([
            this.#sessionTicketCommunicationService.getSeasonTicketTicketTemplates$(),
            this.#ticketTemplatesService.getTicketTemplates$(),
            this.#ticketsPassbookService.getTicketPassbookListData$(),
            this.language$,
            this.#entitiesSrv.getEntity$()
        ]).pipe(
            takeUntil(this._onDestroy),
            filter(([templates, templatesList]) => !!templates && !!templatesList),
            tap(([templates, templatesList, templatesPassbookList, _, entity]) => {
                this.templatesPdf = templatesList.filter(template => template.design.format === TicketTemplateFormat.pdf);
                if (this.templatesPdf.length) {
                    this.templatesForm.get('singlePdfTemplate').enable();
                    this.templatesForm.get('groupPdfTemplate').enable();
                }
                if (entity.settings.allow_hard_ticket_pdf) {
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
                        this.templatesForm.get('seasonPackPassbookTemplate').enable();
                    }
                }
                this._templateFields.forEach(templateField => {
                    const field = this.templatesForm.get(templateField.formField);
                    field.reset();
                    for (const template of templates) {
                        if (template.format === templateField.format && template.type === templateField.type) {
                            field.setValue(isNaN(+template.id) ? template.id : +template.id);
                        }
                    }
                });
            }),
            map(() => null));
    }

    private getSingleTemplateType(): SeasonTicketTemplateType {
        return this.ticketType === TicketType.general ? SeasonTicketTemplateType.single : SeasonTicketTemplateType.singleInvitation;
    }

    private getGroupTemplateType(): SeasonTicketTemplateType {
        return this.ticketType === TicketType.general ? SeasonTicketTemplateType.group : SeasonTicketTemplateType.groupInvitation;
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this._templates$,
            this.form.valueChanges
        ]).pipe(
            filter(([language]) => !!language),
            takeUntil(this._onDestroy)
        ).subscribe();
    }
}
