import { CurrencyInputComponent } from '@admin-clients/shared/common/ui/components';
import { ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { ErrorMessage$Pipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import { CommonModule } from '@angular/common';
import { AfterContentInit, ChangeDetectionStrategy, Component, ContentChildren, Input, QueryList, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormArray } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatColumnDef, MatTable, MatTableModule } from '@angular/material/table';
import { Observable } from 'rxjs';
import { OptionsTableAllDirective } from './all/options-table-all.directive';
import { OptionsTableDefaultComponent } from './default/options-table-default.component';
import { OptionsTableColumnOption as Col } from './models/options-table.model';
import { OptionsTableDirective } from './options-table.directive';

@Component({
    imports: [
        CommonModule,
        MatCheckboxModule,
        MatFormFieldModule,
        MatIconModule,
        MatTableModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        CurrencyInputComponent,
        LocalCurrencyPipe,
        OptionsTableDirective,
        OptionsTableAllDirective,
        OptionsTableDefaultComponent,
        ErrorMessage$Pipe,
        ErrorIconDirective
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-options-table',
    templateUrl: './options-table.component.html',
    styleUrls: ['./options-table.component.scss']
})
export class OptionsTableComponent implements AfterContentInit {

    private _disabled = true;

    readonly col = Col;

    @Input() activeRequired = true;
    @Input() data: Observable<unknown>;
    @Input() form: UntypedFormArray;
    @Input() columns: string[];

    @Input() set disabled(value: BooleanInput) {
        this._disabled = coerceBooleanProperty(value);
        this._disabled ? this.form?.disable() : this.form?.enable();
    }

    get disabled(): boolean {
        return this._disabled;
    }

    @Input() header = true;
    @Input() isEmpty = false;

    @ContentChildren(MatColumnDef) columnDefs: QueryList<MatColumnDef>;
    @ViewChild(MatTable, { static: true }) table: MatTable<unknown>;

    ngAfterContentInit(): void {
        this.columnDefs.forEach(columnDef => this.table.addColumnDef(columnDef));
    }
}
