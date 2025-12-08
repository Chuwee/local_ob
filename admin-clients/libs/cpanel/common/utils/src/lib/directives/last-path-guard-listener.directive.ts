import { Directive, EventEmitter, Host, Input, OnDestroy, OnInit, Optional, Output } from '@angular/core';
import { MatButtonToggleGroup } from '@angular/material/button-toggle';
import { MatSelectionList, MatSelectionListChange } from '@angular/material/list';
import { GuardsCheckEnd, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

@Directive({
    standalone: true,
    selector: '[appLastPathGuardListener]'
})

export class LastPathGuardListenerDirective implements OnInit, OnDestroy {
    private _onDestroy = new Subject();

    @Input() currValue: string;

    @Output() navigationReverted = new EventEmitter<void>();

    constructor(
        private _router: Router,
        @Optional() @Host() private _matSelectionList: MatSelectionList,
        @Optional() @Host() private _matButtonToggleGroup: MatButtonToggleGroup
    ) { }

    ngOnInit(): void {
        this._router.events.pipe(
            filter(event => event instanceof GuardsCheckEnd && !event.shouldActivate),
            takeUntil(this._onDestroy)
        )
            .subscribe(() => {
                if (this._matButtonToggleGroup) {
                    this._matButtonToggleGroup.writeValue(this.currValue);
                } else if (this._matSelectionList) {
                    this._matSelectionList.writeValue([this.currValue]);
                    const selectOption = this._matSelectionList.options.find(option => option.value === this.currValue);
                    //TODO REVISAR
                    const change = new MatSelectionListChange(this._matSelectionList, [selectOption]);
                    this._matSelectionList.selectionChange.emit(change);
                }
                this.navigationReverted.emit();
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
