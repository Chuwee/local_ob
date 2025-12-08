import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Subject } from 'rxjs';

@Component({
    selector: 'app-ticket-table-advance-selector',
    templateUrl: './ticket-table-advance-selector.component.html',
    styleUrls: ['./ticket-table-advance-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TicketTableAdvanceSelectorComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    @Input() items: IdName[];
    @Output() applySelection = new EventEmitter<number[]>();

    readonly dateTimeFormats = DateTimeFormats;

    form: UntypedFormGroup;

    constructor(private _fb: UntypedFormBuilder) { }

    ngOnInit(): void {
        const sessionMap = this.items?.reduce((acc, elem) => (acc[elem.id] = null, acc), {});
        this.form = this._fb.group(sessionMap);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    open(): void {
        this.form.reset();
    }

    apply(): boolean {
        this.applySelection.emit(this.selected);
        return true;
    }

    get selected(): number[] {
        return Object.keys(this.form.value)?.filter(i => !!this.form.value[i]).map(elem => parseInt(elem));
    }
}
