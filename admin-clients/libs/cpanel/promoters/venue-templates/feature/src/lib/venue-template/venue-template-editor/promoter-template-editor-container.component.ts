import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { RoutingState } from '@admin-clients/shared/utility/state';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { StandardVenueTplEditorComponent } from '@admin-clients/shared/venues/feature/standard-venue-tpl-editor';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Component({
    selector: 'app-event-template-editor-container',
    templateUrl: './promoter-template-editor-container.component.html',
    styleUrls: ['./promoter-template-editor-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        StandardVenueTplEditorComponent, RouterLink, TranslatePipe, AsyncPipe, MatIcon, MatTooltip,
        FlexLayoutModule
    ]
})
export class PromoterTemplateEditorContainerComponent implements OnInit, WritingComponent {
    private readonly _routingState = inject(RoutingState);
    private readonly _authSrv = inject(AuthenticationService);
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _venueTplsSrv = inject(VenueTemplatesService);

    @ViewChild(StandardVenueTplEditorComponent)
    private _editor: StandardVenueTplEditorComponent;

    readonly venueTpl$ = this._venueTplsSrv.venueTpl.get$();
    readonly operatorMode$ = this._authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);

    readonly mmcIntegrationEnabled$
        = this._entitiesSrv.getElementEntity$(this._venueTplsSrv.venueTpl.get$(), this._authSrv.getLoggedUser$())
            .pipe(filter(Boolean), map(entity => entity?.settings?.interactive_venue?.enabled));

    ngOnInit(): void {
        this._routingState.removeLastUrlsWith('template-editor');
    }

    canDeactivate(): Observable<boolean> {
        return this._editor?.canDeactivate();
    }
}
