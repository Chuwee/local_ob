import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    TicketsPassbookService, TicketPassbook, TicketPassbookLiterals, LiteralsTextArea
} from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import {
    MessageDialogService, EphemeralMessageService,
    UnsavedChangesDialogResult, LanguageBarComponent
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, UntypedFormArray, UntypedFormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatInput } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, defer, iif, Observable, of, throwError } from 'rxjs';
import { filter, map, take, tap, switchMap, catchError } from 'rxjs/operators';
import { TicketPassbookDownloadComponent } from '../ticket-passbook-download/ticket-passbook-download.component';

@Component({
    selector: 'app-ticket-passbook-literals',
    templateUrl: './ticket-passbook-literals.component.html',
    styleUrls: ['./ticket-passbook-literals.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TicketPassbookDownloadComponent,
        MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle,
        MatInput, MatFormField, MatAccordion,
        MatProgressSpinnerModule, MatTableModule,
        FormContainerComponent,
        LanguageBarComponent,
        ReactiveFormsModule,
        AsyncPipe,
        TranslatePipe
    ]
})
export class TicketPassbookLiteralsComponent implements OnInit, AfterViewInit {
    readonly #ticketsPassbookService = inject(TicketsPassbookService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #auth = inject(AuthenticationService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #onDestroyRef = inject(DestroyRef);
    #language = new BehaviorSubject<string>(null);
    #ticketPassbook: TicketPassbook;

    form: UntypedFormGroup;
    formLiteralsTable: UntypedFormArray;
    formTermConditions: UntypedFormGroup;
    readonly isInProgress$: Observable<boolean> = booleanOrMerge([
        this.#ticketsPassbookService.isTicketPassbookLoading$(),
        this.#ticketsPassbookService.isTicketPassbookTemplateLiteralsInProgress$(),
        this.#ticketsPassbookService.isTicketPassbookPreviewDownloadUrl$(),
        this.#ticketsPassbookService.isTicketPassbookTemplateLiteralsSaving$()
    ]);

    readonly languageList$: Observable<string[]> = this.#ticketsPassbookService.getTicketPassbook$()
        .pipe(
            take(1),
            tap(ticketPassbook => {
                this.#ticketPassbook = ticketPassbook;
            }),
            map(ticketPassbook => ticketPassbook.languages),
            tap(languages => {
                this.loadTicketPassbookLiterals(languages.default);
                this.#language.next(languages.default);
            }),
            map(languages => languages.selected)
        );

    readonly tableHead = ['key', 'title'];
    readonly language$ = this.#language.asObservable();
    literalsTable: TicketPassbookLiterals[];
    readonly literalTextAreaKey = LiteralsTextArea.literalTextArea;

    ngOnInit(): void {
        this.initForms();
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
        this.formChangeHandler();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.language$.pipe(take(1)).subscribe(langCode => {
                this.loadTicketPassbookLiterals(langCode);
            });

            this.form.markAsPristine();
        }
        );
    }

    save$(): Observable<unknown> {
        if (this.form.valid) {
            const ticketPassbookLiterals = {
                ...this.formTermConditions.value,
                ...this.formLiteralsTable.value
                    .reduce((obj, { key, title }) => Object.assign(obj, { [key]: title }), {}) as TicketPassbookLiterals[]
            };
            return this.language$
                .pipe(
                    take(1),
                    switchMap(langCode => this.#ticketsPassbookService
                        .updateTicketPassbookTemplateLiterals(
                            ticketPassbookLiterals, this.#ticketPassbook.code, langCode, this.#ticketPassbook.entity_id.toString())
                        .pipe(
                            tap(() => {
                                this.#ephemeralMessageService.showSaveSuccess();
                            }))

                    )
                );

        } else {
            return throwError(() => new Error('Form invalid'));
        }
    }

    cancel(): void {
        this.#ticketsPassbookService.clearTicketPassbookTemplateLiterals();
        this.language$.pipe(take(1)).subscribe(langCode => this.loadTicketPassbookLiterals(langCode));
        this.form.markAsPristine();
    }

    changeLanguage(newLanguage: string): void {
        this.loadTicketPassbookLiterals(newLanguage);
        this.#language.next(newLanguage);
    }

    canChangeLanguage: (() => Observable<boolean>) = () => iif(
        () => this.form.dirty,
        defer(() => this.#messageDialogService.openRichUnsavedChangesWarn()
            .pipe(
                switchMap(res => {
                    if (res === UnsavedChangesDialogResult.cancel) {
                        return of(false);
                    } else if (res === UnsavedChangesDialogResult.continue) {
                        return of(true);
                    } else {
                        return this.save$().pipe(
                            switchMap(() => of(true)),
                            catchError(() => of(false))
                        );
                    }
                })
            )),
        of(true)
    );

    private loadTicketPassbookLiterals(langCode: string): void {
        this.#ticketsPassbookService.clearTicketPassbookTemplateLiterals();
        this.#ticketsPassbookService.loadTicketPassbookTemplateLiterals(
            this.#ticketPassbook.code, langCode, this.#ticketPassbook.entity_id.toString());
    }

    private initForms(): void {
        this.formTermConditions = this.#fb.group({
            [this.literalTextAreaKey]: null
        });
        this.formLiteralsTable = this.#fb.array([]);
        this.form = this.#fb.group({
            formLiteralsTable: this.formLiteralsTable,
            formTermConditions: this.formTermConditions
        });
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.#ticketsPassbookService.getTicketPassbookTemplateLiterals$(),
            this.#auth.getLoggedUser$()
        ]).pipe(
            filter(([ticketPassbookLiterals, user]) => !!ticketPassbookLiterals && !!user),
            takeUntilDestroyed(this.#onDestroyRef)
        ).subscribe(([ticketPassbookLiterals, user]) => {
            this.form.reset();
            this.formLiteralsTable.clear();
            const isOperator = AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR]);
            const isEventManager = AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.EVN_MGR]);
            const canWrite = isOperator || isEventManager;

            Object.entries(ticketPassbookLiterals).forEach(values => {
                if (values[0] !== this.literalTextAreaKey) {
                    this.formLiteralsTable.push(this.#fb.group({ key: values[0], title: values[1] }));
                } else {
                    this.formTermConditions.patchValue({
                        [this.literalTextAreaKey]: values[1]
                    });
                }
            });
            this.literalsTable = this.formLiteralsTable.value;

            if (!canWrite) {
                this.form.disable({ onlySelf: false, emitEvent: true });
            }
        });
    }

    private formChangeHandler(): void {
        combineLatest([
            this.#ticketsPassbookService.getTicketPassbookTemplateLiterals$(),
            this.form.valueChanges // only used as a trigger
        ]).pipe(
            takeUntilDestroyed(this.#onDestroyRef),
            filter(([ticketPassbookLiterals]) => !!ticketPassbookLiterals))
            .subscribe(([ticketPassbookLiterals]) => {
                Object.entries(ticketPassbookLiterals).filter(value => value[0] !== this.literalTextAreaKey).forEach((values, i) =>
                    FormControlHandler.checkAndRefreshDirtyState(this.formLiteralsTable.at(i).get('title'), values[1]));
                Object.entries(ticketPassbookLiterals).filter(value => value[0] === this.literalTextAreaKey).forEach(values =>
                    FormControlHandler.checkAndRefreshDirtyState(this.formTermConditions.get(values[0]), values[1]));
            });
    }

}
