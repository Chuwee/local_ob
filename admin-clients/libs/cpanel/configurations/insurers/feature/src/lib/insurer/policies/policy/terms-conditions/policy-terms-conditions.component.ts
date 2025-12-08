import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { CustomAssetElement } from '@admin-clients/cpanel/channels/data-access';
import { InsurersService, PolicyTermsConditions } from '@admin-clients/cpanel-configurations-insurers-data-access';
import { LanguageBarComponent, MessageDialogService, RichTextAreaComponent, EphemeralMessageService, EmptyStateTinyComponent, DialogSize }
    from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, htmlContentMaxLengthValidator } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, inject, signal, ViewChild, OnDestroy } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatButtonToggle, MatButtonToggleGroup } from '@angular/material/button-toggle';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatError, MatFormField, MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput, MatInputModule } from '@angular/material/input';
import { MatList, MatListItem } from '@angular/material/list';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable, of, switchMap, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-policy-terms-conditions',
    imports: [
        TranslatePipe, MatProgressSpinner, MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle,
        FormContainerComponent, MatFormField, CommonModule, MatInputModule, ReactiveFormsModule, MatFormFieldModule, RichTextAreaComponent,
        LanguageBarComponent, MatButtonToggle, MatButtonToggleGroup, MatError, FormControlErrorsComponent,
        EmptyStateTinyComponent, MatIcon, MatButton, MatInput, MatList, MatListItem, MatTooltip, MatIconButton,
        MatTableModule
    ],
    templateUrl: './policy-terms-conditions.component.html',
    styleUrls: ['./policy-terms-conditions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PolicyTermsAndConditionsComponent implements WritingComponent, OnDestroy {
    @ViewChild('fileUpload') fileUpload: ElementRef;

    readonly #insurerSrv = inject(InsurersService);
    readonly #fb = inject(FormBuilder);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);

    readonly #$selectedLanguage = signal<string>(null);
    readonly selectedLanguage$ = toObservable(this.#$selectedLanguage);

    readonly form = this.#fb.group({
        contentValues: this.#fb.group({})
    });

    readonly selectedComContent = this.#fb.control('agreement_text');
    readonly selectedMailContent = this.#fb.control('subject_mail_template');

    uploadedFileAsset: CustomAssetElement;

    readonly $policy = toSignal(this.#insurerSrv.policy.get$().pipe(filter(Boolean)));

    readonly $policyTermsConditions = toSignal(this.#insurerSrv.policyTermsConditions.getData$().pipe(
        filter(Boolean),
        tap(termsConditions => {
            this.uploadedFileAsset = null;
            const contentValuesGroup = this.#fb.group({});

            termsConditions.forEach(tc => {
                contentValuesGroup.addControl(tc.lang, this.createFormGroup(tc));
            });

            this.form.setControl('contentValues', contentValuesGroup);

            if (termsConditions.length > 0) {
                const selectedTermCondition = this.#$selectedLanguage() ?
                    termsConditions.find(tc => tc.lang === this.#$selectedLanguage())?.id : termsConditions[0].id;
                this.#insurerSrv.policyTermsConditionsFile.load(this.$policy().insurer_id, this.$policy()?.id, selectedTermCondition);
            }

        })
    ));

    readonly languageList$ = this.#insurerSrv.policyTermsConditions.getData$().pipe(filter(Boolean),
        map(tc => {
            const languages = tc.map(el => el.lang);
            if (!languages.includes(this.#$selectedLanguage())) {
                this.#$selectedLanguage.set(languages[0]);
            }
            return languages;
        })
    );

    readonly $isLoading = toSignal(booleanOrMerge([
        this.#insurerSrv.policyTermsConditions.loading$(),
        this.#insurerSrv.policyTermsConditionsFile.inProgress$()
    ]));

    readonly $fileUrl = toSignal(this.#insurerSrv.policyTermsConditionsFile.get$().pipe(filter(Boolean),
        tap(file => this.form.get(['contentValues', this.#$selectedLanguage(), 'file_url']).setValue(file))
    ));

    ngOnDestroy(): void {
        this.#insurerSrv.policyTermsConditionsFile.clear();
    }

    createFormGroup(data?: PolicyTermsConditions): FormGroup {
        return this.#fb.group({
            id: this.#fb.control(data?.id),
            agreement_text: this.#fb.control(data?.agreement_text ?? '', htmlContentMaxLengthValidator(13000)),
            privacy_policy_text: this.#fb.control(data?.privacy_policy_text ?? '', htmlContentMaxLengthValidator(2500)),
            subject_mail_template: this.#fb.control(data?.subject_mail_template ?? ''),
            mail_template: this.#fb.control(data?.mail_template ?? '', htmlContentMaxLengthValidator(3000)),
            file: this.#fb.control(data?.file),
            file_url: this.#fb.control(null)
        });
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    changeLanguage(newLanguage: string): void {
        this.cancel();
        this.#$selectedLanguage.set(newLanguage);
    }

    loadTermsConditions(): void {
        this.#insurerSrv.policyTermsConditions.load(this.$policy().insurer_id, this.$policy().id);
    }

    cancel(): void {
        this.loadTermsConditions();
        this.form.markAsPristine();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.loadTermsConditions();
            this.#ephemeralSrv.showSaveSuccess();
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {

            const values = this.form.controls.contentValues.controls[this.#$selectedLanguage()].value;

            const payload = {
                agreement_text: values.agreement_text,
                privacy_policy_text: values.privacy_policy_text,
                subject_mail_template: values.subject_mail_template,
                mail_template: values.mail_template
            };

            return this.#insurerSrv.policyTermsConditions.update(this.$policy().insurer_id, this.$policy().id, values.id, payload)
                .pipe(tap(() => this.form.markAsPristine()),
                    switchMap(() => {
                        if (this.uploadedFileAsset) {

                            const payload = {
                                file_name: this.uploadedFileAsset.filename,
                                file_content: this.uploadedFileAsset.binary
                            };

                            return this.#insurerSrv.policyTermsConditionsFile.
                                update(this.$policy().insurer_id, this.$policy().id, values.id, payload
                                );
                        } else {
                            return of(null);

                        }
                    })
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    importFile(event): void {
        const file = event.target.files?.[0];

        if (file.type !== 'application/pdf') {
            this.#messageDialogService.showAlert({
                size: DialogSize.SMALL,
                title: 'INSURERS.FORMS.POLICY.TERMS_CONDITIONS.FILE_ERROR',
                message: 'INSURERS.FORMS.POLICY.TERMS_CONDITIONS.FILE_FORMAT_ERROR'
            });
        } else {
            this.readFile(file).then(asset => {
                this.uploadedFileAsset = asset;
            });
            this.form.get(['contentValues', this.#$selectedLanguage(), 'file']).setValue(file.name);
            this.form.get(['contentValues', this.#$selectedLanguage(), 'file_url']).reset();
            this.form.markAsPristine();
            this.form.markAsDirty();
        }

    }

    edit(): void {
        this.fileUpload.nativeElement.value = '';
        this.fileUpload.nativeElement.click();
    }

    openUrl(url: string): void {
        if (url) {
            window.open(url, '_blank');
        }
    }

    private readFile(element): Promise<CustomAssetElement> {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(element);
            reader.onload = () => {
                resolve({
                    filename: element.name, binary: reader.result.toString().replace(/^.+?;base64,/, '')
                });
            };
            reader.onerror = () => {
                reject(new Error('Unable to read..'));
            };
        });
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this.#messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
