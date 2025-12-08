/* eslint-disable @typescript-eslint/naming-convention */
import { EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import {
    EventChannelContentImageRequest, EventChannelContentImageType
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { EventSessionPackConf, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { ObFile } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { atLeastOneRequiredInFormGroup, booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, DestroyRef, OnInit, effect } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, ValidatorFn } from '@angular/forms';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, filter, forkJoin, map, Observable, of, tap } from 'rxjs';
import { EventChannelImageUploaderComponent } from './image-uploader/event-channel-image-uploader.component';
import { EventChannelSessionImagesComponent } from './session-images/event-channel-session-images.component';

@Component({
    selector: 'app-event-channel-images',
    templateUrl: './event-channel-images.component.html',
    styleUrls: ['./event-channel-images.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, ReactiveFormsModule, MatProgressSpinner, EventChannelImageUploaderComponent,
        TranslatePipe, EventChannelSessionImagesComponent, UpperCasePipe
    ]
})

export class EventChannelImagesComponent implements OnInit {
    readonly #router = inject(Router);
    readonly #destroyRef = inject(DestroyRef);
    readonly #sessionsSrv = inject(EventSessionsService);
    readonly #eventChannelsSrv = inject(EventChannelsService);
    readonly #eventService = inject(EventsService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMsgService = inject(EphemeralMessageService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #translate = inject(TranslateService);

    readonly $eventChannelSquareImages = toSignal(this.#eventChannelsSrv.eventChannelSquareImages.get$().pipe(filter(Boolean)));

    readonly #$isExternalWhitelabel = toSignal(this.#eventChannelsSrv.eventChannel.get$().pipe(
        filter(Boolean),
        map(eventChannel => eventChannel.channel.whitelabel_type === 'EXTERNAL')
    ));

    readonly $isFestivalOrSessionPack = toSignal(this.#eventService.event.get$().pipe(
        filter(Boolean),
        map(event => !!event.settings.festival ||
            event.settings.session_pack === EventSessionPackConf.unrestricted ||
            event.settings.session_pack === EventSessionPackConf.restricted
        )
    ));

    readonly $loading = toSignal(booleanOrMerge([
        this.#eventChannelsSrv.eventChannel.inProgress$(),
        this.#eventService.event.inProgress$(),
        this.#eventChannelsSrv.eventChannelSquareImages.inProgress$(),
        this.#sessionsSrv.sessionList.inProgress$(),
        this.#eventChannelsSrv.eventSessionSquareImages.inProgress$(),
        this.#eventChannelsSrv.eventSessionSquareImagesConfig.inProgress$()
    ]));

    readonly form = this.#fb.group({
        image1: null as ObFile,
        image2: null as ObFile,
        image3: null as ObFile,
        image4: null as ObFile,
        image5: null as ObFile
    });

    eventChannelId: number;
    eventId: number;
    forceSquareImages: boolean;

    constructor() {
        effect(() => {
            const images = this.$eventChannelSquareImages();
            if (images) {
                this.#setFormValues(images);
                this.#setFormValidators();
            };
        });
    }

    ngOnInit(): void {
        combineLatest([
            this.#eventChannelsSrv.eventChannel.get$().pipe(filter(Boolean)),
            this.#eventService.event.get$().pipe(filter(Boolean))
        ]).pipe(
            takeUntilDestroyed(this.#destroyRef),
            tap(([eventChannel, event]) => {
                this.eventChannelId = eventChannel.channel.id;
                this.eventId = event.id;
                this.forceSquareImages = eventChannel.channel?.force_square_pictures ?? false;
            })
        ).subscribe(() => {
            if (this.#$isExternalWhitelabel()) this.#loadEventChannelSquareImages();
            else this.#externalWhitelabelRedirectGuard();
        });
    }

    cancel(): void {
        this.#loadEventChannelSquareImages();
    }

    save(): void {
        if (this.form.invalid) return;
        this.#save$().subscribe(() => {
            this.#loadEventChannelSquareImages();

            if (this.$isFestivalOrSessionPack()) {
                this.#eventChannelsSrv.eventSessionSquareImagesConfig.load(this.eventId, this.eventChannelId);
            }
        });
    }

    #save$(): Observable<void[]> {
        const imagesToUpload: EventChannelContentImageRequest[] = [];
        const operations$: Observable<void>[] = [];

        Object.entries(this.form.controls)
            .filter(([_, control]) => control.dirty)
            .forEach(([key, control]) => {
                const position = Number(key.replace('image', ''));
                const base: EventChannelContentImageRequest = {
                    position,
                    type: EventChannelContentImageType.square,
                    language: this.#translate.getCurrentLang()
                };

                if (control.value) imagesToUpload.push({ ...base, image: control.value.data });
                else operations$.push(this.#deleteEventChannelImage(position));
            });

        if (imagesToUpload.length > 0) operations$.push(this.#saveEventChannelImages(imagesToUpload));

        if (operations$.length === 0) return of();
        return forkJoin(operations$).pipe(tap(() => this.#ephemeralMsgService.showSaveSuccess()));
    }

    #saveEventChannelImages(images: EventChannelContentImageRequest[]): Observable<void> {
        return this.#eventChannelsSrv.eventChannelSquareImages.save(this.eventId, this.eventChannelId, images);
    }

    #loadEventChannelSquareImages(): void {
        this.#eventChannelsSrv.eventChannelSquareImages.load(this.eventId, this.eventChannelId);
    }

    #deleteEventChannelImage(position: number): Observable<void> {
        return this.#eventChannelsSrv.eventChannelSquareImages.delete(
            this.eventId,
            this.eventChannelId,
            this.#translate.getCurrentLang(),
            EventChannelContentImageType.square,
            position
        );
    }

    #setFormValues(images: EventChannelContentImageRequest[]): void {
        this.form.reset();
        const patch: Partial<Record<string, string>> = {};
        images.forEach(({ position, image_url }) => {
            const key = `image${position}`;
            if (this.form.get(key)) patch[key] = image_url;
        });

        this.form.patchValue(patch);
    }

    #setFormValidators(): void {
        if (this.forceSquareImages) this.form.setValidators(atLeastOneRequiredInFormGroup() as ValidatorFn);
        else this.form.setValidators(null);
        this.form.updateValueAndValidity();
    }

    #externalWhitelabelRedirectGuard(): void {
        this.#msgDialogService.showAlert({
            size: DialogSize.SMALL,
            title: 'EVENTS.CHANNEL.SQUARE_IMAGES.GUARD_TITLE',
            message: 'EVENTS.CHANNEL.SQUARE_IMAGES.GUARD_MESSAGE',
            actionLabel: 'FORMS.ACTIONS.CLOSE'
        });

        this.#router.navigate([`/events/${this.eventId}/channels/${this.eventChannelId}/general-data`]);
    }
}
