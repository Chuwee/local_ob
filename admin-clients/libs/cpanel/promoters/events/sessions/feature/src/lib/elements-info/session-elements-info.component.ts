import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { VenueTemplatesConfigSelectorDialogComponent } from '@admin-clients/cpanel-venues-venue-templates-feature';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, MessageType, NotificationSnackbarData, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { VenueTemplatesService, VenueTemplatesState } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ELEMENTS_INFO_SERVICE, ElementsInfoComponent } from '@admin-clients/venues/feature/elements-info';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, first, map, tap } from 'rxjs/operators';

@Component({
    selector: 'app-session-elements-info',
    templateUrl: './session-elements-info.component.html',
    styleUrls: ['./session-elements-info.component.scss'],
    imports: [
        ElementsInfoComponent, TranslatePipe, FlexModule, AsyncPipe, MatIconModule, MatButtonModule
    ],
    providers: [
        { provide: ELEMENTS_INFO_SERVICE, useExisting: EventSessionsService },
        VenueTemplatesService, VenueTemplatesState
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SessionElementsInfoComponent {
    readonly #eventsSrv = inject(EventsService);
    readonly #sessionsService = inject(EventSessionsService);
    readonly #entityService = inject(EntitiesBaseService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #refreshChanges = new Subject<void>();
    readonly refreshChanges$ = this.#refreshChanges.asObservable();

    readonly event$ = this.#eventsSrv.event.get$();
    readonly session$ = this.#sessionsService.session.get$();

    readonly languages$ = this.event$.pipe(first(Boolean), map(event => event.settings.languages.selected));
    readonly interactiveVenue$ = this.#entityService.getEntity$()
        .pipe(first(Boolean), map(entity => entity.settings?.interactive_venue?.enabled));

    readonly sessionId$ = this.session$
        .pipe(
            filter(Boolean),
            tap(session => {
                this.entityId = session.entity.id;
                this.templateId = session.venue_template.id;
            }),
            map(session => session.id));

    readonly isOperatorUser$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);

    entityId: number;
    templateId: number;

    openImportContentsDialog(): void {
        this.#matDialog.open(VenueTemplatesConfigSelectorDialogComponent, new ObMatDialogConfig({
            entityId: this.entityId,
            templateId: this.templateId
        })).beforeClosed()
            .subscribe(result => {
                if (result) {
                    const data: NotificationSnackbarData = {
                        type: MessageType.success,
                        msgKey: 'VENUE_TEMPLATES.IMPORT_CONTENTS.IMPORT_SUCCESS'
                    };
                    this.#ephemeralMessageSrv.show(data);
                    this.#refreshChanges.next();
                }
            });
    }
}
