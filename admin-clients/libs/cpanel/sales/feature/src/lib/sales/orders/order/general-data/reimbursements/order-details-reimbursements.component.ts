import {
    OrderDetail, reimbursementFormsSchemaValues, ReimbursementFormsSchema, ReimbursementInfo
} from '@admin-clients/cpanel-sales-data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ReimburseOrderDialogComponent } from '../reimbursement/reimburse-order-dialog.component';

@Component({
    selector: 'app-order-details-reimbursements',
    templateUrl: './order-details-reimbursements.component.html',
    styleUrls: ['./order-details-reimbursements.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('reimbursementExpand', [
            state('collapsed', style({ height: '0px', minHeight: '0' })),
            state('expanded', style({ height: '*' })),
            transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
        ])
    ],
    standalone: false
})
export class OrderDetailsReimbursementsComponent {
    expandedReimbursement: ReimbursementInfo;
    reimbursementsColumns: string[];
    dateTimeFormats = DateTimeFormats;
    gateway: string;
    canBeExpanded: boolean;
    reimbursementInfo: ReimbursementInfo[];
    currency: string;

    @Input() set order(order: OrderDetail) {
        this.canBeExpanded = !!order.payment_detail?.gateway_additional_info;
        this.reimbursementsColumns = this.canBeExpanded ? ['expand'] : [];
        this.reimbursementsColumns = this.reimbursementsColumns.concat(
            ['date', 'status', 'gateway', 'code', 'message', 'amount', 'action']
        );
        this.reimbursementInfo = order.payment_detail?.reimbursements_info;
        this.gateway = order.payment_detail?.gateway;
        this.currency = order.price?.currency;
    }

    constructor(private _dialog: MatDialog) { }

    openRetryReimbursementDialog(reimbursement: ReimbursementInfo): void {
        let manualFormSchema: ReimbursementFormsSchema['whateverForm'];
        let disableDefault = false;
        if (reimbursement.manual_retry) {
            if (this.gateway.startsWith('ECI')) {
                manualFormSchema = reimbursementFormsSchemaValues['eci_bank_transfer'];
                disableDefault = true;
            }
            // TODO: Implementar
            /*else if (['redsys'].includes(this.gateway)) {
                manualFormSchema = reimbursementFormsSchemaValues.redsys_bank_transfer;
            }*/
        }
        this._dialog.open(ReimburseOrderDialogComponent, new ObMatDialogConfig(
            { reimbursement, manualFormSchema, disableDefault }));
    }
}
