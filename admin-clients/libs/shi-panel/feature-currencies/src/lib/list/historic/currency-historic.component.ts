import { DetailOverlayData, DetailOverlayService } from '@OneboxTM/detail-overlay';
import {
    ObMatDialogConfig, TimelineElement, TimelineElementStatus, VerticalTimelineComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { CustomScrollDirective } from '@admin-clients/shi-panel/utility-directives';
import { UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, map } from 'rxjs';
import { CurrenciesApi } from '../../currencies.api';
import { CurrenciesService } from '../../currencies.service';
import { Transition } from '../../models/currency-transition.model';
import { CurrenciesState } from '../../state/currencies.state';
import { ModifyCurrencyDialogComponent } from '../modify/modify-currency-dialog.component';

@Component({
    selector: 'app-currency-historic',
    templateUrl: './currency-historic.component.html',
    styleUrls: ['./currency-historic.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [CurrenciesService, CurrenciesState, CurrenciesApi],
    imports: [
        CommonModule, MaterialModule, FlexLayoutModule, TranslatePipe, VerticalTimelineComponent,
        CustomScrollDirective
    ]
})
export class CurrencyHistoricComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _currenciesService = inject(CurrenciesService);
    private readonly _authService = inject(AuthenticationService);
    private readonly _detailOverlayService = inject(DetailOverlayService);
    private readonly _matDialog = inject(MatDialog);

    readonly dateTimeFormats = DateTimeFormats;
    readonly hasWritePermissions$: Observable<boolean> = this._authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.exchangeRateWrite])));

    readonly isLoading$ = this._currenciesService.transitions.loading$();
    readonly transitions$ = this._currenciesService.transitions.get$()
        .pipe(map(transitions => transitions?.map(transition => this.mapTransition(transition))));

    readonly data = inject(DetailOverlayData);

    ngOnInit(): void {
        this._currenciesService.transitions.load(this.data.data);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    openModifyExchangeRateDialog(): void {
        this._matDialog.open(ModifyCurrencyDialogComponent, new ObMatDialogConfig({
            currency: this.data.data
        })).beforeClosed()
            .subscribe(changed => {
                if (changed) {
                    this._detailOverlayService.close(true);
                }
            });
    }

    private mapTransition(transition: Transition): TimelineElement {
        return {
            title: 'CURRENCIES.CURRENCY_UPDATE.TRANSITION_TITLE',
            date: transition.date_to,
            status: TimelineElementStatus.ok,
            description: 'Rate updated from ' + transition.original_rate + ' to ' + transition.final_rate + ' by ' + transition.username
        };
    }
}
