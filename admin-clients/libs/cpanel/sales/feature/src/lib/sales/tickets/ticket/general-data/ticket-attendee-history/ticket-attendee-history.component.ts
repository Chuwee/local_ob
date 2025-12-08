import { AttendantFieldType, AttendantsService } from '@admin-clients/cpanel/platform/data-access';
import { TicketsService } from '@admin-clients/cpanel-sales-data-access';
import { TicketAttendeeHistory } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, openDialog } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import {
    MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { filter, tap, shareReplay, map } from 'rxjs/operators';
import { TicketAttendeeEditDialogComponent } from '../ticket-attendee-edit/ticket-attendee-edit-dialog.component';

@Component({
    selector: 'app-ticket-attendee-history',
    templateUrl: './ticket-attendee-history.component.html',
    styleUrls: ['./ticket-attendee-history.component.scss'],
    imports: [
        AsyncPipe, TranslatePipe, MatButton, MatTooltip, KeyValuePipe, MatIcon, MatColumnDef, DateTimePipe, MatCell,
        MatCellDef, MatHeaderCell, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatHeaderCellDef, MatTable
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TicketAttendeeHistoryComponent implements OnInit {
    readonly #attendeesColumns = new Set<string>();
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #ticketsService = inject(TicketsService);
    readonly #attendantsSrv = inject(AttendantsService);
    readonly #dialogSrv = inject(MatDialog);

    readonly dateTimeFormats = DateTimeFormats;
    readonly ticketDetail$ = combineLatest([
        this.#ticketsService.ticketDetail.get$(),
        this.#attendantsSrv.attendantFields.getData$()]
    ).pipe(
        filter(value => value.every(Boolean)),
        tap(([ticketDetail, _]) => {
            if (ticketDetail.attendant) {
                this.#ticketsService.ticketDetail.attendeeHistory.load(ticketDetail.order.code, ticketDetail.id);
            }
        }),
        map(([ticketDetail, attendantFields]) => {
            const images = attendantFields
                .filter(field => field.type === AttendantFieldType.image)
                .reduce<string[]>((acc, field) => {
                    const imageField = ticketDetail.attendant?.[field.sid];
                    if (imageField?.length) {
                        acc.push(imageField);
                        delete ticketDetail.attendant[field.sid];
                    }
                    return acc;
                }, []);
            this.$images.set(images);
            return ticketDetail;
        }));

    readonly ticketAttendeeHistory$ = combineLatest([
        this.#ticketsService.ticketDetail.attendeeHistory.get$(),
        this.#attendantsSrv.attendantFields.getData$()
    ]).pipe(
        filter(val => val.every(Boolean)),
        map(([history, fields]) => {
            const imageFields = fields.filter(field => field.type === AttendantFieldType.image);
            return history.map(attendeeData => {
                const fields = new Map([...Object.entries(attendeeData.fields)]
                    .filter(([attKey, _]) => !imageFields.find(field => attKey === field.sid)));
                return { ...attendeeData, fields } as TicketAttendeeHistory;
            });
        }),
        tap(history => {
            this.#attendeesColumns.add('date');
            history.reduce((acc, attendeeData) => (
                Array.from(attendeeData.fields.entries()).forEach(([key, _]) => acc.add(key)), acc
            ), this.#attendeesColumns);
        }),
        shareReplay(1)
    );

    readonly $images = signal([] as string[]);

    ngOnInit(): void {
        this.#attendantsSrv.attendantFields.load();
        this.#ticketsService.ticketDetail.attendeeFields.clear();
    }

    get attendantFields(): string[] {
        return [...this.#attendeesColumns].filter(column => column !== 'date');
    }

    get attendeesColumns(): string[] {
        return [...this.#attendeesColumns];
    }

    editAttendee(orderCode: string, ticketId: number): void {
        openDialog(this.#dialogSrv, TicketAttendeeEditDialogComponent, { orderCode, ticketId })
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(attendantData => {
                this.#ticketsService.ticketDetail.editTicketAttendant$(orderCode, ticketId.toString(), attendantData)
                    .subscribe(() => {
                        setTimeout(() => {
                            this.#ephemeralMessageService.showSaveSuccess();
                            this.#ticketsService.ticketDetail.load(orderCode, ticketId.toString());
                        }, 1000);
                    });
            });
    }

    downloadImage(base64String: string, fileName: string): void {
        const a = document.createElement('a');
        a.href = `data:image/jpeg;base64,${base64String}`;
        a.download = fileName;
        a.click();
        a.remove();
    }
}
