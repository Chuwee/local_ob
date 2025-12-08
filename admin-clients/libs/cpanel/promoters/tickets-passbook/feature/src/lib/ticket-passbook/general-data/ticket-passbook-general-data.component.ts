import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    defaultPassbookLiterals, TicketPassbook, TicketPassbookFieldsRestriction, TicketPassbookType, TicketsPassbookService
} from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    MessageDialogService, DialogSize, EphemeralMessageService,
    LanguageSelectorComponent, LanguageSelector,
    HelpButtonComponent
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { DefaultIconComponent } from '@admin-clients/shared-common-ui-default-icon';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectorRef, Component, OnInit, ViewChild, AfterViewInit, QueryList, ChangeDetectionStrategy,
    inject,
    DestroyRef
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, of } from 'rxjs';
import { filter, map, tap, catchError, take } from 'rxjs/operators';
import { TicketPassbookDownloadComponent } from '../ticket-passbook-download/ticket-passbook-download.component';

@Component({
    selector: 'app-ticket-passbook-general-data',
    templateUrl: './ticket-passbook-general-data.component.html',
    styleUrls: ['./ticket-passbook-general-data.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        DefaultIconComponent,
        TicketPassbookDownloadComponent,
        LanguageSelectorComponent,
        MatExpansionPanel, MatAccordion, MatExpansionPanelHeader,
        MatProgressSpinner, MatLabel, MatExpansionPanelTitle, MatFormField,
        MatInput, MatError,
        FormContainerComponent,
        ReactiveFormsModule,
        AsyncPipe,
        TranslatePipe,
        HelpButtonComponent,
        FormControlErrorsComponent
    ]
})
export class TicketPassbookGeneralDataComponent implements OnInit, AfterViewInit, WritingComponent {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ticketsPassbookService = inject(TicketsPassbookService);
    readonly #auth = inject(AuthenticationService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #entityService = inject(EntitiesBaseService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild('languageSelector') languageSelector: LanguageSelectorComponent;

    form: UntypedFormGroup;
    formDefaultTemplate: UntypedFormGroup;
    formPrincipalInfo: UntypedFormGroup;
    formChannelLanguages: UntypedFormGroup;
    languageSelectorData: LanguageSelector;
    ticketPassbook: TicketPassbook;
    readonly isInProgress$ = booleanOrMerge([
        this.#ticketsPassbookService.isTicketPassbookLoading$(),
        this.#ticketsPassbookService.isTicketPassbookSaving$(),
        this.#ticketsPassbookService.isTicketPassbookPreviewDownloadUrl$(),
        this.#entityService.isEntityLoading$()
    ]);

    readonly defaultPassbookLiterals = defaultPassbookLiterals;
    readonly isOrderTemplate$: Observable<boolean> = this.#ticketsPassbookService.getTicketPassbook$()
        .pipe(take(1), map(ticketPassbook => ticketPassbook.type === TicketPassbookType.order));

    updateDefault: (ticketPassBookId: { code: string; entityId: number }, isDefault: boolean) =>
        Observable<boolean> = (ticketPassBookId, isDefault) =>
            this.#ticketsPassbookService.updateTicketPassbookDefault(ticketPassBookId, isDefault).pipe(tap(() =>
                this.#ticketsPassbookService.loadTicketPassbook(this.ticketPassbook.code, this.ticketPassbook.entity_id.toString())));

    ngOnInit(): void {
        this.initForms();
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
        this.formChangeHandler();
    }

    cancel(): void {
        this.#ticketsPassbookService.loadTicketPassbook(this.ticketPassbook.code, this.ticketPassbook.entity_id.toString());
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<boolean> {
        if (this.form.valid) {
            const ticketPassbook: TicketPassbook = {
                ...this.ticketPassbook,
                ...this.formPrincipalInfo.value,
                ...this.formDefaultTemplate.value,
                languages: {
                    selected: this.languageSelector.getSelectedLanguages(),
                    default: this.languageSelector.getDefaultLanguage()
                }
            };
            const saveAction$ = this.#ticketsPassbookService.updateTicketPassbook(ticketPassbook, ticketPassbook.entity_id.toString())
                .pipe(
                    map(() => {
                        this.#ticketsPassbookService.loadTicketPassbook(this.ticketPassbook.code, this.ticketPassbook.entity_id.toString());
                        this.#ephemeralMessageService.showSaveSuccess();
                        return true;
                    }),
                    catchError(() => of(false))
                );
            return saveAction$;
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this.#matExpansionPanelQueryList);
            return of(false);
        }
    }

    openDefaultModal(): void {
        if (this.formDefaultTemplate.dirty && this.formDefaultTemplate.get('default_passbook').value) {
            this.#messageDialogService.showWarn({
                size: DialogSize.MEDIUM,
                title: 'TICKET_PASSBOOK.DEFAULT_TEMPLATE_DISMISS_TITLE',
                message: 'TICKET_PASSBOOK.DEFAULT_TEMPLATE_DISMISS_MESSAGE',
                actionLabel: 'FORMS.ACTIONS.AGREED',
                showCancelButton: true
            }).subscribe(response => response ? this.save() : null);
        } else {
            this.save();
        }
    }

    private initForms(): void {
        this.formDefaultTemplate = this.#fb.group({
            default_passbook: null
        });
        this.formPrincipalInfo = this.#fb.group({
            name: [null, [
                Validators.required,
                Validators.maxLength(TicketPassbookFieldsRestriction.ticketPassbookNameLength)
            ]],
            description: [null, [
                Validators.maxLength(TicketPassbookFieldsRestriction.ticketPassbookDescriptionLength)
            ]]
        });
        this.form = this.#fb.group({
            formPrincipalInfo: this.formPrincipalInfo,
            formDefaultTemplate: this.formDefaultTemplate
        });
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.#ticketsPassbookService.getTicketPassbook$(),
            this.#entityService.getEntity$().pipe(
                filter(entity => !!(entity?.settings?.languages?.available?.length)),
                map(entity => entity.settings?.languages.available)),
            this.#auth.getLoggedUser$()
        ]).pipe(
            filter(([ticketPassbook, entityAvailableLanguages, user]) => !!ticketPassbook && !!entityAvailableLanguages && !!user),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([ticketPassbook, entityAvailableLanguages, user]) => {
            const isOperator = AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR]);
            const isEventManager = AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.EVN_MGR]);
            const canWrite = isOperator || isEventManager;
            this.ticketPassbook = ticketPassbook;

            this.formPrincipalInfo.get('name').setValue(ticketPassbook.name);
            this.formPrincipalInfo.get('description').setValue(ticketPassbook.description);
            this.formDefaultTemplate.get('default_passbook').setValue(ticketPassbook.default_passbook);
            if (ticketPassbook.default_passbook) { this.formDefaultTemplate.disable(); }

            this.languageSelectorData = {
                default: ticketPassbook.languages.default,
                selected: ticketPassbook.languages.selected,
                languages: entityAvailableLanguages
            };
            this.form.addControl('formLanguages', this.languageSelector.form);
            this.formChannelLanguages = this.form.get('formLanguages') as UntypedFormGroup;
            this.#ref.detectChanges();
            if (!canWrite) {
                this.form.disable({ onlySelf: false, emitEvent: true });
            }
        });
    }

    private formChangeHandler(): void {
        combineLatest([
            this.#ticketsPassbookService.getTicketPassbook$(),
            this.form.valueChanges // only used as a trigger
        ])
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([ticketPassbook]) => {
                FormControlHandler.checkAndRefreshDirtyState(this.formPrincipalInfo.get('name'), ticketPassbook.name);
                FormControlHandler.checkAndRefreshDirtyState(this.formPrincipalInfo.get('description'), ticketPassbook.description);
                FormControlHandler.checkAndRefreshDirtyState(
                    this.formDefaultTemplate.get('default_passbook'),
                    ticketPassbook.default_passbook
                );
            });
    }
}
