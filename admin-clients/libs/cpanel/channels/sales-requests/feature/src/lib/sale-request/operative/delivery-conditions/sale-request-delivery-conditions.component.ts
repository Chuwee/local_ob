import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    SaleRequestDeliveryConditions, TicketHandlingOptions
    , SaleRequest, SalesRequestsService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { MessageDialogService, DialogSize, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { TrackingService } from '@admin-clients/shared/data-access/trackers';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { filter, first, Observable, Subject, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-sale-request-delivery-conditions',
    templateUrl: './sale-request-delivery-conditions.component.html',
    styleUrls: ['./sale-request-delivery-conditions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestDeliveryConditionsComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _salesRequestsService = inject(SalesRequestsService);
    private readonly _ephemeralMessageService = inject(EphemeralMessageService);
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _messageDialogService = inject(MessageDialogService);
    private readonly _trackingService = inject(TrackingService);

    saleRequest: SaleRequest;
    form: UntypedFormGroup;
    deliveryConditions$: Observable<SaleRequestDeliveryConditions>;
    ticketHandling = TicketHandlingOptions.useChannelConfig;
    loadingOrSaving$: Observable<boolean>;
    avoidEnabled = TicketHandlingOptions.avoidTicket;
    savedDeliveryConditions = TicketHandlingOptions.useChannelConfig;

    ngOnInit(): void {
        // used to show spinner and disable save & cancel button
        this.loadingOrSaving$ = booleanOrMerge([
            this._salesRequestsService.isSaleRequestDeliveryConditionsLoading$(),
            this._salesRequestsService.isSaleRequestDeliveryConditionsSaving$()
        ]);

        this.form = this._fb.group({
            ticketHandling: TicketHandlingOptions.useChannelConfig
        });

        //load saleRequest delivery conditions
        this._salesRequestsService.getSaleRequest$()
            .pipe(first(Boolean))
            .subscribe(saleRequest => {
                this._salesRequestsService.loadSaleRequestDeliveryConditions(saleRequest.id);
                this.saleRequest = saleRequest;
            });

        this.deliveryConditions$ = this._salesRequestsService.getSaleRequestDeliveryConditions$()
            .pipe(
                filter(Boolean),
                tap(saleRequestDeliveryConditions => {
                    this.ticketHandling = saleRequestDeliveryConditions.ticket_handling;
                    this.savedDeliveryConditions = this.ticketHandling;
                    this.form.markAsPristine();
                }));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._salesRequestsService.clearSaleRequestDeliveryConditions();
    }

    changeTicketHandlingOption(): void {
        if (this.ticketHandling === TicketHandlingOptions.useChannelConfig) {
            this.ticketHandling = TicketHandlingOptions.avoidTicket;
        } else {
            this.ticketHandling = TicketHandlingOptions.useChannelConfig;
            if (this.ticketHandling !== this.savedDeliveryConditions) {
                this.showWarningDialog();
            }
        }
        this.form.markAsDirty();
    }

    cancel(): void {
        this._salesRequestsService.loadSaleRequestDeliveryConditions(this.saleRequest.id);
        this.form.markAsPristine();
    }

    save(): void {
        this.save$().subscribe();
    }

    showWarningDialog(): void {
        this._messageDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'SALE_REQUEST.DELIVERY_CONDITIONS.CHANGE_CONFIG_TITLE',
            message: 'SALE_REQUEST.DELIVERY_CONDITIONS.CHANGE_CONFIG_MSG',
            showCancelButton: true
        })
            .pipe(tap(isConfirmed => {
                if (isConfirmed) {
                    this.ticketHandling = TicketHandlingOptions.useChannelConfig;
                } else {
                    this.cancel();
                }
            }));
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const saleRequestDeliveryConditions: SaleRequestDeliveryConditions = {
                ticket_handling: this.ticketHandling
            };
            return this._salesRequestsService.saveSaleRequestDeliveryConditions(this.saleRequest.id, saleRequestDeliveryConditions)
                .pipe(tap(() => {
                    this._ephemeralMessageService.showSuccess({
                        msgKey: 'SALE_REQUEST.UPDATE_SUCCESS',
                        msgParams: { saleRequestName: `${this.saleRequest.event.name}-${this.saleRequest.channel.name}` }
                    });

                    //Save changes to google analytics
                    const eventLabel = `entity: ${this.saleRequest.event.entity.id}, event name: ${this.saleRequest.event.name}, ` +
                        `channel name: ${this.saleRequest.channel.name}, channel id: ${this.saleRequest.channel.id}, ` +
                        `channel request status: ${this.saleRequest.status}`;
                    const eventAction = (this.ticketHandling === TicketHandlingOptions.avoidTicket) ?
                        'Ticket Delivery: activated' : 'Ticket Delivery: deactivated';
                    this._trackingService.sendEventTrack(eventAction, 'Sales request', eventLabel, this.saleRequest.id);

                    this._salesRequestsService.loadSaleRequestDeliveryConditions(this.saleRequest.id);
                }));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }
}
