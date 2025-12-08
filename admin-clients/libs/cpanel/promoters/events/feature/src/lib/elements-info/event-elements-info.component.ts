import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { VenueTemplatesConfigSelectorDialogComponent } from '@admin-clients/cpanel-venues-venue-templates-feature';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    EmptyStateComponent, EphemeralMessageService, MessageType, NotificationSnackbarData, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService, VenueTemplateStatus } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ELEMENTS_INFO_SERVICE, ElementsInfoComponent } from '@admin-clients/venues/feature/elements-info';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, first, map, switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-event-elements-info',
    templateUrl: './event-elements-info.component.html',
    styleUrls: ['./event-elements-info.component.scss'],
    imports: [
        MaterialModule, AsyncPipe, FlexLayoutModule, ReactiveFormsModule, TranslatePipe,
        ElementsInfoComponent, EmptyStateComponent, EllipsifyDirective
    ],
    providers: [
        { provide: ELEMENTS_INFO_SERVICE, useExisting: VenueTemplatesService }
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventElementsInfoComponent implements OnInit {
    readonly #eventsSrv = inject(EventsService);
    readonly #entityService = inject(EntitiesBaseService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #refreshChanges = new Subject<void>();
    readonly refreshChanges$ = this.#refreshChanges.asObservable();

    readonly event$ = this.#eventsSrv.event.get$();
    readonly languages$ = this.event$
        .pipe(
            first(Boolean),
            map(event => event.settings.languages.selected)
        );

    readonly isInProgress$ = booleanOrMerge([
        this.#eventsSrv.event.inProgress$(),
        this.#venueTemplatesSrv.isVenueTemplatesListLoading$()
    ]);

    readonly interactiveVenue$ = this.#entityService.getEntity$()
        .pipe(first(Boolean), map(entity => entity.settings?.interactive_venue?.enabled));

    templateControl = new FormControl<number>(null);

    readonly venueTemplates$ = this.#eventsSrv.event.get$()
        .pipe(
            first(Boolean),
            switchMap(event => {
                this.#venueTemplatesSrv.loadVenueTemplatesList({
                    limit: 999, offset: 0, sort: 'name:asc', eventId: event.id, status: [VenueTemplateStatus.active]
                });
                this.entityId = event.entity.id;
                return this.#venueTemplatesSrv.getVenueTemplatesList$();
            }),
            filter(Boolean),
            map(value => {
                this.templateControl.setValue(value.data[0]?.id);
                return value.data;
            })
        );

    readonly isOperatorUser$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
    entityId: number;

    ngOnInit(): void {
        this.#venueTemplatesSrv.clearVenueTemplateList();
        this.#venueTemplatesSrv.clearVenueTemplateData();
    }

    openImportContentsDialog(): void {
        this.#matDialog.open(VenueTemplatesConfigSelectorDialogComponent, new ObMatDialogConfig({
            entityId: this.entityId,
            templateId: this.templateControl.value
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
