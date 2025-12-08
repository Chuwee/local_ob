import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EntitiesService, EntityContent } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    EphemeralMessageService, LanguageBarComponent, MessageDialogService, RichTextAreaComponent
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, filter, first, map, Observable, of, shareReplay, switchMap, tap, throwError } from 'rxjs';

const CONTENTS_MAPPING = {
    body: 1,
    footer: 2
};

@Component({
    selector: 'app-entity-email-content-change-password',
    templateUrl: './entity-email-content-change-password.component.html',
    styleUrls: ['./entity-email-content-change-password.component.scss'],
    imports: [
        LanguageBarComponent, AsyncPipe, FormContainerComponent, ReactiveFormsModule, TranslatePipe, MatFormField, MatInput, MatLabel,
        MatProgressSpinner, RichTextAreaComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityEmailContentChangePasswordComponent implements OnInit {
    readonly #entitiesService = inject(EntitiesService);
    readonly #fb = inject(FormBuilder);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #ephemeralSrv = inject(EphemeralMessageService);

    readonly #CONTENTS_CATEGORY = 'email';
    readonly #selectedLanguage = new BehaviorSubject<string>(null);
    #entityId: number;

    readonly $isInProgress = toSignal(this.#entitiesService.entityContents.inProgress$());
    readonly selectedLanguage$ = this.#selectedLanguage.asObservable();
    readonly languages$ = this.#entitiesService.getEntity$()
        .pipe(
            first(Boolean),
            tap(entity => this.#entityId = entity.id),
            map(entity => entity.settings?.languages?.available),
            filter(Boolean),
            tap(languages => this.#selectedLanguage.next(languages[0])),
            shareReplay(1)
        );

    readonly form = this.#fb.group({
        subject: [null as string, [Validators.required]],
        body: null as string,
        footer: null as string
    });

    ngOnInit(): void {
        this.selectedLanguage$
            .pipe(
                filter(Boolean),
                switchMap(lang => {
                    this.loadContents(lang);
                    return this.#entitiesService.entityContents.get$();
                }),
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(contents => {
                const body = contents.find(content => content.id === CONTENTS_MAPPING.body);
                const footer = contents.find(content => content.id === CONTENTS_MAPPING.footer);

                this.form.reset({
                    subject: body?.subject,
                    body: body?.value,
                    footer: footer?.value
                }, { emitEvent: false });
            });
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    changeLanguage(newLanguage: string): void {
        this.#selectedLanguage.next(newLanguage);
    }

    save(): void {
        this.save$().subscribe(() => {
            this.loadContents(this.#selectedLanguage.getValue());
        });
    }

    cancel(): void {
        this.loadContents(this.#selectedLanguage.getValue());
    }

    loadContents(lang: string): void {
        this.#entitiesService.entityContents.load(this.#entityId, this.#CONTENTS_CATEGORY, lang);
        this.form.markAsPristine();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const currentLang = this.#selectedLanguage.getValue();
            const formValues = this.form.value;
            const body: EntityContent = {
                id: CONTENTS_MAPPING.body,
                language: currentLang,
                subject: formValues.subject,
                value: formValues.body
            };
            const footer: EntityContent = {
                id: CONTENTS_MAPPING.footer,
                language: currentLang,
                value: formValues.footer
            };
            const data = [body, footer];

            return this.#entitiesService.entityContents.update(this.#entityId, this.#CONTENTS_CATEGORY, data).pipe(
                tap(() => this.#ephemeralSrv.showSaveSuccess())
            );
        }
        this.form.markAllAsTouched();
        this.form.patchValue(this.form.value);
        scrollIntoFirstInvalidFieldOrErrorMsg(document);
        return throwError(() => 'invalid form');
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this.#messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
