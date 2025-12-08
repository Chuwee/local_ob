import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { PRESALES_SERVICE, PresalesRedirectionPolicy, RedirectionPolicyMode } from '@admin-clients/cpanel/shared/data-access';
import { EphemeralMessageService, ObMatDialogConfig, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { urlValidator } from '@admin-clients/shared/utility/utils';
import { UpperCasePipe } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef,
    inject, OnInit, viewChild, viewChildren
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionPanel } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, Observable, throwError, tap, combineLatest, map } from 'rxjs';
import { PresaleLiteralsDialogComponent } from '../presale-literals-dialog/presale-literals-dialog.component';

@Component({
    selector: 'app-presale-redirection-policy',
    templateUrl: './presale-redirection-policy.component.html',
    imports: [TranslatePipe, ReactiveFormsModule, MatRadioGroup, MatRadioButton, UpperCasePipe, MatFormFieldModule, MatInputModule,
        TabsMenuComponent, TabDirective, MatIcon, PrefixPipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PresaleRedirectionPolicyComponent implements OnInit, AfterViewInit {

    private _$textTabs = viewChild('textsTabs', { read: TabsMenuComponent });
    private _$matExpansionPanelQueryList = viewChildren(MatExpansionPanel);

    readonly #presalesSrv = inject(PRESALES_SERVICE);
    readonly #matDialog = inject(MatDialog);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    readonly form = this.#fb.group({
        mode: ['CATALOG' as RedirectionPolicyMode, Validators.required],
        value: this.#fb.group({})
    });

    readonly $redirectionPolicy = toSignal(this.#presalesSrv.getRedirectionPolicy$());
    readonly $languages = toSignal(this.#presalesSrv.getLanguages$());

    ngOnInit(): void {
        this.#initializeForm();
        this.#handleFormModeChanges();
    }

    ngAfterViewInit(): void {
        if (this.form.controls.mode.value === 'CUSTOM' && this.$languages()?.selected.length > 1) {
            this.navigateToDefaultLanguageTab();
        }
    }

    navigateToDefaultLanguageTab(): void {
        if (!this._$textTabs()) return;

        const defaultLang = this.$languages().default;
        const selectedLang = this.$languages().selected;
        const index = selectedLang.indexOf(defaultLang);

        if (index !== -1 && this._$textTabs()) {
            this._$textTabs().selectedIndex = index;
        }
    }

    #initializeForm(): void {
        combineLatest([
            this.#presalesSrv.getLanguages$().pipe(filter(Boolean)),
            this.#presalesSrv.getRedirectionPolicy$().pipe(
                map(policy => policy || { mode: 'CATALOG' as RedirectionPolicyMode, value: {} })
            )
        ]).pipe(
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([languages, redirectionPolicy]) => {
            const { selected: selectedLang, default: defaultLang } = languages;

            if (!this.form.controls.value) {
                this.form.addControl('value', this.#fb.group({}));
            }

            this.form.patchValue({
                mode: redirectionPolicy.mode
            });

            selectedLang.forEach(language => {
                const valueFormGroup = this.form.controls.value as FormGroup;
                const valueControl = valueFormGroup.controls[language];
                if (valueControl) {
                    valueControl.setValue(redirectionPolicy.value?.[language] || null);
                } else {
                    valueFormGroup.addControl(
                        language,
                        this.#fb.control(redirectionPolicy.value?.[language] || '', [
                            urlValidator(),
                            ...(language === defaultLang ? [Validators.required] : [])
                        ])
                    );
                }
            });

            this.form.markAsPristine();
        });
    }

    #handleFormModeChanges(): void {
        this.form.controls.mode?.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(value => {
                if (value === 'CATALOG') {
                    this.form.controls.value?.disable();
                } else {
                    this.form.controls.value?.enable();
                }
            });
    }

    cancel(): void {
        this.form.patchValue(this.$redirectionPolicy());
        this.form.markAsPristine();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.form.markAsPristine();
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const policy: PresalesRedirectionPolicy = {
                mode: this.form.value.mode,
                value: this.form.value.mode === 'CUSTOM' ? this.#removeBlankAttributes(this.form.value.value) : null
            };
            return this.#presalesSrv.updateRedirectionPolicy(policy).pipe(
                tap(() => {
                    this.#presalesSrv.loadRedirectionPolicy();
                    this.#ephemeralSrv.showSaveSuccess();
                })
            );
        } else {
            this.form.markAllAsTouched();
            if (this.$languages()?.selected.length > 1) {
                this._$textTabs().goToInvalidCtrlTab();
            }
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._$matExpansionPanelQueryList());
            return throwError(() => 'invalid form');
        }
    }

    #removeBlankAttributes(obj: Record<string, string>): Record<string, string> {
        const result = {};
        for (const key in obj) {
            if (obj[key] !== null && obj[key] !== undefined && obj[key] !== '') {
                result[key] = obj[key];
            }
        }
        return result;
    }

    async openLiteralsDialog(): Promise<void> {
        this.#matDialog.open(PresaleLiteralsDialogComponent, new ObMatDialogConfig());
    }

}
