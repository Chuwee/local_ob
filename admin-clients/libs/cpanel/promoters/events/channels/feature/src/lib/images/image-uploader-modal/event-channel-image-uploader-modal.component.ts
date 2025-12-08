/* eslint-disable @typescript-eslint/naming-convention */
import { EventChannelsService, eventChannelsProviders } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import {
    EventChannelContentImageRequest, EventChannelContentImageType
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { Session } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { DialogSize, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { ImageRestrictions, ObFile } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, effect, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { filter, forkJoin, Observable, of, tap } from 'rxjs';
import { EventChannelImageUploaderComponent } from '../image-uploader/event-channel-image-uploader.component';

export const eventChannelImageRestrictions: ImageRestrictions = { width: 800, height: 800, size: 92160 };

export enum ImageOrigin {
    SESSION = 'SESSION',
    EVENT = 'EVENT',
    CHANNEL_EVENT = 'CHANNEL_EVENT'
}

interface ImageUploaderData {
    session: Session;
    eventId: number;
    eventChannelId: number;
}

@Component({
    selector: 'app-event-channel-image-uploader-modal',
    templateUrl: './event-channel-image-uploader-modal.component.html',
    styleUrls: ['./event-channel-image-uploader-modal.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatIcon, MatIconButton, MatDialogTitle, MatDialogContent, MatDialogActions, MatButton,
        TranslatePipe, EventChannelImageUploaderComponent, DatePipe, MatProgressSpinner
    ],
    providers: [eventChannelsProviders]
})

export class EventChannelImageUploaderModalComponent implements OnInit {
    readonly #dialogRef = inject(MatDialogRef<EventChannelImageUploaderModalComponent>);
    readonly #eventChannelsSrv = inject(EventChannelsService);
    readonly #translate = inject(TranslateService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMsgService = inject(EphemeralMessageService);
    readonly #data = inject<ImageUploaderData>(MAT_DIALOG_DATA);

    readonly $eventSessionSquareImages = toSignal(this.#eventChannelsSrv.eventSessionSquareImages.get$().pipe(filter(Boolean)));

    readonly #sessionId = this.#data.session.id;
    readonly #eventId = this.#data.eventId;
    readonly #eventChannelId = this.#data.eventChannelId;
    readonly #imageOrigin = this.#data.session.image_origin as ImageOrigin;
    readonly session = this.#data.session;

    readonly $loading = toSignal(booleanOrMerge([
        this.#eventChannelsSrv.eventSessionSquareImages.inProgress$()
    ]));

    readonly form = this.#fb.group({
        image1: null as ObFile,
        image2: null as ObFile,
        image3: null as ObFile,
        image4: null as ObFile,
        image5: null as ObFile
    });

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.LATERAL);
        this.#dialogRef.disableClose = true;

        effect(() => {
            const images = this.$eventSessionSquareImages();
            if (images) this.#setFormValues(images);
        });
    }

    ngOnInit(): void {
        if (this.#imageOrigin !== ImageOrigin.SESSION) return;
        this.#eventChannelsSrv.eventSessionSquareImages.load(this.#eventId, this.#eventChannelId, this.#sessionId);
    }

    close(result = false): void {
        this.#eventChannelsSrv.eventSessionSquareImages.clear();
        this.#dialogRef.close(result);
    }

    save(): void {
        this.#save$().subscribe(() => this.close(true));
    }

    #deleteEventSessionSquareImage(position: number): Observable<void> {
        return this.#eventChannelsSrv.eventSessionSquareImages.delete(
            this.#eventId,
            this.#eventChannelId,
            this.#sessionId,
            this.#translate.getCurrentLang(),
            EventChannelContentImageType.square,
            position
        );
    }

    #saveEventSessionImages(images: EventChannelContentImageRequest[]): Observable<void> {
        return this.#eventChannelsSrv.eventSessionSquareImages.save(this.#eventId, this.#eventChannelId, this.#sessionId, images);
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
                else operations$.push(this.#deleteEventSessionSquareImage(position));
            });

        if (imagesToUpload.length > 0) operations$.push(this.#saveEventSessionImages(imagesToUpload));

        if (operations$.length === 0) return of();
        return forkJoin(operations$).pipe(tap(() => this.#ephemeralMsgService.showSaveSuccess()));
    }

    #setFormValues(images: EventChannelContentImageRequest[]): void {
        const patch: Partial<Record<string, string>> = {};
        images.forEach(({ position, image_url }) => {
            const key = `image${position}`;
            if (this.form.get(key)) patch[key] = image_url;
        });

        this.form.patchValue(patch);
    }
}
