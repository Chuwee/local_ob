import { AttendantFieldType, AttendantsService } from '@admin-clients/cpanel/platform/data-access';
import { TicketsService } from '@admin-clients/cpanel-sales-data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { first, map, tap } from 'rxjs/operators';

@Component({
    selector: 'app-ticket-attendee-edit-dialog',
    templateUrl: './ticket-attendee-edit-dialog.component.html',
    styleUrls: ['./ticket-attendee-edit-dialog.component.scss'],
    imports: [
        ReactiveFormsModule, MatDialogTitle, MatIconButton, MatIcon, TranslatePipe, AsyncPipe, MatDialogContent,
        MatFormField, KeyValuePipe, MatInput, MatLabel, MatProgressSpinner, MatButton, MatDialogActions
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TicketAttendeeEditDialogComponent
    extends ObDialog<TicketAttendeeEditDialogComponent, { ticketId: number; orderCode: string }, Record<string, string>>
    implements OnDestroy {
    readonly #dialogRef = inject(MatDialogRef<TicketAttendeeEditDialogComponent>);
    readonly #fb = inject(FormBuilder);
    readonly #ticketsService = inject(TicketsService);
    readonly #attendantsSrv = inject(AttendantsService);

    readonly form = this.#fb.group({});
    readonly attendant$ = combineLatest([
        this.#ticketsService.ticketDetail.attendeeFields.get$(),
        this.#attendantsSrv.attendantFields.getData$()
    ]).pipe(
        first(val => val.every(Boolean)),
        tap(([attendant, _]) => {
            attendant.forEach((attVal, attKey) => {
                this.form.addControl(attKey, new FormControl(attVal, [Validators.required]));
            });
        }),
        map(([attendant, fields]) => {
            const imageFields = fields.filter(field => field.type === AttendantFieldType.image);
            return new Map([...attendant].filter(([attKey]) => !imageFields.find(field => attKey === field.sid)));
        })
    );

    constructor() {
        super(DialogSize.MEDIUM);
        this.#ticketsService.ticketDetail.attendeeFields.load(this.data.orderCode, this.data.ticketId);
    }

    ngOnDestroy(): void {
        this.#ticketsService.ticketDetail.attendeeFields.clear();
    }

    close(): void {
        this.#dialogRef.close();
    }

    submit(): void {
        this.#dialogRef.close(this.form.value);
    }
}
