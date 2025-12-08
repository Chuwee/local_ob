import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    ChangeDetectionStrategy, Component, ElementRef, inject, Input, OnDestroy, OnInit
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatError } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject, takeUntil } from 'rxjs';
import { LanguageSelector } from './language-selector.model';

export * from './language-selector.model';

@Component({
    selector: 'app-language-selector',
    templateUrl: './language-selector.component.html',
    styleUrls: ['./language-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe,
        ReactiveFormsModule,
        MatCheckbox, MatIcon, MatError,
        FlexLayoutModule,
        FormControlErrorsComponent
    ]
})
export class LanguageSelectorComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(FormBuilder);
    private readonly _el = inject(ElementRef);

    private readonly _onDestroy = new Subject<void>();
    private _data: LanguageSelector;
    private _enableDefaultSelection = false;

    readonly languagesGroup = this._fb.record<boolean>({});
    readonly form = this._fb.group({
        languagesGroup: this.languagesGroup,
        defaultLanguage: this._fb.control({ value: '', disabled: true }, Validators.required)
    });

    languages: string[];

    @Input() description: string;
    @Input() set enableDefaultSelection(enable: boolean) {
        this._enableDefaultSelection = enable;
        if (enable && this._data) {
            this.form.controls.defaultLanguage.enable({ emitEvent: false });
            this.form.controls.defaultLanguage.reset(this._data.default, { onlySelf: true });
        }
    }

    @Input()
    set data(data: LanguageSelector) {
        if (data) {
            this._data = data;
            this.form.reset({}, { emitEvent: false });

            this.languages = data.languages ? data.languages : [];
            const langControlKeys = Object.keys(this.languagesGroup.controls);
            if (langControlKeys.length) {
                for (const controlKey in langControlKeys) {
                    this.languagesGroup.removeControl(controlKey, { emitEvent: false });
                }
            }
            this.languages.forEach(code =>
                this.languagesGroup.setControl(code, this._fb.control(false), { emitEvent: false }));
            const selected = data.selected ? data.selected : [];
            selected.forEach(code => this.languagesGroup.controls[code]?.reset(true, { emitEvent: false }));
            if (this._enableDefaultSelection) {
                this.form.controls.defaultLanguage.enable({ emitEvent: false });
                this.form.controls.defaultLanguage.reset(data.default, { onlySelf: true });
            }
        }
    }

    ngOnInit(): void {
        this.languagesGroup.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(languagesRecord => {
                if (!this._enableDefaultSelection) {
                    return;
                }
                const selectedLanguages = Object.keys(languagesRecord).filter(key => languagesRecord[key]);
                if (!selectedLanguages.includes(this.form.controls.defaultLanguage.value)) {
                    this.form.controls.defaultLanguage.markAsDirty();
                    this.form.controls.defaultLanguage.markAsTouched();
                    this.form.controls.defaultLanguage.setValue(selectedLanguages[0] ?? null, { onlySelf: true });
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    setDefaultLanguage(code: string): void {
        if (this.form.controls.defaultLanguage.value === code) return;

        this.form.controls.defaultLanguage.markAsDirty();
        this.form.controls.defaultLanguage.markAsTouched();
        this.form.controls.defaultLanguage.setValue(code);
        this.form.controls.languagesGroup.controls[code].setValue(true, { emitEvent: false });
    }

    getSelectedLanguages(): string[] {
        return Object.keys(this.form.value.languagesGroup).filter(id => this.form.value.languagesGroup[id]);
    }

    getDefaultLanguage(): string {
        return this._enableDefaultSelection ? this.form.value.defaultLanguage : undefined;
    }

    scrollIntoView(): void {
        this._el.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
}
