import {
    SeasonTicketChannelsService,
    SeasonTicketChannelsState
} from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import {
    SeasonTicketsService
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { PROFESSIONAL_SELLING_SERVICE } from '@admin-clients/cpanel/promoters/shared/data-access';
import { ContextNotificationComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, shareReplay } from 'rxjs/operators';
import { SeasonTicketChannelsListComponent } from '../list/season-ticket-channels-list.component';
import { SeasonTicketProfessionalSellingService } from '../professional-selling/season-ticket-professional-selling.service';
import { SeasonTicketChannelsStateMachine } from '../season-ticket-channel-state-machine';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        ContextNotificationComponent,
        FlexLayoutModule,
        TranslatePipe,
        MaterialModule,
        SeasonTicketChannelsListComponent,
        RouterOutlet
    ],
    selector: 'app-season-ticket-channels-container',
    templateUrl: './season-ticket-channels-container.component.html',
    styleUrls: ['./season-ticket-channels-container.component.scss'],
    providers: [
        SeasonTicketsService,
        SeasonTicketChannelsState,
        SeasonTicketChannelsStateMachine,
        {
            provide: PROFESSIONAL_SELLING_SERVICE,
            useClass: SeasonTicketProfessionalSellingService
        }
    ]
})
export class SeasonTicketChannelsContainerComponent {
    private readonly _breakpointObserver = inject(BreakpointObserver);
    private readonly _seasonTicketSrv = inject(SeasonTicketsService);
    private readonly _seasonTicketChannelsService = inject(SeasonTicketChannelsService);

    readonly sidebarWidth$ = this._breakpointObserver
        .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
        .pipe(
            map(result => result.matches ? '240px' : '280px')
        );

    readonly seasonTicketChannelListMetadata$ = this._seasonTicketChannelsService.seasonTicketChannelList.getMetadata$()
        .pipe(filter(Boolean), shareReplay(1));

    readonly isGenerationStatusInProgress$ = this._seasonTicketSrv.seasonTicketStatus.isGenerationStatusInProgress$();
    readonly isGenerationStatusReady$ = this._seasonTicketSrv.seasonTicketStatus.isGenerationStatusReady$();
    readonly isLoading$ = booleanOrMerge([
        this._seasonTicketChannelsService.isSeasonTicketChannelInProgress$(),
        this._seasonTicketChannelsService.seasonTicketChannelList.loading$(),
        this._seasonTicketSrv.seasonTicketStatus.inProgress$()
    ]);
}
