import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { NgFor, NgIf, NgClass, KeyValuePipe } from '@angular/common';
import {
    Component, ChangeDetectionStrategy, Input, Output, EventEmitter,
    ContentChildren, ViewChild, QueryList, AfterContentInit, OnInit, OnDestroy
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTableModule, MatColumnDef, MatTable } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

type Action = {
    icon: string;
    enabled?: (elem: unknown) => boolean;
    label?: string;
};

@Component({
    selector: 'app-actions-table',
    templateUrl: './actions-table.component.html',
    styleUrls: ['./actions-table.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatButtonModule,
        MatIconModule,
        MatMenuModule,
        MatTableModule,
        NgIf,
        NgFor,
        NgClass,
        KeyValuePipe,
        FlexLayoutModule,
        TranslatePipe
    ]
})
export class ActionsTableComponent<T> implements AfterContentInit, OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    private _useContextMenu = true;

    numberOfActions: number;

    @Input() data: Observable<T[]>;

    @Input() actions?: Record<string, Action>;

    @Input() columns? = ['name', 'actions'];

    @Input() title?: string;

    @Input()
    get useContextMenu(): boolean { return this._useContextMenu; }

    set useContextMenu(useContextMenu: boolean) {
        this._useContextMenu = coerceBooleanProperty(useContextMenu);
    }

    @Output() actionClick = new EventEmitter<{ action: string; elem: unknown }>();
    @Output() isContentChanging = new EventEmitter<boolean>(false);

    @ContentChildren(MatColumnDef) columnDefs: QueryList<MatColumnDef>;
    @ViewChild(MatTable, { static: true }) table: MatTable<unknown>;

    constructor() { }

    ngOnInit(): void {
        this.numberOfActions = this.actions && Object.keys(this.actions).length;

        this.data.pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                this.isContentChanging.emit(true);
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    ngAfterContentInit(): void {
        this.columnDefs.forEach(columnDef => this.table.addColumnDef(columnDef));
    }

    actionClicked(action: string, elem: T): void {
        this.actionClick.emit({ action, elem });
    }

}
