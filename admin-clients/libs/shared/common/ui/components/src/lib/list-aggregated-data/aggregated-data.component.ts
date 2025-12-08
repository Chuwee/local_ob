import { PreserveConfig, TableColConfigService } from '@admin-clients/shared/common/data-access';
import { AUTHENTICATION_SERVICE } from '@admin-clients/shared/core/data-access';
import { AggregatedData, AggregationMetrics, AggregationMetricType, FieldData } from '@admin-clients/shared/data-access/models';
import { LocalCurrencyPipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { AsyncPipe, NgClass, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import {
    MatCell, MatCellDef, MatColumnDef, MatFooterCell, MatFooterCellDef, MatFooterRow, MatFooterRowDef, MatHeaderCell, MatHeaderCellDef,
    MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter } from 'rxjs';
import { ColSelectionDialogComponent } from '../col-selection-dialog/col-selection-dialog.component';
import { ObMatDialogConfig } from '../message-dialog/models/message-dialog.model';

@Component({
    imports: [
        TranslatePipe, LocalNumberPipe, LocalCurrencyPipe, UpperCasePipe, NgClass, MatTable, MatColumnDef, MatHeaderCell,
        MatIconButton, MatIcon, MatCellDef, MatCell, MatFooterCell, MatFooterCellDef, MatHeaderCellDef, MatHeaderRow,
        MatHeaderRowDef, MatRow, MatRowDef, MatFooterRow, MatFooterRowDef, AsyncPipe, MatTooltip, MatCell, MatButton
    ],
    selector: 'app-aggregated-data',
    templateUrl: './aggregated-data.component.html',
    styleUrls: ['./aggregated-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('inOutAnimation', [
            state('0', style({ height: 0, opacity: 0 })),
            transition('0 => 1', [
                animate('300ms ease-out', style({ height: '*', opacity: 1 }))
            ]),
            state('1', style({ height: '*', opacity: 1 })),
            transition('1 => 0', [
                animate('300ms ease-in', style({ height: 0, opacity: 0 }))
            ])
        ])
    ]
})
export class AggregatedDataComponent implements OnInit {

    readonly #$user = toSignal(inject(AUTHENTICATION_SERVICE).getLoggedUser$());

    isTypesExpanded = false;

    readonly #dialog = inject(MatDialog);
    readonly isHandsetOrTablet$ = isHandsetOrTablet$();
    readonly $aggregatedData = input<AggregatedData | undefined | null>(null, { alias: 'aggregatedData' });
    readonly $hiddenAggsColumns = signal<string[]>([]);
    readonly $defaultHiddenColumns = input<string[]>(null, { alias: 'defaultHiddenColumns' });

    readonly $withColumnsPicker = input(false, { alias: 'withColumnsPicker', transform: coerceBooleanProperty });
    readonly $tableId = input<PreserveConfig>(null, { alias: 'tableId' });

    readonly $aggregationMetrics = input<AggregationMetrics>(null, { alias: 'aggregationMetrics' });
    readonly $mainMetricLabel = input('AGGREGATED_METRIC.METRIC.TOTAL', { alias: 'mainMetricLabel' });
    readonly $maxWidth = input('100%', { alias: 'maxWidth' });
    readonly $currencyFormat = input<'wide' | 'narrow'>(null, { alias: 'currencyFormat' });
    readonly $currencyInput = input<string | null>('', { alias: 'currency' });
    readonly #tableSrv = inject(TableColConfigService);

    readonly $groups = computed(() => {
        const fields = Object.keys(this.$aggregationMetrics())
            .map(key => ({
                field: key, fieldKey: (
                    this.$aggregationMetrics()[key].headerKey), isDefault: !this.$defaultHiddenColumns().includes(key), disabled: false
            }) as FieldData);

        return ({ field: 'metrics', fieldKey: 'AGGREGATED_METRIC', isDefault: true, fields });
    });

    readonly $currency = computed(() => this.$currencyInput() || this.#$user()?.currency);
    readonly $typeList = computed(() => this.$aggregatedData()?.types);
    readonly $columns = computed(() => Object.keys(this.$aggregationMetrics()));
    readonly $columnsToDisplay = computed(() => {
        let columnsToDisplay = ['metric', ...this.$columns()];
        if (this.$withColumnsPicker()) {
            columnsToDisplay.push('actions');
        }
        if (this.$hiddenAggsColumns()?.length) {
            columnsToDisplay = columnsToDisplay.filter(colToDisplay => !this.$hiddenAggsColumns().includes(colToDisplay));
        }
        return columnsToDisplay;
    });

    readonly $lastColumn = computed(() => this.$columnsToDisplay()
        ? this.$columnsToDisplay()[this.$columnsToDisplay().length - (this.$columnsToDisplay().includes('actions') ? 2 : 1)]
        : '');

    readonly $aggData = computed(() => this.$aggregatedData() ??
        new AggregatedData({
            overall: this.$columns().map(column => ({ name: column, type: AggregationMetricType.count, value: 0 }))
        }, this.$aggregationMetrics()));

    ngOnInit(): void {
        this.$hiddenAggsColumns.set(this.$tableId()
            ? this.#tableSrv.getColumns(this.$tableId()) ?? this.$defaultHiddenColumns()
            : this.$defaultHiddenColumns());
    }

    changeAggDataColSelection(): void {
        const groups = [this.$groups()];
        const fields = groups
            .map(columnGroup => columnGroup.fields)
            .reduce((previousValue, currentValue) => currentValue.concat(...previousValue))
            .filter(buyerColumn => !this.$hiddenAggsColumns().includes(buyerColumn.field));
        const config = new ObMatDialogConfig({
            fieldDataGroups: groups,
            selectedFields: fields
        });
        this.#dialog.open(ColSelectionDialogComponent, config)
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe((result: FieldData[]) => {
                const resultFields = result.map(resultData => resultData.field);
                const sortResult = [].concat(
                    ...groups
                        .map(colGroup => colGroup.fields)
                )
                    .filter(data => resultFields.includes(data.field))
                    .map(data => data.field);
                const finalHidden = groups[0].fields.map(fields => fields.field).filter(key => !sortResult.includes(key));
                const tableId = this.$tableId();
                if (tableId) {
                    this.#tableSrv.setColumns(tableId, finalHidden);
                }
                this.$hiddenAggsColumns.set(finalHidden);
            });
    }

}
