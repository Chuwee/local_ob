import { Metadata } from '@OneboxTM/utils-state';
import { eventChannelsProviders, EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { PROFESSIONAL_SELLING_SERVICE } from '@admin-clients/cpanel/promoters/shared/data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { EventChannelsStateMachine } from '../event-channels-state-machine';
import { EventChannelsListComponent } from '../list/event-channels-list.component';
import { EventsProfessionalSellingService } from '../professional-selling/events-professional-selling.service';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        MaterialModule,
        EventChannelsListComponent,
        CommonModule,
        RouterOutlet,
        EmptyStateComponent,
        TranslatePipe
    ],
    selector: 'app-event-channels-container',
    templateUrl: './event-channels-container.component.html',
    styleUrls: ['./event-channels-container.component.scss'],
    providers: [
        ...eventChannelsProviders,
        EventChannelsStateMachine,
        {
            provide: PROFESSIONAL_SELLING_SERVICE,
            useClass: EventsProfessionalSellingService
        }
    ]
})
export class EventChannelsContainerComponent implements OnInit {

    sidebarWidth$: Observable<string>;
    isLoading$: Observable<boolean>;
    eventChannelListMetadata$: Observable<Metadata>;
    @ViewChild(EventChannelsListComponent) listComponent: EventChannelsListComponent;

    constructor(
        private _breakpointObserver: BreakpointObserver,
        private _eventChannelsService: EventChannelsService
    ) { }

    ngOnInit(): void {
        this.sidebarWidth$ = this._breakpointObserver
            .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
            .pipe(
                map(result => result.matches ? '240px' : '280px')
            );
        this.isLoading$ = this._eventChannelsService.eventChannel.inProgress$();
        this.eventChannelListMetadata$ = this._eventChannelsService.eventChannelsList.getMetaData$();
    }

    newChannel(): void {
        this.listComponent.openNewChannelDialog();
    }
}
