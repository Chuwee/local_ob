import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EventPrice, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { PricePercentageValue, SessionPackRefundCondition } from '@admin-clients/cpanel/promoters/events/session-packs/data-access';
import { EventSessionsService, Session } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormControl, Validators, UntypedFormGroup, ValidatorFn, ValidationErrors, AbstractControl } from '@angular/forms';
import { Observable, Subject, combineLatest } from 'rxjs';
import { filter, first, map, shareReplay, takeUntil } from 'rxjs/operators';
import { PartialRefundTableElement } from './partial-refund-percentages-table.model';

@Component({
    selector: 'app-partial-refund-percentages-table',
    templateUrl: './partial-refund-percentages-table.component.html',
    styleUrls: ['./partial-refund-percentages-table.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class PartialRefundPercentagesTableComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _parentRowsForm: UntypedFormGroup;
    data$: Observable<PartialRefundTableElement[]>;
    table: PartialRefundTableElement[];
    sessions: Partial<Session>[] = [];
    columns: string[];
    userCurrency: string;
    readonly dateTimeFormats = DateTimeFormats;

    @Input() form: UntypedFormGroup;
    @Input() controlName: string;

    constructor(
        private _sessionsService: EventSessionsService,
        private _eventsService: EventsService,
        private _authSrv: AuthenticationService
    ) { }

    ngOnInit(): void {
        this._authSrv.getLoggedUser$()
            .pipe(first(user => user !== null))
            .subscribe(user => this.userCurrency = user.currency);

        this.data$ = combineLatest([
            this._sessionsService.getSessionRefundConditions$(),
            this._eventsService.eventPrices.get$()
        ]).pipe(
            filter(([refundConditions, prices]) => !!refundConditions && !!prices),
            map(([refundConditions, prices]) => {
                this.form.setControl(this.controlName, new UntypedFormGroup({}));
                this._parentRowsForm = new UntypedFormGroup({});
                this.sessions = this.obtainSessionColumns(refundConditions.session_pack_refund_conditions);
                this.table = this.transformData(
                    refundConditions.session_pack_refund_conditions,
                    prices
                ).sort((a, b) => a.id.localeCompare(b.id));
                this.insertExpandableRows();
                this.insertSuperRow();
                this.columns = ['price-type-rate', ...this.sessions.map(session => `${session.id}`)];
                if (this.form.get('session_pack_automatically_calculate_conditions')?.value) {
                    this._parentRowsForm.disable({ emitEvent: false });
                    this.form.get(this.controlName).disable({ emitEvent: false });
                }
                return this.table;
            }),
            shareReplay(1)
        );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    findSession(sessionId: string): Partial<Session> {
        return this.sessions.find(session => session.id.toString() === sessionId);
    }

    toggleExpand(row: PartialRefundTableElement): void {
        row.expanded = !row.expanded;
        this.table
            .filter(elem => elem.type === 'RATE' && elem.price_type.id === row.price_type.id)
            .forEach(elem => {
                elem.hidden = !row.expanded;
            });
    }

    /**
     * the sum of the array has to be equal to total
     */
    private sumArrayValidator(total: number): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (control instanceof UntypedFormGroup) {
                return Object.values(control.controls).some(control => control.value === null) ||
                    Object.values(control.controls).reduce((acc, control) =>
                        acc + control.value, 0) === total ? null : { rowSumNotEq: total };
            }
            return null;
        };
    }

    /**
     * sets the parent row (or expandable row) of the current row to null
     */
    private setParentCellToNull(row: PartialRefundTableElement, sessionId: number): void {
        const parentRow = this.table.find(elem => elem.id === row.price_type.id.toString());
        parentRow.ctrl.get(`${sessionId}`).setValue(null, { emitEvent: false });
        this.setSuperCellToNull(sessionId);
    }

    /**
     * sets the super cell of the specified sessionId to null
     */
    private setSuperCellToNull(sessionId: number): void {
        this.table
            .find(row => row.type === 'SUPER')?.ctrl
            .get(`${sessionId}`)?.setValue(null, { emitEvent: false });
    }

    /**
     * Inserts a super row at the beginning of the table
     * used to bulk update all the rates and sessions percentages
     */
    private insertSuperRow(): void {
        const rowCtrl = new UntypedFormGroup({}, { validators: this.sumArrayValidator(100) });
        this._parentRowsForm.addControl(`super-row`, rowCtrl);
        const superRow: PartialRefundTableElement = {
            id: 'super-row',
            expanded: false,
            ctrl: rowCtrl,
            type: 'SUPER'
        };
        this.sessions.forEach(session => {
            const sessionCtrl = new UntypedFormControl(null, [Validators.max(100)]);
            rowCtrl.addControl(`${session.id}`, sessionCtrl);
            const expandableParentRows = this.table.filter(elem => elem.type === 'PRICETYPE');
            const firstValueOfExpandableRow = expandableParentRows[0].ctrl.get(`${session.id}`).value;
            if (expandableParentRows.every(row => row.ctrl.get(`${session.id}`).value === firstValueOfExpandableRow)) {
                // if all rows with same price type have the same value then put it on the expandable row
                sessionCtrl.setValue(firstValueOfExpandableRow);
            }

            // ACTION OF SUPER CELL
            sessionCtrl.valueChanges
                .pipe(takeUntil(this._onDestroy), filter(value => value !== null))
                .subscribe(value => {
                    this.table.forEach(row => {
                        row.ctrl.get(`${session.id}`).setValue(value, { emitEvent: false });
                        row.ctrl.markAsDirty();
                    });
                });
        });
        this.table.unshift(superRow);
    }

    /**
     * inserts a parent row at the beginning of each price type group
     * this row updates all the cells of the same price type
     */
    private insertExpandableRows(): void {
        let lastPriceTypeId: number;
        this.table?.forEach((row, index) => {
            let hasToExpand: boolean;
            if (lastPriceTypeId !== row.price_type.id) {
                lastPriceTypeId = row.price_type.id;
                const rowCtrl = new UntypedFormGroup({}, { validators: this.sumArrayValidator(100) });
                this._parentRowsForm.addControl(`${row.price_type.id}`, rowCtrl);
                const expandableRow: PartialRefundTableElement = {
                    id: row.price_type.id.toString(),
                    price_type: row.price_type,
                    expanded: false,
                    ctrl: rowCtrl,
                    type: 'PRICETYPE'
                };
                this.table.splice(index, 0, expandableRow);
                this.sessions.forEach(session => {
                    rowCtrl.addControl(`${session.id}`, new UntypedFormControl(null, [Validators.max(100), Validators.min(0)]));
                    const rowsWithSamePriceType = this.table.filter(elem =>
                        elem.price_type.id === row.price_type.id && elem.type === 'RATE');
                    const firstValueOfRowWithSamePricetype = rowsWithSamePriceType[0].ctrl.get(`${session.id}`).value;
                    if (rowsWithSamePriceType.every(row => row.ctrl.get(`${session.id}`).value === firstValueOfRowWithSamePricetype)) {
                        // if all rows with same price type have the same value then put it on the expandable row
                        rowCtrl.get(`${session.id}`).setValue(firstValueOfRowWithSamePricetype);
                    } else {
                        hasToExpand = true;
                    }

                    // ACTION PARENT CELL
                    rowCtrl.get(`${session.id}`).valueChanges
                        .pipe(takeUntil(this._onDestroy), filter(value => value !== null))
                        .subscribe(value => {
                            rowsWithSamePriceType.forEach(row => {
                                row.ctrl.get(`${session.id}`).setValue(value, { emitEvent: false });
                                row.ctrl.markAsDirty();
                            });
                            this.setSuperCellToNull(session.id);
                        });
                });
                hasToExpand && this.toggleExpand(expandableRow); // expand the row
            }
        });
    }

    /**
     * Gets all the sessions from the data model
     */
    private obtainSessionColumns(conditions: SessionPackRefundCondition[]): Partial<Session>[] {
        const sessions: Partial<Session>[] = [];
        conditions?.forEach(refundConditions => {
            !sessions.find(column => column.id === refundConditions.session_id) && sessions.push({
                id: refundConditions.session_id,
                name: refundConditions.session_name,
                start_date: refundConditions.session_start_date
            });
        });
        return sessions;
    }

    /**
     * transforms the request data of partial refunds to the structure of the UI table
     * @param conditions partial refund condition data
     * @param prices event prices for the current session pack template
     * @returns the table data
     */
    private transformData(conditions: SessionPackRefundCondition[], prices: EventPrice[]): PartialRefundTableElement[] {
        const table: PartialRefundTableElement[] = [];
        conditions?.forEach(refundConditions => {
            refundConditions.price_percentage_values?.forEach(percent => {
                let row = table.find(elem => elem.price_type.id === percent.price_type.id
                    && elem.rate?.id === percent.rate?.id);
                if (!row) {
                    row = {
                        id: percent.price_type.id + '-' + percent.rate.id,
                        price_type: percent.price_type,
                        rate: percent.rate,
                        price: this.findPrice(prices, percent)?.value,
                        ctrl: new UntypedFormGroup({}, { validators: this.sumArrayValidator(100) }),
                        sessions: {},
                        hidden: true,
                        type: 'RATE'
                    };
                    table.push(row);
                }
                const ctrl = new UntypedFormControl(percent.value, [Validators.max(100), Validators.required]);
                row.ctrl.addControl(`${refundConditions.session_id}`, ctrl);

                // ACTION CHILD CELL
                ctrl.valueChanges
                    .pipe(takeUntil(this._onDestroy))
                    .subscribe(() => this.setParentCellToNull(row, refundConditions.session_id));

                row.sessions[refundConditions.session_id] = {
                    id: refundConditions.session_id,
                    ctrl,
                    get calculatedPercentualPrice() { return ctrl.value * row.price / 100; }
                };
                !this.form.get(this.controlName).get(row.id) &&
                    (this.form.get(this.controlName) as UntypedFormGroup).addControl(row.id, row.ctrl);

            });
        });
        return table;
    }

    /**
     * finds price with matching price type id and rate id
     * @param prices prices of he cuurrent session pack template
     * @param ppv price percentage object
     */
    private findPrice(prices: EventPrice[], ppv: PricePercentageValue): EventPrice {
        return prices.find(price =>
            price.price_type.id === ppv.price_type.id &&
            price.rate.id === ppv.rate.id
        );
    }
}
