import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { RoutingState } from '@admin-clients/shared/utility/state';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { StandardVenueTplEditorComponent } from '@admin-clients/shared/venues/feature/standard-venue-tpl-editor';

@Component({
    selector: 'app-event-template-editor-container',
    templateUrl: './venue-template-editor-container.component.html',
    styleUrls: ['./venue-template-editor-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [FlexLayoutModule, StandardVenueTplEditorComponent, RouterLink, TranslatePipe, NgIf, AsyncPipe, MatIcon, MatTooltip]
})
export class VenueTemplateEditorContainerComponent implements OnInit, WritingComponent {
    private readonly _routingState = inject(RoutingState);
    private readonly _authSrv = inject(AuthenticationService);
    private readonly _venueTplsSrv = inject(VenueTemplatesService);
    @ViewChild(StandardVenueTplEditorComponent) private _editor: StandardVenueTplEditorComponent;
    readonly venueTpl$ = this._venueTplsSrv.venueTpl.get$();
    readonly operatorMode$ = this._authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);

    ngOnInit(): void {
        this._routingState.removeLastUrlsWith('template-editor');
    }

    canDeactivate(): Observable<boolean> {
        return this._editor?.canDeactivate();
    }
}
