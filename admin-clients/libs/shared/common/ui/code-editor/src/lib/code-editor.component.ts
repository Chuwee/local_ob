import { NgIf } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy,
    ChangeDetectorRef, Component, DestroyRef, ElementRef, HostBinding, Input, OnDestroy, effect, inject, signal
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
    AbstractControl, ControlValueAccessor, FormControl,
    NgControl, ReactiveFormsModule, ValidationErrors, Validators
} from '@angular/forms';
import type { editor } from 'monaco-editor';
import { CodeEditorService, OB_CODE_EDITOR_CONFIG } from './code-editor.service';

export type Options = {
    control?: FormControl<string>;
    language?: string;
    theme?: string;
};

/**
 * Code editor based on Monaco Editor (VS Code editor)
 * 
 * Imports of monaco editor are made only for typing, the actual code is imported via resources S3 Cloudfront
 * 
 * It's not perfect, but it kind of works
 * Validation of syntax goes async and it means that you can have a form with a wrong value with a valid state
 */
@Component({
    selector: 'ob-code-editor',
    imports: [ReactiveFormsModule, NgIf],
    template: `
        <!-- Loading code editor -->
        <ng-container *ngIf="loading()">
            <div class="loading">Loading code editor...</div>
        </ng-container>
        <!-- Fallback to textarea if code editor is not available -->
        <ng-container *ngIf="error()">
            <textarea [formControl]="control"></textarea>
        </ng-container>
    `,
    styleUrls: ['./code-editor.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CodeEditorComponent implements AfterViewInit, ControlValueAccessor, OnDestroy {

    readonly #editorSrv = inject(CodeEditorService);
    readonly #dialogData = inject(OB_CODE_EDITOR_CONFIG, { optional: true });
    readonly #detectRef = inject(ChangeDetectorRef);
    readonly #destroyRef = inject(DestroyRef);
    readonly #control = inject(NgControl, { optional: true, self: true });
    readonly #elementRef = inject<ElementRef<HTMLElement>>(ElementRef);
    #value: string | undefined;

    error = signal(false);
    loading = signal(true);
    invalid = signal(false);
    control: FormControl<string>;
    editor: editor.IStandaloneCodeEditor | null = null;

    @Input() language?: string;
    @Input() theme?: string = 'vs-dark';
    @Input() wordWrap?: editor.IStandaloneEditorConstructionOptions['wordWrap'] = 'on';

    @HostBinding('class.invalid') invalidClass: boolean;

    onChange: (value: string) => void;
    onTouched: () => void;

    constructor() {
        if (this.#control != null) {
            this.#control.valueAccessor = this;
        }
        effect(() => this.invalidClass = this.invalid());
    }

    ngOnDestroy(): void {
        this.editor?.dispose();
    }

    ngAfterViewInit(): void {
        this.#value = this.#dialogData?.control?.value || this.#value;
        this.language = this.#dialogData?.language || this.language;
        this.theme = this.#dialogData?.theme || this.theme;
        this.control = this.#dialogData?.control || this.#control?.control as FormControl<string>;
        this.control.addValidators(this.validate.bind(this));

        this.#editorSrv.init()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe({
                error: () => {
                    console.error('Failed to load monaco editor');
                    this.error.set(true);
                    this.loading.set(false);
                    this.#detectRef.detectChanges();
                },
                next: monaco => {
                    this.loading.set(false);
                    this.#detectRef.detectChanges();
                    this.value = this.#value;

                    this.editor = monaco.editor.create(
                        this.#elementRef.nativeElement,
                        {
                            value: this.value,
                            language: this.language,
                            theme: this.theme,
                            wordWrap: this.wordWrap,
                            automaticLayout: true,
                            scrollBeyondLastLine: false,
                            autoIndent: 'full',
                            scrollbar: {
                                verticalSliderSize: 10
                            }
                        }
                    );

                    const onChangeContent = (): void => {
                        this.#value = this.editor.getModel().getValue();
                        this.onChange?.(this.#value);
                        this.onTouched?.();
                        this.#dialogData?.control?.setValue(this.#value);
                        this.control.updateValueAndValidity();
                        this.#detectRef.markForCheck();
                    };

                    const onChangeMarkers = (): void => {
                        if (!this.editor.getModel()) return;
                        const markers = monaco.editor.getModelMarkers({
                            resource: this.editor.getModel()?.uri
                        });

                        if (markers.length > 0) {
                            this.invalid.set(true);
                        } else {
                            this.invalid.set(false);
                        }

                        this.control.updateValueAndValidity();
                        this.onTouched?.();
                        this.#detectRef.detectChanges();
                    };

                    monaco.editor.onDidChangeMarkers(onChangeMarkers);
                    this.editor.onDidChangeModelContent(onChangeContent);
                }
            });
    }

    set value(value: string) {
        this.#value = value;
        this.#dialogData?.control?.setValue(value);
        this.editor?.getModel()?.setValue(value);
    }

    get value(): string {
        return this.#value;
    }

    validate(ctrl: AbstractControl): ValidationErrors | null {
        return Validators.compose([
            () => this.invalid() ? { invalid: true } : null
        ])(ctrl);
    }

    writeValue(value: string): void {
        if (value !== this.#value) {
            this.value = value || '';
            this.invalid.set(false);
        }
    }

    registerOnChange(fn: () => void): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: () => void): void {
        this.onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        this.editor?.updateOptions({ readOnly: isDisabled });
    }

}

