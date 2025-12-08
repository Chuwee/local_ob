import { ImageUploaderComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { ObFile } from '@admin-clients/shared/data-access/models';
import { elementInfoSliderRestrictions, elementInfoSliderThumbnailRestrictions, VenueTemplateElementInfoContents, VenueTemplateElementInfoDetail, VenueTemplateElementInfoImage } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { FormBuilder, FormGroupDirective, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-edit-element-info-images',
    imports: [
        ImageUploaderComponent, TranslatePipe, FlexModule, MatTooltipModule, MatCheckboxModule,
        MatDividerModule, ReactiveFormsModule, TabsMenuComponent, TabDirective
    ],
    templateUrl: './edit-element-info-images.component.html',
    styleUrls: ['./edit-element-info-images.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditElementInfoImagesComponent {
    readonly #fb = inject(FormBuilder);
    readonly #form = inject(FormGroupDirective);
    #languages: string[];
    #originalImages: Record<string, VenueTemplateElementInfoImage[]>;

    enabledForm = this.#fb.control(true);
    langFormGroup = this.#fb.group({});
    readonly sliderRestrictions = elementInfoSliderRestrictions;

    @Input() set languages(langs: string[]) {
        this.#languages = langs;
        this.langFormGroup = this.#fb.group(langs.reduce((acc, lang) =>
        (acc[lang] = this.#fb.group({
            slider0: null,
            slider1: null,
            slider2: null,
            slider3: null,
            slider4: null
        }), acc), {}));

        this.#form.control.addControl(VenueTemplateElementInfoContents.imageList, this.langFormGroup, { emitEvent: false });
    }

    get languages(): string[] {
        return this.#languages;
    }

    @Output()
    readonly deleteContent = new EventEmitter<VenueTemplateElementInfoContents>();

    @Input() set defaultInfo(elementInfo: VenueTemplateElementInfoDetail) {
        const sliderImage = elementInfo?.default_info?.image_settings?.SLIDER;
        this.#originalImages = sliderImage?.images;
        if (sliderImage) {
            this.setImageData(sliderImage);
        }
    }

    async getValue(): Promise<{ enabled: boolean; items: Record<string, VenueTemplateElementInfoImage[]>; itemsToDelete: VenueTemplateElementInfoImage[] }> {
        const items = {} as Record<string, VenueTemplateElementInfoImage[]>;
        const itemsToDelete = [] as VenueTemplateElementInfoImage[];
        for (const lang of this.languages) {
            const images = [] as VenueTemplateElementInfoImage[];
            for (let position = 0; position < 5; position++) {
                const field = this.langFormGroup.controls[lang].get(`slider${position}`);
                if (field.dirty) {
                    const image = {
                        position
                    } as VenueTemplateElementInfoImage;
                    if (field.value) {
                        image.image = field.value.data;
                        image.alt_text = field.value.altText;
                        image.thumbnail = await this.createThumbnail(field.value, elementInfoSliderThumbnailRestrictions);
                        images.push(image);
                    } else {
                        image.type = 'SLIDER';
                        image.language = lang;
                        itemsToDelete.push(image);
                    }
                }
            }
            if (images.length) {
                items[lang] = images;
            }
        }
        return { enabled: !!this.enabledForm.value, items, itemsToDelete };
    }

    getDeletedImages(): VenueTemplateElementInfoImage[] {
        const itemsToDelete = [] as VenueTemplateElementInfoImage[];
        Object.keys(this.#originalImages)?.forEach(lang => {
            itemsToDelete.push({
                type: 'SLIDER',
                language: lang
            });
        });
        return itemsToDelete;
    }

    deleteContentComponent(): void {
        this.deleteContent.emit(VenueTemplateElementInfoContents.imageList);
    }

    private createThumbnail(sourceImage: ObFile, dimensions: { width: number; height: number }): Promise<string> {
        return new Promise(((resolve, reject) => {
            const img = new Image();
            img.src = `data:${sourceImage.contentType};base64,${sourceImage.data}`;
            img.onload = () => {
                const canvas = document.createElement('canvas');
                const ctx = canvas.getContext('2d');
                canvas.width = dimensions.width;
                canvas.height = dimensions.height;
                ctx.drawImage(img, 0, 0, img.width, img.height, 0, 0, dimensions.width, dimensions.height);
                const result = canvas.toDataURL();
                canvas.remove();
                resolve(result.split(',')[1]);
            };
            img.onerror = () => {
                reject('');
            };
        }));
    }

    private setImageData(sliderImage: { enabled: boolean; images?: Record<string, VenueTemplateElementInfoImage[]> }): void {
        this.enabledForm.patchValue(sliderImage.enabled);
        this.langFormGroup.reset();
        this.languages.forEach(lang => {
            sliderImage.images?.[lang]?.forEach(image => {
                let imgUrl = null;
                let altText = null;
                if (image?.image) {
                    const url = new URL(image.image);
                    url.searchParams.set('cache', Math.random().toString());
                    imgUrl = url.toString();
                    altText = image.alt_text;
                    this.langFormGroup.controls[lang].get(`slider${image.position}`).setValue({ data: imgUrl, altText });
                } else {
                    this.langFormGroup.controls[lang].get(`slider${image.position}`).setValue(null);
                }

            });
        });
    }
}
