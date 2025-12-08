import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { VenueTemplatesConfigSelectorDialogComponent } from '@admin-clients/cpanel-venues-venue-templates-feature';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, MessageType, NotificationSnackbarData, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ELEMENTS_INFO_SERVICE, ElementsInfoComponent } from '@admin-clients/venues/feature/elements-info';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, map, Subject, tap } from 'rxjs';

@Component({
    selector: 'app-promoter-venue-template-elements-info',
    templateUrl: './promoter-venue-template-elements-info.component.html',
    styleUrls: ['./promoter-venue-template-elements-info.component.scss'],
    imports: [
        ElementsInfoComponent, AsyncPipe, FlexModule, TranslatePipe, MatIconModule, MatButtonModule, MatProgressSpinnerModule
    ],
    providers: [
        { provide: ELEMENTS_INFO_SERVICE, useExisting: VenueTemplatesService }
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PromoterVenueTemplateElementsInfoComponent implements OnDestroy {
    readonly #venueTemplatesService = inject(VenueTemplatesService);
    readonly #entityService = inject(EntitiesBaseService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #refreshChanges = new Subject<void>();
    readonly refreshChanges$ = this.#refreshChanges.asObservable();

    readonly venueTemplate$ = this.#venueTemplatesService.venueTpl.get$()
        .pipe(filter(Boolean), map(venueTpl => {
            this.#entityService.loadEntity(venueTpl.entity.id);
            this.templateId = venueTpl.id;
            return venueTpl.id;
        }));

    readonly entityLanguages$ = this.#entityService.getEntity$()
        .pipe(
            first(Boolean),
            tap(entity => this.entityId = entity.id),
            map(entity => entity.settings.languages.available)
        );

    readonly interactiveVenue$ = this.#entityService.getEntity$()
        .pipe(first(Boolean), map(entity => entity.settings?.interactive_venue?.enabled));

    readonly isInProgress$ = booleanOrMerge([
        this.#entityService.isEntityLoading$(),
        this.#venueTemplatesService.venueTpl.inProgress$()
    ]);

    readonly isOperatorUser$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);

    entityId: number;
    templateId: number;

    ngOnDestroy(): void {
        this.#venueTemplatesService.clearVenueTemplateData();
        this.#entityService.clearEntity();
    }

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
