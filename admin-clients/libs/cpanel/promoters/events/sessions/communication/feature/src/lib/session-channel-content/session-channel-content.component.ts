import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService, SessionWrapper } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    SessionChannelImageRequest, sessionChannelMainRestrictions, sessionChannelSliderNumElements, sessionChannelSliderRestrictions,
    SessionChannelText, SessionChannelTextRestrictions, SessionCommunicationService
} from '@admin-clients/cpanel-promoters-events-sessions-communication-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { combineLatest, iif, Observable, of } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';

@Component({
    selector: 'app-session-channel-content',
    templateUrl: './session-channel-content.component.html',
    styleUrls: ['./session-channel-content.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionChannelContentComponent implements OnInit, OnDestroy, AfterViewInit {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #eventService = inject(EventsService);
    readonly #sessionService = inject(EventSessionsService);
    readonly #sessionCommunicationService = inject(SessionCommunicationService);
    readonly #entitiesService = inject(EntitiesBaseService);

    readonly $allowPngConversion = toSignal(this.#entitiesService.getEntity$()
        .pipe(map(entity => entity?.settings?.allow_png_conversion ?? true))
    );

    readonly #destroyRef = inject(DestroyRef);
    #textFields = {
        title: 'TITLE',
        description: 'DESCRIPTION'
    };

    #imageTypes = {
        main: 'MAIN',
        landscape: 'LANDSCAPE'
    };

    #selectedLanguage: string;
    #eventId: number;
    #sessionId: number;
    #selectedSessions: SessionWrapper[];

    sessionName: string;
    textRestrictions = SessionChannelTextRestrictions;
    mainImageRestrictions = sessionChannelMainRestrictions;
    sliderImageRestrictions = sessionChannelSliderRestrictions;

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;
    @Input() multiple: boolean;

    get channelContentForm(): UntypedFormGroup {
        return this.form.get('channelContentForm') as UntypedFormGroup;
    }

    get textsForm(): UntypedFormGroup {
        return this.channelContentForm.get('texts') as UntypedFormGroup;
    }

    get imagesForm(): UntypedFormGroup {
        return this.channelContentForm.get('images') as UntypedFormGroup;
    }

    ngOnInit(): void {
        this.#initForms();
        this.#loadContents();
        this.language$.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(lang => this.#selectedLanguage = lang);
    }

    ngAfterViewInit(): void {
        this.#refreshFormDataHandler();
    }

    ngOnDestroy(): void {
        this.#clearChannelContent();
    }

    reload(reload = true): void {
        this.channelContentForm.markAsPristine();
        if (!this.multiple) {
            this.#sessionCommunicationService.loadChannelTexts(this.#eventId, this.#sessionId);
            this.#sessionCommunicationService.loadChannelImages(this.#eventId, this.#sessionId);
        } else if (this.multiple && reload) {
            this.#clearChannelContent();
        }
    }

    save(): Observable<void | void[]>[] {
        const obsToSave$: Observable<void | void[]>[] = [];
        const eventId = this.#eventId;

        let sessionIds: number[];
        if (this.multiple) {
            sessionIds = this.#selectedSessions.map(session => session.session.id);
            if (this.imagesForm.get('setDefault').value) {
                obsToSave$.push(this.#sessionCommunicationService.setDefaultChannelImages(
                    eventId, sessionIds, this.#selectedLanguage));
            }
        } else {
            sessionIds = [this.#sessionId];
        }

        const images: SessionChannelImageRequest[] = [];
        images.push(this.#getImage('main', this.#selectedLanguage));
        for (let i = 0; i < sessionChannelSliderNumElements; i++) {
            images.push(this.#getImage('landscape', this.#selectedLanguage, i));
        }
        const imagesToAdd = images.filter(image => image?.image);
        const imagesToDelete = images.filter(image => image && image.image === null);

        if (imagesToAdd.length) {
            obsToSave$.push(this.#sessionCommunicationService.updateChannelImages(
                eventId, sessionIds, imagesToAdd));
        }
        if (imagesToDelete.length) {
            obsToSave$.push(this.#sessionCommunicationService.deleteChannelImages(
                eventId, sessionIds, imagesToDelete));
        }

        const texts: SessionChannelText[] = [];
        texts.push(this.#getText('title', this.#selectedLanguage));
        texts.push(this.#getText('description', this.#selectedLanguage));
        const textToAdd = texts.filter(text => !!text);

        if (textToAdd.length) {
            obsToSave$.push(this.#sessionCommunicationService.updateChannelTexts(
                eventId, sessionIds, textToAdd));
        }
        return obsToSave$;
    }

    #initForms(): void {
        this.form.addControl('channelContentForm', this.#fb.group({
            texts: this.#fb.group({
                title: [null, Validators.maxLength(this.textRestrictions.titleMaxLength)],
                description: [null]
            }),
            images: this.#fb.group({
                setDefault: { value: null, disabled: !this.multiple },
                main: [{ value: null, disabled: false }],
                landscape: this.#fb.array(Array(sessionChannelSliderNumElements).fill([
                    { value: null, disabled: false }
                ]))
            })
        }));
    }

    #loadContents(): void {
        // load texts and images
        combineLatest([
            this.#eventService.event.get$(),
            this.#sessionService.session.get$(),
            iif(
                () => this.multiple,
                this.#sessionService.getSelectedSessions$(),
                of([] as SessionWrapper[])
            )
        ])
            .pipe(
                filter(([event]) => !!event),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([event, session, sessions]) => {
                this.#eventId = event.id;
                if (this.multiple) {
                    this.#clearChannelContent();
                    this.#selectedSessions = sessions;
                } else if (session) {
                    this.#sessionId = session.id;
                    this.sessionName = session.name;
                    this.#sessionCommunicationService.loadChannelTexts(event.id, session.id);
                    this.#sessionCommunicationService.loadChannelImages(event.id, session.id);
                }
            });

        combineLatest([
            this.#sessionCommunicationService.getChannelImages$(),
            this.language$
        ])
            .pipe(
                tap(() => this.imagesForm.reset()),
                filter(([images, language]) => !!images && !!language),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([images, language]) => {
                images.filter(image => image.language === language).forEach(image => {
                    const fieldName = this.#getKeyByValue(this.#imageTypes, image.type);
                    if (image.type === this.#imageTypes.main) {
                        this.imagesForm.get(fieldName).reset({ data: image.image_url, altText: image.alt_text });
                    } else {
                        (this.imagesForm.get(fieldName) as UntypedFormArray).at(image.position - 1).reset({
                            data: image.image_url,
                            altText: image.alt_text
                        });
                    }
                });
            });

        combineLatest([
            this.#sessionCommunicationService.getChannelTexts$(),
            this.language$
        ])
            .pipe(
                tap(() => this.textsForm.reset()),
                filter(([texts, language]) => !!texts && !!language),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([texts, language]) => {
                texts.filter(text => text.language === language).forEach(text => {
                    const fieldName = this.#getKeyByValue(this.#textFields, text.type || this.#textFields.description);
                    this.textsForm.get(fieldName).reset(text.value);
                });
            });
    }

    #refreshFormDataHandler(): void {
        if (this.multiple) {
            this.imagesForm.get('setDefault').valueChanges
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe(setDefault => {
                    if (setDefault) {
                        this.imagesForm.get('main').disable();
                        this.imagesForm.get('landscape').disable();
                    } else {
                        this.imagesForm.get('main').enable();
                        this.imagesForm.get('landscape').enable();
                    }
                    this.imagesForm.get('main').reset();
                    this.imagesForm.get('landscape').reset();
                });
        }
    }

    #clearChannelContent(): void {
        this.channelContentForm.reset();
        this.#sessionCommunicationService.clearChannelTexts();
        this.#sessionCommunicationService.clearChannelImages();
    }

    #getImage(field: string, language: string, position = -1): SessionChannelImageRequest {
        const arrayField = position !== -1 ? `${field}.${position}` : field;
        const imageField = this.imagesForm.get(arrayField);
        if (imageField.dirty) {
            return {
                language,
                type: this.#imageTypes[field],
                image: imageField.value?.data ?? null,
                alt_text: imageField.value?.altText ?? null,
                position: position !== -1 ? position + 1 : null
            };
        }
        return null;
    }

    #getText(field: string, language: string): SessionChannelText | null {
        const formField = this.textsForm.get(field);
        if (formField.dirty) {
            return {
                language,
                type: this.#textFields[field],
                value: formField.value
            };
        }
        return null;
    }

    #getKeyByValue(object: { [key: string]: unknown }, value: string): string {
        return Object.keys(object).find(key => object[key] === value);
    }

}
