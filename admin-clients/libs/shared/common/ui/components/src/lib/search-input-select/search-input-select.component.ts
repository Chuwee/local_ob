import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { merge, Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-search-input-select',
    imports: [
        AsyncPipe,
        FlexLayoutModule,
        MatAutocompleteModule,
        MatButtonModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatTooltipModule,
        TranslatePipe,
        ReactiveFormsModule
    ],
    templateUrl: './search-input-select.component.html',
    styleUrls: ['./search-input-select.component.scss']
})
export class SearchInputSelectComponent implements OnInit {
    private _closeAutocomplete = new Subject<string>();

    readonly inputControl = new FormControl();
    @Input()
    options: string[];

    @Input()
    defaultOption: string;

    @Output()
    commitSearch = new EventEmitter<{ option: string; value: string }>();

    options$: Observable<{ value: string; label: string }[]>;
    currentOption: string;

    @Input()
    set selectedOption(option: string) {
        this.currentOption = option;
    }

    @Input()
    set inputValue(inputValue: string) {
        this.inputControl.setValue(inputValue);
    }

    constructor(
        private _translateSrv: TranslateService
    ) { }

    ngOnInit(): void {
        this.options$ = merge(this.inputControl.valueChanges, this._closeAutocomplete.asObservable())
            .pipe(
                map(inputValue => {
                    if (inputValue?.length > 3) {
                        return this.options.map(value =>
                        ({
                            value,
                            label:
                                `${this._translateSrv.instant('ACTIONS.SEARCH_BY')} ${this._translateSrv.instant(value)?.toLowerCase()} : ${inputValue}`
                        }));
                    } else {
                        return [];
                    }
                })
            );
    }

    optionSelected(event: MatAutocompleteSelectedEvent): void {
        this.currentOption = event.option.id;
        this.emitChange();
    }

    resetKeyword(): void {
        this.currentOption = this.defaultOption;
        this.inputControl.reset();
        this.emitChange();
    }

    emitChange(): void {
        this.commitSearch.emit({
            option: this.currentOption || this.defaultOption,
            value: this.inputControl.value
        });
        this._closeAutocomplete.next(null);
    }
}
