import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ImageRestrictions, ObFile, ObFileDimensions } from '@admin-clients/shared/data-access/models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal, viewChild } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatError, MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
// eslint-disable-next-line @typescript-eslint/naming-convention
import Cropper from 'cropperjs';
import { DialogSize } from '../dialog/models/dialog-size.enum';
import { ImageCropperComponent } from './image-cropper.component';

export const PNG_WITH_TRANSPARENCY_SIZE_LIMIT = 512000;

export interface ImageCropperDialogData {
    file: ObFile;
    hasAltText: boolean;
    imageRestrictions?: ImageRestrictions;
    allowPngConversion: boolean;
}

@Component({
    imports: [
        CommonModule,
        MatButton,
        MatDialogActions,
        MatDialogContent,
        MatDialogTitle,
        MatError,
        MatFormField,
        MatIcon,
        MatIconButton,
        MatInput,
        MatLabel,
        MatSuffix,
        MatTooltip,
        TranslatePipe,
        ImageCropperComponent,
        ReactiveFormsModule,
        FormControlErrorsComponent
    ],
    selector: 'app-image-cropper-dialog',
    templateUrl: './image-cropper-dialog.component.html',
    styleUrl: './image-cropper-dialog.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImageCropperDialogComponent {
    readonly #dialogRef: MatDialogRef<ImageCropperDialogComponent, ObFile> = inject(MatDialogRef);
    readonly #data: ImageCropperDialogData = inject(MAT_DIALOG_DATA);

    readonly imageRestrictions = this.#data.imageRestrictions;
    readonly cropperOptions: Cropper.Options = {
        aspectRatio: this.imageRestrictions ? this.imageRestrictions.width / this.imageRestrictions.height : NaN,
        cropBoxResizable: false,
        toggleDragModeOnDblclick: false,
        dragMode: 'move',
        background: true,
        movable: true,
        rotatable: true,
        scalable: true,
        center: true,
        zoomable: true,
        responsive: true,
        cropBoxMovable: false,
        viewMode: 0,
        checkCrossOrigin: true,
        checkOrientation: false
    };

    // width and height filled only when image restrictions only have maxWidth and maxHeight (no width and height)
    readonly cropBoxData: Cropper.CropBoxData = {
        top: 0,
        left: 0,
        width: this.imageRestrictions?.width,
        height: this.imageRestrictions?.height
    };

    readonly showAltImageField = this.#data.hasAltText;
    readonly allowPngConversion = this.#data.allowPngConversion;
    readonly altImageControl = new FormControl(
        { value: this.#data.file.altText, disabled: !this.#data.hasAltText }, Validators.required
    );

    readonly $file = signal<ObFileDimensions>(this.#data.file);
    readonly $imageCropper = viewChild<ImageCropperComponent>('imageCropper');

    cropperFailed = false;

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.FULL_SCREEN);
    }

    exportImage(cropperResult: { dataUrl: string; size: number; width: number; height: number }): void {
        if (cropperResult.dataUrl !== this.$file().data) {
            this.$file.update(file => {
                const convertToPng = this.allowPngConversion && this.$imageCropper()?.convertToPng;
                if (convertToPng) file.name = file.name.replace(/\.(jpe?g)$/i, '.png');
                file.contentType = convertToPng ? 'image/png' : file.contentType;
                file.remote = false;
                file.data = cropperResult.dataUrl.split(',')[1];
                file.size = convertToPng ? this.#getBase64Size(cropperResult.dataUrl.split(',')[1]) : cropperResult.size;
                file.altText = this.altImageControl.value;
                if (!this.#data.imageRestrictions.width && !this.#data.imageRestrictions.height
                    && this.#data.imageRestrictions.maxWidth && this.#data.imageRestrictions.maxHeight) {
                    file.width = cropperResult.width;
                    file.height = cropperResult.height;
                }
                return file;
            });
            this.#dialogRef.close(this.$file());
        } else if (!this.$file().remote) {
            this.$file.update(file => ({ ...file, altText: this.altImageControl.value }));
            this.#dialogRef.close(this.$file());
        } else {
            this.#dialogRef.close();
        }
    }

    save(): void {
        const validForm = this.altImageControl.disabled || this.altImageControl.valid;
        if (validForm) {
            const altTextTouched = this.altImageControl.value !== this.#data.file.altText;
            this.$imageCropper()?.exportCanvas(altTextTouched);
        } else {
            this.altImageControl.markAsTouched();
        }
    }

    close(): void {
        this.#dialogRef.close();
    }

    #getBase64Size(base64String: string): number {
        const padding = base64String.endsWith('==') ? 2 : base64String.endsWith('=') ? 1 : 0;
        return Math.floor((base64String.length * 3) / 4) - padding;
    }
}
