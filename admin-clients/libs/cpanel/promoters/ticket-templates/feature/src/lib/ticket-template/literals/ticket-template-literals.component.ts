import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { TicketTemplate, TicketTemplatesService, TicketTemplateText } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import {
    MessageDialogService, EphemeralMessageService,
    LanguageBarComponent, RichTextAreaComponent,
    UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { NgIf, NgFor, AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexModule } from '@angular/flex-layout/flex';
import { UntypedFormControl, UntypedFormGroup, ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable, of, throwError } from 'rxjs';
import { catchError, filter, first, map, switchMap, take, tap } from 'rxjs/operators';

const writePermissionRoles = [UserRoles.OPR_MGR, UserRoles.EVN_MGR];

@Component({
    selector: 'app-ticket-template-literals',
    templateUrl: './ticket-template-literals.component.html',
    styleUrls: ['./ticket-template-literals.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, LanguageBarComponent, ReactiveFormsModule,
        MaterialModule, NgIf, NgFor, FlexModule, RichTextAreaComponent, AsyncPipe, TranslatePipe
    ]
})
export class TicketTemplateLiteralsComponent implements AfterViewInit {
    readonly #fb = inject(FormBuilder);
    readonly #ticketsTemplateService = inject(TicketTemplatesService);
    readonly #auth = inject(AuthenticationService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #currentLang = new BehaviorSubject<string>(null);

    #ticketTemplate: TicketTemplate;

    readonly form = this.#fb.group({
        texts: this.#fb.record<string>({}),
        literals: this.#fb.record<string>({})
    });

    readonly isInProgress$ = booleanOrMerge([
        this.#ticketsTemplateService.isTicketTemplateInProgress$(),
        this.#ticketsTemplateService.isTicketTemplateTextsInProgress$(),
        this.#ticketsTemplateService.isTicketTemplateLiteralsInProgress$()
    ]);

    readonly languageList$ = this.#ticketsTemplateService.getTicketTemplate$()
        .pipe(
            take(1),
            tap(ticketTemplate => {
                this.#ticketTemplate = ticketTemplate;
                const { languages } = ticketTemplate;
                this.loadTicketTemplateContents(languages.default);
                this.#currentLang.next(languages.default);
            }),
            map(({ languages }) => languages.selected)
        );

    tableHead = ['key', 'value'];
    currentLang$ = this.#currentLang.asObservable();
    textsKeys = ['TERMS_AND_CONDITIONS'];
    literalsKeys: string[];

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    canChangeLanguage = (): Observable<boolean> => {
        if (this.form.dirty) {
            return this.#messageDialogService.openRichUnsavedChangesWarn().pipe(
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
                }));
        }
        return of(true);
    };

    save(): Observable<boolean> | void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const texts: TicketTemplateText[] = Object.entries(this.form.value.texts)
                ?.map(([type, value]: [string, string]) => ({ type, value, language: this.#currentLang.getValue() }));
            const literals: TicketTemplateText[] = Object.entries(this.form.value.literals)
                ?.map(([type, value]: [string, string]) => ({ type, value, language: this.#currentLang.getValue() }));

            const obs$: Observable<TicketTemplateText[]>[] = [];
            if (texts?.length) {
                obs$.push(this.#ticketsTemplateService.updateTicketTemplateTexts(texts, this.#ticketTemplate.id));
            }
            if (literals?.length) {
                obs$.push(this.#ticketsTemplateService.updateTicketTemplateLiterals(literals, this.#ticketTemplate.id));
            }
            return combineLatest(obs$)
                .pipe(
                    map(() => {
                        this.loadTicketTemplateContents(this.#currentLang.getValue());
                        this.#ephemeralMessageService.showSaveSuccess();

                    })
                );
        }
        return throwError(() => 'invalid form');
    }

    cancel(): void {
        this.#ticketsTemplateService.clearTicketTemplateTexts();
        this.loadTicketTemplateContents(this.#currentLang.getValue());
    }

    changeLanguage(newLanguage: string): void {
        this.loadTicketTemplateContents(newLanguage);
        this.#currentLang.next(newLanguage);
    }

    private loadTicketTemplateContents(langCode: string): void {
        this.#ticketsTemplateService.clearTicketTemplateTexts();
        this.#ticketsTemplateService.clearTicketTemplateLiterals();
        this.#ticketsTemplateService.loadTicketTemplateTexts(this.#ticketTemplate.id, langCode);
        this.#ticketsTemplateService.loadTicketTemplateLiterals(this.#ticketTemplate.id, langCode);
    }

    private refreshFormDataHandler(): void {
        this.#ticketsTemplateService.getTicketTemplateTexts$()
            .pipe(
                filter(texts => !!texts),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(texts => {
                const textsGroup = this.form.get('texts') as UntypedFormGroup;
                this.textsKeys.forEach(type => {
                    const value = texts.find(t => t.type === type)?.value ?? '';
                    const textCtrl = textsGroup.get(type) as UntypedFormControl;
                    if (textCtrl) {
                        textCtrl.setValue(value);
                    } else {
                        textsGroup.setControl(type, this.#fb.control(value));
                    }
                });
                textsGroup.markAsPristine();
            });
        this.#ticketsTemplateService.getTicketTemplateLiterals$()
            .pipe(
                filter(literals => !!literals),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(literals => {
                this.literalsKeys = literals.map(({ type }) => type);
                const literalsGroup = this.form.get('literals') as UntypedFormGroup;
                literals.forEach(({ type, value }) => {
                    const literalCtrl = literalsGroup.get(type) as UntypedFormControl;
                    if (literalCtrl) {
                        literalCtrl.setValue(value);
                    } else {
                        literalsGroup.setControl(type, this.#fb.control(value));
                    }
                });
                literalsGroup.markAsPristine();
            });
        this.#auth.hasLoggedUserSomeRoles$(writePermissionRoles)
            .pipe(first(canWrite => canWrite !== null))
            .subscribe(canWrite => !canWrite && this.form.disable({ onlySelf: false, emitEvent: true }));
    }
}
