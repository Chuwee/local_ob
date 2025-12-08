import { ImageUploaderComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { elementInfoHighlightedImageRestrictions, VenueTemplateElementInfoContents, VenueTemplateElementInfoDetail, VenueTemplateElementInfoImage } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { FormBuilder, FormGroupDirective, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatListModule } from '@angular/material/list';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-edit-element-info-highlighted-image',
    imports: [
        FlexModule, TranslatePipe, MatTooltipModule, MatCheckboxModule, ReactiveFormsModule, MatListModule,
        TabsMenuComponent, TabDirective, ImageUploaderComponent
    ],
    templateUrl: './edit-element-info-highlighted-image.component.html',
    styleUrls: ['./edit-element-info-highlighted-image.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditElementInfoHighlightedImageComponent {
    readonly #fb = inject(FormBuilder);
    readonly #form = inject(FormGroupDirective);
    #languages: string[];
    #originalImages: Record<string, VenueTemplateElementInfoImage[]>;

    enabledForm = this.#fb.control(true);
    langFormGroup = this.#fb.group({});
    readonly highlightedImageRestrictions = elementInfoHighlightedImageRestrictions;

    @Input() set languages(langs: string[]) {
        this.#languages = langs;
        this.langFormGroup = this.#fb.group(langs.reduce((acc, lang) =>
        (acc[lang] = this.#fb.group({
            highlightedImage: null
        }), acc), {}));

        this.#form.control.addControl(VenueTemplateElementInfoContents.highlightedImage, this.langFormGroup, { emitEvent: false });
    }

    get languages(): string[] {
        return this.#languages;
    }

    @Output()
    readonly deleteContent = new EventEmitter<VenueTemplateElementInfoContents>();

    @Input() set defaultInfo(elementInfo: VenueTemplateElementInfoDetail) {
        const highlightedImage = elementInfo?.default_info?.image_settings?.HIGHLIGHTED;
        this.#originalImages = highlightedImage?.images;
        if (highlightedImage) {
            this.setImageData(highlightedImage);
        }
    }

    getValue(): { enabled: boolean; items: Record<string, VenueTemplateElementInfoImage[]>; itemsToDelete: VenueTemplateElementInfoImage[] } {
        const items = {} as Record<string, VenueTemplateElementInfoImage[]>;
        const itemsToDelete = [] as VenueTemplateElementInfoImage[];
        for (const language of this.languages) {
            const field = this.langFormGroup.controls[language].get('highlightedImage');
            if (field.dirty) {
                if (field.value) {
                    items[language] = [{ image: field.value.data, alt_text: field.value.altText }];
                } else {
                    itemsToDelete.push({ type: 'HIGHLIGHTED', language });
                }
            }
        }
        return { enabled: !!this.enabledForm.value, items, itemsToDelete };
    }

    getDeletedImages(): VenueTemplateElementInfoImage[] {
        const itemsToDelete = [] as VenueTemplateElementInfoImage[];
        Object.keys(this.#originalImages)?.forEach(lang => {
            itemsToDelete.push({
                type: 'HIGHLIGHTED',
                language: lang
            });
        });
        return itemsToDelete;
    }

    deleteContentComponent(): void {
        this.deleteContent.emit(VenueTemplateElementInfoContents.highlightedImage);
    }

    private setImageData(highlightedImage: { enabled: boolean; images?: Record<string, VenueTemplateElementInfoImage[]> }): void {
        this.enabledForm.patchValue(highlightedImage.enabled);
        this.langFormGroup.reset();
        this.languages.forEach(lang => {
            let imgUrl = null;
            let altText = null;
            const imageData = highlightedImage.images?.[lang]?.[0];
            if (imageData?.image) {
                const url = new URL(imageData.image);
                url.searchParams.set('cache', Math.random().toString());
                altText = imageData.alt_text;
                imgUrl = url.toString();
                this.langFormGroup.controls[lang].get('highlightedImage').setValue({ data: imgUrl, altText });
            } else {
                this.langFormGroup.controls[lang].get('highlightedImage').setValue(null);
            }
        });
    }
}
