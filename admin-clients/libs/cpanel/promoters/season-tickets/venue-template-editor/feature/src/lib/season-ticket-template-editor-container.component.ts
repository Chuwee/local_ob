import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { RoutingState } from '@admin-clients/shared/utility/state';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { StandardVenueTplEditorComponent } from '@admin-clients/shared/venues/feature/standard-venue-tpl-editor';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-template-editor-container',
    templateUrl: './season-ticket-template-editor-container.component.html',
    styleUrls: ['./season-ticket-template-editor-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketTemplateEditorContainerComponent implements OnInit, OnDestroy, WritingComponent {
    private readonly _routingState = inject(RoutingState);
    private readonly _authSrv = inject(AuthenticationService);
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _seasonTicketSrv = inject(SeasonTicketsService);
    private readonly _venueTplsSrv = inject(VenueTemplatesService);

    @ViewChild(StandardVenueTplEditorComponent)
    private _editor: StandardVenueTplEditorComponent;

    readonly venueTemplate$ = this._venueTplsSrv.venueTpl.get$();

    readonly seasonTicket$ = this._seasonTicketSrv.seasonTicket.get$();
    readonly operatorMode$ = this._authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
    readonly mmcIntegrationEnabled$
        = this._entitiesSrv.getElementEntity$(this._venueTplsSrv.venueTpl.get$(), this._authSrv.getLoggedUser$())
            .pipe(filter(Boolean), map(entity => entity?.settings?.interactive_venue?.enabled));

    ngOnInit(): void {
        this._routingState.removeLastUrlsWith('template-editor');
    }

    ngOnDestroy(): void {
        this._venueTplsSrv.venueTpl.clear();
    }

    canDeactivate(): Observable<boolean> {
        return this._editor?.canDeactivate();
    }
}
