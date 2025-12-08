import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EntitiesService, ZoneTemplateContent } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    MessageDialogService, EphemeralMessageService, LanguageBarComponent, RichTextAreaComponent
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable, of, throwError } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';

const PORTAL_CONTENTS_MAPPING = {
    header: 2,
    mailSending: 48,
    venuePickUp: 49,
    mobileSending: 50,
    whatsapp: 67,
    customModule: 81,
    smartBookingCustomModule: 82,
    downloadTickets: 54,
    footer: 26
};

@Component({
    selector: 'app-entity-zone-template-email',
    templateUrl: './entity-zone-template-email.component.html',
    styleUrls: ['./entity-zone-template-email.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, LanguageBarComponent, MatFormField, MatLabel, RichTextAreaComponent,
        TranslatePipe, ReactiveFormsModule, AsyncPipe, MatProgressSpinner, MatIcon
    ]
})
export class EntityZoneTemplateEmailComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #entityService = inject(EntitiesService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #CONTENTS_CATEGORY = 'email';

    #entityId: number;
    #selectedLanguage = new BehaviorSubject<string>(null);
    #contentsIds: typeof PORTAL_CONTENTS_MAPPING;

    readonly $template = toSignal(this.#entityService.zoneTemplate.get$().pipe(filter(Boolean)));

    readonly form = this.#fb.group({
        customModule: undefined as string,
        smartBookingCustomModule: undefined as string
    });

    readonly isInProgress$ = this.#entityService.zoneTemplate.contents.inProgress$();
    readonly languageList$ = this.#entityService.getEntity$()
        .pipe(
            first(Boolean),
            tap(entity => {
                this.#entityId = entity.id;
                this.#contentsIds = PORTAL_CONTENTS_MAPPING;
            }),
            map(entity => entity.settings.languages.available),
            filter(Boolean),
            tap(languages => this.#selectedLanguage.next(languages[0])),
            shareReplay(1)
        );

    readonly hasSmartBooking$ = this.#entityService.getEntity$()
        .pipe(
            filter(Boolean),
            map(entity => (entity.settings.external_integration?.custom_managements.filter(
                management => management.type === 'SMART_BOOKING_INTEGRATION'
            ).length > 0)));

    readonly selectedLanguage$ = this.#selectedLanguage.asObservable();
    placeholdersMap: Record<string, string[]> = {};

    ngOnInit(): void {
        combineLatest([
            this.selectedLanguage$,
            this.#entityService.zoneTemplate.get$()
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            switchMap(([lang, template]) => {
                this.loadContents(lang, template.id);
                return this.#entityService.zoneTemplate.contents.get$();
            }),
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        )
            .subscribe(contents => {
                const customModule = contents.find(content => content.id === this.#contentsIds.customModule);
                const smartBookingCustomModule = contents.find(content => content.id === this.#contentsIds.smartBookingCustomModule);

                this.placeholdersMap = {
                    customModule: customModule?.labels?.map(({ code }) => code),
                    smartBookingCustomModule: smartBookingCustomModule?.labels?.map(({ code }) => code)
                };
                this.form.patchValue({
                    customModule: customModule?.value,
                    smartBookingCustomModule: smartBookingCustomModule?.value
                });
                this.form.markAsPristine();
            });
    }

    ngOnDestroy(): void {
        this.#entityService.zoneTemplate.contents.clear();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    save(): void {
        this.save$().subscribe(() => {
            this.loadContents(this.#selectedLanguage.getValue(), this.$template().id);
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const currentLang = this.#selectedLanguage.getValue();
            const formValues = this.form.value;
            const customModule: ZoneTemplateContent = {
                id: this.#contentsIds.customModule,
                language: currentLang,
                value: formValues.customModule
            };
            const smartBookingCustomModule: ZoneTemplateContent = {
                id: this.#contentsIds.smartBookingCustomModule,
                language: currentLang,
                value: formValues.smartBookingCustomModule
            };

            const data = [customModule];

            if (formValues.smartBookingCustomModule != null) {
                data.push(smartBookingCustomModule);
            }

            return this.#entityService.zoneTemplate.contents.update(
                this.#entityId, this.$template().id, this.#CONTENTS_CATEGORY, data, currentLang
            ).pipe(
                tap(() => this.#ephemeralSrv.showSaveSuccess())
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.loadContents(this.#selectedLanguage.getValue(), this.$template().id);
    }

    changeLanguage(newLanguage: string): void {
        this.#selectedLanguage.next(newLanguage);
    }

    private loadContents(lang: string, templateId: number): void {
        this.#entityService.zoneTemplate.contents.load(this.#entityId, templateId, this.#CONTENTS_CATEGORY, lang);
        this.form.markAsPristine();
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this.#messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
