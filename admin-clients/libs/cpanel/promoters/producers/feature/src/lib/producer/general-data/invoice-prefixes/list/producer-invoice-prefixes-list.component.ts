import { PostProducerInvoicePrefix, ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import { EphemeralMessageService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ActionsTableComponent } from '@admin-clients/shared-common-ui-actions-table';
import { DefaultIconComponentLiterals, DefaultIconComponent } from '@admin-clients/shared-common-ui-default-icon';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, BehaviorSubject, Observable, of } from 'rxjs';
import { filter, first, map, switchMap } from 'rxjs/operators';
import {
    CreateInvoicePrefixDialogComponent
} from '../create/create-invoice-prefix-dialog.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-producer-invoice-prefixes-list',
    templateUrl: './producer-invoice-prefixes-list.component.html',
    styleUrls: ['./producer-invoice-prefixes-list.component.scss'],
    imports: [
        ReactiveFormsModule, FlexLayoutModule, MaterialModule,
        TranslatePipe, AsyncPipe, NgIf, ActionsTableComponent, DefaultIconComponent
    ]
})
export class ProducerInvoicePrefixesListComponent implements OnInit, OnDestroy {
    private readonly _matDialog = inject(MatDialog);
    private readonly _producerService = inject(ProducersService);
    private readonly _ephemeralMessage = inject(EphemeralMessageService);

    private readonly _producerId$ = this._producerService.getProducer$().pipe(map(producer => producer.id));

    readonly isInvoicePrefixTableChangingBS = new BehaviorSubject<boolean>(false);
    readonly actions = {};
    readonly columns = ['default', 'prefix', 'suffix'];
    readonly defaultPrefixInvoiceDefaultLiterals: DefaultIconComponentLiterals = {
        successMessageUpdate: 'PRODUCER.INVOICES.PREFIX_DEFAULT.UPDATE_SUCCESS_MESSAGE',
        successMessageRemove: 'PRODUCER.INVOICES.PREFIX_DEFAULT.REMOVE_SUCCESS_MESSAGE'
    };

    readonly invoicePrefixes$ = this._producerService.getInvoicePrefixesData$().pipe(filter(val => !!val));

    @Output() readonly isInProgress$ = booleanOrMerge([
        this._producerService.isInvoicePrefixesLoading$(),
        this.isInvoicePrefixTableChangingBS.asObservable()
    ]);

    @Input() canHaveZeroDefaultInvoicePrefixes = true;

    ngOnInit(): void {
        this._producerId$
            .pipe(first())
            .subscribe(producerId => this._producerService.loadInvoicePrefixes(producerId));
    }

    ngOnDestroy(): void {
        this._producerService.clearInvoicePrefixes();
    }

    updateDefault = (invoicePrefixId, isDefault): Observable<boolean> =>
        this._producerId$
            .pipe(
                first(),
                switchMap(producerId => this.updateInvoicePrefixDefault(producerId, invoicePrefixId, isDefault))
            );

    create(): void {
        combineLatest([this.invoicePrefixes$, this._producerId$])
            .pipe(first())
            .subscribe(([invoicePrefixes, producerId]) => {
                this._matDialog.open(CreateInvoicePrefixDialogComponent, new ObMatDialogConfig({ prefixes: invoicePrefixes }))
                    .beforeClosed()
                    .pipe(
                        filter((result): result is PostProducerInvoicePrefix => !!result),
                        switchMap(prefix => this._producerService.createInvoicePrefix(producerId, prefix))
                    )
                    .subscribe(() => {
                        this._ephemeralMessage.showSaveSuccess();
                        this._producerService.loadInvoicePrefixes(producerId);
                    });
            });
    }

    private updateInvoicePrefixDefault(producerId: number, invoicePrefixId: number, isDefault: boolean): Observable<boolean> {
        return this._producerService.updateInvoicePrefix(producerId, invoicePrefixId, { default: isDefault })
            .pipe(
                switchMap(result => {
                    if (result) {
                        this._producerService.loadInvoicePrefixes(producerId);
                        this.isInvoicePrefixTableChangingBS.next(true);
                        return this.isInvoicePrefixTableChangingBS.asObservable()
                            .pipe(
                                first(value => !value),
                                map(() => true)
                            );
                    } else {
                        return of(false);
                    }
                })
            );
    }
}
