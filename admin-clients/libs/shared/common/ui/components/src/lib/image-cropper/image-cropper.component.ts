import { ImageRestrictions, ObFile } from '@admin-clients/shared/data-access/models';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, HostListener, inject, Input, OnDestroy, OnInit, Output,
    signal
} from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatError } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSlider, MatSliderThumb } from '@angular/material/slider';
import { MatTooltip } from '@angular/material/tooltip';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { TranslatePipe } from '@ngx-translate/core';
// eslint-disable-next-line @typescript-eslint/naming-convention
import Cropper from 'cropperjs';
import { ColorPickerComponent } from '../color-picker/color-picker.component';
import { fillContent, fitContent } from '../custom-icons/custom-icons';
import { IconManagerService } from '../custom-icons/icon-manager.service';
import { PNG_WITH_TRANSPARENCY_SIZE_LIMIT } from './image-cropper-dialog.component';

enum ImageType { png = 'image/png', jpeg = 'image/jpeg', gif = 'image/gif' }
const MIN_QUALITY = 0.20;

@Component({
    imports: [
        CommonModule,
        MatButton,
        MatDivider,
        MatError,
        MatIcon,
        MatProgressSpinner,
        MatSlider,
        MatSliderThumb,
        MatTooltip,
        TranslatePipe,
        ColorPickerComponent,
        ReactiveFormsModule
    ],
    selector: 'app-image-cropper',
    templateUrl: './image-cropper.component.html',
    styleUrls: ['./image-cropper.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImageCropperComponent implements OnInit, OnDestroy {
    readonly #sanitizer = inject(DomSanitizer);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #iconManagerSrv = inject(IconManagerService);

    readonly $cropper = signal<Cropper>(null);
    readonly $loading = signal(false);
    readonly $warningMessage = signal<string>(null);
    readonly $imageUrl = signal<SafeResourceUrl>(null);
    readonly $zoom = signal<{ value: number; min: number; max: number }>({ value: 140, min: 10, max: 300 });

    #movedOrResized = false;
    #originalRestrictionSize: number;

    fillColor: FormControl<string> = new FormControl('transparent');
    convertToPng = false;

    @Input() file: ObFile;
    @Input() disabled: boolean;
    @Input() cropBoxData: Cropper.CropBoxData = { width: 0, height: 0, top: 0, left: 0 };
    @Input() cropperOptions: Cropper.Options = {};
    @Input() imageRestrictions: ImageRestrictions;
    @Input() allowPngConversion: boolean;
    @Output() export = new EventEmitter<{ dataUrl: string; size: number; width: number; height: number }>();
    @Output() hasError = new EventEmitter<boolean>();

    constructor() {
        this.#iconManagerSrv.addIconDefinition(fitContent, fillContent);
    }

    ngOnInit(): void {
        this.$loading.set(true);
        this.$imageUrl.set(this.#getSafeUrl());

        this.fillColor.valueChanges.subscribe(() => {
            this.#changeFillColor(this.fillColor.value);
            this.#evaluatePngConversionNeed();
        });

        if (this.allowPngConversion) {
            this.#originalRestrictionSize = this.imageRestrictions.size;
            this.imageRestrictions.size = PNG_WITH_TRANSPARENCY_SIZE_LIMIT;
        }
    }

    ngOnDestroy(): void {
        this.$cropper()?.destroy();
        if (this.allowPngConversion) this.imageRestrictions.size = this.#originalRestrictionSize;
    }

    @HostListener('window:keydown.ArrowDown', ['$event'])
    @HostListener('window:keydown.s', ['$event'])
    goDown(): void {
        this.$cropper()?.move(0, 1);
    }

    @HostListener('window:keydown.ArrowUp', ['$event'])
    @HostListener('window:keydown.w', ['$event'])
    goUp(): void {
        this.$cropper()?.move(0, -1);
    }

    @HostListener('window:keydown.ArrowLeft', ['$event'])
    @HostListener('window:keydown.a', ['$event'])
    goLeft(): void {
        this.$cropper()?.move(-1, 0);
    }

    @HostListener('window:keydown.ArrowRight', ['$event'])
    @HostListener('window:keydown.d', ['$event'])
    goRight(): void {
        this.$cropper()?.move(1, 0);
    }

    @HostListener('window:resize')
    onWindowResize(): void {
        if (this.$cropper()) {
            this.#setDefaults();
        }
    }

    imageLoaded(ev: Event): void {
        const image = ev.target as HTMLCanvasElement;

        image.addEventListener('ready', () => {
            if (this.file.contentType === ImageType.gif) {
                this.$warningMessage.set('IMAGE_CROPPER.WARNING_IMAGE_TYPE_GIF');
            }
            this.#setDefaults();
            this.$loading.set(false);
            this.#ref.detectChanges();
        });

        image.addEventListener('zoom', event => {
            this.$zoom.update(zoom => ({
                ...zoom,
                value: (event as Cropper.ZoomEvent).detail.ratio * 100
            }));
            this.#movedOrResized = true;
            this.#ref.detectChanges();
        });

        image.addEventListener('cropstart', () => {
            this.#movedOrResized = true;
            this.#ref.detectChanges();
        });

        image.addEventListener('crop', () => {
            this.#evaluatePngConversionNeed();
        });

        this.$cropper.set(new Cropper(image, this.cropperOptions));
    }

    imageLoadError(): void {
        this.hasError.emit(true);
        this.disabled = true;
        this.$warningMessage.set('IMAGE_CROPPER.ERROR_LOADING_IMAGE');
        this.$loading.set(false);
    }

    fillCrop(): void {
        const cropper = this.$cropper();
        const cropData = cropper.getCropBoxData();
        const canvasData = cropper.getCanvasData();
        const ratioCanvas = canvasData.naturalHeight / canvasData.naturalWidth;

        const newHeight = ratioCanvas * cropData.width;
        const newWidth = cropData.height / ratioCanvas;
        if (newHeight >= cropData.height) {
            cropper.setCanvasData({ width: cropData.width + 1, height: newHeight });
        } else if (newWidth >= cropData.width) {
            cropper.setCanvasData({ height: cropData.height + 1, width: newWidth });
        }
        this.#centerCanvas();
    }

    fitCrop(): void {
        const cropper = this.$cropper();
        const cropData = cropper.getCropBoxData();
        const ratioCrop = cropData.height / cropData.width;
        const canvasData = cropper.getCanvasData();
        const ratioCanvas = canvasData.naturalHeight / canvasData.naturalWidth;

        if (ratioCanvas < ratioCrop) {
            const newHeight = ratioCanvas * cropData.width;
            cropper.setCanvasData({ width: cropData.width, height: newHeight });
        } else {
            const newWidth = cropData.height / ratioCanvas;
            cropper.setCanvasData({ height: cropData.height, width: newWidth });
        }
        this.#centerCanvas();
    }

    exportCanvas(altTextTouched: boolean = false): void {
        this.$loading.set(true);
        this.#ref.detectChanges();

        const cropper = this.$cropper();
        const imageData = cropper.getImageData();
        const cropData = cropper.getCropBoxData();

        const canvas = cropper.getCroppedCanvas({
            height: this.cropBoxData.height,
            width: this.cropBoxData.width,
            fillColor: this.#getFillColor(),
            imageSmoothingEnabled: true,
            imageSmoothingQuality: 'high',
            maxWidth: this.imageRestrictions?.maxWidth,
            maxHeight: this.imageRestrictions?.maxHeight
        });

        const exportResult = {
            dataUrl: this.file.data,
            size: this.file.size,
            width: canvas.width,
            height: canvas.height
        };

        if (altTextTouched || this.#imageTouched(imageData, cropData) || exportResult.size > this.imageRestrictions.size) {
            this.#compressImage(canvas, 1, compressedResult => {
                exportResult.dataUrl = compressedResult.dataUrl;
                exportResult.size = compressedResult.blob.size;
                this.$loading.set(false);
                this.export.emit(exportResult);
            });
        } else {
            console.log('Image not cropped or compressed');
            this.$loading.set(false);
            this.export.emit(exportResult);
        }
    }

    #changeFillColor(color: string): void {
        const cropperBox = document.getElementsByClassName('cropper-crop-box')[0];
        const style = cropperBox?.getAttribute('style') + `background-color:${color};`;
        cropperBox?.setAttribute('style', style);
    }

    #compressImage(canvas: HTMLCanvasElement, quality: number, callback: (result: { blob: Blob; dataUrl: string }) => void): void {
        canvas.toBlob(blob => {
            const newQuality = Math.round((quality - .01) * 100) / 100;
            if (blob.size > this.imageRestrictions.size && newQuality >= MIN_QUALITY && !this.convertToPng) {
                this.file.contentType = ImageType.jpeg;
                this.#compressImage(canvas, newQuality, callback);
            } else {
                callback({ blob, dataUrl: canvas.toDataURL(this.convertToPng ? ImageType.png : this.file.contentType, quality) });
            }
        }, this.file.contentType, quality);
    }

    #imageTouched(imageData: Cropper.ImageData, cropData: Cropper.CropBoxData): boolean {
        return this.#movedOrResized || imageData.scaleX === -1 || imageData.scaleY === -1 ||
            !!imageData.rotate || imageData.naturalWidth !== Math.round(cropData.width) ||
            imageData.naturalHeight !== Math.round(cropData.height);
    }

    #setDefaults(): void {
        const cropper = this.$cropper();
        cropper.reset();
        if (this.disabled) {
            cropper.setCropBoxData({ top: 0, left: 0, width: 0, height: 0 });
            cropper.disable();
        } else {
            const canvasData = cropper.getCanvasData();
            const cropBoxData = cropper.getCropBoxData();
            // Calcular el centro del marco
            const canvasCenterX = canvasData.left + canvasData.width / 2;
            const canvasCenterY = canvasData.top + canvasData.height / 2;
            // Calcular el centro de la area de recorte
            const cropBoxCenterX = cropBoxData.left + cropBoxData.width / 2;
            const cropBoxCenterY = cropBoxData.top + cropBoxData.height / 2;
            // Calcular la diferencia
            const deltaX = canvasCenterX - cropBoxCenterX;
            const deltaY = canvasCenterY - cropBoxCenterY;

            cropper.setCropBoxData({
                left: cropBoxData.left + deltaX,
                top: cropBoxData.top + deltaY
            });
            this.fitCrop();
        }
    }

    #centerCanvas(): void {
        const cropper = this.$cropper();
        const containerData = cropper.getContainerData();
        const canvasData = cropper.getCanvasData();
        cropper.setCanvasData({
            top: (containerData.height / 2 - canvasData.height / 2),
            left: (containerData.width / 2 - canvasData.width / 2)
        });
        this.#updateZoomSlider();
    }

    #updateZoomSlider(): void {
        const canvasData = this.$cropper().getCanvasData();
        this.$zoom.update(zoom => ({
            ...zoom,
            value: canvasData.width / canvasData.naturalWidth * 100
        }));
    }

    #getFillColor(): string {
        if (this.file.contentType === ImageType.png) {
            return this.fillColor.value;
        } else {
            return !this.fillColor.value || (this.fillColor.value === 'transparent' && !this.convertToPng) ?
                '#fff' : this.fillColor.value;
        }
    }

    #getSafeUrl(): SafeResourceUrl {
        if (this.file) {
            if (this.file.remote) {
                return this.#sanitizer.bypassSecurityTrustResourceUrl(this.file.data);
            } else {
                return this.#sanitizer.bypassSecurityTrustResourceUrl(`data:${this.file.contentType};base64,${this.file.data}`);
            }
        } else {
            return null;
        }
    }

    #evaluatePngConversionNeed(): void {
        if (!this.allowPngConversion || this.file.contentType !== ImageType.jpeg) return;
        this.convertToPng = this.#requiresTransparency();
    }

    #requiresTransparency(): boolean {
        if (this.fillColor.value !== 'transparent') return false;

        const cropper = this.$cropper();
        const crop = cropper.getCropBoxData();
        const canvas = cropper.getCanvasData();

        return canvas.left > crop.left ||
            canvas.top > crop.top ||
            canvas.left + canvas.width < crop.left + crop.width ||
            canvas.top + canvas.height < crop.top + crop.height;
    }

}
