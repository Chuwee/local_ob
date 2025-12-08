import { ChangeDetectionStrategy, Component, OnDestroy, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { UserRoles, AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    VoucherGroupType, VoucherGroupValidationMethod, VoucherLimitlessValue, Voucher, VouchersService,
    aggDataVouchers,
    exportDataVoucher,
    pinExportField
} from '@admin-clients/cpanel-vouchers-data-access';
import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig,
    ExportDialogComponent, SearchablePaginatedSelectionLoadEvent, pageSize
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats, ExportFormat, PageableFilter } from '@admin-clients/shared/data-access/models';
import { isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { NewVoucherDialogComponent } from '../create/new-voucher-dialog.component';
import { ImportVoucherCodesDialogComponent } from './import/import-dialog/import-voucher-codes-dialog.component';

const writeRoles = [UserRoles.OPR_MGR, UserRoles.CNL_MGR];
const PAGE_SIZE = 20;

@Component({
    selector: 'app-voucher-group-code-list',
    templateUrl: './voucher-list.component.html',
    styleUrls: ['./voucher-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherListComponent implements OnDestroy {
    readonly #tableSrv = inject(TableColConfigService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #voucherSrv = inject(VouchersService);
    readonly #auth = inject(AuthenticationService);
    readonly #matDialog = inject(MatDialog);
    #filters: Partial<PageableFilter> = { limit: pageSize };

    displayedColumns = ['code', 'balance', 'uses', 'limit', 'expiration', 'status', 'actions'];
    readonly displayedColumnsWithPin = ['code', 'pin', 'balance', 'uses', 'limit', 'expiration', 'status', 'actions'];
    readonly pageSize = PAGE_SIZE;

    readonly currency$ = this.#voucherSrv.getVoucherGroup$()
        .pipe(
            filter(Boolean),
            switchMap(voucherGroup => {
                if (voucherGroup.currency_code) {
                    return of(voucherGroup.currency_code);
                } else {
                    return this.#auth.getLoggedUser$()
                        .pipe(first(), map(user => user.currency));
                }
            }),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly reqInProgress$ = this.#voucherSrv.isVouchersLoading$();
    readonly voucherGroupType = VoucherGroupType;
    readonly voucherGroup$ = this.#voucherSrv.getVoucherGroup$()
        .pipe(
            filter(Boolean),
            tap(vg => {
                if (VoucherGroupValidationMethod.codeAndPin === vg.validation_method) {
                    exportDataVoucher[0].fields.push(pinExportField);
                    this.displayedColumns = this.displayedColumnsWithPin;
                }
            })
        );

    readonly vouchers$ = this.#voucherSrv.getVoucherListData$().pipe(filter(Boolean));
    readonly vouchersMetadata$ = this.#voucherSrv.getVoucherListMetadata$().pipe(filter(Boolean));
    readonly vouchersAggregatedData$ = this.#voucherSrv.getVoucherListAggregatedData$().pipe(filter(Boolean));
    readonly userCanWrite$ = this.#auth.hasLoggedUserSomeRoles$(writeRoles);
    readonly dateTimeFormats = DateTimeFormats;
    readonly aggDataVouchers = aggDataVouchers;
    readonly unlimited = VoucherLimitlessValue.unlimited;
    readonly isHandsetOrTablet$ = isHandsetOrTablet$();

    ngOnDestroy(): void {
        this.#voucherSrv.clearVouchers();
    }

    loadVouchersList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this.#voucherSrv.getVoucherGroup$()
            .pipe(first())
            .subscribe(voucherGroup => {
                this.#filters = { ...this.#filters, limit, offset, q: q?.length ? q : null, aggs: true };
                // cancel prev requests so it keeps consistency
                this.#voucherSrv.cancelVouchersList();
                this.#voucherSrv.loadVouchers(voucherGroup.id, this.#filters);
            });
    }

    openNewVoucherDialog(): void {
        this.#matDialog.open(NewVoucherDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(codes => {
                if (codes.length) {
                    this.creationNotification(codes);
                }
            });
    }

    openDeleteVoucherDialog(voucher: Voucher): void {
        this.#voucherSrv.getVoucherGroup$()
            .pipe(first())
            .subscribe(voucherGroup => {
                this.#msgDialogService.showWarn({
                    size: DialogSize.SMALL,
                    title: 'TITLES.DELETE_VOUCHER',
                    message: 'VOUCHER.DELETE_WARNING',
                    messageParams: { code: voucher.code },
                    actionLabel: 'FORMS.ACTIONS.DELETE',
                    showCancelButton: true
                })
                    .pipe(
                        filter(Boolean),
                        switchMap(() => this.#voucherSrv.deleteVoucher(voucherGroup.id, voucher.code))
                    )
                    .subscribe(() => {
                        this.#ephemeralSrv.showSuccess({ msgKey: 'VOUCHER.DELETE_VOUCHER_SUCCESS', msgParams: { code: voucher.code } });
                        this.loadVouchersList({ limit: this.pageSize });
                    });
            });
    }

    importVoucherCodes(validationMethod: VoucherGroupValidationMethod): void {
        this.#voucherSrv.getVoucherGroup$()
            .pipe(first())
            .subscribe(voucherGroup => {
                this.#matDialog.open(ImportVoucherCodesDialogComponent, new ObMatDialogConfig({ validationMethod }))
                    .beforeClosed()
                    .pipe(
                        filter(Boolean),
                        switchMap(voucherCodesData => this.#voucherSrv.createVouchers(voucherGroup.id, voucherCodesData))
                    )
                    .subscribe(createdVouchers => this.creationNotification(createdVouchers));
            });
    }

    creationNotification(createdList: string[]): void {
        if (createdList.length > 1) {
            this.#ephemeralSrv.showSuccess({
                msgKey: 'VOUCHER.CREATE_VOUCHERS_SUCCESS',
                msgParams: { total: createdList.length }
            });
        } else {
            this.#ephemeralSrv.showSuccess({ msgKey: 'VOUCHER.CREATE_VOUCHER_SUCCESS' });
        }
        this.loadVouchersList({ limit: this.pageSize });
    }

    exportVouchers(): void {
        this.#voucherSrv.getVoucherGroup$()
            .pipe(first())
            .subscribe(voucherGroup => {
                this.#matDialog.open(
                    ExportDialogComponent,
                    new ObMatDialogConfig({
                        exportData: exportDataVoucher,
                        exportFormat: ExportFormat.csv,
                        selectedFields: this.#tableSrv.getColumns('EXP_VOUCHER')
                    })
                )
                    .beforeClosed()
                    .pipe(
                        filter(Boolean),
                        switchMap(
                            exportList => {
                                this.#tableSrv.setColumns('EXP_VOUCHER', exportList.fields.map(resultData => resultData.field));
                                return this.#voucherSrv.exportVouchers(voucherGroup.id, {}, exportList);
                            }
                        ),
                        filter(result => !!result.export_id)
                    )
                    .subscribe(() => this.#ephemeralSrv.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' }));
            });
    }
}
