import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EntitiesService, EntityContent } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    EphemeralMessageService, LanguageBarComponent, MessageDialogService, RichTextAreaComponent, UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, computed, effect, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslateModule } from '@ngx-translate/core';
import { filter, map, Observable, of, throwError } from 'rxjs';

const CONTENTS_MAPPING = {
    body: 5,
    footer: 6
};
const CONTENTS_CATEGORY = 'email';

@Component({
    selector: 'app-entity-email-content-new-account',
    templateUrl: './entity-email-content-new-account.component.html',
    imports: [
        LanguageBarComponent, FormContainerComponent, ReactiveFormsModule, TranslateModule, MatFormField, MatInput, MatLabel,
        MatProgressSpinner, RichTextAreaComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityEmailContentNewAccountComponent implements WritingComponent {
    readonly #entitiesService = inject(EntitiesService);
    readonly #fb = inject(FormBuilder);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);

    readonly #$entity = toSignal(this.#entitiesService.getEntity$());
    readonly #$entityId = computed(() => this.#$entity()?.id);

    readonly $loading = toSignal(this.#entitiesService.entityContents.inProgress$());
    readonly $selectedLanguage = signal<string>(null);
    readonly $languages = computed(() => this.#$entity()?.settings?.languages?.available);

    readonly form = this.#fb.group({
        subject: this.#fb.control<string>(null, [Validators.required]),
        body: this.#fb.control<string>(null),
        footer: this.#fb.control<string>(null)
    });

    constructor() {
        this.#initLanguageReactivity();
        this.#initContentsListener();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.#validateIfCanChangeLanguage();

    save(): void {
        this.#save$().subscribe(() => {
            this.#ephemeralSrv.showSaveSuccess();
            this.#loadContents(this.$selectedLanguage());
        });
    }

    cancel(): void {
        this.#loadContents(this.$selectedLanguage());
    }

    #loadContents(lang: string): void {
        this.#entitiesService.entityContents.load(this.#$entityId(), CONTENTS_CATEGORY, lang);
        this.form.markAsPristine();
    }

    #save$(): Observable<void> {
        if (this.form.invalid) {
            return this.#showErrors();
        }

        const data = this.#buildEntityContents();

        return this.#entitiesService.entityContents.update(this.#$entityId(), CONTENTS_CATEGORY, data);
    }

    #initLanguageReactivity(): void {
        effect(() => {
            const lang = this.$selectedLanguage();
            if (!lang) return;
            this.#entitiesService.entityContents.load(this.#$entityId(), CONTENTS_CATEGORY, lang);
        });

        effect(() => {
            const languages = this.$languages();
            if (!languages?.length || this.$selectedLanguage()) return;
            this.$selectedLanguage.set(languages[0]);
        });
    }

    #initContentsListener(): void {
        this.#entitiesService.entityContents.get$().pipe(
            filter(val => !!val?.length),
            takeUntilDestroyed()
        ).subscribe(contents => this.#patchFormWithApiData(contents));
    }

    #patchFormWithApiData(contents: EntityContent[]): void {
        const body = contents.find(content => content.id === CONTENTS_MAPPING.body);
        const footer = contents.find(content => content.id === CONTENTS_MAPPING.footer);

        this.form.reset({
            subject: body?.subject,
            body: body?.value,
            footer: footer?.value
        }, { emitEvent: false });
    }

    #buildEntityContents(): EntityContent[] {
        const currentLang = this.$selectedLanguage();
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
        return [body, footer];
    }

    #validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.pristine) {
            return of(true);
        }
        return this.#messageDialogService.openRichUnsavedChangesWarn().pipe(map(res => {
            switch (res) {
                case UnsavedChangesDialogResult.cancel:
                    return false;
                case UnsavedChangesDialogResult.continue:
                    return true;
                case UnsavedChangesDialogResult.save:
                    this.save();
                    return true;
            }
        }));
    }

    #showErrors(): Observable<never> {
        this.form.markAllAsTouched();
        scrollIntoFirstInvalidFieldOrErrorMsg(document);
        return throwError(() => 'invalid form');
    }
}
