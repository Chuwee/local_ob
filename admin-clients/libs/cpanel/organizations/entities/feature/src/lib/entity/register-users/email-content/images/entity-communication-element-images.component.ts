import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    EntitiesService, EntityCommunicationElementImage, EntityCommunicationElementImageType, EntityCommunicationElementText,
    EntityCommunicationElementTextType, entityCommunicationElementImageRestrictions
} from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    EphemeralMessageService, ImageUploaderComponent, LanguageBarComponent, MessageDialogService, UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { ObFile } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { FormControlHandler, urlValidator } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, computed, effect, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslateModule } from '@ngx-translate/core';
import { combineLatest, filter, forkJoin, map, Observable, of, throwError } from 'rxjs';

interface PayloadForm {
    images: {
        save: EntityCommunicationElementImage | null;
        delete: { language: string; type: EntityCommunicationElementImageType } | null;
    };
    texts: EntityCommunicationElementText | null;
}

@Component({
    selector: 'app-entity-communication-element-images',
    templateUrl: './entity-communication-element-images.component.html',
    imports: [
        LanguageBarComponent, FormContainerComponent, ReactiveFormsModule, TranslateModule, MatFormField, MatInput, MatLabel,
        MatProgressSpinner, MatError, FormControlErrorsComponent, ImageUploaderComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityCommunicationElementImagesComponent implements WritingComponent {
    readonly #entitiesService = inject(EntitiesService);
    readonly #fb = inject(FormBuilder);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);

    readonly #$entity = toSignal(this.#entitiesService.getEntity$());

    readonly #$entityId = computed(() => this.#$entity()?.id);

    readonly #$communicationApiValues = toSignal(
        combineLatest([
            this.#entitiesService.entityCommunicationElementImages.get$(),
            this.#entitiesService.entityCommunicationElementTexts.get$()
        ]).pipe(filter(data => data.every(Boolean)), map(([images, texts]) => ({ images, texts }))),
        { initialValue: { images: [], texts: [] } }
    );

    readonly imageRestrictions = entityCommunicationElementImageRestrictions;
    readonly imageType = EntityCommunicationElementImageType;

    readonly $loading = toSignal(this.#entitiesService.entityCommunicationElementImages.inProgress$());
    readonly $selectedLanguage = signal<string>(null);
    readonly $languages = computed(() => this.#$entity()?.settings?.languages?.available);

    readonly form = this.#fb.group({
        headerBanner: this.#fb.control<ObFile | string>(null),
        headerBannerUrl: this.#fb.control<string>(null, urlValidator())
    });

    constructor() {
        this.#initEffects();
        FormControlHandler.getValueChanges(this.form.controls.headerBannerUrl)
            .pipe(takeUntilDestroyed()).subscribe(() => this.#checkHeaderBannerUrl());
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.#validateIfCanChangeLanguage();

    save(): void {
        this.save$().subscribe(() => {
            this.#ephemeralSrv.showSaveSuccess();
            this.#loadCommunicationElements(this.#$entityId());
        });
    }

    save$(): Observable<unknown> {
        if (this.form.invalid) {
            return this.#showErrors();
        }

        const communicationElements = this.#preparePayload();

        return this.#saveCommunicationElements$(communicationElements);
    }

    cancel(): void {
        this.#loadCommunicationElements(this.#$entityId());
    }

    #initEffects(): void {
        effect(() => {
            const entityId = this.#$entityId();
            if (entityId == null) return;
            this.#loadCommunicationElements(entityId);
        });

        effect(() => {
            const languages = this.$languages();
            if (!languages?.length || this.$selectedLanguage()) return;
            this.$selectedLanguage.set(languages[0]);
        });

        effect(() => this.#patchFormWithApiData());
    }

    #patchFormWithApiData(): void {
        const { images, texts } = this.#$communicationApiValues();

        const headerBanner = images.find(image =>
            image.type === EntityCommunicationElementImageType.headerBanner && image.language === this.$selectedLanguage()
        );
        const headerBannerUrl = texts.find(text =>
            text.type === EntityCommunicationElementTextType.headerBannerUrl && text.language === this.$selectedLanguage()
        );

        this.form.reset({
            headerBanner: headerBanner?.image_url || null,
            headerBannerUrl: headerBannerUrl?.value || null
        }, { emitEvent: false });
    }

    #checkHeaderBannerUrl(): void {
        const apiValue = this.#$communicationApiValues().texts.find(text =>
            text.type === EntityCommunicationElementTextType.headerBannerUrl &&
            text.language === this.$selectedLanguage()
        )?.value;

        FormControlHandler.checkAndRefreshDirtyState(this.form.controls.headerBannerUrl, apiValue);
    }

    #preparePayload(): PayloadForm | null {
        if (this.form.pristine) return null;

        const { headerBanner, headerBannerUrl } = this.form.controls;
        const language = this.$selectedLanguage();
        const payload: PayloadForm = {
            images: { save: null, delete: null }, texts: null
        };

        if (headerBanner.dirty) {
            if (headerBanner.value && typeof headerBanner.value !== 'string') {
                payload.images.save = {
                    type: EntityCommunicationElementImageType.headerBanner,
                    image_url: headerBanner.value.data,
                    image: headerBanner.value.name,
                    alt_text: headerBanner.value.altText,
                    language
                };
            } else {
                payload.images.delete = {
                    type: EntityCommunicationElementImageType.headerBanner,
                    language
                };
            }
        }

        if (headerBannerUrl.dirty && headerBannerUrl.valid) {
            payload.texts = {
                type: EntityCommunicationElementTextType.headerBannerUrl,
                language,
                value: headerBannerUrl.value
            };
        }

        return payload;
    }

    #loadCommunicationElements(entityId: number): void {
        this.#entitiesService.entityCommunicationElementImages.load(entityId);
        this.#entitiesService.entityCommunicationElementTexts.load(entityId);
    }

    #saveCommunicationElements$(communicationElements: PayloadForm): Observable<unknown> {
        const observables: Observable<void>[] = [];

        if (communicationElements.images.save) {
            observables.push(
                this.#entitiesService.entityCommunicationElementImages.create$(this.#$entityId(), [communicationElements.images.save])
            );
        }

        if (communicationElements.images.delete) {
            observables.push(
                this.#entitiesService.entityCommunicationElementImages.delete$(
                    this.#$entityId(),
                    communicationElements.images.delete?.language,
                    communicationElements.images.delete?.type
                )
            );
        }

        if (communicationElements.texts) {
            observables.push(
                this.#entitiesService.entityCommunicationElementTexts.create$(this.#$entityId(), [communicationElements.texts])
            );
        }
        if (observables.length === 0) {
            return of(void 0);
        }

        return forkJoin(observables);
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
                    if (this.form.invalid) {
                        this.#showErrors();
                        return false;
                    }
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
