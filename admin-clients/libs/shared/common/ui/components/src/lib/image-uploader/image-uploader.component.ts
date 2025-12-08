import { ImageRestrictions, ObFile } from '@admin-clients/shared/data-access/models';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { NgStyle } from '@angular/common';
import {
    booleanAttribute, ChangeDetectionStrategy, ChangeDetectorRef, Component,
    ElementRef, HostListener, inject, input, OnInit, Self, signal, untracked, viewChild
} from '@angular/core';
import { ControlValueAccessor, NgControl } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatError } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter } from 'rxjs/operators';
import { DialogSize } from '../dialog/models/dialog-size.enum';
import { EphemeralMessageService } from '../ephemeral-message/ephemeral-message.service';
import {
    ImageCropperDialogComponent, ImageCropperDialogData, PNG_WITH_TRANSPARENCY_SIZE_LIMIT
} from '../image-cropper/image-cropper-dialog.component';
import { MessageDialogService } from '../message-dialog/message-dialog.service';
import { ObMatDialogConfig } from '../message-dialog/models/message-dialog.model';
import { MessageType } from '../models/message-type.model';
import { Previewer } from '../previewer/previewer';

@Component({
    imports: [MatIconButton, MatError, MatIcon, MatTooltip, TranslatePipe, NgStyle],
    selector: 'app-image-uploader',
    templateUrl: './image-uploader.component.html',
    styleUrls: ['./image-uploader.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImageUploaderComponent implements OnInit, ControlValueAccessor {
    // Services
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #dialog = inject(MatDialog);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #previewer = inject(Previewer);

    // Private signals
    readonly #$disabled = signal(false);
    readonly #$hideLabelText = signal(false);
    readonly #$provisionalFile = signal<ObFile | null>(null);
    readonly #$allowSvg = signal(false);

    // Private variables
    #onChange: (file: ObFile | null) => void;
    #onTouch: () => void;

    // Disabled
    readonly $disabled = input<boolean, boolean>(null, {
        transform: val => {
            const result = coerceBooleanProperty(val);
            this.#$disabled.set(result);
            return result;
        }, alias: 'disabled'
    });

    get disabled(): boolean {
        return this.#$disabled();
    }

    // Hide label text
    readonly $hideLabelText = input<boolean, boolean>(null, {
        transform: val => {
            const result = booleanAttribute(val);
            this.#$hideLabelText.set(result);
            return result;
        }, alias: 'hideLabelText'
    });

    get hideLabelText(): boolean {
        return this.#$hideLabelText();
    }

    // Allow SVG
    readonly $allowSvg = input<boolean, boolean>(null, {
        transform: val => {
            const result = coerceBooleanProperty(val);
            this.#$allowSvg.set(result);
            return result;
        }, alias: 'allowSvg'
    });

    get allowSvg(): boolean {
        return this.#$allowSvg();
    }

    // Signal Inputs and ViewChilds
    readonly $imageRestrictions = input<ImageRestrictions>(null, { alias: 'imageRestrictions' });
    readonly $label = input<string>(null, { alias: 'label' });
    readonly $placeholder = input<string>(null, { alias: 'placeholder' });
    readonly $previewerScaleFactor = input(1, { alias: 'previewerScaleFactor' });
    readonly $hasAltText = input<boolean, boolean>(false, { alias: 'hasAltText', transform: booleanAttribute });
    readonly $showDeleteSuccessMessage = input<boolean, boolean>(true, { alias: 'showDeleteSuccessMessage', transform: booleanAttribute });
    readonly $allowPngConversion = input<boolean, boolean>(false, { alias: 'allowPngConversion', transform: booleanAttribute });
    readonly $iconTooltip = input<string>('UPLOAD_IMAGE.TOOLTIP', { alias: 'iconTooltip' });
    readonly $fileUpload = viewChild<ElementRef>('fileUpload');

    // Signals
    readonly $selected = signal<boolean | null>(null);
    readonly $file = signal<ObFile | null>(null);
    readonly $reader = signal(new FileReader());
    readonly $defaultPlaceholder = signal('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HA' +
        'wCAAAAC0lEQVR42mNgYAAAAAMAASsJTYQAAAAASUVORK5CYII=');

    constructor(
        @Self() public ngControl: NgControl
    ) {
        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }
    }

    ngOnInit(): void {
        this.#onFileLoad();
        this.#onFileLoadError();
    }

    setImageError(error: string): void {
        this.ngControl.control.setErrors({ [error]: true });
    }

    writeValue(obj: string | ObFile): void {
        untracked(() => {
            // If the value is null, delete the file
            if (!obj) {
                this.#deleteFile();
                return;
            }
            // If the value is a string, generate an ObFile from the url and save it
            // This case happens when receiving a url from the backend
            const valueIsString = typeof obj === 'string';
            if (valueIsString) {
                this.$file.set(this.#generateObFileFromUrl(obj));
            } else {
                // If the value is an ObFile, check if it is incomplete (only data and altText)
                const incompleteFile = Object.keys(obj).length === 2;

                // If the file is incomplete, means that we are receiving the image url and the alt text from the backend
                if (incompleteFile) {
                    this.$file.set({
                        ...this.#generateObFileFromUrl(obj.data),
                        altText: obj.altText
                    });
                } else {
                    // The component has generated a complete ObFile
                    this.$file.set(obj);
                }
            }
        });
        this.#ref.detectChanges();
    }

    registerOnChange(fn: (file: ObFile | null) => void): void {
        this.#onChange = fn;
    }

    registerOnTouched(fn: () => void): void {
        this.#onTouch = fn;
    }

    setDisabledState?(isDisabled: boolean): void {
        this.#$disabled.set(isDisabled);
        this.#ref.markForCheck();
    }

    @HostListener('cancel', ['$event'])
    onCancel(event: Event): void {
        event.stopImmediatePropagation();
    }

    @HostListener('change', ['$event.target.files'])
    emitFiles(event: FileList): void {
        this.$selected.set(false);
        const file = event?.item(0);
        this.#$provisionalFile.set({
            name: file.name,
            size: file.size,
            contentType: file.type
        });
        this.$reader().readAsDataURL(file);
    }

    onClick(): void {
        if (!this.disabled) {
            this.$fileUpload()?.nativeElement.click();
        }
    }

    resolvePath(): string {
        if (this.$file()?.remote) {
            return this.$file()?.data;
        }
        return `data:${this.$file()?.contentType};base64,${this.$file()?.data}`;
    }

    openDelete(): void {
        this.$selected.set(true);
        this.#messageDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'UPLOAD_IMAGE.DELETE',
            message: 'TITLES.DELETE_IMAGE',
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    if (this.$showDeleteSuccessMessage()) {
                        this.#ephemeralMessageService.showSuccess({
                            msgKey: 'UPLOAD_IMAGE.DELETE_SUCCESS'
                        });
                    }
                    if (!this.$file()?.remote) {
                        this.ngControl.control.markAsPristine();
                        this.ngControl.control.setValue(null);
                    } else {
                        this.#onTouch();
                        this.#onChange(null);
                    }
                    this.#deleteFile();
                }
                this.$selected.set(false);
                this.#ref.markForCheck();
            });
    }

    preview(): void {
        this.#previewer.open(this.$file())
            .onClose(withEdition => {
                if (withEdition) {
                    this.edit();
                }
            });
    }

    edit(): void {
        this.#dialog.open<ImageCropperDialogComponent, ImageCropperDialogData, ObFile>(
            ImageCropperDialogComponent,
            new ObMatDialogConfig({
                file: this.$file(),
                hasAltText: this.$hasAltText(),
                imageRestrictions: this.$imageRestrictions(),
                allowPngConversion: this.$allowPngConversion()
            }))
            .afterClosed()
            .pipe(filter(Boolean))
            .subscribe(resultFile => this.#replaceFile(resultFile));
    }

    #onFileLoad(): void {
        this.$reader().onload = () => {
            const readerResult = this.$reader().result;
            if (typeof readerResult === 'string' && this.#$provisionalFile()) {
                this.#$provisionalFile().data = readerResult.split(',')[1];
                if ((this.allowSvg && this.#$provisionalFile()?.contentType.includes('svg'))
                    || this.#$provisionalFile()?.contentType.includes('ico')) {
                    this.#replaceFile(this.#$provisionalFile());
                    return;
                }
                this.#dialog.open<ImageCropperDialogComponent, ImageCropperDialogData, ObFile>(
                    ImageCropperDialogComponent,
                    new ObMatDialogConfig({
                        file: this.#$provisionalFile(),
                        hasAltText: this.$hasAltText(),
                        imageRestrictions: this.$imageRestrictions(),
                        allowPngConversion: this.$allowPngConversion()
                    }))
                    .afterClosed()
                    .subscribe(resultFile => {
                        if (resultFile) {
                            this.#replaceFile(resultFile);
                        } else {
                            this.#deleteFile();
                        }
                    });
            }
        };
    }

    #onFileLoadError(): void {
        this.$reader().onerror = () => {
            this.setImageError('imageLoadError');
            this.#ephemeralMessageService.show({
                type: MessageType.alert,
                msgKey: 'FILE_UPLOADER.ERROR'
            });
        };
    }

    #deleteFile(): void {
        this.$file.set(null);
        this.#$provisionalFile.set(null);
        if (this.$fileUpload()?.nativeElement) {
            this.$fileUpload().nativeElement.value = null;
        }
    }

    #replaceFile(newFile: ObFile): void {
        const restrictions = this.$imageRestrictions();
        const size = this.$allowPngConversion() ? PNG_WITH_TRANSPARENCY_SIZE_LIMIT : restrictions.size;

        if (newFile.size <= size) {
            this.#deleteFile();
            this.$file.set(newFile);
            this.#onTouch();
            this.#onChange(this.$file());
            this.#ref.markForCheck();
        } else {
            this.#messageDialogService.showAlert({
                size: DialogSize.SMALL,
                title: 'TITLES.ERROR_DIALOG',
                message: 'FORMS.ERRORS.MAX_SIZE_EXCEEDED',
                messageParams: { value: size * 0.001 }
            });
        }
    }

    #generateObFileFromUrl(url: string): ObFile {
        const name = url.replace(/^.*[\\/]/, '').split('?')[0].split('#')[0];
        const extension = name.split('.')[1];
        return {
            data: url,
            name,
            contentType: 'image/' + (extension === 'jpg' ? 'jpeg' : extension),
            remote: true
        };
    }
}
