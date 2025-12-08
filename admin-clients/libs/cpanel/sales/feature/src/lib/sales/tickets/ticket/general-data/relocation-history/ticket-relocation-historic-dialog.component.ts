import { TicketsService } from '@admin-clients/cpanel-sales-data-access';
import {
    DialogSize, ObDialog, TimelineElement, TimelineElementStatus, VerticalTimelineComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { IdName } from '@admin-clients/shared/data-access/models';
import { LayoutModule } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

interface TicketState {
    id: number;
    sector: IdName;
    row: IdName;
    seat: IdName;
    priceType: IdName;
    currentTicket?: boolean;
}

@Component({
    selector: 'app-ticket-relocation-historic-dialog',
    templateUrl: './ticket-relocation-historic-dialog.component.html',
    styleUrls: ['./ticket-relocation-historic-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe,
        CommonModule,
        LayoutModule,
        MaterialModule,
        FlexModule,
        VerticalTimelineComponent,
        FormsModule
    ]
})
export class TicketRelocationHistoricDialogComponent
    extends ObDialog<TicketRelocationHistoricDialogComponent, { orderCode: string; ticketId: number }, void> {
    readonly #ticketsSrv = inject(TicketsService);

    readonly relocationHistory$ = this.#ticketsSrv.ticketRelocations.get$();
    readonly timeLineElements$: Observable<TimelineElement[]> = this.#ticketsSrv.ticketRelocations.get$()
        .pipe(
            map(relocationHistory => relocationHistory?.map((item, index) => ({
                id: item.id,
                date: item.date,
                title: null,
                status: index === 0 ? TimelineElementStatus.ok : TimelineElementStatus.disabled
            })))
        );

    readonly timeLineElementDetails$: Observable<Map<number, [TicketState, TicketState]>>
        = combineLatest([this.relocationHistory$, this.#ticketsSrv.ticketDetail.get$()])
            .pipe(
                filter(sources => sources.every(Boolean)),
                map(([relocationHistory, ticketDetail]) => {
                    const ticketStates: TicketState[] = [
                        ticketDetail === null ? null : {
                            currentTicket: true,
                            id: ticketDetail.id,
                            sector: ticketDetail.ticket.allocation.sector,
                            row: ticketDetail.ticket.allocation.row,
                            seat: ticketDetail.ticket.allocation.seat,
                            priceType: ticketDetail.ticket.allocation.price_type
                        },
                        ...relocationHistory.map(item => ({
                            id: item.id,
                            sector: item.sector,
                            row: item.row,
                            seat: item.seat,
                            priceType: item.price_type
                        }))
                    ];
                    return new Map(ticketStates.map((ticketState, index) =>
                        index !== 0 ? [ticketState.id, [ticketStates[index - 1], ticketState]] : [null, null]
                    ));
                })
            );

    readonly loading$ = this.#ticketsSrv.ticketRelocations.loading$();

    constructor() {
        super(DialogSize.LATERAL);
        this.dialogRef.addPanelClass('no-action-bar');
        this.#ticketsSrv.ticketRelocations.clear();
        this.#ticketsSrv.ticketRelocations.load(this.data.orderCode, this.data.ticketId);
    }

    close(): void {
        this.dialogRef.close();
    }

    getElementDetails(id: number, timeLineElementDetails: Map<number, [TicketState, TicketState]>): {
        source: TicketState; target: TicketState;
    } {
        const timeLineItemDetails = timeLineElementDetails?.get(id);
        return !timeLineElementDetails ? null : {
            source: timeLineItemDetails[0],
            target: timeLineItemDetails[1]
        };
    }
}
