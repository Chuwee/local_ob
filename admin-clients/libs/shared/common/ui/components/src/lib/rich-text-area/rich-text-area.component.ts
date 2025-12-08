import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import {
    ChangeDetectionStrategy, Component, HostBinding, Input, OnDestroy, OnInit, Optional, Self, ViewChild
} from '@angular/core';
import { ControlValueAccessor, NgControl } from '@angular/forms';
import { MatFormFieldControl } from '@angular/material/form-field';
import { EditorComponent, EditorModule, TINYMCE_SCRIPT_SRC } from '@tinymce/tinymce-angular';
import { Subject } from 'rxjs';

const ALIGN_SELECTOR = 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img,audio,video';

@Component({
    imports: [EditorModule],
    selector: 'app-rich-text-area',
    templateUrl: './rich-text-area.component.html',
    styleUrls: ['./rich-text-area.component.scss'],
    providers: [
        {
            provide: MatFormFieldControl,
            useExisting: RichTextAreaComponent
        }, {
            provide: TINYMCE_SCRIPT_SRC,
            useValue: 'assets/tinymce/tinymce.min.js'
        }
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RichTextAreaComponent implements MatFormFieldControl<string>, ControlValueAccessor, OnInit, OnDestroy {
    private static _nextId = 0;
    private _required: boolean;
    private _disabled: boolean;
    private _focused = false;
    private _value: string | null;
    private _isError = false;
    private _onChangeListener: (value: unknown) => void;
    private _toolbar: string | string[] | undefined;
    private _enableViewCodeOption = true;
    private _enableAutoresizeOption: boolean;
    private _isAutocompleterEnabled = false;
    private _autocompleterItems: string[];
    private _pasteAsText = false;

    @ViewChild(EditorComponent, { static: true })
    private _editorComponent: EditorComponent;

    @HostBinding()
    id = `rich-text-area-editor-${RichTextAreaComponent._nextId++}`;

    @HostBinding('attr.aria-describedby')
    describedBy = '';

    @Input()
    placeholder: string;

    /* eslint-disable @typescript-eslint/naming-convention */
    options: Record<string, unknown> = {
        branding: false,
        contextmenu: false,
        elementpath: false,
        menubar: false,
        preview_styles: false,
        statusbar: false,
        entity_encoding: 'raw',
        formats: {
            alignleft: { selector: ALIGN_SELECTOR, classes: 'text-left', styles: { 'text-align': 'left' } },
            alignright: { selector: ALIGN_SELECTOR, classes: 'text-right', styles: { 'text-align': 'right' } },
            aligncenter: { selector: ALIGN_SELECTOR, classes: 'text-center', styles: { 'text-align': 'center' } },
            alignjustify: { selector: ALIGN_SELECTOR, classes: 'text-justify', styles: { 'text-align': 'justify' } },
            bold: { inline: 'strong', classes: 'text-bold', styles: { 'font-weight': 'bold' } },
            italic: { inline: 'em', classes: 'text-italic', styles: { 'font-style': 'italic' } },
            underline: { inline: 'u', classes: 'text-underline', styles: { 'text-decoration': 'underline' } }
        }
    };
    /* eslint-enable @typescript-eslint/naming-convention */

    readonly shouldLabelFloat = false;
    readonly stateChanges = new Subject<void>();

    @Input()
    set toolbar(value: string | string[] | undefined) {
        if (value != null) {
            this._toolbar = value;
        }
    }

    get toolbar(): string | string[] | undefined {
        return this._toolbar;
    }

    @Input()
    set required(value: BooleanInput) {
        this._required = coerceBooleanProperty(value);
        this.stateChanges.next(null);
    }

    get required(): boolean {
        return this._required;
    }

    @Input()
    set disabled(value: BooleanInput) {
        this._disabled = coerceBooleanProperty(value);
        this.stateChanges.next(null);
    }

    get disabled(): boolean {
        return this._disabled;
    }

    @Input()
    set viewCode(isEnabled: boolean | string) {
        this._enableViewCodeOption = coerceBooleanProperty(isEnabled);
    }

    set value(value: string | null) {
        this.writeValue(value);
    }

    get value(): string | null {
        return this._value;
    }

    get empty(): boolean {
        return this._value === null;
    }

    get errorState(): boolean {
        if (this._isError !== (this.ngControl.invalid && this.ngControl.touched)) {
            this._isError = !this._isError;
            this.stateChanges.next(null);
        } else if (this._isError && this.ngControl.errors === null) {
            this._isError = false;
            this.stateChanges.next(null);
        }
        return this._isError;
    }

    get focused(): boolean {
        return this._focused;
    }

    @Input() set height(value: string) {
        this.options = {
            ...this.options,
            height: value
        };
    }

    @Input() set autoresize(value: boolean | string | { min: number; max: number }) {
        this._enableAutoresizeOption = coerceBooleanProperty(value);
        if (typeof value === 'object' && (value.min || value.max)) {
            const heightRange: { min_height?: number; max_height?: number } = {};
            if (value.min) {
                heightRange.min_height = value.min;
            }
            if (value.max) {
                heightRange.max_height = value.max;
            }
            this.options = {
                ...this.options,
                ...heightRange
            };
        }
    }

    @Input() set autocompleterItems(items: string[]) {
        this._autocompleterItems = items;
        if (!this._isAutocompleterEnabled) {
            this._isAutocompleterEnabled = true;
            this.options = {
                ...this.options,
                setup: editor => {
                    const onAction = (autocompleteApi, rng, value): void => {
                        editor.selection.setRng(rng);
                        editor.insertContent(value);
                        autocompleteApi.hide();
                    };
                    const getMatchedChars = (pattern: string): string[] =>
                        this._autocompleterItems?.filter(char => char.indexOf(pattern) !== -1);

                    editor.ui.registry.addAutocompleter('placeholders', {
                        ch: '#',
                        minChars: 0,
                        columns: 1,
                        highlightOn: ['char_name'],
                        onAction,
                        fetch: pattern => new Promise(resolve => {
                            const results = getMatchedChars(pattern)?.map(char => ({
                                type: 'cardmenuitem',
                                value: char,
                                items: [{
                                    type: 'cardcontainer',
                                    items: [{
                                        type: 'cardtext',
                                        text: char,
                                        name: 'char_name'
                                    }]
                                }]
                            })) || [];
                            resolve(results);
                        })
                    });
                }
            };
        }
    }

    @Input() set pasteAsText(value: BooleanInput) {
        this._pasteAsText = coerceBooleanProperty(value);
        if (this._pasteAsText) {
            this.options = {
                ...this.options,
                paste_as_text: true
            };
        }
    }

    constructor(@Optional() @Self() public ngControl: NgControl) {
        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }
    }

    ngOnInit(): void {
        this._editorComponent.registerOnChange(value => {
            if (this._value !== value) {
                this._value = value;
                //Mark as touched for displaying errors instantly
                this.ngControl.control.markAsTouched();
                this._onChangeListener(value);
                this.stateChanges.next(null);
            }
        });
        this._editorComponent.registerOnTouched(() => {
            this.ngControl.control.markAsTouched();
            this.stateChanges.next(null);
        });
        this.options = {
            ...this.options,
            placeholder: this.placeholder,
            plugins: 'lists link' +
                (this._enableAutoresizeOption ? ' autoresize' : '') +
                (this._enableViewCodeOption ? ' code' : '') +
                (this._pasteAsText ? ' paste' : '')
        };
        this._toolbar = 'bold italic underline alignleft aligncenter ' +
            'alignright alignjustify bullist link' + (this._enableViewCodeOption ? ' code' : '');
    }

    ngOnDestroy(): void {
        this.stateChanges.complete();
    }

    onContainerClick(_: MouseEvent): void {
        //noop
    }

    setDescribedByIds(ids: string[]): void {
        this.describedBy = ids.join(' ');
    }

    registerOnChange(onChange: () => void): void {
        this._onChangeListener = onChange;
    }

    registerOnTouched(onTouched: unknown): void {
        this._editorComponent.registerOnTouched(onTouched);
    }

    setDisabledState(isDisabled: boolean): void {
        this.disabled = isDisabled;
        this._editorComponent.setDisabledState(isDisabled);
        this.stateChanges.next(null);
    }

    writeValue(value: string | null): void {
        this.setDisabledState(this.ngControl.disabled);
        if (this._value !== value) {
            this._value = value;
            this._editorComponent.writeValue(value);
            this.stateChanges.next(null);
        }
    }

    focusChange(isFocused: boolean): void {
        this._focused = isFocused;
        this.stateChanges.next(null);
    }

    onInitNgModel(): void {
        this.ngControl.control.markAsPristine();
    }
}
