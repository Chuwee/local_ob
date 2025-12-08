import { ObMatDialogConfig, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { urlValidator } from '@admin-clients/shared/utility/utils';
import {
    FeatureAction,
    FeatureElement, FeatureType,
    VenueTemplateElementInfoContents, VenueTemplateElementInfoDetail
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, Input, inject, Output, EventEmitter, viewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormArray, FormBuilder, FormGroup, FormGroupDirective, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import {
    EditElementFeaturesLinkModalComponent
} from '../edit-element-features-link-modal/edit-element-features-link-modal.component';

@Component({
    selector: 'app-edit-element-features-list',
    imports: [
        TabsMenuComponent, MaterialModule, TranslatePipe, TabDirective,
        ReactiveFormsModule, FlexLayoutModule, MatDialogModule
    ],
    templateUrl: './edit-element-features-list.component.html',
    styleUrls: ['./edit-element-features-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditElementFeaturesListComponent {
    readonly #fb = inject(FormBuilder);
    readonly #form = inject(FormGroupDirective);
    readonly #matDialog = inject(MatDialog);
    #languages: string[];

    private readonly _textsTabs = viewChild<TabsMenuComponent>('textsTabs');
    enabledForm = this.#fb.control(true);
    langFormGroup = this.#fb.group({});

    @Input() set languages(langs: string[]) {
        this.#languages = langs;
        this.langFormGroup = this.#fb.group(langs.reduce((acc, lang) =>
            (acc[lang] = this.#fb.array([this.createFormGroup()]), acc), {}));
        this.#form.control.addControl(VenueTemplateElementInfoContents.featuresList, this.langFormGroup, { emitEvent: false });
    }

    get languages(): string[] {
        return this.#languages;
    }

    @Output()
    readonly deleteContent = new EventEmitter<VenueTemplateElementInfoContents>();

    @Input() set defaultInfo(elementInfo: VenueTemplateElementInfoDetail) {
        if (elementInfo?.default_info?.feature_list) {
            this.languages.forEach(lang => {
                this.langFormGroup.controls[lang]?.clear();
                elementInfo.default_info.feature_list?.[lang]?.forEach(feature => {
                    this.langFormGroup.controls[lang].push(this.createFormGroup(feature));
                });
            });
        }
    }

    deleteContentComponent(): void {
        this.deleteContent.emit(VenueTemplateElementInfoContents.featuresList);
    }

    createNewLink(): void {
        if (!this.isNewLinkBtnDisabled()) {
            this.languages.forEach(lang => {
                (this.langFormGroup.get(lang) as FormArray)
                    .push(this.createFormGroup());
            });
        }
    }

    deleteLink(index: number): void {
        this.languages.forEach(lang => {
            (this.langFormGroup.get(lang) as FormArray).removeAt(index);
        });
    }

    openLinkModal(index: number, lang: string): void {
        const feature = (this.langFormGroup.get(lang) as FormArray).at(index);
        this.#matDialog.open(
            EditElementFeaturesLinkModalComponent,
            new ObMatDialogConfig({ feature })
        ).beforeClosed().subscribe(() => feature.patchValue(feature.value));
    }

    isNewLinkBtnDisabled(): boolean {
        return this.languages.some(lang => (this.langFormGroup?.get(lang) as FormArray)?.length > 9);
    }

    showValidationErrors(): void {
        // change language tab if invalid fields found
        const textsGroup = this.langFormGroup;
        if (textsGroup.invalid) {
            this._textsTabs().goToInvalidCtrlTab();
        }
    }

    getValue(): Record<string, FeatureElement[]> {
        return this.langFormGroup.value;
    }

    private createFormGroup(val?: FeatureElement): FormGroup {
        const newFg = this.#fb.group({
            type: FeatureType.text,
            text: ['', Validators.required],
            url: [{ value: null, disabled: true }, [Validators.required, urlValidator()]],
            action: [{ value: FeatureAction.newTab, disabled: true }, Validators.required]
        });
        if (val) {
            if (val.type === FeatureType.link) {
                newFg.controls.url.enable();
                newFg.controls.action.enable();
            }
            newFg.patchValue(val);
        }
        return newFg;
    }
}
