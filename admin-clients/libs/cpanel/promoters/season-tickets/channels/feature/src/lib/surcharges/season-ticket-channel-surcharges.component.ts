import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelSurcharge } from '@admin-clients/cpanel/channels/data-access';
import {
    SeasonTicketChannelsService
} from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, switchMap, throwError } from 'rxjs';
import { filter, tap, takeUntil, map, first } from 'rxjs/operators';
import {
    SeasonTicketChannelSurchargesChannelComponent
} from './channel/season-ticket-channel-surcharges-channel.component';
import {
    SeasonTicketChannelPriceSimulationComponent
} from './price-simulation/season-ticket-channel-price-simulation.component';
import {
    SeasonTicketChannelSurchargesPromoterComponent
} from './promoter/season-ticket-channel-surcharges-promoter.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent,
        MaterialModule,
        CommonModule,
        SeasonTicketChannelSurchargesPromoterComponent,
        TranslatePipe,
        SeasonTicketChannelSurchargesChannelComponent,
        SeasonTicketChannelPriceSimulationComponent
    ],
    selector: 'app-season-ticket-channel-surcharges',
    templateUrl: './season-ticket-channel-surcharges.component.html',
    styleUrls: ['./season-ticket-channel-surcharges.component.scss']
})
export class SeasonTicketChannelSurchargesComponent implements OnInit, OnDestroy, WritingComponent {
    private readonly _fb = inject(FormBuilder);
    private readonly _stChannelsSrv = inject(SeasonTicketChannelsService);
    private readonly _ephemeralMessageSrv = inject(EphemeralMessageService);

    private readonly _onDestroy = new Subject<void>();

    @ViewChildren(MatExpansionPanel) private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly form = this._fb.nonNullable.group({});
    readonly surchargesRequestCtrl = this._fb.nonNullable.control([] as ChannelSurcharge[]);
    readonly channel$ = this._stChannelsSrv.getSeasonTicketChannel$()
        .pipe(filter(Boolean), map(st => st.channel));

    readonly currency$ = inject(SeasonTicketsService).seasonTicket.get$()
        .pipe(map(st => st.currency_code));

    readonly requestAccepted$ = this._stChannelsSrv.isSeasonTicketChannelRequestAccepted$();
    readonly isInProgress$ = booleanOrMerge([
        this._stChannelsSrv.isSeasonTicketChannelInProgress$(),
        this._stChannelsSrv.isSeasonTicketChannelSurchargesLoading$(),
        this._stChannelsSrv.isSeasonTicketChannelSurchargesSaving$(),
        this._stChannelsSrv.channelSurcharges.loading$()
    ]);

    simulationExpanded = false;

    ngOnInit(): void {
        this._stChannelsSrv.getSeasonTicketChannel$()
            .pipe(filter(Boolean), takeUntil(this._onDestroy))
            .subscribe(stChannel => {
                this.simulationExpanded = false;
                this._stChannelsSrv.loadSeasonTicketChannelSurcharges(stChannel.season_ticket.id, stChannel.channel.id);
                if (stChannel.status.request === 'ACCEPTED') {
                    this._stChannelsSrv.channelSurcharges.load(stChannel.season_ticket.id, stChannel.channel.id);
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._stChannelsSrv.clearSeasonTicketChannelSurcharges();
        this._stChannelsSrv.channelSurcharges.clear();
    }

    cancel(): void {
        this.reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            this.surchargesRequestCtrl.setValue([]);
            return this._stChannelsSrv.getSeasonTicketChannel$()
                .pipe(
                    first(),
                    switchMap(stChannel => this._stChannelsSrv.saveSeasonTicketChannelSurcharges(
                        stChannel.season_ticket.id,
                        stChannel.channel.id,
                        this.surchargesRequestCtrl.value
                    ).pipe(
                        tap(() => {
                            this.simulationExpanded = false;
                            this._ephemeralMessageSrv.showSuccess({ msgKey: 'EVENTS.CHANNEL.PROMOTER_SURCHARGES.UPDATE_SUCCESS' });
                        })
                    ))
                );
        } else {
            this.form.markAllAsTouched();
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    private reloadModels(): void {
        this._stChannelsSrv.getSeasonTicketChannel$()
            .pipe(first())
            .subscribe(stChannel => {
                this.form.markAsPristine();
                this.form.markAsUntouched();
                this.surchargesRequestCtrl.reset([], { emitEvent: false });
                this._stChannelsSrv.loadSeasonTicketChannelSurcharges(stChannel.season_ticket.id, stChannel.channel.id);
            });
    }
}
