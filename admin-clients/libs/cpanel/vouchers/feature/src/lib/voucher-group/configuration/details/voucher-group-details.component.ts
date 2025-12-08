import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VoucherGroupType, VouchersService } from '@admin-clients/cpanel-vouchers-data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';

@Component({
    selector: 'app-voucher-details',
    templateUrl: './voucher-group-details.component.html',
    styleUrls: ['./voucher-group-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherGroupDetailsComponent implements OnDestroy {
    readonly #voucherServiceSrv = inject(VouchersService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    readonly voucherGroup$ = this.#voucherServiceSrv.getVoucherGroup$();
    readonly voucher$ = this.#voucherServiceSrv.getVoucher$();
    readonly deepPath$ = getDeepPath$(this.#router, this.#route);
    readonly voucherGroupType = VoucherGroupType;

    ngOnDestroy(): void {
        this.#voucherServiceSrv.clearVoucherGroup();
    }
}
