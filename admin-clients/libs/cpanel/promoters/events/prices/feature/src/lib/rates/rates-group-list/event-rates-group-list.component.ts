import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EventsService, RateGroup } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    DialogSize,
    EphemeralMessageService,
    MessageDialogService,
    ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnDestroy, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import {
    SaveRatesGroupDialogComponent
} from './save/save-rates-group-dialog.component';

@Component({
    selector: 'app-event-rates-group-list',
    imports: [FlexLayoutModule, MatButtonModule, MatIconModule, MatTableModule, TranslatePipe, AsyncPipe,
        MatTooltipModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './event-rates-group-list.component.html'
})
export class EventRatesGroupListComponent implements OnInit, OnDestroy {
    private readonly _matDialog = inject(MatDialog);
    private readonly _eventsSrv = inject(EventsService);
    private readonly _messageDialogSrv = inject(MessageDialogService);
    private readonly _ephemeralMsg = inject(EphemeralMessageService);
    readonly #authenticationService = inject(AuthenticationService);

    readonly $isOperator = toSignal(this.#authenticationService.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]));
    readonly $displayedColumns = computed(() => {
        const columns = ['rateName', 'avetName'];
        if (this.$isOperator()) {
            columns.push('actions');
        }
        return columns;
    });

    readonly ratesGroup$ = this._eventsSrv.ratesGroup.get$();
    readonly isEmptyList$ = this._eventsSrv.ratesGroup.get$()
        .pipe(
            map(ratesGroup => ratesGroup === null || ratesGroup.length === 0),
            shareReplay(1)
        );

    readonly isHandsetOrTablet$ = inject(BreakpointObserver)
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    async ngOnInit(): Promise<void> {
        this._eventsSrv.ratesGroup.clear();
        const event = await firstValueFrom(this._eventsSrv.event.get$());
        this._eventsSrv.ratesGroup.load(event.id);
    }

    ngOnDestroy(): void {
        this._eventsSrv.ratesGroup.clear();
    }

    async openNewRatesDialog(): Promise<void> {
        const event = await firstValueFrom(this._eventsSrv.event.get$());
        const data = { languages: event.settings.languages.selected };
        this._matDialog.open(SaveRatesGroupDialogComponent, new ObMatDialogConfig(data))
            .beforeClosed()
            .subscribe(isSuccess => {
                if (isSuccess) {
                    this._eventsSrv.ratesGroup.load(event.id);
                }
            });
    }

    async openEditRateGroup(rateGroup: RateGroup): Promise<void> {
        const event = await firstValueFrom(this._eventsSrv.event.get$());
        const data = { languages: event.settings.languages.selected, rateGroup };
        this._matDialog.open(SaveRatesGroupDialogComponent, new ObMatDialogConfig(data))
            .beforeClosed()
            .subscribe(isSuccess => {
                if (isSuccess) {
                    this._eventsSrv.ratesGroup.load(event.id);
                }
            });
    }

    async delete(rateGroup: RateGroup): Promise<void> {
        const event = await firstValueFrom(this._eventsSrv.event.get$());
        this._messageDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'EVENTS.RATE_GROUP.DELETE_TITLE',
            message: 'EVENTS.RATE_GROUP.DELETE_MESSAGE',
            messageParams: { rateName: rateGroup.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this._eventsSrv.ratesGroup.delete(event.id, rateGroup.id)
                        .subscribe(() => {
                            this._ephemeralMsg.showSuccess(
                                { msgKey: 'EVENTS.RATE_GROUP.DELETE_SUCCESS', msgParams: { rateName: rateGroup.name } });
                            this._eventsSrv.ratesGroup.load(event.id);
                        });
                }
            });
    }
}
