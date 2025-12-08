import { eventChannelsProviders } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { AutomaticSalesPost, EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    ContextNotificationComponent, DialogSize, EmptyStateTinyComponent, EphemeralMessageService, HelpButtonComponent,
    MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { Topic, WebsocketsService, WsEventMsgType, WsMsg } from '@admin-clients/shared/core/data-access';
import { DateTimeFormats, ExportDeliveryType, ExportFormat } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, switchMap, take, tap } from 'rxjs';
import { ExportAutomaticSaleDialogComponent } from './export-automatic-sale-dialog/export-automatic-sale-dialog.component';
import { NewAutomaticSaleDialogComponent } from './new-automatic-sale-dialog/new-automatic-sale-dialog.component';

export type WSAutomaticSalesMsg = WsMsg<WsEventMsgType.automaticSales, { id: number }>;

@Component({
    selector: 'app-automatic-sales',
    templateUrl: './automatic-sales.component.html',
    styleUrls: ['./automatic-sales.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, ReactiveFormsModule, TranslatePipe,
        EmptyStateTinyComponent, DateTimePipe, FlexLayoutModule, HelpButtonComponent,
        FormContainerComponent, ContextNotificationComponent
    ],
    providers: [
        eventChannelsProviders
    ]
})
export class AutomaticSalesComponent implements OnInit {

    readonly #matDialog = inject(MatDialog);
    readonly #ws = inject(WebsocketsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #eventSessionSrv = inject(EventSessionsService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #messageDialogService = inject(MessageDialogService);

    #sessionId: number;

    readonly dateTimeFormats = DateTimeFormats;
    readonly $data = toSignal(this.#eventSessionSrv.automaticSales.get$());
    readonly $loading = toSignal(this.#eventSessionSrv.automaticSales.loading$());
    readonly $progress = signal<number>(null);

    ngOnInit(): void {
        this.#eventSessionSrv.session.get$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(session => {
                this.#eventSessionSrv.automaticSales.load(session.id);
                this.#sessionId = session.id;
            });

        this.#eventSessionSrv.session.get$().pipe(
            filter(Boolean),
            tap(() => this.$progress.set(null)),
            switchMap(session => this.#ws.getMessages$<WSAutomaticSalesMsg>(Topic.event, session.event.id).pipe(
                filter(msg => msg && msg.type === WsEventMsgType.automaticSales && msg.data?.id === session.id)
            )),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(msg => {
            if (msg.status === 'DONE') {
                this.#eventSessionSrv.automaticSales.load(msg.data.id);
                this.$progress.set(null);
            } else if (msg.status !== 'IN_PROGRESS') {
                this.$progress.set(null);
            } else {
                this.$progress.set(msg.progress || 50);
            }
        });

    }

    openDialog(): void {
        this.#matDialog.closeAll();
        this.#matDialog.open(NewAutomaticSaleDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .pipe(filter(Boolean), take(1))
            .subscribe((request: AutomaticSalesPost) => {
                this.#eventSessionSrv.automaticSales.create(this.#sessionId, request).subscribe({
                    next: () => {
                        this.#ephemeralSrv.showSuccess({ msgKey: 'CSV.IMPORT.SELECT_CONFIRM' });
                        this.#eventSessionSrv.automaticSales.load(this.#sessionId);
                    },
                    error: (error: HttpErrorResponse) => {
                        if (error.error.code === 'WRONG_EMAIL_IN_ROWS') {
                            const errorMessage = [];
                            errorMessage.push(error.error.message);
                            this.#messageDialogService.showAlert({
                                size: DialogSize.SMALL,
                                title: 'TITLES.ERROR_DIALOG',
                                message: 'API_ERRORS.WRONG_EMAIL_IN_ROWS',
                                subMessages: errorMessage
                            });
                        }
                    }
                });
            });

    }

    openExportDialog(name: string): void {
        this.#matDialog.open(ExportAutomaticSaleDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(email => {
                const exportRequest = {
                    filename: name, format: ExportFormat.csv,
                    delivery: { type: ExportDeliveryType.email, properties: { address: email } }
                };
                this.#eventSessionSrv.automaticSales.export(this.#sessionId, exportRequest).subscribe(() => {
                    this.#ephemeralSrv.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' });
                });
            });
    }

    stop(): void {
        this.#eventSessionSrv.automaticSales.stop(this.#sessionId).subscribe(() => {
            this.#ephemeralSrv.show({ msgKey: 'EVENTS.SESSION.AUTOMATIC_SALES.STOPPED', type: 'info' });
            this.#eventSessionSrv.automaticSales.load(this.#sessionId);
        });
    }

}
