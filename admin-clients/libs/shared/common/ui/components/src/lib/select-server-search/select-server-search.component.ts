import { IntersectionListenerDirective } from '@admin-clients/shared/utility/directives';
import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import { CdkVirtualScrollViewport, ScrollingModule } from '@angular/cdk/scrolling';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, computed, ContentChild, DestroyRef, DoCheck,
    EventEmitter, HostBinding, inject, input, Input, OnDestroy, OnInit, Output, TemplateRef, ViewChild
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ControlValueAccessor, NgControl, ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { MatFormFieldControl } from '@angular/material/form-field';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import {
    BehaviorSubject, combineLatest, filter, Observable, of, startWith, Subject, take
} from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { SelectOption } from './select-option.model';

@Component({
    imports: [
        NgxMatSelectSearchModule, ReactiveFormsModule, ScrollingModule, TranslatePipe, NgTemplateOutlet,
        IntersectionListenerDirective, MatSelect, MatOption, MatProgressSpinner, AsyncPipe
    ],
    selector: 'app-select-server-search',
    templateUrl: './select-server-search.component.html',
    styleUrls: ['./select-server-search.component.scss'],
    providers: [{
        provide: MatFormFieldControl,
        useExisting: SelectServerSearchComponent
    }],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SelectServerSearchComponent
    implements MatFormFieldControl<SelectOption | SelectOption[]>, ControlValueAccessor, OnInit, OnDestroy, AfterViewInit, DoCheck {

    private static _nextId = 0;

    readonly #formBuilder = inject(UntypedFormBuilder);
    readonly #destroyRef = inject(DestroyRef);
    readonly #selected = new BehaviorSubject<SelectOption | SelectOption[]>(null);
    readonly #selected$ = this.#selected.asObservable();

    @ViewChild('select', { static: true }) private _select: MatSelect;
    @ViewChild(CdkVirtualScrollViewport, { static: true }) private _scrollViewport: CdkVirtualScrollViewport;
    #onChange: (_: unknown) => unknown;

    readonly ngControl = inject(NgControl, { optional: true, self: true });
    #disabled = false;
    #currentQ = '';
    #firstOpening = true;
    #required = false;
    #isSpinnerLoading = false;

    // virtual scroll parameters
    readonly ITEM_SIZE = 48;
    // custom elements
    readonly UNSELECT_ELEMENT = 'unselectElement';
    readonly LOADER_ELEMENT = 'loaderElement';

    @HostBinding() id = `select-server-search-${SelectServerSearchComponent._nextId++}`;
    @ContentChild('optionTemplate') optionTemplateRef?: TemplateRef<unknown>;
    @Input() placeholder: string;
    @Input() unselectLabel: string;
    @Input() multiple = false;
    @Input() maxSelectItems = 10;
    @Input() bufferItemsAmount = 10;
    @Input() options$: Observable<SelectOption[]>;
    @Input() moreOptionsAvailable$ = of(false);
    @Input() enableTracing = false;
    @Input() loadOptionsOnInit = false;
    @Input() loading$: Observable<boolean>;

    $elementsToShow = input<'4' | '8'>('4', { alias: 'elementsToShow' });
    $scrollHeight = computed(() => this.ITEM_SIZE * Number(this.$elementsToShow()));

    @Output() loadOptions = new EventEmitter<{ q?: string; nextPage?: boolean }>();
    defaultPlaceholder = 'FORMS.SELECT.ALL_ELEMENTS_MALE';
    readonly internalOptionsBS = new BehaviorSubject<(SelectOption | string)[]>([]);
    value: SelectOption | SelectOption[];
    searchControl = this.#formBuilder.control(null);

    get empty(): boolean {
        return this._select.empty;
    }

    get shouldLabelFloat(): boolean {
        return this._select.shouldLabelFloat;
    }

    get focused(): boolean {
        return this._select.focused;
    }

    get stateChanges(): Subject<void> {
        return this._select.stateChanges;
    }

    get errorState(): boolean {
        return this._select.errorState;
    }

    get required(): boolean {
        // Can be set by tpl or by form control validators
        return this.#required || this._select.required;
    }

    @Input()
    set required(value: BooleanInput) {
        this.#required = coerceBooleanProperty(value);
        this.stateChanges.next(null);
    }

    get disabled(): boolean {
        return this.#disabled;
    }

    set disabled(value: BooleanInput) {
        this.#disabled = coerceBooleanProperty(value);
        this.stateChanges.next(null);
    }

    constructor() {
        if (this.ngControl) {
            this.ngControl.valueAccessor = this;
        }
    }

    trackBy = (_: number, item: SelectOption): string | number => item?.id;

    ngOnInit(): void {
        this.prepareTracing();
        this._select.ngControl = this.ngControl;
        if (this.options$) {
            this.setOptionsProcessing();
            this.setFilterBehaviour();
        }
        if (this.loadOptionsOnInit) {
            this.onFirstLoad();
        }
    }

    ngAfterViewInit(): void {
        this.resetScroll();
    }

    ngDoCheck(): void {
        //Necessary for update invalid field styles

        if (this.ngControl) {
            this.updateErrorState();
        }
    }

    ngOnDestroy(): void {
        this._select.ngOnDestroy();
    }

    compareById(option: SelectOption, option2: SelectOption): boolean {
        return option === option2 || option?.id === option2?.id;
    }

    setDescribedByIds(ids: string[]): void {
        this._select.setDescribedByIds(ids);
    }

    onContainerClick(): void {
        this._select.onContainerClick();
    }

    registerOnChange(onChange: (_: unknown) => unknown): void {
        this.#onChange = onChange;
        this._select.registerOnChange(val => this.onChangeBridge(val));
    }

    registerOnTouched(onTouched: () => unknown): void {
        this._select.registerOnTouched(onTouched);
    }

    setDisabledState(isDisabled: boolean): void {
        this.disabled = isDisabled;
    }

    writeValue(val: SelectOption | SelectOption[]): void {
        this.value = val;
        this.#selected.next(val);
    }

    openHandler(open: boolean): void {
        if (open) {
            this.checkReloadRequired();
        }
        this.resetScroll();
    }

    loadMore(more: boolean): void {
        if (!more) return;
        setTimeout(() => {
            if (this.#isSpinnerLoading) return;
            this.#isSpinnerLoading = true;
            this.loadOptions.emit({ q: this.#currentQ || undefined, nextPage: true });
        }, 100);
    }

    // fix to reset and restart virtual viewport, when opens the rendered items are the first ones,
    // but if the scroll position greater than zero, the rendered items are out of the viewport visible area.
    private resetScroll(): void {
        this._scrollViewport.scrollToIndex(0);
        this._scrollViewport.checkViewportSize();
    }

    private setOptionsProcessing(): void {
        this.options$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                this.#isSpinnerLoading = false;
            });

        combineLatest([
            this.options$,
            this.moreOptionsAvailable$,
            this._select.openedChange.pipe(startWith(false)),
            this.#selected$
        ])
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([options, moreOptionsAvailable, open, selection]) => {
                const selections = Array.isArray(selection) ? selection : [selection];
                const augmentedSelections = selections?.map(elem => {
                    if (!options || !elem?.id || elem?.name) return elem;
                    const option = options?.find(opt => elem?.id === opt?.id);
                    if (option?.name) return { ...elem, name: option?.name };
                    return { ...elem, name: `ID: ${elem.id}` };
                });
                if (open) {
                    const resultOptions: (SelectOption | string)[] = [];
                    if (!options) {
                        resultOptions.push(this.LOADER_ELEMENT);
                    } else {
                        if (options.length || !this.required) {
                            resultOptions.push(this.UNSELECT_ELEMENT);
                        }
                        resultOptions.push(...this.selectionOptionCorrection(options, augmentedSelections));
                        if (moreOptionsAvailable) {
                            resultOptions.push(this.LOADER_ELEMENT);
                        }
                    }
                    this.internalOptionsBS.next(resultOptions);
                } else if (selection) {
                    const resultSelectedResult: SelectOption[] = [];
                    resultSelectedResult.push(...augmentedSelections);
                    resultSelectedResult.sort((a, b) =>
                        a?.name.toLowerCase() < b?.name.toLowerCase() ? -1 : 1
                    );
                    this.internalOptionsBS.next(resultSelectedResult);
                } else {
                    this.internalOptionsBS.next([]);
                }
                setTimeout(() => this._select.value = this.value);
            });
    }

    private selectionOptionCorrection(options: SelectOption[], selection: SelectOption[]): (SelectOption | string)[] {
        const resultOptions: (SelectOption | string)[] = [];
        if (selection && (!Array.isArray(selection) || selection.length)) {
            const selectionMap = new Map<string, SelectOption>();
            selection.forEach(selected => selectionMap.set(selected?.id?.toString(), selected));
            options.forEach(option => {
                if (selectionMap.has(option.id.toString())) {
                    resultOptions.push(selectionMap.get(option.id.toString()));
                } else {
                    resultOptions.push(option);
                }
            });
        } else {
            resultOptions.push(...options);
        }
        return resultOptions;
    }

    private onChangeBridge(val: (SelectOption | string) | (SelectOption | string)[]): void {
        if (val !== this.value) {
            this.trace('value changed', val);
            let resultValue: SelectOption | SelectOption[];
            if (!val) {
                if (this.value && (!Array.isArray(this.value) || this.value.length)) {
                    resultValue = this.value;
                } else {
                    resultValue = null;
                }
            } else if (val === this.UNSELECT_ELEMENT) {
                resultValue = null;
                this._select.value = null;
            } else if (Array.isArray(val) && val.includes(this.UNSELECT_ELEMENT)) {
                resultValue = [];
                this._select.value = resultValue;
            } else {
                if (this.multiple) {
                    const value: SelectOption[] = Array.isArray(this.value) ? this.value : [this.value];
                    const inViewSelected = this._select.options.filter(opt => opt.selected).map(opt => opt.value);
                    const inViewNotSelected = this._select.options
                        .filter(opt => !inViewSelected.includes(opt.value) && !opt.selected).map(opt => opt.value);
                    resultValue = value
                        .concat(inViewSelected.filter(v => !value.includes(v)))
                        .filter(v => !!v && !inViewNotSelected.includes(v));
                    if (resultValue.length > this.maxSelectItems) {
                        this._select.value = this.value;
                        return;
                    }
                } else {
                    resultValue = val as (SelectOption | SelectOption[]); // edge case, val can only be SelectOption or SelectOption[]
                }
            }
            this.value = resultValue;
            this.#selected.next(resultValue);
            this.#onChange(resultValue);
        }
    }

    private checkReloadRequired(): void {
        if (this.#firstOpening) {
            this.#firstOpening = false;
            if (!this.loadOptionsOnInit) {
                this.onFirstLoad();
            }
        } else {
            this.options$.pipe(take(1))
                .subscribe(options => {
                    if (!options?.length || this.#currentQ !== this.searchControl.value) {
                        this.#currentQ = this.searchControl.value;
                        this.#isSpinnerLoading = true;
                        this.loadOptions.emit({});
                    }
                });
        }
    }

    private onFirstLoad(): void {
        this.#isSpinnerLoading = true;
        this.loadOptions.emit({});
    }

    private setFilterBehaviour(): void {
        this.searchControl.valueChanges
            .pipe(
                filter(q => q !== this.#currentQ),
                debounceTime(300),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(q => {
                if (this._select.panelOpen) {
                    this.#currentQ = q;
                    this.#isSpinnerLoading = true;
                    this.internalOptionsBS.next([this.LOADER_ELEMENT]);
                    this.loadOptions.emit({ q: q || undefined });
                    this.resetScroll();
                }
            });
    }

    private prepareTracing(): void {
        if (this.enableTracing) {
            this.trace = (...messages: unknown[]): void => {
                console.log(messages);
            };
        }
    }

    private trace(..._: unknown[]): void { /* NOOP */ }

    private updateErrorState(): void {
        const oldState = this.errorState;
        const newState = (this.ngControl?.invalid || this._select.errorState) && (this.ngControl.touched);

        if (oldState !== newState) {
            this._select.errorState = newState;
            this.stateChanges.next();
        }
    }
}
