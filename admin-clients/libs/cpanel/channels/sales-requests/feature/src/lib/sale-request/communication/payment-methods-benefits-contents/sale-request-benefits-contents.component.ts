import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    benefitsContentBadgeType,
    defaultBackgroundColor,
    defaultTextColor,
    SaleRequestBenefitsContentBadge,
    SalesRequestsService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import {
    MessageDialogService, EphemeralMessageService, LanguageBarComponent, ColorPickerComponent
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, of, throwError } from 'rxjs';
import { filter, map, take, tap } from 'rxjs/operators';

@Component({
    selector: 'app-sale-request-benefits-contents',
    templateUrl: './sale-request-benefits-contents.component.html',
    styleUrls: ['./sale-request-benefits-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, LanguageBarComponent, ReactiveFormsModule, MatProgressSpinner, TranslatePipe, MatFormField, MatInput,
        ColorPickerComponent, MatLabel, MatError, FormControlErrorsComponent, MatIcon, MatCheckbox
    ]
})
export class SaleRequestPaymentMethodsBenefitsContentsComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #salesRequestsService = inject(SalesRequestsService);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #destroyRef = inject(DestroyRef);

    readonly form = this.#fb.nonNullable.group({
        visible: [false as boolean, [Validators.required]],
        backgroundColor: [{ value: defaultBackgroundColor, disabled: true }, [Validators.required]],
        textColor: [{ value: defaultTextColor, disabled: true }, [Validators.required]],
        label: [{ value: null as string, disabled: true }, [Validators.required, Validators.maxLength(40)]]
    });

    #salesRequestId: number;

    readonly $isLoading = toSignal(this.#salesRequestsService.paymentMethodsBenefitsContents.inProgress$());
    readonly $language = signal<string>(null);
    readonly $languageList = toSignal(this.#salesRequestsService.getSaleRequest$()
        .pipe(
            map(saleRequest => saleRequest.languages),
            tap(languages => {
                if (!languages?.default && languages?.selected.length) {
                    languages.default = languages.selected[0];
                }
                this.$language.set(languages?.default);
            }),
            map(languages => languages?.selected)
        ));

    readonly formData$ = combineLatest([
        toObservable(this.$language),
        this.#salesRequestsService.paymentMethodsBenefitsContents.get$()
    ]);

    readonly canChangeLanguage: (() => Observable<boolean>) = () => this.#validateIfCanChangeLanguage();

    ngOnInit(): void {
        this.#salesRequestsService.getSaleRequest$()
            .pipe(
                take(1),
                filter(Boolean)
            ).subscribe(saleRequest => {
                this.#salesRequestId = saleRequest.id;
                this.#salesRequestsService.paymentMethodsBenefitsContents.load(this.#salesRequestId);
            });

        this.#refreshFormDataHandler();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.form.markAsPristine();
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const contentsToSave: SaleRequestBenefitsContentBadge[] = [];
            const visible = this.form.get('visible');
            const backgroundColor = this.form.get('backgroundColor');
            const textColor = this.form.get('textColor');
            const label = this.form.get('label');
            if (visible.dirty || backgroundColor.dirty || textColor.dirty || label.dirty) {
                contentsToSave.push({
                    visible: visible.value,
                    language: this.$language(),
                    type: benefitsContentBadgeType,
                    value: label.value,
                    backgroundColor: backgroundColor.value,
                    textColor: textColor.value
                });
            }
            if (contentsToSave.length > 0) {
                return this.#salesRequestsService.paymentMethodsBenefitsContents.update(this.#salesRequestId, contentsToSave).pipe(
                    tap(() => {
                        this.#salesRequestsService.paymentMethodsBenefitsContents.load(this.#salesRequestId);
                        this.#ephemeralMessage.showSaveSuccess();
                    }));
            }
            return throwError(() => 'nothing to save');
        } else {
            FormControlHandler.markAllControlsAsTouched(this.form);
            return throwError(() => 'invalid form');
        }
    }

    enableBenefitsCommunicationElement(checked: boolean): void {
        if (checked) {
            this.form.controls.backgroundColor.enable({ emitEvent: false });
            this.form.controls.textColor.enable({ emitEvent: false });
            this.form.controls.label.enable({ emitEvent: false });
        } else {
            this.form.controls.backgroundColor.disable({ emitEvent: false });
            this.form.controls.textColor.disable({ emitEvent: false });
            this.form.controls.label.disable({ emitEvent: false });
        }
    }

    changeLanguage(newLanguage: string): void {
        this.$language.set(newLanguage);
        this.#refreshFormDataHandler();
        this.form.markAsPristine();
    }

    cancel(): void {
        this.#salesRequestsService.paymentMethodsBenefitsContents.load(this.#salesRequestId);
        this.form.markAsPristine();
    }

    #refreshFormDataHandler(): void {
        this.formData$
            .pipe(
                filter(([language, paymentMethodsBenefitsContents]) => !!language && !!paymentMethodsBenefitsContents),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(([language, paymentMethodsBenefitsContents]) => {
                for (const content of paymentMethodsBenefitsContents) {
                    if (content.language === language) {
                        this.form.controls.visible.setValue(content.visible ?? false);
                        this.form.controls.backgroundColor.setValue(content.backgroundColor ?? defaultBackgroundColor);
                        this.form.controls.textColor.setValue(content.textColor ?? defaultTextColor);
                        this.form.controls.label.setValue(content.value ?? null);
                        this.enableBenefitsCommunicationElement(content.visible ?? false);
                        return;
                    }
                }
                this.form.controls.visible.reset(false);
                this.form.controls.backgroundColor.reset(defaultBackgroundColor);
                this.form.controls.textColor.reset(defaultTextColor);
                this.form.controls.label.reset(null);
                this.enableBenefitsCommunicationElement(false);
            });
    }

    #validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this.#messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
