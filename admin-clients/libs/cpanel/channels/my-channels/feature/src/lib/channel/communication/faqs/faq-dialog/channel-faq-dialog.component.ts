import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelFaqs, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { DialogSize, RichTextAreaComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { atLeastOneRequiredInFormGroup, htmlContentMaxLengthValidator } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, ViewChild, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule, MatHint } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, map, Observable, shareReplay, throwError } from 'rxjs';

@Component({
    selector: 'app-channel-faq-dialog',
    templateUrl: './channel-faq-dialog.component.html',
    styleUrls: ['./channel-faq-dialog.component.scss'],
    imports: [
        AsyncPipe, TranslatePipe, MatIconModule, ReactiveFormsModule, TabsMenuComponent, RichTextAreaComponent,
        MatFormFieldModule, MatHint, FormControlErrorsComponent, MatProgressSpinnerModule, FlexLayoutModule, MatDialogModule,
        MatButtonModule, MatCheckboxModule, MatInputModule, TabDirective
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelFaqDialogComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #channelsService = inject(ChannelsService);
    readonly #dialogRef = inject(MatDialogRef<ChannelFaqDialogComponent>);
    readonly #onDestroy = inject(DestroyRef);
    readonly #data = inject<
        { channelId: number; faqKey: number; categories: string[] }
    >(MAT_DIALOG_DATA);

    @ViewChild(TabsMenuComponent) private _languageTabs: TabsMenuComponent;

    readonly form = this.#fb.group({
        contents: this.#fb.group({}),
        categories: this.#fb.group(
            this.mapToKeyObject(this.#data.categories),
            { validators: [atLeastOneRequiredInFormGroup()] }
        )
    });

    readonly isNewFaq: boolean = !this.#data.faqKey;
    readonly isInProgress$: Observable<boolean> = this.#channelsService.faqs.loading$();
    readonly categories = this.#data.categories;
    readonly faq$ = this.#channelsService.faqs.get$();
    readonly languages$ = combineLatest([
        this.#channelsService.getChannel$(),
        this.faq$
    ]).pipe(
        map(([channel, faq]) => {
            const languages = channel.languages.selected;
            const languagesControls = this.#fb.group({});

            languages.forEach(language => {
                languagesControls.addControl(language, this.#fb.group({
                    title: [faq ? faq?.values[language]?.title : null, [Validators.required, htmlContentMaxLengthValidator(200)]],
                    content: [faq ? faq?.values[language]?.content : null, Validators.required]
                }));
            });
            this.form.setControl('contents', languagesControls);
            this.form.get('contents').markAsPristine();

            if (faq) {
                faq.tags.forEach(cat => {
                    this.$all().categories.find(category => category.name === cat).selected = true;
                    this.form.controls.categories.controls[cat].setValue(true);
                });
            }
            this.$all().selected = this.selectedCategories.every(c => c.selected);
            this.form.get('categories').markAsPristine();
            this.form.get('categories').markAsUntouched();

            return languages;
        }),
        takeUntilDestroyed(this.#onDestroy),
        shareReplay(1)
    );

    readonly selectedCategories = this.categories.map(category => ({ name: category, selected: false }));

    readonly $all = signal({
        selected: false,
        categories: this.selectedCategories
    });

    readonly $partiallySelected = computed(() => {
        const all = this.$all();
        if (!all.categories) {
            return false;
        }
        return all.categories.some(c => c.selected) && !all.categories.every(c => c.selected);
    });

    ngOnInit(): void {
        if (!this.isNewFaq) {
            this.#channelsService.faqs.load(this.#data.channelId, this.#data.faqKey);
        }
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    close(): void {
        this.#channelsService.faqs.clear();
        this.#dialogRef.close();
    }

    save(): void {
        if (this.categories.length === 1) {
            this.update(true);
        }
        if (this.form.valid) {
            const data = this.form.getRawValue().contents;
            const categories = this.categories.filter(cat => this.form.controls.categories.controls[cat].value === true);
            const faq: ChannelFaqs = {
                values: data,
                tags: categories
            };
            if (this.isNewFaq) {
                this.#channelsService.faqs.save(this.#data.channelId, faq).subscribe(() => {
                    this.#channelsService.faqs.clear();
                    this.#dialogRef.close(true);
                });
            } else {
                this.#channelsService.faqs.update(this.#data.channelId, this.#data.faqKey, faq).subscribe(() => {
                    this.#channelsService.faqs.clear();
                    this.#dialogRef.close(true);
                });
            }
        } else {
            console.log('form invalid');
            this.showValidationErrors();
            throwError(() => 'invalid form');
        }
    }

    update(selected: boolean, index?: number): any {
        this.$all.update(all => {
            if (index === undefined) {
                all.selected = selected;
                all.categories?.forEach(c => {
                    (c.selected = selected);
                    this.form.controls.categories.controls[c.name].setValue(selected);
                });
            } else {
                all.categories[index].selected = selected;
                all.selected = all.categories?.every(c => c.selected) ?? true;
            }
            return { ...all };
        });
    }

    private showValidationErrors(): void {
        // change langugage tab if invalid fields found
        if (this.form.controls.contents?.invalid) {
            this._languageTabs.goToInvalidCtrlTab();
        } else if (this.form.controls.categories.invalid) {
            this.form.controls.categories.markAsDirty();
            this.form.controls.categories.markAsTouched();
        }
    }

    private mapToKeyObject(data: string[], value = false): Record<number, boolean> {
        return data?.map(elem => elem)
            .reduce((acc, curr) => (acc[curr] = value, acc), {});
    }
}
